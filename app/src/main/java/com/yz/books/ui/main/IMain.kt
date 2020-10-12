package com.yz.books.ui.main

/**
 * @author lilin
 * @time on 2020-01-14 20:44
 */
interface IMain {

    /**
     * 校验机器码
     */
    fun checkMachineCode(machineCode: String)

    /**
     * 离线数据更新
     */
    fun updateBD(machineCode: String)

    /**
     * 获取首页资源
     */
    fun getMainResources()

    /**
     * 检测下载列表资源更新
     */
    fun checkDownloadResource()
}