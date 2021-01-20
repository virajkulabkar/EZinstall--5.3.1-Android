package com.CL.slcscanner.fragments;

import android.Manifest;
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
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.Spanned;
import android.text.TextWatcher;
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

import com.CL.slcscanner.Activities.SecurityCode;
import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.CommonResponse;
import com.CL.slcscanner.Pojo.CommonResponse2;
import com.CL.slcscanner.Pojo.Login.Datum;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.DBHelper;
import com.CL.slcscanner.Utils.ScannerCallBack;
import com.CL.slcscanner.Utils.Util;
import com.google.android.material.appbar.AppBarLayout;
import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vrajesh on 2/24/2018.
 */

public class ScanMacId extends Fragment {

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
    LinearLayout llPoleBack;
    Bundle mBundle;
    boolean isfromeditui = false;
    boolean isShowPrevious = false;

    TextView tbTitleLine1;
    AppBarLayout appBarMainn;
    TextView txtTest;
    ImageView iv_edit_mac;

    Activity activity;

    Util objUtil;
    LinearLayout llNodeType;
    TextView tvNodeType;
    FrameLayout frmNode;
    ArrayList<com.CL.slcscanner.Pojo.Login.Datum> mClientType;
    String nodeType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle state) {
        activity = getActivity();
        mBundle = getArguments();
        if (mBundle != null) {
            isfromeditui = mBundle.getBoolean(AppConstants.ISFROMBACK);
        }
        objUtil = new Util();
        appBarMainn = getActivity().findViewById(R.id.appBarMainn);
        appBarMainn.setVisibility(View.VISIBLE);

        tbTitleSkip = getActivity().findViewById(R.id.tbTitleSkip);

        iv_edit_mac = getActivity().findViewById(R.id.iv_edit_mac);
        tbTitleSkip.setVisibility(View.GONE);

        iv_edit_mac.setVisibility(View.VISIBLE);

        llPoleBack = getActivity().findViewById(R.id.llPoleBack);
        llPoleBack.setVisibility(View.GONE);

        frmNode = getActivity().findViewById(R.id.frmNode);
        llNodeType = getActivity().findViewById(R.id.llNodeType);
        tvNodeType = getActivity().findViewById(R.id.tvNodeType);
        llNodeType.setVisibility(View.GONE);

        txtTest = getActivity().findViewById(R.id.txtTest);
        txtTest.setVisibility(View.VISIBLE);

        tbTitleLine1 = getActivity().findViewById(R.id.tbTitleLine1);
        objApi = new SLCScanner().networkCall();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.loading));
        tbTitleLine1.setText(getResources().getString(R.string.scan_uid));
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
        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        client_id = spf.getString(AppConstants.CLIENT_ID, "");
        user_id = spf.getString(AppConstants.USER_ID, "");

        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.frm1);
        Log.i(AppConstants.TAG, "isadded:" + isAdded() + "is visible" + isVisible());
        if (currentFragment instanceof ScanMacId) {
            if (!objUtil.checkCameraPermmsion(getActivity())) {
                Util.dialogForMessage(getActivity(), getResources().getString(R.string.camerra_permission));
            }
        }

        mClientType = new ArrayList<>();

      /*  mClientType = Util.getClientTypeList(spf);
        if (mClientType.size() > 0)
            mClientType.remove(0);*/

        //by default selected index 1

        frmNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objUtil.ShowNodeTypeDialog(mClientType, getResources().getString(R.string.strNodeTypTitle), getActivity(), spf, AppConstants.SCAN_MAC_UI, new Util.ClickNodeType() {
                    @Override
                    public void setOnClickNodeType(Datum objOnClickLampType, int position) {
                        //apiCall(objSearchView.getQuery().toString(), false, 1);
                        nodeType = objOnClickLampType.getClientType();
                        tvNodeType.setText(nodeType);
                    }
                });
            }
        });

        //Defualt
        /*
        //temp node type disable
        nodeType = spf.getString(AppConstants.SELECTED_NODE_TYPE_SAVE, mClientType.get(1).getClientType().toString());
        spf.edit().putString(AppConstants.SELECTED_NODE_TYPE_SAVE, nodeType).apply();
        tvNodeType.setText(nodeType);*/

        tbTitleSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdded() && activity != null) {
                    if (isfromeditui)
                        isShowPrevious = true;
                    ShowDialogMacID(getActivity(), "");
                }
            }
        });

        iv_edit_mac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isAdded() && activity != null) {
                    if (isfromeditui)
                        isShowPrevious = true;
                    ShowDialogMacID(getActivity(), "");
                }
            }
        });

        tbTitleSkip.setVisibility(View.GONE);
        //((LinearLayout) linearLayout).addView(valueTV);

        SharedPreferences.Editor edit = spf.edit();
        edit.remove(AppConstants.SELECTED_NODE_TYPE_SAVE);
        edit.remove(AppConstants.SELECTED_NODE_TYPE_INDEX_SAVE);
        edit.apply();
        return mScannerView;
    }


    public boolean getIsVisible() {
        if (getParentFragment() != null && getParentFragment() instanceof ScanMacId) {
            return isVisible() && ((ScanMacId) getParentFragment()).getIsVisible();
        } else {
            return isVisible();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mScannerView.setResultHandler(objResultHandler);
        mScannerView.startCamera(mCameraId);
        //mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);//mAutoFocus

        appBarMainn.setVisibility(View.VISIBLE);
        txtTest.setVisibility(View.VISIBLE);

        //getActivity().findViewById(R.id.txtTest).setVisibility(View.VISIBLE);

        if (getActivity() != null)
            Util.hideKeyboard(getActivity());

        spf.edit().putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, AppConstants.SPF_SCANNER_FRAG).apply();

        //reset value
        SharedPreferences.Editor edt = spf.edit();
        edt.putString(AppConstants.SPF_TEMP_SLCID, "");

        if (!isfromeditui)
            edt.putString(AppConstants.SPF_TEMP_MACID, "");

        edt.putString(AppConstants.SPF_TEMP_POLE_ID, "");
        edt.putString(AppConstants.SPF_DRAG_LONGITUDE, "0.0");
        edt.putString(AppConstants.SPF_DRAG_LATTITUDE, "0.0");
        edt.putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, "");
        edt.putString(AppConstants.ADDRESS, "");
        edt.putString(AppConstants.SPF_LOGOUT_SLCID, "");
        edt.apply();

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        //txtTest.setVisibility(View.GONE);
        //appBarMainn.setVisibility(View.GONE);
        //getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        //getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
    }

    ZXingScannerView.ResultHandler objResultHandler;

    {
        objResultHandler = new ZXingScannerView.ResultHandler() {
            @Override
            public void handleResult(Result result) {
                Log.i("***", result.getText());
                ShowDialogMacID(getActivity(), result.getText());
            }
        };
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    public void showDialog(Activity activity, String msg) {
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
                    etMacId.setError(String.format(getString(R.string.please_enter_or_scanid), getString(R.string.mac_id)));
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
                mDialog.dismiss();
            }
        });

        mDialog.show();
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

    private void ShowDialogMacID(Activity activity, final String result) {
        if (activity != null) {
            try {

                mScannerView.stopCamera();
                String localeStr = Util.getDeviceLocale(spf);

                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                // Get the layout inflater
                LayoutInflater inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.dilog_mac_id, null);
                TextView tvMacLBL = view.findViewById(R.id.tvMacLBL);

                String strMacLbl = spf.getString(AppConstants.MACID_LABLE, "");

                if (tvMacLBL != null) {
                    if (spf != null)
                        tvMacLBL.setText(strMacLbl);
                }

                //ProgressBar progressBar=view.findViewById(R.id.progressBar);

                final EditText etMacId = view.findViewById(R.id.etMacId);
                String strText = spf.getString(AppConstants.SPF_TEMP_MACID, "");
                etMacId.setInputType(InputType.TYPE_CLASS_TEXT);

                etMacId.setFilters(new InputFilter[]{filter});


                if (isShowPrevious)
                    etMacId.setText(strText);
                else
                    etMacId.setText(result);

                String strHint = spf.getString(AppConstants.MACID_PH, "");
                etMacId.setHint(strHint);
                //etMacId.setText(spf.getString(AppConstants.SPF_TEMP_MACID,result.toString()));

                final Editable etext = etMacId.getText();
                Selection.setSelection(etext, etMacId.getText().length());

                Button btConfirm = view.findViewById(R.id.btConfirm);
                Button btnCancle = view.findViewById(R.id.btnCancle);

                btConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmMacIDCall(etMacId);
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
                        isfromeditui = false;
                        isShowPrevious = false;
                    }
                });

                //etMacId.setText("1234567897125482");
                etMacId.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        boolean handled = false;
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            // TODO do something
                            handled = true;
                            confirmMacIDCall(etMacId);
                        }
                        return handled;
                    }
                });

                builder.setView(view);
                builder.setCancelable(false);
                macDialog = builder.create();
                macDialog.show();


            } catch (Exception e) {

            }
        }
    }

    void confirmMacIDCall(EditText editText) {
        if (editText.getText().toString().trim().equals("")) {
            //editText.setError("Please enter / Scan  MACID");
            editText.setError(String.format(getResources().getString(R.string.please_enter_or_scanid), spf.getString(AppConstants.MACID_LABLE, "")));
        } else {
            if (Util.isInternetAvailable(getActivity())) {
                checkInternalUniqueMacAddressAPI(editText.getText().toString().trim());
            }
            //checkMACIDAPICall(editText.getText().toString().trim());
            else
                Util.dialogForMessage(getActivity(), getResources().getString(R.string.internent_connection));
        }
    }

  /*  private void loadFragment(Fragment fragment) {
        try {
            // create a FragmentManager
            FragmentManager fm = getFragmentManager();
            // create a FragmentTransaction to begin the transaction and replace the Fragment
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            // replace the FrameLayout with new Fragment
            fragmentTransaction.replace(R.id.frm1, fragment);
            fragmentTransaction.commit(); // save the changes
        }catch (Exception e){}
    }*/

    void checkMACIDAPICall(final String id) {
        //progressDialog.show();
//nodeType
        objApi.checkMACId(id, client_id, user_id, "").enqueue(new Callback<CommonResponse2>() {
            @Override
            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {
                try {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    if (response.body() != null) {
                        CommonResponse2 response1 = response.body();
                        if (response1.getStatus().equals("success")) {
                            spf.edit().putString(AppConstants.SPF_TEMP_MACID, id.toString()).apply();
                            mScannerView.resumeCameraPreview(objResultHandler);

                            if (isAdded() && activity != null) {
                                if (macDialog.isShowing())
                                    macDialog.dismiss();
                            }

                            objUtil.loadFragment(new ScanSLCId(), getActivity());
                            //Toast.makeText(getActivity(),""+response1.getAddress().toString(),Toast.LENGTH_LONG).show();
                            Util.addLog("Valid MAC ID: " + id);
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
                        Util.dialogForMessage(activity, activity.getResources().getString(R.string.server_error));
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<CommonResponse2> call, Throwable t) {
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }

    void checkInternalUniqueMacAddressAPI(String macId) {
        progressDialog.show();
        objApi.checkInternalUniqueMacAddressAPI(
                user_id,
                client_id,
                macId,
                "Android"
        ).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {

                if (response.body() != null) {
                    CommonResponse response1 = response.body();

                    if (response1.getStatus().equalsIgnoreCase("continue")) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(response1.getMsg().toString())
                                .setCancelable(false);

                        builder.setPositiveButton(getResources().getString(R.string.yes_camel_case), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                if (response1.getMac_address_type().equalsIgnoreCase(AppConstants.Internal)) {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();

                                    spf.edit().putString(AppConstants.SPF_TEMP_SLCID, response1.getSlc_id()).apply();
                                    spf.edit().putString(AppConstants.SPF_TEMP_MACID, macId.toString()).apply();
                                    spf.edit().putString(AppConstants.SPF_TEMP_SLC_STATUS, AppConstants.Internal);
                                    mScannerView.resumeCameraPreview(objResultHandler);
                                    if (isAdded() && activity != null) {
                                        if (macDialog.isShowing())
                                            macDialog.dismiss();
                                    }
                                    Bundle objBundle = new Bundle();
                                    objBundle.putString("From", "MACID");

                                    SelectLocationPoleFragment fragment = new SelectLocationPoleFragment();
                                    fragment.setArguments(objBundle);

                                    objUtil.loadFragment(fragment, getActivity());
                                } else if (response1.getMac_address_type().equalsIgnoreCase(AppConstants.EXTERNAL))
                                    checkMACIDAPICall(macId);

                            }
                        });

                        builder.setNegativeButton(getResources().getString(R.string.no_camel_case), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }
                        });
                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manuallyc
                        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        alert.show();

                    } else if (response1.getStatus().equalsIgnoreCase("success")) {
                        if (response1.getMac_address_type().equalsIgnoreCase("internal")) {
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();

                            spf.edit().putString(AppConstants.SPF_TEMP_SLCID, response1.getSlc_id()).apply();
                            spf.edit().putString(AppConstants.SPF_TEMP_MACID, macId.toString()).apply();
                            spf.edit().putString(AppConstants.SPF_TEMP_SLC_STATUS, AppConstants.Internal);
                            mScannerView.resumeCameraPreview(objResultHandler);
                            if (isAdded() && activity != null) {
                                if (macDialog.isShowing())
                                    macDialog.dismiss();
                            }

                            Bundle objBundle = new Bundle();
                            objBundle.putString("From", "MACID");

                            SelectLocationPoleFragment fragment = new SelectLocationPoleFragment();
                            fragment.setArguments(objBundle);

                            objUtil.loadFragment(fragment, getActivity());
                        } else if (response1.getMac_address_type().equalsIgnoreCase(AppConstants.EXTERNAL))
                            checkMACIDAPICall(macId);
                    }

                    //dialogForMessageSLCID(getActivity(), response1.getMsg().toString(), false);
                    //if (macDialog.isShowing())
                    // macDialog.dismiss();

                } else {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

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

    void dialog(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg)
                .setCancelable(true)
                .setTitle(getResources().getString(R.string.logout))
                .setPositiveButton(getResources().getString(R.string.continue_str), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.cancle), new DialogInterface.OnClickListener() {
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

}