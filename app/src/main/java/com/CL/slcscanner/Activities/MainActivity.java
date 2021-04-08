package com.CL.slcscanner.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.ClientAssets.ClientAssestMaster;
import com.CL.slcscanner.Pojo.ClientAssets.Datum;
import com.CL.slcscanner.Pojo.CommonResponse;
import com.CL.slcscanner.Pojo.SettingMaster.Data;
import com.CL.slcscanner.Pojo.SettingMaster.SettingMaster;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.GPS.FusedLocationUtils;
import com.CL.slcscanner.Utils.GPS.GPSTracker;
import com.CL.slcscanner.Utils.LanguageHelper;
import com.CL.slcscanner.Utils.Util;
import com.CL.slcscanner.fragments.AddressFragement;
import com.CL.slcscanner.fragments.AllPoleSLCMapFragment;
import com.CL.slcscanner.fragments.CameraPreviewFragment;
import com.CL.slcscanner.fragments.ContactFragment;
import com.CL.slcscanner.fragments.NoteFragment;
import com.CL.slcscanner.fragments.PoleDataEditFragment;
import com.CL.slcscanner.fragments.PoleDataFragment;
import com.CL.slcscanner.fragments.PoleIdFragment;
import com.CL.slcscanner.fragments.SLCIdFragment;
import com.CL.slcscanner.fragments.ScanMacId;
import com.CL.slcscanner.fragments.ScanSLCId;
import com.CL.slcscanner.fragments.SelectLocationPoleFragment;
import com.CL.slcscanner.fragments.SettingFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FusedLocationUtils.UpdateUI {

    @BindView(R.id.llScan)
    LinearLayout llScan;
    @BindView(R.id.llPole)
    LinearLayout llPole;
    @BindView(R.id.llSettings)
    LinearLayout llSettings;
    @BindView(R.id.llHelp)
    LinearLayout llContact;
    @BindView(R.id.llMap)
    LinearLayout llMap;

    @BindView(R.id.imgScan)
    ImageView imgScan;
    @BindView(R.id.imgPole)
    ImageView imgPole;
    @BindView(R.id.imgSetting)
    ImageView imgSetting;
    @BindView(R.id.imgHelp)
    ImageView imgHelp;
    @BindView(R.id.imgMap)
    ImageView imgMap;

    @BindView(R.id.tvScan)
    TextView tvScan;
    @BindView(R.id.tvPole)
    TextView tvPole;
    @BindView(R.id.tvSetting)
    TextView tvSetting;
    @BindView(R.id.tvHelp)
    TextView tvHelp;
    @BindView(R.id.tvMap)
    TextView tvMap;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView objBottomNavigationView;
    SharedPreferences spf;

    API objApi;
    Util objUtil;

    @BindView(R.id.appBarMainn)
    AppBarLayout appBarLayout;

    @BindView(R.id.txtTest)
    TextView txtTest;

    @BindView(R.id.llPoleBack)
    LinearLayout llPoleBack;

    @BindView(R.id.tvPoleEditBack)
    TextView tvPoleEditBack;

    String clientId, userId;

    DateFormat df;

    FusedLocationUtils objFusedLocationUtils;

    private String[] PERMISSIONS = {
            android.Manifest.permission.READ_PHONE_STATE,

            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,

            android.Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,

            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,

    };
    private static final int PERMISSION_ALL = 1;
    boolean isAllPermissionGranted;
    private FirebaseAnalytics mFirebaseAnalytics;
    String event_name;
    Bundle bundleFB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        ButterKnife.bind(MainActivity.this);

        df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss");

        objApi = new SLCScanner().networkCall();
        objUtil = new Util();
        spf = getSharedPreferences(AppConstants.SPF, MODE_PRIVATE);
        clientId = spf.getString(AppConstants.CLIENT_ID, "");
        userId = spf.getString(AppConstants.USER_ID, "");
        objFusedLocationUtils = new FusedLocationUtils(MainActivity.this, this, null, true);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
        event_name = spf.getString(AppConstants.CLIENT_ID, null) + "_" + spf.getString(AppConstants.USER_ID, null) + "_";

        boolean isLangSelected = spf.getBoolean(AppConstants.LANGUAGE_SELECTED, false);
        String langCode = spf.getString(AppConstants.LANGUAGE_LOCALE, AppConstants.LANGUAGE_CODE_ENGLISH);

        if (!isLangSelected) {
            new Util().switchLanguage(MainActivity.this, langCode, true);
            Log.i(AppConstants.TAG, "switchLang called");
        }

        llScan.setOnClickListener(this);
        llPole.setOnClickListener(this);
        llSettings.setOnClickListener(this);
        llContact.setOnClickListener(this);
        llMap.setOnClickListener(this);

        //settingAPICall(); -- no more this api, deprecated !
        if (Util.isInternetAvailable(MainActivity.this))
            getClientAssets(clientId, spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC));
        else
            Util.dialogForMessage(MainActivity.this, getResources().getString(R.string.no_internet_connection));

        SharedPreferences.Editor edt = spf.edit();
        edt.putString(AppConstants.SPF_TEMP_SLCID, "");
        edt.putString(AppConstants.SPF_TEMP_MACID, "");
        edt.putString(AppConstants.SPF_TEMP_POLE_ID, "");
        edt.putString(AppConstants.SPF_DRAG_LONGITUDE, "0.0");
        edt.putString(AppConstants.SPF_DRAG_LATTITUDE, "0.0");
        edt.apply();

        spf.edit().putBoolean(AppConstants.isAppKilled, false).apply();
        //bgCall();

        if (!spf.contains(AppConstants.SELCTED_MAP_TYPE)) {
            String defaultMapType = getResources()
                    .getString(R.string.google_map_satellite);
            spf.edit().putString(AppConstants.SELCTED_MAP_TYPE, defaultMapType).apply();
        }
        spf.edit().putString(AppConstants.SPF_SLECTED_TAB, AppConstants.LIST_TAB_SELECTED).apply();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Util.hasPermissions(MainActivity.this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_ALL);
                return;
            } else {
                objFusedLocationUtils.buildGoogleApiClient();
                isAllPermissionGranted = true;
                spf.edit().putBoolean(AppConstants.isPermission, true).apply();
            }
        } else {
            objFusedLocationUtils.buildGoogleApiClient();
            isAllPermissionGranted = true;
            spf.edit().putBoolean(AppConstants.isPermission, true).apply();
        }

        //if (isAllPermissionGranted)
        navigation();

    }

    void navigation() {

        objUtil.loadFragment(new PoleDataFragment(), this);
        selectScan(false);
        selectPole(true);
        selectSetting(false);
        selectHelp(false);
        selectMap(false);
    }

    private void request6plus() {
        if (!Util.hasPermissions(MainActivity.this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, PERMISSION_ALL);
            return;
        } else {
            //getLocation();
            objFusedLocationUtils.buildGoogleApiClient();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
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
                    isAllPermissionGranted = true;
                    spf.edit().putBoolean(AppConstants.isPermission, true).apply();
                    navigation();
                } else {
                    isAllPermissionGranted = false;
                    spf.edit().putBoolean(AppConstants.isPermission, false).apply();
                    Util.dialogForPermisionMessage(MainActivity.this, getResources().getString(R.string.permission));
                }


               /* // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //getLocation();
                    objFusedLocationUtils.buildGoogleApiClient();
                } else {
                    Toast.makeText(MainActivity.this, "All permission requests are not granted..", Toast.LENGTH_SHORT).show();
                }*/
                return;
            }
        }
    }


    View.OnTouchListener objOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    LinearLayout view = (LinearLayout) v;
                    //overlay is black with transparency of 0x77 (119)
                    view.setBackgroundColor(0x770072BC);
                    view.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL: {
                    LinearLayout view = (LinearLayout) v;
                    //clear the overlay
                    view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    view.invalidate();
                    break;
                }
            }
            return false;
        }
    };

    //when application is in foreground
    void bgCall() {
        final Handler handler = new Handler();
        final int delay = 10000; //milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {
                //do something
                handler.postDelayed(this, delay);
                objApi.startBgcall(clientId, userId).enqueue(new Callback<CommonResponse>() {
                    @Override
                    public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                        if (response.body() != null) {
                            CommonResponse obj = response.body();
                            if (obj.getStatus().equalsIgnoreCase("success") || obj.getStatus().contains("success")) {
                                Toast.makeText(MainActivity.this, obj.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonResponse> call, Throwable t) {
                        Log.e(AppConstants.TAG, "error bg call");
                    }
                });
            }
        }, delay);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llScan:

                bundleFB = new Bundle();
                bundleFB.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "ScanNavigationClick");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundleFB);
                Log.d(AppConstants.TAG, event_name + "ScanNavigationClick");

                String result = spf.getString(AppConstants.SPF_SCANNER_CURRENT_FRAG, "");

                spf.edit().putString(AppConstants.SPF_SLECTED_TAB, AppConstants.SCAN_TAB_SELECTED).apply();

                if (result.equals(AppConstants.SPF_POLEID_FRAG)) {
                    objUtil.loadFragment(new PoleIdFragment(), MainActivity.this);
                } else if (result.equals(AppConstants.SPF_SLCID_FRAG)) {
                    objUtil.loadFragment(new SLCIdFragment(), MainActivity.this);
                } else if (result.equals(AppConstants.SPF_SELECT_POLE_LOCATION_FRAG)) {
                    objUtil.loadFragment(new SelectLocationPoleFragment(), MainActivity.this);
                } else if (result.equals(AppConstants.SPF_ADDRESS_FRAG)) {
                    objUtil.loadFragment(new AddressFragement(), MainActivity.this);
                } else if (result.equals(AppConstants.SPF_CAMERA_FRAG)) {
                    objUtil.loadFragment(new CameraPreviewFragment(), MainActivity.this);
                } else if (result.equals(AppConstants.SPF_NOTE_FRAGMENT)) {
                    Bundle mBundle = new Bundle();
                    NoteFragment objNoteFragment = new NoteFragment();
                    mBundle.putBoolean(AppConstants.ISVIEWONLY, false);
                    mBundle.putString(AppConstants.FROM, "main");
                    objNoteFragment.setArguments(mBundle);
                    objUtil.loadFragment(objNoteFragment, MainActivity.this);
                } else if (result.equals(AppConstants.SPF_SLC_ID_SCANNER_FRAG)) {
                    objUtil.loadFragment(new ScanSLCId(), MainActivity.this);
                } else if (result.equals(AppConstants.SPF_POLE_EDIT_FRAG)) {
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    PoleDataEditFragment fragment = new PoleDataEditFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isNewData", true);
                    bundle.putBoolean("IS_FROM_MAP", false);
                    bundle.putString("ID", "");
                    bundle.putString("slcID", spf.getString(AppConstants.SPF_TEMP_SLCID, ""));
                    bundle.putString("poleId", spf.getString(AppConstants.SPF_TEMP_POLE_ID, ""));
                    bundle.putDouble("Lat", Double.valueOf(spf.getString(AppConstants.SPF_DRAG_LATTITUDE, "0.0")));
                    bundle.putDouble("Long", Double.valueOf(spf.getString(AppConstants.SPF_DRAG_LONGITUDE, "0.0")));
                    bundle.putString("MacId", spf.getString(AppConstants.SPF_TEMP_MACID, ""));
                    bundle.putBoolean(AppConstants.isfromNote, false);

                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.frm1, fragment);
                    fragmentTransaction.commit(); // save the changes
                } else {
                    //objUtil.loadFragment(new CameraPreviewFragment(), MainActivity.this);
                    objUtil.loadFragment(new ScanMacId(), MainActivity.this);
                }
                selectScan(true);
                selectPole(false);
                selectSetting(false);
                selectMap(false);

                break;

            case R.id.llPole:

                bundleFB = new Bundle();
                bundleFB.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "ListNavigationSearch");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundleFB);
                Log.d(AppConstants.TAG, event_name + "ListNavigationSearch");


                String result1 = spf.getString(AppConstants.SPF_POLE_CURRENT_FRAG, "");
                spf.edit().putString(AppConstants.SPF_SLECTED_TAB, AppConstants.LIST_TAB_SELECTED).apply();

              /*  if (result1.toString().equals(AppConstants.SPF_POLE_EDIT_FRAG) || result1.toString().equals(AppConstants.SPF_SCANNER_EDIT_FRAG)) {

                    boolean isfromMap = spf.getBoolean(AppConstants.SPF_ISFROMMAP, false);
                    String ID = spf.getString(AppConstants.SPF_ID, "");

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    PoleDataEditFragment fragment = new PoleDataEditFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isNewData", false);
                    bundle.putBoolean("IS_FROM_MAP", isfromMap);
                    bundle.putString("ID", ID);
                    bundle.putString("slcID", spf.getString(AppConstants.SPF_TEMP_SLCID, ""));
                    bundle.putString("poleId", spf.getString(AppConstants.SPF_TEMP_POLE_ID, ""));
                    bundle.putDouble("Lat", Double.valueOf(spf.getString(AppConstants.SPF_POLE_DISPLAY_LAT, "0.0")));
                    bundle.putDouble("Long", Double.valueOf(spf.getString(AppConstants.SPF_POLE_DISPLAY_Long, "0.0")));
                    bundle.putString("MacId", spf.getString(AppConstants.SPF_TEMP_MACID, ""));
                    bundle.putBoolean(AppConstants.isfromNote,false);
                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.frm1, fragment);
                    fragmentTransaction.commit(); // save the changes
                } else */

                if (result1.toString().equals(AppConstants.SPF_POLE_ALL_MAP) || (result1.toString().equals(AppConstants.SPF_POLE_DISPLAY_FRAG))) {
                    objUtil.loadFragment(new PoleDataFragment(), MainActivity.this);
                } else
                    objUtil.loadFragment(new PoleDataFragment(), MainActivity.this);

                //loadFragment(new SelectLocationPoleFragment());
                selectScan(false);
                selectPole(true);
                selectSetting(false);
                selectMap(false);

                break;

            case R.id.llSettings:
                bundleFB = new Bundle();
                bundleFB.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "Settings");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundleFB);
                Log.d(AppConstants.TAG, event_name + "Settings");

                spf.edit().putString(AppConstants.SPF_SLECTED_TAB, AppConstants.SETTING_TAB_SELECTED).apply();
                objUtil.loadFragment(new SettingFragment(), MainActivity.this);
                //loadFragmentWithBackStack(new SettingFragment());
                selectScan(false);
                selectPole(false);
                selectSetting(true);
                selectMap(false);

                break;

            case R.id.llHelp:
                //objUtil.loadFragment(new ContactFragment(), MainActivity.this);
                //loadFragmentWithBackStack(new ContactFragment());
                selectScan(false);
                selectPole(false);
                selectSetting(false);
                selectHelp(true);
                break;

            case R.id.llMap:

                bundleFB = new Bundle();
                bundleFB.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "Map");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundleFB);
                Log.d(AppConstants.TAG, event_name + "Map");

                objUtil.loadFragment(new AllPoleSLCMapFragment(), MainActivity.this);
                selectScan(false);
                selectPole(false);
                selectSetting(false);
                selectMap(true);
                break;

        }
    }

    public void selectScan(boolean mValue) {
        if (mValue) {
            imgScan.setImageDrawable(getResources().getDrawable(R.drawable.scan_white));
            tvScan.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            imgScan.setImageDrawable(getResources().getDrawable(R.drawable.scan_icon_grey));
            //tvScan.setTextColor(getResources().getColor(R.color.colorGray));
            tvScan.setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    public void selectPole(boolean mValue) {
        if (mValue) {
            imgPole.setImageDrawable(getResources().getDrawable(R.drawable.list));
            tvPole.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            imgPole.setImageDrawable(getResources().getDrawable(R.drawable.list_grey));
            tvPole.setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    public void selectSetting(boolean mValue) {
        if (mValue) {
            imgSetting.setImageDrawable(getResources().getDrawable(R.drawable.setting_white));
            tvSetting.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            imgSetting.setImageDrawable(getResources().getDrawable(R.drawable.setting_grey));
            tvSetting.setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    public void selectHelp(boolean mValue) {
        if (mValue) {
            imgHelp.setImageDrawable(getResources().getDrawable(R.drawable.contact_white));
            tvHelp.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            imgHelp.setImageDrawable(getResources().getDrawable(R.drawable.contact_g));
            tvHelp.setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    public void selectMap(boolean mValue) {
        if (mValue) {
            imgMap.setImageDrawable(getResources().getDrawable(R.drawable.map_icon_pole));
            tvMap.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            imgMap.setImageDrawable(getResources().getDrawable(R.drawable.map_icon_pole_grey));
            tvMap.setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    private void loadFragmentWithBackStack(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment

        fragmentTransaction.replace(R.id.frm1, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit(); // save the changes
    }

   /* public class BottomNavigationViewHelper {
        @SuppressLint("RestrictedApi")
        public void disableShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    //noinspection RestrictedApi
                    item.setShiftingMode(false);
                    // set once again checked value, so view will be updated
                    //noinspection RestrictedApi
                    item.setChecked(item.getItemData().isChecked());
                }
            } catch (NoSuchFieldException e) {
                Log.e("BNVHelper", "Unable to get shift mode field", e);
            } catch (IllegalAccessException e) {
                Log.e("BNVHelper", "Unable to change value of shift mode", e);
            }
        }
    }*/

    public void settingAPICall() {
        String units1 = spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC);
        objApi.getSettingList(units1).enqueue(new Callback<SettingMaster>() {
            @Override
            public void onResponse(Call<SettingMaster> call, Response<SettingMaster> response) {
                SettingMaster objSettingMaster = response.body();
                if (objSettingMaster.getStatus().toString().equals("success")) {
                    Data objData = objSettingMaster.getData();

                    List<String> list = objData.getFixtureManufacturer();
                    list.add(0, "Other");

                    List<String> fixtureWattageList = objData.getFixtureWattage();
                    fixtureWattageList.add(0, "Other");

                    List<String> heightList = objData.getPole_height();
                    heightList.add(0, "Other");

                    List<String> methodList = objData.getMountingMethod();
                    methodList.add(0, "Other");

                    List<String> armLenghtList = objData.getArmLenght();
                    armLenghtList.add(0, "Other");

                    List<String> fixurePerPoleList = objData.getFixturesPerPole();
                    fixurePerPoleList.add(0, "Other");

                    List<String> poleConditionList = objData.getPoleCondition();
                    poleConditionList.add(0, "Other");

                    List<String> poleConfigList = objData.getPoleConfig();
                    poleConfigList.add(0, "Other");

                    List<String> poleTypeList = objData.getPoleType();
                    poleTypeList.add(0, "Other");

                    List<String> poleDiameterList = objData.getPoleDiameter();
                    poleDiameterList.add(0, "Other");

                    List<String> poleOwnerList = objData.getPole_owner();
                    poleOwnerList.add(0, "Other");

                    List<String> poleFeedList = objData.getPole_power_feed();
                    poleFeedList.add(0, "Other");

                    objUtil.setSPFData(AppConstants.SPF_LIST_HEIGHT, heightList, spf);
                    objUtil.setSPFData(AppConstants.SPF_LIST_ARM_LEGTH, armLenghtList, spf);
                    objUtil.setSPFData(AppConstants.SPF_LIST_FIXTURE_MANUFACTURER, list, spf);
                    objUtil.setSPFData(AppConstants.SPF_LIST_FIXTURES_PER_POLE, fixurePerPoleList, spf);
                    objUtil.setSPFData(AppConstants.SPF_LIST_FIXTURE_WATTAGE, fixtureWattageList, spf);
                    objUtil.setSPFData(AppConstants.SPF_LIST_MOUNTING_METHOD, methodList, spf);
                    objUtil.setSPFData(AppConstants.SPF_LIST_POLE_CONDITION, poleConditionList, spf);
                    objUtil.setSPFData(AppConstants.SPF_LIST_POLE_CONFIG, poleConfigList, spf);
                    objUtil.setSPFData(AppConstants.SPF_LIST_POLE_TYPE, poleTypeList, spf);
                    objUtil.setSPFData(AppConstants.SPF_LIST_POLE_DIAMETER, poleDiameterList, spf);
                    objUtil.setSPFData(AppConstants.SPF_LIST_POLE_FEED, poleFeedList, spf);
                    objUtil.setSPFData(AppConstants.SPF_LIST_POLE_OWNER, poleOwnerList, spf);
                }
            }

            @Override
            public void onFailure(Call<SettingMaster> call, Throwable t) {

            }
        });
    }

    void getClientAssets(String clientId, String units) {
       /* String CopyFromPrevious;
        boolean isCopyFromPrevious=spf.getBoolean(AppConstants.CopyFromPrevious,false);

        if(isCopyFromPrevious)
            CopyFromPrevious="Yes";
        else
            CopyFromPrevious="No";*/
        ArrayList<Datum> mList = new ArrayList<>();
        objApi.getClientAssets(clientId, units, "No").enqueue(new Callback<ClientAssestMaster>() {
            @Override
            public void onResponse(Call<ClientAssestMaster> call, Response<ClientAssestMaster> response) {
                ClientAssestMaster objClientAssestMaster = response.body();
                if (objClientAssestMaster != null) {
                    if (objClientAssestMaster.getStatus().toString().equals("success")) {
                        mList.clear();
                        for (int i = 0; i < objClientAssestMaster.getData().size(); i++) {
                            String AttKey = Util.decodeUnicode(objClientAssestMaster.getData().get(i).getAttrKey());
                            String AttributeName = Util.decodeUnicode(objClientAssestMaster.getData().get(i).getAttributeName());
                            if (AttKey.toLowerCase().equalsIgnoreCase(AppConstants.POLE_IMAGE)
                                    || AttKey.toLowerCase().equalsIgnoreCase(AppConstants.POLE_IMAGE_SPANISH)
                                    || AttKey.toLowerCase().equalsIgnoreCase(AppConstants.POLE_IMAGE_PT)) {

                                spf.edit().putString(AppConstants.pole_image_key, AttKey).apply();
                            } else {
                                mList.add(objClientAssestMaster.getData().get(i));
                            }
                        }

                        Util.setAssetDataSPF(spf, mList);
                        Log.i(AppConstants.TAG, "Assets data stored");
                        Util.addLog("Assets data stored");
                    } else {
                        Log.i(AppConstants.TAG, "Assets data stored Failed !");
                        Util.addLog("Assets data stored Failed !");
                    }
                } else
                    Util.dialogForMessage(MainActivity.this, getResources().getString(R.string.server_error));
            }

            @Override
            public void onFailure(Call<ClientAssestMaster> call, Throwable t) {
                Util.dialogForMessage(MainActivity.this, t.getLocalizedMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        long current = System.currentTimeMillis();
        long lastDeActive = spf.getLong(AppConstants.SPF_DEACTIVE_TIME, current);

        String dfLastActive = df.format(new Date(lastDeActive));
        String dfCurrent = df.format(new Date(current));
        String dfWithInterval = df.format(new Date(lastDeActive + AppConstants.SESSION_INTERVAL));
        Log.i(AppConstants.TAG, "LastActive: " + dfLastActive + " Current: " + dfCurrent + " Expire at: " + dfWithInterval);
        Util.addLog("LastActive: " + dfLastActive + " Current Time: " + dfCurrent + " Expire at: " + dfWithInterval);

        if (current > (lastDeActive + AppConstants.SESSION_INTERVAL)) {
            //finish();
            //spf.edit().putBoolean(AppConstants.ISLOGGEDIN, false).apply();
            //startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            if (GPSTracker.isFromSetting == true) {
                GPSTracker.isFromSetting = false;

                if (GPSTracker.isIsFromPoleData == true) {
                    objUtil.loadFragment(new PoleDataFragment(), MainActivity.this);
                    selectScan(false);
                    selectPole(true);
                    selectSetting(false);
                    selectHelp(false);
                } else {
                    objUtil.loadFragment(new SelectLocationPoleFragment(), MainActivity.this);
                    selectScan(true);
                    selectPole(false);
                    selectSetting(false);
                    selectHelp(false);
                }
            }
        }

        //Util.LocaleCheck(spf,MainActivity.this);
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.back_press_title_dialog))
                .setMessage(getResources().getString(R.string.back_press_msg_dialog))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (GPSTracker.isFromSetting == true) {
                            startActivity(getIntent());
                            GPSTracker.isFromSetting = false;
                        }
                        finish();
                        spf.edit().putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, "").apply();
                        spf.edit().putString(AppConstants.SPF_POLE_CURRENT_FRAG, "").apply();
                    }
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(AppConstants.TAG, "onStop");

        long timeDate = System.currentTimeMillis();
        String dfWithInterval = df.format(new Date(timeDate));
        Log.i(AppConstants.TAG, "TimeDate:" + dfWithInterval);

        spf.edit().putLong(AppConstants.SPF_DEACTIVE_TIME, timeDate).apply();
        Util.addLog("App Deactivate at:: " + timeDate);
        objFusedLocationUtils.pause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(AppConstants.TAG, "onDestroy");

        SharedPreferences.Editor edit = spf.edit();
        edit.remove(AppConstants.SELECTED_NODE_TYPE_SAVE);
        edit.remove(AppConstants.SELECTED_NODE_TYPE_INDEX_SAVE);

        edit.remove(AppConstants.SELECTED_NODE_TYPE_LIST);
        edit.remove(AppConstants.SELECTED_NODE_TYPE_INDEX_LIST);

        edit.remove(AppConstants.SELECTED_NODE_TYPE_EDIT_SLC);
        edit.remove(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_SLC);

        edit.remove(AppConstants.SELECTED_NODE_TYPE_EDIT_MAC);
        edit.remove(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_MAC);

        edit.remove(AppConstants.SPF_SLECTED_TAB);
        edit.remove(AppConstants.SPF_SCANNER_CURRENT_FRAG);
        edit.remove(AppConstants.SPF_POLE_CURRENT_FRAG);

        edit.apply();
    }

    /*public androidx.core.app.Fragment getVisibleFragment() {
        androidx.core.app.FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<androidx.core.app.Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (androidx.core.app.Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }*/

    // override the base context of application to update default locale for this activity
    @Override
    protected void attachBaseContext(Context base) {
        spf = base.getSharedPreferences(AppConstants.SPF, MODE_PRIVATE);
        super.attachBaseContext(LanguageHelper.wrap(base, Util.getDeviceLocale(spf)));
    }

    private void setApplicationLanguage(String newLanguage) {
        Resources activityRes = getResources();
        Configuration activityConf = activityRes.getConfiguration();
        Locale newLocale = new Locale(newLanguage);
        activityConf.setLocale(newLocale);
        activityRes.updateConfiguration(activityConf, activityRes.getDisplayMetrics());

        Resources applicationRes = getApplicationContext().getResources();
        Configuration applicationConf = applicationRes.getConfiguration();
        applicationConf.setLocale(newLocale);
        applicationRes.updateConfiguration(applicationConf,
                applicationRes.getDisplayMetrics());
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        objFusedLocationUtils.onActivityResult(MainActivity.this,requestCode,resultCode);
    }*/


    @Override
    protected void onStart() {
        super.onStart();
        objFusedLocationUtils.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //objFusedLocationUtils.pause();
        Log.i(AppConstants.TAG, "Pause");
    }


    @Override
    public void updateLatLng(Location location) {
        if (location != null) {
            spf.edit().putString(AppConstants.LATTITUDE, String.valueOf(location.getLatitude())).apply();
            spf.edit().putString(AppConstants.LONGITUDE, String.valueOf(location.getLongitude())).apply();
            spf.edit().putFloat(AppConstants.LOCATION_ACCURACY, location.getAccuracy()).apply();

        }
    }

    @Override
    public void onClickForControl(int scount, float meters) {
        spf.edit().putInt(AppConstants.SATELLITE_COUNTS, scount).commit();
        spf.edit().putFloat(AppConstants.LOCATION_ACCURACY, meters).commit();

    }
}
/*
MAP type UI Navigation code

    String selectedTab=spf.getString(AppConstants.SPF_SLECTED_TAB,AppConstants.LIST_TAB_SELECTED);
    String childLastActiveFragment="";
    String currentFragment = null;
                if(selectedTab.equalsIgnoreCase(AppConstants.LIST_TAB_SELECTED)){
                        childLastActiveFragment = spf.getString(AppConstants.SPF_POLE_CURRENT_FRAG, "");
                        if (childLastActiveFragment.equals(AppConstants.SPF_POLE_DISPLAY_FRAG)) {
                        currentFragment = AppConstants.SPF_POLE_DISPLAY_FRAG;
                        }else if(childLastActiveFragment.equals(AppConstants.SPF_POLE_ALL_MAP)) {
                        currentFragment = AppConstants.SPF_POLE_ALL_MAP;
                        } else if(childLastActiveFragment.equals(AppConstants.SPF_POLE_LIST_FRAG)) {
                        currentFragment = AppConstants.SPF_POLE_LIST_FRAG;
                        }

                        }else if(selectedTab.equalsIgnoreCase(AppConstants.SCAN_TAB_SELECTED)){
                        childLastActiveFragment = spf.getString(AppConstants.SPF_SCANNER_CURRENT_FRAG, "");
                        if (childLastActiveFragment.equals(AppConstants.SPF_POLEID_FRAG)) {
                        currentFragment = AppConstants.SPF_POLEID_FRAG;
                        } else if (childLastActiveFragment.equals(AppConstants.SPF_SLCID_FRAG)) {
                        currentFragment = AppConstants.SPF_SLCID_FRAG;
                        } else if (childLastActiveFragment.equals(AppConstants.SPF_SELECT_POLE_LOCATION_FRAG)) {
                        currentFragment = AppConstants.SPF_SELECT_POLE_LOCATION_FRAG;
                        } else if (childLastActiveFragment.equals(AppConstants.SPF_ADDRESS_FRAG)) {
                        currentFragment = AppConstants.SPF_ADDRESS_FRAG;
                        } else if (childLastActiveFragment.equals(AppConstants.SPF_CAMERA_FRAG)) {
                        currentFragment = AppConstants.SPF_CAMERA_FRAG;
                        } else if (childLastActiveFragment.equals(AppConstants.SPF_SLC_ID_SCANNER_FRAG)) {
                        currentFragment = AppConstants.SPF_SLC_ID_SCANNER_FRAG;
                        } else if (childLastActiveFragment.equals(AppConstants.SPF_SCANNER_FRAG)) {
                        currentFragment = AppConstants.SPF_SCANNER_FRAG;
                        } else if (childLastActiveFragment.equals(AppConstants.SPF_POLE_EDIT_FRAG)) {
                        currentFragment = AppConstants.SPF_POLE_EDIT_FRAG;
                        }
                        } else if(selectedTab.equalsIgnoreCase(AppConstants.SETTING_TAB_SELECTED)){
                        currentFragment=AppConstants.SETTING_TAB_SELECTED;
                        }

                        //getVisibleFragment();

                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();

                        Bundle mBundle = new Bundle();
                        mBundle.putString(AppConstants.KEY_MAP_TYPE_UI_TRANSFER, currentFragment);

                        MapTypeFragment mapTypeFragment = new MapTypeFragment();
                        mapTypeFragment.setArguments(mBundle);

                        fragmentTransaction.replace(R.id.frm1, mapTypeFragment);
                        fragmentTransaction.commit(); // save the changes

//loadFragment(mapTypeFragment);
//loadFragmentWithBackStack(new ContactFragment());
*/
