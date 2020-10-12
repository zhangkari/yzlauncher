package com.yz.books.webview;

/**
 * @author lilin
 * @time on 2020-01-12 15:48
 */
public interface  CompletionHandler<T> {
    void complete(T retValue);
    void complete();
    void setProgressData(T value);
}
