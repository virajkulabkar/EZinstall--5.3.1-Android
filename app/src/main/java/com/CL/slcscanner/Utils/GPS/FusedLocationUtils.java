package com.CL.slcscanner.Utils.GPS;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.CL.slcscanner.R;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.MyCallbackForMapUtility;
import com.CL.slcscanner.Utils.Util;
import com.CL.slcscanner.fragments.SelectLocationPoleFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Iterator;

public class FusedLocationUtils extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GpsStatus.Listener {


    //https://stackoverflow.com/questions/29796436/why-is-fusedlocationapi-getlastlocation-null/52534719#52534719
    //https://stackoverflow.com/questions/31235564/locationsettingsrequest-dialog-to-enable-gps-onactivityresult-skipped
    public interface UpdateUI {
        void updateLatLng(Location location);
        void onClickForControl(int scount, float meters);
    }

    private static final int REQUEST_CHECK_SETTINGS = 214;
    private static final int REQUEST_ENABLE_GPS = 516;

    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 1000; // = 5 seconds
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    protected LocationSettingsRequest mLocationSettingsRequest;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    GnssStatus.Callback mGnssStatusCallback;

    protected LocationManager locationManager;

    Double lattitudeD;
    Double longitudeD;
    Activity activity;
    UpdateUI objUpdateUI;

    public static boolean isFromSetting = false;
    public static boolean isIsFromPoleData = false;
    SharedPreferences spf;
    MyCallbackForMapUtility objBr;

    Fragment fragment;
    ProgressDialog dialogForLatlong;
    Util objUtil;
    boolean isdialogVisible=false;

    public FusedLocationUtils(Activity activity, UpdateUI objUpdateUI, android.app.Fragment fragment,boolean isdialogVisible) {
        this.activity = activity;
        this.objUpdateUI = objUpdateUI;
        locationManager = (LocationManager) activity
                .getSystemService(LOCATION_SERVICE);
        spf = activity.getSharedPreferences(AppConstants.SPF, MODE_PRIVATE);
        this.fragment = fragment;
        this.isdialogVisible=isdialogVisible;

        dialogForLatlong = new ProgressDialog(activity);
        dialogForLatlong.setMessage(activity.getResources().getString(R.string.updated_location));
        dialogForLatlong.setCancelable(false);

        objUtil = new Util();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();

        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("Location", "GPS Success");
                        if (dialogForLatlong.isShowing())
                            objUtil.dismissProgressDialog(dialogForLatlong);
                            getSatelliteSignals();
                            requestLocationUpdate();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        try {
                            if (dialogForLatlong.isShowing())
                                objUtil.dismissProgressDialog(dialogForLatlong);
                        }catch (Exception e1){

                        }
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                try {

                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    if (fragment != null)
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            fragment.startIntentSenderForResult(rae.getResolution().getIntentSender(), REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null);
                                        } else {

                                        }
                                    else
                                        rae.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);

                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("Location", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                Log.i("Location", "Location settings are inadequate, and cannot be fixed here. Fix in Settings.");
                        }
                    }
                })
                .addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        try {
                            if (dialogForLatlong.isShowing())
                                objUtil.dismissProgressDialog(dialogForLatlong);
                        }catch (Exception e){

                        }

                        Log.i("Location", "checkLocationSettings -> onCanceled");
                    }
                });

    }

    @Override
    public void onConnectionSuspended(int i) {
        connectGoogleClient();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {

            if (dialogForLatlong.isShowing())
                objUtil.dismissProgressDialog(dialogForLatlong);

            String latitude = String.valueOf(location.getLatitude());
            String longitude = String.valueOf(location.getLongitude());

            if (latitude.equalsIgnoreCase("0.0") && longitude.equalsIgnoreCase("0.0")) {
                requestLocationUpdate();
            } else {
                longitudeD = location.getLongitude();
                lattitudeD = location.getLatitude();
                objUpdateUI.updateLatLng(location);
                Log.i("Location: ", "Latitude : " + location.getLatitude() + " Longitude : " + location.getLongitude());
            }
        }

    }

    @SuppressLint("MissingPermission")
    void getSatelliteSignals() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Util.addLog("*******Nougut Device*********");

            mGnssStatusCallback = new GnssStatus.Callback() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onSatelliteStatusChanged(GnssStatus status) {
                    super.onSatelliteStatusChanged(status);
                    int satelliteCount = status.getSatelliteCount();

                    //Log.i(AppConstants.TAG, "S counts: " + satelliteCount);
                    //Util.addLogSatellites(gnssStatusToString(status)); // not require for log
                    spf.edit().putInt(AppConstants.SATELLITE_COUNTS, satelliteCount).apply();
                    objUpdateUI.onClickForControl(satelliteCount, spf.getFloat(AppConstants.LOCATION_ACCURACY, 0.0f));

                }
            };

            locationManager.registerGnssStatusCallback(mGnssStatusCallback);
        } else {
            locationManager.addGpsStatusListener(this);//inserted new
        }
    }


    @SuppressLint("MissingPermission")
    private void requestLocationUpdate() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i("Location", "User allow to access location. Request Location Update");
                    requestLocationUpdate();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i("Location", "User denied to access location.");
                    openGpsEnableSetting();
                    break;
            }
        } else if (requestCode == REQUEST_ENABLE_GPS) {
            LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGpsEnabled) {
                openGpsEnableSetting();
            } else {
                requestLocationUpdate();
            }
        }
    }*/

    private void openGpsEnableSetting() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(intent, REQUEST_ENABLE_GPS);
    }

    public synchronized void buildGoogleApiClient() {

        if (connectGoogleClient()) {

            if(isdialogVisible) {
                if (!dialogForLatlong.isShowing())
                    dialogForLatlong.show();
            }
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
            mSettingsClient = LocationServices.getSettingsClient(activity);

            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mGoogleApiClient.connect();

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    Log.i("Location", "Location Received");
                    mCurrentLocation = locationResult.getLastLocation();
                    onLocationChanged(mCurrentLocation);
                }
            };

        } else {
            Util.dialogForMessage(activity, "You need to install Google Play Services to use the App properly");
            //Toast.makeText(activity,"You need to install Google Play Services to use the App properly", Toast.LENGTH_LONG).show();
        }

    }

    //play service is avaible ?
    private boolean connectGoogleClient() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }
        return true;
    }


    public void start() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    public void pause() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            mGoogleApiClient.disconnect();
        }
    }

    public String resume() {
        String result = "";
        if (!connectGoogleClient()) {
            result = "You need to install Google Play Services to use the App properly";
        }

        Log.i("Location", result);
        return result;
    }

    public double getLatitude() {
        if (mCurrentLocation != null) {
            lattitudeD = mCurrentLocation.getLatitude();
        }
        return lattitudeD;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (mCurrentLocation != null) {
            longitudeD = mCurrentLocation.getLongitude();
        }
        return longitudeD;
    }

    public void showSettingsAlert(boolean isIsFromPoleData1) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(intent);
                    isFromSetting = true;
                    isIsFromPoleData = isIsFromPoleData1;
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton(activity.getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        } catch (Exception e) {

        }
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i("Location", "User allow to access location. Request Location Update");
                    requestLocationUpdate();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i("Location", "User denied to access location.");
                    openGpsEnableSetting();
                    break;
            }
        } else if (requestCode == REQUEST_ENABLE_GPS) {
            LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGpsEnabled) {
                openGpsEnableSetting();
            } else {
                requestLocationUpdate();
            }
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        try {

            if (Util.isInternetAvailable(activity)) {
                @SuppressLint("MissingPermission") GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                if (gpsStatus != null) {
                    String strGpsStats = null;
                    Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
                    Iterator<GpsSatellite> sat = satellites.iterator();
                    int i = 0;
                    while (sat.hasNext()) {
                        GpsSatellite satellite = sat.next();
                        if (satellite.usedInFix())
                            i++;
                        strGpsStats += (i++) + ": " + satellite.getPrn() + "," + satellite.usedInFix() + "," + satellite.getSnr() + "," + satellite.getAzimuth() + "," + satellite.getElevation() + "\n\n";
                    }

                    Util.addLogSatellites("Below API 24 Satellites: " + i + " Other Details: " + strGpsStats);
                    Log.i(AppConstants.TAG, "Below API 24 Satellites: " + i);

                    spf.edit().putInt(AppConstants.SATELLITE_COUNTS, i).apply();
                    //spf.edit().putFloat(AppConstants.LOCATION_ACCURACY,spf.getFloat(AppConstants.LOCATION_ACCURACY,0.0f)).apply();

                    objUpdateUI.onClickForControl(i, spf.getFloat(AppConstants.LOCATION_ACCURACY, 0.0f));
                }
            }

        } catch (Exception e) {

        }
    }

}