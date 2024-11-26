const { defineConfig } = require('@vue/cli-service');
const webpack = require('webpack'); // Importiere webpack

module.exports = defineConfig({
    configureWebpack: {
        plugins: [
            new webpack.DefinePlugin({
                'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV)
            })
        ]
    },
});
