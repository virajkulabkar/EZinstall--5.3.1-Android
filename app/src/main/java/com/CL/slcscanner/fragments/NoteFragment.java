package com.CL.slcscanner.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.CL.slcscanner.Activities.MainActivity;
import com.CL.slcscanner.Adapter.NoteAdapter;
import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.CommonResponse2;
import com.CL.slcscanner.Pojo.Note.Datum;
import com.CL.slcscanner.Pojo.Note.NoteMaster;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.CL.slcscanner.Utils.AppConstants.SPF_UNITES_MATRIC;
import static com.CL.slcscanner.Utils.Util.getRequestBody;

public class NoteFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.edtNotes)
    EditText edtNotes;

    @BindView(R.id.rvNote)
    RecyclerView recyclerView;

    @BindView(R.id.ivNext)
    Button ivNext;

    @BindView(R.id.btnPoleMap)
    ImageView btnPoleMap;

    @BindView(R.id.ivSkipp)
    Button ivSkipp;

    @BindView(R.id.tbTitleLine1)
    TextView tbTitleLine1;

    @BindView(R.id.ivUpdate)
    Button ivUpdate;

    NoteAdapter adapter;
    ArrayList<Datum> objNote;
    ProgressDialog objProgressDialog;

    View view;
    API objApi;
    Util objUtil;

    SharedPreferences spf;
    String notes;

    String isCameraPreview;
    Bundle objBundle;
    boolean isViewOnly = false;

    String from;
    String note_option_whole_data;
    String note_option_json;

    String ID = "", slcId, macID, poleId, units, address;
    Double Long, Lat;
    boolean isFromMap, isNewData = false;

    ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> mList;
    Double lattitude, longitude;

    String assets;

    ProgressDialog dialog;

    String clientId, userid;

    AlertDialog loginAlertDialog;
    ProgressDialog loginDialog;
    EditText etUsername;
    EditText etPass;

    String GlobalSLCId = "";
    ProgressDialog dialog_save;
    ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> mListAssets;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notes, null);
        init();
        return view;
    }

    void init() {
        ButterKnife.bind(this, view);
        objBundle = getArguments();
        if (objBundle != null) {
            isViewOnly = objBundle.getBoolean(AppConstants.ISVIEWONLY, false);
            from = objBundle.getString(AppConstants.FROM, "");
            note_option_whole_data = objBundle.getString(AppConstants.NOTES_POLE_OPTION, "");
        }
        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        mList = new ArrayList<>();
        objUtil = new Util();
        objNote = new ArrayList<>();
        adapter = new NoteAdapter(getActivity(), objNote, isViewOnly);

        mListAssets = new ArrayList<>();

        lattitude = Double.valueOf(spf.getString(AppConstants.SPF_DRAG_LATTITUDE, "0.0"));
        longitude = Double.valueOf(spf.getString(AppConstants.SPF_DRAG_LONGITUDE, "0.0"));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        dialog_save = new ProgressDialog(getActivity());
        dialog_save.setMessage(getResources().getString(R.string.saving));
        dialog_save.setCancelable(false);

        //spf.edit().putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, AppConstants.SPF_NOTE_FRAGMENT).apply();

        //if (isCopyFromPrevious) {
        //    poleId = spf.getString(AppConstants.SPF_TEMP_POLE_ID_COPY_FEATURE, "");
        //   units = spf.getString(AppConstants.SPF_UNITS_FOR_COPY_FEATURE, AppConstants.SPF_UNITES_MATRIC);
        //   mList.addAll(Util.gettAssetDataCopyFeatureSPF(spf));
        //} else {
        poleId = spf.getString(AppConstants.SPF_TEMP_POLE_ID, "");
        slcId = spf.getString(AppConstants.SPF_TEMP_SLCID, "");
        macID = spf.getString(AppConstants.SPF_TEMP_MACID, "");
        units = spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC);
        mList.addAll(Util.gettAssetDataSPF(spf));
        //}

        //this default values


        objProgressDialog = new ProgressDialog(getActivity());
        objProgressDialog.setMessage(getResources().getString(R.string.loading));
        objProgressDialog.setCancelable(false);

        objApi = new SLCScanner().networkCall();

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getResources().getString(R.string.loading));
        dialog.setCancelable(false);

        isCameraPreview = spf.getString(AppConstants.SPF_CLIENT_SLC_POLE_IMAGE_VIEW, "Yes");

        if (from.equals("display")) {
            edtNotes.setFocusable(false);
            getNoteDataForDisplay();
            ivNext.setVisibility(View.GONE);
            ivSkipp.setVisibility(View.GONE);
            ivUpdate.setVisibility(View.GONE);

            if (objBundle != null) {
                ID = objBundle.getString("ID");
            }

        } else if (from.equals("edit")) {
            if (objBundle != null) {
                ID = objBundle.getString("ID");
                isFromMap = objBundle.getBoolean("IS_FROM_MAP");
                isNewData = objBundle.getBoolean("isNewData");
                slcId = objBundle.getString("slcID");
                macID = objBundle.getString("MacId");
                poleId = objBundle.getString("poleId");
                Long = objBundle.getDouble("Long");
                Lat = objBundle.getDouble("Lat");

                lattitude = Lat;
                longitude = Long;
            }
            getNoteDataForDisplay();
            ivNext.setVisibility(View.VISIBLE);
            ivSkipp.setVisibility(View.GONE);
            ivUpdate.setVisibility(View.GONE);

        } else if (from.equalsIgnoreCase("pole") || from.equalsIgnoreCase("pole_data_edit")) {
            ivNext.setVisibility(View.VISIBLE);
            ivSkipp.setVisibility(View.VISIBLE);
            ivUpdate.setVisibility(View.GONE);
            edtNotes.setEnabled(true);
            getNoteDataForDisplay();
        } else {
            ivNext.setVisibility(View.VISIBLE);
            ivSkipp.setVisibility(View.VISIBLE);
            ivUpdate.setVisibility(View.GONE);
            getNoteData();
            edtNotes.setEnabled(true);
        }

        ivNext.setOnClickListener(this);
        ivSkipp.setOnClickListener(this);
        ivUpdate.setOnClickListener(this);
        btnPoleMap.setOnClickListener(this);

       /*if (isCameraPreview.equalsIgnoreCase("Yes"))
            tbTitleLine1.setText(getResources().getString(R.string.camera));
        else
            tbTitleLine1.setText(getResources().getString(R.string.address_title));*/

    }

    void getNoteDataForDisplay() {
        try {
            String[] arrOfStr = note_option_whole_data.split("#");
            notes = arrOfStr[0];
            edtNotes.setText(notes);

            note_option_json = arrOfStr[1];

            JSONArray jsonArray = new JSONArray(note_option_json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                //Log.i(AppConstants.TAG, object.toString());

                String value = object.getString("value");
                String keybool = object.getString("key");
                String keytext = keybool + "_text";

                String ValueBool = object.getString(keybool);
                String ValueText = object.getString(keytext);

                boolean flagBool = false;
                Datum obj = new Datum();
                obj.setKey(keybool);
                obj.setValue(value);

                if (ValueBool.equals("0"))
                    flagBool = false;
                else
                    flagBool = true;

                obj.setChecked(flagBool);
                obj.setNotesEach(ValueText);
                objNote.add(obj);

            }
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getNoteData() {
        objProgressDialog.show();
        objApi.getNotes().enqueue(new Callback<NoteMaster>() {
            @Override
            public void onResponse(Call<NoteMaster> call, Response<NoteMaster> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        NoteMaster obj = response.body();
                        List<Datum> objList = obj.getData();
                        for (int i = 0; i < objList.size(); i++) {
                            objNote.add(objList.get(i));
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else
                    objUtil.responseHandle(getActivity(), response.code(), response.errorBody());

                if (objProgressDialog.isShowing())
                    objProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<NoteMaster> call, Throwable t) {
                if (objProgressDialog.isShowing())
                    objProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivSkipp:
                //spf.edit().remove(AppConstants.NOTES).apply();
                //spf.edit().remove(AppConstants.POLE_OPTION).apply();

                try {
                    collectNotes(false, true, false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ivNext:
                try {
                    if (from.toString().equals("edit"))
                        collectNotes(true, false, false);
                    else
                        collectNotes(false, false, false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ivUpdate:
                try {
                    collectNotes(true, false, false);
                    //collectNotes(false, false,true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btnPoleMap:
                try {
                    collectNotes(false, false, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    void collectNotes(boolean isForEdit, boolean isSkip, boolean isFromBack) throws JSONException {

        JSONArray poleOption = new JSONArray();
        for (int i = 0; i < objNote.size(); i++) {

            String keyBool = objNote.get(i).getKey();
            String keyText = keyBool + "_text";

            String valueNoteEach = "";

            try {
                if (objNote.get(i).getNotesEach() != null)
                    valueNoteEach = objNote.get(i).getNotesEach();
                else
                    valueNoteEach = "";
            } catch (Exception e) {
            }

            int x = 0;

            if (objNote.get(i).isChecked())
                x = 1;
            else
                x = 0;

            if (isSkip) {
                x = 0;
                valueNoteEach = "";
            }
            JSONObject object = new JSONObject();
            object.put("key", keyBool);
            object.put("value", objNote.get(i).getValue());
            object.put(keyBool, x);
            object.put(keyText, "" + valueNoteEach);

            poleOption.put(object);
        }

        if (isSkip) {
            spf.edit().putString(AppConstants.NOTES, "").apply();
        } else {
            spf.edit().putString(AppConstants.NOTES, edtNotes.getText().toString()).apply();
        }
        spf.edit().putString(AppConstants.POLE_OPTION, poleOption.toString()).apply();

        boolean temp = false;

        if (isForEdit) {
            temp = true;
            spf.edit().putString(AppConstants.NOTES_EDIT, edtNotes.getText().toString()).apply();
            //spf.edit().remove(AppConstants.POLE_OPTION).apply();
            spf.edit().putString(AppConstants.NOTES_POLE_OPTION_EDIT, poleOption.toString()).apply();

        } else if (isFromBack) {
            spf.edit().putString(AppConstants.NOTES_EDIT, edtNotes.getText().toString()).apply();
            spf.edit().putString(AppConstants.NOTES_POLE_OPTION_EDIT, poleOption.toString()).apply();
            //navigation(poleOption.toString());

            if (from.toString().equals("edit")) {
                temp = true;
                isNewData = false;
            } else if (from.toString().equals("display")) {

                PoleDataDisplayFragment objNoteFragment = new PoleDataDisplayFragment();

                Bundle mBundle = new Bundle();
                mBundle.putString("ID", ID);
                mBundle.putBoolean("IS_FROM_MAP", false);
                mBundle.putBoolean("isNewData", false);

                objNoteFragment.setArguments(mBundle);
                objUtil.loadFragment(objNoteFragment, getActivity());

            } else {
                if (isCameraPreview.equalsIgnoreCase("Yes")) {
                    objUtil.loadFragment(new CameraPreviewFragment(), getActivity());
                } else
                    objUtil.loadFragment(new AddressFragement(), getActivity());
            }

        } else {

            spf.edit().putString(AppConstants.NOTES_EDIT, edtNotes.getText().toString()).apply();
            //spf.edit().remove(AppConstants.POLE_OPTION).apply();
            spf.edit().putString(AppConstants.NOTES_POLE_OPTION_EDIT, poleOption.toString()).apply();

            if (spf.getBoolean(AppConstants.SPF_POLE_ID_VISIBILITY, true)) {
                objUtil.loadFragment(new PoleIdFragment(), getActivity());
                Log.i("---*", "note- pole view call");

            } else if (spf.getBoolean(AppConstants.SPF_OTHER_DATA_VISIBILITY, true)) {
                temp = true;
                isNewData = true;

            } else {

                Log.i("---*", "note- save call");
                //place API call for save new data
                Util.addLog("Camera Preview UI: Calling Save Data");
                //mList.clear();
                //mList.addAll(Util.gettAssetDataSPF(spf));

                mList.clear();
                mList.addAll(Util.gettAssetDataSPF(spf));

                HashMap<String, String> map1 = new HashMap<>();
                for (int i = 0; i < mList.size(); i++) {
                    if (!mList.get(i).isStaticData()) {
                        map1.put(mList.get(i).getAttrKey(), "");
                    }
                }

                JSONObject json1 = new JSONObject(map1);
                assets = json1.toString();
                Log.i(AppConstants.TAG, json1.toString());

                if (Util.isInternetAvailable(getActivity())) {
                    saveNewSLCData2(edtNotes.getText().toString(), poleOption.toString());
                } else
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
            }
            //objUtil.loadFragment(new PoleIdFragment(), getActivity());
        }
        if (temp) {

            Log.i("---*", "note- edit view call");
            PoleDataEditFragment objNoteFragment = new PoleDataEditFragment();
            Bundle mBundle = new Bundle();

            mBundle.putString("ID", ID);
            mBundle.putBoolean("IS_FROM_MAP", isFromMap);
            mBundle.putBoolean("isNewData", isNewData);
            mBundle.putString("slcID", slcId);
            mBundle.putString("MacId", macID);
            mBundle.putString("poleId", poleId);
            mBundle.putDouble("Long", longitude);
            mBundle.putDouble("Lat", lattitude);
            mBundle.putBoolean("noteBack", true);
            mBundle.putBoolean("noteUpdate", true);
            mBundle.putBoolean(AppConstants.isfromNote, true);
            mBundle.putString(AppConstants.NOTES_POLE_OPTION_EDIT, edtNotes.getText().toString() + "#" + poleOption.toString());

            objNoteFragment.setArguments(mBundle);
            objUtil.loadFragment(objNoteFragment, getActivity());
        }

    }

    void saveNewSLCData() {

        /*String CopyFromPrevious;
        boolean isCopyFromPrevious = spf.getBoolean(AppConstants.CopyFromPrevious, false);
        if (isCopyFromPrevious)
            CopyFromPrevious = "Yes";
        else
            CopyFromPrevious = "No";*/

        String pole_option = spf.getString(AppConstants.POLE_OPTION, "");
        String notes = spf.getString(AppConstants.NOTES, "");

        ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> mListNotes=new ArrayList<>();
        mListNotes.addAll(Util.getNotesData(pole_option));
        HashMap<String, RequestBody> mapNote = new HashMap<>();
        for (int i = 0; i < mListNotes.size(); i++) {
            if (!mListNotes.get(i).isStaticData()) {
                if (mListNotes.get(i).isNoteData()) {
                    mapNote.put(mListNotes.get(i).getAttrKey(), getRequestBody(mListNotes.get(i).getSelectected()));
                }
            }
        }


       /* File root = Environment.getExternalStorageDirectory();
        final File fileCamerra = new File(root + "/SLCScanner/Preview.jpg");*/

        File root = new File(String.valueOf(getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM)));
        root.mkdirs();
        File fileCamerra = new File(root, "Preview.jpg");

        MultipartBody.Part filePart = null;
        if (fileCamerra.exists()) {
            filePart = MultipartBody.Part.createFormData("pole_image",
                    fileCamerra.getName(), RequestBody.create(MediaType.parse("image/*"),
                            fileCamerra));
        }

        clientId = spf.getString(AppConstants.CLIENT_ID, "");
        userid = spf.getString(AppConstants.USER_ID, "");
        GlobalSLCId = spf.getString(AppConstants.SPF_LOGOUT_SLCID, "");
        units = spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC);
        macID = spf.getString(AppConstants.SPF_TEMP_MACID, "");
        slcId = spf.getString(AppConstants.SPF_TEMP_SLCID, "");
        lattitude = Double.valueOf(spf.getString(AppConstants.SPF_DRAG_LATTITUDE, "0.0"));
        longitude = Double.valueOf(spf.getString(AppConstants.SPF_DRAG_LONGITUDE, "0.0"));
        address = spf.getString(AppConstants.ADDRESS, "");

       /* ArrayList<com.CL.slcscanner.Pojo.Login.Datum> mClientType = Util.getClientTypeList(spf);
        if (mClientType.size() > 0)
            mClientType.remove(0);*/

        /*
        //temp node type disable
        String node_type = spf.getString(AppConstants.SELECTED_NODE_TYPE_SAVE, mClientType.get(1).getClientType());

        if (node_type
                .equalsIgnoreCase(getString(R.string.unknown)))
            node_type = "";
*/

        dialog.show();
        objApi.saveNewSLCDataWithFile(
                getRequestBody(clientId),
                getRequestBody(userid),
                getRequestBody(macID),
                getRequestBody(slcId),
                getRequestBody(""),
                getRequestBody(String.valueOf(lattitude)),
                getRequestBody(String.valueOf(longitude)),
                getRequestBody("NO"),
                getRequestBody(units),
                getRequestBody("Android"),
                getRequestBody(address),
                getRequestBody(""),
                getRequestBody(assets),
                getRequestBody(GlobalSLCId),
                getRequestBody("No"),
                filePart,
                getRequestBody(notes),
                mapNote,
                getRequestBody("")//node_type
        ).enqueue(new Callback<CommonResponse2>() {
            @Override
            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {

                if (response.body() != null) {
                    CommonResponse2 response1 = response.body();
                    if (response1.getStatus().equalsIgnoreCase("success")) {
                        if (dialog.isShowing())
                            dialog.dismiss();

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
                        edt.remove(AppConstants.isNoneChecked);
                        edt.apply();

                        dialog(response1.getMsg());

                        Util.addLog("Camera Preview UI: New Data saved Successfully");

                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, "").apply();

                        Util.deletePreviewFile(getActivity());

                    } else if (response1.getStatus().equalsIgnoreCase("error")) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        Util.dialogForMessage(getActivity(), response1.getMsg());
                        Util.addLog("Camera Preview UI: New Data saved Failed");
                    } else
                        Util.dialogForMessage(getActivity(), getResources().getString(R.string.server_error));

                } else {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.server_error));
                }
            }

            @Override
            public void onFailure(Call<CommonResponse2> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                Util.addLog("Camera Preview UI: New Data saved Failed - Network / Server Error");
            }
        });
    }

    void saveNewSLCData2(String notes, String pole_option) {

        String DOI = "";
        String address = "";

        clientId = spf.getString(AppConstants.CLIENT_ID, "");
        userid = spf.getString(AppConstants.USER_ID, "");
        GlobalSLCId = spf.getString(AppConstants.SPF_LOGOUT_SLCID, "");
        units = spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC);
        macID = spf.getString(AppConstants.SPF_TEMP_MACID, "");
        slcId = spf.getString(AppConstants.SPF_TEMP_SLCID, "");
        address = spf.getString(AppConstants.ADDRESS, "");

        //add notes array

        HashMap<String, String> map = new HashMap<>();
        HashMap<String, RequestBody> map3 = new HashMap<>();

        HashMap<String, String> map1 = new HashMap<>();
        for (int i = 0; i < mListAssets.size(); i++) {
            if (!mListAssets.get(i).isStaticData()) {
                map1.put(mListAssets.get(i).getAttrKey(), "");
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

        File root = new File(String.valueOf(getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM)));
        root.mkdirs();
        File fileCamerra = new File(root, "Preview.jpg");

        /*File root = Environment.getExternalStorageDirectory();
        final File fileCamerra = new File(root + "/SLCScanner/Preview.jpg");*/

        MultipartBody.Part filePart = null;
        if (fileCamerra.exists()) {
            filePart = MultipartBody.Part.createFormData("pole_image",
                    fileCamerra.getName(), RequestBody.create(MediaType.parse("image/*"),
                            fileCamerra));
        }

        units = spf.getString(AppConstants.SPF_UNITS, SPF_UNITES_MATRIC);

        /*ArrayList<com.CL.slcscanner.Pojo.Login.Datum> mClientType = Util.getClientTypeList(spf);
        if (mClientType.size() > 0)
            mClientType.remove(0);*/

        /*
        //temp node type disable
        String node_type = spf.getString(AppConstants.SELECTED_NODE_TYPE_SAVE, mClientType.get(1).getClientType());

        if (node_type.equalsIgnoreCase(getString(R.string.unknown)))
            node_type = "";
*/

        String version = spf.getString(AppConstants.VERSION,"");
        dialog_save.show();
        objApi.saveNewSLCDataWithFile2(
                getRequestBody(clientId),
                getRequestBody(userid),
                getRequestBody(macID),
                getRequestBody(slcId),
                getRequestBody(""),
                getRequestBody(String.valueOf(lattitude)),
                getRequestBody(String.valueOf(longitude)),
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
                        Util.deletePreviewFile(getActivity());

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


    void dialog(String msg) {
        TextView textView = new TextView(getActivity());
        textView.setText(msg);

        //msg = "You will be able to add/edit this data later from <strong>List ->Edit<strong>";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(Html.fromHtml(msg))
                // builder.setView(textView)
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
}