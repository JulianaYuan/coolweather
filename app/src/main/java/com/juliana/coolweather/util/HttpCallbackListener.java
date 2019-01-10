package com.juliana.coolweather.util;

/**
 * Created by JYUAN7 on 1/9/2019.
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
