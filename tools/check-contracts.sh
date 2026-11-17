#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
contracts_dir="$repo_root/apps/web/src/app/contracts"

node - "$contracts_dir" <<'NODE'
const fs = require('node:fs');
const path = require('node:path');

const root = process.argv[2];
const openapiPath = path.join(root, 'openapi.json');
const openapi = JSON.parse(fs.readFileSync(openapiPath, 'utf8'));
if (openapi['x-generated'] !== 'GENERATED - DO NOT EDIT') {
  throw new Error(`${openapiPath} is missing its generated marker`);
}
if (openapi.servers?.length !== 1 || openapi.servers[0].url !== '/') {
  throw new Error(`${openapiPath} has a non-deterministic server URL`);
}

const schemaDirectory = path.join(root, 'stomp');
for (const name of fs.readdirSync(schemaDirectory)) {
  const file = path.join(schemaDirectory, name);
  const source = fs.readFileSync(file, 'utf8');
  if (name.endsWith('.schema.json')) {
    const schema = JSON.parse(source);
    if (schema.$comment !== 'GENERATED - DO NOT EDIT') {
      throw new Error(`${file} is missing its generated marker`);
    }
  } else if (name.endsWith('.ts') && !source.startsWith('// GENERATED - DO NOT EDIT\n')) {
    throw new Error(`${file} is missing its generated header`);
  }
}

const apiTypes = fs.readFileSync(path.join(root, 'api.ts'), 'utf8');
if (!apiTypes.startsWith('// GENERATED - DO NOT EDIT\n')) {
  throw new Error('api.ts is missing its generated header');
}
NODE
