const d1Config = require('./d1.json');
const reg = new RegExp('^/agent');
const path = require('path');
// const CountComponentPlugin = require('./config/CountComponentWebpackPlugin');
d1Config.appConfig.webpackCustom = {
  devServer: {
    inline: true,
    port: 8000,
    host: '0.0.0.0',
    publicPath: '/',
    proxy: {
      '/api/v1': {
        changeOrigin: true,
        // target: 'http://116.85.69.237:8080',
        // target: 'http://116.85.35.62:8010',
        // target: 'http://116.85.69.237:8010',
        // target: 'http://116.85.55.101/',
        target: 'http://10.96.65.64/',
      },
    },
    historyApiFallback: {
      rewrites: [{ from: reg, to: '/agent.html' }],
    },
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
  },
  resolve: {
    alias: {
      '@tpl_packages': path.resolve(__dirname, 'src/packages'),
      '@lib': path.resolve(__dirname, 'src/lib'),
    },
  },
  plugins: [
    // new CountComponentPlugin({
    //   pathname: '@didi/dcloud-design',
    //   startCount: true,
    //   isExportExcel: false,
    // }),
  ],
};
module.exports = d1Config;
