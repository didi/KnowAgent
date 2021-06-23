const { override, addWebpackPlugin } = require('customize-cra');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');

module.exports = override(
  // 判断环境，只有在生产环境的时候才去使用这个插件
  // 如果不想这样做的话可以只修改build的命令为"build": "react-app-rewired build"
  process.env.NODE_ENV === 'production' && addWebpackPlugin(new UglifyJsPlugin({
    uglifyOptions: {
      compress: {
        drop_debugger: true,
        drop_console: true
      }
    }
  }))
);