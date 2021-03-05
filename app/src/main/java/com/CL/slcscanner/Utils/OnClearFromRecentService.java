package com.CL.slcscanner.Utils;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.security.spec.ECField;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OnClearFromRecentService extends Service {

    SharedPreferences spf;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(AppConstants.TAG, "Service Started");
        spf=getApplicationContext().getSharedPreferences(AppConstants.SPF,MODE_PRIVATE);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //just for safety, if onTaskRemoved not work then this help us
        try {
            spf.edit().putBoolean(AppConstants.isAppKilled, true).apply();
            clearSharedPref();
            Log.d(AppConstants.TAG, "Service Destroyed");
            stopSelf();
        }catch (Exception e){}
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try {
            Log.e(AppConstants.TAG, "END");

            SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            long timeDate = System.currentTimeMillis();
            String dfWithInterval = df.format(new Date(timeDate));
            Log.i(AppConstants.TAG, "( service) TimeDate:" + dfWithInterval);
            Util.addLog("App Deactivate at:: " + timeDate);
            if (spf != null) {
                spf.edit().putBoolean(AppConstants.isAppKilled, true).apply();
                spf.edit().putLong(AppConstants.SPF_DEACTIVE_TIME, timeDate).apply();
            }

            Util.deletePreviewFile(this);
            clearSharedPref();
            stopSelf();
        }catch (Exception e){}
    }

    public void clearSharedPref(){
        SharedPreferences.Editor edt = spf.edit();

        edt.putString(AppConstants.SPF_TEMP_SLCID, "");
        edt.putString(AppConstants.SPF_TEMP_MACID, "");
        edt.putString(AppConstants.SPF_TEMP_POLE_ID, "");
        edt.putString(AppConstants.SPF_DRAG_LONGITUDE, "0.0");
        edt.putString(AppConstants.SPF_DRAG_LATTITUDE, "0.0");

        edt.putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, "");
        edt.putString(AppConstants.SPF_POLE_CURRENT_FRAG,"");

        edt.putString("search_text", "");
        edt.putBoolean(AppConstants.SPF_ISFROMMAP,false);
        edt.putString(AppConstants.SPF_ID,"");
        edt.putString(AppConstants.SPF_POLE_DISPLAY_LAT, "0.0");
        edt.putString(AppConstants.SPF_POLE_DISPLAY_Long, "0.0");
        edt.putString(AppConstants.ADDRESS, "");
        edt.putString(AppConstants.SPF_LOGOUT_SLCID,"");
        edt.putBoolean(AppConstants.CopyFromPrevious,false);
        edt.putString(AppConstants.SPF_TEMP_POLE_ID_COPY_FEATURE, "");
        edt.remove(AppConstants.isNoneChecked);
        edt.remove(AppConstants.LOCATION_ACCURACY);
        edt.remove(AppConstants.SATELLITE_COUNTS);

        edt.apply();
    }
}