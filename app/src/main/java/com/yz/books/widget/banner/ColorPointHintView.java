package com.yz.books.widget.banner;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

/**
 * @author lilin
 * @time on 2019/7/31 下午8:22
 */
public class ColorPointHintView extends ShapeHintView {

    private int focusColor;
    private int normalColor;
    private int radius;

    public ColorPointHintView(Context context, int focusColor, int normalColor, int radius) {
        super(context);
        this.focusColor = focusColor;
        this.normalColor = normalColor;
        this.radius = radius;
    }

    @Override
    public Drawable makeFocusDrawable() {
        //return getContext().getResources().getDrawable(R.drawable.img_point_selected);
        GradientDrawable dotFocus = new GradientDrawable();
        dotFocus.setColor(focusColor);
        dotFocus.setCornerRadius(BannerUtils.dip2px(getContext(), radius));
        dotFocus.setSize(BannerUtils.dip2px(getContext(), 2*radius),
                BannerUtils.dip2px(getContext(), 2*radius));
        return dotFocus;
    }

    @Override
    public Drawable makeNormalDrawable() {
        //return getContext().getResources().getDrawable(R.drawable.img_point_normal);
        GradientDrawable dotNormal = new GradientDrawable();
        dotNormal.setColor(normalColor);
        dotNormal.setCornerRadius(BannerUtils.dip2px(getContext(), radius));
        dotNormal.setSize(BannerUtils.dip2px(getContext(), 2*radius),
                BannerUtils.dip2px(getContext(), 2*radius));
        return dotNormal;
    }

}
