#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
api_url="${API_URL:-http://localhost:8080}"
contracts_dir="$repo_root/apps/web/src/app/contracts"
openapi_file="$contracts_dir/openapi.json"
types_file="$contracts_dir/api.ts"

mkdir -p "$contracts_dir"
curl --fail --silent --show-error "$api_url/openapi" --output "$openapi_file"
npx --yes --package=openapi-typescript@7.13.0 --package=typescript@5.9.3 openapi-typescript \
  "$openapi_file" \
  --output "$types_file"

generated_file="$(mktemp)"
{
  printf '// GENERATED - DO NOT EDIT\n'
  cat "$types_file"
} > "$generated_file"
mv "$generated_file" "$types_file"
