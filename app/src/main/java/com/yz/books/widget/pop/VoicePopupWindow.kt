package com.yz.books.widget.pop

import android.app.Activity
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.yz.books.R
import com.yz.books.ext.textContent
import kotlin.math.abs


/**
 * @author lilin
 * @time on 2019-12-16 15:41
 */
class VoicePopupWindow(context: Activity,
                       val callback: (Int) -> Unit) : BasePopupWindow(context) {

    private lateinit var tvVoiceValue: TextView
    private lateinit var ivVoiceLess: ImageView
    private lateinit var ivVoiceAdd: ImageView

    override fun initView() {
        val popView = LayoutInflater.from(context).inflate(R.layout.view_voice_pop, null)
        contentView = popView

        tvVoiceValue = popView.findViewById(R.id.tv_voice_value)
        ivVoiceLess = popView.findViewById(R.id.iv_voice_less)
        ivVoiceAdd = popView.findViewById(R.id.iv_voice_add)
    }

    override fun initListener() {
        ivVoiceLess.setOnClickListener {
            var voiceValue = tvVoiceValue.textContent.toInt()
            if (voiceValue > 0) {
                voiceValue -= 10
                tvVoiceValue.text = "$voiceValue"
                callback(voiceValue)
            }

        }

        ivVoiceAdd.setOnClickListener {
            var voiceValue = tvVoiceValue.textContent.toInt()
            if (voiceValue < 100) {
                voiceValue += 10
                tvVoiceValue.text = "$voiceValue"
                callback(voiceValue)
            }
        }
    }

    fun initVoiceValue(voiceValue: Float) {
        tvVoiceValue.text = "${(voiceValue * 100).toInt()}"
    }

    /**
     * 基于控件底部展示
     * @param view
     */
    fun showAtBottom(view: View) {
        val xoff = abs(contentView.measuredWidth - view.width) / 2
        val yoff = abs(contentView.measuredHeight + view.height) + 15
        showAsDropDown(view, -xoff, -yoff, Gravity.START)
    }

    /**
     * 基于控件底部展示
     * @param view
     */
    fun showAtBottom1(view: View) {
        val xoff = abs(contentView.measuredWidth * 3 / 4 - view.width / 2)
        val yoff = abs(contentView.measuredHeight + view.height) + 15
        showAsDropDown(view, -xoff, -yoff, Gravity.CENTER)
    }

}