# 本地开发环境配置指南

## 🚨 常见错误说明

### 错误1：内容安全检测API失败
```
Error: SystemError (appServiceSDKScriptError)
{"errMsg":"webapi_getwxaasyncsecinfo:fail "}
```
**原因**：没有配置有效的微信小程序AppID，导致微信的内容安全检测API调用失败。

**解决方案**：按照下面的"方法一"配置"不校验"选项即可。

### 错误2：WXSS 文件编译错误
```
[ WXSS 文件编译错误] 
./app.wxss(3:1): unexpected token `*`
```
**原因**：在 WXSS 中使用了 `*` 通配符选择器，这在微信小程序中不被支持。

**解决方案**：已将 `App.vue` 中的 `*` 选择器修改为 `page` 选择器。如果你在其他页面遇到类似问题，请将 `*` 替换为具体的选择器（如 `page`、`view` 等）。

## ✅ 解决方案（本地开发测试）

### 方法一：在微信开发者工具中配置（推荐）

1. **打开微信开发者工具**
2. **导入项目**：
   - 点击"导入项目"
   - 选择 `uni-fronted` 文件夹
3. **配置项目**：
   - 项目名称：随意填写（如"零工记账"）
   - 目录：选择 `uni-fronted` 文件夹
   - AppID：**保持空白** 或 填写 `测试号`（如果没有正式AppID）
   - 点击"导入"
4. **关键配置**：
   - 导入后，点击右上角的 **"详情"** 按钮
   - 在弹出的窗口中选择 **"本地设置"** 标签
   - ✅ **勾选** "不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书"
   - 关闭窗口

### 方法二：使用微信测试号（如果需要完整功能）

1. **获取测试号**：
   - 访问 [微信测试号管理页面](https://mp.weixin.qq.com/debug/cgi-bin/sandbox?t=admin/lang/zh_CN&token=&lang=zh_CN)
   - 使用微信扫码登录
   - 获取测试AppID和AppSecret

2. **配置到项目中**：
   - 打开 `manifest.json`
   - 找到 `"mp-weixin"` 部分
   - 将 `"appid": ""` 修改为你的测试AppID
   - 保存文件

3. **重新编译运行**：
   - 在HBuilderX中重新运行到微信开发者工具
   - 或在微信开发者工具中刷新项目

## 🛠️ 开发环境完整配置步骤

### 1. 启动后端服务

```bash
# 进入后端目录
cd backend

# 配置数据库（首次运行需要）
# 编辑 src/main/resources/application.yml
# 修改数据库连接信息

# 启动后端
mvn clean spring-boot:run
```

### 2. 初始化数据库

```bash
# 使用MySQL客户端执行初始化脚本
mysql -u root -p < database/init.sql

# 或手动在MySQL工具中执行 database/init.sql 文件
```

### 3. 配置前端API地址

编辑 `uni-fronted/utils/request.js`：
```javascript
const BASE_URL = 'http://localhost:8080/api'
```

### 4. 运行前端项目

#### 方式A：使用HBuilderX（推荐）
1. 打开HBuilderX
2. 文件 → 打开目录 → 选择 `uni-fronted` 文件夹
3. 配置微信开发者工具路径：
   - 工具 → 设置 → 运行配置 → 小程序运行时路径
   - 选择你的微信开发者工具安装目录
4. 运行项目：
   - 点击工具栏的 "运行" 按钮
   - 选择 "运行到小程序模拟器" → "微信开发者工具"

#### 方式B：使用微信开发者工具
1. 打开微信开发者工具
2. 导入项目（选择 `uni-fronted` 文件夹）
3. 按照上面的"方法一"配置
4. 点击编译按钮

### 5. 验证运行

- 后端健康检查：访问 http://localhost:8080/actuator/health
- 前端：在微信开发者工具中查看是否正常运行

## 📝 注意事项

### 关于AppID
- **开发测试阶段**：可以不填AppID，但部分微信原生功能可能无法使用
- **完整功能测试**：建议使用微信测试号
- **生产环境**：必须使用正式的微信小程序AppID

### 关于错误信息
- `webapi_getwxaasyncsecinfo:fail` 错误在开发阶段可以忽略
- 这个错误不会影响核心业务功能（收入/支出记录）
- 只要正确配置了"不校验"选项，小程序可以正常运行

### 常见问题

#### Q: 为什么会有这个错误？
A: 因为代码中可能间接调用了微信的内容安全检测API，或者uni-app框架内部使用了这个API。在没有有效AppID的情况下，这个API调用会失败。

#### Q: 这个错误会影响功能吗？
A: 在开发阶段，只要配置了"不校验"，这个错误不会影响小程序的正常运行。但建议在生产环境中配置正确的AppID。

#### Q: 如何彻底消除这个错误？
A: 获取正式的微信小程序AppID并配置到 `manifest.json` 中。

## 🔧 调试技巧

### 查看控制台日志
在微信开发者工具中：
- 点击"调试器"标签
- 查看Console面板的错误和警告信息

### 检查网络请求
- 点击"调试器" → "Network"标签
- 查看API请求和响应

### 检查本地存储
在控制台执行：
```javascript
// 查看token
console.log(uni.getStorageSync('token'))

// 查看用户信息
console.log(uni.getStorageSync('userInfo'))
```

## 📞 获取帮助

如果遇到问题：
1. 检查后端服务是否正常启动
2. 检查数据库连接配置
3. 检查API地址配置
4. 查看微信开发者工具的控制台错误信息

---

**最后更新**: 2024年5月
**适用环境**: 本地开发测试