package com.yz.books.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lilin
 * @time on 2020/6/4 下午9:23
 */
public class HtmlUtils {
    private final static Pattern ATTR_PATTERN = Pattern.compile("<img[^<>]*?\\ssrc=['\"]?(.*?)['\"]?\\s.*?>",Pattern.CASE_INSENSITIVE);

    //替换body里面的图片路径问题
    public static String getAbsSource(String source, String bigpath) {
        Matcher matcher = ATTR_PATTERN.matcher(source);
        List<String> list = new ArrayList<String>();  // 装载了匹配整个的Tag
        List<String> list2 = new ArrayList<String>(); // 装载了src属性的内容
        while (matcher.find()) {
            list.add(matcher.group(0));
            list2.add(matcher.group(1));
        }
        StringBuilder sb = new StringBuilder();
        sb.append(source.split("<img")[0]); // 连接<img之前的内容
        for (int i = 0; i < list.size(); i++) { // 遍历list
            sb.append(list.get(i).replace(list2.get(i), // 对每一个Tag进行替换
                    bigpath + list2.get(i).substring(1)));
        }
        String[] str = source.split("(?:<img[^<>]*?\\s.*?['\"]?\\s.*?>)+");
        if (str.length > 1) {
            sb.append(str[1]);
        }
        return sb.toString();
    }
}
