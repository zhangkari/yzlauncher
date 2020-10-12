package com.yz.books.api

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * @author lilin
 * @time on 2019-11-03 10:35
 */

val UI: CoroutineDispatcher      = Dispatchers.Main

val IO: CoroutineDispatcher      = Dispatchers.IO

val Default: CoroutineDispatcher = Dispatchers.Default