package org.ntqqrev.yogurt.api.system

import org.ntqqrev.yogurt.api.routeMilkyApi
import org.ntqqrev.yogurt.protocol.ApiEmptyStruct
import org.ntqqrev.yogurt.protocol.GetLoginInfoResponse

val routeGetLoginInfoApi = routeMilkyApi<ApiEmptyStruct, GetLoginInfoResponse>("get_login_info") {
    GetLoginInfoResponse(
        uin = uin,
        nickname = "TODO" // todo: resolve nickname
    )
}