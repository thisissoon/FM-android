package com.soon.fm.async;

public interface CallbackInterface<T> {

    void onSuccess(T obj);

    void onFail();

}
