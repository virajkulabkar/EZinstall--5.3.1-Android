package com.CL.slcscanner.fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.CL.slcscanner.Activities.LoginActivity;
import com.CL.slcscanner.Activities.MainActivity;
import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.CommonResponse2;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.CustomInfoWindowGoogleMap;
import com.CL.slcscanner.Utils.GPS.FusedLocationUtils;
import com.CL.slcscanner.Utils.GPS.GPSTracker;
import com.CL.slcscanner.Utils.MyCallbackForMapUtility;
import com.CL.slcscanner.Utils.MyCallbackForMapUtilityLocation;
import com.CL.slcscanner.Utils.Util;
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

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.CL.slcscanner.Utils.AppConstants.TAG;

/**
 * Created by vrajesh on 3/10/2018.
 */

public class SelectLocationPoleFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerDragListener, MyCallbackForMapUtility, MyCallbackForMapUtilityLocation, FusedLocationUtils.UpdateUI {

    @BindView(R.id.btnPoleBack)
    ImageView ivPoleBack;
    @BindView(R.id.ivPoleGlobe)
    ImageView btnPoleGlobe;

    @BindView(R.id.map)
    MapView mMapView;

    @BindView(R.id.llMapSLCPoleId)
    LinearLayout llMapSLCPoleId;

    @BindView(R.id.tvMapSLCPoleId)
    TextView tvMapSLCPoleId;

    @BindView(R.id.btnAcceptPoleLocation)
    Button btnAcceptPoleLocation;

    @BindView(R.id.SelectLocationMacID)
    TextView SelectLocationMacID;

    @BindView(R.id.SelectLocationSLCID)
    TextView SelectLocationSLCID;

    @BindView(R.id.llSelectLocationBack)
    LinearLayout llSelectLocationBack;

    @BindView(R.id.tvMapBack)
    TextView tvMapBack;

    @BindView(R.id.llSatellites)
    LinearLayout llSatellites;

    @BindView(R.id.llAccuracy)
    LinearLayout llAccuracy;

    @BindView(R.id.llSignalStrength)
    LinearLayout llSignalStrength;

    @BindView(R.id.tvSatellites)
    TextView tvSatellites;

    @BindView(R.id.tvAccuracy)
    TextView tvAccuracy;

    @BindView(R.id.tvSignalStrength)
    TextView tvSignalStrength;

    @BindView(R.id.viewLatLong)
    TextView viewLatLong;

    @BindView(R.id.ivInfo)
    ImageView imgInfoView;

    @BindView(R.id.ivSatellitesInfo)
    ImageView ivSatellitesInfo;

    @BindView(R.id.ivAccuracyInfo)
    ImageView ivAccuracyInfo;

    View view;

    private GoogleMap googleMap;
    ArrayList<LatLng> markerPoints;
    SharedPreferences spf;

    Bundle objBundle;

    private String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE
    };
    private GPSTracker gps;
    private static final int PERMISSION_ALL = 1;

    API objApi;
    ProgressDialog dialog;
    String slcID, macId;
    String units;

    //custom marker
    View mCustomMarkerView;
    ImageView marker_image;
    String lattitude, longgitude;
    ProgressDialog dialogForLatlong;

    DecimalFormat form;

    Util objUtil;
    String from = "";

    FusedLocationUtils objFusedLocationUtils;
    int count = 0;
    int scount;
    float meters;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_location_map, null);
        init(savedInstanceState);
        return view;
    }

    void init(Bundle savedInstanceState) {

        ButterKnife.bind(this, view);
        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);

        objBundle = getArguments();

        if (objBundle != null) {
            from = objBundle.getString("From", "");
        }

        getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
        getActivity().findViewById(R.id.llNodeType).setVisibility(View.GONE);
        objUtil = new Util();
        llSelectLocationBack.setVisibility(View.VISIBLE);
        form = new DecimalFormat("0.0000");
        new Util().hideKeyboard(getActivity());
        imgInfoView.setVisibility(View.GONE);

        //objFusedLocationUtils=new FusedLocationUtils(getActivity(),this,this,true);

        dialogForLatlong = new ProgressDialog(getActivity());
        dialogForLatlong.setMessage(getResources().getString(R.string.please_wait));
        dialogForLatlong.setCancelable(false);

        mCustomMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        marker_image = mCustomMarkerView.findViewById(R.id.marker_image);

        slcID = spf.getString(AppConstants.SPF_TEMP_SLCID, "0");
        macId = spf.getString(AppConstants.SPF_TEMP_MACID, "MAC ID: 0");

        lattitude = spf.getString(AppConstants.SPF_DRAG_LATTITUDE, "0.0");
        longgitude = spf.getString(AppConstants.SPF_DRAG_LONGITUDE, "0.0");

        SelectLocationSLCID.setText(getResources().getString(R.string.attribute_slc_id) + " : " + slcID);
        SelectLocationMacID.setText(spf.getString(AppConstants.MACID_LABLE, "NA") + " : " + macId);

        llMapSLCPoleId.setVisibility(View.VISIBLE);
        tvMapSLCPoleId.setVisibility(View.VISIBLE);
        btnAcceptPoleLocation.setVisibility(View.VISIBLE);

        //btnAcceptPoleLocation.setOnTouchListener(Util.colorFilter());

        btnAcceptPoleLocation.setOnClickListener(this);
        ivPoleBack.setOnClickListener(this);
        btnPoleGlobe.setOnClickListener(this);

        btnPoleGlobe.setOnTouchListener(Util.colorFilter());

        llSelectLocationBack.setOnClickListener(this);

        mMapView = (MapView) view.findViewById(R.id.map);

        units = spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC);

        objApi = new SLCScanner().networkCall();
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getResources().getString(R.string.loading));

        if (Util.isInternetAvailable(getActivity())) {
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume(); // needed to get the map to display immediately

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            mMapView.getMapAsync(this);
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request6plus();
            } else {
                //getLocation();
                objFusedLocationUtils.buildGoogleApiClient();
            }*/
        } else {
            Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
        }

        if (getActivity() != null) {
            ((MainActivity) getActivity()).selectScan(true);
            ((MainActivity) getActivity()).selectPole(false);
            ((MainActivity) getActivity()).selectSetting(false);
            ((MainActivity) getActivity()).selectMap(false);
        }
        spf.edit().putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, AppConstants.SPF_SELECT_POLE_LOCATION_FRAG).apply();

        //tvAccuracy.setText(spf.getFloat(AppConstants.LOCATION_ACCURACY, 0) + " Meter");
        //tvSatellites.setText(spf.getInt(AppConstants.SATELLITE_COUNTS, 0) + " Satellites");

        imgInfoView.setOnClickListener(this);

        ivSatellitesInfo.setOnClickListener(this);
        ivAccuracyInfo.setOnClickListener(this);

        llAccuracy.setOnClickListener(this);
        llSatellites.setOnClickListener(this);

        if (from.equalsIgnoreCase("MACID")) {
            tvMapBack.setText(getResources().getString(R.string.mac_id));
        } else {
            tvMapBack.setText(getResources().getString(R.string.slc_id));
        }

     /*   scount = spf.getInt(AppConstants.SATELLITE_COUNTS, scount);
        meters = spf.getFloat(AppConstants.LOCATION_ACCURACY, meters);

        tvSatellites.setText(scount + "");
        String str = String.format("%.2f", meters);//
        tvAccuracy.setText(str + " M");*/
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if(GPSTracker.isFromSetting){
            GPSTracker.isFromSetting = false;
            //spf.edit().putBoolean("test",false).apply();
            loadFragment(new SelectLocationPoleFragment());
            mMapView.onPause();
        }*/
        try {
            if (mMapView != null) {
                if (Util.isInternetAvailable(getActivity()))
                    mMapView.onResume();
                else
                    Toast.makeText(getActivity(), getResources().getString(R.string.internent_connection), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //objFusedLocationUtils.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (Util.isInternetAvailable(getActivity()))
                mMapView.onPause();
        } catch (Exception e) {

        }
        //objFusedLocationUtils.pause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (Util.isInternetAvailable(getActivity()))
                mMapView.onDestroy();
        } catch (Exception e) {
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        try {
            if (Util.isInternetAvailable(getActivity()))
                mMapView.onLowMemory();
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.llSelectLocationBack:
                if (from.equalsIgnoreCase("MACID")) {
                    objUtil.loadFragment(new ScanMacId(), getActivity());
                } else {
                    ScanSLCId fragment = new ScanSLCId();

                    Bundle mBundle = new Bundle();
                    mBundle.putBoolean(AppConstants.ISFROMBACK, true);
                    fragment.setArguments(mBundle);

                    objUtil.loadFragment(fragment, getActivity());
                }
                break;
            case R.id.ivPoleGlobe:
                /*if (googleMap != null) {
                    new Util().displayDialog(getActivity(), googleMap, googleMap.getMapType());
                }*/

                objUtil.loadFragment(new SettingFragment(), getActivity());

                /*FragmentManager fm1 = getFragmentManager();
                FragmentTransaction fragmentTransaction1 = fm1.beginTransaction();

                Bundle mBundle1 = new Bundle();
                mBundle1.putString(AppConstants.KEY_MAP_TYPE_UI_TRANSFER, AppConstants.SPF_SELECT_POLE_LOCATION_FRAG);

                MapTypeFragment mapTypeFragment = new MapTypeFragment();
                mapTypeFragment.setArguments(mBundle1);

                fragmentTransaction1.replace(R.id.frm1, mapTypeFragment);
                fragmentTransaction1.commit(); // save the changes*/

                break;
            case R.id.btnAcceptPoleLocation:
                objUtil.loadFragment(new AddressFragement(), getActivity());
                break;
            case R.id.ivInfo:
                Util.dialogForMapInfo(getActivity(), "Information", getResources().getString(R.string.info_msg));
                break;
            case R.id.ivAccuracyInfo:
            case R.id.llAccuracy:
                Util.dialogForMapInfo(getActivity(), getResources().getString(R.string.accuracy_str), getResources().getString(R.string.accuracy_msg));
                break;
            case R.id.ivSatellitesInfo:
            case R.id.llSatellites:
                Util.dialogForMapInfo(getActivity(), getResources().getString(R.string.satellite_str), getResources().getString(R.string.satellite_msg));
                break;
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frm1, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        marker.hideInfoWindow();
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(final Marker marker) {

        if (Util.isInternetAvailable(getActivity())) {
            LatLng objLatLng = marker.getPosition();
            SharedPreferences.Editor edit = spf.edit();

            edit.putString(AppConstants.SPF_DRAG_LATTITUDE, String.valueOf(objLatLng.latitude));
            edit.putString(AppConstants.SPF_DRAG_LONGITUDE, String.valueOf(objLatLng.longitude));
            edit.apply();

            //tvSatellites.setText(spf.getInt(AppConstants.SATELLITE_COUNTS, 0) + getResources().getString(R.string.google_map_satellites));
            //String str = String.format("%.2f",spf.getFloat(AppConstants.LOCATION_ACCURACY, 0));//
            //tvAccuracy.setText(str + " " + getResources().getString(R.string.google_map_accuracy_lable));
            //viewLatLong.setText(form.format(objLatLng.latitude) + " " + form.format(objLatLng.longitude));


            getAddress(marker);

            Log.d(TAG, "" + (float) objLatLng.latitude + "|||" + (float) objLatLng.longitude);
        } else
            Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivity - SelectedLocation called");
        //objFusedLocationUtils.onActivityResult(getActivity(),requestCode,resultCode);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            this.googleMap = googleMap;

            //googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            //googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            new Util().setMapType(getActivity(), googleMap, spf);

            //googleMap.getUiSettings().setMyLocationButtonEnabled(false); //UI  not visible, plz add: googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                return;
            }

            googleMap.setMyLocationEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false); // this is for redirect to google map after click marker
            // For dropping a marker at a point on the MapTypePojo

            String tempLat = spf.getString(AppConstants.SPF_DRAG_LATTITUDE, "0.0");
            String tempLong = spf.getString(AppConstants.SPF_DRAG_LONGITUDE, "0.0");

            String oldLat, oldLong;
            LatLng location;

            if (tempLat.equals("0.0") && tempLong.toString().equals("0.0")) { //no dragable
                oldLat = spf.getString(AppConstants.LATTITUDE, "0.0");
                oldLong = spf.getString(AppConstants.LONGITUDE, "0.0");

                spf.edit().putString(AppConstants.SPF_DRAG_LATTITUDE, oldLat).apply();
                spf.edit().putString(AppConstants.SPF_DRAG_LONGITUDE, oldLong).apply();

                location = new LatLng(Double.valueOf(oldLat), Double.valueOf(oldLong));

            } else {
                location = new LatLng(Double.valueOf(tempLat), Double.valueOf(tempLong));
            }

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location);
            //markerOptions.snippet("Current Location");
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(new Util().getMarkerBitmapFromView(mCustomMarkerView, R.drawable.custome_pin, marker_image)));
            markerOptions.draggable(true);

            Marker m = googleMap.addMarker(markerOptions);

            if (Util.isInternetAvailable(getActivity()))
                getAddress(m);
            else
                Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
            //CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity(), true, "Yes", "");
            //googleMap.setInfoWindowAdapter(customInfoWindow);
            //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));

            // For zooming automatically to the location of the marker
            CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(17).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            googleMap.setOnMarkerDragListener(this);

            tvSatellites.setText(spf.getInt(AppConstants.SATELLITE_COUNTS, 0) + "");
            String str = String.format("%.2f", spf.getFloat(AppConstants.LOCATION_ACCURACY, 0));//
            tvAccuracy.setText(str + " M");

            viewLatLong.setText(form.format(location.latitude) + "   " + form.format(location.longitude));

            //viewLatLong.setText("" + (float) location.latitude + "\n" + (float) location.longitude);
        } catch (Exception e) {

        }
    }

    void getAddress(final Marker marker) {

        objApi.getAddress(marker.getPosition().latitude, marker.getPosition().longitude, spf.getString(AppConstants.CLIENT_ID, ""), spf.getString(AppConstants.USER_ID, "")).
                enqueue(new Callback<CommonResponse2>() {
                    @Override
                    public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {
                        if (response.body() != null) {
                            CommonResponse2 obj = response.body();

                            if (isAdded() && getActivity() != null) {
                                if (obj.getStatus().equalsIgnoreCase("success")) {
                                    CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity(), true, "Yes", obj.getShortaddress());
                                    googleMap.setInfoWindowAdapter(customInfoWindow);
                                    marker.showInfoWindow();
                                } else if (obj.getStatus().equalsIgnoreCase("error")) {
                                    CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity(), true, "Yes", obj.getMsg());
                                    googleMap.setInfoWindowAdapter(customInfoWindow);
                                    marker.showInfoWindow();
                                } else
                                    Toast.makeText(getActivity(), "No address found, Try Again !", Toast.LENGTH_SHORT).show();
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

    private void request6plus() {
        if (!Util.hasPermissions(getActivity(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
            return;
        } else {
            //getLocation();
            //objFusedLocationUtils.buildGoogleApiClient();
        }
    }

    private void getLocation() {
        gps = new GPSTracker(getActivity(), this, this);
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Log.i(TAG, latitude + " | " + longitude);

            if (latitude == 0.0 || longitude == 0.0) {
                dialogForLatlong.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getLocation();//recursion
                    }
                }, 5000);

            } else {
                if (dialogForLatlong.isShowing())
                    dialogForLatlong.dismiss();

                spf.edit().putString(AppConstants.LATTITUDE, String.valueOf(latitude)).apply();
                spf.edit().putString(AppConstants.LONGITUDE, String.valueOf(longitude)).apply();

                mMapView.getMapAsync(this);
            }

            Log.d(TAG, "onLocationChanged ->" + latitude + "--" + longitude);
        } else {
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert(true);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //getLocation();
                    objFusedLocationUtils.buildGoogleApiClient();
                } else {
                    Toast.makeText(getActivity(), "All permission requests are not granted..", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void updateLatLng(Location location) {
        if (dialogForLatlong.isShowing())
            dialogForLatlong.dismiss();

        if (location != null) {
            spf.edit().putString(AppConstants.LATTITUDE, String.valueOf(location.getLatitude())).apply();
            spf.edit().putString(AppConstants.LONGITUDE, String.valueOf(location.getLongitude())).apply();
            spf.edit().putFloat(AppConstants.LOCATION_ACCURACY, location.getAccuracy()).apply();

            if (count == 0)
                mMapView.getMapAsync(this);

        }
        count++;
    }

    @Override
    public void onClickForControl(int scount, float meters) {
        spf.edit().putInt(AppConstants.SATELLITE_COUNTS, scount).commit();
        spf.edit().putFloat(AppConstants.LOCATION_ACCURACY, meters).commit();

        tvSatellites.setText(scount + "");
        String str = String.format("%.2f", meters);//
        tvAccuracy.setText(str + " M");

        //Toast.makeText(getActivity(),"updat", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickForControl(Double Lat, Double longgitude, float accuracy) {

        viewLatLong.setText(form.format(Lat) + "   " + form.format(longgitude));
        //Toast.makeText(getActivity(),"updat", Toast.LENGTH_SHORT).show();
        String str = String.format("%.2f", accuracy);//
        tvAccuracy.setText(str + " M");

    }
}