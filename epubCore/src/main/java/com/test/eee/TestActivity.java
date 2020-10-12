package com.test.eee;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import rmkj.lib.epub_ggebook.R;
import rmkj.lib.read.RMReadController;
import rmkj.lib.read.itf.OnRMEPUBLoaderListener;
import rmkj.lib.read.view.RMEPUBView;

public class TestActivity extends Activity {
	protected RMEPUBView epubview;
	protected RMReadController container;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		epubview = (RMEPUBView) findViewById(R.id.curl);
//		container = new RMReadController(epubview, book.getInstallPath());
//		container.setOnJSClick(this);
//		container.setOnPageChaneListener(this);
//		container.setOnSpineChangedListener(this);
//		container.setOnSelectionListener(selectionListener);
//		container.loadEpubRzp(book.getInstallPath(), this);
		
		container = new RMReadController(epubview, "", "userid");
		/*
		 * container.loadEpubZip(getSDPath() + "/a.epub", new
		 * OnRMEPUBLoaderListener() {
		 * 
		 * @Override public void onLoadBookComplete() { // TODO Auto-generated
		 * method stub container.showSpine(0); } }); /bse.rzp
		 */
		container.loadEpubRzp(getSDPath() + "/a.epub", new OnRMEPUBLoaderListener() {
			@Override
			public void onLoadBookComplete() {
				// TODO Auto-generated method stub
				container.showSpine(0);
			}
		});
	}

	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();

	}
}
