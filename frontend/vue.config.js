const CopyPlugin = require('copy-webpack-plugin');
const path = require('path');
const express = require('express')

module.exports = {
    configureWebpack: {
        plugins: [
            new CopyPlugin([
                { from: 'src/static', to: 'static' },
            ])
        ],
        devServer: {
            contentBase: path.resolve('src/static'),
            hot: true,
            setup (app) {
                app.use('/static', express.static(path.resolve('src/static')));
            }
        }
    },
}