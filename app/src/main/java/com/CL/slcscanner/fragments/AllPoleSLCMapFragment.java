package com.CL.slcscanner.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
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

import com.CL.slcscanner.Activities.MainActivity;
import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.ListResponse.ListResponse;
import com.CL.slcscanner.Pojo.PoleMaster.Datum;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.DBHelper;
import com.CL.slcscanner.Utils.GPS.GPSTracker;
import com.CL.slcscanner.Utils.MyCallbackForMapUtility;
import com.CL.slcscanner.Utils.MyCallbackForMapUtilityLocation;
import com.CL.slcscanner.Utils.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.SphericalUtil;

import net.sharewire.googlemapsclustering.Cluster;
import net.sharewire.googlemapsclustering.ClusterManager;
import net.sharewire.googlemapsclustering.DefaultIconGenerator;
import net.sharewire.googlemapsclustering.IconStyle;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;


/**
 * Created by vrajesh on 3/12/2018.
 */

public class AllPoleSLCMapFragment extends Fragment implements
        OnMapReadyCallback,
        //com.google.maps.android.clustering.ClusterManager.OnClusterItemInfoWindowClickListener<com.CL.slcscanner.Pojo.ListResponse.List>,
        MyCallbackForMapUtility, MyCallbackForMapUtilityLocation, ClusterManager.Callbacks<com.CL.slcscanner.Pojo.ListResponse.List>
        //GoogleApiClient.ConnectionCallbacks,
        //GoogleApiClient.OnConnectionFailedListener,
        //LocationListener
{

    @BindView(R.id.map)
    MapView mMapView;

    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @BindView(R.id.llMapSLCPoleId)
    LinearLayout llMapSLCPoleId;

    @BindView(R.id.tvMapSLCPoleId)
    TextView tvMapSLCPoleId;

    @BindView(R.id.btnAcceptPoleLocation)
    Button btnAcceptPoleLocation;

    @BindView(R.id.ivPoleGlobe)
    ImageView btnPoleGlobe;

    @BindView(R.id.tvMapBack)
    TextView tvMapBack;

    @BindView(R.id.llSelectLocationBack)
    LinearLayout llSelectLocationBack;

    private GoogleMap googleMap;
    ArrayList<LatLng> markerPoints;

    SharedPreferences spf;

    View view;

    @BindView(R.id.btnPoleBack)
    ImageView btnPoleBack;

    ArrayList<Datum> mList;
    ArrayList<Datum> mListAll;

    View mCustomMarkerView;
    ImageView marker_image;

    DBHelper mDatabase;
    String isPoleDetailVisibility;

    @BindView(R.id.llBottombar)
    LinearLayout llBottombar;

    @BindView(R.id.ivInfo)
    ImageView ivInfo;

    String client_id, userid;

    //ClusterManager<com.CL.slcscanner.Pojo.ListResponse.List> mClusterManager;
    net.sharewire.googlemapsclustering.ClusterManager mClusterManagerLib;
    testCluster mClusterTest;
    com.CL.slcscanner.Pojo.ListResponse.List objDatum;

    Util objUtil;
    API objApi;

  /*  private String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE
    };*/

    private GPSTracker gps;
    private static final int PERMISSION_ALL = 1;

    ProgressDialog dialogForLatlong;

    //Initialize to a non-valid zoom value
    private float previousZoomLevel = -1.0f;

    private boolean isZooming = false;

    ProgressDialog dialog;

    String top_lon;
    String top_lat;
    String bottom_lon;
    String bottom_lat;

    //library for cluster manager
    //ClusterManager<com.CL.slcscanner.Pojo.ListResponse.List> clusterManagerLib;

    private FirebaseAnalytics mFirebaseAnalytics;
    String event_name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_select_location_map, null);

        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        if (spf.getBoolean(AppConstants.isPermission, true))
            init(savedInstanceState);
        else
            Util.dialogForPermisionMessage(getActivity(), getResources().getString(R.string.permission));

        return view;
    }

    void init(Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        dialogForLatlong = new ProgressDialog(getActivity());
        dialogForLatlong.setMessage(getResources().getString(R.string.please_wait));
        dialogForLatlong.setCancelable(false);

        llBottombar.setVisibility(View.GONE);

        objApi = new SLCScanner().networkCall();

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getResources().getString(R.string.loading));
        dialog.setCancelable(false);

        objUtil = new Util();
        getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
        getActivity().findViewById(R.id.llNodeType).setVisibility(View.GONE);
        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        isPoleDetailVisibility = spf.getString(AppConstants.SPF_CLIENT_SLC_LIST_VIEW, "Yes");
        mDatabase = new DBHelper(getActivity());

        mCustomMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        marker_image = mCustomMarkerView.findViewById(R.id.marker_image);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        event_name = spf.getString(AppConstants.CLIENT_ID, null) + "_" + spf.getString(AppConstants.USER_ID, null) + "_";
        mFirebaseAnalytics.setCurrentScreen(getActivity(), "MapPolePinUI", null);

        //tvTitle.setText(getResources().getString(R.string.installation));
        tvTitle.setText(getResources().getString(R.string.mapsetting));
        client_id = spf.getString(AppConstants.CLIENT_ID, "");
        userid = spf.getString(AppConstants.USER_ID, "");
        llMapSLCPoleId.setVisibility(View.GONE);
        tvMapSLCPoleId.setVisibility(View.GONE);
        btnAcceptPoleLocation.setVisibility(View.GONE);

        final Bundle mBundle = getArguments();

        //mList = mDatabase.getAllSLC();
        //mList = Util.getArraylist(spf);
        //mList = Util.getArraylist(spf);

        if (Util.isInternetAvailable(getActivity())) {
            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume(); // needed to get the map to display immediately


            mMapView.getMapAsync(this);

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request6plus();
            } else {
                getLocation();
            }*/

            //mMapView.getMapAsync(this);
        } else {
            Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
        }

        tvMapBack.setText("LIST");
        ivInfo.setVisibility(View.GONE);

        llSelectLocationBack.setVisibility(View.GONE);
        llSelectLocationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frm1, new PoleDataFragment());
                fragmentTransaction.commit(); // save the changes
            }
        });

        btnPoleGlobe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isInternetAvailable(getActivity()) && googleMap != null) {
                    //new Util().displayDialog(getActivity(), googleMap, googleMap.getMapType());
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frm1, new SettingFragment());
                    fragmentTransaction.commit(); // save the changes
                }
            }
        });

        spf.edit().putString(AppConstants.SPF_POLE_CURRENT_FRAG, AppConstants.SPF_POLE_ALL_MAP).apply();

        if (getActivity() != null) {
            ((MainActivity) getActivity()).selectScan(false);
            ((MainActivity) getActivity()).selectPole(false);
            ((MainActivity) getActivity()).selectSetting(false);
            ((MainActivity) getActivity()).selectMap(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Util.isInternetAvailable(getActivity())) {
            if (mMapView != null)
                mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (Util.isInternetAvailable(getActivity())) {
                if (mMapView != null)
                    mMapView.onPause();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (mMapView != null)
                mMapView.onDestroy();
        } catch (Exception e) {
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null)
            mMapView.onLowMemory();
    }

  /*  private void request6plus() {
        if (!Util.hasPermissions(getActivity(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
            return;
        } else {
            getLocation();
        }
    }*/

    private void getLocation() {
        gps = new GPSTracker(getActivity(), this, this);
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Log.i(AppConstants.TAG, latitude + " | " + longitude);

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


            }

            Log.d(AppConstants.TAG, "onLocationChanged ->" + latitude + "--" + longitude);
        } else {
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert(false);
        }
    }

  /*  @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(getActivity(), "All permission requests are not granted..", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }*/

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(final GoogleMap googleMap1) {
        googleMap = googleMap1;

        googleMap1.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap1.getUiSettings().setRotateGesturesEnabled(true);
        googleMap1.getUiSettings().setMapToolbarEnabled(false);
        googleMap1.getUiSettings().setZoomControlsEnabled(true);
        googleMap1.getUiSettings().setCompassEnabled(true);
        googleMap1.getUiSettings().setMyLocationButtonEnabled(true); //UI  not visible, plz add: googleMap.setMyLocationEnabled(true);
        googleMap1.setMyLocationEnabled(true);
        googleMap1.setOnCameraChangeListener(getCameraChangeListener());

        //googleMap1.setInfoWindowAdapter(customInfoWindow);

        //3rd lib
        if (mClusterManagerLib == null)
            mClusterManagerLib = new net.sharewire.googlemapsclustering.ClusterManager<>(getActivity(), googleMap);

        if (mClusterTest == null)
            mClusterTest = new testCluster(getActivity(), googleMap);

        new Util().setMapType(getActivity(), googleMap, spf);
        Double lat = 0.0, lng = 0.0;
        try {
            lat = Double.valueOf(spf.getString(AppConstants.LATTITUDE, "0.0"));
            lng = Double.valueOf(spf.getString(AppConstants.LONGITUDE, "0.0"));
        } catch (Exception e) {
        }

        googleMap1.setOnCameraIdleListener(mClusterTest);
        //googleMap1.setInfoWindowAdapter(mClusterTest.getMarkerManager());

        googleMap1.setOnInfoWindowClickListener(mClusterTest);
        //googleMap1.setOnMarkerClickListener(mClusterTest);

        mClusterTest.setIconGenerator(new pinDefault(getActivity(), googleMap1));

        mClusterTest.setCallbacks(this);

        LatLng location = new LatLng(lat, lng);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(17).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        Double finalLat = lat;
        Double finalLng = lng;
        googleMap1.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            public void onMapLoaded() {
                //do stuff here

                VisibleRegion visibleRegion = googleMap1.getProjection()
                        .getVisibleRegion();

                LatLngBounds latLngBounds = visibleRegion.latLngBounds;
                top_lon = String.valueOf(latLngBounds.northeast.longitude);
                top_lat = String.valueOf(latLngBounds.northeast.latitude);

                bottom_lon = String.valueOf(latLngBounds.southwest.longitude);
                bottom_lat = String.valueOf(latLngBounds.southwest.latitude);

                //getData(minX, minY, maxX, maxY, false);
                Log.i("***", "Bounds: " + top_lat + " " + top_lon + " " + bottom_lat + " " + bottom_lon);
                setData(mClusterTest, googleMap, 1, finalLat, finalLng, top_lat, top_lon, bottom_lat, bottom_lon);
            }
        });


    }

    void setData(final testCluster mClusterManager1, final GoogleMap googleMap1, final double km, double lat, double lng,
                 String top_lat, String top_lon, String bottom_lat, String bottom_lon) {

        if (getActivity() != null) {
            if (Util.isInternetAvailable(getActivity())) {

                final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                try {
                    if (getActivity() != null) {
                        dialog.show();
                    }
                } catch (Exception e) {
                }

                objApi.getLatLong(lat, lng, km, client_id, userid,
                        top_lat, top_lon, bottom_lat, bottom_lon
                ).enqueue(new Callback<ListResponse>() {
                    @Override
                    public void onResponse(Call<ListResponse> call, Response<ListResponse> response) {
                        ListResponse objListResponse = response.body();
                        if (objListResponse != null) {

                            if (objListResponse.getStatus().equalsIgnoreCase("1")) {
                                //LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                objListResponse.getData().getTotNoOfRecords();

                                List<com.CL.slcscanner.Pojo.ListResponse.List> mList = objListResponse.getData().getList();

                                if (mList != null && mList.size() != 0) {
                                    try {
                                        List<com.CL.slcscanner.Pojo.ListResponse.List> clusterItems = new ArrayList<>();
                                        for (int i = 0; i < mList.size(); i++) {
                                            // For dropping a marker at a point on the MapTypePojo
                                            //LatLng position1 = new LatLng(Double.valueOf(mList.get(i).getLat()), Double.valueOf(mList.get(i).getLng()));
                                            final com.CL.slcscanner.Pojo.ListResponse.List info = new com.CL.slcscanner.Pojo.ListResponse.List(Double.valueOf(mList.get(i).getLat()), Double.valueOf(mList.get(i).getLng()), getActivity());

                                            info.setID(mList.get(i).getID());
                                            info.setPoleId(mList.get(i).getPoleId());
                                            info.setSlcId(mList.get(i).getSlcId());
                                            info.setMacAddress(mList.get(i).getMacAddress());
                                            info.setLat(mList.get(i).getLat());
                                            info.setLng(mList.get(i).getLng());
                                            info.setTitle(getResources().getString(R.string.slc_id) + " : " + mList.get(i).getSlcId());
                                            info.setSnippet(getResources().getString(R.string.pole_id) + " : " + mList.get(i).getPoleId());
                                            info.setTag("cluster");

                                            clusterItems.add(info);

                                            //mClusterManager1.setRenderer(new pinRender(getActivity(), googleMap1, mClusterManager));
                                            //mClusterManager1.addItem(info);
                                            //mClusterManager1.cluster();
                                            //Cluster<com.CL.slcscanner.Pojo.ListResponse.List> cluster = new Cluster<com.CL.slcscanner.Pojo.ListResponse.List>(info.getLat(),info.getLng(),info,0,0,0,0);
                                        }

                                        //googleMap1.setOnCameraIdleListener(mClusterManager1);
                                        //CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity());
                                        //mClusterManager1.getMarkerCollection().setOnInfoWindowAdapter(customInfoWindow);

                                        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity());
                                        googleMap1.setInfoWindowAdapter(customInfoWindow); // for single item-

                                        //mClusterTest.getMarkerCollection().setOnInfoWindowAdapter(customInfoWindow);
                                        mClusterManager1.setItems(clusterItems);

                                        objUtil.dismissProgressDialog(dialog);
                                    } catch (Exception e) {
                                        objUtil.dismissProgressDialog(dialog);
                                    }
                                } else {
                                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.slc_not_found));
                                    objUtil.dismissProgressDialog(dialog);
                                }
                            } else if (response.body().getStatus().equalsIgnoreCase("error")) {
                                Util.dialogForMessage(getActivity(), getResources().getString(R.string.slc_not_found));
                                objUtil.dismissProgressDialog(dialog);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ListResponse> call, Throwable t) {
                        Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                        objUtil.dismissProgressDialog(dialog);
                    }
                });

            } else
                Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
        }
    }

    public GoogleMap.OnCameraChangeListener getCameraChangeListener() {
        return new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {

                Log.d("Zoom", "Zoom: " + position.zoom);
                Log.d("PreviousZoom", "Zoom: " + previousZoomLevel);

                if (previousZoomLevel != position.zoom && position.zoom < previousZoomLevel) {
                    isZooming = true;
                } else
                    isZooming = false;

                previousZoomLevel = position.zoom;

                LatLng latLng = position.target;
                Log.i(AppConstants.TAG, "LatLong: " + latLng.latitude + " | " + latLng.longitude);

                VisibleRegion visibleRegion = googleMap.getProjection().getVisibleRegion();
                double distance = SphericalUtil.computeDistanceBetween(
                        visibleRegion.farLeft, googleMap.getCameraPosition().target);

                Log.i(AppConstants.TAG, "meters:  " + distance);

                distance = distance / 1000;
                Log.i(AppConstants.TAG, "KM:  " + distance);

                Double lat = 0.0, lng = 0.0;
                try {
                    lat = Double.valueOf(spf.getString(AppConstants.LATTITUDE, "0.0"));
                    lng = Double.valueOf(spf.getString(AppConstants.LONGITUDE, "0.0"));
                } catch (Exception e) {

                }

                if (isZooming) {
                    final Double finalLat = lat;
                    final Double finalLng = lng;


                    LatLngBounds latLngBounds = visibleRegion.latLngBounds;
                    top_lon = String.valueOf(latLngBounds.northeast.longitude);
                    top_lat = String.valueOf(latLngBounds.northeast.latitude);

                    bottom_lon = String.valueOf(latLngBounds.southwest.longitude);
                    bottom_lat = String.valueOf(latLngBounds.southwest.latitude);

                    Log.i("***", "Bounds: " + top_lat + " " + top_lon + " " + bottom_lat + " " + bottom_lon);
                    setData(mClusterTest, googleMap, distance, finalLat, finalLng, top_lat, top_lon, bottom_lat, bottom_lon);
                }
            }
        };
    }


    @Override
    public void onClickForControl(int scount, float meters) {

    }

    @Override
    public void onClickForControl(Double Lat, Double longgitude, float accuracy) {

    }

    @Override
    public boolean onClusterClick(@NonNull Cluster<com.CL.slcscanner.Pojo.ListResponse.List> cluster) {
        return true;
    }

    @Override
    public boolean onClusterItemClick(@NonNull com.CL.slcscanner.Pojo.ListResponse.List clusterItem) {
        return false;
    }

   /* @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }*/

    /*@Override
    public void onInfoWindowClick(Marker marker) {
        if (isPoleDetailVisibility.equalsIgnoreCase("Yes")) {

            Datum mData = (Datum) marker.getTag();

            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            PoleDataDisplayFragment fragement = new PoleDataDisplayFragment();

            Bundle mBundle = new Bundle();
            //mBundle.putSerializable("DATA_FOR_DISPLAY", mData);
            mBundle.putString("ID", mData.getID().toString());
            mBundle.putBoolean("IS_FROM_MAP", true);
            fragement.setArguments(mBundle);
            fragement.setArguments(mBundle);
            fragmentTransaction.replace(R.id.frm1, fragement);
            fragmentTransaction.commit(); // save the changes

        }
    }*/

    private class testCluster extends net.sharewire.googlemapsclustering.ClusterManager<com.CL.slcscanner.Pojo.ListResponse.List>
            implements GoogleMap.OnInfoWindowClickListener {

        /**
         * Creates a new cluster manager using the default icon generator.
         * To customize marker icons, set a custom icon generator using
         *
         * @param context
         * @param googleMap the map instance where markers will be rendered
         */
        MarkerManager mMarkerManager;
        final MarkerManager.Collection mMarkers;

        public testCluster(@NonNull Context context, @NonNull GoogleMap googleMap) {
            super(context, googleMap);
            mMarkerManager = new MarkerManager(googleMap);
            this.mMarkers = mMarkerManager.newCollection();
        }

        @Override
        public void onInfoWindowClick(Marker marker) {
            Log.d(AppConstants.TAG, "----");
            try {

                Bundle bundle1=new Bundle();
                bundle1.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "MapPinDetails");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle1);
                Log.d(AppConstants.TAG, event_name+"MapPinDetails");

                net.sharewire.googlemapsclustering.Cluster objCluster = (net.sharewire.googlemapsclustering.Cluster) marker.getTag();
                if (objCluster.getItems().size() != 0) {

                    com.CL.slcscanner.Pojo.ListResponse.List clusterItem = (com.CL.slcscanner.Pojo.ListResponse.List) objCluster.getItems().get(0);

                    if (isPoleDetailVisibility.equalsIgnoreCase("Yes")) {

                        Bundle bundle=new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "SLCDetails");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                        Log.d(AppConstants.TAG, event_name+"SLCDetails");

                        //String text[] = marker.getTitle().split("#");
                        //String id = text[1];
                        // com.CL.slcscanner.Pojo.ListResponse.List mData = (com.CL.slcscanner.Pojo.ListResponse.List) marker.getTag();
                        FragmentManager fm = getFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        PoleDataDisplayFragment fragement = new PoleDataDisplayFragment();

                        Bundle mBundle = new Bundle();
                        //mBundle.putSerializable("DATA_FOR_DISPLAY", mData);
                        mBundle.putString("ID", clusterItem.getID());//mData.getID().toString()
                        mBundle.putBoolean("IS_FROM_MAP", true);
                        fragement.setArguments(mBundle);
                        //fragmentTransaction.add(R.id.frm1, fragement);
                        //fragmentTransaction.hide(AllPoleSLCMapFragment.this);
                        //fragmentTransaction.addToBackStack(AllPoleSLCMapFragment.class.getName());
                        fragmentTransaction.replace(R.id.frm1, fragement);
                        fragmentTransaction.commit(); // save the changes
                    }
                }

            } catch (Exception e) {

            }

        }

       /* @Override
        public boolean onMarkerClick(Marker marker) {
            Log.d(AppConstants.TAG, "----007--->" + marker.getTitle());
            marker.getTitle();
            *//*com.CL.slcscanner.Pojo.ListResponse.List mData = (com.CL.slcscanner.Pojo.ListResponse.List) marker.getTag();
            if (mData.getTag().equalsIgnoreCase("cluster")) {
                objDatum = mData;
            }*//*

            return false;
        }*/

        public MarkerManager.Collection getMarkerCollection() {
            return this.mMarkers;
        }

        public MarkerManager getMarkerManager() {
            return this.mMarkerManager;
        }

    }

    private class pinDefault extends DefaultIconGenerator<com.CL.slcscanner.Pojo.ListResponse.List> {
        GoogleMap googleMap;

        /**
         * Creates an icon generator with the default icon style.
         *
         * @param context
         */
        public pinDefault(@NonNull Context context, GoogleMap map) {
            super(context);
            this.googleMap = map;
        }

        @NonNull
        @Override
        public BitmapDescriptor getClusterIcon(@NonNull Cluster<com.CL.slcscanner.Pojo.ListResponse.List> cluster) {

            int size = cluster.getItems().size();
            Log.i("---*", "cluster size" + size);
            //int size=getClusterIconBucket(cluster);

            View customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_custer, null);
            TextView objTextView = customMarkerView.findViewById(R.id.txtClusterSize);

            if (size != 0) {
                if (size > 999)
                    objTextView.setText("999+");
                else
                    objTextView.setText(String.valueOf(size));
            }

            customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
            customMarkerView.buildDrawingCache();
            Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
            Drawable drawable = customMarkerView.getBackground();
            if (drawable != null) drawable.draw(canvas);
            customMarkerView.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(returnedBitmap);
        }

        @Override
        public void setIconStyle(@NonNull IconStyle iconStyle) {
            super.setIconStyle(iconStyle);
        }

        @NonNull
        @Override
        public BitmapDescriptor getClusterItemIcon(@NonNull com.CL.slcscanner.Pojo.ListResponse.List clusterItem) {
            super.getClusterItemIcon(clusterItem);
            return BitmapDescriptorFactory.fromBitmap(new Util().getMarkerBitmapFromView(mCustomMarkerView, R.drawable.custome_pin, marker_image));
            //return super.getClusterItemIcon(clusterItem);
        }
    }

    class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {
        private Activity context;
        LayoutInflater inflater;
        View view;

        CustomInfoWindowGoogleMap(Activity ctx) {
            this.context = ctx;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.raw_google_marker, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            view = inflater.inflate(R.layout.raw_google_marker, null);
            final TextView tvInfo = view.findViewById(R.id.tvInfo);
            final TextView tvtitle2 = view.findViewById(R.id.tvtitle2);
            final ImageView ivInfo = view.findViewById(R.id.ivInfo);

            Bundle bundle=new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "MapPinInfo");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            Log.d(AppConstants.TAG, event_name+"MapPinInfo");

            try {
                net.sharewire.googlemapsclustering.Cluster objCluster = (net.sharewire.googlemapsclustering.Cluster) marker.getTag();
                if (objCluster.getItems().size() != 0) {
                    //Datum clusterItem = (Datum) objCluster.getItems().get(0);
                    com.CL.slcscanner.Pojo.ListResponse.List clusterItem = (com.CL.slcscanner.Pojo.ListResponse.List) objCluster.getItems().get(0);
                    tvInfo.setText(clusterItem.getTitle());
                    tvtitle2.setText(clusterItem.getSnippet());
                }
            } catch (Exception e) {
                Log.i(AppConstants.TAG, e.getMessage());
            }
            return view;
        }

        @Override
        public View getInfoContents(Marker marker) {
            //this will take default background of google map
            return null;
        }

        /*@Override
        public void onInfoWindowClick(Marker marker) {
            Log.d(AppConstants.TAG, "TESTSTT");
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            Log.d(AppConstants.TAG, "TESTSTT10000");
            return false;
        }*/
    }

}
   /* private void animateToMeters(int meters, LatLng ll) {
        int mapHeightInDP = 256;
        Resources r = getResources();
        int mapSideInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mapHeightInDP, r.getDisplayMetrics());

        LatLngBounds latLngBounds = calculateBounds(ll, meters);

        if (latLngBounds != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, mapSideInPixels, mapSideInPixels, MARKER_BOUNDS);
            if (googleMap != null)
                googleMap.animateCamera(cameraUpdate);
        }
    }*/


  /*  private LatLng centerMapOnMyLocation() {
        Location locationCt = getLastKnownLocation();
        if (locationCt == null) {
            return null;
        }

        LatLng latLng = new LatLng(locationCt.getLatitude(), locationCt.getLongitude());
           .icon(BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_add)));

        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        return latLng;
    }*/

//animateToMeters(1500, ll);

// Condition check for lat long value
                    /*if ((mList.get(i).getLat().equalsIgnoreCase("") ||
                            mList.get(i).getLat().contains("0"))
                            &&
                            (mList.get(i).getLng().equalsIgnoreCase("") ||
                                    mList.get(i).getLng().contains("0"))
                            ) {
                    } else {*/
//MyItem offsetItem = new MyItem(Double.valueOf(mList.get(i).getLat()), Double.valueOf(mList.get(i).getLng()));
//mClusterManager.addItem(offsetItem);
 /* mBundle.putString("ID", mData.getID());//require id
        mBundle.putBoolean("isDisplayPurpose", true);
        mBundle.putString("slcID", mData.getSlcId());
        mBundle.putString("poleId", mData.getPoleId());
        mBundle.putFloat("Lat", lattitude);
        mBundle.putFloat("Long", longitude);
        mBundle.putString("MacId", mData.getMacAddress());*/
//mBundle.putBoolean("isFromMap",true);
      /*if (mBundle != null) {
            mList = (ArrayList<Datum>) mBundle.getSerializable("DATA");
            Log.i(AppConstants.TAG, "List:" + mList.toString());
        }*/

        /*mClusterManager.setOnClusterInfoWindowClickListener(new ClusterManager.OnClusterInfoWindowClickListener<Datum>() {
@Override
public void onClusterInfoWindowClick(Cluster<Datum> cluster) {
        Datum objDatum = (Datum) cluster.getItems();

        if (isPoleDetailVisibility.equalsIgnoreCase("Yes")) {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        PoleDataDisplayFragment fragement = new PoleDataDisplayFragment();

        Bundle mBundle = new Bundle();
        //mBundle.putSerializable("DATA_FOR_DISPLAY", mData);
        mBundle.putString("ID", objDatum.getID().toString());
        mBundle.putBoolean("IS_FROM_MAP", true);
        fragement.setArguments(mBundle);
        fragement.setArguments(mBundle);
        fragmentTransaction.replace(R.id.frm1, fragement);
        fragmentTransaction.commit(); // save the changes

        }
        }
        });*/


        /*@Override
    public void onInfoWindowClick(Marker marker) {

        //GoogleMapInfo info= (GoogleMapInfo) marker.getTag();

        if (isPoleDetailVisibility.equalsIgnoreCase("Yes")) {

            Datum mData = (Datum) marker.getTag();

            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            PoleDataDisplayFragment fragement = new PoleDataDisplayFragment();

            Bundle mBundle = new Bundle();
            //mBundle.putSerializable("DATA_FOR_DISPLAY", mData);
            mBundle.putString("ID", mData.getID().toString());
            mBundle.putBoolean("IS_FROM_MAP", true);
            fragement.setArguments(mBundle);
            fragement.setArguments(mBundle);
            fragmentTransaction.replace(R.id.frm1, fragement);
            fragmentTransaction.commit(); // save the changes

        }
    }
    */

//MarkerOptions options = new MarkerOptions();
//options.position(position1);
//options.icon(BitmapDescriptorFactory.fromBitmap(new Util().getMarkerBitmapFromView(mCustomMarkerView, R.drawable.custome_pin, marker_image)));

//options.title("POLE ID: "+mList.get(i).getPoleId());
//options.snippet(mList.get(i).getSlcId());

//CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity(), false, isPoleDetailVisibility, "");
//googleMap1.setInfoWindowAdapter(customInfoWindow);

//Marker m = googleMap1.addMarker(options);



/* builder.include(SphericalUtil.computeOffset(position1, km, 0)).
                                            include(SphericalUtil.computeOffset(position1, km, 90)).
                                            include(SphericalUtil.computeOffset(position1, km, 180)).
                                            include(SphericalUtil.computeOffset(position1, km, 270)).build();

                                    // builder.include(position1);

                                    int width = getResources().getDisplayMetrics().widthPixels;
                                    int height = getResources().getDisplayMetrics().heightPixels;
                                    int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
                                    LatLngBounds bounds = builder.build();

                                    int mapHeightInDP = 256;
                                    Resources r = getResources();
                                    int mapSideInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mapHeightInDP, r.getDisplayMetrics());

                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                    //googleMap.animateCamera(cu, 2000, null);
                                    googleMap.animateCamera(cu);*/

//getActivity().runOnUiThread(new Runnable() {
//   @Override
//  public void run() {


   /* void dummyData(ClusterManager clusterManager){
        ClusterManager<SampleClusterItem> clusterManager1 = new ClusterManager<>(getActivity(), googleMap);

        clusterManager1.setCallbacks(new ClusterManager.Callbacks<SampleClusterItem>() {
            @Override
            public boolean onClusterClick(@NonNull Cluster<SampleClusterItem> cluster) {
                Log.d(AppConstants.TAG, "onClusterClick");
                return false;
            }

            @Override
            public boolean onClusterItemClick(@NonNull SampleClusterItem clusterItem) {
                Log.d(AppConstants.TAG, "onClusterItemClick");
                return false;
            }
        });


        LatLngBounds NETHERLANDS = new LatLngBounds(
                new LatLng(30.77083, 5.57361), new LatLng(33.35917, 7.10833));

        LatLngBounds india = new LatLngBounds(
                new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));

        List<SampleClusterItem> clusterItems = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            //Log.i(AppConstants.TAG,""+i);
            clusterItems.add(new SampleClusterItem(
                    RandomLocationGenerator.generate(NETHERLANDS)));
        }

        for (int i = 0; i < 10000; i++) {
            //Log.i(AppConstants.TAG,""+i);
            clusterItems.add(new SampleClusterItem(
                    RandomLocationGenerator.generate(india)));
        }

        clusterManager1.setItems(clusterItems);
        googleMap.setOnCameraIdleListener(clusterManager);

    }*/


   /* mDatabase.insertSLCData(mList.get(i).getID(),
                                            mList.get(i).getSlcId(),
                                            mList.get(i).getMacAddress(),
                                            mList.get(i).getPoleId(),
                                            mList.get(i).getLat(),
                                            mList.get(i).getLng());*/
//mClusterManager.setRenderer(new pinRender(getActivity(), googleMap1, mClusterManager));
// mClusterManager.addItem(info);
//mClusterManager.cluster();

    /*void LoadDataOnMAp(GoogleMap googleMap, ClusterManager<com.CL.slcscanner.Pojo.ListResponse.List> mClusterManager) {

        List<com.CL.slcscanner.Pojo.ListResponse.List> mList = mDatabase.getAllSLC();

        for (int i = 0; i < mList.size(); i++) {
            // For dropping a marker at a point on the MapTypePojo
            // LatLng position1 = new LatLng(Double.valueOf(mList.get(i).getLat()), Double.valueOf(mList.get(i).getLng()));
            final com.CL.slcscanner.Pojo.ListResponse.List info = new com.CL.slcscanner.Pojo.ListResponse.List(Double.valueOf(mList.get(i).getLat()), Double.valueOf(mList.get(i).getLng()));
            info.setID(mList.get(i).getID());
            info.setPoleId(mList.get(i).getPoleId());
            info.setSlcId(mList.get(i).getSlcId());
            info.setMacAddress(mList.get(i).getMacAddress());
            info.setLat(mList.get(i).getLat());
            info.setLng(mList.get(i).getLng());
            info.setTag("cluster");

            //mClusterManager.setRenderer(new pinRender(getActivity(), googleMap, mClusterManager));
            //mClusterManager.addItem(info);
            //mClusterManager.cluster();

        }
        //mClusterManager.setItems(mList);
        objUtil.dismissProgressDialog(dialog);

        //CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(getActivity());
        //mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(customInfoWindow);

    }*/

    /*
    private int getClusterIconBucket(@NonNull Cluster<com.CL.slcscanner.Pojo.ListResponse.List> cluster) {
        int itemCount = cluster.getItems().size();
        if (itemCount <= CLUSTER_ICON_BUCKETS[0]) {
            return itemCount;
        }

        for (int i = 0; i < CLUSTER_ICON_BUCKETS.length - 1; i++) {
            if (itemCount < CLUSTER_ICON_BUCKETS[i + 1]) {
                return CLUSTER_ICON_BUCKETS[i];
            }
        }
        return CLUSTER_ICON_BUCKETS[CLUSTER_ICON_BUCKETS.length - 1];
    }
*/

/*
    private class pinRender extends DefaultClusterRenderer<com.CL.slcscanner.Pojo.ListResponse.List> {
        GoogleMap googleMap;

        public pinRender(Context context, GoogleMap map, net.sharewire.googlemapsclustering.ClusterManager<com.CL.slcscanner.Pojo.ListResponse.List> clusterManager) {
            super(context, map, clusterManager);
            this.googleMap = map;
        }

        @Override
        protected void onBeforeClusterItemRendered(com.CL.slcscanner.Pojo.ListResponse.List item, MarkerOptions markerOptions) {
            // For dropping a marker at a point on the MapTypePojo
            LatLng position1 = new LatLng(Double.valueOf(item.getLat()), Double.valueOf(item.getLng()));
            markerOptions.position(position1);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(new Util().getMarkerBitmapFromView(mCustomMarkerView, R.drawable.custome_pin, marker_image)));
            //Marker m = googleMap.addMarker(markerOptions);
            //m.setTag(item);
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<com.CL.slcscanner.Pojo.ListResponse.List> cluster, MarkerOptions markerOptions) {
            super.onBeforeClusterRendered(cluster, markerOptions);
            //BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(here_goes_your_bitmap);
            //markerOptions.icon(descriptor);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<com.CL.slcscanner.Pojo.ListResponse.List> cluster) {
            return cluster.getSize() > 2; // when count of markers is more than 2, render as cluster
        }

        @Override
        protected void onClusterRendered(Cluster<com.CL.slcscanner.Pojo.ListResponse.List> cluster, Marker marker) {
            super.onClusterRendered(cluster, marker);
            try {
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapForClusterFromView(cluster.getSize())));
            } catch (Exception e) {

            }
        }
    }
*/
/*
private Bitmap getMarkerBitmapForClusterFromView(int size) {

    View customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_custer, null);
    TextView objTextView = customMarkerView.findViewById(R.id.txtClusterSize);

    if (size != 0) {
        if (size > 999)
            objTextView.setText("999+");
        else
            objTextView.setText(String.valueOf(size));
    }

    customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
    customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
    customMarkerView.buildDrawingCache();
    Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(returnedBitmap);
    canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
    Drawable drawable = customMarkerView.getBackground();
    if (drawable != null) drawable.draw(canvas);
    customMarkerView.draw(canvas);
    return returnedBitmap;
}*/

/*
    //@Override
    public void onClusterItemInfoWindowClick(com.CL.slcscanner.Pojo.ListResponse.List datum) {
        if (isPoleDetailVisibility.equalsIgnoreCase("Yes")) {

            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            PoleDataDisplayFragment fragement = new PoleDataDisplayFragment();

            Bundle mBundle = new Bundle();
            //mBundle.putSerializable("DATA_FOR_DISPLAY", mData);
            mBundle.putString("ID", datum.getID().toString());
            mBundle.putBoolean("IS_FROM_MAP", true);
            fragement.setArguments(mBundle);
            fragement.setArguments(mBundle);
            fragmentTransaction.replace(R.id.frm1, fragement);
            fragmentTransaction.commit(); // save the changes

        }
    }
*/
/*
MarkerOptions objOptions=new MarkerOptions();
                                    objOptions.position(new LatLng(Double.valueOf(info.getLat()),Double.valueOf(info.getLng())));
                                            objOptions.title(mList.get(i).getSlcId());
                                            Marker objMarker=googleMap1.addMarker(objOptions);
                                            objMarker.setTag(info);*/
