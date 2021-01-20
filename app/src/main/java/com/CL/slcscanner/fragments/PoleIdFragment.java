package com.CL.slcscanner.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.CL.slcscanner.Activities.LoginActivity;
import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.ClientAssets.Datum;
import com.CL.slcscanner.Pojo.CommonResponse;
import com.CL.slcscanner.Pojo.CommonResponse2;
import com.CL.slcscanner.Pojo.Login.LoginResponse;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.Util;
import com.CL.slcscanner.Activities.MainActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.CL.slcscanner.Utils.AppConstants.SPF_UNITES_MATRIC;
import static com.CL.slcscanner.Utils.Util.getRequestBody;

/**
 * Created by vrajesh on 2/24/2018.
 */

public class PoleIdFragment extends Fragment {

    @BindView(R.id.ivPoleIdBg)
    ImageView ivPoleIdBg;

    @BindView(R.id.btnConfirmPoleId)
    Button btnConfirmPoleId;

    @BindView(R.id.btnCanclePoleId)
    Button btnCanclePoleId;

    @BindView(R.id.edtPoleId)
    EditText edtPoleId;

    @BindView(R.id.btnPoleBack)
    ImageView btnPoleBack;

    @BindView(R.id.cbNone)
    CheckBox cbNone;

    @BindView(R.id.llPoleBack)
    LinearLayout llPoleBack;

    @BindView(R.id.tvBackTitlePoleId)
    TextView tvBackTitlePoleId;

    View view;
    Bundle mBundle;
    Double Lat, Long;

    FragmentManager fm;

    String slcId, macId, poleId;

    SharedPreferences spf;
    BottomNavigationView objBottomNavigationView;

    API objApi;
    ProgressDialog dialog;
    String units;
    ArrayList<Datum> mList;

    String address;

    AlertDialog loginAlertDialog;
    EditText etUsername;
    EditText etPass;
    String GlobalSLCId_PoleID = "";
    ProgressDialog loginProgressDialog;
    boolean isCopyFromPrevious;
    String client_id;
    String user_id;


    String isPoleCompulsary;
    String isCameraPreview;
    String isEditVisible;
    String isAssetViewVisible;

    ProgressDialog dialogSaveData;

    Util objUtil;

    ProgressDialog dialog_save;

    String GlobalSLCId = "";
    String version;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_pole_id, null);
        init();
        return view;
    }

    void init() {
        ButterKnife.bind(this, view);
        //edtPoleId.setText("123456");

        Glide.with(getActivity()).load(R.drawable.bg_1_1242_2208).into(ivPoleIdBg);
        objBottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        //objBottomNavigationView.setSelectedItemId(R.id.action_pole);

        objUtil = new Util();
        getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
        getActivity().findViewById(R.id.llNodeType).setVisibility(View.GONE);
        //Util.showKeyobard(getActivity());

        mBundle = getArguments();
        fm = getFragmentManager();

        dialog_save = new ProgressDialog(getActivity());
        dialog_save.setMessage(getResources().getString(R.string.saving));
        dialog_save.setCancelable(false);

        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        isPoleCompulsary = spf.getString(AppConstants.SPF_CLIENT_SLC_POLE_ID, "No");
        isCameraPreview = spf.getString(AppConstants.SPF_CLIENT_SLC_POLE_IMAGE_VIEW, "Yes");
        isEditVisible = spf.getString(AppConstants.SPF_CLIENT_SLC_EDIT_VIEW, "Yes");
        isAssetViewVisible = spf.getString(AppConstants.SPF_CLIENT_SLC_POLE_ASSETS_VIEW, "Yes");

        //isCopyFromPrevious = spf.getBoolean(AppConstants.CopyFromPrevious, false);
        mList = new ArrayList<>();
        version = spf.getString(AppConstants.VERSION,"");

        units = spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC);
        Long = Double.valueOf(spf.getString(AppConstants.SPF_DRAG_LONGITUDE, "0.0"));
        Lat = Double.valueOf(spf.getString(AppConstants.SPF_DRAG_LATTITUDE, "0.0"));
        slcId = spf.getString(AppConstants.SPF_TEMP_SLCID, "");
        macId = spf.getString(AppConstants.SPF_TEMP_MACID, "");
        client_id = spf.getString(AppConstants.CLIENT_ID, "");
        user_id = spf.getString(AppConstants.USER_ID, "");

        /*if (isPoleCompulsary.equalsIgnoreCase("No"))
            cbNone.setVisibility(View.GONE);
        else
            cbNone.setVisibility(View.VISIBLE);*/

        if (isCopyFromPrevious) {
            poleId = spf.getString(AppConstants.SPF_TEMP_POLE_ID_COPY_FEATURE, "");
            units = spf.getString(AppConstants.SPF_UNITS_FOR_COPY_FEATURE, AppConstants.SPF_UNITES_MATRIC);
            mList.addAll(Util.gettAssetDataCopyFeatureSPF(spf));
        } else {
            poleId = spf.getString(AppConstants.SPF_TEMP_POLE_ID, "");
            units = spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC);
            mList.addAll(Util.gettAssetDataSPF(spf));
        }
        address = spf.getString(AppConstants.ADDRESS, "");
        edtPoleId.setText(poleId);

//        if(cbNone.isChecked())

        objApi = new SLCScanner().networkCall();
        dialog = new ProgressDialog(getActivity());

        dialog.setMessage(getResources().getString(R.string.saving));

        dialog.setCancelable(false);

        dialogSaveData = new ProgressDialog(getActivity());
        dialogSaveData.setMessage(getResources().getString(R.string.saving));

        if (isAssetViewVisible.equalsIgnoreCase("No") || !spf.getBoolean(AppConstants.SPF_OTHER_DATA_VISIBILITY, true))
            dialog.setMessage(getResources().getString(R.string.saving));
        else
            dialog.setMessage(getResources().getString(R.string.loading));

        dialogSaveData.setCancelable(false);

        /*Fragment f = getFragmentManager().findFragmentById(R.id.frm1);
        if (f instanceof PoleIdFragment) {
            BottomNavigationView objBottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
            objBottomNavigationView.setSelectedItemId(R.id.action_pole);
        }*/

        if (getActivity() != null) {
            ((MainActivity) getActivity()).selectPole(false);
            ((MainActivity) getActivity()).selectScan(true);
            ((MainActivity) getActivity()).selectSetting(false);
            ((MainActivity) getActivity()).selectMap(false);
        }

        /*if (isCameraPreview.equalsIgnoreCase("Yes"))
            tvBackTitlePoleId.setText(getResources().getString(R.string.camera));
        else
            tvBackTitlePoleId.setText(getResources().getString(R.string.address_title));*/


        tvBackTitlePoleId.setText(getResources().getString(R.string.note));


        boolean isChecked = spf.getBoolean(AppConstants.isNoneChecked, false);
        if (isChecked)
            cbNone.setChecked(true);
        else
            cbNone.setChecked(false);

        checkBoxUi(isChecked);

        edtPoleId.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // TODO do something
                    handled = true;
                    confirm();
                }
                return handled;
            }
        });

        btnConfirmPoleId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbNone.isChecked())
                    confirm();
                else {
                    if (edtPoleId.getText().toString().trim().equals("")) {
                        Util.dialogForMessage(getActivity(), getResources().getString(R.string.please_enter_pole_id));
                        return;
                    } else {
                        if (Util.isInternetAvailable(getActivity()))
                            checkPOLEIDAPICall(edtPoleId.getText().toString());
                        else
                            Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
                    }
                }
            }
        });

        //btnConfirmPoleId.setOnTouchListener(Util.colorFilter());
        //btnCanclePoleId.setOnTouchListener(Util.colorFilter());

        llPoleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String note_option = spf.getString(AppConstants.NOTES, "") + "#" + spf.getString(AppConstants.POLE_OPTION, "");

                //objBottomNavigationView.setSelectedItemId(R.id.action_scan);
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                Bundle mBundle = new Bundle();
                NoteFragment objNoteFragment = new NoteFragment();
                mBundle.putBoolean(AppConstants.ISVIEWONLY, false);
                mBundle.putString(AppConstants.FROM, "pole");
                mBundle.putString(AppConstants.NOTES_POLE_OPTION, note_option);

                objNoteFragment.setArguments(mBundle);

                objUtil.loadFragment(objNoteFragment, getActivity());

                /*if (isCameraPreview.equalsIgnoreCase("Yes"))
                    fragmentTransaction.replace(R.id.frm1, new CameraPreviewFragment());
                else
                    fragmentTransaction.replace(R.id.frm1, new AddressFragement());*/

                fragmentTransaction.commit(); // save the changes
            }
        });

        btnCanclePoleId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtPoleId.setText("");
                spf.edit().putString(AppConstants.SPF_TEMP_POLE_ID, "").apply();
            }
        });

        cbNone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkBoxUi(isChecked);
            }
        });

        spf.edit().putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, AppConstants.SPF_POLEID_FRAG).apply();

        edtPoleId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                spf.edit().putString(AppConstants.SPF_TEMP_POLE_ID, s.toString()).apply();
            }
        });
        loginAlertDialog = loginDialog(getActivity());
        GlobalSLCId_PoleID = spf.getString(AppConstants.SPF_LOGOUT_SLCID, "");

        if (poleId.toString().equalsIgnoreCase("None")) {
            cbNone.setChecked(true);
            edtPoleId.setText("");
        }
    }

    void checkBoxUi(boolean isChecked) {
        if (isChecked) {
            edtPoleId.setFocusable(false);
            edtPoleId.setAlpha(0.80f);
            edtPoleId.setText("");
            spf.edit().putBoolean(AppConstants.isNoneChecked, true).apply();
        } else {
            edtPoleId.setFocusableInTouchMode(true);
            edtPoleId.setAlpha(1f);
            spf.edit().putBoolean(AppConstants.isNoneChecked, false).apply();
        }
    }

    void checkPOLEIDAPICall(final String id) {

        dialog.show();
        objApi.checkPoleId(id, client_id, user_id).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {

                if (dialog.isShowing())
                    dialog.dismiss();

                if (response.body() != null) {
                    CommonResponse response1 = response.body();
                    if (response1.getStatus().toString().equals("success")) {
                        spf.edit().putString(AppConstants.SPF_TEMP_POLE_ID, id.toString()).apply();

                        //loadFragment(new SLCIdFragment());
                        confirm();

                        //Toast.makeText(getActivity(),""+response1.getAddress().toString(),Toast.LENGTH_LONG).show();
                        Util.addLog("Valid MAC ID: " + id);
                    } else {
                        Util.dialogForMessage(getActivity(), response1.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    void confirm() {

        Util.hideKeyboard(getActivity());

        if (Long != 0 && Lat != 0) {
            //mBundle.putFloat(AppConstants.BUNDLE_LONGITUDE, Long);
            //mBundle.putFloat(AppConstants.BUNDLE_LONGITUDE, Lat);

            if (!cbNone.isChecked()) {
                spf.edit().putBoolean(AppConstants.IS_NONE_CHECKED, false).apply();
                spf.edit().putString(AppConstants.SPF_TEMP_POLE_ID, edtPoleId.getText().toString().trim()).apply();
            } else {
                spf.edit().putBoolean(AppConstants.IS_NONE_CHECKED, true).apply();
                spf.edit().putString(AppConstants.SPF_TEMP_POLE_ID, "").apply();
            }

            if (isAssetViewVisible.equalsIgnoreCase("No")) {
                /*HashMap<String, String> map1 = new HashMap<>();
                for (int i = 0; i < mList.size(); i++) {
                    if (!mList.get(i).isStaticData()) {
                        map1.put(mList.get(i).getAttrKey(), "");
                    }
                }
                JSONObject json1 = new JSONObject(map1);
                Log.i(AppConstants.TAG, json1.toString());
                saveNewSLCData("No", "", address, json1.toString(), "No");*/

                String notes = spf.getString(AppConstants.NOTES_EDIT, "");
                String pole_option = spf.getString(AppConstants.NOTES_POLE_OPTION_EDIT, "");

                saveNewSLCData2(notes, pole_option);
            } else {

                if (spf.getBoolean(AppConstants.SPF_OTHER_DATA_VISIBILITY, true)) {
                    //spf.edit().putString(AppConstants.SPF_TEMP_POLE_ID, edtPoleId.getText().toString().trim()).apply();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    PoleDataEditFragment fragment = new PoleDataEditFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isNewData", true);
                    bundle.putBoolean("IS_FROM_MAP", false);
                    bundle.putString("ID", "");
                    bundle.putString("slcID", slcId);
                    bundle.putString("poleId", spf.getString(AppConstants.SPF_TEMP_POLE_ID, ""));
                    bundle.putDouble("Lat", Lat);
                    bundle.putDouble("Long", Long);
                    bundle.putString("MacId", macId);
                    bundle.putBoolean(AppConstants.isfromNote, false);

                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.frm1, fragment);
                    fragmentTransaction.commit(); // save the changes

                } else {
                    HashMap<String, String> map1 = new HashMap<>();
                    for (int i = 0; i < mList.size(); i++) {
                        if (!mList.get(i).isStaticData()) {
                            map1.put(mList.get(i).getAttrKey(), "");
                        }
                    }
                    JSONObject json1 = new JSONObject(map1);
                    Log.i(AppConstants.TAG, json1.toString());


                   /* ArrayList<com.CL.slcscanner.Pojo.Login.Datum> mClientType = Util.getClientTypeList(spf);
                    if (mClientType.size() > 0)
                        mClientType.remove(0);*/

                    //temp node type disable
                    /*String node_type = spf.getString(AppConstants.SELECTED_NODE_TYPE_SAVE, mClientType.get(1).getClientType());

                    if (node_type
                            .equalsIgnoreCase(getString(R.string.unknown)))
                        node_type = "";*/


                    saveNewSLCData("No", "", address, json1.toString(), "No","");//node_type
                }
            }
            Util.hideKeyboard(getActivity());

        } else
            Util.dialogForMessage(getActivity(), getResources().getString(R.string.location_msg_violate));

    }

    void saveNewSLCData(String skip, String DOI, String address, String assets, String copyAssets, String node_type) {

        String pole_option = spf.getString(AppConstants.POLE_OPTION, "");
        String notes = spf.getString(AppConstants.NOTES, "");

        ArrayList<Datum> mListNotes=new ArrayList<>();
        mListNotes.addAll(Util.getNotesData(pole_option));
        HashMap<String, RequestBody> mapNote = new HashMap<>();
        for (int i = 0; i < mListNotes.size(); i++) {
            if (!mListNotes.get(i).isStaticData()) {
                if (mListNotes.get(i).isNoteData()) {
                    mapNote.put(mListNotes.get(i).getAttrKey(), getRequestBody(mListNotes.get(i).getSelectected()));
                }
            }
        }

        File root = Environment.getExternalStorageDirectory();
        final File fileCamerra = new File(root + "/SLCScanner/Preview.jpg");

        MultipartBody.Part filePart = null;
        if (fileCamerra.exists()) {
            filePart = MultipartBody.Part.createFormData("pole_image",
                    fileCamerra.getName(), RequestBody.create(MediaType.parse("image/*"),
                            fileCamerra));
        }

        dialogSaveData.show();
        objApi.saveNewSLCDataWithFile(
                Util.getRequestBody(spf.getString(AppConstants.CLIENT_ID, "")),
                Util.getRequestBody(spf.getString(AppConstants.USER_ID, "")),
                Util.getRequestBody(macId),
                Util.getRequestBody(slcId),
                Util.getRequestBody(edtPoleId.getText().toString().trim()),
                Util.getRequestBody(spf.getString(AppConstants.SPF_DRAG_LATTITUDE, "0.0")),
                Util.getRequestBody(spf.getString(AppConstants.SPF_DRAG_LONGITUDE, "0.0")),
                Util.getRequestBody(skip),
                Util.getRequestBody(units),
                Util.getRequestBody("Android"),
                Util.getRequestBody(address),
                Util.getRequestBody(DOI),
                Util.getRequestBody(assets),
                Util.getRequestBody(GlobalSLCId_PoleID),
                Util.getRequestBody(copyAssets),
                filePart,
                Util.getRequestBody(notes),
                mapNote,
                Util.getRequestBody("")//node_type

        ).enqueue(new Callback<CommonResponse2>() {
            @Override
            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {
                if (dialogSaveData.isShowing())
                    dialogSaveData.dismiss();
                if (response.body() != null) {
                    CommonResponse2 response1 = response.body();
                    if (response1.getStatus().equals("success")) {
                        //Util.dialogForMessage(getActivity(), response1.getAddress());

                        SharedPreferences.Editor edt = spf.edit();
                        edt.putString(AppConstants.SPF_TEMP_SLCID, "");
                        edt.putString(AppConstants.SPF_TEMP_MACID, "");
                        edt.putString(AppConstants.SPF_TEMP_POLE_ID, "");
                        edt.putString(AppConstants.SPF_DRAG_LONGITUDE, "0.0");
                        edt.putString(AppConstants.SPF_DRAG_LATTITUDE, "0.0");
                        edt.putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, "");
                        edt.putString(AppConstants.SPF_POLE_CURRENT_FRAG, "");
                        edt.putString(AppConstants.ADDRESS, "");
                        edt.putBoolean(AppConstants.SPF_ISFROMMAP, false);
                        edt.putString(AppConstants.SPF_ID, "");
                        edt.putString(AppConstants.SPF_TEMP_POLE_ID_COPY_FEATURE, "");
                        edt.putBoolean(AppConstants.CopyFromPrevious, false);
                        edt.remove(AppConstants.isNoneChecked);
                        edt.apply();

                        dialog(response1.getMsg());

                        Util.addLog("pole id ui: Data Saved Successfully");

                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, "").apply();

                    } else if (response1.getStatus().equalsIgnoreCase("logout")) {
                        Log.i(AppConstants.TAG, response1.getMsg());

                        spf.edit().putBoolean(AppConstants.ISLOGGEDIN, false).apply();

                        etPass.setText("");
                        etUsername.setText("");
                        loginAlertDialog.show();
                        GlobalSLCId_PoleID = response1.getSLCID();
                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, GlobalSLCId_PoleID).apply();

                        Toast.makeText(getActivity(), response1.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.i(AppConstants.TAG, response1.getMsg());
                        Util.addLog("Address Fragment UI" + response1.getMsg());
                    } else {
                        Util.dialogForMessage(getActivity(), response1.getMsg());
                        Util.addLog("pole id ui: Data Saved Failed");
                    }
                } else
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.server_error));
            }

            @Override
            public void onFailure(Call<CommonResponse2> call, Throwable t) {
                if (dialogSaveData.isShowing())
                    dialogSaveData.dismiss();
                try {
                    Util.addLog("Edit Pole Data: New Data saved Failed - Network / Server Error");
                    Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                } catch (Exception e) {
                }
            }
        });
    }


    void saveNewSLCData2(String notes, String pole_option) {

        String DOI = "";
        String address = "";

        String clientId = spf.getString(AppConstants.CLIENT_ID, "");
        String userid = spf.getString(AppConstants.USER_ID, "");
        GlobalSLCId = spf.getString(AppConstants.SPF_LOGOUT_SLCID, "");
        units = spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC);
        String macID = spf.getString(AppConstants.SPF_TEMP_MACID, "");
        slcId = spf.getString(AppConstants.SPF_TEMP_SLCID, "");
        address = spf.getString(AppConstants.ADDRESS, "");

        /*ArrayList<com.CL.slcscanner.Pojo.Login.Datum> mClientType = Util.getClientTypeList(spf);
        if (mClientType.size() > 0)
            mClientType.remove(0);*/

        //temp node type disable
        /*String node_type = spf.getString(AppConstants.SELECTED_NODE_TYPE_SAVE, mClientType.get(1).getClientType());

        if (node_type
                .equalsIgnoreCase(getString(R.string.unknown)))
            node_type = "";
*/
        //add notes array

        HashMap<String, String> map = new HashMap<>();
        HashMap<String, RequestBody> map3 = new HashMap<>();

        HashMap<String, String> map1 = new HashMap<>();
        for (int i = 0; i < mList.size(); i++) {
            if (!mList.get(i).isStaticData()) {
                map1.put(mList.get(i).getAttrKey(), "");
            }
        }

        //Assests
        JSONObject jsonAssets = new JSONObject(map1);
        Log.i("--**Assets: ", jsonAssets.toString());

        ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> mListTemp = new ArrayList<>();

        mListTemp.addAll(Util.getNotesData(pole_option));

        for (int i = 0; i < mListTemp.size(); i++) {
            if (mListTemp.get(i).getSelectected().toString().equals("") || mListTemp.get(i).getSelectected().toString().equalsIgnoreCase("None")) {
                map3.put(mListTemp.get(i).getAttrKey().toString(), getRequestBody(" "));
                //Log.i(AppConstants.TAG2, "BLANK Notes" + mList.get(i).getSelectected() + " " + i);
            } else {
                map3.put(mListTemp.get(i).getAttrKey(), getRequestBody(mListTemp.get(i).getSelectected()));
                //Log.i(AppConstants.TAG2, "FILL Notes" + mList.get(i).getSelectected() + " " + i);
            }
        }

        //display NOTES
        JSONObject json1 = new JSONObject(map3);
        Log.i("--**Notes : ", json1.toString());

        File root = Environment.getExternalStorageDirectory();
        final File fileCamerra = new File(root + "/SLCScanner/Preview.jpg");

        MultipartBody.Part filePart = null;
        if (fileCamerra.exists()) {
            filePart = MultipartBody.Part.createFormData("pole_image",
                    fileCamerra.getName(), RequestBody.create(MediaType.parse("image/*"),
                            fileCamerra));
        }

        units = spf.getString(AppConstants.SPF_UNITS, SPF_UNITES_MATRIC);

        dialog_save.show();
        objApi.saveNewSLCDataWithFile2(
                getRequestBody(clientId),
                getRequestBody(userid),
                getRequestBody(macID),
                getRequestBody(slcId),
                getRequestBody(""),
                getRequestBody(String.valueOf(Lat)),
                getRequestBody(String.valueOf(Long)),
                getRequestBody("No"),
                getRequestBody(units),
                getRequestBody("Android"),
                getRequestBody(address),
                getRequestBody(DOI),
                getRequestBody(GlobalSLCId),
                getRequestBody("No"),
                filePart,
                getRequestBody(notes),
                map3,
                getRequestBody(jsonAssets.toString()),
                getRequestBody(""),//node_type
                getRequestBody(version)
        ).enqueue(new Callback<CommonResponse2>() {
            @Override
            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {

                if (response.body() != null) {
                    CommonResponse2 response1 = response.body();
                    if (response1.getStatus().equalsIgnoreCase("success")) {
                        if (dialog_save.isShowing())
                            dialog_save.dismiss();

                        //Util.dialogForMessage(getActivity(), response1.getAddress());

                        SharedPreferences.Editor edt = spf.edit();
                        edt.putString(AppConstants.SPF_TEMP_SLCID, "");
                        edt.putString(AppConstants.SPF_TEMP_MACID, "");
                        edt.putString(AppConstants.SPF_TEMP_POLE_ID, "");
                        edt.putString(AppConstants.SPF_DRAG_LONGITUDE, "0.0");
                        edt.putString(AppConstants.SPF_DRAG_LATTITUDE, "0.0");
                        edt.putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, "");
                        edt.putString(AppConstants.SPF_POLE_CURRENT_FRAG, "");
                        edt.putString(AppConstants.ADDRESS, "");
                        edt.putBoolean(AppConstants.SPF_ISFROMMAP, false);
                        edt.putString(AppConstants.SPF_ID, "");
                        edt.putBoolean(AppConstants.CopyFromPrevious, false);
                        edt.putString(AppConstants.SPF_UNITS_FOR_COPY_FEATURE, "");
                        edt.remove(AppConstants.isNoneChecked);
                        edt.remove(AppConstants.POLE_OPTION);
                        edt.remove(AppConstants.NOTES_POLE_OPTION);
                        edt.remove(AppConstants.NOTES);
                        edt.remove(AppConstants.NOTES_POLE_OPTION_EDIT);
                        edt.remove(AppConstants.NOTES_EDIT);

                        edt.apply();

                        dialog(response1.getMsg());

                        Util.addLog("Edit Pole Data: New Data saved Successfully");

                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, "").apply();
                        Util.deletePreviewFile();

                    } else if (response1.getStatus().equalsIgnoreCase("logout")) {

                        if (dialog_save.isShowing())
                            dialog_save.dismiss();

                        Log.i(AppConstants.TAG, response1.getMsg());

                        spf.edit().putBoolean(AppConstants.ISLOGGEDIN, false).apply();

                        etPass.setText("");
                        etUsername.setText("");
                        loginAlertDialog.show();

                        GlobalSLCId = response1.getSLCID();
                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, response1.getSLCID()).apply();

                        Toast.makeText(getActivity(), response1.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.i(AppConstants.TAG, response1.getMsg());
                        Util.addLog("Save new data error" + response1.getMsg());

                    } else if (response1.getStatus().equalsIgnoreCase("error")) {
                        if (dialog_save.isShowing())
                            dialog_save.dismiss();
                        Util.dialogForMessage(getActivity(), response1.getMsg());
                        Util.addLog("Edit Pole Data: New Data saved Failed");
                    } else
                        Util.dialogForMessage(getActivity(), getResources().getString(R.string.server_error));

                } else {
                    if (dialog_save.isShowing())
                        dialog_save.dismiss();
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.server_error));
                }
            }

            @Override
            public void onFailure(Call<CommonResponse2> call, Throwable t) {
                if (dialog_save.isShowing())
                    dialog_save.dismiss();
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                Util.addLog("Edit Pole Data: New Data saved Failed - Network / Server Error");
            }
        });
    }


    void sendDataToCameraPreview(String address, String assests) {
        Bundle mBundle1 = new Bundle();

        mBundle1.putString(AppConstants.BUNDLE_BACK_NAV_TITTLE, "POLE ID");
        mBundle1.putBoolean(AppConstants.BUNDLE_IS_FROM_MAP, false);
        mBundle1.putBoolean(AppConstants.BUNDLE_ISNEWDATA, true);
        mBundle1.putString(AppConstants.BUNDLE_ID, "");

        mBundle1.putString(AppConstants.BUNDLE_MACID, macId);
        mBundle1.putString(AppConstants.BUNDLE_SLCID, slcId);
        mBundle1.putString(AppConstants.BUNDLE_POLE_ID, poleId);

        mBundle1.putString(AppConstants.BUNDLE_SKIPP, "No");
        mBundle1.putString(AppConstants.BUNDLE_DOI, "");
        mBundle1.putString(AppConstants.BUNDLE_ADDRESS, address);

        mBundle1.putString(AppConstants.BUNDLE_ASSETS, assests);

        mBundle1.putDouble(AppConstants.BUNDLE_LATTITUDE, Double.parseDouble(spf.getString(AppConstants.SPF_DRAG_LATTITUDE, "0.0")));
        mBundle1.putDouble(AppConstants.BUNDLE_LONGITUDE, Double.parseDouble(spf.getString(AppConstants.SPF_DRAG_LONGITUDE, "0.0")));

        CameraPreviewFragment fragment = new CameraPreviewFragment();
        fragment.setArguments(mBundle1);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.replace(R.id.frm1, fragment);
        fragmentTransaction.commit();
    }


    void dialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg)
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.new_scan), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        objUtil.loadFragment(new ScanMacId(), getActivity());
                        if (getActivity() != null) {
                            ((MainActivity) getActivity()).selectScan(true);
                            ((MainActivity) getActivity()).selectPole(false);
                            ((MainActivity) getActivity()).selectSetting(false);
                            ((MainActivity) getActivity()).selectHelp(false);
                        }
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.list), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                objUtil.loadFragment(new PoleDataFragment(), getActivity());

                if (getActivity() != null) {
                    ((MainActivity) getActivity()).selectScan(false);
                    ((MainActivity) getActivity()).selectPole(true);
                    ((MainActivity) getActivity()).selectSetting(false);
                    ((MainActivity) getActivity()).selectHelp(false);
                }
            }
        });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setCancelable(false);
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


    private AlertDialog loginDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dilog_login, null);
        etUsername = view.findViewById(R.id.etUsername);
        etPass = view.findViewById(R.id.etPass);

        Button btSend = (Button) view.findViewById(R.id.btSend);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etUsername.getText().toString().equals("")) {
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.empty_username));
                } else if (etPass.getText().toString().equals("")) {
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.empty_password));
                } else {
                    //login call
                    if (Util.isInternetAvailable(getActivity())) {
                        sendLoginDetailToSever(etUsername.getText().toString(), etPass.getText().toString(), 0.0, 0.0);
                    } else {
                        Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
                    }

                }
            }
        });

        etPass.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // TODO do something
                    handled = true;
                    if (etUsername.getText().toString().equals("")) {
                        Util.dialogForMessage(getActivity(), getResources().getString(R.string.empty_username));
                    } else if (etPass.getText().toString().equals("")) {
                        Util.dialogForMessage(getActivity(), getResources().getString(R.string.empty_password));
                    } else {
                        //login call
                        if (Util.isInternetAvailable(getActivity())) {
                            sendLoginDetailToSever(etUsername.getText().toString(), etPass.getText().toString(), 0.0, 0.0);
                        } else {
                            Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
                        }
                    }
                }
                return handled;
            }
        });

        builder.setCancelable(false);
        builder.setView(view);
        return builder.create();
    }

    void sendLoginDetailToSever(String username, String password, Double lattitude, Double longitude) {
        String cleintid = spf.getString(AppConstants.CLIENT_ID, null);
        String userId = spf.getString(AppConstants.USER_ID, null);
        loginProgressDialog = new ProgressDialog(getActivity());
        loginProgressDialog.setMessage(getResources().getString(R.string.loading));
        loginProgressDialog.setCancelable(false);
        loginProgressDialog.show();

        Call<LoginResponse> objCheckUnieCall = objApi.checkUserLogin(
                cleintid,
                userId,
                username,
                password,
                lattitude,
                longitude,
                "Android",
                spf.getString(AppConstants.VERSION,"")
                );

        objCheckUnieCall.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                LoginResponse objCommonResponse = response.body();
                if (loginProgressDialog.isShowing()) {
                    loginProgressDialog.dismiss();
                }
                if (response.body() != null) {
                    if (objCommonResponse.getStatus().toString().equalsIgnoreCase("success")) {
                        spf.edit().putBoolean(AppConstants.ISLOGGEDIN, true).apply();
                        Toast.makeText(getActivity(), objCommonResponse.getMsg().toString(), Toast.LENGTH_SHORT).show();
                        Util.hideKeyboard(getActivity());
                        Util.addLog("Address Fragment UI : Login Successful");

                        if (loginAlertDialog.isShowing())
                            loginAlertDialog.dismiss();

                    } else if (objCommonResponse.getStatus().toString().equalsIgnoreCase("error")) {
                        Util.dialogForMessage(getActivity(), objCommonResponse.getMsg().toString());
                        Util.addLog("Address Fragment UI: Login failed");
                    } else if (objCommonResponse.getStatus().toString().equalsIgnoreCase("-1")) {
                        //Util.dialogForMessage(getActivity(), objCommonResponse.getMsg().toString());
                        Util.dialogForMessagePlayStoreNavigate(getActivity(),objCommonResponse.getMsg().toString());
                    } else if (objCommonResponse.getStatus().toString().equalsIgnoreCase("0")) {
                        Util.dialogForMessageNavigate(getActivity(), objCommonResponse.getMsg().toString());
                    } else
                        Util.dialogForMessage(getActivity(), getResources().getString(R.string.server_error));
                } else
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.server_error));
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (loginProgressDialog.isShowing()) {
                    loginProgressDialog.dismiss();
                }
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
            }
        });
    }
}