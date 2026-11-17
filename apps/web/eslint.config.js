// @ts-check
const eslint = require('@eslint/js');
const { defineConfig } = require('eslint/config');
const tseslint = require('typescript-eslint');
const angular = require('angular-eslint');

const templateTextPlugin = {
  rules: {
    'no-hardcoded-text': {
      meta: {
        type: 'problem',
        docs: { description: 'Require user-facing template text to use Transloco.' },
        schema: [],
        messages: { hardcoded: 'Use a Transloco expression for user-facing text.' },
      },
      create(context) {
        return {
          Text(node) {
            if (node.value.trim()) {
              context.report({ node, messageId: 'hardcoded' });
            }
          },
        };
      },
    },
  },
};

module.exports = defineConfig([
  {
    files: ['**/*.ts'],
    extends: [
      eslint.configs.recommended,
      tseslint.configs.recommended,
      tseslint.configs.stylistic,
      angular.configs.tsRecommended,
    ],
    processor: angular.processInlineTemplates,
    rules: {
      '@angular-eslint/directive-selector': [
        'error',
        {
          type: 'attribute',
          prefix: 'app',
          style: 'camelCase',
        },
      ],
      '@angular-eslint/component-selector': [
        'error',
        {
          type: 'element',
          prefix: 'app',
          style: 'kebab-case',
        },
      ],
    },
  },
  {
    files: ['src/app/**/*.html'],
    extends: [angular.configs.templateRecommended, angular.configs.templateAccessibility],
    plugins: { kithlore: templateTextPlugin },
    rules: {
      'kithlore/no-hardcoded-text': 'error',
    },
  },
]);
