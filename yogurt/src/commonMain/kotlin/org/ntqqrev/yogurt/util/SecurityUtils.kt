package org.ntqqrev.yogurt.util

import com.github.ajalt.mordant.rendering.TextColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import org.ntqqrev.yogurt.YogurtApp

val isDockerEnv: Boolean by lazy {
    SystemFileSystem.exists(Path("/.dockerenv"))
}

fun checkSecurity() = runBlocking {
    if (
        !YogurtApp.config.skipSecurityCheck &&
        YogurtApp.config.httpConfig.host == "0.0.0.0" &&
        YogurtApp.config.httpConfig.accessToken.isEmpty() &&
        !isDockerEnv
    ) {
        println(
            TextColors.brightYellow(
                """
                    |警告：你可能正在将 Yogurt 的 Milky 服务暴露在公网环境下，且未设置 accessToken。
                    |这可能导致你的 QQ 账号被他人恶意使用，造成损失。
                    |请在设置中配置 accessToken，或将 host 设置为 127.0.0.1 或其他内网 IP 地址。
                    |如果你明确知道自己在做什么，可以在配置文件中将 skipSecurityCheck 设置为 true 以跳过此检查。
                    |程序将在 10 秒后继续运行...
                """.trimMargin()
            )
        )
        delay(10_000)
    }
}