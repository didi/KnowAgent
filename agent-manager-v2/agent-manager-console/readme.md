## 运行环境
`node 14.15.1 (node版本不低于12版本)`
`npm 6.4.8`

### 使用方法
1. 安装依赖
```
npm install
```
2. 进入项目，启动项目
```
npm start
```
3. 本地地址 
```
http://localhost:8000/agent
```
4. 打包
```
npm run build
```

### 本地开发代理配置
fetk.config.js 文件配置
```
devServer: {
  proxy: {
    '/api/v1': {
      target: 'path',
      changeOrigin: true,
    }
  }
},
```