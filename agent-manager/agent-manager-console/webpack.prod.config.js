const { HotModuleReplacementPlugin, ProgressPlugin, DefinePlugin, SourceMapDevToolPlugin } = require('webpack');
const calc = require('postcss-calc');
const presetEnv = require('postcss-preset-env');
module.exports = {
  mode: 'production',
  plugins: [
    /* config.plugin('ProgressPlugin') */
    new ProgressPlugin(
      {
        profile: true
      }
    ),
    /* config.plugin('mini-css-extract-plugin') */
    new (require('mini-css-extract-plugin'))(
      {
        filename: 'css/[name].[contenthash].css',
        chunkFilename: 'css/[name].[contenthash].css'
      }
    ),
    /* config.plugin('html-template?entry=agent') */
    new (require('html-webpack-plugin'))(
      {
        title: 'KnowAgent',
        context: {
          env: 'env',
          isDebug: false,
          isProduction: false
        },
        favicon: './favicon.ico',
        filename: require('path').resolve(__dirname, '../agent-manager-rest/src/main/resources/templates/pages/agent.html'),
        template: require('path').resolve(__dirname, 'src/index.html'),
        chunks: [
          'agent',
          'vendor',
          'common',
          'runtime'
        ]
      }
    ),
    /* config.plugin('script-ext-html-webpack-plugin') */
    new (require('script-ext-html-webpack-plugin'))(
      {
        custom: {
          test: /\.js$/,
          attribute: 'crossorigin',
          value: 'anonymous'
        }
      }
    ),
    /* config.plugin('d1-plugin-case-sensitive-paths') */
    new (require('case-sensitive-paths-webpack-plugin'))(
      {
        debug: false
      }
    ),
    /* config.plugin('webpack-manifest-plugin') */
    new (require('webpack-manifest-plugin'))(),
    /* config.plugin('webpack.DefinePlugin') */
    new DefinePlugin(
      {
        'process.env.config': '"development"',
        'process.env.NODE_ENV': '"development"'
      }
    ),
    /* config.plugin('SourceMapDevToolPlugin') */
    new SourceMapDevToolPlugin(
      {
        filename: 'sourcemap/[file].map',
        publicPath: '/env/'
      }
    ),
    new (require('copy-webpack-plugin'))([
      {
        from: require('path').resolve(__dirname, 'favicon.ico'),
        to: require('path').resolve(__dirname, '../agent-manager-rest/src/main/resources/templates/env/favicon.ico'),
      },
    ])
  ],
  output: {
    filename: 'js/[name].[contenthash].js',
    path: require('path').resolve(__dirname, '../agent-manager-rest/src/main/resources/templates/env'),
    publicPath: '/env/'
  },
  resolve: {
    alias: {
      '@tpl_packages': require('path').resolve(__dirname, 'src/packages'),
      '@lib': require('path').resolve(__dirname, 'src/lib')
    },
    extensions: [
      '.js',
      '.jsx',
      '.ts',
      '.tsx',
      '.json',
      '.vue'
    ]
  },
  devServer: {
    inline: true,
    port: 8000,
    host: '0.0.0.0',
    publicPath: '/',
    proxy: {
      '/api/v1': {
        changeOrigin: true,
        target: 'http://10.96.65.64/'
      }
    },
    historyApiFallback: {
      rewrites: [
        {
          from: /^\/agent/,
          to: '/agent.html'
        }
      ]
    },
    headers: {
      'Access-Control-Allow-Origin': '*'
    },
    openPage: 'agent'
  },
  module: {
    rules: [
      /* config.module.rule('module-js') */
      {
        test: /\.(js|jsx|ts|tsx)$/,
        include: [
          require('path').resolve(__dirname, 'src')
        ],
        use: [
          /* config.module.rule('module-js').use('babel-loader') */
          {
            loader: 'babel-loader',
            options: {
              presets: [
                [
                  '@babel/preset-env',
                  {
                    modules: false,
                    targets: {
                      chrome: '58',
                      ie: '11'
                    },
                    useBuiltIns: false
                  }
                ],
                [
                  '@babel/preset-typescript'
                ]
              ],
              plugins: [
                [
                  '@babel/plugin-proposal-decorators',
                  {
                    legacy: true
                  }
                ],
                [
                  '@babel/plugin-transform-runtime',
                  {
                    absoluteRuntime: '@babel/runtime'
                  }
                ],
                [
                  '@babel/plugin-proposal-class-properties',
                  {
                    loose: true
                  }
                ],
                [
                  '@babel/plugin-syntax-dynamic-import'
                ],
                [
                  'babel-plugin-import',
                  {
                    libraryName: 'antd',
                    style: true
                  }
                ]
              ]
            }
          }
        ]
      },
      /* config.module.rule('module-css') */
      {
        test: /\.css$/,
        use: [
          /* config.module.rule('module-css').use('mini-css-extract-plugin/loader') */
          {
            loader: 'mini-css-extract-plugin/dist/loader.js'
          },
          /* config.module.rule('module-css').use('css-loader') */
          {
            loader: 'css-loader',
            options: {
              sourceMap: false,
              importLoaders: 1,
              modules: false
            }
          },
          /* config.module.rule('module-css').use('postcss-loader') */
          {
            loader: 'postcss-loader',
            options: {
              sourceMap: false,
              plugins: [
                calc(),
                presetEnv({
                  browsers: 'chrome >= 58, ie >= 11',
                  autoprefixer: true,
                  stage: 3,
                }),
              ]
            }
          }
        ]
      },
      /* config.module.rule('module-less') */
      {
        test: /\.less$/,
        use: [
          /* config.module.rule('module-less').use('mini-css-extract-plugin/loader') */
          {
            loader: 'mini-css-extract-plugin/dist/loader.js'
          },
          /* config.module.rule('module-less').use('css-loader') */
          {
            loader: 'css-loader',
            options: {
              sourceMap: false,
              importLoaders: 1,
              modules: false
            }
          },
          /* config.module.rule('module-less').use('postcss-loader') */
          {
            loader: 'postcss-loader',
            options: {
              sourceMap: false,
              plugins: [
                calc(),
                presetEnv({
                  browsers: 'chrome >= 58, ie >= 11',
                  autoprefixer: true,
                  stage: 3,
                }),
              ]
            }
          },
          /* config.module.rule('module-less').use('less-loader') */
          {
            loader: 'less-loader',
            options: {
              lessOptions: {
                javascriptEnabled: true,
                modifyVars: {}
              }
            }
          }
        ]
      },
      /* config.module.rule('module-html') */
      {
        test: /\.html$/,
        use: [
          /* config.module.rule('module-html').use('html-loader') */
          {
            loader: 'html-loader'
          }
        ]
      },
      /* config.module.rule('module-image') */
      {
        test: /\.(png|jpg|jpeg|gif|svg)$/,
        use: [
          /* config.module.rule('module-image').use('url-loader') */
          {
            loader: 'url-loader',
            options: {
              limit: 1000,
              name: 'images/[name]-[hash:10].[ext]'
            }
          }
        ]
      },
      /* config.module.rule('module-font') */
      {
        test: /\.(ttf|ttc|eot|dfont|otf|woff|woff2)$/,
        use: [
          /* config.module.rule('module-font').use('url-loader') */
          {
            loader: 'url-loader',
            options: {
              limit: 1000,
              name: 'fonts/[name]-[hash:10].[ext]'
            }
          }
        ]
      },
      /* config.module.rule('module-json') */
      {
        test: /\.json$/,
        type: 'javascript/auto',
        use: [
          /* config.module.rule('module-json').use('json-loader') */
          {
            loader: 'json-loader'
          }
        ]
      },
      /* config.module.rule('module-ts-script') */
      {
        test: /\.(ts|tsx)$/,
        exclude: [
          /node_modules\/(@ant-design\/icons)/
        ],
        use: [
          /* config.module.rule('module-ts-script') */
          {
            loader: 'ts-loader',
            options: {
              allowTsInNodeModules: true
            }
          }
        ]
      }
    ]
  },
  optimization: {
    moduleIds: 'hashed',
    chunkIds: 'named',
    runtimeChunk: false,
    splitChunks: {
      minSize: 244000,
      cacheGroups: {
        vendor: {
          test: /\/node_modules\//,
          chunks: 'initial',
          name: 'vendor',
          priority: 20,
          minChunks: 1,
          reuseExistingChunk: true
        },
        common: {
          chunks: 'initial',
          name: 'common',
          priority: 10,
          minChunks: 1,
          reuseExistingChunk: true
        }
      }
    }
  },
  entry: {
    agent: [
      './src/index.tsx'
    ]
  }
}