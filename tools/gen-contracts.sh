#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
api_url="${API_URL:-http://localhost:8080}"
contracts_dir="$repo_root/apps/web/src/app/contracts"
openapi_file="$contracts_dir/openapi.json"
types_file="$contracts_dir/api.ts"
schema_dir="$contracts_dir/stomp"
tool_bin="$repo_root/node_modules/.bin"
generated_marker="GENERATED - DO NOT EDIT"

mkdir -p "$contracts_dir" "$schema_dir"
openapi_tmp="$(mktemp)"
trap 'rm -f "$openapi_tmp"' EXIT
curl --fail --silent --show-error "$api_url/openapi" --output "$openapi_tmp"
node - "$openapi_tmp" "$openapi_file" "$generated_marker" <<'NODE'
const fs = require('node:fs');
const [input, output, marker] = process.argv.slice(2);
const spec = JSON.parse(fs.readFileSync(input, 'utf8'));
const generatedSpec = { 'x-generated': marker, ...spec, servers: [{ url: '/' }] };
fs.writeFileSync(output, `${JSON.stringify(generatedSpec, null, 2)}\n`);
NODE
"$tool_bin/openapi-typescript" \
  "$openapi_file" \
  --output "$types_file"

generated_file="$(mktemp)"
{ printf '// %s\n' "$generated_marker"; cat "$types_file"; } > "$generated_file"
mv "$generated_file" "$types_file"

if [[ "$OSTYPE" == msys* || "$OSTYPE" == cygwin* ]]; then
  api_maven="$(cygpath -w "$repo_root/apps/api/mvnw.cmd")"
  api_pom="$(cygpath -w "$repo_root/apps/api/pom.xml")"
  cmd.exe //c call "$api_maven" -q -f "$api_pom" -pl app -am -DskipTests process-classes
else
  (cd "$repo_root/apps/api" && ./mvnw -q -pl app -am -DskipTests process-classes)
fi

for schema_file in "$schema_dir"/*.schema.json; do
  type_file="${schema_file%.schema.json}.ts"
  "$tool_bin/json2ts" \
    --input "$schema_file" \
    --output "$type_file"
  generated_file="$(mktemp)"
  { printf '// %s\n' "$generated_marker"; cat "$type_file"; } > "$generated_file"
  mv "$generated_file" "$type_file"
done

node - "$schema_dir" <<'NODE'
const fs = require('node:fs');
const path = require('node:path');
const directory = process.argv[2];
for (const name of fs.readdirSync(directory)) {
  if (!name.endsWith('.ts')) continue;
  const file = path.join(directory, name);
  const source = fs.readFileSync(file, 'utf8');
  fs.writeFileSync(
    file,
    source.replace(
      /^\/\/ GENERATED - DO NOT EDIT\r?\n\/\* eslint-disable \*\/\r?\n/,
      '// GENERATED - DO NOT EDIT\n',
    ),
  );
}
NODE
"$tool_bin/prettier" --write \
  "$openapi_file" "$types_file" "$schema_dir"/*.schema.json "$schema_dir"/*.ts >/dev/null
