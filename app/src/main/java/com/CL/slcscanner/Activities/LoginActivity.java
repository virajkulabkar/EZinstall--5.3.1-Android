package com.CL.slcscanner.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.CheckUniqueCode.CheckUniqueCode;
import com.CL.slcscanner.Pojo.CommonResponse;
import com.CL.slcscanner.Pojo.Login.Datum;
import com.CL.slcscanner.Pojo.Login.LoginResponse;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.FingerprintHandler;
import com.CL.slcscanner.Utils.GPS.FusedLocationUtils;
import com.CL.slcscanner.Utils.GPS.GPSTracker;
/*import com.CL.slcscanner.Utils.MyCallbackForMapUtility;
import com.CL.slcscanner.Utils.MyCallbackForMapUtilityLocation;*/
import com.CL.slcscanner.Utils.Util;
import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity implements FingerprintHandler.OnSuccessAPI, FusedLocationUtils.UpdateUI { //MyCallbackForMapUtility, MyCallbackForMapUtilityLocation,

    SharedPreferences spf;
    private AlertDialog dilog;

    @BindView(R.id.ivLogin)
    Button ivLogin;
    @BindView(R.id.edtUsername)
    EditText edtUsername;
    @BindView(R.id.edtPassword)
    EditText edtPassword;
    SLCScanner objSlcScanner;

    @BindView(R.id.ivBack)
    ImageView ivBack;

    @BindView(R.id.llFaceFingurePrint)
    LinearLayout llFaceFingurePrint;

    @BindView(R.id.cbRemberMe)
    CheckBox cbRemberMe;

    ProgressBar progressSecurityCode;
    AlertDialog objUniqueCodeDialog;
    TextView tvError;
    Button btSend;
    API objApi;
    ProgressDialog progressDialog;
    Double lattitude, longitude;
    ProgressDialog dialogForLatlong;
    boolean isAllPermissionGranted;
    SharedPreferences spf_login, spfLogin;


    private FirebaseAnalytics mFirebaseAnalytics;
    private String[] PERMISSIONS = {
            android.Manifest.permission.READ_PHONE_STATE,

            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,

            android.Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,

            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,

    };
    private GPSTracker gps;
    private static final int PERMISSION_ALL = 1;
    String uniqueCode;

    boolean isRemember = false;
    FingerprintManager fingerprintManager;
    KeyguardManager keyguardManager;

    private KeyStore keyStore;
    // Variable used for storing the key in the Android Keystore container
    private static final String KEY_NAME = "androidLG";
    private Cipher cipher;

    String temp_pass;

    FusedLocationUtils objFusedLocationUtils;
    String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    void init() {

        progressDialog = new ProgressDialog(LoginActivity.this);
        objApi = new SLCScanner().networkCall();
        Util.hideKeyboard(LoginActivity.this);

        spf = getSharedPreferences(AppConstants.SPF, MODE_PRIVATE);
        spf_login = getSharedPreferences(AppConstants.SPF_LOGIN, MODE_PRIVATE);
        spfLogin = getSharedPreferences(AppConstants.SPF_LOGIN, MODE_PRIVATE);
        ButterKnife.bind(this);
        reset();

        objFusedLocationUtils = new FusedLocationUtils(LoginActivity.this, this, null, false);

        temp_pass = spf_login.getString(AppConstants.TEMP_PASS, "");
        objUniqueCodeDialog = uniqueCodeDialog(LoginActivity.this);
        cbRemberMe.setText(Html.fromHtml(getResources().getString(R.string.remember)));
        uniqueCode = spf.getString(AppConstants.UNIQUECODE, "");

        dialogForLatlong = new ProgressDialog(LoginActivity.this);
        dialogForLatlong.setMessage(getResources().getString(R.string.please_wait));
        dialogForLatlong.setCancelable(false);

        isRemember = spfLogin.getBoolean(AppConstants.IS_REMMEBER, true);
        if (isRemember) {
            edtUsername.setText(spfLogin.getString(AppConstants.USERNAME, "").toString());
            cbRemberMe.setChecked(true);
        }

        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            request6plus();
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        } else {
            request6less();
            fingerprintManager = null;
        }

        //edtUsername.setText("viraj.kulabkar1@cimconlighting.com");
        //edtPassword.setText("cimcon");

        ivLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid(edtUsername, edtPassword)) {
                    //if (isAllPermissionGranted)
                    if (hasPermissions(LoginActivity.this, PERMISSIONS)) {
                        if (Util.isInternetAvailable(LoginActivity.this))
                            sendLoginDetailToSever(edtUsername.getText().toString(), edtPassword.getText().toString(), lattitude, longitude, false);
                        else
                            Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.no_internet_connection));
                    } else {
                        ActivityCompat.requestPermissions(LoginActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }
                }
                //startActivity(new Intent(LoginActivity.this,TempActivity.class));
            }
        });

        //ivLogin.setOnTouchListener(Util.colorFilter());

        edtPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // TODO do something
                    handled = true;
                    if (hasPermissions(LoginActivity.this, PERMISSIONS))
                        if (Util.isInternetAvailable(LoginActivity.this))
                            sendLoginDetailToSever(edtUsername.getText().toString(), edtPassword.getText().toString(), lattitude, longitude, false);
                        else
                            Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.no_internet_connection));

                    else {
                        ActivityCompat.requestPermissions(LoginActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }
                }
                return handled;
            }
        });

        try {
            lattitude = Double.valueOf(spf.getString(AppConstants.LATTITUDE, "0.0"));
            longitude = Double.valueOf(spf.getString(AppConstants.LONGITUDE, "0.0"));
        } catch (Exception e) {
            Util.addLog("Crash " + e.getMessage());
        }

        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, "").apply();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SecurityCode.class));
                finish();
            }
        });

        llFaceFingurePrint.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                //boolean isFaceTouchAllow = spf_login.getBoolean(AppConstants.ALLOW_FACE_TOUCH_ID, false);
                spf_login.edit().putBoolean(AppConstants.IS_FINGURE_TOUCH_PRESSED, true).apply();
                checkIsPasswordExist();
                //dialogOption();
                //Toast.makeText(LoginActivity.this, "Please enter login first then able to activate this feature", Toast.LENGTH_LONG).show();
                //}
            }
        });

        cbRemberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor edtEditor = spfLogin.edit();
                if (b) {
                    edtEditor.putBoolean(AppConstants.IS_REMMEBER, true);
                    edtEditor.apply();
                } else {
                    edtEditor.putBoolean(AppConstants.IS_REMMEBER, false);
                    edtEditor.remove(AppConstants.USERNAME);
                    edtEditor.apply();
                }
            }
        });

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version="";
        }
        //version="5.3.0";

        spf.edit().putString(AppConstants.VERSION,version).commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void checkIsPasswordExist() {
        temp_pass = spf_login.getString(AppConstants.TEMP_PASS, "");
        if (edtUsername.getText().toString().equalsIgnoreCase("")) {
            Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.empty_username));
        } else if (temp_pass.toString().equals("")) {
            if (edtPassword.getText().toString().equals("")) {
                Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.empty_password));
            } else {
                if (Util.isInternetAvailable(LoginActivity.this)) {
                    /*sendLoginDetailToSever(
                            edtUsername.getText().toString(),
                            edtPassword.getText().toString(),
                            lattitude,
                            longitude,
                            true);*/
                    spf.edit().putString(AppConstants.TEMP_PASS,edtPassword.getText().toString()).apply();

                    temp_pass=edtPassword.getText().toString();
                    if (checkFingurePrint())
                        dialogOptionTouch();
                } else
                    Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.no_internet_connection));

            }
            //Utils.dialogForMessage(LoginActivity.this, getResources().getString(R.string.empty_password));
        } else {
            //dialogOption();
            if (checkFingurePrint())
                dialogOptionTouch();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void checkIsPasswordExist2() {
        String temp_pass = spf_login.getString(AppConstants.TEMP_PASS, "");
        if (edtUsername.getText().toString().equalsIgnoreCase("")) {
            Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.empty_username));
        } else if (temp_pass.toString().equals("")) {
            if (edtPassword.getText().toString().equals("")) {
                Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.empty_password));
            } else {
                if (Util.isInternetAvailable(LoginActivity.this))
                    if (checkFingurePrint())
                        dialogOptionTouch();
                    else
                        Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.no_internet_connection));

            }
            //Utils.dialogForMessage(LoginActivity.this, getResources().getString(R.string.empty_password));
        } else {
            //dialogOption();
            if (checkFingurePrint())
                dialogOptionTouch();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void dialogOptionTouch() {

        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(R.layout.custom_dialog_touch_id);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView tvTouchIDAgain = dialog.findViewById(R.id.tvTouchIDAgain);
        TextView tvUserName = dialog.findViewById(R.id.tvUserName);
        ImageView ivFingurePrint = dialog.findViewById(R.id.ivFingurePrint);

        Glide.with(this).load(R.drawable.fingure_print).into(ivFingurePrint);

        tvTouchIDAgain.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.put_figure_on_hw), Toast.LENGTH_LONG).show();
                fingurePrintOperation();

            }
        });

        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        fingurePrintOperation();
        Rect displayRectangle = new Rect();
        Window window = LoginActivity.this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        dialog.getWindow().setLayout((int) (displayRectangle.width() * 0.9f), (int) (displayRectangle.height() * 0.8f));
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
            window.setDimAmount(0.5f); //0 for no dim to 1 for full dim
        }
        dialog.show();
        // getMapData(mClusterTest, date, type,googleMap12);

    }

    private void request6plus() {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            return;
        } else {
            //if (uniqueCode.toString().equals(""))
            //objUniqueCodeDialog.show();

            //getLocation();
            objFusedLocationUtils.buildGoogleApiClient();
            //String DEVICE_ID = getDeviceId(LoginActivity.this);
            //spf.edit().putString(AppConstants.DEVICE_ID, DEVICE_ID).apply();
        }
    }

    private void request6less() {
        //if (uniqueCode.toString().equals(""))
        //   objUniqueCodeDialog.show();

        objFusedLocationUtils.buildGoogleApiClient();
        //getLocation();
        //String DEVICE_ID = getDeviceId(LoginActivity.this);
        //spf.edit().putString(AppConstants.DEVICE_ID, DEVICE_ID).apply();
        //log.alert(HomeActivity.this, "5-" + DEVICE_ID).show();
    }

    /*private String getDeviceId(Activity activity) {
        TelephonyManager telephonyManager = (TelephonyManager) activity
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }*/

    void reset() {
        SharedPreferences.Editor edt = spf.edit();
        edt.putString(AppConstants.SPF_TEMP_SLCID, "");
        edt.putString(AppConstants.SPF_TEMP_MACID, "");
        edt.putString(AppConstants.SPF_TEMP_POLE_ID, "");

        edt.putString(AppConstants.SPF_DRAG_LONGITUDE, "0.0");
        edt.putString(AppConstants.SPF_DRAG_LATTITUDE, "0.0");

        edt.putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, "");
        edt.putString(AppConstants.SPF_POLE_CURRENT_FRAG, "");

        edt.putString("search_text", "");
        edt.putBoolean(AppConstants.SPF_ISFROMMAP, false);
        edt.putString(AppConstants.SPF_ID, "");
        edt.putString(AppConstants.SPF_POLE_DISPLAY_LAT, "0.0");
        edt.putString(AppConstants.SPF_POLE_DISPLAY_Long, "0.0");
        edt.remove(AppConstants.SPF_DEACTIVE_TIME);
        edt.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
        objFusedLocationUtils.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        objFusedLocationUtils.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        spf_login.edit().remove(AppConstants.IS_FINGURE_TOUCH_PRESSED).apply();
    }

/*    private void getLocation() {
        gps = new GPSTracker(LoginActivity.this, this, this);
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Log.i(AppConstants.TAG, latitude + " | " + longitude);

            if (latitude == 0.0 || longitude == 0.0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getLocation();//recursion
                    }
                }, 2000);

            } else {
                if (dialogForLatlong.isShowing())
                    dialogForLatlong.dismiss();
                spf.edit().putString(AppConstants.LATTITUDE, String.valueOf(latitude)).apply();
                spf.edit().putString(AppConstants.LONGITUDE, String.valueOf(longitude)).apply();
                Util.addLog("Current Location:  Lat:" + latitude + " Long:" + longitude);
            }
            //log.alert(HomeActivity.this, "onLocationChanged ->" + LATITUDE + "--" + LONGITUDE).show();
        } else {
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert(false);
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.

                int grandP = 0;
                int denyP = 0;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults.length > 0
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        grandP++;
                    } else {
                        denyP++;
                    }
                }

                if (grandP == permissions.length) {
                    objFusedLocationUtils.buildGoogleApiClient();
                    //getLocation();
                    //String DEVICE_ID = getDeviceId(LoginActivity.this);
                    //spf.edit().putString(AppConstants.DEVICE_ID, DEVICE_ID).apply();
                    //Log.i(AppConstants.TAG, "DeviceID:" + DEVICE_ID);
                    isAllPermissionGranted = true;
                } else {
                    isAllPermissionGranted = false;
                    Util.dialogForPermisionMessage(LoginActivity.this, getResources().getString(R.string.permission));
                }
                return;
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    Boolean isValid(EditText edUsername, EditText edPassword) {
        boolean result = true;
        if (Util.isInternetAvailable(LoginActivity.this)) {
            if (edUsername.getText().toString().equals("")) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.empty_username), Toast.LENGTH_SHORT).show();
                //edUsername.setError(getResources().getString(R.string.empty_username));
                result = false;
            } else if (edPassword.getText().toString().equals("")) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.empty_password), Toast.LENGTH_SHORT).show();
                //edPassword.setError(getResources().getString(R.string.empty_password));
                result = false;
            }
        } else {
            //Toast.makeText(LoginActivity.this, getResources().getString(R.string.internent_connection), Toast.LENGTH_SHORT).show();
            Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.internent_connection));
            result = false;
        }
        return result;
    }

    private AlertDialog uniqueCodeDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dilog_secret_code, null);
        final EditText etCode = (EditText) view.findViewById(R.id.etCode);
        etCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});

        //etCode.setText("441293");
        etCode.setText(getResources().getString(R.string.security_code));

        btSend = (Button) view.findViewById(R.id.btSend);
        progressSecurityCode = (ProgressBar) view.findViewById(R.id.progressBar);
        //tvError = (TextView) view.findViewById(R.id.tvError);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendSecurityCode(etCode);
            }
        });

        etCode.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // TODO do something
                    handled = true;
                    SendSecurityCode(etCode);

                }
                return handled;
            }
        });
        builder.setCancelable(false);
        builder.setView(view);
        return builder.create();
    }

    void SendSecurityCode(EditText etCode) {
        if (etCode.getText().toString().equals("")) {
            //Toast.makeText(LoginActivity.this, "Please enter unique code.", Toast.LENGTH_SHORT).show();
            Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.please_enter_unique_code));
            return;
        }
        btSend.setClickable(false);
        progressSecurityCode.setVisibility(View.VISIBLE);

        //API
        if (Util.isInternetAvailable(LoginActivity.this)) {
            sendUniqueCodeToServer(etCode.getText().toString(), spf.getString(AppConstants.DEVICE_ID,""), "");
        } else {
            Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.no_internet_connection));
        }
    }

    void sendUniqueCodeToServer(final String uniqueCode, String udid, String langCode) {
        Call<CheckUniqueCode> objCheckUnieCall = objApi.checkUniqueCode(uniqueCode, udid, "Android", langCode);
        objCheckUnieCall.enqueue(new Callback<CheckUniqueCode>() {
            @Override
            public void onResponse(Call<CheckUniqueCode> call, Response<CheckUniqueCode> response) {
                progressSecurityCode.setVisibility(View.GONE);
                if (response.body() != null) {
                    CheckUniqueCode objCheckUniqueCode = response.body();
                    if (objCheckUniqueCode.getStatus().toString().equals("success")) {
                        SharedPreferences.Editor editor = spf.edit();
                        Log.i(AppConstants.TAG, objCheckUniqueCode.toString());
                        editor.putString(AppConstants.CLIENT_ID, objCheckUniqueCode.getClientID());
                        editor.putString(AppConstants.USER_ID, objCheckUniqueCode.getUserid());
                        editor.putString(AppConstants.UNIQUECODE, uniqueCode);
                        editor.apply();
                        objUniqueCodeDialog.dismiss();
                    } else if (objCheckUniqueCode.getStatus().toString().equals("error")) {
                        Util.dialogForMessage(LoginActivity.this, objCheckUniqueCode.getMsg().toString());
                    }
                }
                btSend.setClickable(true);
            }

            @Override
            public void onFailure(Call<CheckUniqueCode> call, Throwable t) {
                progressSecurityCode.setVisibility(View.GONE);
                Util.dialogForMessage(LoginActivity.this, t.getLocalizedMessage());
                btSend.setClickable(true);
            }
        });
    }

    void sendLoginDetailToSever(String username, String password, Double lattitude, Double longitude, final boolean isTocuhId) {
        String cleintid = spf.getString(AppConstants.CLIENT_ID, null);
        String userId = spf.getString(AppConstants.USER_ID, null);
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        //ivLogin.setEnabled(false);

        Call<LoginResponse> objCheckUnieCall = objApi.checkUserLogin(
                cleintid,
                userId,
                username,
                password,
                lattitude,
                longitude,
                "Android",
                version);

        objCheckUnieCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse objCommonResponse = response.body();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (response.body() != null) {
                    if (objCommonResponse.getStatus().toString().equalsIgnoreCase("success")) {
                        //spf.edit().putString(AppConstants.PASSWORD,edtPassword.getText().toString()).apply();
                        spf.edit().putBoolean(AppConstants.ISLOGGEDIN, true).apply();
                        spf.edit().putBoolean(AppConstants.ISLOGGEDIN, true).commit();

                        spf_login.edit().putString(AppConstants.TEMP_PASS, password).commit();
                        spfLogin.edit().putString(AppConstants.USERNAME, username).commit();


                        //node type
                        /*List<Datum> objData = objCommonResponse.getNodeTypeLg().getData();

                        List<Datum> objList = new ArrayList<>();
                        Datum objDatum = new Datum();
                        objDatum.setClientType(getString(R.string.status_all));
                        objDatum.setValue("");
                        objList.add(objDatum);

                        Datum objDatum1 = new Datum();
                        objDatum1.setClientType(getString(R.string.unknown));
                        objDatum1.setValue("");
                        objList.add(objDatum1);

                        objList.addAll(objData);

                        Util.SaveClientTypeList(spf, objList);*/

                        if (isTocuhId) {
                            spf_login.edit().putBoolean(AppConstants.IS_REMMEBER, true).apply();
                        }
                        temp_pass = password;

                        ivLogin.setEnabled(true);
                        //Toast.makeText(LoginActivity.this, objCommonResponse.getMsg().toString(), Toast.LENGTH_SHORT).show();
                        Util.hideKeyboard(LoginActivity.this);
                        Util.addLog("Login Successful");

                        boolean isLangSelected = spf.getBoolean(AppConstants.LANGUAGE_SELECTED, false);
                        String langCode = Util.getDeviceLocale(spf);

                        if (!isLangSelected) {
                            new Util().switchLanguage(LoginActivity.this, langCode, false);
                            Log.i(AppConstants.TAG, "switchLang called");
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    } else if (objCommonResponse.getStatus().toString().equalsIgnoreCase("error")) {
                        ivLogin.setEnabled(true);
                        Util.dialogForMessage(LoginActivity.this, objCommonResponse.getMsg().toString());
                    } else if (objCommonResponse.getStatus().toString().equalsIgnoreCase("-1")) {
                        ivLogin.setEnabled(true);

                        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        //Util.dialogForMessage(LoginActivity.this, objCommonResponse.getMsg().toString());
                        Util.dialogForMessagePlayStoreNavigate(LoginActivity.this,objCommonResponse.getMsg().toString());

                    } else if (objCommonResponse.getStatus().toString().equals("0")) {
                        String msg = objCommonResponse.getMsg();
                        if (msg.toString().equals(""))
                            msg = getResources().getString(R.string.server_error);
                        //navigate to login
                        Util.dialogForMessageNavigate(LoginActivity.this, msg);
                    } else
                        Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.server_error));

                    //Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.server_error));
                } else
                    Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.server_error));
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                ivLogin.setEnabled(true);
                //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                //Toast.makeText(LoginActivity.this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                Util.dialogForMessage(LoginActivity.this, t.getLocalizedMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*if (GPSTracker.isFromSetting == true) {
            finish();
            startActivity(getIntent());
            GPSTracker.isFromSetting = false;
        }*/
        // Util.LocaleCheck(spf,LoginActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*if (GPSTracker.isFromSetting == true) {
            finish();
            startActivity(getIntent());
            GPSTracker.isFromSetting = false;
        }*/
    }


    boolean flagFingurePrint = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    boolean checkFingurePrint() {
        // Check whether the device has a Fingerprint sensor.

        try {
            if (fingerprintManager != null) {
                if (!fingerprintManager.isHardwareDetected()) {
                    /**
                     * An error message will be displayed if the device does not contain the fingerprint hardware.
                     * However if you plan to implement a default authentication method,
                     * you can redirect the user to a default authentication activity from here.
                     * Example:
                     * Intent intent = new Intent(this, DefaultAuthenticationActivity.class);
                     * startActivity(intent);
                     */
                    Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.your_device_does_not_have_a_fingerprint_sensor));
                    flagFingurePrint = false;

                } else {
                    // Checks whether fingerprint permission is set on manifest
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.fingerprint_authentication_permission_not_enabled), Toast.LENGTH_LONG).show();
                        flagFingurePrint = false;
                    } else {
                        // Check whether at least one fingerprint is registered
                        if (!fingerprintManager.hasEnrolledFingerprints()) {
                            Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.to_use_this_feature_please_setup_your_fingerprints_from_the_settings_tab_on_your_phone));
                            flagFingurePrint = false;
                        } else {
                            // Checks whether lock screen security is enabled or not
                            if (!keyguardManager.isKeyguardSecure()) {
                                Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.lock_screen_security_not_enabled_in_settings));
                                flagFingurePrint = false;
                            } else {
                                flagFingurePrint = true;
                            }
                        }
                    }
                }
            } else
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.your_device_does_not_have_a_fingerprint_sensor), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flagFingurePrint;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void fingurePrintOperation() {
        generateKey();
        if (cipherInit()) {
            FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
            FingerprintHandler helper = new FingerprintHandler(this, this);
            helper.startAuth(fingerprintManager, cryptoObject);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    @Override
    public void onSuccessForLogin() {
        if (Util.isInternetAvailable(LoginActivity.this))
            sendLoginDetailToSever(
                    edtUsername.getText().toString(),
                    temp_pass,
                    lattitude,
                    longitude, true);
        else
            Util.dialogForMessage(LoginActivity.this, getResources().getString(R.string.no_internet_connection));
    }

    @Override
    public void onSuccessForStatusCmd(int cmdType, Dialog dialog) {
    }

    @Override
    public void updateLatLng(Location location) {
        if (location != null) {
            Double lat = location.getLatitude();
            Double lng = location.getLongitude();
            spf.edit().putString(AppConstants.LATTITUDE, String.valueOf(lat)).apply();
            spf.edit().putString(AppConstants.LONGITUDE, String.valueOf(lng)).apply();
            spf.edit().putFloat(AppConstants.LOCATION_ACCURACY, location.getAccuracy()).apply();
            Log.i("Location: ", "Latitude : " + lat + "Longitude : " + lng);

            lattitude = lat;
            longitude = lng;
        }
    }

    @Override
    public void onClickForControl(int scount, float meters) {
        spf.edit().putInt(AppConstants.SATELLITE_COUNTS, scount).commit();
        spf.edit().putFloat(AppConstants.LOCATION_ACCURACY, meters).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        objFusedLocationUtils.onActivityResult(LoginActivity.this, requestCode, resultCode);
    }
/*
    @Override
    public void onClickForControl(Double Lat, Double longgitude, float accuracy) {

    }*/
}
  /*// override the base context of application to update default locale for this activity
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }*/
    /*if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //if (uniqueCode.toString().equals(""))
                    //   objUniqueCodeDialog.show();

                    getLocation();
                    String DEVICE_ID = getDeviceId(LoginActivity.this);
                    spf.edit().putString(AppConstants.DEVICE_ID, DEVICE_ID).apply();
                    Log.i(AppConstants.TAG, "DeviceID:" + DEVICE_ID);

                } else {
                    Toast.makeText(LoginActivity.this, "All permission requests are not granted..", Toast.LENGTH_SHORT).show();
                    isAllPermissionGranted = false;
                }*/