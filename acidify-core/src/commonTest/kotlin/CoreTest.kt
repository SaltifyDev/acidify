import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking
import org.ntqqrev.acidify.common.AppInfo
import org.ntqqrev.acidify.common.SessionStore
import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.service.system.FetchQrCode
import org.ntqqrev.acidify.util.UrlSignProvider
import kotlin.test.BeforeTest
import kotlin.test.Test

val defaultAppInfo = AppInfo(
    os = "Linux",
    kernel = "Linux",
    vendorOs = "linux",
    currentVersion = "3.2.15-30366",
    miscBitmap = 32764,
    ptVersion = "2.0.0",
    ssoVersion = 19,
    packageName = "com.tencent.qq",
    wtLoginSdk = "nt.wtlogin.0.0.1",
    appId = 1600001615,
    subAppId = 537258424,
    appClientVersion = 30366,
    mainSigMap = 169742560,
    subSigMap = 0,
    ntLoginType = 1
)

val defaultSignProvider = UrlSignProvider("https://sign.lagrangecore.org/api/sign/30366")

class CoreTest {
    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var client: LagrangeClient

    @BeforeTest
    fun setup() {
        client = LagrangeClient(
            appInfo = defaultAppInfo,
            sessionStore = SessionStore.empty(),
            signProvider = defaultSignProvider,
            scope = scope
        )
        runBlocking { client.packetLogic.connect() }
    }

    @Test
    fun fetchQrCode() {
        val qrCode = runBlocking { client.callService(FetchQrCode) }
        println("QR Code URL: ${qrCode.qrCodeUrl}")
    }
}