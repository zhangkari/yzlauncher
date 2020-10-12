package com.yz.books.widget.banner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @author lilin
 * @time on 2019/7/31 下午8:22
 */
public abstract class ShapeHintView extends LinearLayout implements BaseHintView {

    private ImageView[] mDots;
    private int length = 0;
    private int lastPosition = 0;

    private Drawable dotNormal;
    private Drawable dotFocus;

    private int gap = 10;

    public ShapeHintView(Context context){
        super(context);
        gap = BannerUtils.dip2px(context, 8);
    }

    public ShapeHintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        gap = BannerUtils.dip2px(context, 8);
    }


    public abstract Drawable makeFocusDrawable();

    public abstract Drawable makeNormalDrawable();

    @Override
    public void initView(int length, int gravity) {
        removeAllViews();
        lastPosition = 0;
        setOrientation(HORIZONTAL);
        switch (gravity) {
            case 0:
                setGravity(Gravity.START| Gravity.CENTER_VERTICAL);
                break;
            case 1:
                setGravity(Gravity.CENTER);
                break;
            case 2:
                setGravity(Gravity.END| Gravity.CENTER_VERTICAL);
                break;
            default:
                break;
        }

        this.length = length;
        mDots = new ImageView[length];

        dotFocus = makeFocusDrawable();
        dotNormal = makeNormalDrawable();

        for (int i = 0; i < length; i++) {
            mDots[i]=new ImageView(getContext());
            LayoutParams dotLp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            dotLp.setMargins(gap, 0, gap, 0);
            mDots[i].setLayoutParams(dotLp);
            mDots[i].setBackgroundDrawable(dotNormal);
            addView(mDots[i]);
        }
        setCurrent(0);
    }

    @Override
    public void setCurrent(int current) {
        if (current < 0 || current > length - 1) {
            return;
        }
        if (length == 1) {
            GradientDrawable dotFocus = new GradientDrawable();
            dotFocus.setColor(Color.parseColor("#00000000"));
            mDots[lastPosition].setBackgroundDrawable(dotFocus);
            mDots[current].setBackgroundDrawable(dotFocus);
        } else {
            mDots[lastPosition].setBackgroundDrawable(dotNormal);
            mDots[current].setBackgroundDrawable(dotFocus);
        }
        lastPosition = current;
    }
}
