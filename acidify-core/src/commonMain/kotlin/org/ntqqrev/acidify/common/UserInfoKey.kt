package org.ntqqrev.acidify.common

/**
 * 用户信息字段枚举
 */
enum class UserInfoKey(val number: Int) {
    /**
     * 头像 URL
     */
    AVATAR(101),

    /**
     * 个性签名
     */
    BIO(102),

    /**
     * 备注
     */
    REMARK(103),

    /**
     * QQ 等级
     */
    LEVEL(105),

    /**
     * 业务列表
     */
    BUSINESS_LIST(107),

    /**
     * 昵称
     */
    NICKNAME(20002),

    /**
     * 国家
     */
    COUNTRY(20003),

    /**
     * 性别
     */
    GENDER(20009),

    /**
     * 城市
     */
    CITY(20020),

    /**
     * 学校
     */
    SCHOOL(20021),

    /**
     * 注册的 Unix 时间戳（秒）
     */
    REGISTER_TIME(20026),

    /**
     * 年龄
     */
    AGE(20037),

    /**
     * QID
     */
    QID(27394);
}