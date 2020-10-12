package com.yz.books.base.bean

/**
 * @author lilin
 * @time on 2019-11-02 18:48
 */

data class BaseBean<out T>(val code: Int, val message: String, val data: T)