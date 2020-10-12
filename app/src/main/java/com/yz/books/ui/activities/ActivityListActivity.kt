package com.yz.books.ui.activities

import android.content.res.AssetManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.yz.books.R
import com.yz.books.base.activity.BaseMVVMActivity
import com.yz.books.base.viewmodel.State
import com.yz.books.ext.dismissDialog
import com.yz.books.widget.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_activity_list.*
import kotlinx.android.synthetic.main.activity_read_journal_book.btn_back
import me.jessyan.autosize.internal.CancelAdapt

class ActivityListActivity : BaseMVVMActivity<ActivitiesViewModel>(), CancelAdapt {
    val adapter = ActivitiesAdapter()

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

    override fun getLayoutId() = R.layout.activity_activity_list

    override fun initView() {
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.adapter = adapter
    }

    override fun initData() {
        mViewModel?.getActivities()
    }

    override fun initListener() {
        super.initListener()
        btn_back.setOnClickListener { finish() }
    }

    override fun observerUI(state: State) {
        when (state) {
            is ActivitiesModel.ActivityListState -> {
                adapter.data.clear()
                if (state.list != null) {
                    adapter.data.addAll(state.list)
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun observerForever() = false

}