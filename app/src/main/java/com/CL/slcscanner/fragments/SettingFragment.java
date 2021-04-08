package com.CL.slcscanner.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.CL.slcscanner.Activities.MainActivity;
import com.CL.slcscanner.Activities.SecurityCode;
import com.CL.slcscanner.Adapter.MapTypeAdapter;
import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.CommonResponse;
import com.CL.slcscanner.Pojo.GoogleMapTypes;
import com.CL.slcscanner.Pojo.MapTypePojo;
import com.CL.slcscanner.Pojo.SettingMaster2.Data;
import com.CL.slcscanner.Pojo.SettingMaster2.SettingMaster2;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.DBHelper;
import com.CL.slcscanner.Utils.MyCallbackForMapType;
import com.CL.slcscanner.Utils.Util;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.android.segmented.SegmentedGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vrajesh on 2/24/2018.
 */

public class SettingFragment extends Fragment implements MyCallbackForMapType {

    @BindView(R.id.ivLogout)
    Button ivLogout;

    @BindView(R.id.sbCapturePoleID)
    SwitchButton sbCapturePoleID;

    @BindView(R.id.sbOtherData)
    SwitchButton sbOtherData;

    @BindView(R.id.segmentMeasurementUnits)
    SegmentedGroup segmentMeasurementUnits;

    @BindView(R.id.btnMatric)
    RadioButton btnMatric;
    @BindView(R.id.btnEnglish)
    RadioButton btnEnglish;

    View view;
    SharedPreferences spf;

    @BindView(R.id.ivsettingBg)
    ImageView ivsettingBg;

    @BindView(R.id.tvVersion)
    TextView tvVersion;

    @BindView(R.id.txtMapType)
    TextView txtMapType;

    @BindView(R.id.txtLangType)
    TextView txtLangType;

    @BindView(R.id.llMapSetting)
    LinearLayout llMapSetting;

    @BindView(R.id.llLangSetting)
    LinearLayout llLangSetting;

    DBHelper mDatabase;

    API objApi;
    String client_id;
    String units;
    ProgressDialog dialog;
    @BindView(R.id.rvMapType)
    RecyclerView rvMapType;

    MapTypeAdapter mAdapter;
    List<MapTypePojo> mList;
    List<MapTypePojo> mLangList;

    String selectedMap;
    String selectLang;
    String langCode;
    AlertDialog dialogSelection;

    ArrayList<GoogleMapTypes> listGoogleMapTypes;
    ProgressDialog progressLanguage;

    int selectedMapIndex;
    int selectedLangIndex;

    Util objUtil;

    boolean isManualPoleDetail = false;

    private FirebaseAnalytics mFirebaseAnalytics;
    String event_name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setting, null);
        init();
        return view;
    }

    void init() {

        objApi = new SLCScanner().networkCall();
        getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
        getActivity().findViewById(R.id.llNodeType).setVisibility(View.GONE);

        objUtil = new Util();
        mList = new ArrayList<>();
        mLangList = new ArrayList<>();
        mList.clear();
        mLangList.clear();

        ButterKnife.bind(this, view);

        //BG image
        //Glide.with(this).load(R.drawable.bg_1_1242_2208).into(ivsettingBg);

        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        client_id = spf.getString(AppConstants.CLIENT_ID, "");
        mDatabase = new DBHelper(getActivity());
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        event_name = spf.getString(AppConstants.CLIENT_ID, null) + "_" + spf.getString(AppConstants.USER_ID, null) + "_";
        mFirebaseAnalytics.setCurrentScreen(getActivity(), "SettingUi", null);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getResources().getString(R.string.loading));
        dialog.setCancelable(false);

        if (Util.isInternetAvailable(getActivity()))
            getSettigs(client_id);
        else
            Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));

        //ivLogout.setColorFilter(0xFF000000, PorterDuff.Mode.MULTIPLY);

        //selectedMap = spf.getString(AppConstants.SELCTED_MAP_TYPE, getString(R.string.google_map_satellite));

        String locale = Util.getDeviceLocale(spf);
        Log.i(AppConstants.TAG, "**" + locale);
        langCode = spf.getString(AppConstants.LANGUAGE_LOCALE, locale.toString());

        boolean isLanguageSelected = spf.getBoolean(AppConstants.LANGUAGE_SELECTED, false);
        if (!isLanguageSelected) {
            if (langCode.toString().contains(AppConstants.LANGUAGE_CODE_ENGLISH)) {
                selectedLangIndex = 0;
            } else if (langCode.toString().contains(AppConstants.LANGUAGE_CODE_SPANISH)) {
                selectedLangIndex = 1;
            } else if (langCode.toString().contains(AppConstants.LANGUAGE_CODE_PORTUGUES)) {
                selectedLangIndex = 2;
            }
            spf.edit().putInt(AppConstants.SELECTED_LANG_INDEX, selectedLangIndex).apply();
        } else {
            selectLang = spf.getString(AppConstants.SELECTED_LANG_TYPE, getString(R.string.english));
            selectedLangIndex = spf.getInt(AppConstants.SELECTED_LANG_INDEX, 0);
        }

        selectedMapIndex = spf.getInt(AppConstants.SELECTED_MAP_INDEX, 1); // satellite so default 1

        units = spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC);
        //String client_units = spf.getString(AppConstants.SPF_CLIENT_MEASURMENT_UNIT, AppConstants.SPF_UNITES_MATRIC);
        //ivLogout.setOnTouchListener(Util.colorFilter());

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            tvVersion.setText(getString(R.string.version) + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (getActivity() != null) {
            ((MainActivity) getActivity()).selectPole(false);
            ((MainActivity) getActivity()).selectScan(false);
            ((MainActivity) getActivity()).selectSetting(true);
            ((MainActivity) getActivity()).selectMap(false);
        }

        mAdapter = new MapTypeAdapter(getActivity(), mList, this);
        //rvMapType.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        rvMapType.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rvMapType.setLayoutManager(mLayoutManager);
        rvMapType.setAdapter(mAdapter);

        segmentMeasurementUnits.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                Bundle bundle=new Bundle();
                int selectedId = segmentMeasurementUnits.getCheckedRadioButtonId();

                if (selectedId == R.id.btnEnglish) {
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "SettingUnitEnglish");
                    Log.i(AppConstants.TAG, "English :checked");
                    Log.d(AppConstants.TAG, event_name+"SettingUnitEnglish");
                    spf.edit().putString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_ENGLISH).apply();
                    //((MainActivity) getActivity()).settingAPICall();
                } else {
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "SettingUnitMetric");
                    Log.i(AppConstants.TAG, "Matric :checked");
                    Log.d(AppConstants.TAG, event_name+"SettingUnitMetric");
                    spf.edit().putString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC).apply();
                    //((MainActivity) getActivity()).settingAPICall();
                }
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });

        sbCapturePoleID.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                Bundle bundle=new Bundle();
                Log.i(AppConstants.TAG, "pole id :checked");
                if (isChecked) {
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "SettingCapturePoleIdOn");
                    spf.edit().putBoolean(AppConstants.SPF_POLE_ID_VISIBILITY, true).apply();
                    Log.i(AppConstants.TAG, "pole id :checked true");
                    Util.addLog("pole id :checked");
                    Log.d(AppConstants.TAG, event_name+"SettingCapturePoleIdOn");
                } else {
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "SettingCapturePoleIdOff");
                    spf.edit().putBoolean(AppConstants.SPF_POLE_ID_VISIBILITY, false).apply(); // make it false...this is temporary
                    Log.i(AppConstants.TAG, "pole id :checked false");
                    Util.addLog("pole id :unchecked");
                    Log.d(AppConstants.TAG, event_name+"SettingCapturePoleIdOff");
                }
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });

        sbOtherData.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                Bundle bundle=new Bundle();

                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                Log.d(AppConstants.TAG, event_name+"OtherData");
                isManualPoleDetail = true;

                if (isChecked) {
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "SettingPoleDetailsOn");
                    spf.edit().putBoolean(AppConstants.SPF_OTHER_DATA_VISIBILITY, true).apply();
                    Log.i(AppConstants.TAG, "other data :checked true");
                    Util.addLog("pole data :checked");
                    Log.d(AppConstants.TAG, event_name+"SettingPoleDetailsOn");
                } else {
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "SettingPoleDetailsOn");
                    spf.edit().putBoolean(AppConstants.SPF_OTHER_DATA_VISIBILITY, false).apply(); // make it false...this is temporary
                    Log.i(AppConstants.TAG, "other data :checked false");
                    Util.addLog("pole data :unchecked");
                    Log.d(AppConstants.TAG, event_name+"SettingPoleDetailsOn");
                }
            }
        });

        setMapType();

        setLangType();

        listGoogleMapTypes = new ArrayList<>();

        llMapSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle=new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "SettingMapType");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                Log.d(AppConstants.TAG, event_name+"SettingMapType");

                ShowDialog(mList, getResources().getString(R.string.select_map_type), true);
            }
        });

        llLangSetting.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Bundle bundle=new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "SettingLanguage");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                        Log.d(AppConstants.TAG, event_name+"SettingLanguage");


                        ShowDialog(mLangList, getResources().getString(R.string.select_lang), false);
                    }
                }
        );
    }

    void changeLaguage(String langCode) {

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(config,
                getActivity().getBaseContext().getResources().getDisplayMetrics());

    }

    void ShowDialog(final List<MapTypePojo> objList, String title, final boolean isSelectMapType) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_title, null);

        TextView tvTitle1 = view.findViewById(R.id.tvDialogTitle1);
        TextView tvTitle2 = view.findViewById(R.id.tvDialogTitle2);
        tvTitle2.setVisibility(View.VISIBLE);
        tvTitle1.setVisibility(View.GONE);

        tvTitle2.setText(title);
        builder.setCustomTitle(view);
        //builder.setTitle(title);

        int selectedIndex;

        //getting fresh shared preference values ...!
        selectedLangIndex = spf.getInt(AppConstants.SELECTED_LANG_INDEX, 0);
        selectedMapIndex = spf.getInt(AppConstants.SELECTED_MAP_INDEX, 1);

        if (isSelectMapType) {
            selectedIndex = selectedMapIndex;
        } else {
            selectedIndex = selectedLangIndex;
        }

        final String[] ary = new String[objList.size()];

        for (int i = 0; i < objList.size(); i++) {
            ary[i] = objList.get(i).getValue();
        }

        builder.setSingleChoiceItems(ary, selectedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog1, int which) {
                String langTempCode = "";
                objList.get(which).setChecked(true);

                MapTypePojo objMapTypePojo = objList.get(which);

                if (isSelectMapType) {
                    txtMapType.setText(objMapTypePojo.getValue());
                    spf.edit().putString(AppConstants.SELCTED_MAP_TYPE, objMapTypePojo.getValue().toString()).apply();
                    spf.edit().putString(AppConstants.SELECTED_MAP_TYPE_KEY, objMapTypePojo.getKeyLable().toString()).apply();
                    spf.edit().putInt(AppConstants.SELECTED_MAP_INDEX, which).apply();
                } else {
                    String tempLang = objMapTypePojo.getKeyLable();

                    if (tempLang.equals(getString(R.string.key_english))) {
                        langTempCode = AppConstants.LANGUAGE_CODE_ENGLISH;
                    } else if (tempLang.equals(getString(R.string.key_spanins))) {
                        langTempCode = AppConstants.LANGUAGE_CODE_SPANISH;
                    } else if (tempLang.equals(getString(R.string.key_portuguese))) {
                        langTempCode = AppConstants.LANGUAGE_CODE_PORTUGUES;
                    }
                }
                if (!isSelectMapType) {
                    //loadFragment(new SettingFragment());
                    if (Util.isInternetAvailable(getActivity())) {
                        changeLanguageAPICall(spf.getString(AppConstants.USER_ID, ""),
                                langTempCode,
                                objMapTypePojo.getKeyLable(),
                                objMapTypePojo.getValue(), which);
                    } else {
                        Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
                    }
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //dialog.dismiss();
                        dialog1.cancel();
                    }
                }, 500);
                mAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // create and show the alert dialog
        dialogSelection = builder.create();
        dialogSelection.show();
    }

    void changeLanguageAPICall(String userid, final String langCode, final String langKey, final String langValue, final int index) {
        dialog.show();
        Call<CommonResponse> objApiCall = objApi.changeLanguage(userid, langCode, client_id);

        objApiCall.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {

                if (response.body() != null) {
                    CommonResponse commonResponse = response.body();
                    String msg = response.body().getMsg().toString();
                    //if(response.body().getStatus().equalsIgnoreCase("success")){

                    Log.i(AppConstants.TAG, msg);
                    txtLangType.setText(langValue);
                    SharedPreferences.Editor editor = spf.edit();
                    editor.putString(AppConstants.SELECTED_LANG_TYPE, langValue);
                    editor.putString(AppConstants.SELECTED_LANG_TYPE_KEY, langKey);
                    editor.putInt(AppConstants.SELECTED_LANG_INDEX, index);
                    editor.putString(AppConstants.LANGUAGE_LOCALE, langCode);
                    editor.putBoolean(AppConstants.LANGUAGE_SELECTED, true);
                    editor.putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, "");

                    editor.putString(AppConstants.MACID_LABLE, commonResponse.getScanLBL());
                    editor.putString(AppConstants.MACID_PH, commonResponse.getScanPH());

                    editor.commit();
                    new Util().switchLanguage(getActivity(), langCode, false);
                    //}else{
                    // Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
                    //   Log.i(AppConstants.TAG,msg);
                    //}

                } else
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.server_error));
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                new Util().dismissProgressDialog(dialog);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.e(AppConstants.TAG, t.getLocalizedMessage());
            }
        });
    }

    void setMapType() {


        MapTypePojo obj1 = new MapTypePojo();
        obj1.setKeyLable(getString(R.string.key_google_map_standard));
        obj1.setValue(getString(R.string.google_map_standard));
        obj1.setPosition(0);
        obj1.setStyle(false);

        if (selectedMapIndex == 0)
            obj1.setChecked(true);
        else
            obj1.setChecked(false);
        mList.add(obj1);

        MapTypePojo obj2 = new MapTypePojo();
        obj2.setKeyLable(getString(R.string.key_google_map_satellite));
        obj2.setValue(getString(R.string.google_map_satellite));
        obj2.setPosition(1);
        obj2.setStyle(false);
        if (selectedMapIndex == 1)
            obj2.setChecked(true);
        else
            obj2.setChecked(false);
        mList.add(obj2);

        MapTypePojo obj3 = new MapTypePojo();
        obj3.setKeyLable(getString(R.string.key_google_map_hybride));
        obj3.setValue(getString(R.string.google_map_hybride));
        obj3.setPosition(2);
        obj3.setStyle(false);
        if (selectedMapIndex == 2)
            obj3.setChecked(true);
        else
            obj3.setChecked(false);
        mList.add(obj3);

        MapTypePojo obj4 = new MapTypePojo();
        obj4.setKeyLable(getString(R.string.key_open_street_standard));
        obj4.setValue(getString(R.string.open_street_standard));
        obj4.setPosition(3);
        obj4.setStyle(true);
        obj4.setMapLink(AppConstants.OPEN_STREET_MAP_STANDARD_URL);
        if (selectedMapIndex == 4)
            obj4.setChecked(true);
        else
            obj4.setChecked(false);
        mList.add(obj4);

        txtMapType.setText(mList.get(selectedMapIndex).getValue());

        mAdapter.notifyDataSetChanged();
    }

    void setLangType() {

        MapTypePojo obj1 = new MapTypePojo();
        obj1.setKeyLable(getString(R.string.key_english));
        obj1.setValue(getString(R.string.key_english));
        obj1.setPosition(0);
        obj1.setStyle(false);

        if (selectedLangIndex == 0)
            obj1.setChecked(true);
        else
            obj1.setChecked(false);
        mLangList.add(obj1);

        MapTypePojo obj2 = new MapTypePojo();
        obj2.setKeyLable(getString(R.string.key_spanins));
        obj2.setValue(getString(R.string.key_spanins));
        obj2.setPosition(1);
        obj2.setStyle(false);
        if (selectedLangIndex == 1)
            obj2.setChecked(true);
        else
            obj2.setChecked(false);
        mLangList.add(obj2);

        MapTypePojo obj3 = new MapTypePojo();
        obj3.setKeyLable(getString(R.string.key_portuguese));
        obj3.setValue(getString(R.string.key_portuguese));
        obj3.setPosition(2);
        obj3.setStyle(false);
        if (selectedLangIndex == 2)
            obj3.setChecked(true);
        else
            obj3.setChecked(false);
        mLangList.add(obj3);

        txtLangType.setText(mLangList.get(selectedLangIndex).getValue());

    }

    @Override
    public void onClickForControl(int position, MapTypePojo objSlcsBean) {

        Log.i(AppConstants.TAG, "Selected map: " + objSlcsBean.getKeyLable() + " | " + objSlcsBean.getValue());
        if (objSlcsBean.isStyle()) {
            spf.edit().putString(AppConstants.SELCTED_MAP_TYPE, objSlcsBean.getMapLink().toString()).apply();
        } else {
            spf.edit().putString(AppConstants.SELCTED_MAP_TYPE, objSlcsBean.getKeyLable().toString()).apply();
        }

        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getKeyLable().toString().equalsIgnoreCase(objSlcsBean.getKeyLable())) {
                mList.get(i).setChecked(true);

                mAdapter.notifyItemChanged(position);
            } else {
                mList.get(i).setChecked(false);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    void getSettigs(String client_id) {
        dialog.show();
        objApi.getSettings(client_id).enqueue(
                new Callback<SettingMaster2>() {
                    @Override
                    public void onResponse(Call<SettingMaster2> call, Response<SettingMaster2> response) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        if (response.body() != null) {
                            SettingMaster2 objSettingMaster2 = response.body();

                            Data objData = objSettingMaster2.getData();
                            SharedPreferences.Editor editor = spf.edit();
                            editor.putString(AppConstants.SPF_CLIENT_SLC_EDIT_VIEW, objData.getClientSlcEditView());
                            editor.putString(AppConstants.SPF_CLIENT_SLC_LIST_VIEW, objData.getClientSlcListView());
                            editor.putString(AppConstants.SPF_CLIENT_SLC_POLE_IMAGE_VIEW, objData.getClientSlcPoleImageView());
                            editor.putString(AppConstants.SPF_CLIENT_SLC_POLE_ID, objData.getClientSlcPoleId());
                            editor.putString(AppConstants.SPF_CLIENT_SLC_POLE_ASSETS_VIEW, objData.getClientSlcPoleAssetsView());
                            editor.commit();

                            if (objSettingMaster2.getData().getClientSlcPoleId().equalsIgnoreCase("No")) {
                                sbCapturePoleID.setChecked(false);
                                sbCapturePoleID.setEnabled(false);
                                sbCapturePoleID.setAlpha(0.70f);
                                spf.edit().putBoolean(AppConstants.SPF_POLE_ID_VISIBILITY, false).apply();
                            } else {
                                //spf.edit().putBoolean(AppConstants.SPF_POLE_ID_VISIBILITY, true).apply();
                                //sbCapturePoleID.setAlpha(1f);
                                sbCapturePoleID.setChecked(true);

                                sbCapturePoleID.setEnabled(false);
                                sbCapturePoleID.setAlpha(0.70f);

                                //This is commented because user change but we total depends on this api call....
                                if (spf.getBoolean(AppConstants.SPF_POLE_ID_VISIBILITY, true)) {
                                    sbCapturePoleID.setChecked(true);
                                    spf.edit().putBoolean(AppConstants.SPF_POLE_ID_VISIBILITY, true).apply();
                                    Log.i(AppConstants.TAG, "pole id :checked true");

                                    sbCapturePoleID.setEnabled(false);
                                    sbCapturePoleID.setAlpha(0.70f);
                                } else {
                                    sbCapturePoleID.setChecked(false);
                                    spf.edit().putBoolean(AppConstants.SPF_POLE_ID_VISIBILITY, false).apply();

                                    sbCapturePoleID.setEnabled(false);
                                    sbCapturePoleID.setAlpha(0.70f);
                                }
                            }

                            if (objSettingMaster2.getData().getClientSlcPoleAssetsView().equalsIgnoreCase("No")) {
                                sbOtherData.setChecked(false);
                                sbOtherData.setEnabled(false);
                                sbOtherData.setAlpha(0.70f);

                                btnEnglish.setChecked(false);
                                btnMatric.setChecked(true);
                                segmentMeasurementUnits.setEnabled(false);
                                segmentMeasurementUnits.setAlpha(0.70f);
                                btnEnglish.setEnabled(false);
                                btnMatric.setEnabled(false);

                                spf.edit().putBoolean(AppConstants.SPF_OTHER_DATA_VISIBILITY, false).apply();

                            } else {
                                sbOtherData.setEnabled(true);
                                sbOtherData.setAlpha(1f);



                                //This is commented because user change but we total depends on this api call....
                                if (spf.getBoolean(AppConstants.SPF_OTHER_DATA_VISIBILITY, true)) {
                                    sbOtherData.setChecked(true);
                                    spf.edit().putBoolean(AppConstants.SPF_OTHER_DATA_VISIBILITY, true).apply();
                                    Log.i(AppConstants.TAG, "other data :checked true");

                                    sbOtherData.setEnabled(false);
                                    sbOtherData.setAlpha(0.70f);
                                } else {
                                    sbOtherData.setChecked(false);
                                    spf.edit().putBoolean(AppConstants.SPF_OTHER_DATA_VISIBILITY, false).apply();
                                    Log.i(AppConstants.TAG, "other data :checked false");

                                    sbOtherData.setEnabled(false);
                                    sbOtherData.setAlpha(0.70f);
                                }

                                segmentMeasurementUnits.setAlpha(1f);
                                segmentMeasurementUnits.setEnabled(true);
                                btnEnglish.setEnabled(true);
                                btnMatric.setEnabled(true);

                                if (units.toString().equalsIgnoreCase("")) { //default
                                    btnEnglish.setChecked(false);
                                    btnMatric.setChecked(true);
                                    spf.edit().putString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC).apply();
                                } else if (units.toString().equalsIgnoreCase(AppConstants.SPF_UNITES_ENGLISH)) {
                                    btnEnglish.setChecked(true);
                                    btnMatric.setChecked(false);
                                    spf.edit().putString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_ENGLISH).apply();
                                    Util.addLog("English :checked");
                                } else if (units.toString().equalsIgnoreCase(AppConstants.SPF_UNITES_MATRIC)) {
                                    btnEnglish.setChecked(false);
                                    btnMatric.setChecked(true);
                                    spf.edit().putString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC).apply();
                                    Util.addLog("Matric :checked");
                                }

                            }
                        } else
                            Util.dialogForMessage(getActivity(), getActivity().getResources().getString(R.string.server_error));
                    }

                    @Override
                    public void onFailure(Call<SettingMaster2> call, Throwable t) {

                        if (dialog.isShowing())
                            dialog.dismiss();
                        //Toast.makeText(getActivity(), call.toString(), Toast.LENGTH_SHORT).show();
                        Util.dialogForMessage(getActivity(), t.getLocalizedMessage());

                        /*sbCapturePoleID.setEnabled(true);
                        sbCapturePoleID.setChecked(true);
                        sbOtherData.setEnabled(true);
                        sbOtherData.setChecked(true);
                        segmentMeasurementUnits.setEnabled(true);
                        btnEnglish.setEnabled(true);
                        btnMatric.setEnabled(true);*/
                    }
                }
        );
    }

    void dialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.do_you_want_to_logout))
                .setCancelable(true)
                .setTitle(getResources().getString(R.string.logout))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Bundle bundle=new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "SettingLogout");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                        Log.d(AppConstants.TAG, event_name+"SettingLogout");

                        mDatabase.deleteTableData(DBHelper.SLC_TABLE_NAME);
                        spf.edit().clear().apply();
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), SecurityCode.class));
                        dialogInterface.dismiss();
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manuallyc
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();
    }

    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment

        fragmentTransaction.replace(R.id.frm1, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new Util().dismissProgressDialog(dialog);
    }
}

    /*int selectedId = segmentMeasurementUnits.getCheckedRadioButtonId();
                                if (selectedId == R.id.btnEnglish) {
                                        Log.i(AppConstants.TAG, "English :checked");
                                        spf.edit().putString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_ENGLISH).apply();
                                        btnEnglish.setChecked(true);
                                        btnMatric.setChecked(false);
                                        //((MainActivity) getActivity()).settingAPICall();
                                        Util.addLog("English :checked");
                                        }else{
                                        Log.i(AppConstants.TAG,"Matric :checked");
                                        spf.edit().putString(AppConstants.SPF_UNITS,AppConstants.SPF_UNITES_MATRIC).apply();
                                        btnMatric.setChecked(true);
                                        btnEnglish.setChecked(false);
                                        //((MainActivity) getActivity()).settingAPICall();
                                        Util.addLog("Matric :checked");
                                        }*/




/* Extra code for map types

       MapTypePojo obj11 = new MapTypePojo();
        obj11.setValue(getString(R.string.google_map_terrian));
        obj11.setPosition(4);
        if (selectedMap.equalsIgnoreCase(getString(R.string.google_map_terrian)))
            obj11.setChecked(true);
        else
            obj11.setChecked(false);
        mList.add(obj11);

        MapTypePojo obj5 = new MapTypePojo();
        obj5.setValue(getString(R.string.open_street_cycle));
        obj5.setPosition(4);
        if (selectedMap.equalsIgnoreCase(getString(R.string.open_street_cycle)))
            obj5.setChecked(true);
        else
            obj5.setChecked(false);
        mList.add(obj5);

        MapTypePojo obj6 = new MapTypePojo();
        obj6.setValue(getString(R.string.open_street_humantarian));
        obj6.setPosition(5);
        if (selectedMap.equalsIgnoreCase(getString(R.string.open_street_humantarian)))
            obj6.setChecked(true);
        else
            obj6.setChecked(false);
        mList.add(obj6);

        MapTypePojo obj7 = new MapTypePojo();
        obj7.setValue(getString(R.string.stamen_terriean));
        obj7.setPosition(6);
        if (selectedMap.equalsIgnoreCase(getString(R.string.stamen_terriean)))
            obj7.setChecked(true);
        else
            obj7.setChecked(false);
        mList.add(obj7);

        MapTypePojo obj8 = new MapTypePojo();
        obj8.setValue(getString(R.string.stamen_tonner));
        obj8.setPosition(7);
        if (selectedMap.equalsIgnoreCase(getString(R.string.stamen_tonner)))
            obj8.setChecked(true);
        else
            obj8.setChecked(false);
        mList.add(obj8);

        MapTypePojo obj9 = new MapTypePojo();
        obj9.setValue(getString(R.string.stamen_watercolor));
        obj9.setPosition(8);
        if (selectedMap.equalsIgnoreCase(getString(R.string.stamen_watercolor)))
            obj9.setChecked(true);
        else
            obj9.setChecked(false);
        mList.add(obj9);

        MapTypePojo obj10 = new MapTypePojo();
        obj10.setValue(getString(R.string.carto_db_dark));
        obj10.setPosition(9);
        if (selectedMap.equalsIgnoreCase(getString(R.string.carto_db_dark)))
            obj10.setChecked(true);
        else
            obj10.setChecked(false);
        mList.add(obj10);

        MapTypePojo obj11 = new MapTypePojo();
        obj11.setValue(getString(R.string.carto_db_light_positron));
        obj11.setPosition(10);
        if (selectedMap.equalsIgnoreCase(getString(R.string.carto_db_light_positron)))
            obj11.setChecked(true);
        else
            obj11.setChecked(false);
        mList.add(obj11);*/
/* void setMapTypeString() {

        listGoogleMapTypes.clear();

        String selecteType= spf.getString(AppConstants.SELCTED_MAP_TYPE,getString(R.string.google_map_satellite));
        Log.i(AppConstants.TAG,"Selected: "+selecteType.toString());

        GoogleMapTypes objGoogleMapTypes0 = new GoogleMapTypes();
        objGoogleMapTypes0.setMapypes(getResources().getString(R.string.google_map_standard));
        if(selecteType.toString().equalsIgnoreCase(getString(R.string.google_map_standard)))
            objGoogleMapTypes0.setSelected(true);
        listGoogleMapTypes.add(objGoogleMapTypes0);

        GoogleMapTypes objGoogleMapTypes1 = new GoogleMapTypes();
        objGoogleMapTypes1.setMapypes(getResources().getString(R.string.google_map_satellite));
        if(selecteType.toString().equalsIgnoreCase(getString(R.string.google_map_satellite)))
            objGoogleMapTypes1.setSelected(true);
        listGoogleMapTypes.add(objGoogleMapTypes1);

        GoogleMapTypes objGoogleMapTypes2 = new GoogleMapTypes();
        objGoogleMapTypes2.setMapypes(getResources().getString(R.string.google_map_hybride));
        if(selecteType.toString().equalsIgnoreCase(getString(R.string.google_map_hybride)))
            objGoogleMapTypes2.setSelected(true);
        listGoogleMapTypes.add(objGoogleMapTypes2);

        GoogleMapTypes objGoogleMapTypes3 = new GoogleMapTypes();
        objGoogleMapTypes3.setMapypes(getResources().getString(R.string.open_street_standard));
        if(selecteType.toString().equalsIgnoreCase(getString(R.string.open_street_standard)))
            objGoogleMapTypes3.setSelected(true);
        listGoogleMapTypes.add(objGoogleMapTypes3);

    }*/
//used contains beacuase of code have hyphen, extra region code or +b charactor
       /* if(     langCode.toString().contains(AppConstants.LANGUAGE_CODE_ENGLISH) ||
                langCode.toString().contains(AppConstants.LANGUAGE_CODE_SPANISH) ||
                langCode.toString().contains(AppConstants.LANGUAGE_CODE_PORTUGUES)
          ){

            if(langCode.toString().contains(AppConstants.LANGUAGE_CODE_SPANISH)){
                selectLang= getString(R.string.spanins);
            }else if( langCode.toString().contains(AppConstants.LANGUAGE_CODE_PORTUGUES)){
                selectLang= getString(R.string.portuguese);
            }else{
                selectLang= getString(R.string.english);
            }

        }else{
            selectLang=getString(R.string.english);
        }*/

         /*   for (int i = 0; i < objList.size(); i++) {
            if (isSelectMapType) {
                if (objList.get(i).getValue().equalsIgnoreCase(selectedMap)) {
                    selectedIndex = i;
                    break;
                }
            } else {
                if (objList.get(i).getValue().equalsIgnoreCase(selectLang)) {
                    selectedIndex = i;
                    break;
                }
            }
        }*/ // selectedMap = spf.getString(AppConstants.SELCTED_MAP_TYPE, getString(R.string.google_map_satellite));
//selectLang = spf.getString(AppConstants.SELECTED_LANG_TYPE, getString(R.string.english));
//  selectLang = spf.getString(AppConstants.SELECTED_LANG_TYPE, getString(R.string.english));