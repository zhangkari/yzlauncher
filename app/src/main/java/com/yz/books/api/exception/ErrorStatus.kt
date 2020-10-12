package com.yz.books.api.exception

/**
 * @author lilin
 * @time on 2020-01-20 17:45
 */
object ErrorStatus {
    /**
     * 响应成功
     */
    @JvmField
    val SUCCESS = 200

    /**
     * 登录失效
     */
    val TOKEN_EXPIRED = 1000
    /**
     * 城市cityCode为空
     */
    val CITY_COTY_EMPTY = 1065

    /**
     * 未知错误
     */
    @JvmField
    val UNKNOWN_ERROR = 1002

    /**
     * 服务器内部错误
     */
    @JvmField
    val SERVER_ERROR = 1003

    /**
     * 网络连接超时
     */
    @JvmField
    val NETWORK_ERROR = 1004
}