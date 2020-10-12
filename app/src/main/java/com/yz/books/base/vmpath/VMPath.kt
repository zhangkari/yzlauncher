package com.yz.books.base.vmpath


@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class VMPath(val path:String)
