package com.soon.fm;

public interface OnTaskCompleted {

    void onSuccess(Object accessToken);

    void onFailed();
}
