package com.CL.slcscanner.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import androidx.annotation.DrawableRes;
import androidx.core.app.ActivityCompat;

import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.CL.slcscanner.Activities.MainActivity;
import com.CL.slcscanner.Activities.SecurityCode;

import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.ClientType.ClientType;
import com.CL.slcscanner.Pojo.CommonResponse;
import com.CL.slcscanner.Pojo.Login.LoginResponse;
import com.CL.slcscanner.Pojo.PoleMaster.Datum;
import com.CL.slcscanner.Pojo.SLCDetails.Data;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.fragments.SelectLocationPoleFragment;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import butterknife.internal.Utils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vrajesh on 1/20/2018.
 */

public class Util {

    private static Uri imageUri;

    public static boolean isInternetAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }


    public static void dialogForMessage(Activity activity, String message) {
        try {
            if (activity != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alert.show();
            }
        } catch (Exception e) {

        }
    }


    public static void dialogForMessagePlayStoreNavigate(Activity activity, String message) {
        try {
            if (activity != null) {
                String[] ary = message.split("\\|");
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(ary[0])
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                try {
                                    //https://play.google.com/store/apps/details?id=com.example.android
                                    //)
                                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(ary[1].trim())));
                                } catch (Exception e) {

                                }
                               dialogInterface.dismiss();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alert.show();
            }
        } catch (Exception e) {

        }
    }

    public static void dialogForMessageNavigate(final Activity activity, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        //body.getStatus();
                        activity.startActivity(new Intent(activity, SecurityCode.class));
                        activity.finish();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();
    }

    public static void dialogForMapInfo(Activity activity, String title, String message) {

        try {
            if (activity != null) {
                Spanned s = Html.fromHtml(message);

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(s)
                        .setCancelable(true)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                alert.setTitle(title);
                alert.show();
            }
        } catch (Exception e) {
        }
    }

    public static void dialogForPermisionMessage(final Context context, String message) {

        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Permission Require");
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            try {
                                //context.startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                            } catch (Exception e) {
                                Util.addLog("Permission Setting Activity face issue: " + e.getMessage());
                            }
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alert.show();
        }
    }

    public static String[] getSPFData(String key, SharedPreferences spf) {
        //Retrieve the values
        Gson gson = new Gson();
        String jsonText = spf.getString(key, null);
        String[] text = gson.fromJson(jsonText, String[].class);  //EDIT: gso to gson
        return text;
    }

    public static void setSPFData(String key, List<String> data, SharedPreferences spf) {
        //Set the values
        Gson gson = new Gson();
        List<String> textList = new ArrayList<>();
        textList.addAll(data);
        String jsonText = gson.toJson(textList);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString(key, jsonText);
        editor.apply();
    }

    public static boolean hasPermissions(Context context, String... permissions) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;

    }

    public static void setAssetDataSPF(SharedPreferences spf, List<com.CL.slcscanner.Pojo.ClientAssets.Datum> arrayList) {
        //Set the values
        Gson gson = new Gson();
        List<com.CL.slcscanner.Pojo.ClientAssets.Datum> textList = new ArrayList<>();
        textList.addAll(arrayList);
        String jsonText = gson.toJson(textList);
        spf.edit().putString(AppConstants.SPF_CLIENT_ASSETS, jsonText).apply();
    }

    public static ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> gettAssetDataSPF(SharedPreferences spf) {
        Gson gson = new Gson();
        String jsonText = spf.getString(AppConstants.SPF_CLIENT_ASSETS, null);
        Type type = new TypeToken<ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum>>() {
        }.getType();
        ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> objText = gson.fromJson(jsonText, type);  //EDIT: gso to gson
        return objText;
    }

    public static void setAssetDataCopyFeatureSPF(SharedPreferences spf, List<com.CL.slcscanner.Pojo.ClientAssets.Datum> arrayList) {
        //Set the values
        Gson gson = new Gson();
        List<com.CL.slcscanner.Pojo.ClientAssets.Datum> textList = new ArrayList<>();
        textList.addAll(arrayList);
        String jsonText = gson.toJson(textList);
        spf.edit().putString(AppConstants.SPF_CLIENT_ASSETS_COPY_FEATURE, jsonText).apply();
    }

    public static ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> gettAssetDataCopyFeatureSPF(SharedPreferences spf) {
        Gson gson = new Gson();
        String jsonText = spf.getString(AppConstants.SPF_CLIENT_ASSETS_COPY_FEATURE, null);
        Type type = new TypeToken<ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum>>() {
        }.getType();
        ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> objText = gson.fromJson(jsonText, type);  //EDIT: gso to gson
        return objText;
    }


    public void displayDialog(Context context, final GoogleMap map, int type) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose MapTypePojo Type");

        int checkedItem = 0;
        if (type == GoogleMap.MAP_TYPE_HYBRID)
            checkedItem = 2;
        else if (type == GoogleMap.MAP_TYPE_NORMAL)
            checkedItem = 1;
        else if (type == GoogleMap.MAP_TYPE_SATELLITE)
            checkedItem = 0;

        // add a radio button list
        String[] animals = {"Satellite", "Standard", "Hybrid"};
        builder.setSingleChoiceItems(animals, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // user checked an item
                if (which == 0) {
                    map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                } else if (which == 1) {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                } else if (which == 2) {
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
                dialog.dismiss();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void hideKeyboard(Activity activity) {
        try {
            if (activity != null) {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view = activity.getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(activity);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
        }
    }

    public static void showKeyobard(Activity activity) {
        if (activity != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void SaveArraylistInSPF(SharedPreferences spf, ArrayList<Datum> arrayList) {
        //Set the values
        Gson gson = new Gson();
        ArrayList<Datum> textList = new ArrayList<Datum>();
        textList.addAll(arrayList);
        String jsonText = gson.toJson(textList);
        spf.edit().putString(AppConstants.SPF_POLE_DATA, jsonText).apply();
    }

    public static ArrayList<Datum> getArraylist(SharedPreferences spf) {
        Gson gson = new Gson();
        String jsonText = spf.getString(AppConstants.SPF_POLE_DATA, null);
        Type type = new TypeToken<ArrayList<Datum>>() {
        }.getType();
        ArrayList<Datum> objText = gson.fromJson(jsonText, type);  //EDIT: gso to gson
        return objText;
    }

    public static View.OnTouchListener colorFilter() {

        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        //overlay is black with transparency of 0x77 (119)
                        view.getDrawable().setColorFilter(0x770072BC, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return false;
            }
        };

    }

    public static void addLog(String msg) {
        if (AppConstants.isLogDisplay) {

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy '|' HH:mm:ss ZZZZZ", Locale.ENGLISH);
                Date dt = new Date();
                File root = Environment.getExternalStorageDirectory();
                if (root.canWrite()) {
                    File dir = new File(root + "/SLCScanner");
                    if (!dir.exists())
                        dir.mkdir();

                    File file = new File(dir, "log.txt");

                    FileWriter writer = new FileWriter(file, true);
                    BufferedWriter out = new BufferedWriter(writer);
                    out.append("\n [" + dateFormat.format(new Date()) + "] " + msg + "\n");
                    out.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void addLogSatellites(String msg) {
        if (AppConstants.isLogDisplay) {

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy '|' HH:mm:ss ZZZZZ", Locale.ENGLISH);
                Date dt = new Date();
                File root = Environment.getExternalStorageDirectory();
                if (root.canWrite()) {
                    File dir = new File(root + "/SLCScanner");
                    if (!dir.exists())
                        dir.mkdir();

                    File file = new File(dir, "GPS.txt");

                    FileWriter writer = new FileWriter(file, true);
                    BufferedWriter out = new BufferedWriter(writer);
                    out.append("\n [" + dateFormat.format(new Date()) + "] " + msg + "\n");
                    out.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public Bitmap getMarkerBitmapFromView(View view, @DrawableRes int resId, ImageView marker_image) {

        marker_image.setImageResource(resId);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }


    public class VersionChecker extends AsyncTask<String, String, String> {

        String newVersion;
        String packageName;
        Context mContex;

        public VersionChecker(String packageName, Context mContex) {
            this.packageName = packageName;
            this.mContex = mContex;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                String packageName = mContex.getPackageName();
                newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName + "&hl=en")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div[itemprop=softwareVersion]")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return newVersion;
        }
    }

    public void commonLoginDialog(final Activity activity, final String msg) {

        if (activity != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            // Get the layout inflater
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.dilog_login, null);
            final EditText etUsername = view.findViewById(R.id.etUsername);
            final EditText etPass = view.findViewById(R.id.etPass);

            Button btSend = (Button) view.findViewById(R.id.btSend);
            builder.setCancelable(false);
            builder.setView(view);
            final AlertDialog loginAlertDialog = builder.create();
            loginAlertDialog.show();

            btSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etUsername.getText().toString().equals("")) {
                        Util.dialogForMessage(activity, activity.getResources().getString(R.string.empty_username));
                    } else if (etPass.getText().toString().equals("")) {
                        Util.dialogForMessage(activity, activity.getResources().getString(R.string.empty_username));
                    } else {
                        if (Util.isInternetAvailable(activity)) {
                            sendLoginDetailToSever(activity, loginAlertDialog, etUsername.getText().toString(), etPass.getText().toString(), 0.0, 0.0, msg);
                        } else {
                            Util.dialogForMessage(activity, activity.getResources().getString(R.string.no_internet_connection));
                        }
                        //login call
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
                            Util.dialogForMessage(activity, activity.getResources().getString(R.string.empty_username));
                        } else if (etPass.getText().toString().equals("")) {
                            Util.dialogForMessage(activity, activity.getResources().getString(R.string.empty_password));
                        } else {
                            //login call
                            sendLoginDetailToSever(activity, loginAlertDialog, etUsername.getText().toString(), etPass.getText().toString(), 0.0, 0.0, msg);
                        }
                    }
                    return handled;
                }
            });
        }
    }


    void sendLoginDetailToSever(final Activity activity, final AlertDialog loginAlertDialog, String username, String password, Double lattitude, Double longitude, final String msg) {

        API objApi = new SLCScanner().networkCall();
        final SharedPreferences spf = activity.getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        final String client_id = spf.getString(AppConstants.CLIENT_ID, "");
        final String user_id = spf.getString(AppConstants.USER_ID, "");

        final ProgressDialog loginProgressDialog = new ProgressDialog(activity);
        loginProgressDialog.setMessage(activity.getResources().getString(R.string.loading));
        loginProgressDialog.setCancelable(false);
        loginProgressDialog.show();

        Call<LoginResponse> objCheckUnieCall = objApi.checkUserLogin(
                client_id,
                user_id,
                username,
                password,
                lattitude,
                longitude,
                "Android",
                spf.getString(AppConstants.VERSION, "")
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
                        Toast.makeText(activity, objCommonResponse.getMsg().toString(), Toast.LENGTH_SHORT).show();
                        if (activity != null)
                            Util.hideKeyboard(activity);

                        Util.addLog(msg + " : Login Successful");

                        if (loginAlertDialog.isShowing())
                            loginAlertDialog.dismiss();

                    } else if (objCommonResponse.getStatus().toString().equalsIgnoreCase("error")) {
                        Util.dialogForMessage(activity, objCommonResponse.getMsg().toString());
                        Util.addLog(msg + " : Login failed");
                    } else if (objCommonResponse.getStatus().toString().equalsIgnoreCase("-1")) {
                        Util.dialogForMessage(activity, objCommonResponse.getMsg().toString());
                        //Util.addLog("Login failed");
                        //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else if (objCommonResponse.getStatus().toString().equalsIgnoreCase("0")) {
                        Util.dialogForMessageNavigate(activity, objCommonResponse.getMsg().toString());
                    } else
                        Util.dialogForMessage(activity, activity.getResources().getString(R.string.server_error));

                } else
                    Util.dialogForMessage(activity, activity.getResources().getString(R.string.server_error));
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (loginProgressDialog.isShowing()) {
                    loginProgressDialog.dismiss();
                }
                Util.dialogForMessage(activity, t.getLocalizedMessage());
            }
        });
    }

    public static void deletePreviewFile(Context context) {
        /*final File fileCamerra = new File(Environment.getExternalStorageDirectory() + "/SLCScanner/Preview.jpg");
        if (fileCamerra.exists())
            fileCamerra.delete();*/


        File folder = new File(String.valueOf(context.getExternalFilesDir(Environment.DIRECTORY_DCIM)));
        folder.mkdirs();
        File f = new File(folder, "Preview.jpg");
        if (f.exists()) {
            f.delete();
        }

    }

    public static void storeImage(Bitmap bitmap, String name) {
        File filesDir = Environment.getExternalStorageDirectory();
        //File filesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = new File(filesDir, "/SLCScanner/" + name + ".jpg");

        FileOutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("***", "Error writing bitmap", e);
        }
        //imageUri = Uri.fromFile(imageFile);
    }

    public static void storeImage1(Bitmap bitmap, String name) {

        File root = Environment.getExternalStorageDirectory();
        if (root.canWrite()) {
            File dir = new File(root + "/SLCScanner");
            if (!dir.exists())
                dir.mkdir();
        }

        File filesDir = Environment.getExternalStorageDirectory();
        File imageFile = new File(filesDir, "/SLCScanner/" + name + ".jpg");

        ByteArrayOutputStream bos;
        try {
            bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            byte[] bytes = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.e("ERROR", "Error writing bitmap", e);
        }
        //imageUri = Uri.fromFile(imageFile);
    }


    public static void storeImage2(Context context,Bitmap bitmap) {

        //File root = Environment.getExternalStorageDirectory();

        File folder = new File(String.valueOf(context.getExternalFilesDir(Environment.DIRECTORY_DCIM)));
        folder.mkdirs();
        File file = new File(folder, "Preview.jpg");

        if (file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*File root= new File(context.getFilesDir() + "/SLCScanner");
        if (root.canWrite()) {
            File dir = new File(root + "/SLCScanner");
            if (!dir.exists())
                dir.mkdir();
        }*/

        //File filesDir = Environment.getExternalStorageDirectory();
        //File imageFile = new File(filesDir, "/SLCScanner/" + name + ".jpg");

        ByteArrayOutputStream bos;
        try {
            bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            byte[] bytes = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.e("ERROR", "Error writing bitmap", e);
        }
        //imageUri = Uri.fromFile(imageFile);
    }

    public static RequestBody getRequestBody(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);

    }

    public String getRealPathFromURI(Uri contentUri, Activity activity) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public class RotateTransformation extends BitmapTransformation {

        private float rotateRotationAngle = 0f;

        public RotateTransformation(Context context, float rotateRotationAngle) {
            //super(context);
            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Matrix matrix = new Matrix();

            matrix.postRotate(rotateRotationAngle);

            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {
            messageDigest.update(("rotate" + rotateRotationAngle).getBytes());
        }
    }

    public Bitmap setScaledPic(String mCurrentPhotoPath, ImageView mImageView) {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
    }

    private static final int MAX_HEIGHT = 1024;
    private static final int MAX_WIDTH = 1024;

    public static Bitmap decodeSampledBitmap(Context context, Uri selectedImage, String filePath)
            throws IOException {

        Bitmap img;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            img = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                            context.getContentResolver(),
                            selectedImage));
        } else {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
            BitmapFactory.decodeStream(imageStream, null, options);
            imageStream.close();

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            imageStream = context.getContentResolver().openInputStream(selectedImage);
            img = BitmapFactory.decodeStream(imageStream, null, options);


            int orientation;
            try {
                orientation = Util.getCameraPhotoOrientation(context, selectedImage, filePath);
            } catch (Exception e) {
                try {
                    orientation = Util.getRotation(context, selectedImage);
                } catch (Exception e1) {
                    orientation = 0;
                }
            }

            try {
                //rotation if required
                if (orientation != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(orientation);
                    img = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
                    //img.recycle();
                }
            } catch (Exception e) {

            }
        }
        //img = rotateImageIfRequired(context,img, selectedImage);

        return img;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * Rotate an image if required.
     *
     * @param img
     * @param selectedImage
     * @return
     */
    public static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) {

        // Detect rotation
        int rotation = getRotation(context, selectedImage);
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            img.recycle();
            return rotatedImg;
        } else {
            return img;
        }
    }

    /**
     * Get the rotation of the last image added.
     *
     * @param context
     * @param selectedImage
     * @return
     */
    public static int getRotation(Context context, Uri selectedImage) {

        int rotation = 0;
        try {
            if (context != null) {
                ContentResolver content = context.getContentResolver();

                Cursor mediaCursor = content.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{"orientation", "date_added"},
                        null, null, "date_added desc");

                if (mediaCursor != null && mediaCursor.getCount() != 0) {
                    while (mediaCursor.moveToNext()) {
                        rotation = mediaCursor.getInt(0);
                        break;
                    }
                }
                mediaCursor.close();
            }
        } catch (Exception e) {
        }
        return rotation;
    }

    public static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);

            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }


    public static void BitmapToFile(Bitmap bitmap, String destination) {
        //Convert bitmap to byte array

        File f = new File(destination);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap decodeUriToBitmap(Context mContext, Uri sendUri) {
        Bitmap getBitmap = null;
        try {
            InputStream image_stream;
            try {
                image_stream = mContext.getContentResolver().openInputStream(sendUri);
                getBitmap = BitmapFactory.decodeStream(image_stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getBitmap;
    }

    void showCopyDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Do you want to Copy Pole data from previous entry ?")
                .setCancelable(false)
                .setTitle("Alert")
                .setPositiveButton(activity.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        //loadFragment(new SelectLocationPoleFragment());
                        //saveNewSLCData();
                    }
                });
        builder.setNegativeButton(activity.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
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


    public void setMapType(final Context context, GoogleMap googleMap, final SharedPreferences spf) {

        try {
            final int selectedMapIndex = spf.getInt(AppConstants.SELECTED_MAP_INDEX, 1);

            if (selectedMapIndex == 0) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            } else if (selectedMapIndex == 1) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            } else if (selectedMapIndex == 2) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            } else if (selectedMapIndex == 3) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                TileOverlayOptions options1 = new TileOverlayOptions();
                options1.tileProvider(new UrlTileProvider(256, 256) {
                    @Override
                    public synchronized URL getTileUrl(int x, int y, int zoom) {
                        String styleUrl = String.format(Locale.US, AppConstants.OPEN_STREET_MAP_STANDARD_URL, zoom, x, y);
                        //String s = String.format(Locale.US,selectedMap,zoom, x, y);
                        URL url = null;
                        try {
                            url = new URL(styleUrl);
                        } catch (MalformedURLException e) {
                            throw new AssertionError(e);
                        }
                        return url;
                    }
                });
                googleMap.addTileOverlay(options1);
            }
        } catch (Exception e) {
        }
         /*else if (selectedMap.equalsIgnoreCase(context.getResources().getString(R.string.google_map_terrian))) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }*/
    }

    public Spanned getSpannedText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
        }
    }

    public static void LocaleCheck(SharedPreferences spf, Context context) {
        boolean isLanguageSelected = spf.getBoolean(AppConstants.LANGUAGE_SELECTED, false);

        String locale = Locale.getDefault().getLanguage();
        spf.edit().putString(AppConstants.LANGUAGE_LOCALE, locale).apply();
        Log.i(AppConstants.TAG, "Locale: " + locale);

        if (!isLanguageSelected) {
            if (locale.equalsIgnoreCase("en"))
                spf.edit().putString(AppConstants.SELECTED_LANG_TYPE, context.getString(R.string.key_english)).apply();
            else if (locale.equalsIgnoreCase("es"))
                spf.edit().putString(AppConstants.SELECTED_LANG_TYPE, context.getString(R.string.key_spanins)).apply();
            else if (locale.equalsIgnoreCase("pt"))
                spf.edit().putString(AppConstants.SELECTED_LANG_TYPE, context.getString(R.string.key_portuguese)).apply();
        }
    }

    private BroadcastReceiver mLangReceiver = null;

    public BroadcastReceiver setupLangReceiver(final SharedPreferences spf, Context context, final Activity activity) {
        try {
            if (context != null && activity != null) {
                if (mLangReceiver == null) {

                    mLangReceiver = new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            // do what you want

                            boolean isLanguageSelected = spf.getBoolean(AppConstants.LANGUAGE_SELECTED, false);
                            //String locale = Locale.getDefault().getLanguage();
                            String locale = Resources.getSystem().getConfiguration().locale.getLanguage();
                            spf.edit().putString(AppConstants.LANGUAGE_LOCALE, locale).apply();
                            spf.edit().putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, "").apply();

                            Log.i(AppConstants.TAG, "Locale: BR " + locale);
                            if (!isLanguageSelected) {
                                if (locale.equalsIgnoreCase("en"))
                                    spf.edit().putString(AppConstants.SELECTED_LANG_TYPE, context.getString(R.string.key_english)).apply();
                                else if (locale.equalsIgnoreCase("es"))
                                    spf.edit().putString(AppConstants.SELECTED_LANG_TYPE, context.getString(R.string.key_spanins)).apply();
                                else if (locale.equalsIgnoreCase("pt"))
                                    spf.edit().putString(AppConstants.SELECTED_LANG_TYPE, context.getString(R.string.key_portuguese)).apply();

                                //app locale change code:
                                // relaunch(activity);
                                //activity.recreate();

                        /*Intent intent1= new Intent(activity, SplashScreen.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent1);
                        Runtime.getRuntime().exit(0);*/
                                //activity.finish();

                            }
                        }
                    };

                    IntentFilter filter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
                    context.registerReceiver(mLangReceiver, filter);
                }
            }
        } catch (Exception e) {
        }
        return mLangReceiver;
    }

    public static String decodeUnicode(String theString) {
        try {
            char aChar;
            int len = theString.length();
            StringBuffer outBuffer = new StringBuffer(len);
            for (int x = 0; x < len; ) {
                aChar = theString.charAt(x++);
                if (aChar == '\\') {
                    aChar = theString.charAt(x++);
                    if (aChar == 'u') {
                        // Read the xxxx
                        int value = 0;
                        for (int i = 0; i < 4; i++) {
                            aChar = theString.charAt(x++);
                            switch (aChar) {
                                case '0':
                                case '1':
                                case '2':
                                case '3':
                                case '4':
                                case '5':
                                case '6':
                                case '7':
                                case '8':
                                case '9':
                                    value = (value << 4) + aChar - '0';
                                    break;
                                case 'a':
                                case 'b':
                                case 'c':
                                case 'd':
                                case 'e':
                                case 'f':
                                    value = (value << 4) + 10 + aChar - 'a';
                                    break;
                                case 'A':
                                case 'B':
                                case 'C':
                                case 'D':
                                case 'E':
                                case 'F':
                                    value = (value << 4) + 10 + aChar - 'A';
                                    break;
                                default:
                                    throw new IllegalArgumentException(
                                            "Malformed   \\uxxxx   encoding.");
                            }

                        }
                        outBuffer.append((char) value);
                    } else {
                        if (aChar == 't')
                            aChar = '\t';
                        else if (aChar == 'r')
                            aChar = '\r';
                        else if (aChar == 'n')
                            aChar = '\n';
                        else if (aChar == 'f')
                            aChar = '\f';
                        outBuffer.append(aChar);
                    }
                } else
                    outBuffer.append(aChar);
            }
            return outBuffer.toString();
        } catch (Exception e) {
            return theString;
        }
    }

    public void switchLanguage(Activity activity, String languageCode, boolean isFromMain) {
        try {
            if (activity != null) {
                LanguageHelper.setLanguage(activity, languageCode);
                if (!isFromMain)
                    relaunch(activity);
            }
        } catch (Exception e) {
        }
    }

    public void relaunch(Activity activity) {
        try {
            if (activity != null) {
                Intent intent;
                intent = new Intent(activity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("language", "yes");
                activity.startActivity(intent);
                Runtime.getRuntime().exit(0);
                activity.finish();
            }
        } catch (Exception e) {
        }

    }

    public static String getDeviceLocale(SharedPreferences spf) {
        //String locale = Locale.getDefault().getLanguage();
        String locale = Resources.getSystem().getConfiguration().locale.getLanguage();
        String langCode;
        if (locale.equals(AppConstants.LANGUAGE_CODE_ENGLISH) || locale.equals(AppConstants.LANGUAGE_CODE_SPANISH) || locale.equals(AppConstants.LANGUAGE_CODE_PORTUGUES))
            langCode = spf.getString(AppConstants.LANGUAGE_LOCALE, locale);
        else
            langCode = AppConstants.LANGUAGE_CODE_ENGLISH;
        return langCode;
    }

    public void dismissProgressDialog(Dialog pDialog) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public void loadFragment(Fragment fragment, Activity activity) {
        try {
            if (activity != null) {
                // create a FragmentManager
                FragmentManager fm = activity.getFragmentManager();
                // create a FragmentTransaction to begin the transaction and replace the Fragment
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                // replace the FrameLayout with new Fragment
                fragmentTransaction.replace(R.id.frm1, fragment);
                //fragmentTransaction.commit(); // save the changes
                fragmentTransaction.commitAllowingStateLoss();
            }
        } catch (Exception e) {
        }
    }

    public boolean checkCameraPermmsion(Activity activity) {
        if (activity != null) {
            String permission = Manifest.permission.CAMERA;
            int res = activity.checkCallingOrSelfPermission(permission);
            return (res == PackageManager.PERMISSION_GRANTED);
        } else
            return true;
    }

    public void responseHandle(final Activity activity, int code, final ResponseBody body) {
        if (activity != null) {
            String msg = activity.getResources().getString(R.string.server_error);
            String status = "";
            try {
                JSONObject jObjError = new JSONObject(body.string());
                msg = jObjError.getString("message").toString();
                status = jObjError.getString("status").toString();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (code == 401 || code == 500) {
                if (activity != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    final String finalStatus = status;
                    builder.setMessage(msg)
                            .setCancelable(true)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    //body.getStatus();
                                    if (!finalStatus.equals("-1")) {
                                        activity.finish();
                                        activity.startActivity(new Intent(activity, SecurityCode.class));
                                    }
                                }
                            });

                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alert.show();
                }
            } else
                Util.dialogForMessage(activity, msg.toString());
        }
    }

    public static boolean saveSLCMAC(Context context, ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> objList, String mac, String slc) {

        SharedPreferences sp = context.getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        /* sKey is an array */
        mEdit1.putInt("Status_size", objList.size());
        //sp.edit().putString(AppConstants.IMAGE_URL, "").apply();

        for (int i = 0; i < objList.size(); i++) {
            mEdit1.remove("BtnText" + i);
            mEdit1.remove("Selectected" + i);
            if (objList.get(i).getAttrKey().toString().equalsIgnoreCase("mac_address")) {
                Log.i("---*", "mac" + mac);
                mEdit1.putString("BtnText" + i, mac);
                mEdit1.putString("Selectected" + i, mac);
            } else if (objList.get(i).getAttrKey().toString().equalsIgnoreCase("slc_id")) {
                Log.i("---*", "slc" + slc);
                mEdit1.putString("BtnText" + i, slc);
                mEdit1.putString("Selectected" + i, slc);
            }
        }
        return mEdit1.commit();
    }


    public static boolean saveArray(Context context, ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> objList, String mac, String slc, boolean isForScannerEditUI) {

        SharedPreferences sp = context.getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1 = sp.edit();
        /* sKey is an array */
        mEdit1.putInt("Status_size", objList.size());

        if (!isForScannerEditUI)
            sp.edit().putString(AppConstants.IMAGE_URL, "").apply();

        for (int i = 0; i < objList.size(); i++) {
            mEdit1.remove("AssetName" + i);
            mEdit1.remove("AttributeName" + i);
            mEdit1.remove("AttrKey" + i);
            mEdit1.remove("BtnText" + i);
            mEdit1.remove("Note" + i);
            mEdit1.remove("Selectected" + i);
            mEdit1.remove("isNoteData" + i);
            mEdit1.remove("isStaticData" + i);
            mEdit1.remove("isClickable" + i);
            mEdit1.remove("isMacIdClick" + i);
            mEdit1.remove("ispicklist" + i);
            mEdit1.remove("isRequire" + i);
            mEdit1.remove("type" + i);

            String temBtnText;
            String tempSelected;

            if (isForScannerEditUI) {
                if (objList.get(i).getAttrKey().toString().equalsIgnoreCase("mac_address")) {
                    Log.i("---*", "mac" + mac);
                    temBtnText = mac;
                    tempSelected = mac;
                } else if (objList.get(i).getAttrKey().toString().equalsIgnoreCase("slc_id")) {
                    Log.i("---*", "slc" + slc);
                    temBtnText = slc;
                    tempSelected = slc;
                } else {
                    temBtnText = objList.get(i).getBtnText();
                    tempSelected = objList.get(i).getSelectected();
                }
            } else {
                temBtnText = objList.get(i).getBtnText();
                tempSelected = objList.get(i).getSelectected();
            }

            mEdit1.putString("AssetName" + i, objList.get(i).getAssetName());
            mEdit1.putString("AttributeName" + i, objList.get(i).getAttributeName());
            mEdit1.putString("AttrKey" + i, objList.get(i).getAttrKey());
            mEdit1.putString("BtnText" + i, temBtnText);
            mEdit1.putString("Note" + i, objList.get(i).getNote());
            mEdit1.putString("Selectected" + i, tempSelected);
            mEdit1.putBoolean("isNoteData" + i, objList.get(i).isNoteData());
            mEdit1.putBoolean("isStaticData" + i, objList.get(i).isStaticData());
            mEdit1.putBoolean("isClickable" + i, objList.get(i).isClickable());
            mEdit1.putBoolean("isMacIdClick" + i, objList.get(i).isMacIdClick());
            mEdit1.putString("ispicklist" + i, objList.get(i).getIspicklist());
            mEdit1.putString("isRequire" + i, objList.get(i).getIsRequire());
            mEdit1.putString("type" + i, objList.get(i).getType());

            //Set<com.CL.slcscanner.Pojo.ClientAssets.Datum> hSet = new HashSet<com.CL.slcscanner.Pojo.ClientAssets.Datum>();

            Set<String> hSet = new HashSet<String>();

            List<String> objListValues = objList.get(i).getValues();

            if (objListValues != null) {
                if (objListValues.size() > 0) {
                    for (String x : objListValues)
                        hSet.add(x);

                    mEdit1.putStringSet("values" + i, hSet);
                }
            }

        }
        return mEdit1.commit();
    }

    public static ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> loadArray(Context mContext, ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> objList) {
        SharedPreferences myPref = mContext.getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        objList.clear();
        int size = myPref.getInt("Status_size", 0);

        for (int i = 0; i < size; i++) {
            com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum1 = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
            objDatum1.setAttributeName(myPref.getString("AssetName" + i, ""));
            objDatum1.setAttributeName(myPref.getString("AttributeName" + i, ""));
            objDatum1.setAttrKey(myPref.getString("AttrKey" + i, ""));
            objDatum1.setBtnText(myPref.getString("BtnText" + i, ""));
            objDatum1.setSelectected(myPref.getString("Selectected" + i, ""));
            objDatum1.setNoteData(myPref.getBoolean("isNoteData" + i, false));
            objDatum1.setStaticData(myPref.getBoolean("isStaticData" + i, false));
            objDatum1.setNote(myPref.getString("Note" + i, ""));
            objDatum1.setClickable(myPref.getBoolean("isClickable" + i, true));
            objDatum1.setMacIdClick(myPref.getBoolean("isMacIdClick" + i, true));
            objDatum1.setIsRequire(myPref.getString("isRequire" + i, ""));
            objDatum1.setIspicklist(myPref.getString("ispicklist" + i, ""));
            objDatum1.setType(myPref.getString("type" + i, ""));

            Set<String> objSet = myPref.getStringSet("values" + i, new HashSet<>());

            List<String> objValues = new ArrayList<String>(objSet);
            objDatum1.setValues(objValues);

            objList.add(objDatum1);

        }
        return objList;
    }

    public static ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> getNotesData(String pole_option) {
        ArrayList<com.CL.slcscanner.Pojo.ClientAssets.Datum> mList1 = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(pole_option);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);

                //Log.i(AppConstants.TAG, object.toString());

                String keybool = object.getString("key");
                String keytext = keybool + "_text";

                String ValueBool = object.getString(keybool);
                String ValueText = object.getString(keytext);

                com.CL.slcscanner.Pojo.ClientAssets.Datum obj = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
                obj.setAttrKey(keybool);
                obj.setSelectected(ValueBool);
                obj.setAttributeName("");
                obj.setBtnText(ValueBool);
                obj.setStaticData(false);
                obj.setNoteData(true);
                mList1.add(obj);

                com.CL.slcscanner.Pojo.ClientAssets.Datum obj2 = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
                obj2.setAttrKey(keytext);
                obj2.setSelectected(ValueText);
                obj2.setAttributeName("");
                obj2.setBtnText(ValueText);
                obj2.setStaticData(false);
                obj2.setNoteData(true);
                mList1.add(obj2);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mList1;

    }

    public interface ClickNodeType {
        void setOnClickNodeType(com.CL.slcscanner.Pojo.Login.Datum objOnClickLampType, int position);
    }

    ClickNodeType objClickNodeType;

    public void ShowNodeTypeDialog(final List<com.CL.slcscanner.Pojo.Login.Datum> objList, String title, Activity activity, SharedPreferences spf, String ui, ClickNodeType objNodeType) {
        //String event_name = spf.getString(AppConstants.GLOBAL_EVENT_NAME, "") + "NodeType";
        //Log.d(AppConstants.TAG, "Event:" + event_name);

        //FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
        //Bundle bundle = new Bundle();
        // bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name);
        //mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        objClickNodeType = objNodeType;
        AlertDialog dialogSelection = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_title, null);

        TextView tvTitle1 = view.findViewById(R.id.tvDialogTitle1);
        tvTitle1.setText(title);
        builder.setCustomTitle(view);
        //builder.setTitle(title);

        int tempIndex = 0;//skip unknown
        if (objList.size() > 0) {
            tempIndex = 1;
        }
        int selectedIndex = 0;

        //save
        if (ui.toString().equalsIgnoreCase(AppConstants.SCAN_MAC_UI)) {
            selectedIndex = spf.getInt(AppConstants.SELECTED_NODE_TYPE_INDEX_SAVE, tempIndex);
        }

        //list
        else if (ui.toString().equalsIgnoreCase(AppConstants.LIST_SLC_UI)) {
            selectedIndex = spf.getInt(AppConstants.SELECTED_NODE_TYPE_INDEX_LIST, 0);
        }

        /*//edit mac id scan screen
        else if(ui.toString().equalsIgnoreCase(AppConstants.SCAN_EDIT_MAC_UI)){
            selectedIndex = spf.getInt(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_MAC, tempIndex);//default
        }*/

        //edit SLC
        else if (ui.toString().equalsIgnoreCase(AppConstants.EDIT_SLC_UI)) {
            selectedIndex = spf.getInt(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_SLC, tempIndex);//default 1
        }


        //this is for edit UI
        //if (position != -1)
        //selectedIndex = position;

        //spf.edit().putString(AppConstants.SELECTED_NODE_TYPE, objList.get(selectedIndex).getClientType()).apply();

        final String[] ary = new String[objList.size()];

        for (int i = 0; i < objList.size(); i++) {
            ary[i] = objList.get(i).getClientType();
        }

        builder.setSingleChoiceItems(ary, selectedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog1, int which) {
                //objList.get(which).getClientType().setChecked(true);
                objList.get(which).setChecked(true);

                com.CL.slcscanner.Pojo.Login.Datum objMapTypePojo = objList.get(which);

                SharedPreferences.Editor edit = spf.edit();
                //save  - macid
                if (ui.toString().equalsIgnoreCase(AppConstants.SCAN_MAC_UI)) {
                    edit.putString(AppConstants.SELECTED_NODE_TYPE_SAVE, objMapTypePojo.getClientType().toString());
                    edit.putInt(AppConstants.SELECTED_NODE_TYPE_INDEX_SAVE, which);
                }

                //list
                else if (ui.toString().equalsIgnoreCase(AppConstants.LIST_SLC_UI)) {
                    edit.putString(AppConstants.SELECTED_NODE_TYPE_LIST, objMapTypePojo.getClientType().toString());
                    edit.putInt(AppConstants.SELECTED_NODE_TYPE_INDEX_LIST, which);
                }

                //edit mac id scan screen
               /* else if(ui.toString().equalsIgnoreCase(AppConstants.SCAN_EDIT_MAC_UI)){
                    edit.putString(AppConstants.SELECTED_NODE_TYPE_EDIT_MAC, objMapTypePojo.getClientType().toString());
                    edit.putInt(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_MAC, which);
                }*/

                //edit SLC
                else if (ui.toString().equalsIgnoreCase(AppConstants.EDIT_SLC_UI)) {
                    edit.putString(AppConstants.SELECTED_NODE_TYPE_EDIT_SLC, objMapTypePojo.getClientType().toString());
                    edit.putInt(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_SLC, which);
                }

                edit.apply();
                // new Utils().setMapType(getActivity(), googleMapGlobal, spf);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //dialog.dismiss();
                        dialog1.dismiss();
                        objClickNodeType.setOnClickNodeType(objMapTypePojo, which);
                    }
                }, 500);
            }
        });

        builder.setNegativeButton(activity.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // create and show the alert dialog
        dialogSelection = builder.create();
        dialogSelection.show();
    }

    public static void SaveClientTypeList(SharedPreferences spf, List<com.CL.slcscanner.Pojo.Login.Datum> arrayList) {
        //Set the values
        Gson gson = new Gson();
        ArrayList<com.CL.slcscanner.Pojo.Login.Datum> textList = new ArrayList<>();
        textList.addAll(arrayList);
        String jsonText = gson.toJson(textList);
        spf.edit().putString(AppConstants.SPF_CLIENT_TYPE_DATA, jsonText).apply();
        if (arrayList.size() > 0)
            spf.edit().putBoolean(AppConstants.SPF_CLIENT_TYPE_DATA_ADDED, true).commit();
        else
            spf.edit().putBoolean(AppConstants.SPF_CLIENT_TYPE_DATA_ADDED, false).commit();
    }

    public static ArrayList<com.CL.slcscanner.Pojo.Login.Datum> getClientTypeList(SharedPreferences spf) {
        Gson gson = new Gson();
        String jsonText = spf.getString(AppConstants.SPF_CLIENT_TYPE_DATA, null);
        Type type = new TypeToken<ArrayList<com.CL.slcscanner.Pojo.Login.Datum>>() {
        }.getType();
        ArrayList<com.CL.slcscanner.Pojo.Login.Datum> objText = gson.fromJson(jsonText, type);  //EDIT: gso to gson
        return objText;
    }

}