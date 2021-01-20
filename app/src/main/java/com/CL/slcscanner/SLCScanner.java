package com.CL.slcscanner;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.LanguageHelper;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/**
 * Created by vrajesh on 3/9/2018.
 */

public class SLCScanner extends Application {

    public SLCScanner() {}

    public API networkCall() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

        if (AppConstants.isLogDisplay)
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        else
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.connectTimeout(180, TimeUnit.SECONDS);
        httpClient.readTimeout(180, TimeUnit.SECONDS);
        httpClient.addInterceptor(interceptor);

        Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(AppConstants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.client(httpClient.build())
                .build();

        return retrofit.create(API.class);
    }


    // override the base context of application to update default locale for the application
   /* @Override
    protected void attachBaseContext(Context base) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            super.attachBaseContext(LanguageHelper.onAttach(base, LanguageHelper.getLanguage(base)));
        }else
            super.attachBaseContext(LanguageHelper.onAttach(base));

        //super.attachBaseContext(LanguageHelper.setApplicationLanguage(base));
    }*/

}