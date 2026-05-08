# uni-fronted 小程序项目配置指南

## 📋 项目配置清单

本指南将帮助你快速配置和启动uni-fronted小程序项目。

---

## 步骤一：配置后端API地址

### 文件位置
`uni-fronted/utils/request.js`

### 修改内容
```javascript
// 第3行 - 修改为你的后端服务地址
const BASE_URL = 'http://localhost:8080/api'
// ↓ 修改为
const BASE_URL = 'http://your-server-ip:8080/api'

// 或使用域名
const BASE_URL = 'https://api.yourdomain.com/api'
```

### 说明
- **开发环境**: 使用 `http://localhost:8080/api`（后端在本地运行）
- **测试环境**: 使用测试服务器地址
- **生产环境**: 必须使用HTTPS和正式域名

---

## 步骤二：配置微信小程序ID

### 文件位置
`uni-fronted/manifest.json`

### 修改内容
```json
{
  // ... 其他配置
  "mp-weixin": {
    "appid": "your-wechat-appid",  // ← 修改为你的微信小程序AppID
    "setting": {
      // ... 配置
    },
    "usingComponents": true
  }
  // ... 其他配置
}
```

### 获取方式
1. 登录 [微信公众平台](https://mp.weixin.qq.com/)
2. 进入"小程序"
3. 选择你的小程序
4. 在"设置" → "基本信息"中找到 AppID

### 示例
```json
{
  "mp-weixin": {
    "appid": "wxaa1234567890abcd"
  }
}
```

---

## 步骤三：准备Tab栏图标

### 文件位置
创建在 `uni-fronted/static/` 目录下

### 需要的图标文件
以下每个图标需要**两个版本**（普通版和选中版）：

| 功能 | 普通版图标 | 选中版图标 | 用途 |
|------|----------|----------|------|
| 收入 | `tab-income.png` | `tab-income-active.png` | 收入管理Tab |
| 支出 | `tab-expense.png` | `tab-expense-active.png` | 支出管理Tab |
| 统计 | `tab-statistics.png` | `tab-statistics-active.png` | 数据统计Tab |
| 我的 | `tab-mine.png` | `tab-mine-active.png` | 个人中心Tab |

### 图标规格
- **尺寸**: 60×60 像素
- **格式**: PNG格式（支持透明背景）
- **颜色**:
  - 普通版: 灰色 (#999999)
  - 选中版: 紫色 (#667eea)

### 创建步骤
1. 使用任意图像编辑工具（如Photoshop、GIMP等）
2. 创建60×60像素的图标
3. 保存为PNG格式
4. 放到 `static/` 目录

### 或使用在线工具
- [iconfont](https://www.iconfont.cn/) - 阿里巴巴矢量图标库
- [Emoji](https://emojipedia.org/) - 使用Emoji作为图标
- [IconMoon](https://icomoon.io/) - 图标字体生成

---

## 步骤四：配置小程序权限

### 文件位置
`uni-fronted/manifest.json`

### 所需权限
```json
{
  "mp-weixin": {
    "permission": {
      "scope.userLocation": {
        "desc": "获取用户地理位置"
      }
    }
  }
}
```

### 说明
- **scope.userLocation**: 获取用户位置（如需要）
- **scope.userInfo**: 获取用户信息（本项目需要）

---

## 步骤五：配置后端服务

### 确保后端已启动
```bash
# 后端启动命令（在backend目录）
cd backend
mvn clean spring-boot:run
```

### 验证后端连接
后端应该在以下地址运行：
- **API文档**: http://localhost:8080/doc.html
- **健康检查**: http://localhost:8080/actuator/health

### 后端必要配置
在 `backend/src/main/resources/application.yml` 中配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/work_record
    username: root
    password: root  # 修改为实际密码

wechat:
  appid: your-wechat-appid      # 微信AppID
  secret: your-wechat-secret    # 微信AppSecret

jwt:
  secret: your-jwt-secret-key    # JWT密钥
  expiration: 604800000          # Token过期时间
```

---

## 步骤六：开发工具配置

### 推荐工具
- **HBuilderX** (官方推荐)
  - 下载: https://www.dcloud.io/hbuilderx.html
  - 优点: 内置uni-app支持，开发体验最好

- **VS Code** (社区选择)
  - 需要安装uni-app插件

- **WebStorm/IntelliJ IDEA**
  - 需要安装uni-app插件

### HBuilderX 配置步骤

1. **安装HBuilderX**
   ```bash
   下载并安装 https://www.dcloud.io/hbuilderx.html
   ```

2. **打开项目**
   ```
   HBuilderX → 文件 → 打开项目 → 选择 uni-fronted 文件夹
   ```

3. **配置微信开发者工具路径**
   ```
   工具 → 设置 → 运行配置 → 小程序运行时路径
   选择你的微信开发者工具安装目录
   ```

4. **运行项目**
   ```
   右键项目 → 运行 → 运行到小程序模拟器
   ```

---

## 步骤七：微信小程序配置

### 开发者账号准备
1. 注册 [微信小程序](https://mp.weixin.qq.com/)
2. 创建小程序项目
3. 获取 AppID 和 AppSecret
4. 配置服务器地址

### 服务器配置（可选）
在微信公众平台配置消息接收：
- 服务器地址 (URL): `https://your-server.com/api/wechat`
- Token: 自定义的token
- EncodingAESKey: 自定义的加密密钥

### API权限
- 确保 `https://api.weixin.qq.com` 可访问
- 后端服务器能够访问外网

---

## 步骤八：初始化数据库（后端配置）

### 执行初始化脚本
```bash
# 使用mysql客户端
mysql -u root -p < database/init.sql

# 或在MySQL Workbench中执行脚本
```

### 创建的表
- `user` - 用户表
- `income_record` - 收入记录表
- `expense_record` - 支出记录表

### 验证数据库
```sql
USE work_record;
SHOW TABLES;
```

---

## 调试与测试

### 开启开发者模式

#### HBuilderX 内置模拟器
1. 运行项目后自动打开模拟器
2. 控制台显示日志信息
3. 支持实时热更新

#### 微信开发者工具
1. 打开微信开发者工具
2. 导入项目文件夹 `uni-fronted`
3. 输入AppID（获取的微信小程序ID）
4. 勾选"不校验合法域名"（开发环境）

### 调试技巧

#### 查看控制台日志
```javascript
// 在代码中打印日志
console.log('调试信息:', data)

// 开发者工具查看 - 网络 tab
// 查看请求和响应
```

#### 检查API请求
1. 打开微信开发者工具
2. 切换到"网络"标签
3. 刷新页面查看请求

#### 本地存储调试
```javascript
// 查看本地存储
console.log(uni.getStorageSync('token'))
console.log(uni.getStorageSync('userInfo'))
```

---

## 常见问题解决

### 问题1: "后端连接失败"
**原因**: 后端API地址配置错误或后端未启动

**解决方案**:
1. 检查 `utils/request.js` 中的 `BASE_URL` 是否正确
2. 确保后端服务已启动 (http://localhost:8080)
3. 检查是否存在跨域问题

### 问题2: "微信登录失败"
**原因**: AppID配置错误或微信服务器问题

**解决方案**:
1. 检查 `manifest.json` 中的 AppID
2. 确保AppID对应的微信小程序账号有效
3. 检查微信API文档

### 问题3: "页面加载缓慢"
**原因**: 网络问题或数据量过大

**解决方案**:
1. 使用分页加载
2. 检查网络连接
3. 优化API响应时间

### 问题4: "Token过期自动跳转登录"
**原因**: 正常行为 - Token已过期

**解决方案**:
1. 重新登录获取新Token
2. 在后端调整Token过期时间
3. 实现Token自动刷新机制

---

## 部署前检查清单

在发布到生产环境前，确保完成以下检查：

- [ ] API地址改为HTTPS协议
- [ ] 所有环境变量配置正确
- [ ] 后端服务部署完成
- [ ] 数据库备份完成
- [ ] 日志系统配置完成
- [ ] 错误上报系统配置完成
- [ ] 安全证书配置完成
- [ ] 性能测试通过
- [ ] 功能测试通过
- [ ] 安全审计通过

---

## 性能优化建议

### 前端优化
1. **图片优化**: 使用适当的图片格式和大小
2. **代码分割**: 使用动态导入减小初始包体积
3. **缓存策略**: 合理使用本地存储
4. **虚拟列表**: 大列表使用虚拟滚动

### 后端优化
1. **数据库索引**: 已经在建表脚本中添加
2. **查询优化**: 使用分页，避免一次性加载大量数据
3. **缓存**: 使用Redis缓存热点数据
4. **CDN**: 部署静态资源到CDN

---

## 进阶配置

### 环境变量配置
在 `utils/request.js` 中添加环境变量支持：
```javascript
const ENV = process.env.NODE_ENV || 'development'
const BASE_URL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080/api'
```

### 多环境支持
创建不同的配置文件：
- `.env.development` - 开发环境
- `.env.staging` - 测试环境
- `.env.production` - 生产环境

### 错误上报
```javascript
// 在 utils/request.js 中添加
if (code !== 200) {
  // 上报错误到监控系统
  uni.request({
    url: 'https://your-error-tracking.com/api/error',
    method: 'POST',
    data: { error: message }
  })
}
```

---

## 更新和维护

### 定期更新
- [ ] 检查 uni-app 版本更新
- [ ] 更新依赖包
- [ ] 检查安全漏洞
- [ ] 优化性能

### 日志记录
启用日志记录便于调试和监控：
```javascript
// 在 api 调用前后记录
console.log('请求:', url, data)
console.log('响应:', response)
```

---

## 获取帮助

### 文档链接
- [uni-app官方文档](https://uniapp.dcloud.net.cn/)
- [API参考](https://uniapp.dcloud.net.cn/api/)
- [问题反馈](https://github.com)

### 社区支持
- uni-app官方论坛
- Stack Overflow
- GitHub Issues

---

## 项目完成

所有配置完成后，你就可以开始开发或测试了！

```bash
# 最后验证
✓ 后端API地址配置完成
✓ 微信AppID配置完成
✓ Tab栏图标准备完成
✓ 后端服务启动完成
✓ 数据库初始化完成

👍 项目已准备好进行开发/测试
```

---

**最后更新**: 2024年5月
**配置难度**: ⭐⭐☆☆☆ (中等)
**所需时间**: 15-30分钟

