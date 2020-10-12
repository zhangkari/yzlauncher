package com.yz.books.base.viewmodel

/**
 * @author lilin
 * @time on 2019-12-16 21:24
 */

/**
 * Abstract State
 */
open class State

/**
 * Generic Loading State
 */

class LoadingState(
    val resId: Int = 0
) : State()

class LoadedState : State()

/**
 * Generic Error state
 * @param errorMsg
 * @param errorCode
 * @param source 对应接口来源
 */
class ErrorState(
    val errorMsg: String,
    val errorCode: Int = -0x11,
    val source: String = ""
) : State()