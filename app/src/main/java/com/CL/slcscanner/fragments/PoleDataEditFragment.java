package com.CL.slcscanner.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.CL.slcscanner.Activities.MainActivity;
import com.CL.slcscanner.Adapter.AssestEditAdapter;

import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.AddOther.AddOtherMaster;
import com.CL.slcscanner.Pojo.ClientAssets.ClientAssestMaster;
import com.CL.slcscanner.Pojo.CommonResponse;
import com.CL.slcscanner.Pojo.CommonResponse2;
import com.CL.slcscanner.Pojo.Login.LoginResponse;
import com.CL.slcscanner.Pojo.PoleDisplayData.Asset;
import com.CL.slcscanner.Pojo.PoleMaster.Datum;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.CustomInfoWindowGoogleMap;
import com.CL.slcscanner.Utils.Util;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.android.segmented.SegmentedGroup;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.CL.slcscanner.Utils.AppConstants.SPF_LIST_ARM_LEGTH;
import static com.CL.slcscanner.Utils.AppConstants.SPF_LIST_HEIGHT;
import static com.CL.slcscanner.Utils.AppConstants.SPF_LIST_POLE_DIAMETER;
import static com.CL.slcscanner.Utils.AppConstants.SPF_UNITES_MATRIC;
import static com.CL.slcscanner.Utils.Util.getRequestBody;
import static com.CL.slcscanner.Utils.Util.hasPermissions;
import static com.CL.slcscanner.Utils.Util.storeImage;
import static com.CL.slcscanner.Utils.Util.storeImage1;

/**
 * Created by vrajesh on 2/24/2018.
 */

public class PoleDataEditFragment extends Fragment implements View.OnClickListener, AssestEditAdapter.MyCallbackForControl, AssestEditAdapter.MyCallbackPoleOption {

    View view;
    @BindView(R.id.btnPoleBack)
    ImageView btnPoleBack;

    Bundle mBundle;
    String userid, clientId, slcId, macID, poleId, units;

    Double Lat;
    Double Long;
    SharedPreferences spf;

    @BindView(R.id.btnSave)
    Button btnSave;

    @BindView(R.id.llPoleBack)
    LinearLayout llPoleBack;

    @BindView(R.id.ivPoleDisplayMap)
    ImageView ivPoleDisplayMap;

    @BindView(R.id.tvPoleEditBack)
    TextView tvPoleEditBack;

    @BindView(R.id.rvAssets)
    RecyclerView recyclerView;

    @BindView(R.id.ivImgUpload)
    ImageView ivImgUpload;

    @BindView(R.id.btnImgEdit)
    ImageView btnImgEdit;

    @BindView(R.id.tbTitleSkip)
    TextView tbTitleSkip;

    @BindView(R.id.tvCopyText)
    TextView tvCopyText;

    @BindView(R.id.llCopyPoleData)
    LinearLayout llCopyPoleData;

    @BindView(R.id.btnYes)
    RadioButton btnYes;

    @BindView(R.id.btnNo)
    RadioButton btnNo;

    @BindView(R.id.segmentCopyFromPrevious)
    SegmentedGroup segmentCopyFromPrevious;

    @BindView(R.id.llBotomViewEdit)
    LinearLayout llBotomViewEdit;

    ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> mList;
    AssestEditAdapter mAdapter;

    AlertDialog loginAlertDialog;
    EditText etUsername;
    EditText etPass;

    String GlobalSLCId = "";
    ProgressDialog loginDialog;

    Dialog dialog_googlemap;
    Dialog dialog_cammera;

    Bitmap myBitmap;
    Uri picUri;

    boolean noteBack = false;
    boolean noteUpdate = false;

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 107;

    private String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    private static final int PERMISSION_ALL = 10;
    String userChoosenTask;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;

    ImageView ivCameraPreview;

    //File fileCamerra;
    String imgUrl = "";
    private Uri imageUri;

    Uri objUri = null;
    String filePath;
    AlertDialog macDialog;

    boolean IsFromScannerEdit;
    boolean isCopyFromPrevious = false;
    RequestOptions requestOptions;

    boolean isNewData = false;
    String ID;
    API objApi;
    ProgressDialog dialog;
    ProgressDialog dialog_save;

    boolean isFromMap = false;

    Double dragLat = 0.0, dragLong = 0.0;
    boolean isDragPin = false;

    AlertDialog dialogSelection;
    AlertDialog dialogFixture;

    //custom marker
    View mCustomMarkerView;
    ImageView marker_image;

    Util objUtil;

    String pole_option;
    String notes;

    boolean isfromNote = false;

    String slcStaus;

    boolean isSLCIDClickable = true;
    ArrayList<com.CL.slcscanner.Pojo.Login.Datum> mClientType;

    String node_type_update_slc;
    String node_type_display;
    String node_type_save;

    boolean isFromScannerForEdit = false;
    String node_type_from_scanner_for_edit = "";
    int node_type_from_scanner_for_edit_index = 0;

    String version;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pole_data_edit, null);
        init();
        return view;
    }

    void init() {

        ButterKnife.bind(this, view);
        objApi = new SLCScanner().networkCall();

        objUtil = new Util();

        getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
        getActivity().findViewById(R.id.llNodeType).setVisibility(View.GONE);
        //custom marker
        mCustomMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        marker_image = mCustomMarkerView.findViewById(R.id.marker_image);

        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        //pole_option = spf.getString(AppConstants.POLE_OPTION, "");
        //notes = spf.getString(AppConstants.NOTES, "");
        isCopyFromPrevious = spf.getBoolean(AppConstants.CopyFromPrevious, false);
        slcStaus = spf.getString(AppConstants.SPF_TEMP_SLC_STATUS, AppConstants.EXTERNAL);

        mList = new ArrayList<>();
        mAdapter = new AssestEditAdapter(getActivity(), mList, this, spf, this, isfromNote);
        //recyclerView.setHasFixedSize(true);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        version=spf.getString(AppConstants.VERSION,"");

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getResources().getString(R.string.loading));
        dialog.setCancelable(false);

        dialog_save = new ProgressDialog(getActivity());
        dialog_save.setMessage(getResources().getString(R.string.saving));
        dialog_save.setCancelable(false);

        userid = spf.getString(AppConstants.USER_ID, "");
        clientId = spf.getString(AppConstants.CLIENT_ID, "");
        units = spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC);

        GlobalSLCId = spf.getString(AppConstants.SPF_LOGOUT_SLCID, "");

        requestOptions = new RequestOptions();
        String localTemp = Util.getDeviceLocale(spf);
        if (localTemp.equalsIgnoreCase(AppConstants.LANGUAGE_CODE_ENGLISH)) {
            requestOptions.placeholder(R.drawable.loading);
            requestOptions.error(R.drawable.no_image);
        } else if (localTemp.equalsIgnoreCase(AppConstants.LANGUAGE_CODE_SPANISH)) {
            requestOptions.placeholder(R.drawable.loading_es);
            requestOptions.error(R.drawable.no_image_es);
        } else if (localTemp.equalsIgnoreCase(AppConstants.LANGUAGE_CODE_PORTUGUES)) {
            requestOptions.placeholder(R.drawable.loading_pt);
            requestOptions.error(R.drawable.no_images_pt);
        }

        llBotomViewEdit.setVisibility(View.GONE);
        Bundle mBundle = getArguments();
        if (mBundle != null) {
            IsFromScannerEdit = mBundle.getBoolean(AppConstants.IsFromScannerEdit, false);
            ID = mBundle.getString("ID");
            isFromMap = mBundle.getBoolean("IS_FROM_MAP");
            isNewData = mBundle.getBoolean("isNewData");
            slcId = mBundle.getString("slcID");
            macID = mBundle.getString("MacId");
            poleId = mBundle.getString("poleId");
            Long = mBundle.getDouble("Long");
            Lat = mBundle.getDouble("Lat");
            //isFromScannerForEdit = mBundle.getBoolean(AppConstants.isFromScannerForEdit, false);

            /*if (isFromScannerForEdit) {
                node_type_from_scanner_for_edit = mBundle.getString(AppConstants.SELECTED_NODE_TYPE_EDIT_SLC, "");
                node_type_from_scanner_for_edit_index = mBundle.getInt(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_SLC, 0);
            }*/

            dragLat = mBundle.getDouble("Lat");
            dragLong = mBundle.getDouble("Long");

            isfromNote = mBundle.getBoolean(AppConstants.isfromNote, false);
            noteBack = mBundle.getBoolean("noteBack", false);
            noteUpdate = mBundle.getBoolean("noteUpdate", false);

           /* if (isfromNote) {
                pole_option = spf.getString(AppConstants.NOTES_POLE_OPTION_EDIT, "");
                notes = spf.getString(AppConstants.NOTES_EDIT, "");
            } else {
                pole_option = spf.getString(AppConstants.POLE_OPTION, "");
                notes = spf.getString(AppConstants.NOTES, "");
            }*/

            if (isfromNote) {
                try {
                    String note_option_whole_data = mBundle.getString(AppConstants.NOTES_POLE_OPTION_EDIT, "");
                    String[] arrOfStr = note_option_whole_data.split("#");
                    notes = arrOfStr[0];
                    pole_option = arrOfStr[1];
                } catch (Exception e) {
                }
            } else {
                pole_option = spf.getString(AppConstants.POLE_OPTION, "");
                notes = spf.getString(AppConstants.NOTES, "");
            }

            SharedPreferences.Editor editor = spf.edit();
            editor.putBoolean(AppConstants.SPF_ISFROMMAP, isFromMap);
            editor.putString(AppConstants.SPF_ID, ID);
            editor.putBoolean(AppConstants.SPF_ISFROMMAP, isFromMap);
            editor.apply();

            if (isNewData) {

                if (spf.getBoolean(AppConstants.SPF_POLE_ID_VISIBILITY, true)) {
                    tvPoleEditBack.setText(getResources().getString(R.string.pole_id));
                } else {
                    tvPoleEditBack.setText(getResources().getString(R.string.camera));
                }
                ivPoleDisplayMap.setVisibility(View.GONE);
                ivImgUpload.setVisibility(View.GONE);
                btnImgEdit.setVisibility(View.GONE);

                //mList.addAll(Util.gettAssetDataSPF(spf));

                if (Util.isInternetAvailable(getActivity()))
                    getClientAssets(clientId, units, isCopyFromPrevious);
                else
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));

            } else {

                tbTitleSkip.setVisibility(View.GONE);
                ivPoleDisplayMap.setVisibility(View.VISIBLE);
                ivImgUpload.setVisibility(View.GONE);

                String pole_image_view_display = spf.getString(AppConstants.SPF_CLIENT_SLC_POLE_IMAGE_VIEW, "Yes");
                if (pole_image_view_display.equalsIgnoreCase("Yes"))
                    btnImgEdit.setVisibility(View.VISIBLE);
                else
                    btnImgEdit.setVisibility(View.GONE);

                tvPoleEditBack.setText(getResources().getString(R.string.back));
                DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
                int height = displayMetrics.heightPixels;
                if (!IsFromScannerEdit) {
                    if (!isfromNote) {
                        Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.editMsg), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, (int) (height * 0.15));
                        toast.show();
                    }
                }

                if (!ID.toString().equals("")) {
                    if (Util.isInternetAvailable(getActivity())) {
                        if (noteBack || noteUpdate || IsFromScannerEdit) {
                            //test
                            llBotomViewEdit.setVisibility(View.VISIBLE);
                            btnSave.setVisibility(View.VISIBLE);

                            if (IsFromScannerEdit) {
                                //Log.i("--*", mList.toString());
                                Util.saveArray(getActivity(), Util.loadArray(getActivity(), mList),
                                        macID, slcId, true);
                            }
                            Util.loadArray(getActivity(), mList);

                            imgUrl = spf.getString(AppConstants.IMAGE_URL, imgUrl);
                            mAdapter.notifyDataSetChanged();

                        } else {
                            callAPIGetDetails2(ID);
                        }
                    } else
                        Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
                } else
                    objUtil.loadFragment(new PoleDataFragment(), getActivity());
            }
            loginAlertDialog = loginDialog(getActivity());
        }

        segmentCopyFromPrevious.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (Util.isInternetAvailable(getActivity())) {
                    int selectedId = segmentCopyFromPrevious.getCheckedRadioButtonId();
                    if (selectedId == R.id.btnYes) {
                        isCopyFromPrevious = true;
                        spf.edit().putBoolean(AppConstants.CopyFromPrevious, true).apply();

                        getClientAssets(clientId, units, isCopyFromPrevious);
                    } else {
                        isCopyFromPrevious = false;
                        spf.edit().putBoolean(AppConstants.CopyFromPrevious, false).apply();
                        getClientAssets(clientId, units, isCopyFromPrevious);
                    }
                } else
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
            }
        });

        llPoleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNewData) {
                    boolean isVisible = spf.getBoolean(AppConstants.SPF_POLE_ID_VISIBILITY, true);
                    if (isVisible)
                        objUtil.loadFragment(new PoleIdFragment(), getActivity());
                    else {
                        //loadFragment(new SelectLocationPoleFragment());
                        //objUtil.loadFragment(new NoteFragment(), getActivity());
                        String note_option = spf.getString(AppConstants.NOTES, "") + "#" + spf.getString(AppConstants.POLE_OPTION, "");
                        Bundle mBundle = new Bundle();
                        NoteFragment objNoteFragment = new NoteFragment();
                        mBundle.putBoolean(AppConstants.ISVIEWONLY, false);
                        mBundle.putString(AppConstants.FROM, "pole_data_edit");
                        mBundle.putString(AppConstants.NOTES_POLE_OPTION, note_option);
                        objNoteFragment.setArguments(mBundle);
                        objUtil.loadFragment(objNoteFragment, getActivity());

                    }
                } else {
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    PoleDataDisplayFragment fragment = new PoleDataDisplayFragment();

                    Bundle mBundle = new Bundle();
                    mBundle.putString("ID", ID);
                    mBundle.putBoolean("IS_FROM_MAP", isFromMap);
                    fragment.setArguments(mBundle);
                    //fragmentTransaction.addToBackStack("");
                    fragmentTransaction.replace(R.id.frm1, fragment);
                    fragmentTransaction.commit(); // save the changes
                }
            }
        });

        btnSave.setOnClickListener(this);
        tbTitleSkip.setOnClickListener(this);

        //btnSave.setOnTouchListener(Util.colorFilter());

        if (isNewData) {
            spf.edit().putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, AppConstants.SPF_POLE_EDIT_FRAG).apply();
            if (getActivity() != null) {
                ((MainActivity) getActivity()).selectPole(false);
                ((MainActivity) getActivity()).selectScan(true);
            }
        } else {
            spf.edit().putString(AppConstants.SPF_POLE_CURRENT_FRAG, AppConstants.SPF_POLE_EDIT_FRAG).apply();
            if (getActivity() != null) {
                ((MainActivity) getActivity()).selectPole(true);
                ((MainActivity) getActivity()).selectScan(false);
            }

        }

        if (getActivity() != null) {
            ((MainActivity) getActivity()).selectSetting(false);
            ((MainActivity) getActivity()).selectMap(false);
        }

        ivPoleDisplayMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_map();
            }
        });

        ivImgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //loadFragment(new CameraPreviewFragment());
                if (hasPermissions(getActivity(), PERMISSIONS)) {
                    dialogCammera();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                }
            }
        });

        btnImgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermissions(getActivity(), PERMISSIONS)) {
                    dialogCammera();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                }
            }
        });

        //permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        /*mClientType = Util.getClientTypeList(spf);
        if (mClientType.size() > 0)
            mClientType.remove(0);*/

    }

    @Override
    public void onResume() {
        super.onResume();
        Util.hideKeyboard(getActivity());
        units = spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC);
    }

    void callAPIGetDetails(String id) {
        dialog.show();
        objApi.getSLCDetails(id, units).enqueue(new Callback<com.CL.slcscanner.Pojo.PoleDisplayData.PoleDisplayMaster>() {
            @Override
            public void onResponse(Call<com.CL.slcscanner.Pojo.PoleDisplayData.PoleDisplayMaster> call, Response<com.CL.slcscanner.Pojo.PoleDisplayData.PoleDisplayMaster> response) {

                llBotomViewEdit.setVisibility(View.VISIBLE);
                if (response.body() != null) {
                    com.CL.slcscanner.Pojo.PoleDisplayData.PoleDisplayMaster objDetails = response.body();
                    if (objDetails.getStatus().equals("success")) {
                        mList.clear();
                        com.CL.slcscanner.Pojo.PoleDisplayData.Data objData = objDetails.getData();
                        //setDataInLables(objData);
                        setDataLables(objData);
                        btnSave.setVisibility(View.VISIBLE);

                        if (!objDetails.getMsg().equalsIgnoreCase("")) {
                            Util.dialogForMessage(getActivity(), objDetails.getMsg().toString());
                        }
                    } else {
                        Util.dialogForMessage(getActivity(), "Invalid ID");
                    }
                } else
                    Util.dialogForMessage(getActivity(), getActivity().getResources().getString(R.string.server_error));
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<com.CL.slcscanner.Pojo.PoleDisplayData.PoleDisplayMaster> call, Throwable t) {
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                dialog.dismiss();

                llBotomViewEdit.setVisibility(View.GONE);
            }
        });
    }

    void callAPIGetDetails2(String id) {
        dialog.show();
        objApi.getSLCDetails2(id, units).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                llBotomViewEdit.setVisibility(View.VISIBLE);
                String objDetails = null;

                if (response.body() != null) {
                    try {
                        objDetails = response.body().string();
                        Log.d(AppConstants.TAG, objDetails.toString());

                        JSONObject object = new JSONObject(objDetails);

                        if (object.getString("status").equals("success")) {
                            mList.clear();

                            JSONObject objData = object.getJSONObject("data");

                            String mac_id_type = objData.getString("mac_address_type");
                            boolean isSLCIdClickable = false;
                            if (mac_id_type.toString().equalsIgnoreCase(AppConstants.Internal)) {
                                isSLCIdClickable = false;
                            } else {
                                isSLCIdClickable = true;
                            }

                            Log.i("---*", "click:" + isSLCIdClickable);

                            setDataInLables2(objData, isSLCIdClickable);
                            btnSave.setVisibility(View.VISIBLE);

                            if (!object.getString("msg").equalsIgnoreCase("")) {
                                Util.dialogForMessage(getActivity(), object.getString("msg"));
                            }

                        } else {
                            Util.dialogForMessage(getActivity(), "Invalid ID");
                        }

                    } catch (Exception e) {
                    }
                } else
                    Util.dialogForMessage(getActivity(), getActivity().getResources().getString(R.string.server_error));

                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                dialog.dismiss();
                llBotomViewEdit.setVisibility(View.GONE);
            }
        });
    }


    void callEditSLCApi(final String slcid, String poleid, String tempMacid, String
            address, String doi, String assets) {
        File root = Environment.getExternalStorageDirectory();
        final File fileCamerra = new File(root + "/SLCScanner/Preview.jpg");
        //final File fileCamerra = new File(Path);

        //RequestBody requestFile =RequestBody.create(MediaType.parse("multipart/form-data"), fileCamerra);
        //MultipartBody.Part body = MultipartBody.Part.createFormData("image", fileCamerra.getName(), requestFile);
        MultipartBody.Part filePart = null;
        if (fileCamerra.exists()) {
            filePart = MultipartBody.Part.createFormData("pole_image",
                    fileCamerra.getName(), RequestBody.create(MediaType.parse("image/*"),
                            fileCamerra));
        }

        btnSave.setEnabled(false);

        dialog_save.show();
        objApi.edtitUpdateSLCData(
                RequestBody.create(MediaType.parse("text/plain"), ID),
                RequestBody.create(MediaType.parse("text/plain"), userid),
                RequestBody.create(MediaType.parse("text/plain"), clientId),

                RequestBody.create(MediaType.parse("text/plain"), slcid),
                RequestBody.create(MediaType.parse("text/plain"), poleid),
                RequestBody.create(MediaType.parse("text/plain"), tempMacid),

                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Lat)),

                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Long)),
                RequestBody.create(MediaType.parse("text/plain"), units),
                RequestBody.create(MediaType.parse("text/plain"), address),

                RequestBody.create(MediaType.parse("text/plain"), doi),
                RequestBody.create(MediaType.parse("text/plain"), assets),
                RequestBody.create(MediaType.parse("text/plain"), "Android"),

                filePart
        ).enqueue(new Callback<CommonResponse2>() {
            @Override
            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {
                btnSave.setEnabled(true);

                if (response.body() != null) {
                    CommonResponse2 response1 = response.body();
                    if (response1.getStatus().equals("success")) {

                        if (dialog_save.isShowing())
                            dialog_save.dismiss();

                        //Util.dialogForMessage(getActivity(), response1.getAddress());
                        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
                        int height = displayMetrics.heightPixels;
                        Toast toast = Toast.makeText(getActivity(), response1.getMsg(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, (int) (height * 0.15));
                        toast.show();

                        //toast.setGravity(Gravity.BOTTOM, 0, );
                        toast.show();

                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        PoleDataDisplayFragment fragment = new PoleDataDisplayFragment();

                        Bundle mBundle = new Bundle();
                        mBundle.putBoolean("IS_FROM_MAP", isFromMap);
                        mBundle.putBoolean("isNewData", false);
                        mBundle.putString("ID", ID);

                        fragment.setArguments(mBundle);
                        fragmentTransaction.replace(R.id.frm1, fragment);
                        fragmentTransaction.commit(); // save the changes

                        Util.addLog("Edit Pole Data: Data Edit Successfully, SLC ID: " + slcid + "MAC ID: " + macID);
                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, "").apply();

                        if (fileCamerra.exists())
                            fileCamerra.delete();

                    } else if (response1.getStatus().equals("logout")) {
                        if (dialog_save.isShowing())
                            dialog_save.dismiss();

                        spf.edit().putBoolean(AppConstants.ISLOGGEDIN, false).apply();

                        etPass.setText("");
                        etUsername.setText("");
                        loginAlertDialog.show();

                        Toast.makeText(getActivity(), response1.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.i(AppConstants.TAG, response1.getMsg());
                        Util.addLog(response1.getMsg());

                        GlobalSLCId = response1.getSLCID();
                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, response1.getSLCID()).apply();

                        if (fileCamerra.exists())
                            fileCamerra.delete();

                    } else {
                        Util.dialogForMessage(getActivity(), response1.getMsg());
                        Util.addLog("Edit Pole Data: Data Edit failed SLC ID: " + slcid + "MAC ID: " + macID);
                        if (dialog_save.isShowing())
                            dialog_save.dismiss();
                    }
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
                Util.addLog("Edit Pole Data: Edit Data saved Failed - Network / Server Error");
                btnSave.setEnabled(true);
            }
        });
    }

    void callEditSLCApi2(final String slcid, String poleid, String tempMacid, String
            address, String doi, Map<String, RequestBody> map, String assets, String notes, String node_type) {
        File root = Environment.getExternalStorageDirectory();
        final File fileCamerra = new File(root + "/SLCScanner/Preview.jpg");
        //final File fileCamerra = new File(Path);

        HashMap<String, RequestBody> map2 = new HashMap<>();

        //RequestBody requestFile =RequestBody.create(MediaType.parse("multipart/form-data"), fileCamerra);
        //MultipartBody.Part body = MultipartBody.Part.createFormData("image", fileCamerra.getName(), requestFile);
        MultipartBody.Part filePart = null;
        if (fileCamerra.exists()) {
            filePart = MultipartBody.Part.createFormData("pole_image",
                    fileCamerra.getName(), RequestBody.create(MediaType.parse("image/*"),
                            fileCamerra));
        }

        btnSave.setEnabled(false);

        dialog_save.show();
        objApi.edtitUpdateSLCData2(
                RequestBody.create(MediaType.parse("text/plain"), ID),
                RequestBody.create(MediaType.parse("text/plain"), userid),
                RequestBody.create(MediaType.parse("text/plain"), clientId),

                RequestBody.create(MediaType.parse("text/plain"), slcid),
                RequestBody.create(MediaType.parse("text/plain"), poleid),
                RequestBody.create(MediaType.parse("text/plain"), tempMacid),

                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Lat)),

                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Long)),
                RequestBody.create(MediaType.parse("text/plain"), units),
                RequestBody.create(MediaType.parse("text/plain"), address),

                RequestBody.create(MediaType.parse("text/plain"), doi),
                RequestBody.create(MediaType.parse("text/plain"), "Android"),
                filePart,
                RequestBody.create(MediaType.parse("text/plain"), notes),
                map,
                RequestBody.create(MediaType.parse("text/plain"), assets),
                RequestBody.create(MediaType.parse("text/plain"), node_type),
                RequestBody.create(MediaType.parse("text/plain"),version)
        ).enqueue(new Callback<CommonResponse2>() {
            @Override
            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {
                btnSave.setEnabled(true);

                if (response.body() != null) {
                    CommonResponse2 response1 = response.body();
                    if (response1.getStatus().equals("success")) {

                        spf.edit().remove("tempSLCIDLive");

                        if (dialog_save.isShowing())
                            dialog_save.dismiss();

                        SharedPreferences.Editor edit = spf.edit();
                        edit.remove(AppConstants.SELECTED_NODE_TYPE_EDIT_SLC);
                        edit.remove(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_SLC);
                        edit.apply();


                        //Util.dialogForMessage(getActivity(), response1.getAddress());
                        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
                        int height = displayMetrics.heightPixels;
                        Toast toast = Toast.makeText(getActivity(), response1.getMsg(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.TOP, 0, (int) (height * 0.15));
                        toast.show();

                        //toast.setGravity(Gravity.BOTTOM, 0, );
                        toast.show();

                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        PoleDataDisplayFragment fragment = new PoleDataDisplayFragment();

                        Bundle mBundle = new Bundle();
                        mBundle.putBoolean("IS_FROM_MAP", isFromMap);
                        mBundle.putBoolean("isNewData", false);
                        mBundle.putString("ID", ID);

                        fragment.setArguments(mBundle);
                        fragmentTransaction.replace(R.id.frm1, fragment);
                        fragmentTransaction.commit(); // save the changes

                        Util.addLog("Edit Pole Data: Data Edit Successfully, SLC ID: " + slcid + "MAC ID: " + macID);
                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, "").apply();

                        if (fileCamerra.exists())
                            fileCamerra.delete();

                        spf.edit().remove(AppConstants.NOTES_EDIT).apply();
                        spf.edit().remove(AppConstants.NOTES_POLE_OPTION_EDIT).apply();

                    } else if (response1.getStatus().equals("logout")) {
                        if (dialog_save.isShowing())
                            dialog_save.dismiss();

                        spf.edit().putBoolean(AppConstants.ISLOGGEDIN, false).apply();

                        etPass.setText("");
                        etUsername.setText("");
                        loginAlertDialog.show();

                        Toast.makeText(getActivity(), response1.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.i(AppConstants.TAG, response1.getMsg());
                        Util.addLog(response1.getMsg());

                        GlobalSLCId = response1.getSLCID();
                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, response1.getSLCID()).apply();

                        if (fileCamerra.exists())
                            fileCamerra.delete();

                    } else {
                        Util.dialogForMessage(getActivity(), response1.getMsg());
                        Util.addLog("Edit Pole Data: Data Edit failed SLC ID: " + slcid + "MAC ID: " + macID);
                        if (dialog_save.isShowing())
                            dialog_save.dismiss();
                    }
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
                Util.addLog("Edit Pole Data: Edit Data saved Failed - Network / Server Error");
                btnSave.setEnabled(true);
            }
        });
    }

    void saveNewSLCData2(String skip, String DOI, String address, String
            tempSlc, Map<String, RequestBody> map, String assets, String notes, String node_type) {
        File root = Environment.getExternalStorageDirectory();
        final File fileCamerra = new File(root + "/SLCScanner/Preview.jpg");

        MultipartBody.Part filePart = null;
        if (fileCamerra.exists()) {
            filePart = MultipartBody.Part.createFormData("pole_image",
                    fileCamerra.getName(), RequestBody.create(MediaType.parse("image/*"),
                            fileCamerra));
        }

        if (isCopyFromPrevious)
            units = spf.getString(AppConstants.SPF_UNITS_FOR_COPY_FEATURE, SPF_UNITES_MATRIC);
        else
            units = spf.getString(AppConstants.SPF_UNITS, SPF_UNITES_MATRIC);

        //getRequestBody(assets);

        //if (skip.toString().equalsIgnoreCase("yes"))
        //notes = "";


        dialog_save.show();
        objApi.saveNewSLCDataWithFile2(
                getRequestBody(clientId),
                getRequestBody(userid),
                getRequestBody(macID),
                getRequestBody(slcId),
                getRequestBody(poleId),
                getRequestBody(String.valueOf(Lat)),
                getRequestBody(String.valueOf(Long)),
                getRequestBody(skip),
                getRequestBody(units),
                getRequestBody("Android"),
                getRequestBody(address),
                getRequestBody(DOI),
                getRequestBody(tempSlc),
                getRequestBody("No"),
                filePart,
                getRequestBody(notes),
                map,
                getRequestBody(assets),
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

                        SharedPreferences.Editor edit = spf.edit();
                        edit.remove(AppConstants.SELECTED_NODE_TYPE_SAVE);
                        edit.remove(AppConstants.SELECTED_NODE_TYPE_INDEX_SAVE);
                        edit.apply();

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
                        edt.remove(AppConstants.NOTES);
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

    void dialogConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.confirmation))
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.no_camel_case), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.yes_camel_case), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveData();
            }
        });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manuallyc
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();
    }

    void SaveData() {
        if (isDragPin) {
            Lat = dragLat;
            Long = dragLong;
        }
        boolean flagForSaveData = true;

        if (!isNewData) {
            for (int k = 0; k < mList.size(); k++) {
                if (mList.get(k).getAttributeName().equalsIgnoreCase(spf.getString(AppConstants.MACID_LABLE, ""))) {//getString(R.string.attribute_mac_id)
                    if (mList.get(k).getBtnText().equalsIgnoreCase("")) {
                        Util.dialogForMessage(getActivity(), String.format(getString(R.string.enter_mac_id), spf.getString(AppConstants.MACID_LABLE, "")));
                        flagForSaveData = false;
                        //return;
                    }
                } else if (mList.get(k).getAttrKey().equalsIgnoreCase("slc_id")) {
                    if (mList.get(k).getBtnText().equalsIgnoreCase("")) {
                        Util.dialogForMessage(getActivity(), getString(R.string.enter_slc_id));
                        flagForSaveData = false;
                    }
                }
            }

        } else {
            flagForSaveData = true;
        }

        boolean isRequired = false;
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < mList.size(); k++) {
            if (!mList.get(k).isStaticData()) {
                if (mList.get(k).getIsRequire().equals("1")) {
                    if (mList.get(k).getSelectected().toString().equalsIgnoreCase("None")
                            || mList.get(k).getSelectected().toString().equalsIgnoreCase("")) {
                        sb.append(mList.get(k).getAttributeName() + " is required !\n");
                        isRequired = true;
                    }
                }
            }
        }

        String DOI = "";
        String address = "";
        String tempslcid = "", temppoleid = "", tempMacid = "";
        String notesTemp = "";
        String node_type = "";
        if (!isRequired) {
            if (flagForSaveData) {

                //add notes array
                HashMap<String, String> mapAssets = new HashMap<>();
                //HashMap<String, RequestBody> map2 = new HashMap<>();
                //notes
                HashMap<String, RequestBody> mapNotes = new HashMap<>();

                for (int i = 0; i < mList.size(); i++) {
                    boolean flagForNoJson;

                    if (mList.get(i).getAttributeName().toString().contains(getString(R.string.attribute_address))) {
                        flagForNoJson = true;
                        address = mList.get(i).getSelectected();
                    } else if (mList.get(i).getAttributeName().toString().contains(getString(R.string.attribute_date_of_installation))) {
                        flagForNoJson = true;
                        if (!mList.get(i).getBtnText().toString().contains(getString(R.string.select_date)))
                            DOI = mList.get(i).getBtnText();
                    } else if (mList.get(i).getAttributeName().toString().equalsIgnoreCase(getString(R.string.attribute_slc_id))) {
                        flagForNoJson = true;
                        tempslcid = mList.get(i).getBtnText();
                    } else if (mList.get(i).getAttributeName().toString().equalsIgnoreCase(getString(R.string.attribute_pole_id))) {
                        if (!mList.get(i).isPoleNoteAsset()) {
                            flagForNoJson = true;
                            temppoleid = mList.get(i).getBtnText();
                        } else
                            flagForNoJson = false;
                    } else if (mList.get(i).getAttributeName().toString().equalsIgnoreCase(spf.getString(AppConstants.MACID_LABLE, ""))) { //getString(R.string.attribute_mac_id)
                        flagForNoJson = true;
                        tempMacid = mList.get(i).getBtnText();
                    } else if (mList.get(i).getAttrKey().toString().equalsIgnoreCase("notes")) {
                        flagForNoJson = true;
                        notesTemp = notes;
                    } else if (mList.get(i).getAttrKey().toString().equalsIgnoreCase(getString(R.string.attribute_node_type))) {
                        flagForNoJson = true;
                        //node_type = mList.get(i).getBtnText();
                    } else
                        flagForNoJson = false;

                    if (!flagForNoJson) {
                        Log.i("--*", "AtrribeKey" + mList.get(i).getAttrKey());
                        Log.i("--*", "Selected: " + mList.get(i).getSelectected());
                        if (mList.get(i).getSelectected().toString().equals("") || mList.get(i).getSelectected().toString().equalsIgnoreCase("None")) {
                            mapAssets.put(mList.get(i).getAttrKey(), "");
                            //map2.put(mList.get(i).getAttrKey().toString(), getRequestBody(" "));
                            Log.i(AppConstants.TAG2, "BLANK " + mList.get(i).getSelectected() + " " + i);

                        } else {
                            mapAssets.put(mList.get(i).getAttrKey(), mList.get(i).getSelectected());
                            //map2.put(mList.get(i).getAttrKey(), getRequestBody(mList.get(i).getSelectected()));
                            Log.i(AppConstants.TAG2, "FILL " + mList.get(i).getSelectected() + " " + i);
                        }
                    }
                    Log.i(AppConstants.TAG2, mList.get(i).getAttributeName() + "   " + mList.get(i).getSelectected());
                }

                //blank image tag added
                mapAssets.put(spf.getString(AppConstants.pole_image_key, AppConstants.POLE_IMAGE), "");
                mapAssets.put(spf.getString(AppConstants.pole_id_key, AppConstants.POLE_ID), "");
                mapAssets.put(spf.getString(AppConstants.pole_notes_key, AppConstants.POLE_NOTES), "");

                //Assests
                JSONObject jsonAssets = new JSONObject(mapAssets);
                Log.i("--**Assets: ", jsonAssets.toString());

                ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> mListTemp = new ArrayList<>();
                mListTemp.addAll(Util.getNotesData(pole_option));

                for (int i = 0; i < mListTemp.size(); i++) {
                    if (mListTemp.get(i).getSelectected().toString().equals("") || mListTemp.get(i).getSelectected().toString().equalsIgnoreCase("None")) {
                        mapNotes.put(mListTemp.get(i).getAttrKey().toString(), getRequestBody(" "));
                        //Log.i(AppConstants.TAG2, "BLANK Notes" + mList.get(i).getSelectected() + " " + i);
                    } else {
                        mapNotes.put(mListTemp.get(i).getAttrKey(), getRequestBody(mListTemp.get(i).getSelectected()));
                        //Log.i(AppConstants.TAG2, "FILL Notes" + mList.get(i).getSelectected() + " " + i);
                    }
                }

                //display NOTES
                JSONObject json1 = new JSONObject(mapNotes);
                Log.i("--**Notes : ", json1.toString());

                if (Util.isInternetAvailable(getActivity())) {
                    if (isNewData) {
                        ////saveNewSLCData("No", DOI, spf.getString(AppConstants.ADDRESS, ""), json.toString(), GlobalSLCId);
                        Log.i("***", ": " + node_type);
                        saveNewSLCData2("No", DOI, spf.getString(AppConstants.ADDRESS, ""), GlobalSLCId, mapNotes, jsonAssets.toString(), notes, "");//node_type
                    } else {
                        if (Util.isInternetAvailable(getActivity())) {
                            callEditSLCApi2(tempslcid, temppoleid, tempMacid, address, DOI, mapNotes, jsonAssets.toString(), notesTemp, "");//node_type
                            Log.i("***", ": " + node_type);
                        } else
                            Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
                    }
                } else {
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
                }
            }
        } else {
            Util.dialogForMessage(getActivity(), sb.toString());
        }

    }

     /* for (String name : map2.keySet()) {
                String key = name.toString();
                RequestBody value = map2.get(name);
                Log.i(AppConstants.TAG2, "MAP 2 - Notes: " + key + "  " + value.toString());
            }*/

           /* for (String name : map.keySet()) {
                String key = name.toString();
                String value = map.get(name).toString();
                Log.i("--**", "MAP : "+key + " " + value);
            }*/


        /*
        //temp node type disable
        if (isNewData) {
                if (spf.getString(AppConstants.SELECTED_NODE_TYPE_SAVE, mClientType.get(1).getClientType().toString())
                        .equalsIgnoreCase(getString(R.string.unknown)))
                    node_type = "";
                else
                    node_type = spf.getString(AppConstants.SELECTED_NODE_TYPE_SAVE, mClientType.get(1).getClientType().toString());
            } else {
                if (spf.getString(AppConstants.SELECTED_NODE_TYPE_EDIT_SLC, mClientType.get(1).getClientType().toString())
                        .equalsIgnoreCase(getString(R.string.unknown)))
                    node_type = "";
                else
                    node_type = spf.getString(AppConstants.SELECTED_NODE_TYPE_EDIT_SLC, mClientType.get(1).getClientType().toString());
            }*/


    void setDataInLables2(JSONObject objData, final boolean isSLCIdClickable) {

        try {
            mList.clear();
            String tempMacId, tempSLCId;
            String poleId = objData.getString("pole_id");

            /*
            node type:
            String node_type2;
            if (isFromScannerForEdit) {
                node_type2 = node_type_from_scanner_for_edit;
            } else {
                node_type2 = objData.getString("node_type");
            }

            int index = 1;
            String node_type;
            if (node_type2.equals("") || node_type2.equals(getString(R.string.unknown))) {
                node_type2 = getString(R.string.unknown);
                node_type = "";
                index = 0;
            } else {
                node_type = node_type2;
                for (int i = 0; i < mClientType.size(); i++) {
                    if (mClientType.get(i).getClientType().equalsIgnoreCase(node_type2)) {
                        index = i;
                        break;
                    }
                }
            }

            SharedPreferences.Editor edit = spf.edit();
            edit.putString(AppConstants.SELECTED_NODE_TYPE_EDIT_SLC, node_type);
            edit.putInt(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_SLC, index);
            edit.commit();*/

            /*if (IsFromScannerEdit) {
                tempMacId = macID;
                tempSLCId = slcId;
            } else {*/
            tempMacId = objData.getString("mac_address");
            tempSLCId = objData.getString("slc_id");

            spf.edit().putString("tempSLCIDLive", tempSLCId).apply();
            //}

            Long = Double.valueOf(objData.getString("lng"));
            Lat = Double.valueOf(objData.getString("lat"));

            com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum = new com.CL.slcscanner.Pojo.ClientAssets.Datum();

            objDatum.setBtnText(tempMacId);
            objDatum.setAttributeName(spf.getString(AppConstants.MACID_LABLE, ""));
            //objDatum.setAttributeName(getString(R.string.attribute_mac_id));
            objDatum.setAttrKey("mac_address");
            objDatum.setStaticData(true);
            objDatum.setSelectected(tempMacId);
            objDatum.setNoteData(false);
            objDatum.setClickable(isSLCIdClickable);
            objDatum.setMacIdClick(isSLCIdClickable);
            mList.add(objDatum);

            com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum0 = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
            objDatum0.setBtnText(tempSLCId);
            objDatum0.setAttributeName(getString(R.string.attribute_slc_id));
            objDatum0.setAttrKey("slc_id");
            objDatum0.setStaticData(true);
            objDatum0.setSelectected(tempSLCId);
            objDatum0.setNoteData(false);
            objDatum0.setClickable(isSLCIdClickable);
            objDatum0.setMacIdClick(isSLCIdClickable);
            mList.add(objDatum0);

            String isPoleCompulsary = spf.getString(AppConstants.SPF_CLIENT_SLC_POLE_ID, "No");

            if (isPoleCompulsary.toString().equalsIgnoreCase("yes")) {
                com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum1 = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
                objDatum1.setBtnText(poleId);
                objDatum1.setAttributeName(getString(R.string.attribute_pole_id));
                objDatum1.setAttrKey("pole_id");
                objDatum1.setStaticData(true);
                objDatum1.setSelectected(poleId);
                objDatum1.setNoteData(false);
                objDatum1.setNote("");
                objDatum1.setClickable(true);
                objDatum1.setMacIdClick(false);

                mList.add(objDatum1);
            }

           /* com.CL.slcscanner.Pojo.ClientAssets.Datum objDatumPoleType = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
            objDatumPoleType.setBtnText(poleId);
            objDatumPoleType.setAttributeName(getString(R.string.attribute_node_type));
            objDatumPoleType.setAttrKey("node_type");
            objDatumPoleType.setStaticData(true);
            objDatumPoleType.setSelectected(node_type2);
            objDatumPoleType.setNoteData(false);
            objDatumPoleType.setNote("");
            objDatumPoleType.setClickable(true);
            objDatumPoleType.setMacIdClick(false);
            mList.add(objDatumPoleType);*/

            JSONArray pole_option = objData.getJSONArray("pole_options");
            //spf.edit().putString(AppConstants.NOTES_POLE_OPTION_EDIT, pole_option.toString()).apply();
            //spf.edit().putString(AppConstants.NOTES_EDIT,objData.getString("notes")).apply();
            Log.i(AppConstants.TAG, "---*POLE OPTION" + pole_option.toString());

            JSONArray objAssets = objData.getJSONArray("Assets");
            Log.i(AppConstants.TAG, "objAssests:" + objAssets.length());

            for (int i = 0; i < objAssets.length(); i++) {
                String AttKey = Util.decodeUnicode(objAssets.getJSONObject(i).getString("AttrKey"));
                String AttributeName = Util.decodeUnicode(objAssets.getJSONObject(i).getString("AttributeName"));

                com.CL.slcscanner.Pojo.ClientAssets.Datum objBean = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
                String selected = "";
                boolean isPoleNoteAssets;
                if (AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_IMAGE_ANAME)
                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_IMAGE_ANAME_SPANISH)
                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_IMAGE_ANAME_PT)
                ) {
                    spf.edit().putString(AppConstants.pole_image_key, Util.decodeUnicode(AttKey)).apply();
                } else if (AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_ID_ANAME)
                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_ID_ANAME_SPANISH)
                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_ID_ANAME_PT)) {

                    spf.edit().putString(AppConstants.pole_id_key, Util.decodeUnicode(AttKey)).apply();
                } else if (AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_NOTES_ANAME)
                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_NOTES_ANAME_SPANISH)
                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_NOTES_ANAME_PT)) {
                    spf.edit().putString(AppConstants.pole_notes_key, Util.decodeUnicode(AttKey)).apply();
                } else {

                    selected = objAssets.getJSONObject(i).getString("Selected");

                    objBean.setSelectected(selected);
                    objBean.setBtnText(objAssets.getJSONObject(i).getString("BtnText"));

                    objBean.setAttributeName(AttributeName);
                    objBean.setAssetName(objAssets.getJSONObject(i).getString("AssetName"));
                    objBean.setAttrKey(objAssets.getJSONObject(i).getString("AttrKey"));
                    objBean.setStaticData(false);
                    objBean.setNote(objAssets.getJSONObject(i).getString("Note"));

                    objBean.setIsRequire(objAssets.getJSONObject(i).getString("isRequire"));
                    objBean.setIspicklist(objAssets.getJSONObject(i).getString("ispicklist"));
                    objBean.setType(objAssets.getJSONObject(i).getString("type"));

                    objDatum.setNoteData(false);
                    objDatum.setClickable(true);
                    JSONArray values = objAssets.getJSONObject(i).getJSONArray("Values");
                    List<String> mListValue = new ArrayList<>();

                    if (values.length() > 0) {
                        for (int j = 0; j < values.length(); j++) {
                            //Log.i(AppConstants.TAG, "values:" + values.length());
                            mListValue.add(values.getString(j));
                        }
                    }
                    objBean.setValues(mListValue);

                    mList.add(objBean);
                }
            }

            com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum5 = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
            objDatum5.setAttributeName(getString(R.string.note));
            objDatum5.setAttrKey("notes");
            objDatum5.setStaticData(true);
            objDatum5.setSelectected("View");
            objDatum5.setBtnText("View");
            objDatum5.setNote(objData.getString("notes").toString() + "#" + pole_option.toString());
            objDatum5.setNoteData(true);
            objDatum5.setClickable(true);
            objDatum5.setMacIdClick(false);

            mList.add(objDatum5);

            com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum3 = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
            objDatum3.setAttributeName(getString(R.string.attribute_date_of_installation));
            objDatum3.setAttrKey("date_of_installation");
            objDatum3.setStaticData(true);
            objDatum3.setSelectected(objData.getString("date_of_installation"));
            objDatum3.setBtnText(objData.getString("date_of_installation"));
            objDatum3.setNote("");
            objDatum3.setNoteData(false);
            objDatum3.setClickable(true);
            objDatum3.setMacIdClick(false);
            mList.add(objDatum3);

            com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum4 = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
            objDatum4.setBtnText(objData.getString("address"));
            objDatum4.setSelectected(objData.getString("address"));
            objDatum4.setAttributeName(getString(R.string.attribute_address));
            objDatum4.setAttrKey("address");
            objDatum4.setStaticData(true);
            objDatum4.setNote("");
            objDatum4.setNoteData(false);
            objDatum4.setClickable(true);
            objDatum4.setMacIdClick(false);
            mList.add(objDatum4);

            imgUrl = objData.getString("pole_image_url");

            Util.saveArray(getActivity(), mList, "", "", false);
            spf.edit().putString(AppConstants.IMAGE_URL, imgUrl).apply();

            mAdapter.notifyDataSetChanged();

        } catch (Exception e) {
        }
    }

    void setDataLables(com.CL.slcscanner.Pojo.PoleDisplayData.Data objData) {

        com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum = new com.CL.slcscanner.Pojo.ClientAssets.Datum();

        String tempMacId, tempSLCId;
        if (IsFromScannerEdit) {
            tempMacId = macID;
            tempSLCId = slcId;
        } else {
            tempMacId = objData.getMacAddress();
            tempSLCId = objData.getSlcId();
        }

        objDatum.setBtnText(tempMacId);
        objDatum.setAttributeName(spf.getString(AppConstants.MACID_LABLE, ""));
        //objDatum.setAttributeName(getString(R.string.attribute_mac_id));
        objDatum.setAttrKey("mac_address");
        objDatum.setStaticData(true);
        objDatum.setSelectected(tempMacId);
        objDatum.setNoteData(false);
        mList.add(objDatum);

        com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum0 = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
        objDatum0.setBtnText(tempSLCId);
        objDatum0.setAttributeName(getString(R.string.attribute_slc_id));
        objDatum0.setAttrKey("slc_id");
        objDatum0.setStaticData(true);
        objDatum0.setSelectected(tempSLCId);
        objDatum0.setNoteData(false);
        mList.add(objDatum0);

        com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum1 = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
        objDatum1.setBtnText(objData.getPoleId());
        objDatum1.setAttributeName(getString(R.string.attribute_pole_id));
        objDatum1.setAttrKey("pole_id");
        objDatum1.setStaticData(true);
        objDatum1.setSelectected(objData.getPoleId());
        objDatum1.setNoteData(false);
        objDatum1.setNote("");
        mList.add(objDatum1);

        for (Asset objAsset : objData.getAssets()) {
            com.CL.slcscanner.Pojo.ClientAssets.Datum objBean = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
            objBean.setSelectected(objAsset.getSelected());
            objBean.setBtnText(objAsset.getBtnText());

            objBean.setAttributeName(objAsset.getAttributeName());
            objBean.setAttrKey(objAsset.getAttrKey());
            objBean.setStaticData(false);
            objBean.setValues(objAsset.getValues());
            objBean.setNote(objAsset.getNote());
            objBean.setNoteData(false);
            mList.add(objBean);
        }

        com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum5 = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
        objDatum5.setAttributeName(getString(R.string.note));
        objDatum5.setAttrKey("notes");
        objDatum5.setStaticData(true);
        objDatum5.setSelectected("View");
        objDatum5.setBtnText("View");
        objDatum5.setNote("");
        objDatum5.setNoteData(false);
        mList.add(objDatum5);

        com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum3 = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
        objDatum3.setAttributeName(getString(R.string.attribute_date_of_installation));
        objDatum3.setAttrKey("date_of_installation");
        objDatum3.setStaticData(true);
        objDatum3.setSelectected(objData.getDateOfInstallation());
        objDatum3.setBtnText(objData.getDateOfInstallation());
        objDatum3.setNote("");
        objDatum3.setNoteData(false);
        mList.add(objDatum3);

        com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum4 = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
        objDatum4.setBtnText(objData.getAddress());
        objDatum4.setSelectected(objData.getAddress());
        objDatum4.setAttributeName(getString(R.string.attribute_address));
        objDatum4.setAttrKey("address");
        objDatum4.setStaticData(true);
        objDatum4.setNote("");
        objDatum4.setNoteData(false);
        mList.add(objDatum4);

        imgUrl = objData.getPole_image_url();

        mAdapter.notifyDataSetChanged();
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

    void ShowDialog(final com.CL.slcscanner.Pojo.ClientAssets.Datum objBean,
                    final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_title, null);

        TextView tvTitle1 = view.findViewById(R.id.tvDialogTitle1);
        TextView tvTitle2 = view.findViewById(R.id.tvDialogTitle2);
        tvTitle2.setVisibility(View.VISIBLE);
        tvTitle1.setVisibility(View.GONE);

        tvTitle2.setText(objBean.getNote());
        builder.setCustomTitle(view);
        //builder.setTitle(title);

        int selectedIndex = -1;

        /*if (mList.get(position).getIspicklist().toString().equals("0")) {
            boolean tempOtherExist = false;
            if (!mList.get(position).getValues().contains(getResources().getString(R.string.other))) {
                objBean.getValues().add(0, getResources().getString(R.string.other));
            }
        }*/

        for (int i = 0; i < objBean.getValues().size(); i++) {
            if (mList.get(position).getSelectected().toString().equalsIgnoreCase(objBean.getValues().get(i))) {
                selectedIndex = i;
            }
        }

        final String[] ary = objBean.getValues().toArray(new String[0]);
        builder.setSingleChoiceItems(ary, selectedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                boolean isOther = false;

                //due to isPicklist flag directly other dialog will be open so comment code
               /* if (ary[which].toString().contains(getResources().getString(R.string.other)))
                    isOther = true;
                else
                    isOther = false;

                if (isOther) {
                    String a = objBean.getAttributeName();
                    dialogFixture = OtherFixtureDialog(getActivity(), objBean.getAttrKey(),
                            String.format(getResources().getString(R.string.other_dialog_enter_data), a), getString(R.string.enter_data),
                            true, position, objBean.getType());
                    dialogFixture.show();
                } else {
                    //selectedView.setText(ary[which]);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 500);
                }*/


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 500);

                objBean.setBtnText(ary[which]);
                objBean.setSelectected(ary[which]);

                SharedPreferences.Editor editor = spf.edit();
                editor.putString("BtnText" + position, ary[which]);
                editor.putString("Selectected" + position, ary[which]);
                editor.apply();

                mAdapter.notifyItemChanged(position, objBean);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tbTitleSkip:
                if (Util.isInternetAvailable(getActivity())) {
                    if (isDragPin) {
                        Lat = dragLat;
                        Long = dragLong;
                    }
                    Util.addLog("Edit Pole Data: SKIPP");

                    //mList.addAll(Util.getNotesData(pole_option));

                    HashMap<String, String> map1 = new HashMap<>();
                    for (int i = 0; i < mList.size(); i++) {
                        if (!mList.get(i).isStaticData()) {
                            map1.put(mList.get(i).getAttrKey(), "");
                        }
                    }
                    //map1.put(AppConstants.POLE_IMAGE,"");
                    map1.put(spf.getString(AppConstants.pole_image_key, AppConstants.POLE_IMAGE), "");
                    map1.put(spf.getString(AppConstants.pole_id_key, AppConstants.POLE_ID), "");
                    map1.put(spf.getString(AppConstants.pole_notes_key, AppConstants.POLE_NOTES), "");

                    JSONObject json1 = new JSONObject(map1);
                    Log.i(AppConstants.TAG, "Assets: skipp " + json1.toString());

                    //notes
                    mList.clear();
                    mList.addAll(Util.getNotesData(pole_option));
                    HashMap<String, RequestBody> map2 = new HashMap<>();
                    for (int i = 0; i < mList.size(); i++) {
                        if (!mList.get(i).isStaticData()) {
                            if (mList.get(i).isNoteData()) {
                                map2.put(mList.get(i).getAttrKey(), getRequestBody(mList.get(i).getSelectected()));
                            }
                        }
                    }

                    JSONObject json2 = new JSONObject(map2);
                    Log.i(AppConstants.TAG, "NOTE: skipp" + json2.toString());

                   /*
                   //temp node type disable
                   if (spf.getString(AppConstants.SELECTED_NODE_TYPE_SAVE, mClientType.get(1).getClientType().toString()).equalsIgnoreCase(getString(R.string.unknown))) {
                        node_type_save = "";
                    } else
                        node_type_save = spf.getString(AppConstants.SELECTED_NODE_TYPE_SAVE, mClientType.get(1).getClientType().toString());*/

                    saveNewSLCData2("Yes", "", spf.getString(AppConstants.ADDRESS, ""), GlobalSLCId, map2, json1.toString(), notes, "");//node_type_save
                    //sendDataToCameraPreview("Yes", "", spf.getString(AppConstants.ADDRESS, ""), json1.toString());
                } else
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));

                break;
            case R.id.btnSave:
                if (Util.isInternetAvailable(getActivity())) {
                    if (isNewData)
                        SaveData();
                    else
                        dialogConfirmation();
                } else {
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
                }

                break;
        }
    }

    void DateTimePicker(final int position,
                        final com.CL.slcscanner.Pojo.ClientAssets.Datum objBean) {
        int mYear, mMonth, mDay, mHour, mMinute, mSeconds;
// Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        String dateVal = objBean.getSelectected().toString();

        String[] parts;
        if (!dateVal.equalsIgnoreCase("")) {
            parts = dateVal.split("-");
            mMonth = Integer.valueOf(parts[0]) - 1;
            mDay = Integer.valueOf(parts[1]);
            mYear = Integer.valueOf(parts[2]);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = (monthOfYear + 1) + "-" + dayOfMonth + "-" + year;
                        //tvPDDOI.setText(selectedDate);

                        objBean.setBtnText(selectedDate);
                        objBean.setSelectected(selectedDate);

                        SharedPreferences.Editor editor = spf.edit();
                        editor.putString("BtnText" + position, selectedDate);
                        editor.putString("Selectected" + position, selectedDate);
                        editor.apply();


                        mAdapter.notifyItemChanged(position, objBean);
                        //timePicker(selectedDate);
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    InputFilter filter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start,
                                   int end, Spanned dest, int dstart, int dend) {

            for (int i = start; i < end; i++) {
                if (!Character.isLetterOrDigit(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    };

    EditText etData;

    private AlertDialog OtherFixtureDialog(Activity activity, final String tag, String
            title, String hint, final boolean isNumber, final int adapterPosition, String type, String selected) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dilog_other_fixture, null);

        TextView tvView = view.findViewById(R.id.tvTitle);
        etData = (EditText) view.findViewById(R.id.etCode);


        if (!selected.toString().equalsIgnoreCase("none"))
            etData.setText(selected);

        final Button btOK = (Button) view.findViewById(R.id.btOK);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancle);

        etData.setHint(hint);

        if (type.toString().equalsIgnoreCase("1")) {
            etData.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            etData.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        //etData.setFilters(new InputFilter[]{filter});

        if (tag.toString().equals(SPF_LIST_HEIGHT) || tag.toString().equals(SPF_LIST_ARM_LEGTH)) {
            if (units.toString().equals(AppConstants.SPF_UNITES_MATRIC))
                tvView.setText(getResources().getString(R.string.edit_other_dialog_title_meter));
                //tvView.setText(Html.fromHtml(title + " in Meters. <br><small>(e.g. 9)</small>")); //meters
            else
                tvView.setText(getResources().getString(R.string.edit_other_dialog_title_feet));
            //tvView.setText(Html.fromHtml(title + " in Feet. <br><small>(e.g. 5'10\")</small>")); // feet

        } else if (tag.equals(SPF_LIST_POLE_DIAMETER)) {
            if (units.toString().equals(AppConstants.SPF_UNITES_MATRIC))
                tvView.setText(getResources().getString(R.string.edit_other_dialog_title_cm));
                //tvView.setText(Html.fromHtml(title + " in CM. <br><small>(e.g. 7.5)</small>")); //cm
            else
                tvView.setText(getResources().getString(R.string.edit_other_dialog_title_inches));
            //tvView.setText(Html.fromHtml(title + " in inches. <br><small>(e.g. 5)</small>")); // inches
        } else
            tvView.setText(title);


        btOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isInternetAvailable(getActivity())) {
                    if (!etData.getText().toString().trim().equalsIgnoreCase("")) {
                        addAnotherData(tag, etData.getText().toString(), units, adapterPosition);
                        etData.setText("");
                    } else {
                        etData.setError(getResources().getString(R.string.required));
                    }
                } else
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogFixture.isShowing())
                    dialogFixture.dismiss();
                etData.setText("");
            }
        });

        etData.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // TODO do something
                    handled = true;
                    if (Util.isInternetAvailable(getActivity())) {
                        if (!etData.getText().toString().trim().equalsIgnoreCase("")) {
                            addAnotherData(tag, etData.getText().toString(), units, adapterPosition);
                            etData.setText("");
                        } else {
                            etData.setError(getResources().getString(R.string.required));
                        }
                    } else
                        Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
                }
                return handled;
            }
        });


        builder.setCancelable(false);
        builder.setView(view);
        return builder.create();
    }

    //API call
    void addAnotherData(final String tag, final String name, final String units,
                        final int adapterPosition) {
        dialog.show();
        objApi.getAddOtherList(tag, name, units, clientId, userid).enqueue(new Callback<AddOtherMaster>() {
            @Override
            public void onResponse(Call<AddOtherMaster> call, Response<AddOtherMaster> response) {
                if (response.body() != null) {
                    AddOtherMaster objMaster = response.body();
                    String unitsMaeassurement = "";

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    if (objMaster.getStatus().toString().equals("success")) {
                        objUtil.dismissProgressDialog(dialogSelection);
                        objUtil.dismissProgressDialog(dialogFixture);
                        try {

                            String label = objMaster.getLabel();
                            com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum = mList.get(adapterPosition);
                            objDatum.setSelectected(label);
                            objDatum.setBtnText(objMaster.getData().getBtnText());
                            objDatum.setAttrKey(objMaster.getData().getAttrKey());
                            objDatum.setAttributeName(objMaster.getData().getAttributeName());

                            objDatum.setValues(objMaster.getData().getValues());

                            SharedPreferences.Editor editor = spf.edit();
                            editor.putString("BtnText" + adapterPosition, objMaster.getData().getBtnText());
                            editor.putString("Selectected" + adapterPosition, label);
                            editor.apply();

                            mAdapter.notifyItemChanged(adapterPosition, objDatum);

                            Util.addLog("Add another value successfull -- " + label + " | " + objDatum.getAttributeName());

                        } catch (Exception e) {
                        }
                    } else {
                        //dialogFixture.dismiss();
                        //Toast.makeText(getActivity(), response.body().getAddress(), Toast.LENGTH_SHORT).show();
                        Util.addLog(response.body().getMsg());

                        Util.dialogForMessage(getActivity(), response.body().getMsg());
                        //etData.setError(response.body().getAddress());
                    }
                } else
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.server_error));
            }

            @Override
            public void onFailure(Call<AddOtherMaster> call, Throwable t) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
            }
        });
    }

    void dialog_map() {

        Util.hideKeyboard(getActivity());

        dialog_googlemap = new Dialog(getActivity());
        dialog_googlemap.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_googlemap.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog_googlemap.setContentView(R.layout.custom_dialog_map);
        dialog_googlemap.setCancelable(true);

        MapView mMapView = dialog_googlemap.findViewById(R.id.mapView1);
        MapsInitializer.initialize(getActivity());
        mMapView.onCreate(dialog_googlemap.onSaveInstanceState());
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                isDragPin = false;
                LatLng position = new LatLng(Lat, Long); ////your lat lng

                MarkerOptions options = new MarkerOptions();
                options.position(position);
                options.icon(BitmapDescriptorFactory.fromBitmap(new Util().getMarkerBitmapFromView(mCustomMarkerView, R.drawable.custome_pin, marker_image)));
                options.draggable(true);

                //googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));

                googleMap.getUiSettings().setZoomControlsEnabled(false);

                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(false);
                //googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

                new Util().setMapType(getActivity(), googleMap, spf);

                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity(), false, "No", "");
                googleMap.setInfoWindowAdapter(customInfoWindow);

                CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(17).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                final Marker m = googleMap.addMarker(options);

                Datum info = new Datum();
                info.setID(ID);
                info.setPoleId(poleId);
                info.setSlcId(slcId);
                info.setMacAddress(macID);
                info.setLat(String.valueOf(Lat));
                info.setLng(String.valueOf(Long));
                m.setTag(info);

                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        if (m.isInfoWindowShown())
                            m.hideInfoWindow();

                        isDragPin = true;
                        LatLng latLng = marker.getPosition();
                        dragLat = latLng.latitude;
                        dragLong = latLng.longitude;
                        Util.addLog("Edit Pole Data: Location Edit, Lat:" + dragLat + " Long:" + dragLong);

                        if (Util.isInternetAvailable(getActivity()))
                            getAddress(googleMap, m, dragLat, dragLong);
                        else
                            Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
                    }
                });
            }
        });

        Button dialogButton = dialog_googlemap.findViewById(R.id.btn_cancel_map);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Util.isInternetAvailable(getActivity())) {
                    if (isDragPin) {
                        //new GetAddress(dragLat, dragLong).execute();
                        getAddressAPI(dragLat, dragLong, clientId);
                    } else {
                        //new GetAddress(Lat, Long).execute();
                        getAddressAPI(Lat, Long, clientId);
                    }
                } else
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
            }
        });

        //dialogButton.setOnTouchListener(Util.colorFilter());

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();

        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialog_googlemap.getWindow().setLayout((int) (displayRectangle.width() *
                0.9f), (int) (displayRectangle.height() * 0.7f));

        dialog_googlemap.show();
    }

    void getAddress(final GoogleMap googleMap, final Marker m, Double lattitude, Double
            longitude) {

        objApi.getAddress(lattitude, longitude, spf.getString(AppConstants.CLIENT_ID, ""), spf.getString(AppConstants.USER_ID, "")).
                enqueue(new Callback<CommonResponse2>() {
                    @Override
                    public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {
                        if (response.body() != null) {
                            CommonResponse2 obj = response.body();

                            if (isAdded() && getActivity() != null) {
                                if (obj.getStatus().equalsIgnoreCase("success")) {

                                    CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity(), true, "Yes", obj.getShortaddress());
                                    googleMap.setInfoWindowAdapter(customInfoWindow);
                                    m.showInfoWindow();

                                } else if (obj.getStatus().equalsIgnoreCase("error")) {

                                    CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity(), true, "Yes", obj.getMsg());
                                    googleMap.setInfoWindowAdapter(customInfoWindow);
                                    m.showInfoWindow();
                                } else
                                    Toast.makeText(getActivity(), getResources().getString(R.string.no_address_found), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (isAdded() && getActivity() != null) {
                                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity(), true, "Yes", getResources().getString(R.string.single_pin));
                                googleMap.setInfoWindowAdapter(customInfoWindow);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonResponse2> call, Throwable t) {
                        if (isAdded() && getActivity() != null) {
                            CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity(), true, "Yes", getResources().getString(R.string.single_pin));
                            googleMap.setInfoWindowAdapter(customInfoWindow);
                        }
                    }
                });
    }

    void dialogCammera() {

        Util.hideKeyboard(getActivity());

        dialog_cammera = new Dialog(getActivity());
        dialog_cammera.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_cammera.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog_cammera.setContentView(R.layout.camera_preview_dialog);
        dialog_cammera.setCancelable(true);

        Button ivOk = dialog_cammera.findViewById(R.id.ivOk);
        Button ivChoose = dialog_cammera.findViewById(R.id.ivChoose);
        LinearLayout llcamera_inner_view = dialog_cammera.findViewById(R.id.llcamera_inner_view);
        requestOptions = new RequestOptions();
        String localTemp = Util.getDeviceLocale(spf);

        ivCameraPreview = dialog_cammera.findViewById(R.id.ivCameraPreview);

        if (localTemp.equalsIgnoreCase(AppConstants.LANGUAGE_CODE_ENGLISH)) {
            requestOptions.placeholder(R.drawable.loading);
            requestOptions.error(R.drawable.no_image);
        } else if (localTemp.equalsIgnoreCase(AppConstants.LANGUAGE_CODE_SPANISH)) {
            requestOptions.placeholder(R.drawable.loading_es);
            requestOptions.error(R.drawable.no_image_es);
        } else if (localTemp.equalsIgnoreCase(AppConstants.LANGUAGE_CODE_PORTUGUES)) {
            requestOptions.placeholder(R.drawable.loading_pt);
            requestOptions.error(R.drawable.no_images_pt);
        }

        if (objUri != null) {
            Glide.with(getActivity()).load(objUri).apply(requestOptions).into(ivCameraPreview);
        } else {

            //Bitmap bitmap = Util.decodeSampledBitmap(getActivity(), data.getData(), filePath);
            String path = android.os.Environment.getExternalStorageDirectory() + "/SLCScanner/Preview.jpg";
            File f = new File(path);
            Bitmap myBitmap = BitmapFactory.decodeFile(path);

            if (f.exists()) {
                ivCameraPreview.setImageBitmap(myBitmap);
            } else {
                if (imgUrl.toString().equalsIgnoreCase("")) {
                    Glide.with(getActivity()).load(R.drawable.add_photo_bg).into(ivCameraPreview);
                } else {
                    Glide.with(getActivity()).load(imgUrl).apply(requestOptions).into(ivCameraPreview);
                }
            }
        }

        //ivCameraPreview.setImageDrawable(getResources().getDrawable(R.drawable.loading_image));

        ivOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //upload to server
                //UploadSelectedImage();
                dialog_cammera.dismiss();
            }
        });

        ivChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivityForResult(getPickImageChooserIntent(), 200);
                selectImage();
            }
        });

        /*ivOk.setOnTouchListener(Util.colorFilter());
        ivChoose.setOnTouchListener(Util.color
        Filter());*/

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();

        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialog_cammera.getWindow().setLayout((int) (displayRectangle.width() *
                0.9f), (int) (displayRectangle.height() * 0.7f));

        dialog_cammera.show();
    }

    void getAddressAPI(Double lat, Double lng, String clientid) {
        dialog.show();
        objApi.getAddress(lat, lng, clientid, userid).enqueue(new Callback<CommonResponse2>() {
            @Override
            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (response.body() != null) {
                    CommonResponse2 response1 = response.body();

                    if (response1.getStatus().equalsIgnoreCase("success")) {
                        String address = response1.getAddress();
                        for (com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum : mList) {
                            if (objDatum.getAttributeName().toString().equalsIgnoreCase(getString(R.string.attribute_address))) {
                                objDatum.setBtnText(address);
                                objDatum.setSelectected(address);
                                mAdapter.notifyItemChanged(mList.indexOf(objDatum));
                                Log.i(AppConstants.TAG, "Index of " + mList.indexOf(objDatum));
                                break;
                            }
                        }
                        Util.addLog("Edit pole Data: Successfully get address from api");
                        dialog_googlemap.dismiss();

                        recyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                // Call smooth scroll
                                recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                            }
                        });

                    } else if (response1.getStatus().equalsIgnoreCase("error")) {
                        Util.dialogForMessage(getActivity(), response1.getMsg());
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.no_address_found), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonResponse2> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                Util.addLog("Edit pole Data: Failed to get address from api");
            }
        });
    }

    @Override
    public void onClickForControl(int position, com.
            CL.slcscanner.Pojo.ClientAssets.Datum objBean) {
        boolean flagForDialog = true;
        if (mList.get(position).getAttributeName().toString().equalsIgnoreCase(getString(R.string.attribute_date_of_installation))) {
            DateTimePicker(position, objBean);
            flagForDialog = false;
        } else if (mList.get(position).getAttributeName().equalsIgnoreCase(spf.getString(AppConstants.MACID_LABLE, "")))//getString(R.string.attribute_mac_id)
            flagForDialog = true;
        else if (mList.get(position).getAttributeName().equalsIgnoreCase(getString(R.string.attribute_slc_id)))
            flagForDialog = true;
        else if (mList.get(position).getAttributeName().equalsIgnoreCase(getString(R.string.attribute_pole_id)))
            flagForDialog = false;
        else if (mList.get(position).getAttributeName().equalsIgnoreCase(getString(R.string.attribute_address)))
            flagForDialog = false;


        if (flagForDialog) {
            if (mList.get(position).getAttributeName().equalsIgnoreCase(spf.getString(AppConstants.MACID_LABLE, ""))) {//getString(R.string.attribute_mac_id)
                //mac id dailog
                boolean mbool = mList.get(position).isMacIdClick();
                Log.i("---*", "Onclick Postion" + mbool);
                if (!mbool) {
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.mac_id_internal));
                } else {
                    ShowDialogMacID(getActivity(), mList.get(position).getBtnText(), position, false, "", mList.get(0).getSelectected());
                }
            } else if (mList.get(position).getAttributeName().equalsIgnoreCase(getString(R.string.attribute_slc_id))) {
                //slc id dailog
                if (!mList.get(position).isClickable()) {
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.slc_id_internal));
                } else {
                    ShowDialogMacID(getActivity(), mList.get(position).getBtnText(), position, true, "", mList.get(0).getSelectected());
                }
            } else if (mList.get(position).getAttributeName().equalsIgnoreCase(getString(R.string.attribute_node_type))) {


                /*int selectedIndex = 0;
                String tempNodeType = "";
                for (int i = 0; i < mClientType.size(); i++) {
                    if (mList.get(position).getSelectected().toString().equalsIgnoreCase(mClientType.get(i).getClientType())) {
                        selectedIndex = i;
                        tempNodeType = mClientType.get(i).getClientType();
                    }
                }*/

               /* SharedPreferences.Editor edit=spf.edit();
                edit.putString(AppConstants.SELECTED_NODE_TYPE_EDIT_MAC, tempNodeType);
                edit.putInt(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_MAC, selectedIndex);
                edit.commit();*/

               /* SharedPreferences.Editor edit = spf.edit();
                edit.putString(AppConstants.SELECTED_NODE_TYPE_EDIT_SLC, tempNodeType);
                edit.putInt(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_SLC, selectedIndex);
                edit.commit();
*/
                //opend dialog
                /*objUtil.ShowNodeTypeDialog(mClientType, getResources().getString(R.string.strNodeTypTitle), getActivity(), spf, AppConstants.EDIT_SLC_UI, new Util.ClickNodeType() {
                    @Override
                    public void setOnClickNodeType(com.CL.slcscanner.Pojo.Login.Datum objOnClickLampType, int NodeTypeposition) {

                        com.CL.slcscanner.Pojo.ClientAssets.Datum objBean = mList.get(position);
                        objBean.setBtnText(objOnClickLampType.getClientType());
                        objBean.setSelectected(objOnClickLampType.getClientType());

                        SharedPreferences.Editor editor = spf.edit();
                        editor.putString("BtnText" + position, objOnClickLampType.getClientType());
                        editor.putString("Selectected" + position, objOnClickLampType.getClientType());
                        editor.apply();

                        mAdapter.notifyItemChanged(position, objBean);

                        if (objOnClickLampType.getClientType().equalsIgnoreCase(getString(R.string.unknown)))
                            node_type_update_slc = "";
                        else
                            node_type_update_slc = objOnClickLampType.getClientType();

                    }
                });*/

            } else {

                if (objBean.getIspicklist().toString().equals("0")) {
                    String a = objBean.getAttributeName();
                    dialogFixture = OtherFixtureDialog(getActivity(), objBean.getAttrKey(),
                            String.format(getResources().getString(R.string.other_dialog_enter_data), a), getString(R.string.enter_data),
                            true, position, objBean.getType(), objBean.getSelectected());
                    dialogFixture.show();

                } else {
                    ShowDialog(objBean, position);
                }
            }
        }
    }

    private void ShowDialogMacID(Activity activity, final String result, final int position,
                                 final boolean isForSlc, String node_type, String mac_id) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dilog_mac_id_edit, null);
        TextView tvMacLBL = view.findViewById(R.id.tvMacLBL);


        int tempSlcPostion = 1;

        if (isForSlc) {
            tvMacLBL.setText(getResources().getString(R.string.slc_id));
        } else {
            tvMacLBL.setText(spf.getString(AppConstants.MACID_LABLE, ""));
            for (int i = 0; i < mList.size(); i++) {
                if (mList.get(i).getAttributeName().equalsIgnoreCase(getString(R.string.attribute_slc_id))) {
                    tempSlcPostion = i;
                    break;
                }
            }
        }

        builder.setView(view);
        builder.setCancelable(false);

        macDialog = builder.create();
        macDialog.show();

        //ProgressBar progressBar=view.findViewById(R.id.progressBar);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        final EditText etMacId = view.findViewById(R.id.etMacId);
        etMacId.setText(result);
        if (isForSlc) {
            etMacId.setHint(getString(R.string.slc_id));
            etMacId.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            etMacId.setHint(spf.getString(AppConstants.MACID_PH, ""));
            etMacId.setInputType(InputType.TYPE_CLASS_TEXT);
            etMacId.setFilters(new InputFilter[]{filter});
        }

        final Editable etext = etMacId.getText();
        Selection.setSelection(etext, etMacId.getText().length());

        Button btOK = view.findViewById(R.id.btnOK);
        Button btnCancle = view.findViewById(R.id.btnCancle);
        Button btnScan = view.findViewById(R.id.btnScan);

        int finalTempSlcPostion = tempSlcPostion;
        btOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (etMacId.getText().toString().equals("")) {
                    if (isForSlc)
                        etMacId.setError(getString(R.string.enter_slc_id));
                    else
                        etMacId.setError(String.format(getString(R.string.enter_mac_id), spf.getString(AppConstants.MACID_LABLE, "")));
                } else {
                    macDialog.dismiss();

                    if (isForSlc) {
                        com.CL.slcscanner.Pojo.ClientAssets.Datum objBean = mList.get(position);
                        objBean.setBtnText(etMacId.getText().toString());
                        objBean.setSelectected(etMacId.getText().toString());

                        SharedPreferences.Editor editor = spf.edit();
                        editor.putString("BtnText" + position, etMacId.getText().toString());
                        editor.putString("Selectected" + position, etMacId.getText().toString());
                        editor.apply();

                        mAdapter.notifyItemChanged(position, objBean);
                    } else
                        checkInternalUniqueMacAddressAPI(etMacId.getText().toString(), position, finalTempSlcPostion);

                }
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                macDialog.dismiss();
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                macDialog.dismiss();
                String tempSLCId, tempMacId;

                if (isForSlc) {
                    tempSLCId = result;
                    tempMacId = macID;
                } else {
                    tempSLCId = slcId;
                    tempMacId = result;
                }

                Log.i("@@@","macid: "+tempMacId+" SLC id: "+tempSLCId);

                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                ScannerForEdit fragment = new ScannerForEdit();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isNewData", false);
                bundle.putBoolean("IS_FROM_MAP", false);
                bundle.putString("ID", ID);
                bundle.putString("slcID", tempSLCId);
                bundle.putString("poleId", poleId);
                bundle.putDouble("Lat", Lat);
                bundle.putDouble("Long", Long);
                bundle.putString("MacId", tempMacId);
                bundle.putBoolean("isForSLC", isForSlc);


                //for mac id - node_type dialog
                bundle.putString(AppConstants.SELECTED_NODE_TYPE_EDIT_SLC,
                        spf.getString(AppConstants.SELECTED_NODE_TYPE_EDIT_SLC, "")
                );
                bundle.putInt(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_SLC,
                        spf.getInt(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_SLC, 0)
                );

                fragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.frm1, fragment);
                //fragmentTransaction.addToBackStack(AppConstants.TAG_SCANER_EDIT);
                fragmentTransaction.commit(); // save the changes
            }
        });

        etMacId.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // TODO do something
                    handled = true;
                }
                return handled;
            }
        });


    }


    @Override
    public void onClickForControl(int position, boolean from, String notes) {
        //loadFragment(new NoteFragment());

        Bundle mBundle = new Bundle();
        NoteFragment objNoteFragment = new NoteFragment();
        mBundle.putBoolean(AppConstants.ISVIEWONLY, false);
        mBundle.putString(AppConstants.FROM, "edit");

        Log.i(AppConstants.TAG2, notes);

        String pole_option = "";

        if (isNewData) {
            mBundle.putString(AppConstants.NOTES_POLE_OPTION, notes);
            pole_option = notes;
        } else {
            //String notesUpdate = spf.getString(AppConstants.NOTES, "") + "#" + notes;
            String editableString = spf.getString(AppConstants.NOTES_EDIT, "") + "#" + spf.getString(AppConstants.NOTES_POLE_OPTION_EDIT, "");

            if (noteBack) {
                if (noteUpdate) {
                    //mBundle.putString(AppConstants.NOTES_POLE_OPTION, notes);
                    pole_option = editableString;
                } else {
                    //mBundle.putString(AppConstants.NOTES_POLE_OPTION, editableString);
                    pole_option = notes;
                }
            } else { // default
                //mBundle.putString(AppConstants.NOTES_POLE_OPTION, pole_option);
                pole_option = notes;
            }
        }

        mBundle.putString("ID", ID);
        mBundle.putBoolean("IS_FROM_MAP", isFromMap);
        mBundle.putBoolean("isNewData", isNewData);
        mBundle.putString("slcID", slcId);
        mBundle.putString("MacId", macID);
        mBundle.putString("poleId", poleId);
        mBundle.putDouble("Long", Long);
        mBundle.putDouble("Lat", Lat);
        mBundle.putString(AppConstants.NOTES_POLE_OPTION, pole_option);

        objNoteFragment.setArguments(mBundle);

        objUtil.loadFragment(objNoteFragment, getActivity());
    }

    class GetAddress extends AsyncTask<Void, Void, String> {

        Double latitude, longitude;

        String address;

        public GetAddress(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Geocoder geo = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());

                if (geo.isPresent()) {
                    List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
                    if (addresses.isEmpty()) {
                        address = "Waiting for Location";
                    } else {
                        if (addresses.size() > 0) {
                            address = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                            //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                            Util.addLog("Selected address: " + addresses);
                            Log.i(AppConstants.TAG, "Address: " + address);
                        }
                    }
                } else {
                    address = "Waiting for Location";
                    Util.addLog("Geo coder not present");
                    Log.i(AppConstants.TAG, "Geo coder not present");
                }
            } catch (Exception e) {
                Util.addLog("Selected address Exception: " + e.getMessage());
            }
            return address;
        }

        @Override
        protected void onPostExecute(String address) {
            super.onPostExecute(address);

            for (com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum : mList) {

                if (objDatum.getAttrKey().contains("address")) {
                    objDatum.setBtnText(address);
                    objDatum.setSelectected(address);
                    mAdapter.notifyItemChanged(mList.indexOf(objDatum));
                    Log.i(AppConstants.TAG, "Index of " + mList.indexOf(objDatum));
                    break;
                }
            }
        }
    }

    void getClientAssets(String clientId, String units, final boolean isCopyFromPrevious) {

        //final boolean isCopyFromPrevious = spf.getBoolean(AppConstants.CopyFromPrevious, false);
        String CopyFromPrevious;
        if (isCopyFromPrevious) {
            CopyFromPrevious = "Yes";
        } else {
            CopyFromPrevious = "No";
        }

        dialog.show();
        objApi.getClientAssets(clientId, units, CopyFromPrevious).enqueue(new Callback<ClientAssestMaster>() {
            @Override
            public void onResponse(Call<ClientAssestMaster> call, Response<ClientAssestMaster> response) {

                if (getActivity() != null) {
                    mList.clear();
                    if (dialog.isShowing())
                        dialog.dismiss();

                    tbTitleSkip.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);

                    ClientAssestMaster objClientAssestMaster = response.body();

                    if (objClientAssestMaster != null) {

                        if (objClientAssestMaster.getSlccount().toString().equalsIgnoreCase("Yes") && isNewData) {
                            llCopyPoleData.setVisibility(View.VISIBLE);

                            if (isCopyFromPrevious) {
                                btnYes.setChecked(true);
                                btnNo.setChecked(false);
                            } else {
                                btnNo.setChecked(true);
                                btnYes.setChecked(false);
                            }

                        } else
                            llCopyPoleData.setVisibility(View.GONE);

                        //tvCopyText.setText(Html.fromHtml("<big>Copy</big> Pole Data from Last Entry"));

                        if (objClientAssestMaster.getStatus().toString().equals("success")) {

                            Log.i(AppConstants.TAG, "Assets data stored");
                            Util.addLog("Assets data stored");
                            mList.clear();

                            for (int i = 0; i < objClientAssestMaster.getData().size(); i++) {

                                String AttKey = Util.decodeUnicode(objClientAssestMaster.getData().get(i).getAttrKey());
                                String AttributeName = Util.decodeUnicode(objClientAssestMaster.getData().get(i).getAttributeName());

                                com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum = objClientAssestMaster.getData().get(i);

                                if (AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_IMAGE_ANAME)
                                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_IMAGE_ANAME_SPANISH)
                                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_IMAGE_ANAME_PT)) {
                                    spf.edit().putString(AppConstants.pole_image_key, AttKey).apply();
                                } else if (AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_ID_ANAME)
                                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_ID_ANAME_SPANISH)
                                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_ID_ANAME_PT)) {
                                    //nothing
                                    spf.edit().putString(AppConstants.pole_id_key, AttKey).apply();
                                } else if (AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_NOTES_ANAME)
                                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_NOTES_ANAME_SPANISH)
                                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_NOTES_ANAME_PT)) {
                                    //nothing
                                    spf.edit().putString(AppConstants.pole_notes_key, AttKey).apply();
                                } else {

                                    objDatum.setSelectected( objDatum.getSelectected());
                                    mList.add(objDatum);

                                    //mList.add(objClientAssestMaster.getData().get(i));
                                }
                            }
                            //mList.addAll(objClientAssestMaster.getData());

                            if (isCopyFromPrevious) {
                                Util.setAssetDataCopyFeatureSPF(spf, mList);
                              /*Glide.with(getActivity())
                                    .asBitmap()
                                    .load(objClientAssestMaster.getPole_image_url()).
                                    apply(requestOptions).
                                    into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                            ivCameraPreview.setImageBitmap(resource);
                                            storeImage(resource, "Preview");
                                        }
                                    });*/

                                spf.edit().putString(AppConstants.SPF_UNITS_FOR_COPY_FEATURE, objClientAssestMaster.getMeasurement_units()).apply();

                            } else {
                                Util.setAssetDataSPF(spf, mList);

                                com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum2 = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
                                objDatum2.setBtnText(getString(R.string.select_date));
                                objDatum2.setAttributeName(getString(R.string.attribute_date_of_installation));
                                objDatum2.setAttrKey("date_of_installation");
                                objDatum2.setSelectected("");
                                objDatum2.setStaticData(true);
                                objDatum2.setNoteData(false);
                                mList.add(objDatum2);
                            }
                            mAdapter.notifyDataSetChanged();

                        } else {
                            Log.i(AppConstants.TAG, "Assets data stored Failed !");
                            Util.addLog("Assets data stored Failed !");
                        }
                    }
                    llBotomViewEdit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ClientAssestMaster> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();

                llBotomViewEdit.setVisibility(View.GONE);
            }
        });
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
                    Util.dialogForMessage(getActivity(), getString(R.string.empty_username));
                } else if (etPass.getText().toString().equals("")) {
                    Util.dialogForMessage(getActivity(), getString(R.string.empty_password));
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
                        Util.dialogForMessage(getActivity(), getString(R.string.empty_username));
                    } else if (etPass.getText().toString().equals("")) {
                        Util.dialogForMessage(getActivity(), getString(R.string.empty_password));
                    } else {
                        if (Util.isInternetAvailable(getActivity()))
                            sendLoginDetailToSever(etUsername.getText().toString(), etPass.getText().toString(), 0.0, 0.0);
                        else
                            Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
                        //login call
                    }
                }
                return handled;
            }
        });

        builder.setCancelable(false);
        builder.setView(view);
        return builder.create();
    }

    void sendLoginDetailToSever(String username, String password, Double lattitude, Double
            longitude) {
        String cleintid = spf.getString(AppConstants.CLIENT_ID, null);
        String userId = spf.getString(AppConstants.USER_ID, null);
        loginDialog = new ProgressDialog(getActivity());
        loginDialog.setMessage(getString(R.string.saving));
        loginDialog.setCancelable(false);
        loginDialog.show();

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
                if (loginDialog.isShowing()) {
                    loginDialog.dismiss();
                }

                if (response.body() != null) {
                    if (objCommonResponse.getStatus().toString().equalsIgnoreCase("success")) {
                        spf.edit().putBoolean(AppConstants.ISLOGGEDIN, true).apply();
                        Toast.makeText(getActivity(), objCommonResponse.getMsg().toString(), Toast.LENGTH_SHORT).show();
                        Util.hideKeyboard(getActivity());
                        Util.addLog("Dialog: Login Successful");

                        if (loginAlertDialog.isShowing())
                            loginAlertDialog.dismiss();

                        /*List<com.CL.slcscanner.Pojo.Login.Datum> objData = objCommonResponse.getNodeTypeLg().getData();

                        List<com.CL.slcscanner.Pojo.Login.Datum> objList = new ArrayList<>();
                        com.CL.slcscanner.Pojo.Login.Datum objDatum = new com.CL.slcscanner.Pojo.Login.Datum();
                        objDatum.setClientType(getString(R.string.status_all));
                        objDatum.setValue("");
                        objList.add(objDatum);

                        com.CL.slcscanner.Pojo.Login.Datum objDatum1 = new com.CL.slcscanner.Pojo.Login.Datum();
                        objDatum1.setClientType(getString(R.string.unknown));
                        objDatum1.setValue("");
                        objList.add(objDatum1);
                        objList.addAll(objData);

                        Util.SaveClientTypeList(spf, objList);*/

                        if (Util.isInternetAvailable(getActivity())) {
                            if (isNewData) {
                                getClientAssets(clientId, units, isCopyFromPrevious);
                            } else {
                                callAPIGetDetails2(ID);
                            }

                        } else {
                            Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
                        }

                    } else if (objCommonResponse.getStatus().toString().equalsIgnoreCase("error")) {
                        Util.dialogForMessage(getActivity(), objCommonResponse.getMsg().toString());
                        Util.addLog("Dialog: Login failed");
                        //startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
                if (loginDialog.isShowing()) {
                    loginDialog.dismiss();
                }
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
            }
        });
    }

    /**
     * Get URI to image received from capture by camera.
     */
    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getActivity().getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
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
                    dialogCammera();
                }
                return;
            }
        }
    }

    private void selectImage() {
        final CharSequence[] items = {getString(R.string.photo_library), getString(R.string.camera),
                getString(R.string.cancle)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.select_pole_image));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(getString(R.string.camera))) {
                    userChoosenTask = getString(R.string.camera);
                    cameraIntent();
                } else if (items[item].equals(getString(R.string.photo_library))) {
                    userChoosenTask = getString(R.string.photo_library);
                    galleryIntent();
                } else if (items[item].equals(getString(R.string.cancle))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            //Create the File where the photo should go
            File f = new File(android.os.Environment.getExternalStorageDirectory(), "/SLCScanner/Preview.jpg");
            filePath = f.getAbsolutePath();

            if (f != null) {
                if (Build.VERSION.SDK_INT >= 24)
                    objUri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", f);
                else
                    objUri = Uri.fromFile(f);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, objUri);
                intent.putExtra("return-data", true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent, REQUEST_CAMERA);
            }
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        //startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_file)), SELECT_FILE);
        startActivityForResult(intent,SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        try {

            Bitmap bitmap = Util.decodeSampledBitmap(getActivity(), data.getData(), filePath);
            storeImage1(bitmap, "Preview");
            ivCameraPreview.setImageBitmap(bitmap);

        } catch (Exception e) {

        }
    }

    String Path;

    private void onCaptureImageResult(Intent data) {
        try {
            Bitmap bitmap = Util.decodeSampledBitmap(getActivity(), objUri, filePath);
            storeImage1(bitmap, "Preview");
            ivCameraPreview.setImageBitmap(bitmap);
        } catch (Exception e) {

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //objUri=null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new Util().dismissProgressDialog(dialog);
        new Util().dismissProgressDialog(dialog_save);
    }

    void checkInternalUniqueMacAddressAPI(String macIdFinal, int postion, int tempSLC) {
        dialog.show();
        objApi.checkInternalUniqueMacAddressEditAPI(
                userid,
                clientId,
                macIdFinal,
                "Android"
        ).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if (dialog.isShowing())
                    dialog.dismiss();

                if (response.body() != null) {
                    CommonResponse response1 = response.body();

                    if (response1.getStatus().equalsIgnoreCase(AppConstants.Internal)) {
                        //if(dialog.isShowing())
                        //  dialog.dismiss();
                        //spf.edit().putString(AppConstants.SPF_TEMP_SLCID, response1.getSlc_id()).apply();
                        //spf.edit().putString(AppConstants.SPF_TEMP_MACID, macIdFinal.toString()).apply();
                        //spf.edit().putString(AppConstants.SPF_TEMP_SLC_STATUS,AppConstants.Internal);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(response1.getMsg().toString())
                                .setCancelable(false);
                        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                //updateMacId(macIdFinal, postion, tempSLC, response1.getSlc_id(), false);
                                updateMacId(macID, postion, tempSLC, spf.getString("tempSLCIDLive", ""), true);
                            }
                        });

                      /*  builder.setNegativeButton(getResources().getString(R.string.no_camel_case), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                updateMacId(macIdFinal, postion, tempSLC, spf.getString("tempSLCIDLive",""), false);
                            }
                        });*/

                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manuallyc
                        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        alert.show();


                    } else if (response1.getStatus().equalsIgnoreCase(AppConstants.EXTERNAL)) {
                        //checkMACIDAPICall(macIdFinal,postion,tempSLC,response1.getSlc_id(),true);
                        //spf.edit().putString(AppConstants.SPF_TEMP_SLC_STATUS,AppConstants.EXTERNAL);

                        //updateMacId(macIdFinal, postion, tempSLC, response1.getSlc_id(), true);
                        updateMacId(macIdFinal, postion, tempSLC, response1.getSlc_id(), true);
                        macID=macIdFinal;

                        /*com.CL.slcscanner.Pojo.ClientAssets.Datum objBean = mList.get(postion);
                        objBean.setBtnText(macIdFinal);
                        objBean.setSelectected(macIdFinal);
                        objBean.setClickable(true);
                        com.CL.slcscanner.Pojo.ClientAssets.Datum objBean = mList.get(postion);
                        objBean.setBtnText(macIdFinal);
                        objBean.setSelectected(macIdFinal);
                        objBean.setClickable(true);
                        SharedPreferences.Editor editor = spf.edit();
                        editor.putString("BtnText" + postion, macIdFinal);
                        editor.putString("Selectected" + postion,macIdFinal);
                        editor.apply();
                        mAdapter.notifyItemChanged(postion, objBean);*/
                    }

                } else {
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.server_error));
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                Util.dialogForMessage(getActivity(), getResources().getString(R.string.server_error));
                Util.addLog("Forgot API OnFailure called: " + t.getMessage());
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    void checkMACIDAPICall(final String id, int position, int tempSlc, String slcId,
                           boolean isSLCClickable) {
        //progressDialog.show();
        dialog.show();
        String node_type_temp;
        if (spf.getString(AppConstants.SELECTED_NODE_TYPE_EDIT_SLC, "").equalsIgnoreCase(getString(R.string.unknown))) {
            node_type_temp = "";
        } else
            node_type_temp = spf.getString(AppConstants.SELECTED_NODE_TYPE_EDIT_SLC, "");

//node_type_temp
        objApi.checkMACId(id, clientId, userid, "").enqueue(new Callback<CommonResponse2>() {
            @Override
            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {
                try {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    if (response.body() != null) {
                        CommonResponse2 response1 = response.body();
                        if (response1.getStatus().equals("success")) {
                            //spf.edit().putString(AppConstants.SPF_TEMP_MACID, id.toString()).apply();

                            //objUtil.loadFragment(new ScanSLCId(), getActivity());
                            //Toast.makeText(getActivity(),""+response1.getAddress().toString(),Toast.LENGTH_LONG).show();
                            updateMacId(id, position, tempSlc, slcId, isSLCClickable);
                            Util.addLog("Valid MAC ID: " + id);
                            //updateMacId(id,position);

                        } else if (response1.getStatus().equalsIgnoreCase("logout")) {

                            spf.edit().putBoolean(AppConstants.ISLOGGEDIN, false).apply();
                            spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, response1.getSLCID()).apply();

                            objUtil.commonLoginDialog(getActivity(), spf.getString(AppConstants.MACID_LABLE, "") + " " + getResources().getString(R.string.address_title));

                            Toast.makeText(getActivity(), response1.getMsg(), Toast.LENGTH_SHORT).show();
                            Util.addLog(spf.getString(AppConstants.MACID_LABLE, "") + "ADDRESS UI" + response1.getMsg());
                            Log.i(AppConstants.TAG, response1.getMsg());
                        } else {
                            Util.dialogForMessage(getActivity(), response1.getMsg());
                        }
                    } else
                        Util.dialogForMessage(getActivity(), getActivity().getResources().getString(R.string.server_error));
                } catch (Exception e) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<CommonResponse2> call, Throwable t) {
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });
    }

    void updateMacId(String macid, int position, int tempSlc, String slcId,
                     boolean isSLCClickable) {
        com.CL.slcscanner.Pojo.ClientAssets.Datum objBean = mList.get(position);
        objBean.setBtnText(macid);
        objBean.setSelectected(macid);
        objBean.setClickable(isSLCClickable);

        SharedPreferences.Editor editor = spf.edit();
        editor.putString("BtnText" + position, macid);
        editor.putString("Selectected" + position, macid);
        editor.apply();

        mAdapter.notifyItemChanged(position, objBean);

        com.CL.slcscanner.Pojo.ClientAssets.Datum objBean1 = mList.get(tempSlc);
        objBean1.setBtnText(slcId);
        objBean1.setSelectected(slcId);
        objBean1.setClickable(isSLCClickable);

        SharedPreferences.Editor editor1 = spf.edit();
        editor1.putString("BtnText" + tempSlc, slcId);
        editor1.putString("Selectected" + tempSlc, slcId);
        editor1.putBoolean("isClickable" + tempSlc, isSLCClickable);
        editor1.apply();
        mAdapter.notifyItemChanged(tempSlc, objBean);
    }
}

    /*
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//onCaptureImageResult
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(), "/SLCScanner/" + "Preview.jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //imageUri = Uri.fromFile(destination);

        //Path = new Util().getRealPathFromURI(imageUri, getActivity());
*/

    /*void sendDataToCameraPreview(String skipValue, String DOI, String address, String assests) {
        Bundle mBundle1 = new Bundle();

        mBundle1.putString(AppConstants.BUNDLE_BACK_NAV_TITTLE, "EDIT");
        mBundle1.putBoolean(AppConstants.BUNDLE_IS_FROM_MAP, isFromMap);
        mBundle1.putBoolean(AppConstants.BUNDLE_ISNEWDATA, isNewData);
        mBundle1.putString(AppConstants.BUNDLE_ID, ID);

        mBundle1.putString(AppConstants.BUNDLE_MACID, macID);
        mBundle1.putString(AppConstants.BUNDLE_SLCID, slcId);
        mBundle1.putString(AppConstants.BUNDLE_POLE_ID, poleId);

        mBundle1.putString(AppConstants.BUNDLE_SKIPP, skipValue);
        mBundle1.putString(AppConstants.BUNDLE_DOI, DOI);
        mBundle1.putString(AppConstants.BUNDLE_ADDRESS, address);

        mBundle1.putString(AppConstants.BUNDLE_ASSETS, assests);
        mBundle1.putDouble(AppConstants.BUNDLE_LATTITUDE, dragLat);
        mBundle1.putDouble(AppConstants.BUNDLE_LONGITUDE, dragLong);

        CameraPreviewFragment fragment = new CameraPreviewFragment();
        fragment.setArguments(mBundle1);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.replace(R.id.frm1, fragment);
        fragmentTransaction.commit();
    }*/

     /* void getClientAssets2(String clientId, String units, final boolean isCopyFromPrevious) {

        //final boolean isCopyFromPrevious = spf.getBoolean(AppConstants.CopyFromPrevious, false);
        String CopyFromPrevious;
        if (isCopyFromPrevious) {
            CopyFromPrevious = "Yes";
        } else {
            CopyFromPrevious = "No";
        }

        dialog.show();
        objApi.getClientAssets2(clientId, units, CopyFromPrevious).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if (getActivity() != null) {
                    mList.clear();
                    if (dialog.isShowing())
                        dialog.dismiss();

                    tbTitleSkip.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.VISIBLE);

                    try {

                        String objClientAssestMaster = response.body();
                        if (objClientAssestMaster != null) {

                            JSONObject objJson = new JSONObject(objClientAssestMaster);

                            objJson.getString()

                            if (objClientAssestMaster.getSlccount().toString().equalsIgnoreCase("Yes") && isNewData) {
                                llCopyPoleData.setVisibility(View.VISIBLE);

                                if (isCopyFromPrevious) {
                                    btnYes.setChecked(true);
                                    btnNo.setChecked(false);
                                } else {
                                    btnNo.setChecked(true);
                                    btnYes.setChecked(false);
                                }

                            } else
                                llCopyPoleData.setVisibility(View.GONE);

                            //tvCopyText.setText(Html.fromHtml("<big>Copy</big> Pole Data from Last Entry"));

                            if (objClientAssestMaster.getStatus().toString().equals("success")) {

                                Log.i(AppConstants.TAG, "Assets data stored");
                                Util.addLog("Assets data stored");
                                mList.clear();
                                mList.addAll(objClientAssestMaster.getData());

                                if (isCopyFromPrevious) {
                                    Util.setAssetDataCopyFeatureSPF(spf, mList);
                            *//*Glide.with(getActivity())
                                    .asBitmap()
                                    .load(objClientAssestMaster.getPole_image_url()).
                                    apply(requestOptions).
                                    into(new SimpleTarget<Bitmap>() {
                                        @Override
                                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                            ivCameraPreview.setImageBitmap(resource);
                                            storeImage(resource, "Preview");
                                        }
                                    });*//*

                                    spf.edit().putString(AppConstants.SPF_UNITS_FOR_COPY_FEATURE, objClientAssestMaster.getMeasurement_units()).apply();

                                } else {

                                    Util.setAssetDataSPF(spf, mList);
                                    com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum2 = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
                                    objDatum2.setBtnText(getString(R.string.select_date));
                                    objDatum2.setAttributeName(getString(R.string.attribute_date_of_installation));
                                    objDatum2.setAttrKey("date_of_installation");
                                    objDatum2.setSelectected("");
                                    objDatum2.setStaticData(true);
                                    mList.add(objDatum2);
                                }

                                mAdapter.notifyDataSetChanged();

                            } else {
                                Log.i(AppConstants.TAG, "Assets data stored Failed !");
                                Util.addLog("Assets data stored Failed !");
                            }
                        }

                        llBotomViewEdit.setVisibility(View.VISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();

                llBotomViewEdit.setVisibility(View.GONE);
            }
        });
    }*/