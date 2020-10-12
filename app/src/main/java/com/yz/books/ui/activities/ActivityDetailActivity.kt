package com.yz.books.ui.activities

import android.content.res.AssetManager
import com.bumptech.glide.Glide
import com.yz.books.R
import com.yz.books.base.activity.BaseMVVMActivity
import com.yz.books.base.viewmodel.State
import com.yz.books.common.Constant
import com.yz.books.ext.dismissDialog
import com.yz.books.ext.startToActivity
import com.yz.books.ui.activities.bean.ActivityDetail
import com.yz.books.ui.h5.H5Activity
import com.yz.books.widget.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_read_journal_book.*
import kotlinx.android.synthetic.main.activity_read_journal_book.tv_title
import kotlinx.android.synthetic.main.item_activity.*
import me.jessyan.autosize.internal.CancelAdapt

class ActivityDetailActivity : BaseMVVMActivity<ActivitiesViewModel>(), CancelAdapt {

    private val mProgressDialog by lazy(LazyThreadSafetyMode.NONE) {
        LoadingDialog(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mProgressDialog.dismissDialog()
    }

    override fun getAssets(): AssetManager {
        return resources.assets
    }

    override fun providerVMClass() =
        ActivitiesViewModel()

    override fun getLayoutId() = R.layout.activity_activity_detail

    override fun initView() {
    }

    override fun initData() {
        val id = intent.getIntExtra("id", -1);
        mViewModel?.getActivityDetail(id)
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener { finish() }
    }

    override fun observerUI(state: State) {
        when (state) {
            is ActivitiesModel.ActivityDetailState -> {
                showActivityDetail(state.detail!!)
            }
        }
    }

    private fun showActivityDetail(detail: ActivityDetail) {
        tv_title.text = detail.title
        Glide.with(this).load(detail.imageUrl).into(iv_cover)
        tv_content.text = detail.content

        btn_live.setOnClickListener {
            startToActivity<H5Activity>(Constant.H5_URL_KEY_EXTRA to detail.videoUrl)
        }

    }

    override fun observerForever() = false

}