package com.qooke.levelrunproject.api;

import android.content.Context;

import com.qooke.levelrunproject.config.Config;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {

    public static Retrofit retrofit;
    public static Retrofit placeRetrofit;
    public static Retrofit weatherRetrofit;
    public static Retrofit translateRetrofit;
    public static Retrofit getRetrofitClient(Context context){
        if(retrofit == null){

            // 통신 로그 확인할 때 필요한 코드
            HttpLoggingInterceptor loggingInterceptor =
                    new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // 실제 사용할 때는 BASIC으로 사용(테스트용: BODY)

            // 네트워크 연결관련 코드
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES) // 보통 1분 정도 부여
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .addInterceptor(loggingInterceptor)
                    .build();

            // 네트워크로 데이터를 보내고 받는
            // 레트로핏 라이브러리 관련 코드
            retrofit = new Retrofit.Builder()
                    .baseUrl(Config.DOMAIN) // 서버의 도메인(aws 엔드포인트)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getPlaceRetrofitClient(Context context){
        if(placeRetrofit == null){

            // 통신 로그 확인할 때 필요한 코드
            HttpLoggingInterceptor loggingInterceptor =
                    new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC); // 실제 사용할 때는 BASIC으로 사용(테스트용: BODY)

            // 네트워크 연결관련 코드
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES) // 보통 1분 정도 부여
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .addInterceptor(loggingInterceptor)
                    .build();

            // 네트워크로 데이터를 보내고 받는
            // 레트로핏 라이브러리 관련 코드
            placeRetrofit = new Retrofit.Builder()
                    .baseUrl(Config.DOMAIN_GOOGLE) // 서버의 도메인(aws 엔드포인트)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return placeRetrofit;
    }

    public static Retrofit getWeatherRetrofitClient(Context context){
        if(weatherRetrofit == null){

            // 통신 로그 확인할 때 필요한 코드
            HttpLoggingInterceptor loggingInterceptor =
                    new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC); // 실제 사용할 때는 BASIC으로 사용(테스트용: BODY)

            // 네트워크 연결관련 코드
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES) // 보통 1분 정도 부여
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .addInterceptor(loggingInterceptor)
                    .build();

            // 네트워크로 데이터를 보내고 받는
            // 레트로핏 라이브러리 관련 코드
            weatherRetrofit = new Retrofit.Builder()
                    .baseUrl(Config.DOMAIN_WEATHER) // 서버의 도메인(aws 엔드포인트)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return weatherRetrofit;
    }

    public static Retrofit getTranslateRetrofitClient(Context context){
        if(translateRetrofit == null){

            // 통신 로그 확인할 때 필요한 코드
            HttpLoggingInterceptor loggingInterceptor =
                    new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC); // 실제 사용할 때는 BASIC으로 사용(테스트용: BODY)

            // 네트워크 연결관련 코드
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES) // 보통 1분 정도 부여
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .addInterceptor(loggingInterceptor)
                    .build();

            // 네트워크로 데이터를 보내고 받는
            // 레트로핏 라이브러리 관련 코드
            translateRetrofit = new Retrofit.Builder()
                    .baseUrl(Config.DOMAIN_PAPAGO) // 서버의 도메인(aws 엔드포인트)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return translateRetrofit;
    }
}
