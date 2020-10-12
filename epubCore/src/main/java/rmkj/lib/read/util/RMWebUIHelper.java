package rmkj.lib.read.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class RMWebUIHelper {

	public static int dip2px(Context context, float dipValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(pxValue / scale + 0.5f);
	}

	public static int getScreenWidthDip(Context context){
		int widthPx = RMWebUIHelper.getScreenWidth(context);
		int widthDip = RMWebUIHelper.px2dip(context, widthPx);
		return widthDip;
	}

	public static int getScreenHeightDip(Context context){
		int heightPx = RMWebUIHelper.getScreenHeight(context);
		int heightDip = RMWebUIHelper.px2dip(context, heightPx);
		return heightDip;
	}

	/**
	 * 获取设备密度
	 * 
	 * @param context
	 * @return
	 */
	public static float getDensity(Context context) {
		if (context != null) {
			return context.getResources().getDisplayMetrics().density;
		} else {
			return 1.0f;
		}
	}

	/**
	 * 获取设备密度DPI
	 * 
	 * @param context
	 * @return
	 */
	public static int getDensityDpi(Context context) {
		if (context != null) {
			return context.getResources().getDisplayMetrics().densityDpi;
		} else {
			return 120;
		}
	}

	/**
	 * 获取屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		if (context != null) {
			return context.getResources().getDisplayMetrics().widthPixels;
		} else {
			return 0;
		}
	}	

	/**
	 * 获取屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		if (context != null) {
			return context.getResources().getDisplayMetrics().heightPixels;
		} else {
			return 0;
		}
	}
	
	
	/**
	 * 获取实际网页位置
	 * @param val
	 * @param ctx
	 * @return
	 */
	public static float getDensityIndependentValue(float val, Context ctx) {

		// Get display from context
		Display display = ((WindowManager) ctx
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);

		return val / (metrics.densityDpi / 160f);

	}

}
