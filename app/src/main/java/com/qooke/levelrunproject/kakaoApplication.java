package com.qooke.levelrunproject;

import android.app.Application;
import android.util.Log;

import com.kakao.sdk.common.KakaoSdk;

public class kakaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Kakao SDK 초기화
        KakaoSdk.init(this, "00efd621fea14ab2bc93e3387728c3b9");
    }
}