### 使用方法
1. 安装脚手架
```
npm i @didi/d1-cli -g
```
2. 进入项目，启动项目
```
d1 start
```
3. 本地地址 http://localhost:8000/agent

### 常见错误提示
* 根目录缺少proxy.config.js：本地服务请求代理配置文件，自行创建示例如下
```
module.exports = {
  '/api/es/admin': {
    pathRewrite: { '^/api/es/admin': '/bigdata_databus_arius_admin_v2_3_test_epri_test' },
    target: 'http://api-kylin-xg02.intra.xiaojukeji.com',
    changeOrigin: true,
  }
};
```
