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
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.Spanned;
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
import com.CL.slcscanner.Pojo.CommonResponse;
import com.CL.slcscanner.Pojo.CommonResponse2;
import com.CL.slcscanner.Pojo.Login.Datum;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.ScannerCallBack;
import com.CL.slcscanner.Utils.Util;
import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vrajesh on 2/24/2018.
 */

public class ScannerForEdit extends Fragment {

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
    ImageView iv_edit_mac;
    LinearLayout llPoleBack;
    Bundle mBundle;

    Double lattitude;
    Double longitude;
    String poleId, ID, SLCID, MacId;
    TextView tvPoleEditBack;
    boolean isForSLC = false;
    TextView tbTitleLine1;

    Util objUtil;
    LinearLayout llNodeType;
    TextView tvNodeType;
    FrameLayout frmNode;
    ArrayList<com.CL.slcscanner.Pojo.Login.Datum> mClientType;
    String nodeType;

    String node_type_from_edit;
    String node_type_predefine="";
    int node_type_index_predefine=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle state) {
        mBundle = getArguments();
        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        if (mBundle != null) {
            ID = mBundle.getString("ID");
            SLCID = mBundle.getString("slcID");
            poleId = mBundle.getString("poleId");
            MacId = mBundle.getString("MacId");
            longitude = mBundle.getDouble("Long");
            lattitude = mBundle.getDouble("Lat");
            isForSLC = mBundle.getBoolean("isForSLC", false);
            node_type_from_edit = mBundle.getString(AppConstants.NODE_TYPE_SCANNER_EDIT_MAC, "");

            Log.i("@@@","scanner:  macid: "+MacId+" SLC id: "+SLCID);

            if (!isForSLC) {
                node_type_predefine = mBundle.getString(AppConstants.SELECTED_NODE_TYPE_EDIT_SLC, "");
                node_type_index_predefine = mBundle.getInt(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_SLC, 0);
            }

        }

        objUtil = new Util();

        tbTitleSkip = getActivity().findViewById(R.id.tbTitleSkip);
        getActivity().findViewById(R.id.appBarMainn).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.VISIBLE);

        tbTitleLine1 = getActivity().findViewById(R.id.tbTitleLine1);

        iv_edit_mac = getActivity().findViewById(R.id.iv_edit_mac);

        llPoleBack = getActivity().findViewById(R.id.llPoleBack);
        tvPoleEditBack = getActivity().findViewById(R.id.tvPoleEditBack);


        frmNode = getActivity().findViewById(R.id.frmNode);
        llNodeType = getActivity().findViewById(R.id.llNodeType);
        tvNodeType = getActivity().findViewById(R.id.tvNodeType);

        if (isForSLC) {
            tbTitleLine1.setText(getResources().getString(R.string.scan_slc_id));
            llNodeType.setVisibility(View.GONE);
        } else {
            llNodeType.setVisibility(View.VISIBLE);
            String title = getResources().getString(R.string.scan);
            tbTitleLine1.setText(title + " " + spf.getString(AppConstants.MACID_LABLE, ""));
        }
        llNodeType.setVisibility(View.GONE);

        //tvPoleEditBack.setText(getResources().getString(R.string.pole_details));
        tvPoleEditBack.setText(getResources().getString(R.string.back));

        llPoleBack.setVisibility(View.VISIBLE);
        objApi = new SLCScanner().networkCall();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.loading));

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

        tbTitleSkip.setVisibility(View.GONE);
        iv_edit_mac.setVisibility(View.VISIBLE);
        llPoleBack.setVisibility(View.VISIBLE);
        tvPoleEditBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(MacId, SLCID, true, "",0);
            }
        });

        llPoleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(MacId, SLCID, true, "",0);
            }
        });

        iv_edit_mac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isForSLC)
                    ShowDialogMacSlcID(getActivity(), SLCID);
                else
                    ShowDialogMacSlcID(getActivity(), MacId);
            }
        });

        mClientType = new ArrayList<>();

       /* mClientType = Util.getClientTypeList(spf);
        if (mClientType.size() > 0)
            mClientType.remove(0);*/

        tvNodeType.setText(node_type_predefine);

        frmNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objUtil.ShowNodeTypeDialog(mClientType, getResources().getString(R.string.strNodeTypTitle), getActivity(), spf, AppConstants.EDIT_SLC_UI, new Util.ClickNodeType() {
                    @Override
                    public void setOnClickNodeType(Datum objOnClickLampType,int position) {
                        node_type_predefine= objOnClickLampType.getClientType();
                        node_type_index_predefine=position;
                        tvNodeType.setText(node_type_predefine);
                    }
                });
            }
        });

        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.frm1);
        if (currentFragment instanceof ScannerForEdit) {
            if (!objUtil.checkCameraPermmsion(getActivity())) {
                Util.dialogForMessage(getActivity(), getResources().getString(R.string.camerra_permission));
            }
        }

        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mScannerView.setResultHandler(objResultHandler);
        mScannerView.startCamera(mCameraId);
        //mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);

        getActivity().findViewById(R.id.appBarMainn).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.VISIBLE);

        Util.hideKeyboard(getActivity());

        spf.edit().putString(AppConstants.SPF_POLE_CURRENT_FRAG, AppConstants.SPF_SCANNER_EDIT_FRAG).apply();

    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        //getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        //getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
    }

    ZXingScannerView.ResultHandler objResultHandler;

    {
        objResultHandler = new ZXingScannerView.ResultHandler() {
            @Override
            public void handleResult(Result result) {
                ShowDialogMacSlcID(getActivity(), result.getText());
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

    private void ShowDialogMacSlcID(Activity activity, final String result) {
        mScannerView.stopCamera();

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dilog_mac_id, null);
        TextView tvMacLBL = view.findViewById(R.id.tvMacLBL);


        builder.setView(view);
        builder.setCancelable(false);

        macDialog = builder.create();
        macDialog.show();

        //ProgressBar progressBar=view.findViewById(R.id.progressBar);

        final EditText etMacId = view.findViewById(R.id.etMacId);
        etMacId.setText(result);

        if (!isForSLC) {
            etMacId.setHint(spf.getString(AppConstants.MACID_PH, ""));
            tvMacLBL.setText(spf.getString(AppConstants.MACID_LABLE, ""));
            etMacId.setInputType(InputType.TYPE_CLASS_TEXT);
            etMacId.setFilters(new InputFilter[]{filter});
        } else {
            etMacId.setHint(getResources().getString(R.string.slc_id));
            tvMacLBL.setText(getResources().getString(R.string.slc_id));
            etMacId.setInputType(InputType.TYPE_CLASS_NUMBER);
            //etMacId.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        }

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
            }
        });

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
    }

    void confirmMacIDCall(EditText editText) {
        if (editText.getText().toString().trim().equals("")) {
            if (isForSLC)
                editText.setError(String.format(getResources().getString(R.string.please_enter_or_scanid), getResources().getString(R.string.slc_id)));
            else
                editText.setError(String.format(getResources().getString(R.string.please_enter_or_scanid), getResources().getString(R.string.mac_id)));
        } else {

            if (Util.isInternetAvailable(getActivity())) {
                if (isForSLC) {
                    checkSLCIDAPICall(editText.getText().toString().trim());
                } else {
                    checkInternalUniqueMacAddressAPI(editText.getText().toString().trim());
                }
            } else {
                Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
            }
        }
    }


    void checkInternalUniqueMacAddressAPI(String macId) {
        progressDialog.show();
        objApi.checkInternalUniqueMacAddressEditAPI(
                user_id,
                client_id,
                macId,
                "Android"
        ).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {

                if (response.body() != null) {
                    CommonResponse response1 = response.body();

                    if (response1.getStatus().equalsIgnoreCase(AppConstants.Internal)) {
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(response1.getMsg().toString())
                                .setCancelable(false);

                           /* builder.setPositiveButton(getResources().getString(R.string.yes_camel_case), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    //updateMacId(macId, response1.getSlc_id(), false);

                                    spf.edit().putString(AppConstants.SPF_TEMP_MACID, response1.getSlc_id()).apply();
                                    mScannerView.resumeCameraPreview(objResultHandler);
                                    if (macDialog.isShowing())
                                        macDialog.dismiss();

                                    loadFragment(macId, response1.getSlc_id(),false);
                                }
                            });*/

                        builder.setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //updateMacId(macId, SLCID, false);

                                spf.edit().putString(AppConstants.SPF_TEMP_MACID, macId.toString()).apply();
                                mScannerView.resumeCameraPreview(objResultHandler);
                                if (macDialog.isShowing())
                                    macDialog.dismiss();

                                loadFragment(macId, SLCID, false, node_type_predefine,node_type_index_predefine);
                            }
                        });
                        //Creating dialog box
                        AlertDialog alert = builder.create();
                        //Setting the title manuallyc
                        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        alert.show();

                    } else if (response1.getStatus().equalsIgnoreCase(AppConstants.EXTERNAL)) {

                        checkMACIDAPICall(macId, true, response1.getSlc_id(), node_type_predefine);
                    }
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

    private void loadFragment(String macId, String slcId, boolean isClickable, String nodeType,int index) {
        // create a FragmentManager

        if (getActivity() != null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            PoleDataEditFragment fragment = new PoleDataEditFragment();
            Bundle bundle = new Bundle();

            Log.i("@@@","macid: "+macId+" SLC id: "+slcId);

            bundle.putBoolean(AppConstants.IsFromScannerEdit, true);
            bundle.putBoolean("isNewData", false);
            bundle.putBoolean("IS_FROM_MAP", false);
            bundle.putString("ID", ID);
            bundle.putString("slcID", slcId.toString());
            bundle.putString("poleId", poleId);
            bundle.putDouble("Lat", lattitude);
            bundle.putDouble("Long", longitude);
            bundle.putString("MacId", macId.toString());
            bundle.putBoolean(AppConstants.isfromNote, false);
            bundle.putBoolean("isSLCIDClickable", isClickable);
            //bundle.putBoolean(AppConstants.isFromScannerForEdit, true);

            /*if (nodeType.equals("")) {
                bundle.putBoolean(AppConstants.isFromScannerForEdit, false);
            } else {
                bundle.putString(AppConstants.SELECTED_NODE_TYPE_EDIT_SLC, nodeType);
                bundle.putInt(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_SLC,index);
            }*/

            /*spf.edit().remove(AppConstants.SELECTED_NODE_TYPE_EDIT_MAC).apply();
            spf.edit().remove(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_MAC).apply();*/

            fragment.setArguments(bundle);

            //Fragment fm1 = fm.findFragmentByTag(AppConstants.TAG_SCANER_EDIT);
        /*if(fm1==null)
            fragmentTransaction.replace(R.id.frm1, fragment);
        else
            fragmentTransaction.replace(R.id.frm1, fm1);*/

            fragmentTransaction.replace(R.id.frm1, fragment);
            fragmentTransaction.commit(); // save the changes
        }
    }

    void checkMACIDAPICall(final String macId, boolean isClickable, final String slc_id, String nodeType) {
        //progressDialog.show();
//nodeType
        objApi.checkMACId(macId, client_id, user_id,"").enqueue(new Callback<CommonResponse2>() {
            @Override
            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

                if (response.body() != null) {
                    CommonResponse2 response1 = response.body();
                    if (response1.getStatus().toString().equals("success")) {
                        spf.edit().putString(AppConstants.SPF_TEMP_MACID, macId.toString()).apply();
                        mScannerView.resumeCameraPreview(objResultHandler);
                        if (macDialog.isShowing())
                            macDialog.dismiss();

                        loadFragment(macId, slc_id, isClickable, nodeType,node_type_index_predefine);
                        Util.addLog("Valid MAC ID: " + macId);
                    } else if (response1.getStatus().equalsIgnoreCase("logout")) {

                        spf.edit().putBoolean(AppConstants.ISLOGGEDIN, false).apply();
                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, response1.getSLCID()).apply();

                        new Util().commonLoginDialog(getActivity(), getResources().getString(R.string.mac_address));

                        Toast.makeText(getActivity(), response1.getMsg(), Toast.LENGTH_SHORT).show();
                        Util.addLog("MAC ADDRESS UI" + response1.getMsg());
                        Log.i(AppConstants.TAG, response1.getMsg());
                    } else {
                        Util.dialogForMessage(getActivity(), response1.getMsg());
                    }
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

    void checkSLCIDAPICall(final String id) {
        progressDialog.show();
        objApi.checkSLCId(id, client_id, user_id).enqueue(new Callback<CommonResponse2>() {
            @Override
            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                if (response.body() != null) {
                    CommonResponse2 response1 = response.body();
                    if (response1.getStatus().toString().equals("success")) {
                        spf.edit().putString(AppConstants.SPF_TEMP_SLCID, id.toString()).apply();
                        mScannerView.resumeCameraPreview(objResultHandler);
                        if (macDialog.isShowing())
                            macDialog.dismiss();
                        loadFragment(MacId, id, true, "",0);
                        Util.addLog("Valid SLC ID: " + id);
                    } else if (response1.getStatus().equalsIgnoreCase("logout")) {

                        spf.edit().putBoolean(AppConstants.ISLOGGEDIN, false).apply();
                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, response1.getSLCID()).apply();

                        new Util().commonLoginDialog(getActivity(), "MAC ADDRESS ");

                        Toast.makeText(getActivity(), response1.getMsg(), Toast.LENGTH_SHORT).show();
                        Util.addLog("MAC ADDRESS UI" + response1.getMsg());
                        Log.i(AppConstants.TAG, response1.getMsg());
                    } else {
                        Util.dialogForMessage(getActivity(), response1.getMsg());
                    }
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
}