package com.CL.slcscanner.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.CL.slcscanner.Adapter.AssestDisplayAdapter;
import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.CommonResponse2;
import com.CL.slcscanner.Pojo.PoleDisplayData.Asset;
import com.CL.slcscanner.Pojo.PoleDisplayData.PoleDisplayMaster;
import com.CL.slcscanner.Pojo.PoleMaster.Datum;
import com.CL.slcscanner.Pojo.SLCDetails.Data;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vrajesh on 2/24/2018.
 */

public class PoleDataDisplayFragment extends Fragment implements AssestDisplayAdapter.MyCallbackForControl {

    View view;
    @BindView(R.id.btnPoleBack)
    ImageView btnPoleBack;

    Bundle mBundle;
    String userid, clientId, slcId, macID, poleId, units;

    Double Lat, Long;
    SharedPreferences spf;

    @BindView(R.id.tvIDMACId)
    TextView tvIDMACId;

    @BindView(R.id.tvIDPOLEId)
    TextView tvIDPOLEId;
    @BindView(R.id.tvIDSLCId)
    TextView tvIDSLCId;
    @BindView(R.id.tvIDPoleHeight)
    TextView tvIDPoleHeight;

    @BindView(R.id.tvIDArmLength)
    TextView tvIDArmLength;
    @BindView(R.id.tvIDDOI)
    TextView tvIDDOI;
    @BindView(R.id.tvIDFixtureWattage)
    TextView tvIDFixtureWattage;
    @BindView(R.id.tvIDFixureManufecturer)
    TextView tvIDFixureManufecturer;

    @BindView(R.id.tvIDMounting)
    TextView tvIDMounting;
    @BindView(R.id.tvIDNumberOfFixurePole)
    TextView tvIDNumberOfFixurePole;
    @BindView(R.id.tvIDPoleCondition)
    TextView tvIDPoleCondition;
    @BindView(R.id.tvIDPoleConfig)
    TextView tvIDPoleConfig;
    @BindView(R.id.tvIDPoleDiameter)
    TextView tvIDPoleDiameter;
    @BindView(R.id.tvIDPoleType)
    TextView tvIDPoleType;

    @BindView(R.id.tvPolePowerFeed)
    TextView tvPolePowerFeed;

    @BindView(R.id.tvPoleOwner)
    TextView tvPoleOwner;

    @BindView(R.id.btnEdit)
    Button btnEdit;

    @BindView(R.id.ivPoleDisplayMap)
    ImageView ivPoleDisplayMap;

    @BindView(R.id.llPoleDisplayBack)
    LinearLayout llPoleDisplayBack;

    @BindView(R.id.tvPoleDisplayBack)
    TextView tvPoleDisplayBack;

    @BindView(R.id.llSLCPOLEID)
    LinearLayout llSLCPOLEID;

    @BindView(R.id.ivImgUpload)
    ImageView ivImgUpload;

    @BindView(R.id.btnImgDisplayCamera)
    ImageView btnImgDisplayCamera;

    boolean isDisplayPurpose = false;
    String ID;
    API objApi;
    ProgressDialog dialog;

    ArrayAdapter<String> adp;

    Datum objDatum;
    String[] mountionAry, poleMaterialAry, armLengthAry, poleDiameterAry, poleConfigAry, noFixtureAry, fixtureManuAry, poleConditionAry, fixtureWattageAry;

    com.CL.slcscanner.Pojo.PoleDisplayData.Data objData;
    boolean isFromMap = false;
    String search_text;

    //custom marker
    View mCustomMarkerView;
    ImageView marker_image;

    //------------------------------------------
    @BindView(R.id.rvPoleDataDisplay)
    RecyclerView rvPoleDataDisplay;
    AssestDisplayAdapter mAdapter;

    @BindView(R.id.tvIDNodeType)
    TextView tvIDNodeType;

    ArrayList<com.CL.slcscanner.Pojo.PoleDisplayData.Asset> mList;

    Dialog dialog_cammera;
    ImageView ivCameraPreview;
    String imgUrl;

    String isEditViewVisible;
    String poleAddress;

    Util objUtil;

    AssestDisplayAdapter.MyCallbackForControl objMyCallbackForControl;
    String node_type;
    boolean isPoleIdVisible;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pole_data_display, null);
        init();
        return view;
    }

    void init() {
        ButterKnife.bind(this, view);

        getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
        getActivity().findViewById(R.id.llNodeType).setVisibility(View.GONE);

        objApi = new SLCScanner().networkCall();
        objUtil = new Util();
        //custom marker
        mCustomMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        marker_image = mCustomMarkerView.findViewById(R.id.marker_image);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getResources().getString(R.string.loading));
        dialog.setCancelable(false);

        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        units = spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC);
        isEditViewVisible = spf.getString(AppConstants.SPF_CLIENT_SLC_EDIT_VIEW, "Yes");
        llSLCPOLEID.setVisibility(View.GONE);

        //isPoleIdVisible=spf.getBoolean(AppConstants.SPF_POLE_ID_VISIBILITY,false);
        String isPoleCompulsary = spf.getString(AppConstants.SPF_CLIENT_SLC_POLE_ID, "No");
        if(isPoleCompulsary.equalsIgnoreCase("No")){
            tvIDPOLEId.setVisibility(View.GONE);
        }else{
            tvIDPOLEId.setVisibility(View.VISIBLE);
        }

        if (isEditViewVisible.equalsIgnoreCase("Yes")) {
            //btnEdit.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.edit));
            btnEdit.setText(getResources().getString(R.string.edit));
            btnEdit.setEnabled(true);
        } else {
            //btnEdit.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ok_button_blue));
            btnEdit.setText(getResources().getString(R.string.ok));
            btnEdit.setEnabled(true);
        }

        //btnEdit.setOnTouchListener(Util.colorFilter());
        btnImgDisplayCamera.setOnTouchListener(Util.colorFilter());

        mList = new ArrayList<>();
        mAdapter = new AssestDisplayAdapter(getActivity(), mList, this);
        //rvPoleDataDisplay.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        rvPoleDataDisplay.setLayoutManager(mLayoutManager);
        rvPoleDataDisplay.setAdapter(mAdapter);
        btnEdit.setVisibility(View.GONE);
        btnImgDisplayCamera.setVisibility(View.GONE);
        Bundle mBundle = getArguments();

        if (mBundle != null) {
            //objDatum = (Datum) mBundle.getSerializable("DATA_FOR_DISPLAY");
            isFromMap = mBundle.getBoolean("IS_FROM_MAP");
            ID = mBundle.getString("ID");

            spf.edit().putString(AppConstants.SPF_ID, ID).apply();
            spf.edit().putBoolean(AppConstants.SPF_ISFROMMAP, isFromMap).apply();

            /*slcId = objDatum.getSlcId();
            poleId = objDatum.getPoleId();
            Lat = Float.valueOf(objDatum.getLat());
            Long = Float.valueOf(objDatum.getLng());
            macID = objDatum.getMacAddress();*/

            // isFromMap = mBundle.getBoolean("isFromMap");

            if (isFromMap)
                tvPoleDisplayBack.setText(getResources().getString(R.string.location));
            else
                tvPoleDisplayBack.setText(getResources().getString(R.string.list));

        }

        if (Util.isInternetAvailable(getActivity()))
            callAPIGetDetails2(ID);
        else
            Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));

        llPoleDisplayBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFromMap) {
                    //getActivity().onBackPressed();
                    objUtil.loadFragment(new AllPoleSLCMapFragment(), getActivity());
                } else {
                    objUtil.loadFragment(new PoleDataFragment(), getActivity());

                    /*FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    PoleDataFragment fragment = new PoleDataFragment();
                   *//* Bundle mBundle = new Bundle();
                    mBundle.putString("search_text", search_text);
                    fragment.setArguments(mBundle);*//*
                    fragmentTransaction.replace(R.id.frm1, fragment);
                    fragmentTransaction.commit();*/

                }
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create a FragmentManager
                FragmentManager fm = getFragmentManager();
                // create a FragmentTransaction to begin the transaction and replace the Fragment
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                // replace the FrameLayout with new Fragment
                if (isEditViewVisible.equalsIgnoreCase("Yes")) {

                    PoleDataEditFragment fragment = new PoleDataEditFragment();
                    Bundle bundle = new Bundle();

                    try {
                        bundle.putBoolean("IS_FROM_MAP", isFromMap);
                        bundle.putBoolean("isNewData", false);

                        bundle.putString("ID", ID);
                        bundle.putString("slcID", slcId);
                        bundle.putString("poleId", poleId);
                        bundle.putDouble("Lat", Lat);
                        bundle.putDouble("Long", Long);
                        bundle.putString("MacId", macID);
                        bundle.putBoolean(AppConstants.isfromNote, false);
                        //bundle.putString(AppConstants.NODE_TYPE_DISPLAY_UI,node_type);
                        fragment.setArguments(bundle);
                        fragmentTransaction.replace(R.id.frm1, fragment);
                        // fragmentTransaction.addToBackStack("");

                    } catch (Exception e) {

                    }
                } else {
                    if (isFromMap)
                        fragmentTransaction.replace(R.id.frm1, new AllPoleSLCMapFragment());
                    else
                        fragmentTransaction.replace(R.id.frm1, new PoleDataFragment());
                }

                fragmentTransaction.commit(); // save the changes
                //loadFragment(new PoleDataEditFragment());
            }
        });

        ivPoleDisplayMap.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dialog_map();
                                                }
                                            }
        );

        ivImgUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogCammera();
            }
        });

        btnImgDisplayCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCammera();
            }
        });

        spf.edit().putString(AppConstants.SPF_POLE_CURRENT_FRAG, AppConstants.SPF_POLE_DISPLAY_FRAG).apply();

    }

    void dialogCammera() {

        Util.hideKeyboard(getActivity());

        dialog_cammera = new Dialog(getActivity());
        dialog_cammera.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_cammera.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog_cammera.setContentView(R.layout.camera_preview_dialog);
        dialog_cammera.setCancelable(false);

        Button ivOk = dialog_cammera.findViewById(R.id.ivOk);
        Button ivChoose = dialog_cammera.findViewById(R.id.ivChoose);

        View cameraPreviewTempView = dialog_cammera.findViewById(R.id.cameraPreviewTempView);

        ivCameraPreview = dialog_cammera.findViewById(R.id.ivCameraPreview);

        String localTemp = Util.getDeviceLocale(spf);
        RequestOptions requestOptions = new RequestOptions();
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

        Glide.with(getActivity())
                .load(imgUrl)
                .apply(requestOptions)
                .into(ivCameraPreview);

        //ivCameraPreview.setImageDrawable(getResources().getDrawable(R.drawable.loading_image));
        //.apply(new RequestOptions().placeholder(R.drawable.no_image).error(R.drawable.no_image))

        ivChoose.setVisibility(View.GONE);

        cameraPreviewTempView.setVisibility(View.GONE);

        ivOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //upload to server
                //UploadSelectedImage();
                dialog_cammera.dismiss();
            }
        });

        /*ivOk.setOnTouchListener(Util.colorFilter());
        ivChoose.setOnTouchListener(Util.colorFilter());*/

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();

        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialog_cammera.getWindow().setLayout((int) (displayRectangle.width() * 0.9f), (int) (displayRectangle.height() * 0.7f));

        dialog_cammera.show();
    }

    void dialog_map() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(R.layout.custom_dialog_map);

        MapView mMapView = (MapView) dialog.findViewById(R.id.mapView1);

        MapsInitializer.initialize(getActivity());
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {


                LatLng position = new LatLng(Lat, Long); ////your lat lng

                MarkerOptions options = new MarkerOptions();
                options.icon(BitmapDescriptorFactory.fromBitmap(new Util().getMarkerBitmapFromView(mCustomMarkerView, R.drawable.custome_pin, marker_image)));
                options.position(position);

                //googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));

                googleMap.getUiSettings().setZoomControlsEnabled(false);
                //googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                googleMap.getUiSettings().setRotateGesturesEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(false);

                new Util().setMapType(getActivity(), googleMap, spf);

                CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(17).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity(), false, "No", poleAddress);
                googleMap.setInfoWindowAdapter(customInfoWindow);

                Datum info = new Datum();
                info.setID(ID);
                info.setPoleId(poleId);
                info.setSlcId(slcId);
                info.setMacAddress(macID);
                info.setLat(String.valueOf(Lat));
                info.setLng(String.valueOf(Long));

                final Marker m = googleMap.addMarker(options);
                m.setTag(info);
                //m.showInfoWindow();

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (marker.isInfoWindowShown())
                            marker.hideInfoWindow();
                        else
                            marker.showInfoWindow();
                        return false;
                    }
                });
            }
        });

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_cancel_map);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // dialogButton.setOnTouchListener(Util.colorFilter());

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();

        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        dialog.getWindow().setLayout((int) (displayRectangle.width() *
                0.9f), (int) (displayRectangle.height() * 0.7f));

        dialog.show();
    }

    void callAPIGetDetails(String id) {
        dialog.show();
        objApi.getSLCDetails(id, units).enqueue(new Callback<PoleDisplayMaster>() {
            @Override
            public void onResponse(Call<PoleDisplayMaster> call, Response<PoleDisplayMaster> response) {
                if (response.body() != null) {
                    PoleDisplayMaster objDetails = response.body();
                    if (objDetails.getStatus().equals("success")) {
                        objData = objDetails.getData();

                        if (isAdded() && getActivity() != null) {
                            setDataInLables1(objData);
                        }

                        /*JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.body().toString());
                            JSONArray objJsonArray=jsonObject.getJSONArray("pole_option");
                            objData.setPole_option(objJsonArray.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                        btnEdit.setVisibility(View.VISIBLE);

                        String pole_image_view_display = spf.getString(AppConstants.SPF_CLIENT_SLC_POLE_IMAGE_VIEW, "Yes");
                        if (pole_image_view_display.equalsIgnoreCase("Yes"))
                            btnImgDisplayCamera.setVisibility(View.VISIBLE);
                        else
                            btnImgDisplayCamera.setVisibility(View.GONE);

                    } else
                        Util.dialogForMessage(getActivity(), "Invalid ID");
                }
                objUtil.dismissProgressDialog(dialog);
            }

            @Override
            public void onFailure(Call<PoleDisplayMaster> call, Throwable t) {
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                objUtil.dismissProgressDialog(dialog);
            }
        });
    }

    void callAPIGetDetails2(String id) {
        dialog.show();
        objApi.getSLCDetails2(id, units).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    //PoleDisplayMaster objDetails = response.body();
                    String objDetails = null;
                    try {
                        objDetails = response.body().string();
                        Log.i(AppConstants.TAG, objDetails.toString());

                        JSONObject object = new JSONObject(objDetails);

                        if (object.getString("status").equals("success")) {
                            JSONObject objData = object.getJSONObject("data");

                            if (isAdded() && getActivity() != null) {
                                setDataInLables2(objData);
                            }
                            btnEdit.setVisibility(View.VISIBLE);


                            String pole_image_view_display = spf.getString(AppConstants.SPF_CLIENT_SLC_POLE_IMAGE_VIEW, "Yes");
                            if (pole_image_view_display.equalsIgnoreCase("Yes"))
                                btnImgDisplayCamera.setVisibility(View.VISIBLE);
                            else
                                btnImgDisplayCamera.setVisibility(View.GONE);

                        } else {
                            Util.dialogForMessage(getActivity(), "Invalid ID");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                  /*  JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().toString());
                        JSONArray objJsonArray = jsonObject.getJSONArray("pole_option");
                        objData.setPole_option(objJsonArray.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (objDetails.getStatus().equals("success")) {
                        objData = objDetails.getData();

                        if (isAdded() && getActivity() != null) {
                            setDataInLables1(objData);
                        }
                        btnEdit.setVisibility(View.VISIBLE);
                        btnImgDisplayCamera.setVisibility(View.VISIBLE);
                    } else
                        Util.dialogForMessage(getActivity(), "Invalid ID");*/
                }
                objUtil.dismissProgressDialog(dialog);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                objUtil.dismissProgressDialog(dialog);
            }
        });
    }

    void setDataInLables(Data objData) {

        tvIDArmLength.setText(objData.getArmLenght());
        tvIDDOI.setText(objData.getDateOfInstallation());
        tvIDFixtureWattage.setText(objData.getFixtureWattage());
        tvIDFixureManufecturer.setText(objData.getFixtureManufacturer());

        tvIDMounting.setText(objData.getMountingMethod());
        tvIDNumberOfFixurePole.setText(objData.getFixturesPerPole());
        tvIDPoleCondition.setText(objData.getPoleCondition());
        tvIDPoleConfig.setText(objData.getPoleConfig());
        tvIDPoleDiameter.setText(objData.getPoleDiameter());
        tvIDPoleType.setText(objData.getPoleType());

        tvPoleOwner.setText(objData.getPole_owner());
        tvPolePowerFeed.setText(objData.getPole_power_feed());

        macID = objData.getMacAddress();
        poleId = objData.getPoleId();
        slcId = objData.getSlcId();
        Long = Double.valueOf(objData.getLng());
        Lat = Double.valueOf(objData.getLat());

        SharedPreferences.Editor edt = spf.edit();
        edt.putString(AppConstants.SPF_POLE_DISPLAY_LAT, String.valueOf(Lat));
        edt.putString(AppConstants.SPF_POLE_DISPLAY_Long, String.valueOf(Long));
        edt.putString(AppConstants.SPF_TEMP_SLCID, slcId);
        edt.putString(AppConstants.SPF_TEMP_POLE_ID, poleId);
        edt.apply();

        tvIDPOLEId.setText("POLE ID: " + poleId);
        tvIDSLCId.setText("SLC ID: " + slcId);

        tvIDPoleHeight.setText(objData.getPoleHeight());

//        String units1 = objData.getMeasurementUnits().toString();
//        if (units1.toString().equalsIgnoreCase(AppConstants.SPF_UNITES_ENGLISH))
//            tvIDPoleHeight.setText(objData.getPoleHeight() + " (feet)");
//        else
//            tvIDPoleHeight.setText(objData.getPoleHeight() + " (Meters)");

    }

    void setDataInLables1(com.CL.slcscanner.Pojo.PoleDisplayData.Data objData) {
        macID = objData.getMacAddress();
        poleId = objData.getPoleId();
        slcId = objData.getSlcId();
        Long = Double.valueOf(objData.getLng());
        Lat = Double.valueOf(objData.getLat());


        String[] pole_option = objData.getPole_option();
        //Log.i(AppConstants.TAG,"---*"+pole_option.toString());

        tvIDPOLEId.setText(getResources().getString(R.string.pole_id) + " : " + poleId);
        tvIDSLCId.setText(getResources().getString(R.string.slc_id) + " : " + slcId);
        tvIDMACId.setText(spf.getString(AppConstants.MACID_LABLE, "NA") + " : " + macID);
        //tvIDNodeType.setText(getString(R.string.node_type_break) + " " +node_type);

        /*Asset objAsset = new Asset();
        objAsset.setAttributeName(AppConstants.attribSLCID);
        objAsset.setSelected(slcId);
        objAsset.setAttrKey("slc_id");
        mList.add(objAsset);

        Asset objAsset1 = new Asset();
        objAsset1.setAttributeName(AppConstants.attribPoleId);
        objAsset1.setSelected(poleId);
        objAsset1.setAttrKey("pole_id");
        mList.add(objAsset1);*/

        for (Asset objAsset3 : objData.getAssets()) {
            mList.add(objAsset3);
        }

        Asset objAsset6 = new Asset();
        objAsset6.setAttributeName(getResources().getString(R.string.note));
        objAsset6.setSelected("Options");
        objAsset6.setAttrKey("notes");
        objAsset6.setNote(objData.getNotes());
        mList.add(objAsset6);

        Asset objAsset4 = new Asset();
        objAsset4.setAttributeName(getResources().getString(R.string.date_of_installation));
        objAsset4.setSelected(objData.getDateOfInstallation());
        objAsset4.setAttrKey("date_of_installation");
        mList.add(objAsset4);

        Asset objAsset5 = new Asset();
        objAsset5.setAttributeName(getResources().getString(R.string.attribute_address));
        objAsset5.setSelected(objData.getAddress());
        objAsset5.setAttrKey("address");
        mList.add(objAsset5);

        imgUrl = objData.getPole_image_url();

        SharedPreferences.Editor edt = spf.edit();
        edt.putString(AppConstants.SPF_POLE_DISPLAY_LAT, String.valueOf(Lat));
        edt.putString(AppConstants.SPF_POLE_DISPLAY_Long, String.valueOf(Long));
        edt.putString(AppConstants.SPF_TEMP_SLCID, slcId);
        edt.putString(AppConstants.SPF_TEMP_POLE_ID, poleId);
        edt.putString(AppConstants.ADDRESS, objData.getAddress());
        edt.apply();

        llSLCPOLEID.setVisibility(View.VISIBLE);

        mAdapter.notifyDataSetChanged();

        if (Util.isInternetAvailable(getActivity()))
            getAddress(Lat, Long);
        else
            Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
    }

    void setDataInLables2(JSONObject objData) {

        try {
            macID = objData.getString("mac_address");
            poleId = objData.getString("pole_id");
            slcId = objData.getString("slc_id");
            Long = Double.valueOf(objData.getString("lng"));
            Lat = Double.valueOf(objData.getString("lat"));

            JSONArray pole_option = objData.getJSONArray("pole_options");
            //spf.edit().putString(AppConstants.POLE_OPTION, pole_option.toString()).apply();
            //Log.i(AppConstants.TAG,"---*"+pole_option.toString());

            String tempNodeType=objData.getString("node_type");
            if (tempNodeType.toString().equals(""))
                node_type =getString(R.string.unknown);
            else
                node_type =tempNodeType;

            String isPoleCompulsary = spf.getString(AppConstants.SPF_CLIENT_SLC_POLE_ID, "No");
            if(isPoleCompulsary.toString().equalsIgnoreCase("yes")) {
                tvIDPOLEId.setVisibility(View.VISIBLE);
                tvIDPOLEId.setText(getResources().getString(R.string.pole_id) + " : " + poleId);
            }else{
                tvIDPOLEId.setVisibility(View.GONE);
            }



            tvIDSLCId.setText(getResources().getString(R.string.slc_id) + " : " + slcId);
            tvIDMACId.setText(spf.getString(AppConstants.MACID_LABLE, "NA") + " : " + macID);
            tvIDNodeType.setText(getString(R.string.node_type_break) + " " +node_type);

            JSONArray objAssets = objData.getJSONArray("Assets");
            Log.i(AppConstants.TAG, "objAssests:" + objAssets.length());

            for (int i = 0; i < objAssets.length(); i++) {

                String AttKey = Util.decodeUnicode(objAssets.getJSONObject(i).getString("AttrKey"));
                String AttributeName = Util.decodeUnicode(objAssets.getJSONObject(i).getString("AttributeName"));
                Log.i(AppConstants.TAG, AttKey);

                if (AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_IMAGE_ANAME)
                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_IMAGE_ANAME_SPANISH)
                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_IMAGE_ANAME_PT)
                ) {
                    spf.edit().putString(AppConstants.pole_image_key, AttKey).apply();
                }else if(AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_ID_ANAME)
                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_ID_ANAME_SPANISH)
                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_ID_ANAME_PT)){
                    //nothing
                    spf.edit().putString(AppConstants.pole_id_key, AttKey).apply();

                }else if(AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_NOTES_ANAME)
                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_NOTES_ANAME_SPANISH)
                        || AttributeName.toLowerCase().equalsIgnoreCase(AppConstants.POLE_NOTES_ANAME_PT)){
                    spf.edit().putString(AppConstants.pole_notes_key, AttKey).apply();
                }
                else {

                    Asset objAsset = new Asset();
                    objAsset.setAssetName(objAssets.getJSONObject(i).getString("AssetName"));
                    objAsset.setAttributeName(AttributeName);
                    objAsset.setBtnText(objAssets.getJSONObject(i).getString("BtnText"));
                    objAsset.setSelected(objAssets.getJSONObject(i).getString("Selected"));
                    objAsset.setAttrKey(AttKey);
                    objAsset.setNote(objAssets.getJSONObject(i).getString("Note"));

                    JSONArray values = objAssets.getJSONObject(i).getJSONArray("Values");
                    List<String> mListValue = new ArrayList<>();

                    if (values.length() > 0) {
                        for (int j = 0; j < values.length(); j++) {
                            //Log.i(AppConstants.TAG, "values:" + values.length());
                            mListValue.add(values.getString(j));
                        }
                    }
                    objAsset.setValues(mListValue);

                    mList.add(objAsset);
                }
            }

            Asset objAsset6 = new Asset();
            objAsset6.setAttributeName(getResources().getString(R.string.note));
            objAsset6.setSelected("View");
            objAsset6.setAttrKey("notes");
            objAsset6.setNote(objData.getString("notes") + "#" + pole_option);

            mList.add(objAsset6);
            //spf.edit().putString(AppConstants.NOTES, objData.getString("notes")).apply();

            Asset objAsset4 = new Asset();
            objAsset4.setAttributeName(getResources().getString(R.string.date_of_installation));
            objAsset4.setSelected(objData.getString("date_of_installation"));
            objAsset4.setAttrKey("date_of_installation");

            mList.add(objAsset4);

            Asset objAsset5 = new Asset();
            objAsset5.setAttributeName(getResources().getString(R.string.attribute_address));
            objAsset5.setSelected(objData.getString("address"));
            objAsset5.setAttrKey("address");

            mList.add(objAsset5);

            imgUrl = objData.getString("pole_image_url");

            SharedPreferences.Editor edt = spf.edit();
            edt.putString(AppConstants.SPF_POLE_DISPLAY_LAT, String.valueOf(Lat));
            edt.putString(AppConstants.SPF_POLE_DISPLAY_Long, String.valueOf(Long));
            edt.putString(AppConstants.SPF_TEMP_SLCID, slcId);
            edt.putString(AppConstants.SPF_TEMP_POLE_ID, poleId);
            edt.putString(AppConstants.ADDRESS, objData.getString("address"));
            edt.apply();

            llSLCPOLEID.setVisibility(View.VISIBLE);

            mAdapter.notifyDataSetChanged();

            if (Util.isInternetAvailable(getActivity()))
                getAddress(Lat, Long);
            else
                Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
        } catch (Exception e) {

        }
    }


    void getAddress(Double lattitude, Double longitude) {

        if (getActivity() != null) {
            if (Util.isInternetAvailable(getActivity())) {

                objApi.getAddress(lattitude, longitude, spf.getString(AppConstants.CLIENT_ID, ""), userid).
                        enqueue(new Callback<CommonResponse2>() {
                            @Override
                            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {
                                if (response.body() != null) {
                                    CommonResponse2 obj = response.body();
                                    if (obj.getStatus().equalsIgnoreCase("success")) {
                                        poleAddress = obj.getShortaddress();
                                    } else if (obj.getStatus().equalsIgnoreCase("error")) {
                                        poleAddress = obj.getMsg();
                                    } else
                                        Toast.makeText(getActivity(), "No address found, Try Again !", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (getActivity() != null)
                                        poleAddress = getActivity().getResources().getString(R.string.single_pin);
                                }

                            }

                            @Override
                            public void onFailure(Call<CommonResponse2> call, Throwable t) {
                                if (getActivity() != null)
                                    poleAddress = getActivity().getResources().getString(R.string.single_pin);
                            }
                        });
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frm1, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        objUtil.dismissProgressDialog(dialog);
    }

    @Override
    public void onClickStatusUI(int position, String result) {
        //loadFragment(new NoteFragment());

        Bundle mBundle = new Bundle();
        NoteFragment objNoteFragment = new NoteFragment();
        mBundle.putBoolean(AppConstants.ISVIEWONLY, true);
        mBundle.putString(AppConstants.FROM, "display");
        mBundle.putString(AppConstants.NOTES_POLE_OPTION, result);

        mBundle.putString("ID", ID);

        objNoteFragment.setArguments(mBundle);

        objUtil.loadFragment(objNoteFragment, getActivity());
    }
}  /* googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                TileOverlayOptions options1 = new TileOverlayOptions();
                options1.tileProvider(new UrlTileProvider(256, 256) {
                    @Override
                    public synchronized URL getTileUrl(int x, int y, int zoom) {
                        String s = String.format(Locale.US, AppConstants.STAMEN_TONNER, zoom, x, y);
                        URL url = null;
                        try {
                            url = new URL(s);
                        } catch (MalformedURLException e) {
                            throw new AssertionError(e);
                        }
                        return url;
                    }
                });*/
