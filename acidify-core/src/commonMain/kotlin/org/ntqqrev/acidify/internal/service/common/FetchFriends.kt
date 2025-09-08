package org.ntqqrev.acidify.internal.service.common

import org.ntqqrev.acidify.common.enum.UserInfoGender
import org.ntqqrev.acidify.common.enum.UserInfoKey
import org.ntqqrev.acidify.common.struct.BotFriendCategoryData
import org.ntqqrev.acidify.common.struct.BotFriendData
import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.oidb.FetchFriendsCookie
import org.ntqqrev.acidify.internal.packet.oidb.IncPull
import org.ntqqrev.acidify.internal.packet.oidb.IncPullResp
import org.ntqqrev.acidify.internal.service.OidbService
import org.ntqqrev.acidify.pb.invoke

internal object FetchFriends : OidbService<FetchFriends.Req, FetchFriends.Resp>(0xfd4, 1) {
    internal class Req(val nextUin: Long?)
    internal class Resp(
        val nextUin: Long?,
        val friendDataList: List<BotFriendData>,
        val friendCategoryList: List<BotFriendCategoryData>
    )

    override fun buildOidb(client: LagrangeClient, payload: Req): ByteArray = IncPull {
        it[reqCount] = 300
        it[cookie] = payload.nextUin?.let {
            FetchFriendsCookie {
                it[nextUin] = payload.nextUin
            }
        }
        it[requestBiz] = listOf(
            IncPull.Biz {
                it[bizType] = 1
                it[bizData] = IncPull.Biz.Busi {
                    it[extBusi] = listOf(
                        UserInfoKey.BIO,
                        UserInfoKey.REMARK,
                        UserInfoKey.NICKNAME,
                        UserInfoKey.QID,
                        UserInfoKey.AGE,
                        UserInfoKey.GENDER
                    ).map { key -> key.number }
                }
            },
            IncPull.Biz {
                it[bizType] = 4
                it[bizData] = IncPull.Biz.Busi {
                    it[extBusi] = listOf(100, 101, 102)
                }
            }
        )
    }.toByteArray()

    override fun parseOidb(client: LagrangeClient, payload: ByteArray): Resp {
        val resp = IncPullResp(payload)
        return Resp(
            nextUin = resp.get { cookie }?.get { nextUin },
            friendDataList = resp.get { friendList }.map { friend ->
                val subBiz = friend.get { subBizMap }
                    .find { it.get { key } == 1 }!!
                    .get { value }
                val numMap = subBiz.get { numDataMap }.associate { it.get { key } to it.get { value } }
                val strMap = subBiz.get { dataMap }.associate { it.get { key } to it.get { value } }
                BotFriendData(
                    uin = friend.get { uin },
                    uid = friend.get { uid },
                    nickname = strMap[UserInfoKey.NICKNAME.number] ?: "",
                    remark = strMap[UserInfoKey.REMARK.number] ?: "",
                    bio = strMap[UserInfoKey.BIO.number] ?: "",
                    qid = strMap[UserInfoKey.QID.number] ?: "",
                    age = numMap[UserInfoKey.AGE.number] ?: 0,
                    gender = UserInfoGender.entries
                        .find { it.value == numMap[UserInfoKey.GENDER.number] }
                        ?: UserInfoGender.UNKNOWN,
                    categoryId = friend.get { categoryId }
                )
            },
            friendCategoryList = resp.get { category }.map { category ->
                BotFriendCategoryData(
                    id = category.get { categoryId },
                    name = category.get { categoryName }
                )
            }
        )
    }
}