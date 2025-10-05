<div align="center">

<h1>Yogurt</h1>

**Acid**ify + **Milk**y = Yogurt!

</div>

Yogurt 是基于 Acidify 的 [Milky](https://milky.ntqqrev.org/) 实现，支持 Kotlin/JVM 和 Kotlin/Native 平台。

## 启动

Yogurt 支持的平台有 Kotlin/JVM 和 Kotlin/Native (Windows x64, macOS arm64, Linux x64/arm64)。

### 通过可执行文件启动 (Kotlin/Native)

在 [Actions](https://github.com/LagrangeDev/acidify/actions/workflows/build-yogurt-native.yml)
中下载对应平台的可执行文件，解压到工作目录后运行：

```
./yogurt.kexe   (for Linux and macOS)
yogurt.exe      (for Windows)
```

### 通过 Java 运行时启动 (Kotlin/JVM)

配置 Java 21+ 运行时，然后在 [Actions](https://github.com/LagrangeDev/acidify/actions/workflows/build-yogurt-jvm.yml) 中下载
JAR 文件，运行：

```
java -jar yogurt-jvm-all.jar
```

注意：即使使用 Kotlin/JVM 版本，由于 Yogurt 依赖 [LagrangeCodec](https://github.com/LagrangeDev/LagrangeCodec)
的预编译构建，因此只支持在以下平台**发送语音和视频消息**：

| OS      | Arch       |
|---------|------------|
| Windows | x86, x64   |
| Linux   | x64, arm64 |
| macOS   | x64, arm64 |

## 配置

Yogurt 在启动后，会在当前工作目录下生成 `config.json` 文件，用户可以编辑该文件来配置 Yogurt。

```json
{
  "signApiUrl": "https://sign.lagrangecore.org/api/sign/39038",
  "reportSelfMessage": true,
  "httpConfig": {
    "host": "127.0.0.1",
    "port": 3000,
    "accessToken": ""
  },
  "webhookConfig": {
    "url": []
  },
  "logging": {
    "coreLogLevel": "INFO",
    "messageLogLevel": "INFO"
  }
}
```

下面是各配置项的说明：

### `signApiUrl`

签名服务地址。默认地址由 Lagrange.Core 的开发者提供。

### `reportSelfMessage`

是否上报自己发送的消息。

### `httpConfig` 和 `webhookConfig`

Milky 协议服务的有关配置，参考 [Milky 文档的“通信”部分](https://milky.ntqqrev.org/guide/communication)。

### `logging`

见下面的“日志配置”部分。

## 日志配置

Yogurt 的日志分为两类：由 Yogurt 自身产生的日志和 Ktor 产生的日志。在不同平台下，日志的配置方式有很大不同。

### Kotlin/Native 平台

Kotlin/Native 平台的 Yogurt 使用 `println` 输出日志。可以想象有以下的闸门：

```
Yogurt 消息处理模块日志
    ↓ 闸门 1 (由 messageLogLevel 控制)
Yogurt 核心模块日志
    ↓ 闸门 2 (由 coreLogLevel 控制)
标准输出
```

```
Ktor 日志
    ↓ 闸门 3 (由 KTOR_LOG_LEVEL 环境变量控制)
标准输出
```

要控制 Yogurt 日志的输出级别，可以在 `config.json` 中配置 `logging.coreLogLevel` 和 `logging.messageLogLevel`：

- `coreLogLevel` 控制 Yogurt 核心模块的日志输出级别；
- `messageLogLevel` 控制消息处理模块的日志输出级别。消息处理模块是 Yogurt 核心模块的一部分，因此如果 `coreLogLevel` 设置得比
  `messageLogLevel` 更高，则不会打印任何消息处理模块的日志。

上述两个配置项的可选值有 `VERBOSE`, `DEBUG`, `INFO`, `WARN`, `ERROR`。如果不设置这些配置项，则默认输出 `INFO` 级别的日志。

要控制 Ktor 日志的输出级别，可以设置环境变量 `KTOR_LOG_LEVEL`，可选值有 `DEBUG`, `INFO`, `WARN`, `ERROR`。如果不设置该环境变量，则
Ktor 默认输出 `INFO` 级别及以上的日志。

### Kotlin/JVM 平台

Kotlin/JVM 平台的 Yogurt 使用 [Logback](https://logback.qos.ch/) 进行日志管理。可以想象有以下的闸门：

```
Yogurt 消息处理模块日志
    ↓ 闸门 1 (由 messageLogLevel 控制)
Yogurt 核心模块日志
    ↓ 闸门 2 (由 coreLogLevel 控制)
Logback 处理
    ↓ 闸门 3 (由 logback.xml 控制)
标准输出 / 文件
```

```
Ktor 日志 (直接由 Logback 处理)
    ↓ 闸门 3 (由 logback.xml 控制)
标准输出 / 文件
```

要控制 Yogurt 日志的输出级别，可以在 `config.json` 中配置 `logging.coreLogLevel` 和 `logging.messageLogLevel`，设置方式和
Kotlin/Native 平台相同。

Yogurt/JVM 的日志最终由 Logback 处理，因此可以通过配置 Logback 来控制日志的输出方式和格式。JAR 文件中已经包含了一个默认的
`logback.xml`，如果需要自定义日志配置，可以在运行时通过 `-Dlogback.configurationFile=path/to/logback.xml` 指定自定义的配置文件。
