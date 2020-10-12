package com.yz.books.ui.activities

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yz.books.R
import com.yz.books.common.Constant
import com.yz.books.ext.startToActivity
import com.yz.books.ui.activities.bean.ActivityItem
import com.yz.books.ui.h5.H5Activity

class ActivitiesAdapter : RecyclerView.Adapter<VHolder>() {
    var data: MutableList<ActivityItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
        val view = View.inflate(parent.context, R.layout.item_activity, null)
        return VHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: VHolder, position: Int) {
        holder.itemView.isFocusable = true
        (holder.itemView as ViewGroup).descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS

        holder.setText(R.id.tv_title, data[position].title)
        holder.setText(R.id.tv_content, data[position].content)
        Glide.with(holder.itemView).load(data[position].imageUrl).into(holder.ivCover)

        holder.ivCover.setOnClickListener {
            (it.context as Activity).startToActivity<ActivityDetailActivity>(
                "id" to data[position].id

            )
        }

        holder.btnLivePlay.setOnClickListener {
            (it.context as Activity).startToActivity<H5Activity>(
                Constant.H5_URL_KEY_EXTRA to data[position].videoUrl
            )
        }
    }

}

class VHolder(view: View) : RecyclerView.ViewHolder(view) {
    var ivCover: ImageView = view.findViewById(R.id.iv_cover)
    var btnLivePlay: View = view.findViewById(R.id.btn_live)
    fun setText(id: Int, text: String) {
        itemView.findViewById<TextView>(id).text = text
    }
}