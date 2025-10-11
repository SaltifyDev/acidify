<div align="center">

![acidify](https://socialify.git.ci/LagrangeDev/acidify/image?custom_description=&description=1&font=Inter&forks=1&issues=1&language=1&logo=https%3A%2F%2Fstatic.live.moe%2Flagrange.jpg&name=1&owner=1&pulls=1&stargazers=1&theme=Light)

[QQ 群](https://qm.qq.com/q/C04kPQzayk) | [Telegram](https://t.me/WeavingStar)

</div>

## 使用

### 通过 Milky 协议对接其他框架

使用 **[Yogurt](yogurt/)**。参考 Yogurt 的文档下载并启动 Yogurt。

### 基于 `acidify-core` 使用 Kotlin 进行开发

在 Git 中引入该仓库作为 Submodule，然后在项目的 `settings.gradle.kts` 中添加：

```kts
includeBuild("acidify")
```

然后，需要引用时，在项目的 `build.gradle.kts` 中添加：

```kts
dependencies {
    implementation("acidify:acidify-core")
}
```

也可以用同样的方式引用其他模块。

## 模块一览

- `acidify-core` - PC NTQQ 协议的核心实现
- `acidify-crypto` - 加密与 Hash 算法的高效实现
- `acidify-pb` - Protobuf 编解码基础设施
- `yogurt` - 基于 Acidify 的 Milky 实现
- `yogurt-jvm` - Yogurt 的 JVM 平台实现 (Workaround for Ktor plugin's incompatibility issue)
- `yogurt-media-codec` - Yogurt 的多媒体编解码支持模块
- `yogurt-qrcode` - Yogurt 二维码矩阵生成

## 支持平台

- Kotlin/JVM
- Kotlin/Native
    - Windows via `mingwX64`
    - macOS via `macosArm64`
    - Linux via `linuxX64` and `linuxArm64`

## Special Thanks

- [Lagrange.Core](https://github.com/LagrangeDev/Lagrange.Core)
  提供项目的基础架构和绝大多数协议包定义
- [Konata.Core](https://github.com/KonataDev/Konata.Core)
  最初的 PC NTQQ 协议实现
- [lagrange-kotlin](https://github.com/LagrangeDev/lagrange-kotlin)
  提供 TEA & 登录认证的实现
- [qrcode-kotlin](https://github.com/g0dkar/qrcode-kotlin/)
  提供二维码矩阵生成的实现
- [LagrangeCodec](https://github.com/LagrangeDev/LagrangeCodec)
  提供多媒体编解码的实现
- [@Linwenxuan04](https://github.com/Linwenxuan04)
  编写 `acidify-crypto` 模块
- ... and all the contributors along the way!

## Contributors

### Directly to this repository

![Contributors of Acidify](https://contributors-img.web.app/image?repo=LagrangeDev/Acidify)

### Lagrange.Core

![Contributors of Lagrange.Core](https://contributors-img.web.app/image?repo=LagrangeDev/Lagrange.Core)

### LagrangeV2

![Contributors of LagrangeV2](https://contributors-img.web.app/image?repo=LagrangeDev/LagrangeV2)

### Konata.Core

![Contributors of Konata.Core](https://contributors-img.web.app/image?repo=KonataDev/Konata.Core)
