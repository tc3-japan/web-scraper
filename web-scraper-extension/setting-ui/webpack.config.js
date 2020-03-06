const {join} = require('path');
const CopyPlugin = require('copy-webpack-plugin');

module.exports = {
  context: __dirname,
  entry: {
    background: './src/background.js',
    content: './src/content.js',
    devtools: './src/devtools.js',
    panel: './src/panel.js',
  },
  output: {
    path: join(__dirname, 'build'),
    filename: '[name].js',
  },
  module: {
    rules: [
      {
        test: /\.css$/,
        use: ['style-loader', 'css-loader'],
      },
    ]
  },
  plugins: [
    new CopyPlugin([
      {from: './src/manifest.json', to: './manifest.json'},
      {from: './src/panel.html', to: 'panel.html'},
      {from: './src/devtools.html', to: 'devtools.html'},
    ]),
  ],
  mode: 'production',
  performance: {
    hints: false,
  },
};
