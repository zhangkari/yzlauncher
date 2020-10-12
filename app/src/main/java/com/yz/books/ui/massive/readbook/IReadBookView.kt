package com.yz.books.ui.massive.readbook

import android.view.View

/**
 * @author lilin
 * @time on 2020-01-13 08:25
 */
interface IReadBookView {

    /**
     * 设置阅读书名
     */
    fun setBookTitle(title: String?)

    /**
     * 目录
     */
    fun setBookChapters(chapter: MutableList<String>)

    /**
     * 拿到需要显示的 PDF EPUB TXT 的View
     */
    fun getDocumentView(): View

    /**
     * 拿到文件类型
     */
    fun getDocType(): DocTypeEnum

    /**
     * 获取书的信息
     */
    fun getReadBookBean(): ReadBookBean?

    /**
     * 设置字体大小
     */
    fun setFontSize(size: Int)
}