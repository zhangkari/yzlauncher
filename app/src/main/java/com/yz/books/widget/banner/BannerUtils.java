package com.yz.books.widget.banner;

import android.content.Context;

/**
 * @author lilin
 * @time on 2019/7/31 下午8:25
 */
class BannerUtils {

    static int dip2px(Context ctx, float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
