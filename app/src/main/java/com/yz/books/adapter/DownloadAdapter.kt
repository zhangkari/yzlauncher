package com.yz.books.adapter

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseViewHolder
import com.yz.books.R
import com.yz.books.base.adapter.YZBaseAdapter
import com.yz.books.common.Constant
import com.yz.books.ext.addFileHostUrl
import com.yz.books.ext.showToast
import com.yz.books.ui.downloadlist.bean.DownloadResourceBean
import com.yz.books.ui.downloadlist.download.DownloadCallback
import com.yz.books.ui.downloadlist.download.DownloadFacade
import com.yz.books.utils.FileMD5Utils
import com.yz.books.utils.FileUtils
import com.yz.books.utils.ImageLoaderUtils
import com.yz.books.utils.LogUtils
import java.io.File

/**
 * @author lilin
 * @time on 2019-12-16 19:53
 */
class DownloadAdapter(val context: Context, dataList: MutableList<DownloadResourceBean.ResourceInfo>?) :
    YZBaseAdapter<DownloadResourceBean.ResourceInfo>(R.layout.adapter_item_download, dataList) {

    override fun handleViewData(helper: BaseViewHolder, item: DownloadResourceBean.ResourceInfo) {
        helper.convertView.isFocusable = true
        helper.itemView.isFocusable = true

        helper.addOnClickListener(R.id.iv_remove)
            .addOnClickListener(R.id.iv_status)

        val ivCover = helper.getView<ImageView>(R.id.iv_cover)
        val ivStatus = helper.getView<ImageView>(R.id.iv_status)
        val progressBar = helper.getView<ProgressBar>(R.id.progress_bar)

        progressBar.progress = item.progress
        LogUtils.e("==${item.completed}")
        helper.setGone(R.id.group_status, !item.completed)
        helper.setGone(R.id.tv_completed, item.completed)

        if (item.started) {
            ivStatus.setImageResource(R.drawable.ic_download_pause)
        } else {
            ivStatus.setImageResource(R.drawable.ic_download_start)
        }

        if (helper.layoutPosition % 2 == 0) {
            helper.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.cC4C2E9))
        } else {
            helper.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }

        helper.setText(R.id.tv_name, item.resourceName)
            .setText(R.id.tv_time, item.updateTime)

        //ImageLoaderUtils.withBookCover(item.coverImg.addFileHostUrl(), ivCover, ImageView.ScaleType.FIT_XY)

        /*ivStatus.setOnClickListener {
            if (ivStatus.tag == true) {
                ivStatus.tag = false
                DownloadFacade.getFacade().stopDownload(item.resourcePath.addFileHostUrl())
            } else {
                ivStatus.tag = true
                handleDownloadResource(item, helper.adapterPosition, progressBar)
            }
        }*/
    }

    private fun handleDownloadResource(data: DownloadResourceBean.ResourceInfo,
                                       position: Int,
                                       progressBar: ProgressBar) {
        data.apply {
            val path = "${FileUtils.getLocalPath()}$resourceId$resourceName"
            val file = File(path)
            if (file.exists() && file.listFiles().isNotEmpty()) {
                file.listFiles { dir, name ->
                    if (name in resourcePath &&
                        fileMd5String == FileMD5Utils.getFileMD5(File("$dir/$name"))) {
                    } else {
                        FileUtils.deleteFile("$dir/$name")
                        downloadResource(this, position, progressBar)
                    }
                    false
                }
            } else {
                downloadResource(this, position, progressBar)
            }
        }
    }

    private fun downloadResource(resourceInfo: DownloadResourceBean.ResourceInfo,
                                 position: Int,
                                 progressBar: ProgressBar) {
        resourceInfo.apply {
            if (resourcePath == null || type != "1") {
                return@apply
            }
            val activity = context as Activity

            val fileName = resourcePath.substring(resourcePath.lastIndexOf("/") + 1)
            val file = FileUtils.createAudioFile("$resourceId$resourceName", fileName)
            LogUtils.e("filepath==${file.path}")
            DownloadFacade.getFacade().startDownload(resourcePath.addFileHostUrl(), file.path,
                object : DownloadCallback {
                    override fun onSuccess(file: File?) {
                        val path: String? = file?.path
                        activity.runOnUiThread {
                            LogUtils.e("md5比较=${FileMD5Utils.getFileMD5(File(path))}//${fileMd5String}")
                            //mDownloading = false
                            path?.let {
                                if (FileMD5Utils.getFileMD5(File(it)) == fileMd5String) {
                                    completed = true
                                    notifyItemChanged(position)
                                    context.showToast("下载完成！")
                                } else {
                                    started = false
                                    notifyItemChanged(position)
                                    context.showToast("下载失败，请重新下载！")
                                    FileUtils.deleteFile(it)
                                }
                            }
                        }
                    }

                    override fun onFailure(e: Exception?) {
                        LogUtils.e("onDownloadFailed==${e?.message}")
                        activity.runOnUiThread {
                            //mDownloading = false
                            /*started = false
                            progress = 0
                            notifyItemChanged(position)*/
                            progressBar.progress = 0
                            context.showToast("下载失败，请重新下载！")
                        }
                    }

                    override fun onPause(progress: Long, currentLength: Long) {
                        LogUtils.e("onPause==$progress//$currentLength")
                        activity.runOnUiThread {
                            //mDownloading = false
                        }
                    }

                    override fun onProgress(progress: Long, currentLength: Long) {
                        //LogUtils.e("onDownloading==$progress//$currentLength")
                        activity.runOnUiThread {
                            val pro = progress * 100L / currentLength
                            LogUtils.e("onDownloading-pro==$pro//$resourceName")
                            //mDownloading = true
                            if (pro % 2 == 0L) {
                                progressBar.progress = pro.toInt()
                                //notifyItemChanged(position)
                            }
                        }
                    }
                })
        }
    }
}