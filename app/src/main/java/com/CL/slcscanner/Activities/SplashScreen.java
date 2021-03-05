package com.CL.slcscanner.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.CL.slcscanner.R;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.OnClearFromRecentService;
import com.CL.slcscanner.Utils.Util;
import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vrajesh on 2/24/2018.
 */

public class SplashScreen extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1500;
    String uniqCode = "";
    String password = "";
    boolean isLoggedIn = false;
    boolean isAppKilled = true;
    SharedPreferences spf;

    @BindView(R.id.ivSplashScreenBg)
    ImageView ivSplashScreenBg;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splesh);
        Util.deletePreviewFile(SplashScreen.this);
        init();
    }

    void init() {
        Handler objHandler = new Handler();
        ButterKnife.bind(this);
        Glide.with(this).load(R.drawable.splash_screen_without_logo).into(ivSplashScreenBg);

        spf = getSharedPreferences(AppConstants.SPF, MODE_PRIVATE);
        uniqCode = spf.getString(AppConstants.UNIQUECODE, "");
        //password=spf.getString(AppConstants.PASSWORD,"");
        isLoggedIn = spf.getBoolean(AppConstants.ISLOGGEDIN, false);
        isAppKilled = spf.getBoolean(AppConstants.isAppKilled, true);
        try {
            startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        } catch (Exception e) {
        }

        new Util().setupLangReceiver(spf, getApplicationContext(), new SplashScreen());
        //Util.LocaleCheck(spf,SplashScreen.this);
        System.gc();

        objHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mIntent;
                if (uniqCode.toString().equals("")) {
                    mIntent = new Intent(SplashScreen.this, SecurityCode.class);
                } else if (!isLoggedIn) {
                    mIntent = new Intent(SplashScreen.this, LoginActivity.class);
                } else {
                    mIntent = new Intent(SplashScreen.this, MainActivity.class);
                }

                startActivity(mIntent);
                finish();
                //throw new RuntimeException("Test Crash"); // Force a crash
            }
        }, SPLASH_TIME_OUT);
        //fabricEvent();
    }

   /* void fabricEvent() {

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = getString(R.string.version) + pInfo.versionName;
            Answers.getInstance().logCustom(new CustomEvent("Device Info")
                    .putCustomAttribute(
                            "Device Info", Build.MANUFACTURER +
                                    " | " + Build.MODEL +
                                    " | OS " + Build.VERSION.RELEASE +
                                    " | API: " + Build.VERSION.SDK_INT
                                    + " | " + version)
            );

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // TODO: Use your own string attributes to track common values over time
        // TODO: Use your own number attributes to track median value over time

    }*/

    // override the base context of application to update default locale for this activity
   /* @Override
    protected void attachBaseContext(Context base) {
        // super.attachBaseContext(LanguageHelper.onAttach(base));
        //super.attachBaseContext(LanguageHelper.setApplicationLanguage(base));
        super.attachBaseContext(LanguageHelper.wrap(base, Util.getDeviceLocale(spf)));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            super.attachBaseContext(LanguageHelper.onAttach(base, LanguageHelper.getLanguage(base)));
        } else
            super.attachBaseContext(LanguageHelper.onAttach(base));
    }*/
}