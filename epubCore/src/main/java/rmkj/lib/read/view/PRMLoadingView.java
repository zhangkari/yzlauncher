package rmkj.lib.read.view;

import android.content.Context;

/**
 * 进行耗时操作弹出的正在加载框
 * 
 * @author zsx
 * 
 */
public class PRMLoadingView {
	private ProgressHUD dialog;

	public PRMLoadingView(Context context) {
//		dialog = new ProgressDialog(context);
//		dialog.setCancelable(false);
//		dialog.setCanceledOnTouchOutside(false);
//		dialog.setMessage("正在加载...");
		dialog = ProgressHUD.show(context,"加载中...", true,true,null);
	}

	public void show() {
		if (dialog.isShowing()) {
			return;
		}
		dialog.show();
	}

	public void cancel() {
		if (dialog != null && dialog.isShowing()) {
			dialog.cancel();
		}
	}
}
