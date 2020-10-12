package com.yz.books.ext

import android.app.Dialog
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.EditText
import android.widget.TextView

/**
 * @author lilin
 * @time on 2019-12-17 09:38
 */

/**
 * dialog扩展函数
 */
fun Dialog?.dismissDialog() {
    this?.let {
        if (isShowing) {
            dismiss()
        }
    }
}

/**
 * 获取edittext内容
 */
//扩展函数
fun EditText.getContent(): String {
    return text.toString().trim()
}

fun TextView.getContent(): String {
    return text.toString().trim()
}

//扩展属性
var EditText.textContent: String
    get() = text.toString().trim()
    set(value) {
        setText(value)
    }

var TextView.textContent: String
    get() = text.toString().trim()
    set(value) {
        text = value
    }

/**
 * 收藏状态
 * @param status 0未收藏
 */
fun TextView.setCollectStatus(status: Int) {
    isSelected = status != 0
    text = if (status == 0) {
        "收藏"
    } else {
        "已收藏"
    }
}

/**
 * 关键字标颜色
 * @param _text 文本
 * @param _specifiedText 关键字
 * @param color 颜色
 * @param firstKeyWord 是否只标记第一个出现的关键字
 * @return
 */
fun TextView.setSpecifiedTextColor(_text: String, _specifiedText: String,
                                   color: Int, firstKeyWord: Boolean? = false) {
    val text = _text.toUpperCase()
    val specifiedTexts = _specifiedText.toUpperCase()

    val sTextsStartList = mutableListOf<Int>()

    val sTextLength = specifiedTexts.length
    var temp = text
    var lengthFront = 0//记录被找出后前面的字段的长度
    var start = -1
    do {
        start = temp.indexOf(specifiedTexts)//.indexOf(specifiedTexts);
        if (start != -1) {
            start += lengthFront
            sTextsStartList.add(start)
            lengthFront = start + sTextLength
            temp = text.substring(lengthFront)
        }

    } while (start != -1)

    val styledText = SpannableStringBuilder(text)
    for (i in sTextsStartList) {
        styledText.setSpan(
            ForegroundColorSpan(color),
            i,
            i + sTextLength,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (firstKeyWord == true) {
            break
        }
    }

    setText(styledText)
    //return styledText
}