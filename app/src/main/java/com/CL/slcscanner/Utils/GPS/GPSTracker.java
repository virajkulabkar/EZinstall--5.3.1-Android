package com.CL.slcscanner.Utils.GPS;

/**
 * Created by vrajesh on 11/9/2016.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.CL.slcscanner.R;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.MyCallbackForMapUtility;
import com.CL.slcscanner.Utils.MyCallbackForMapUtilityLocation;
import com.CL.slcscanner.Utils.Util;

import java.util.Iterator;

public class GPSTracker extends Service implements LocationListener, GpsStatus.Listener {

    private final Context mContext;

    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 5 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 10; // 30 seconds;

    // Declaring a Location Manager
    protected LocationManager locationManager;

    SharedPreferences spf;
    float accuracyFinal;


    private static final int PERMISSION_ALL = 1;
    GnssStatus obj;

    private String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE};

    public static boolean isFromSetting = false;
    public static boolean isIsFromPoleData=false;
    GnssStatus.Callback mGnssStatusCallback;
    LocationManager lm;

    MyCallbackForMapUtilityLocation objBrLocation;

    MyCallbackForMapUtility objBr;

    @TargetApi(Build.VERSION_CODES.N)
    public GPSTracker(Context context, MyCallbackForMapUtility objBr, MyCallbackForMapUtilityLocation objBrLocation) {
        this.mContext = context;
        spf = context.getSharedPreferences(AppConstants.SPF, MODE_PRIVATE);
        //getLocation();
        getLocation2();
        this.objBr = objBr;
        this.objBrLocation=objBrLocation;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.unregisterGnssStatusCallback(
                mGnssStatusCallback
        );
    }

    /*class GpsListener implements GpsStatus.Listener{
        @Override
        public void onGpsStatusChanged(int event) {
            GpsStatus gpsStatus = lm.getGpsStatus(null);
            if(gpsStatus != null) {
                Iterable<GpsSatellite>satellites = gpsStatus.getSatellites();
                Iterator<GpsSatellite>sat = satellites.iterator();
                int i=0;
                while (sat.hasNext()) {
                    GpsSatellite satellite = sat.next();
                    strGpsStats+= (i++) + ": " + satellite.getPrn() + "," + satellite.usedInFix() + "," + satellite.getSnr() + "," + satellite.getAzimuth() + "," + satellite.getElevation()+ "\n\n";
                }
                tv.setText(strGpsStats);
            }
        }
    }*/

    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    location.getAccuracy();

                    //new GnssStatus().getSatelliteCount();

                    //3GpsStatus status=locationManager.getGpsStatus();

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            SharedPreferences.Editor editor = spf.edit();
                            editor.putString(AppConstants.LATTITUDE, String.valueOf(latitude));
                            editor.putString(AppConstants.LONGITUDE, String.valueOf(longitude));
                            editor.apply();
                            Log.i(AppConstants.TAG, "Network: Lat::" + latitude + "------Long: " + longitude);
                            Util.addLog("Network: Lat::" + latitude + "------Long: " + longitude);
                        }

                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.i(AppConstants.TAG, "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                                SharedPreferences.Editor editor = spf.edit();
                                editor.putString(AppConstants.LATTITUDE, String.valueOf(latitude));
                                editor.putString(AppConstants.LONGITUDE, String.valueOf(longitude));
                                editor.apply();
                                Log.i(AppConstants.TAG, "GPS Lat::" + latitude + "------Long: " + longitude);
                                Util.addLog("GPS: Lat::" + latitude + "------Long: " + longitude);
                            }
                        }
                    }
                }

            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return location;
    }


    @SuppressLint("MissingPermission")
    public Location getLocation2() {
        boolean gps_enabled = false;
        boolean network_enabled = false;

        Location net_loc = null;
        Location gps_loc = null;
        Location finalLoc = null;
        try {
            lm = (LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);

            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!gps_enabled && !network_enabled) {
                //no network or GPS apater available
            } else {
                this.canGetLocation = true;

                if (gps_enabled) {
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                if (network_enabled) {
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }

                if (gps_loc != null && net_loc != null) {
                    Util.addLog("Checking accuracy..");
                    //smaller the number more accurate result will
                    if (gps_loc.getAccuracy() > net_loc.getAccuracy()) {
                        finalLoc = net_loc;
                        Util.addLog("Network: Lat::" + finalLoc.getLatitude() + "------Long: " + finalLoc.getLongitude());
                    } else {
                        finalLoc = gps_loc;
                        Util.addLog("GPS: Lat::" + finalLoc.getLatitude() + "------Long: " + finalLoc.getLongitude());
                    }
                    Util.addLog("GPS Accuracy: " + gps_loc.getAccuracy() + " Network Accuracy: " + net_loc.getAccuracy());
                    Log.i(AppConstants.TAG, "GPS Accuracy: " + gps_loc.getAccuracy() + " Network Accuracy: " + net_loc.getAccuracy());
                    // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)
                } else {
                    if (gps_loc != null) {

                        finalLoc = gps_loc;
                        Log.i(AppConstants.TAG, "GPS Lat::" + finalLoc.getLatitude() + "------Long: " + finalLoc.getLongitude());
                        Util.addLog("GPS: Lat::" + finalLoc.getLatitude() + "------Long: " + finalLoc.getLongitude());

                        Util.addLog("GPS Accuracy: " + gps_loc.getAccuracy());
                        Log.i(AppConstants.TAG, "GPS Accuracy: " + gps_loc.getAccuracy());
                    } else if (net_loc != null) {

                        finalLoc = net_loc;
                        Log.i(AppConstants.TAG, "Network Lat::" + finalLoc.getLatitude() + "------Long: " + finalLoc.getLongitude());
                        Util.addLog("Network: Lat::" + finalLoc.getLatitude() + "------Long: " + finalLoc.getLongitude());

                        Util.addLog(" Network Accuracy: " + net_loc.getAccuracy());
                        Log.i(AppConstants.TAG, " Network Accuracy: " + net_loc.getAccuracy());
                    }
                }

                if (finalLoc != null) {
                    SharedPreferences.Editor editor = spf.edit();
                    editor.putString(AppConstants.LATTITUDE, String.valueOf(finalLoc.getLatitude()));
                    editor.putString(AppConstants.LONGITUDE, String.valueOf(finalLoc.getLongitude()));
                    editor.putFloat(AppConstants.LOCATION_ACCURACY, finalLoc.getAccuracy());
                    editor.apply();
                    Util.addLog("final location: Lat::" + finalLoc.getLatitude() + "------Long: " + finalLoc.getLongitude());
                } else {
                    Util.addLog("Final location null");
                }
                location = finalLoc;

                accuracyFinal = finalLoc.getAccuracy();
                // if (gps_enabled) {
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
                            objBr.onClickForControl(satelliteCount, spf.getFloat(AppConstants.LOCATION_ACCURACY,0.0f));

                        }
                    };

                    lm.registerGnssStatusCallback(mGnssStatusCallback);
                } else {
                    lm.addGpsStatusListener(this);//inserted new
                }
                //}


            }
        } catch (Exception e) {
            Util.addLog("Exception Getting location: " + e.getMessage());
            location = finalLoc;
        }
        return location;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String gnssStatusToString(GnssStatus gnssStatus) {

        try {
            StringBuilder builder = new StringBuilder("\nSATELLITE_STATUS | [Satellites : " + gnssStatus.getSatelliteCount() + "]");
            for (int i = 0; i < gnssStatus.getSatelliteCount(); i++) {
                builder
                        .append("*********************\n")
                        .append("Satellite No.: " + i)
                        .append(" Constellation = ")
                        .append(gnssStatus.getConstellationType(gnssStatus.getConstellationType(i)))
                        .append(", ");
                builder.append("Svid = ").append(gnssStatus.getSvid(i)).append(", ");
                builder.append("Cn0DbHz = ").append(gnssStatus.getCn0DbHz(i)).append(", ");
                builder.append("Elevation = ").append(gnssStatus.getElevationDegrees(i)).append(", ");
                builder.append("Azimuth = ").append(gnssStatus.getAzimuthDegrees(i)).append(", ");
                builder.append("hasEphemeris = ").append(gnssStatus.hasEphemerisData(i)).append(", ");
                builder.append("hasAlmanac = ").append(gnssStatus.hasAlmanacData(i)).append(", ");
                builder.append("usedInFix = ").append(gnssStatus.usedInFix(i)).append("\n");

            }
            builder.append("]");
            return builder.toString();
        }catch (Exception e){}
        return "";
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert(boolean isIsFromPoleData1) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                    isFromSetting = true;
                    spf.edit().putBoolean("test", true).commit();
                    isIsFromPoleData=isIsFromPoleData1;
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton(mContext.getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }catch (Exception e){

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(AppConstants.TAG,"Lat:"+location.getLatitude()+"LNG:"+location.getLongitude());
        objBrLocation.onClickForControl(location.getLatitude(),location.getLongitude(),location.getAccuracy());
        spf.edit().putFloat(AppConstants.LOCATION_ACCURACY, location.getAccuracy()).apply();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLocation();
                } else {
                    Toast.makeText(mContext, "All permission requests are not granted..", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        try {

            if (Util.isInternetAvailable(mContext)) {
                @SuppressLint("MissingPermission") GpsStatus gpsStatus = lm.getGpsStatus(null);
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

                    objBr.onClickForControl(i, spf.getFloat(AppConstants.LOCATION_ACCURACY,0.0f));
                }
            }

        } catch (Exception e) {

        }
    }
}


/*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
         ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
         ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
         ) {
         ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
         }

         if (location == null) {
                            Criteria criteria = new Criteria();
                            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                            String provider = locationManager.getBestProvider(criteria, true);
                            location = locationManager.getLastKnownLocation(provider);
                        }


                        else if (location == null) {
                            Criteria criteria = new Criteria();
                            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                            String provider = locationManager.getBestProvider(criteria, true);
                            location = locationManager.getLastKnownLocation(provider);
                        }
         */

 /*lm.getGpsStatus(null);
         mGpsStatus = lm.getGpsStatus(null);
         int iTempCountInView = 0;
         int iTempCountInUse = 0;

         Iterable<GpsSatellite> satellites =mGpsStatus.getSatellites();
        if(satellites!=null){
        for(GpsSatellite gpsSatellite : satellites){
        iTempCountInView++;
        if (gpsSatellite.usedInFix()) {
        iTempCountInUse++;
        }
        }
        }
        Util.addLogSatellites("Below API 24 Satellites: "+iTempCountInView+" | used: "+iTempCountInUse);
        Log.i(AppConstants.TAG,"Below API 24 Satellites: "+iTempCountInView+" | used: "+iTempCountInUse);

        spf.edit().putInt(AppConstants.SATELLITE_COUNTS,iTempCountInView).apply();*/

//private GpsStatus mGpsStatus;
//protected GpsListener gpsListener = new GpsListener();


                      /*  lm.addGpsStatusListener(new GpsStatus.Listener() {
                            @Override
                            public void onGpsStatusChanged(int event) {
                                Util.addLog("GPS STATUS API <24");
                            }
                        });*/