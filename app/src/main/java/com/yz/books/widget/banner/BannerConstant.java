package com.yz.books.widget.banner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author lilin
 * @time on 2019/7/31 下午9:08
 */
public class BannerConstant {

    /**
     * 不建议用枚举，可以用注解替代
     * 轮播图红点是0，数字是1
     * 后期还可以加入其他的
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface HintMode {
        int COLOR_POINT_HINT = 0;
        int TEXT_HINT = 1;
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface HintGravity {
        int LEFT = 1;
        int CENTER = 2;
        int RIGHT = 3;
    }
}
