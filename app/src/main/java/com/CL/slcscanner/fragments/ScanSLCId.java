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
import android.os.Bundle;

import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.ClientType.ClientType;
import com.CL.slcscanner.Pojo.CommonResponse;
import com.CL.slcscanner.Pojo.CommonResponse2;
import com.CL.slcscanner.Pojo.Login.Datum;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.ScannerCallBack;
import com.CL.slcscanner.Utils.Util;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vrajesh on 2/24/2018.
 */

public class ScanSLCId extends Fragment {

    private ZXingScannerView mScannerView;
    private int mCameraId = -1;

    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";

    SharedPreferences spf;
    ScannerCallBack objScannerCallBack;

    API objApi;
    AlertDialog macDialog;
    ProgressDialog progressDialog;
    String client_id, user_id;

    TextView tbTitleSkip;
    TextView tbTitleLine1;
    ImageView iv_edit_mac;

    LinearLayout llPoleBack;
    TextView tvPoleEditBack;

    LinearLayout llNodeType;
    TextView tvNodeType;
    FrameLayout frmNode;

    TextView txtTest;
    AppBarLayout appBarMainn;

    Bundle mBundle;
    boolean isfromeditui = false;
    boolean isShowPrevious = false;

    String slcid;
    Util objUtil;
    ProgressDialog dialog_wait;

    String token;
    ArrayList<com.CL.slcscanner.Pojo.Login.Datum> mClientType;
    String node_type;
    private FirebaseAnalytics mFirebaseAnalytics;
    String event_name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle state) {

        mBundle = getArguments();
        if (mBundle != null) {
            isfromeditui = mBundle.getBoolean(AppConstants.ISFROMBACK);
        }
        objUtil = new Util();
        tbTitleSkip = getActivity().findViewById(R.id.tbTitleSkip);
        llPoleBack = getActivity().findViewById(R.id.llPoleBack);
        llPoleBack.setVisibility(View.VISIBLE);
        frmNode = getActivity().findViewById(R.id.frmNode);
        llNodeType = getActivity().findViewById(R.id.llNodeType);
        tvNodeType = getActivity().findViewById(R.id.tvNodeType);
        llNodeType.setVisibility(View.GONE);

        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        event_name = spf.getString(AppConstants.CLIENT_ID, null) + "_" + spf.getString(AppConstants.USER_ID, null) + "_";

        mFirebaseAnalytics.setCurrentScreen(getActivity(), "ScanSLCUidUI", null);

        tvPoleEditBack = getActivity().findViewById(R.id.tvPoleEditBack);
        appBarMainn = getActivity().findViewById(R.id.appBarMainn);
        appBarMainn.setVisibility(View.VISIBLE);

        tbTitleLine1 = getActivity().findViewById(R.id.tbTitleLine1);
        iv_edit_mac = getActivity().findViewById(R.id.iv_edit_mac);

        txtTest = getActivity().findViewById(R.id.txtTest);
        txtTest.setVisibility(View.GONE);

        objApi = new SLCScanner().networkCall();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.loading));
        tbTitleLine1.setText(getResources().getString(R.string.scan_slc_id));
        tvPoleEditBack.setText(spf.getString(AppConstants.MACID_LABLE, "").toString());

        dialog_wait = new ProgressDialog(getActivity());
        dialog_wait.setMessage(getResources().getString(R.string.please_wait));
        dialog_wait.setCancelable(false);

        mScannerView = new ZXingScannerView(getActivity());
        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }

        client_id = spf.getString(AppConstants.CLIENT_ID, "");
        user_id = spf.getString(AppConstants.USER_ID, "");
        token = spf.getString(AppConstants.TOKEN, "");

        // slcid=spf.getString(AppConstants.SPF_TEMP_SLCID,"");
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.frm1);
        if (currentFragment instanceof ScanSLCId) {
            if (!objUtil.checkCameraPermmsion(getActivity())) {
                Util.dialogForMessage(getActivity(), getResources().getString(R.string.camerra_permission));
            }
        }

        mClientType = new ArrayList<>();

        /*mClientType = Util.getClientTypeList(spf);
        if (mClientType.size() > 0)
            mClientType.remove(0);*/

        //temp node type disable
        /*node_type = spf.getString(AppConstants.SELECTED_NODE_TYPE_SAVE,mClientType.get(1).getClientType().toString());*/

        llPoleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ScanMacId fragment = new ScanMacId();

                    Bundle mBundle = new Bundle();
                    mBundle.putBoolean(AppConstants.ISFROMBACK, true);
                    fragment.setArguments(mBundle);

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.frm1, fragment);
                    fragmentTransaction.commit(); //
                } catch (Exception e) {

                }

            }
        });
        iv_edit_mac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle=new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "ScanSLCID");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                Log.d(AppConstants.TAG, event_name+"ScanSLCID");
                if (isfromeditui)
                    isShowPrevious = true;
                ShowDialogSlcID(getActivity(), "");
            }
        });

        iv_edit_mac.setVisibility(View.VISIBLE);
        tbTitleSkip.setVisibility(View.GONE);


        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mScannerView.setResultHandler(objResultHandler);
        mScannerView.startCamera(mCameraId);
        //mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);

        appBarMainn.setVisibility(View.VISIBLE);
        //txtTest.setVisibility(View.VISIBLE);

        /*getActivity().findViewById(R.id.appBarMainn).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.VISIBLE);*/

        Util.hideKeyboard(getActivity());

        spf.edit().putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, AppConstants.SPF_SLC_ID_SCANNER_FRAG).apply();

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        //appBarMainn.setVisibility(View.GONE);
        //txtTest.setVisibility(View.GONE);

        //getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
    }

    ZXingScannerView.ResultHandler objResultHandler;

    {
        objResultHandler = new ZXingScannerView.ResultHandler() {
            @Override
            public void handleResult(Result result) {
                ShowDialogSlcID(getActivity(), result.getText());
            }
        };
    }

    void checkMacIDSLCIDBeforeSave(String slcid) {
        //progressDialog.show();
        //node_type
        objApi.checkMacAddressSlcBeforeSave(
                user_id,
                client_id,
                spf.getString(AppConstants.SPF_TEMP_MACID, ""),
                slcid,
                "Android",
                ""
        ).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                if (response.body() != null) {
                    spf.edit().putString(AppConstants.SPF_TEMP_SLCID, slcid).apply();
                    CommonResponse response1 = response.body();
                    if (response1.getStatus().toString().equalsIgnoreCase("success")) {
                        spf.edit().putString(AppConstants.SPF_TEMP_SLCID, slcid).apply();
                        Util.addLog("Valid SLC ID : " + slcid);
                        //dialogForMessageSLCID(getActivity(), response1.getMsg().toString(), false);
                        objUtil.loadFragment(new SelectLocationPoleFragment(), getActivity());

                        if (macDialog.isShowing())
                            macDialog.dismiss();

                    } else if (response1.getStatus().toString().equalsIgnoreCase("continue")) {
                        dialogForMessageSLCID(getActivity(), response1.getMsg().toString(), false);
                    } else if (response1.getStatus().equalsIgnoreCase("error")) {
                        dialogForMessageSLCID(getActivity(), response1.getMsg().toString(), true);
                    } else {
                        Util.dialogForMessage(getActivity(), response1.getMsg());
                    }
                } else {
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.server_error));
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                Util.dialogForMessage(getActivity(), getResources().getString(R.string.server_error));
                Util.addLog("Forgot API OnFailure called: " + t.getMessage());
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });

    }

    public void dialogForMessageSLCID(Activity activity, String message, boolean isError) {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setMessage(message)
                    .setCancelable(true)
                    .setPositiveButton(activity.getResources().getText(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            if (!isError) {
                                if (macDialog.isShowing())
                                    macDialog.dismiss();
                                objUtil.loadFragment(new SelectLocationPoleFragment(), activity);
                            }
                        }
                    })
                    .setNegativeButton(activity.getResources().getText(R.string.cancle), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            /*if (!isError) {
                                objUtil.loadFragment(new SelectLocationPoleFragment(), activity);
                            }*/
                        }
                    });

            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alert.show();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    public void showDialog(final Activity activity, String msg) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View dialogView = factory.inflate(R.layout.dilog_mac_id, null);
        final AlertDialog mDialog = new AlertDialog.Builder(getActivity()).create();
        mDialog.setView(dialogView);

        final EditText etMacId = mDialog.findViewById(R.id.etMacId);
        etMacId.setText(msg);

        mDialog.findViewById(R.id.btConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMacId.getText().toString().equals("")) {
                    etMacId.setError(String.format(getResources().getString(R.string.please_enter_or_scanid), getResources().getString(R.string.mac_id)));
                } else {
                    spf.edit().putString(AppConstants.SPF_TEMP_MACID, "").apply();
                    mScannerView.resumeCameraPreview(objResultHandler);
                    mDialog.dismiss();
                }
            }
        });

        mDialog.findViewById(R.id.btConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMacId.setText("");
                mScannerView.resumeCameraPreview(objResultHandler);
                try {
                    mDialog.dismiss();
                } catch (Exception e) {

                }
            }
        });

        mDialog.show();
    }

    private void ShowDialogSlcID(Activity activity, final String result) {
        if (activity != null) {

            mScannerView.stopCamera();

            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            // Get the layout inflater
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.dilog_mac_id, null);
            TextView tvMacLBL = view.findViewById(R.id.tvMacLBL);

            if (tvMacLBL != null) {
                if (spf != null)
                    tvMacLBL.setText(getResources().getString(R.string.slc_id));
            }

            builder.setView(view);
            builder.setCancelable(false);

            macDialog = builder.create();
            macDialog.show();

            //ProgressBar progressBar=view.findViewById(R.id.progressBar);

            final EditText etSlcId = view.findViewById(R.id.etMacId);
            etSlcId.setText(result);
            etSlcId.setHint(getResources().getString(R.string.slc_id));
            etSlcId.setInputType(InputType.TYPE_CLASS_NUMBER);
            etSlcId.setKeyListener(DigitsKeyListener.getInstance("0123456789"));

            if (isShowPrevious)
                etSlcId.setText(spf.getString(AppConstants.SPF_TEMP_SLCID, ""));
            else
                etSlcId.setText(result);

            //etSlcId.setText(spf.getString(AppConstants.SPF_TEMP_SLCID,result.toString()));

            final Editable etext = etSlcId.getText();

            Selection.setSelection(etext, etSlcId.getText().length());

            Button btConfirm = view.findViewById(R.id.btConfirm);
            Button btnCancle = view.findViewById(R.id.btnCancle);
            //etSlcId.setText("18741");
            btConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    confirmSLCIDCall(etSlcId);
                }
            });

            btnCancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    macDialog.dismiss();
                    //mScannerView.resumeCameraPreview(objResultHandler);
                    mScannerView.setResultHandler(objResultHandler);
                    mScannerView.startCamera(mCameraId);
                    mScannerView.setAutoFocus(mAutoFocus);
                    isShowPrevious = false;
                    isfromeditui = false;
                }
            });

            etSlcId.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // TODO do something
                        handled = true;

                        confirmSLCIDCall(etSlcId);
                    }
                    return handled;
                }
            });
        }
    }

    void confirmSLCIDCall(EditText editText) {
        if (editText.getText().toString().trim().equals("")) {
            editText.setError(String.format(getResources().getString(R.string.please_enter_or_scanid), getResources().getString(R.string.slc_id)));
        } else {
            if (Util.isInternetAvailable(getActivity()))
                checkSLCIDAPICall(editText.getText().toString().trim());
            else
                Util.dialogForMessage(getActivity(), getResources().getString(R.string.internent_connection));
        }
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

    void checkSLCIDAPICall(final String id) {
        progressDialog.show();
        objApi.checkSLCId(id, client_id, user_id).enqueue(new Callback<CommonResponse2>() {
            @Override
            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {

             /*   if (progressDialog.isShowing())
                    progressDialog.dismiss();
                */
                if (response.body() != null) {
                    CommonResponse2 response1 = response.body();
                    if (response1.getStatus().toString().equals("success")) {
                        mScannerView.resumeCameraPreview(objResultHandler);
                        checkMacIDSLCIDBeforeSave(id.toString());

                    } else if (response1.getStatus().equalsIgnoreCase("logout")) {

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                        spf.edit().putBoolean(AppConstants.ISLOGGEDIN, false).apply();
                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, response1.getSLCID()).apply();

                        new Util().commonLoginDialog(getActivity(), getResources().getString(R.string.slc_id));
                        Toast.makeText(getActivity(), response1.getMsg(), Toast.LENGTH_SHORT).show();
                        Util.addLog("SLC ID UI" + response1.getMsg());
                        Log.i(AppConstants.TAG, response1.getMsg());
                    } else {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Util.dialogForMessage(getActivity(), response1.getMsg());
                    }
                } else {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<CommonResponse2> call, Throwable t) {
                if (getActivity() != null) {
                    Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }
        });
    }
}