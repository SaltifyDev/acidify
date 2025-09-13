package org.ntqqrev.yogurt.api.system

import org.ntqqrev.yogurt.api.routeMilkyApi
import org.ntqqrev.yogurt.protocol.GetLoginInfoInput
import org.ntqqrev.yogurt.protocol.GetLoginInfoOutput

val routeGetLoginInfoApi = routeMilkyApi<GetLoginInfoInput, GetLoginInfoOutput>("get_login_info") {
    GetLoginInfoOutput(
        uin = uin,
        nickname = "TODO" // todo: resolve nickname
    )
}