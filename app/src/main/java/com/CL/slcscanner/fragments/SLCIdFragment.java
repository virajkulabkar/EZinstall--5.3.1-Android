package com.CL.slcscanner.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.speech.RecognizerIntent;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.CL.slcscanner.Activities.MainActivity;
import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.CommonResponse;
import com.CL.slcscanner.Pojo.CommonResponse2;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.Util;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by vrajesh on 2/24/2018.
 */

public class SLCIdFragment extends Fragment {

    @BindView(R.id.ivSLCIdBg)
    ImageView ivSLCIdBg;

    @BindView(R.id.btnConfirmSlcId)
    Button btnConfirmSlcId;

    @BindView(R.id.btnCancleSlcId)
    Button btnCancleSlcId;

    @BindView(R.id.edtSLCId)
    EditText edtSLCId;

    @BindView(R.id.btnSLCBack)
    ImageView btnSLCBack;

    @BindView(R.id.llSLCBack)
    LinearLayout llSLCBack;

    @BindView(R.id.ivSpeech)
    ImageView ivSpeech;

    @BindView(R.id.cbCopyPole)
    CheckBox cbCopyPole;

    View view;

    SharedPreferences spf;
    API objApi;

    ProgressDialog dialog;
    String client_id, user_id, mac_address;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    Util objUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_slc_id, null);
        init();
        return view;
    }

    void init() {
        ButterKnife.bind(this, view);

        getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
        getActivity().findViewById(R.id.llNodeType).setVisibility(View.GONE);

        Glide.with(this).load(R.drawable.bg_1_1242_2208).into(ivSLCIdBg);

        //getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        //getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);

        //edtSLCId.setText("123456");
        objApi = new SLCScanner().networkCall();
        objUtil = new Util();
        //Util.showKeyobard(getActivity());

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getResources().getString(R.string.loading));
        dialog.setCancelable(false);

        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        spf.edit().putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, AppConstants.SPF_SLCID_FRAG).apply();
        client_id = spf.getString(AppConstants.CLIENT_ID, "");
        user_id = spf.getString(AppConstants.USER_ID, "");
        mac_address = spf.getString(AppConstants.SPF_TEMP_MACID, "");
        edtSLCId.setText(spf.getString(AppConstants.SPF_TEMP_SLCID, ""));

        btnConfirmSlcId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSLCID();
            }
        });

        //btnConfirmSlcId.setOnTouchListener(Util.colorFilter());
        //btnCancleSlcId.setOnTouchListener(Util.colorFilter());

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtSLCId, InputMethodManager.SHOW_IMPLICIT);

        edtSLCId.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // TODO do something
                    handled = true;
                    confirmSLCID();
                }
                return handled;
            }
        });

        btnCancleSlcId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSLCId.setText("");
                spf.edit().putString(AppConstants.SPF_TEMP_SLCID, "").apply();
                cbCopyPole.setChecked(false);
            }
        });

        llSLCBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ScanMacId fragment = new ScanMacId();

                Bundle mBundle = new Bundle();
                mBundle.putBoolean(AppConstants.ISFROMBACK, true);
                fragment.setArguments(mBundle);

                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.frm1, fragment);
                fragmentTransaction.commit(); // save the changes

            }
        });

        if (getActivity() != null) {
            ((MainActivity) getActivity()).selectPole(false);
            ((MainActivity) getActivity()).selectScan(true);
            ((MainActivity) getActivity()).selectSetting(false);
            ((MainActivity) getActivity()).selectMap(false);
        }

        edtSLCId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                spf.edit().putString(AppConstants.SPF_TEMP_SLCID, s.toString()).apply();
            }
        });

        ivSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        cbCopyPole.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    void confirmSLCID() {
        if (!edtSLCId.getText().toString().trim().equals("")) {
            //spf.edit().putString(AppConstants.SPF_TEMP_SLCID,edtSLCId.getText().toString()).apply();
            //loadFragment(new SelectLocationPoleFragment());
            if (Util.isInternetAvailable(getActivity()))
                checkSLCIDAPICall(edtSLCId.getText().toString().trim());
            else
                Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
        } else {
            Util.dialogForMessage(getActivity(), getResources().getString(R.string.enter_slc_id));
        }
    }

    void checkMacIDSLCIDBeforeSave(String slcid) {

        String nodeType=spf.getString(AppConstants.SELECTED_NODE_TYPE_SAVE,"");

        dialog.show();
        objApi.checkMacAddressSlcBeforeSave(
                user_id,
                client_id,
                mac_address,
                edtSLCId.getText().toString(),
                "Android",
                ""
        ).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    CommonResponse response1 = response.body();
                    if (response1.getStatus().toString().equals("success") || response1.getStatus().toString().equalsIgnoreCase("continue")) {
                        spf.edit().putString(AppConstants.SPF_TEMP_SLCID, slcid).apply();
                        Util.addLog("Valid SLC ID : " + edtSLCId.getText());
                        //dialogForMessageSLCID(getActivity(),response1.getMsg().toString(), false);
                    } else if (response1.getStatus().equalsIgnoreCase("error")) {
                       //dialogForMessageSLCID(getActivity(), response1.getMsg().toString(), true);
                    } else {
                        Util.dialogForMessage(getActivity(), response1.getMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {

            }
        });

    }

    private void loadFragment(Fragment fragment) {
        // create a FragmentManagers
        FragmentManager fm = getFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frm1, fragment);
        fragmentTransaction.commit(); // save the changes
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {

            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String actualOutput = result.get(0).replace(" ", "");
                    if (android.text.TextUtils.isDigitsOnly(actualOutput)) {
                        edtSLCId.setText(actualOutput.trim());
                        edtSLCId.setSelection(edtSLCId.length());
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.speech_prompt), Toast.LENGTH_SHORT).show();
                        edtSLCId.setText("");
                    }
                }
                break;
            }
        }

    }

    void checkSLCIDAPICall(final String id) {
        dialog.show();
        Util.hideKeyboard(getActivity());
        objApi.checkSLCId(id, client_id, user_id).enqueue(new Callback<CommonResponse2>() {
            @Override
            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    CommonResponse2 response1 = response.body();
                    if (response1.getStatus().toString().equals("success")) {

                        checkMacIDSLCIDBeforeSave(edtSLCId.getText().toString());

                        //spf.edit().putString(AppConstants.SPF_TEMP_SLCID, id).apply();
                        //Util.addLog("Valid SLC ID : " + edtSLCId.getText());

                        //Toast.makeText(getActivity(),""+response1.getAddress().toString(),Toast.LENGTH_LONG).show();
                        //objUtil.loadFragment(new SelectLocationPoleFragment(), getActivity());

                    } else if (response1.getStatus().equalsIgnoreCase("logout")) {

                        spf.edit().putBoolean(AppConstants.ISLOGGEDIN, false).apply();
                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, response1.getSLCID()).apply();

                        new Util().commonLoginDialog(getActivity(), "SLC ID ");

                        Toast.makeText(getActivity(), response1.getMsg(), Toast.LENGTH_SHORT).show();
                        Util.addLog("SLC ID UI" + response1.getMsg());
                        Log.i(AppConstants.TAG, response1.getMsg());
                    } else {
                        Util.dialogForMessage(getActivity(), response1.getMsg());
                    }
                }

            }

            @Override
            public void onFailure(Call<CommonResponse2> call, Throwable t) {
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                dialog.dismiss();
            }
        });
    }
}