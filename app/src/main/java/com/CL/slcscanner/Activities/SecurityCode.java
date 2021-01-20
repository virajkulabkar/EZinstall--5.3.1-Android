package com.CL.slcscanner.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.CheckUniqueCode.CheckUniqueCode;
import com.CL.slcscanner.Pojo.CheckUniqueCode.Setting;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.Util;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vrajesh on 3/19/2018.
 */

public class SecurityCode extends AppCompatActivity {
    @BindView(R.id.ivBGSCode)
    ImageView ivBGSCode;

    @BindView(R.id.etCode)
    TextInputEditText etCode;

    @BindView(R.id.tvSecurityCode)
    TextView tvSecurityCode;

    @BindView(R.id.btSend)
    Button btSend;
    SharedPreferences spf,spf_login;
    API objApi;
    ProgressDialog progressSecurityCode;
    private static final int PERMISSION_ALL = 2;

    TelephonyManager telephonyManager;
    String deviceId;
    String langCode;


    private String[] PERMISSIONS = {
            android.Manifest.permission.READ_PHONE_STATE
    };

    boolean isPermissionGranted = false;
    boolean isRemeber = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_security_code);
        init();
    }

    void init() {

        LogDeviceinfo();

        ButterKnife.bind(this);
        Glide.with(this).load(R.drawable.securitycode_screen_bg).into(ivBGSCode);
        spf = getSharedPreferences(AppConstants.SPF, MODE_PRIVATE);
        spf_login = getSharedPreferences(AppConstants.SPF_LOGIN, MODE_PRIVATE);
        etCode.setText(spf.getString(AppConstants.UNIQUECODE,""));

        //this is for safety, if preference is not deleted
        spf.edit().clear().apply();

        langCode=Util.getDeviceLocale(spf);

        Log.i(AppConstants.TAG,"Security UI: "+langCode);

        objApi = new SLCScanner().networkCall();

        Util.hideKeyboard(SecurityCode.this);
        //Util.showKeyobard(SecurityCode.this);

        progressSecurityCode = new ProgressDialog(SecurityCode.this);
        progressSecurityCode.setMessage(getResources().getString(R.string.loading));
        progressSecurityCode.setCancelable(false);

        //Util.LocaleCheck(spf,SecurityCode.this);

        isRemeber = spf_login.getBoolean(AppConstants.IS_REMMEBER, true);
        if (isRemeber) {
            etCode.setText(spf_login.getString(AppConstants.SECURITY_CODE, ""));
        }

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPermissionGranted)
                    SendSecurityCode();
                else {
//                    Util.dialogForMessage(SecurityCode.this, "Permission not granted".toString());
                    ActivityCompat.requestPermissions(SecurityCode.this, PERMISSIONS, PERMISSION_ALL);
                }
            }
        });
        //btSend.setOnTouchListener(Util.colorFilter());

        etCode.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // TODO do something
                    handled = true;
                    if (isPermissionGranted)
                        SendSecurityCode();
                    else
                        Util.dialogForMessage(SecurityCode.this, getResources().getString(R.string.permission_error));
                }
                return handled;
            }
        });

        //telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                isPermissionGranted = false;
                return;
            }
            deviceId = getIMEINumber(SecurityCode.this);
            isPermissionGranted = true;
        } else {
            isPermissionGranted = true;
            deviceId = getIMEINumber(SecurityCode.this);
        //deviceId=telephonyManager.getDeviceId();
            spf.edit().putString(AppConstants.DEVICE_ID, deviceId).apply();
        }
        //new Util().setupLangReceiver(spf,getApplicationContext());
    }

    @SuppressLint("HardwareIds")
    public String getIMEINumber(@NonNull final Context context) {
        String imei;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert tm != null;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
                    return "";
                }
                imei = tm.getImei();
                //this change is for Android 10 as per security concern it will not provide the imei number.
                if (imei == null) {
                    imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                }
            } else {
                assert tm != null;
                if (tm.getDeviceId() != null && !tm.getDeviceId().equals("000000000000000")) {
                    imei = tm.getDeviceId();
                } else {
                    imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                }
            }
            UUID androidId_UUID = UUID.nameUUIDFromBytes(imei.getBytes("utf8"));
        } catch (Exception e) {
            imei = "";
        }

        spf.edit().putString(AppConstants.DEVICE_ID, imei).apply();
        return imei;
    }

    void LogDeviceinfo() {
        String deviceInfo = "";
        try {

            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int versionCode = pInfo.versionCode;

            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            int API = Build.VERSION.SDK_INT;
            String versionRelease = Build.VERSION.RELEASE;
            deviceInfo = " \n Manufacturer: " + manufacturer + "\n Model: " + model + "\n API :" + API + "\n OS Version: " + versionRelease + "\n App Version name: " + version + " \n App Version Code: " + versionCode;
            Util.addLog(deviceInfo);
        } catch (Exception e) {
            Util.addLog("***************** " + e.getMessage() + "********************");
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //deviceId = telephonyManager.getDeviceId();
                    deviceId=getIMEINumber(SecurityCode.this);
                    isPermissionGranted = true;

                    spf.edit().putString(AppConstants.DEVICE_ID, deviceId).apply();
                    Log.i(AppConstants.TAG, "DeviceID:" + deviceId);

                } else {
                    isPermissionGranted = false;
                    Util.dialogForPermisionMessage(SecurityCode.this, getResources().getString(R.string.permission));
                }
                return;
            }
        }
    }

    void SendSecurityCode() {

        if (etCode.getText().toString().trim().equals("")) {
            Util.dialogForMessage(SecurityCode.this, getResources().getString(R.string.please_enter_unique_code));
            return;
        }

        //API
        if (Util.isInternetAvailable(SecurityCode.this))
            sendUniqueCodeToServer(etCode.getText().toString(), deviceId);
        else
            Util.dialogForMessage(SecurityCode.this, getResources().getString(R.string.internent_connection));

        //Crashlytics.getInstance().crash(); // Force a crash for testing purpose

    }

    void sendUniqueCodeToServer(final String uniqueCode, String udid) {
        progressSecurityCode.show();

        final Call<CheckUniqueCode> objCheckUnieCall = objApi.checkUniqueCode(uniqueCode, udid, "Android", langCode);
        objCheckUnieCall.enqueue(new Callback<CheckUniqueCode>() {
            @Override
            public void onResponse(Call<CheckUniqueCode> call, Response<CheckUniqueCode> response) {

                if (progressSecurityCode.isShowing())
                    progressSecurityCode.dismiss();

                if (response.body() != null) {
                    CheckUniqueCode objCheckUniqueCode = response.body();
                    if (objCheckUniqueCode.getStatus().toString().equals("success")) {



                        SharedPreferences.Editor editor = spf.edit();
                        Log.i(AppConstants.TAG, objCheckUniqueCode.toString());
                        editor.putString(AppConstants.CLIENT_ID, objCheckUniqueCode.getClientID());
                        editor.putString(AppConstants.USER_ID, objCheckUniqueCode.getUserid());
                        editor.putString(AppConstants.UNIQUECODE, uniqueCode);
                        //editor.putString(AppConstants.LGVERSION,objCheckUniqueCode.getLGVersion());

                        editor.putString(AppConstants.MACID_LABLE, objCheckUniqueCode.getScanLBL());
                        editor.putString(AppConstants.MACID_PH, objCheckUniqueCode.getScanPH());

                        //UI visibility of POLE EDIT - DISPLAY , Camera preview,
                        Setting objSetting = response.body().getSetting();
                        editor.putString(AppConstants.SPF_CLIENT_SLC_EDIT_VIEW, objSetting.getClientSlcEditView());
                        editor.putString(AppConstants.SPF_CLIENT_SLC_LIST_VIEW, objSetting.getClientSlcListView());
                        editor.putString(AppConstants.SPF_CLIENT_SLC_POLE_IMAGE_VIEW, objSetting.getClientSlcPoleImageView());
                        editor.putString(AppConstants.SPF_CLIENT_SLC_POLE_ID, objSetting.getClientSlcPoleId());
                        editor.putString(AppConstants.SPF_CLIENT_SLC_POLE_ASSETS_VIEW, objSetting.getClientSlcPoleAssetsView());

                        editor.apply();
                        startActivity(new Intent(SecurityCode.this, LoginActivity.class));
                        finish();

                        spf_login.edit().putString(AppConstants.SECURITY_CODE, uniqueCode).apply();

                        if (objSetting.getClientSlcPoleId().equalsIgnoreCase("No")) {
                            spf.edit().putBoolean(AppConstants.SPF_POLE_ID_VISIBILITY, false).apply();
                        }else{
                            spf.edit().putBoolean(AppConstants.SPF_POLE_ID_VISIBILITY, true).apply();
                        }

                        if (objSetting.getClientSlcPoleAssetsView().equalsIgnoreCase("No")) {
                            spf.edit().putBoolean(AppConstants.SPF_OTHER_DATA_VISIBILITY, false).apply();
                        }else{
                            spf.edit().putBoolean(AppConstants.SPF_OTHER_DATA_VISIBILITY, true).apply();
                        }


                    } else if (objCheckUniqueCode.getStatus().toString().equals("error")) {
                        Util.dialogForMessage(SecurityCode.this, objCheckUniqueCode.getMsg().toString());
                    }
                }else{
                    Util.dialogForMessage(SecurityCode.this,getResources().getString(R.string.server_error));
                }
                btSend.setClickable(true);
                progressSecurityCode.dismiss();
            }

            @Override
            public void onFailure(Call<CheckUniqueCode> call, Throwable t) {
                progressSecurityCode.dismiss();
               //Util.dialogForMessage(SecurityCode.this, call.request().body().toString());
                Util.dialogForMessage(SecurityCode.this,t.getLocalizedMessage());
                btSend.setClickable(true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Util.LocaleCheck(spf,SecurityCode.this);
    }

    // override the base context of application to update default locale for this activity
    /*@Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }*/
}