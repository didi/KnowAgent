const pkgJson = require('./package');
const axios = require('axios');
const entry ={
    [pkgJson.systemName]: './src/index.tsx',
}
module.exports = {
  // devEntry: {
  //   [pkgJson.systemName]: './src/index.tsx',
  // },
  // buildEntry: {
  //   [pkgJson.systemName]: './src/index.tsx',
  // },
  devEntry: entry,
  buildEntry: entry,
  webpackDevConfig: 'config/webpack.dev.config.js',
  webpackBuildConfig: 'config/webpack.build.config.js',
  webpackDllConfig: 'config/webpack.dll.config.js',
  theme: 'config/theme.js',
  template: 'src/index.html',
  output: `../agent-manager-rest/src/main/resources/templates`,
  publicPath:'/',
  eslintFix: true,
  hmr: false,
  port: pkgJson.port,
  extraBabelPlugins: [
    [
      'babel-plugin-import',
      {
        libraryName: 'antd',
        style: true,
      },
    ],
    '@babel/plugin-transform-object-assign',
    '@babel/plugin-transform-modules-commonjs',
    // ...prodPlugins
  ],
  devServer: {
    inline: true,
    proxy: {
      '/api/v1': {
        target: 'http://10.190.32.213:8080/',
        changeOrigin: true,
      },
      // '/api/v1': {
      //   target: 'http://116.85.23.35/',
      //   changeOrigin: true,
      // },
      '/bigdata_cloud_agent_manager_test/': {
        target: 'http://10.96.98.84:8026/',
        changeOrigin: true,
      },
      // '/': {
      //   target: 'http://10.164.13.170:8006/',
      //   changeOrigin: true,
      // },
      '/': {
        target: 'https://localhost:8001',
        secure: false,
        bypass: function(req) {
          if (req.headers.accept.indexOf('html') !== -1) {
            return '/index.html';
          }
        }
      }
    
    },
    historyApiFallback: true,
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
  },
  jsLoaderExclude: /node_modules\/(?!react-intl|intl-messageformat|intl-messageformat-parser)/
};
