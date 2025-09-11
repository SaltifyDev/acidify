<div align="center">

<h1>Acidify</h1>

PC NTQQ 协议的 Kotlin 实现，支持 JVM 和 Native 平台

</div>

## 模块一览

- `acidify-core` - PC NTQQ 协议的核心实现
- `acidify-crypto` - 加密与 Hash 算法的高效实现
- `acidify-pb` - Protobuf 编解码基础设施
- `acidify-qrcode` - 二维码矩阵生成
- `yogurt` - 基于 Acidify 的 [Milky](https://milky.ntqqrev.org/) 实现
- `yogurt-jvm` - Yogurt 的 JVM 平台实现 (Workaround for Ktor plugin's incompatibility issue)

## 支持平台

- Kotlin/JVM
- Kotlin/Native
    - Windows via `mingwX64`
    - macOS via `macosX64` and `macosArm64`
    - Linux via `linuxX64` and `linuxArm64`

## Special Thanks

- [Lagrange.Core](https://github.com/LagrangeDev/Lagrange.Core)
  最初的 PC NTQQ 协议实现，提供项目的基础架构和绝大多数协议包定义
- [lagrange-kotlin](https://github.com/LagrangeDev/lagrange-kotlin)
  提供 TEA & 登录认证的实现
- @Linwenxuan04
  编写 `acidify-crypto` 模块
- [qrcode-kotlin](https://github.com/g0dkar/qrcode-kotlin/)
  提供二维码矩阵生成的实现
