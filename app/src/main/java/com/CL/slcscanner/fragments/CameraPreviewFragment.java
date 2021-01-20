package com.CL.slcscanner.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.CL.slcscanner.Activities.LoginActivity;
import com.CL.slcscanner.Activities.MainActivity;

import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.ClientAssets.ClientAssestMaster;
import com.CL.slcscanner.Pojo.ClientAssets.Datum;
import com.CL.slcscanner.Pojo.CommonResponse;
import com.CL.slcscanner.Pojo.CommonResponse2;
import com.CL.slcscanner.Pojo.Login.LoginResponse;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.Util;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

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

import static com.CL.slcscanner.Utils.Util.getRequestBody;
import static com.CL.slcscanner.Utils.Util.storeImage;
import static com.CL.slcscanner.Utils.Util.storeImage1;

/**
 * Created by vrajesh on 6/8/2018.
 */

public class CameraPreviewFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.llPoleBack)
    LinearLayout llPoleBack;
    @BindView(R.id.tvPoleEditBack)
    TextView tvPoleEditBack;

    @BindView(R.id.ivCameraPreview)
    ImageView ivCameraPreview;
    @BindView(R.id.ivSelectImage)
    Button ivSelectImage;
    @BindView(R.id.ivOk)
    Button ivOk;

    @BindView(R.id.ivNext)
    Button ivNext;

    View view;
    String backNav;

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    String userChoosenTask;

    ProgressDialog dialog;
    API objApi;
    String clientId,
            userid,
            macID,
            slcId,
            poleId;

    String DOI, address, assets, tempSlc, units;
    Double lattitude, longitude;

    SharedPreferences spf;

    AlertDialog loginAlertDialog;
    ProgressDialog loginDialog;
    EditText etUsername;
    EditText etPass;

    String GlobalSLCId = "";
    Bundle mBundle;

    boolean isFromMap = false;
    boolean isNewData;
    String ID;

    @BindView(R.id.ivCameraPreviewBg)
    ImageView ivCameraPreviewBg;

    @BindView(R.id.llcamera_inner_view)
    LinearLayout llcamera_inner_view;

    ArrayList<Datum> mList;

    boolean isClickable = true;
    boolean isCopyFromPrevious = false;

    RequestOptions requestOptions;

    Util objUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.camerra_preview_fragment, null);
        init();
        return view;
    }

    void init() {
        ButterKnife.bind(this, view);

        ivNext.setVisibility(View.GONE);
        ivOk.setVisibility(View.VISIBLE);

        objUtil = new Util();

        getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
        getActivity().findViewById(R.id.llNodeType).setVisibility(View.GONE);

        mBundle = getArguments();
        loginAlertDialog = loginDialog(getActivity());
        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        objApi = new SLCScanner().networkCall();
        llPoleBack.setOnClickListener(this);
        ivSelectImage.setOnClickListener(this);
        ivOk.setOnClickListener(this);
        ivNext.setOnClickListener(this);

        mList = new ArrayList<>();

        //isCopyFromPrevious = spf.getBoolean(AppConstants.CopyFromPrevious, false);
        Util.deletePreviewFile();

        Glide.with(getActivity()).load(R.drawable.bg_1_1242_2208).into(ivCameraPreviewBg);

        //spf
        clientId = spf.getString(AppConstants.CLIENT_ID, "");
        userid = spf.getString(AppConstants.USER_ID, "");
        GlobalSLCId = spf.getString(AppConstants.SPF_LOGOUT_SLCID, "");
        units = spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC);
        macID = spf.getString(AppConstants.SPF_TEMP_MACID, "");
        slcId = spf.getString(AppConstants.SPF_TEMP_SLCID, "");
        lattitude = Double.valueOf(spf.getString(AppConstants.SPF_DRAG_LATTITUDE, "0.0"));
        longitude = Double.valueOf(spf.getString(AppConstants.SPF_DRAG_LONGITUDE, "0.0"));
        address = spf.getString(AppConstants.ADDRESS, "");

        if (getActivity() != null) {
            ((MainActivity) getActivity()).selectPole(false);
            ((MainActivity) getActivity()).selectScan(true);
            ((MainActivity) getActivity()).selectSetting(false);
            ((MainActivity) getActivity()).selectMap(false);
        }

        spf.edit().putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, AppConstants.SPF_CAMERA_FRAG).apply();

        tvPoleEditBack.setText(getResources().getString(R.string.address_title));

        ivCameraPreview.setOnClickListener(this);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getResources().getString(R.string.loading));
        dialog.setCancelable(false);

        /*
        if (isCopyFromPrevious) {
            getClientAssets(clientId, units);
        } else {
            mList.addAll(Util.gettAssetDataSPF(spf));
        }
        */

        requestOptions = new RequestOptions();
        String localTemp = Util.getDeviceLocale(spf);
        if (localTemp.equalsIgnoreCase(AppConstants.LANGUAGE_CODE_ENGLISH)) {
            requestOptions.placeholder(R.drawable.loading);
        } else if (localTemp.equalsIgnoreCase(AppConstants.LANGUAGE_CODE_SPANISH)) {
            requestOptions.placeholder(R.drawable.loading_es);
        } else if (localTemp.equalsIgnoreCase(AppConstants.LANGUAGE_CODE_PORTUGUES)) {
            requestOptions.placeholder(R.drawable.loading_pt);
        }
        requestOptions.error(R.drawable.add_photo_bg);

        /*requestOptions.placeholder(R.drawable.loading);
        requestOptions.error(R.drawable.add_photo_ios)*/
        ;

        Glide.with(getActivity()).load(R.drawable.add_photo_bg).apply(requestOptions).into(ivCameraPreview);
        llcamera_inner_view.setVisibility(View.VISIBLE);

        //filePath=spf.getString(AppConstants.CAMERA_FILE_PATH,"");
        //File imgFile = new  File(filePath);


        //file not able to read blank bitmap
        /*File imgFile = new File(android.os.Environment.getExternalStorageDirectory(), "/SLCScanner/Preview.jpg");
        filePath = imgFile.getAbsolutePath();
        if(imgFile.exists()){
            //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
           // Bitmap myBitmap= null;
            try {
                if (imgFile != null) {
                    if (Build.VERSION.SDK_INT >= 24)
                        objUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", imgFile);
                    else
                        objUri = Uri.fromFile(imgFile);
                }
                Bitmap myBitmap = Util.decodeSampledBitmap(getActivity().getApplicationContext() , objUri, filePath);
                //Bitmap myBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), objUri);

                if(myBitmap!=null && !myBitmap.equals("")) {
                    ivCameraPreview.setImageBitmap(myBitmap);
                    isClickable = false;
                    ivNext.setVisibility(View.VISIBLE);
                    ivOk.setVisibility(View.GONE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llPoleBack:
                objUtil.loadFragment(new AddressFragement(), getActivity());
                break;
            case R.id.ivSelectImage:
                selectImage();
                break;

            case R.id.ivCameraPreview:
                // String TagName=String.valueOf(ivCameraPreview.getTag());
                if (isClickable) {
                    selectImage();
                    //userChoosenTask = "Camera";
                    //cameraIntent();
                }
                break;

            case R.id.ivOk:
            case R.id.ivNext:

                Bundle mBundle = new Bundle();
                NoteFragment objNoteFragment = new NoteFragment();
                mBundle.putBoolean(AppConstants.ISVIEWONLY, false);
                mBundle.putString(AppConstants.FROM, "camera");
                objNoteFragment.setArguments(mBundle);

                objUtil.loadFragment(objNoteFragment, getActivity());
                Util.addLog("Camera Preview UI: Navigate to POLE ID");

             /*   if (spf.getBoolean(AppConstants.SPF_POLE_ID_VISIBILITY, true)) {


                } else if (spf.getBoolean(AppConstants.SPF_OTHER_DATA_VISIBILITY, true)) {

                    Util.addLog("Location Accept UI: Navigate to POLE Details");
                    PoleDataEditFragment objFragment = new PoleDataEditFragment();
                    Bundle mBundle = new Bundle();

                    mBundle.putString("ID", "");
                    mBundle.putBoolean("IS_FROM_MAP", false);
                    mBundle.putBoolean("isNewData", true);
                    mBundle.putString("slcID", slcId);
                    mBundle.putString("poleId", "");
                    mBundle.putDouble("Lat", lattitude);
                    mBundle.putDouble("Long", longitude);

                    mBundle.putString("MacId", macID);
                    mBundle.putBoolean(AppConstants.isfromNote,false);
                    objFragment.setArguments(mBundle);
                    objUtil.loadFragment(objFragment, getActivity());

                } else {
                    //place API call for save new data
                    Util.addLog("Camera Preview UI: Calling Save Data");
                    //mList.clear();
                    //mList.addAll(Util.gettAssetDataSPF(spf));

                    HashMap<String, String> map1 = new HashMap<>();
                    for (int i = 0; i < mList.size(); i++) {
                        if (!mList.get(i).isStaticData()) {
                            map1.put(mList.get(i).getAttrKey(), "");
                        }
                    }

                    JSONObject json1 = new JSONObject(map1);
                    assets = json1.toString();
                    Log.i(AppConstants.TAG, json1.toString());

                    if (Util.isInternetAvailable(getActivity()))
                        saveNewSLCData();
                    else
                        Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
                }*/
                break;
        }

    }

     /*else if (spf.getBoolean(AppConstants.CopyFromPrevious, false)) {
        //place API call for save new data
        Util.addLog("Camera Preview UI: Calling Save Data");
        mList.clear();
        mList.addAll(Util.gettAssetDataCopyFeatureSPF(spf));
        HashMap<String, String> map1 = new HashMap<>();
        for (int i = 0; i < mList.size(); i++) {
            if (!mList.get(i).isStaticData()) {
                map1.put(mList.get(i).getAttrKey(), mList.get(i).getSelectected());
            }
        }

        JSONObject json1 = new JSONObject(map1);
        assets = json1.toString();
        Log.i(AppConstants.TAG, json1.toString());

        saveNewSLCData();
    }*/

    private void selectImage() {
        final CharSequence[] items = {getResources().getString(R.string.photo_library), getResources().getString(R.string.camera),
                getResources().getString(R.string.cancle)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.select_pole_image));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //https://arkapp.medium.com/accessing-images-on-android-10-scoped-storage-bbe65160c3f4
                if (items[item].equals(getResources().getString(R.string.camera))) {
                    userChoosenTask = getResources().getString(R.string.camera);
                    cameraIntent();
                } else if (items[item].equals(getResources().getString(R.string.photo_library))) {
                    userChoosenTask = getResources().getString(R.string.photo_library);
                    galleryIntent();
                } else if (items[item].equals(getResources().getString(R.string.cancle))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    Uri objUri;
    String filePath;

    private void cameraIntent2(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CAMERA);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Ensure that there's a camera activity to handle the intent
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            //Create the File where the photo should go
            File f = new File(android.os.Environment.getExternalStorageDirectory(), "/SLCScanner/Preview.jpg");
            //File f=new File(android.os.Environment.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "/Preview.jpg");
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
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_file)), SELECT_FILE);
        startActivityForResult(intent,SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }


    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bitmap = null;
        try {
            ivNext.setVisibility(View.VISIBLE);
            ivOk.setVisibility(View.GONE);
            llcamera_inner_view.setVisibility(View.GONE);

            bitmap = Util.decodeSampledBitmap(getActivity(), data.getData(), filePath);
            storeImage1(bitmap, getResources().getString(R.string.preview));
            ivCameraPreview.setImageBitmap(bitmap);
            isClickable = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = null;
        try {
            ivNext.setVisibility(View.VISIBLE);
            ivOk.setVisibility(View.GONE);
            llcamera_inner_view.setVisibility(View.GONE);

            bitmap = Util.decodeSampledBitmap(getActivity(), objUri, filePath);
            storeImage1(bitmap, getResources().getString(R.string.preview));

            ivCameraPreview.setImageBitmap(bitmap);
            isClickable = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getClientAssets(String clientId, String units) {

        dialog.show();
        objApi.getClientAssets(clientId, units, "Yes").enqueue(new Callback<ClientAssestMaster>() {
            @Override
            public void onResponse(Call<ClientAssestMaster> call, Response<ClientAssestMaster> response) {
                mList.clear();
                if (dialog.isShowing())
                    dialog.dismiss();

                //btnSkip.setVisibility(View.VISIBLE);
                //btnSave.setVisibility(View.VISIBLE);

                ClientAssestMaster objClientAssestMaster = response.body();
                if (objClientAssestMaster != null) {
                    if (objClientAssestMaster.getStatus().toString().equals("success")) {

                        Log.i(AppConstants.TAG, "Assets data stored");
                        Util.addLog("Assets data stored");

                        mList.clear();
                        for (int i = 0; i < objClientAssestMaster.getData().size(); i++) {
                            String AttributeName = Util.decodeUnicode(objClientAssestMaster.getData().get(i).getAttributeName());
                            String AttKey = objClientAssestMaster.getData().get(i).getAttrKey();
                            if (AttKey.toLowerCase().equalsIgnoreCase(AppConstants.POLE_IMAGE)
                                    || AttKey.toLowerCase().equalsIgnoreCase(AppConstants.POLE_IMAGE_SPANISH)
                                    || AttKey.toLowerCase().equalsIgnoreCase(AppConstants.POLE_IMAGE_PT)
                            ) {
                                spf.edit().putString(AppConstants.pole_image_key, Util.decodeUnicode(AttKey)).apply();
                            } else {
                                mList.add(objClientAssestMaster.getData().get(i));
                            }
                        }

                        Util.setAssetDataCopyFeatureSPF(spf, mList);
                        //Util.setAssetDataCopyFeatureSPF(spf, objClientAssestMaster.getData());

                        try {
                            String unit;

                            if (isCopyFromPrevious) {
                                spf.edit().putString(AppConstants.SPF_TEMP_POLE_ID_COPY_FEATURE, objClientAssestMaster.getPole_id()).commit();
                                Glide.with(getActivity())
                                        .asBitmap()
                                        .load(objClientAssestMaster.getPole_image_url()).

                                        apply(requestOptions).
                                        into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                                ivCameraPreview.setImageBitmap(resource);
                                                storeImage1(resource, "Preview");
                                                llcamera_inner_view.setVisibility(View.GONE);
                                            }
                                        });

                                unit = objClientAssestMaster.getMeasurement_units();
                                spf.edit().putString(AppConstants.SPF_UNITS_FOR_COPY_FEATURE, objClientAssestMaster.getMeasurement_units()).apply();
                                //isClickable=false;

                                if (!unit.toString().equalsIgnoreCase(spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC))) {
                                    Util.dialogForMessage(getActivity(), objClientAssestMaster.getMsg().toString());
                                }
                            }
                        } catch (Exception e) {

                        }

                    } else {

                        Log.i(AppConstants.TAG, "Assets data stored Failed !");
                        Util.addLog("Assets data stored Failed !");
                    }
                }
            }

            @Override
            public void onFailure(Call<ClientAssestMaster> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        });

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

        ArrayList<Datum> mListNotes = new ArrayList<>();
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

                        Util.deletePreviewFile();

                    } else if (response1.getStatus().equalsIgnoreCase("logout")) {

                        if (dialog.isShowing())
                            dialog.dismiss();

                        Log.i(AppConstants.TAG, response1.getMsg());

                        spf.edit().putBoolean(AppConstants.ISLOGGEDIN, false).apply();

                        etPass.setText("");
                        etUsername.setText("");
                        loginAlertDialog.show();
                        GlobalSLCId = response1.getSLCID();
                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, response1.getSLCID()).apply();

                        Toast.makeText(getActivity(), response1.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.i(AppConstants.TAG, response1.getMsg());
                        Util.addLog("Camera Preview UI: Save new data error" + response1.getMsg());

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
                        sendLoginDetailToSever(etUsername.getText().toString(), etPass.getText().toString(), 0.0, 0.0);
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
        loginDialog = new ProgressDialog(getActivity());
        loginDialog.setMessage(getResources().getString(R.string.loading));
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
                        Util.addLog("Camera Preview UI Dialog: Login Successful");

                        if (loginAlertDialog.isShowing())
                            loginAlertDialog.dismiss();

                       /* if (isNewData)
                            getClientAssets(clientId, units);
                        else
                            callAPIGetDetails(ID);*/

                    } else if (objCommonResponse.getStatus().toString().equalsIgnoreCase("error")) {
                        Util.dialogForMessage(getActivity(), objCommonResponse.getMsg().toString());
                        Util.addLog("Camera Preview UI Dialog: Login failed");
                        //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else if (objCommonResponse.getStatus().toString().equalsIgnoreCase("-1")) {
                      //  Util.dialogForMessage(getActivity(), objCommonResponse.getMsg().toString());
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

    void loadEditData() {

        if (backNav.toString().equalsIgnoreCase(getResources().getString(R.string.address_title))) {
            loadFragment(new AddressFragement());
        } else if (backNav.toString().equalsIgnoreCase(getResources().getString(R.string.pole_id))) {
            loadFragment(new PoleIdFragment());
        } else if (backNav.toString().equalsIgnoreCase(getResources().getString(R.string.edit)) || backNav.toString().equalsIgnoreCase(getResources().getString(R.string.back))) {

            Bundle mBundle = new Bundle();
            mBundle.putBoolean(AppConstants.BUNDLE_IS_FROM_MAP, isFromMap);
            mBundle.putBoolean(AppConstants.BUNDLE_ISNEWDATA, isNewData);
            mBundle.putString(AppConstants.BUNDLE_ID, ID);

            mBundle.getString(AppConstants.BUNDLE_MACID, macID);
            mBundle.getString(AppConstants.BUNDLE_SLCID, slcId);
            mBundle.getString(AppConstants.BUNDLE_POLE_ID, poleId);

            mBundle.putDouble(AppConstants.BUNDLE_LATTITUDE, lattitude);
            mBundle.putDouble(AppConstants.BUNDLE_LONGITUDE, longitude);
            mBundle.putBoolean(AppConstants.isfromNote, false);

            PoleDataEditFragment fragment = new PoleDataEditFragment();
            fragment.setArguments(mBundle);

            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.frm1, fragment);
            fragmentTransaction.commit();
        }

    }
    //saveNewSLCData("No", "", edtAddress.getText().toString(), json1.toString());
    //sendDataToCameraPreview(edtAddress.getText().toString(),json1.toString());
}

/*
    byte[] bitmapdata = bos.toByteArray();
    //write the bytes in file
    FileOutputStream fos = null;
            try {
        fos = new FileOutputStream(filePath);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
*/
/*Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
//fileCamerra=new File(bm);
//     Bitmap bitmap= (Bitmap) data.getExtras().get("data");

//1 - works
//bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), objUri);
//Glide.with(getActivity()).load(bitmap).into(ivCameraPreview);

//rotaion issue...then below code auto rotate image and display
//int orientation=new Util().getCameraPhotoOrientation(getActivity(),objUri,filePath);
//ivCameraPreview.setRotation(orientation);

//2 - works
            /*ParcelFileDescriptor parcelFileDescriptor = getActivity().getContentResolver().openFileDescriptor(objUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            Glide
                    .with(getActivity())
                    .load(image)
                    .into(ivCameraPreview);
            ivCameraPreview.setRotation(90f);*/

//3 - works
//Bitmap imageBitmap = BitmapFactory.decodeStream( getActivity().getContentResolver().openInputStream(objUri));
//Glide.with(getActivity()).load(imageBitmap).into(ivCameraPreview);
//ivCameraPreview.setRotation(90f); // not for sefli

//4 works
//Bitmap original = BitmapFactory.decodeStream( getActivity().getContentResolver().openInputStream(objUri));
//Glide.with(getActivity()).load(original).into(ivCameraPreview);
//ivCameraPreview.setRotation(90f);

//5 works
//ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//original.compress(Bitmap.CompressFormat.PNG, 100, bytes);
//Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

//6 works
//ivCameraPreview.setImageBitmap(new Util().setScaledPic(filePath, ivCameraPreview));
//ivCameraPreview.setRotation(90f);

//7 auto matic rotation - not work in Xiomi A1
//Bitmap imageBitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(objUri));
//Bitmap bitmapRotated = Util.rotateImageIfRequired(getActivity(), imageBitmap, objUri);
//ivCameraPreview.setImageBitmap(bitmapRotated);

//8 - Rotation issue
//Picasso.with(getActivity().getApplicationContext()).load(filePath).into(ivCameraPreview);
//Picasso.get().load(objUri).into(ivCameraPreview);

//9 Decode - resize (1024 x 1024 ) - auto rotate - not rotate
//bitmap=Util.decodeSampledBitmap(getActivity(),objUri);
//ivCameraPreview.setImageBitmap(bitmap);

//10
//bitmap=Util.decodeSampledBitmap(getActivity(),objUri);
           /* bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(objUri));
            int orientation=new Util().getCameraPhotoOrientation(getActivity(),objUri,filePath);
            ivCameraPreview.setImageBitmap(bitmap);
            ivCameraPreview.setRotation(orientation);*/