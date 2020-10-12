package com.yz.books.utils;


/**
 * @author lilin
 * @time on 2020/6/2 下午8:33
 */
public class SearchUtils {

    /**
     * 汉字转拼音缩写
     *
     * @param str
     *            要转换的汉字字符串
     * @return String 拼音缩写
     */
    public static String convertTo(String str) {
        StringBuilder tempStr = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            LogUtils.INSTANCE.e("convertTo=="+getPingYin(String.valueOf(c)));
            if (c >= 33 && c <= 126) {// 字母和符号原样保留
                tempStr.append(c);
            } else {// 累加拼音声母
                tempStr.append(getPingYin(String.valueOf(c)).charAt(0));
            }
        }
        return tempStr.toString();
    }

    private static String getPingYin(String inputString) {
        return "";//inputString == null ? "" : Pinyin.toPinyin(inputString, "").toLowerCase();
    }

}
