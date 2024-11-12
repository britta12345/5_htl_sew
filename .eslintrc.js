module.exports = {
    root: true,
    env: {
        browser: true,
        node: true,
    },
    extends: [
        'plugin:vue/essential', // oder 'plugin:vue/vue3-essential' für Vue 3
        'eslint:recommended',
    ],
    parserOptions: {
        parser: '@babel/eslint-parser',
    },
    rules: {
        'no-undef': 'off', // Deaktiviert die 'no-undef'-Regel
        indent: 'off',
        'space-before-function-paren': 'off'
    }
};
