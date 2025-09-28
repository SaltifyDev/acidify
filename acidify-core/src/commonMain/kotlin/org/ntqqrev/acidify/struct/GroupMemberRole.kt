package org.ntqqrev.acidify.struct

/**
 * 群成员角色（权限等级）枚举
 */
enum class GroupMemberRole(val value: Int) {
    MEMBER(0),
    ADMIN(1),
    OWNER(2);

    companion object {
        fun from(value: Int): GroupMemberRole {
            return entries.firstOrNull { it.value == value } ?: MEMBER
        }
    }
}