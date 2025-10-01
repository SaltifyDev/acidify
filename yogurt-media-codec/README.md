# yogurt-media-codec

Yogurt 的多媒体编解码支持模块，是 [LagrangeCodec](https://github.com/LagrangeDev/LagrangeCodec) 的 Kotlin 绑定。

在 JVM 平台，该模块使用 JNA 调用 LagrangeCodec 的动态链接库；在 Native 平台，该模块通过 `kotlinx.cinterop` 调用 LagrangeCodec 的动态链接库。

使用时，工作目录下需要包含 LagrangeCodec 的动态链接库文件，目录结构为：

```
working-directory/
├── lib/
│   ├── windows-x64/           (for Windows x64)
│   │   └── lagrangecodec.dll
│   ├── windows-x86/           (for Windows x86)
│   │   └── lagrangecodec.dll
│   ├── linux-x64/             (for Linux x64)
│   │   └── liblagrangecodec.so
│   ├── linux-arm64/           (for Linux arm64)
│   │   └── liblagrangecodec.so
│   ├── macos-arm64/           (for macOS arm64)
│   │   └── liblagrangecodec.dylib
│   └── macos-x64/             (for macOS x64)
│       └── liblagrangecodec.dylib
└── application.jar/exe/kexe
```

支持的平台和架构如上所示。