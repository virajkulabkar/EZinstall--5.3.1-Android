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
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;

import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.CL.slcscanner.Activities.LoginActivity;
import com.CL.slcscanner.Activities.MainActivity;
import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.ClientAssets.ClientAssestMaster;
import com.CL.slcscanner.Pojo.ClientAssets.Datum;
import com.CL.slcscanner.Pojo.CommonResponse;
import com.CL.slcscanner.Pojo.CommonResponse2;
import com.CL.slcscanner.Pojo.Login.LoginResponse;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.Util;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.android.segmented.SegmentedGroup;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.CL.slcscanner.Utils.Util.getRequestBody;

/**
 * Created by vrajesh on 4/30/2018.
 */

public class AddressFragement extends Fragment implements View.OnClickListener {

    View v;
    Double lattitude, longgitude;
    String address;

    @BindView(R.id.llSLCBack)
    LinearLayout llSLCBack;

    @BindView(R.id.edtAddress)
    EditText edtAddress;

    @BindView(R.id.btnCancleAddress)
    Button btnCancleAddress;
    @BindView(R.id.btnokAddress)
    Button btnokAddress;

    @BindView(R.id.llCopyPoleData)
    LinearLayout llCopyPoleData;

    @BindView(R.id.btnYes)
    RadioButton btnYes;

    @BindView(R.id.btnNo)
    RadioButton btnNo;

    @BindView(R.id.tvCopyText)
    TextView textView2;

    @BindView(R.id.segmentCopyFromPrevious)
    SegmentedGroup segmentCopyFromPrevious;
    SharedPreferences spf;

    ProgressDialog dialogForApi;
    String slcID, macId;
    String units;

    API objApi;
    ArrayList<Datum> mList;

    AlertDialog loginAlertDialog;
    EditText etUsername;
    EditText etPass;
    String GlobalSLCId_AddressFragment = "";
    ProgressDialog loginProgressDialog;

    ProgressDialog dialog;
    String client_id;
    String GlobalSLCId = "";
    String userid, macID, slcId;

    String isPoleDetailUIVisible;

    Util objUtil;

    private FirebaseAnalytics mFirebaseAnalytics;
    String event_name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_pole_address, null);
        init();
        return v;
    }

    void init() {

        ButterKnife.bind(this, v);

        getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
        getActivity().findViewById(R.id.llNodeType).setVisibility(View.GONE);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage(getResources().getString(R.string.loading));
        dialog.setCancelable(false);
        objUtil = new Util();
        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        isPoleDetailUIVisible = spf.getString(AppConstants.SPF_CLIENT_SLC_EDIT_VIEW, "Yes");
        mList = new ArrayList<>();
        mList.addAll(Util.gettAssetDataSPF(spf));

        lattitude = Double.valueOf(spf.getString(AppConstants.SPF_DRAG_LATTITUDE, "0.0"));
        longgitude = Double.valueOf(spf.getString(AppConstants.SPF_DRAG_LONGITUDE, "0.0"));
        userid = spf.getString(AppConstants.USER_ID, "");
        macID = spf.getString(AppConstants.SPF_TEMP_MACID, "");
        slcId = spf.getString(AppConstants.SPF_TEMP_SLCID, "");

        units = spf.getString(AppConstants.SPF_UNITS, AppConstants.SPF_UNITES_MATRIC);
        dialogForApi = new ProgressDialog(getActivity());
        dialogForApi.setMessage(getResources().getString(R.string.loading));
        dialogForApi.setCancelable(false);

        macId = spf.getString(AppConstants.SPF_TEMP_MACID, "");
        slcID = spf.getString(AppConstants.SPF_TEMP_SLCID, "");
        client_id = spf.getString(AppConstants.CLIENT_ID, "");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        event_name = spf.getString(AppConstants.CLIENT_ID, null) + "_" + spf.getString(AppConstants.USER_ID, null) + "_";
        mFirebaseAnalytics.setCurrentScreen(getActivity(), "AddressUI", null);

        //
        objApi = new SLCScanner().networkCall();
        //new GetAddress(lattitude, longgitude).execute();
        if (Util.isInternetAvailable(getActivity()))
            getAddressAPI(lattitude, longgitude, client_id);
        else
            Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));

        btnokAddress.setOnClickListener(this);
        btnCancleAddress.setOnClickListener(this);
        llSLCBack.setOnClickListener(this);

        if (getActivity() != null) {
            ((MainActivity) getActivity()).selectScan(true);
            ((MainActivity) getActivity()).selectPole(false);
            ((MainActivity) getActivity()).selectSetting(false);
            ((MainActivity) getActivity()).selectMap(false);
        }

        spf.edit().putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, AppConstants.SPF_ADDRESS_FRAG).apply();

        //btnokAddress.setOnTouchListener(Util.colorFilter());
        //btnCancleAddress.setOnTouchListener(Util.colorFilter());

        loginAlertDialog = loginDialog(getActivity());

        GlobalSLCId_AddressFragment = spf.getString(AppConstants.SPF_LOGOUT_SLCID, "");

    }

    void getAddressAPI(Double lat, Double lng, String client_id) {
        dialogForApi.show();
        objApi.getAddress(lat, lng, client_id, userid).enqueue(new Callback<CommonResponse2>() {
            @Override
            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {
                if (dialogForApi.isShowing())
                    dialogForApi.dismiss();

                if (response.body() != null) {
                    CommonResponse2 obj = response.body();
                    if (obj.getStatus().equalsIgnoreCase("success")) {
                        CommonResponse2 response1 = response.body();
                        edtAddress.setText(response1.getAddress());
                        Util.addLog("Address Fragment: Successfully get address from api");
                    } else if (obj.getStatus().equalsIgnoreCase("error")) {
                        Util.dialogForMessage(getActivity(), obj.getMsg());
                        Util.addLog("Address Fragment: Failed to get address from api");
                    } else {
                        Toast.makeText(getActivity(), "No address found, Try Again !", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonResponse2> call, Throwable t) {
                if (dialogForApi.isShowing())
                    dialogForApi.dismiss();

                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                Util.addLog("Failed to get address from api");
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        Util.hideKeyboard(getActivity());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancleAddress:
                //edtAddress.setText("");
                Bundle bundle=new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "AddressEdit");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                Log.d(AppConstants.TAG, event_name+"AddressEdit");

                edtAddress.setSelection(edtAddress.getText().length());
                Util.showKeyobard(getActivity());
                edtAddress.setFocusable(true);
                edtAddress.setFocusableInTouchMode(true);
                edtAddress.requestFocus();

                break;
            case R.id.btnokAddress:
                Bundle bundle1=new Bundle();
                bundle1.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "AddressConfirm");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle1);
                Log.d(AppConstants.TAG, event_name+"AddressConfirm");

                if (!edtAddress.getText().toString().equals("")) {
                    spf.edit().putString(AppConstants.ADDRESS, edtAddress.getText().toString()).apply();
                    String pole_image_view_display = spf.getString(AppConstants.SPF_CLIENT_SLC_POLE_IMAGE_VIEW, "Yes");
                    if (pole_image_view_display.equalsIgnoreCase("Yes"))
                        objUtil.loadFragment(new CameraPreviewFragment(), getActivity());
                    else {
                        Bundle mBundle = new Bundle();
                        NoteFragment objNoteFragment=new NoteFragment();
                        mBundle.putBoolean(AppConstants.ISVIEWONLY,false);
                        mBundle.putString(AppConstants.FROM,"address");
                        objNoteFragment.setArguments(mBundle);

                        objUtil.loadFragment(objNoteFragment, getActivity());
                        Util.addLog("Camera Preview UI: Navigate to POLE ID");


                    }

                } else {
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.please_enter_valid_addressw));
                }
                break;
            case R.id.llSLCBack:
                Bundle bundle2=new Bundle();
                bundle2.putString(FirebaseAnalytics.Param.ITEM_NAME, event_name + "AddressBack");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle2);
                Log.d(AppConstants.TAG, event_name+"AddressBack");
                objUtil.loadFragment(new SelectLocationPoleFragment(), getActivity());
                break;
        }
    }

    void saveNewSLCData() {
        String pole_option = spf.getString(AppConstants.POLE_OPTION, "");
        String notes = spf.getString(AppConstants.NOTES, "");

        ArrayList<Datum> mListNotes=new ArrayList<>();
        mListNotes.addAll(Util.getNotesData(pole_option));
        HashMap<String, RequestBody> mapNote = new HashMap<>();
        for (int i = 0; i < mListNotes.size(); i++) {
            if (!mListNotes.get(i).isStaticData()) {
                if (mListNotes.get(i).isNoteData()) {
                    mapNote.put(mListNotes.get(i).getAttrKey(), getRequestBody(mListNotes.get(i).getSelectected()));
                }
            }
        }

      /*  ArrayList<com.CL.slcscanner.Pojo.Login.Datum> mClientType = Util.getClientTypeList(spf);
        if (mClientType.size() > 0)
            mClientType.remove(0);*/

        /*
        //temp node type disable
        String node_type = spf.getString(AppConstants.SELECTED_NODE_TYPE_SAVE, mClientType.get(1).getClientType());
        if (node_type
                .equalsIgnoreCase(getString(R.string.unknown)))
            node_type = "";
*/
        dialog.show();
        objApi.saveNewSLCDataWithFile(
                getRequestBody(client_id),
                getRequestBody(userid),
                getRequestBody(macID),
                getRequestBody(slcId),
                getRequestBody(""),
                getRequestBody(String.valueOf(lattitude)),
                getRequestBody(String.valueOf(longgitude)),
                getRequestBody("NO"),
                getRequestBody(units),
                getRequestBody("Android"),
                getRequestBody(edtAddress.getText().toString()),
                getRequestBody(""),
                getRequestBody(""),
                getRequestBody(GlobalSLCId),
                getRequestBody("Yes"),
                null,
                getRequestBody(notes),
                mapNote,
                getRequestBody("")//node_type

        ).enqueue(new Callback<CommonResponse2>() {
            @Override
            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {

                if (response.body() != null) {
                    CommonResponse2 response1 = response.body();
                    if (response1.getStatus().equalsIgnoreCase("success")) {
                        if (dialog.isShowing())
                            dialog.dismiss();

                        //Util.dialogForMessage(getActivity(), response1.getAddress());

                        SharedPreferences.Editor edt = spf.edit();
                        edt.putString(AppConstants.SPF_TEMP_SLCID, "");
                        edt.putString(AppConstants.SPF_TEMP_MACID, "");
                        edt.putString(AppConstants.SPF_TEMP_POLE_ID, "");
                        edt.putString(AppConstants.SPF_DRAG_LONGITUDE, "0.0");
                        edt.putString(AppConstants.SPF_DRAG_LATTITUDE, "0.0");
                        edt.putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, "");
                        edt.putString(AppConstants.SPF_POLE_CURRENT_FRAG, "");
                        edt.putString(AppConstants.ADDRESS, "");
                        edt.putBoolean(AppConstants.SPF_ISFROMMAP, false);
                        edt.putString(AppConstants.SPF_ID, "");
                        edt.remove(AppConstants.isNoneChecked);
                        edt.apply();

                        dialog(response1.getMsg());

                        Util.addLog("slcId: New Data saved Successfully");

                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, "").apply();

                    } else if (response1.getStatus().equalsIgnoreCase("logout")) {

                        if (dialog.isShowing())
                            dialog.dismiss();

                        Log.i(AppConstants.TAG, response1.getMsg());

                        spf.edit().putBoolean(AppConstants.ISLOGGEDIN, false).apply();

                        etPass.setText("");
                        etUsername.setText("");
                        loginAlertDialog.show();
                        GlobalSLCId = response1.getSLCID();
                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, response1.getSLCID()).apply();

                        Toast.makeText(getActivity(), response1.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.i(AppConstants.TAG, response1.getMsg());
                        Util.addLog("Address UI: Save new data error" + response1.getMsg());

                    } else if (response1.getStatus().equalsIgnoreCase("error")) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        Util.dialogForMessage(getActivity(), response1.getMsg());
                        Util.addLog("Address UI: New Data saved Failed");
                    } else
                        Util.dialogForMessage(getActivity(), getString(R.string.server_error));

                } else {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    Util.dialogForMessage(getActivity(), getString(R.string.server_error));
                }
            }

            @Override
            public void onFailure(Call<CommonResponse2> call, Throwable t) {
                if (dialog.isShowing())
                    dialog.dismiss();
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                Util.addLog("Camera Preview UI: New Data saved Failed - Network / Server Error");
            }
        });
    }

    void sendDataToCameraPreview(String address, String assests) {
        Bundle mBundle1 = new Bundle();

        mBundle1.putString(AppConstants.BUNDLE_BACK_NAV_TITTLE, "ADDRESS");
        mBundle1.putBoolean(AppConstants.BUNDLE_IS_FROM_MAP, false);
        mBundle1.putBoolean(AppConstants.BUNDLE_ISNEWDATA, true);
        mBundle1.putString(AppConstants.BUNDLE_ID, "");

        mBundle1.putString(AppConstants.BUNDLE_MACID, macId);
        mBundle1.putString(AppConstants.BUNDLE_SLCID, slcID);
        mBundle1.putString(AppConstants.BUNDLE_POLE_ID, "");

        mBundle1.putString(AppConstants.BUNDLE_SKIPP, "No");
        mBundle1.putString(AppConstants.BUNDLE_DOI, "");
        mBundle1.putString(AppConstants.BUNDLE_ADDRESS, address);

        mBundle1.putString(AppConstants.BUNDLE_ASSETS, assests);

        mBundle1.putDouble(AppConstants.BUNDLE_LATTITUDE, Double.parseDouble(spf.getString(AppConstants.SPF_DRAG_LATTITUDE, "0.0")));
        mBundle1.putDouble(AppConstants.BUNDLE_LONGITUDE, Double.parseDouble(spf.getString(AppConstants.SPF_DRAG_LONGITUDE, "0.0")));

        CameraPreviewFragment fragment = new CameraPreviewFragment();
        fragment.setArguments(mBundle1);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.replace(R.id.frm1, fragment);
        fragmentTransaction.commit();
    }

    void getClientAssets(String clientId, String units) {

        objApi.getClientAssets(clientId, units, "no").enqueue(new Callback<ClientAssestMaster>() {
            @Override
            public void onResponse(Call<ClientAssestMaster> call, Response<ClientAssestMaster> response) {
                ClientAssestMaster objClientAssestMaster = response.body();
                if (objClientAssestMaster != null) {
                    if (objClientAssestMaster.getStatus().toString().equals("success")) {

                        Log.i(AppConstants.TAG, "Assets data stored");
                        Util.addLog("Assets data stored");

                        mList.addAll(objClientAssestMaster.getData());
                        com.CL.slcscanner.Pojo.ClientAssets.Datum objDatum2 = new com.CL.slcscanner.Pojo.ClientAssets.Datum();
                        objDatum2.setBtnText("Select Date");
                        objDatum2.setAttributeName(getString(R.string.attribute_date_of_installation));
                        objDatum2.setAttrKey("date_of_installation");
                        objDatum2.setSelectected("");
                        objDatum2.setStaticData(true);
                        mList.add(objDatum2);

                        //safety
                        Util.setAssetDataSPF(spf, mList);

                    } else {

                        Log.i(AppConstants.TAG, "Assets data stored Failed !");
                        Util.addLog("Assets data stored Failed !");
                    }
                }
            }

            @Override
            public void onFailure(Call<ClientAssestMaster> call, Throwable t) {
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
            }
        });
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

    class GetAddress extends AsyncTask<Void, Void, String> {

        Double latitude, longitude;
        ProgressDialog dialog;

        public GetAddress(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Loading ");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Geocoder geo = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());

                if (geo.isPresent()) {
                    List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
                    if (addresses.isEmpty()) {
                        address = "Waiting for Location";
                    } else {
                        if (addresses.size() > 0) {
                            address = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                            //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                            Util.addLog("Selected address: " + addresses);
                            Log.i(AppConstants.TAG, "Address: " + address);
                        }
                    }
                } else {
                    address = "Waiting for Location";
                    Util.addLog("Geo coder not present");
                    Log.i(AppConstants.TAG, "Geo coder not present");
                }

            } catch (Exception e) {
                Util.addLog("Selected address Exception: " + e.getMessage());
            }
            return address;
        }

        @Override
        protected void onPostExecute(String address) {
            super.onPostExecute(address);
            if (dialog.isShowing())
                dialog.dismiss();
            edtAddress.setText(address);
        }

    }

    void saveNewSLCData(String skip, String DOI, String address, String assets) {
        dialogForApi.show();
        objApi.saveNewSLCData(
                spf.getString(AppConstants.CLIENT_ID, ""),
                spf.getString(AppConstants.USER_ID, ""),
                spf.getString(AppConstants.SPF_TEMP_MACID, ""),
                spf.getString(AppConstants.SPF_TEMP_SLCID, ""),
                "",
                spf.getString(AppConstants.SPF_DRAG_LATTITUDE, "0.0"),
                spf.getString(AppConstants.SPF_DRAG_LONGITUDE, "0.0"),
                skip,
                units,
                "Android",
                address,
                DOI,
                assets,
                GlobalSLCId_AddressFragment
        ).enqueue(new Callback<CommonResponse2>() {
            @Override
            public void onResponse(Call<CommonResponse2> call, Response<CommonResponse2> response) {
                if (dialogForApi.isShowing())
                    dialogForApi.dismiss();

                if (response.body() != null) {
                    CommonResponse2 response1 = response.body();


                    if (response1.getStatus().equals("success")) {
                        //Util.dialogForMessage(getActivity(), response1.getAddress());

                        SharedPreferences.Editor edt = spf.edit();
                        edt.putString(AppConstants.SPF_TEMP_SLCID, "");
                        edt.putString(AppConstants.SPF_TEMP_MACID, "");
                        edt.putString(AppConstants.SPF_TEMP_POLE_ID, "");
                        edt.putString(AppConstants.SPF_DRAG_LONGITUDE, "0.0");
                        edt.putString(AppConstants.SPF_DRAG_LATTITUDE, "0.0");
                        edt.putString(AppConstants.SPF_SCANNER_CURRENT_FRAG, "");
                        edt.putString(AppConstants.SPF_POLE_CURRENT_FRAG, "");
                        edt.putString(AppConstants.ADDRESS, "");
                        edt.putBoolean(AppConstants.SPF_ISFROMMAP, false);
                        edt.putString(AppConstants.SPF_ID, "");

                        edt.apply();

                        dialog(response1.getMsg());

                        Util.addLog("Address Fragment UI: Data Saved Successfully");

                        //clear SLC id value
                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, "").apply();

                    } else if (response1.getStatus().equalsIgnoreCase("logout")) {
                        Log.i(AppConstants.TAG, response1.getMsg());

                        spf.edit().putBoolean(AppConstants.ISLOGGEDIN, false).apply();

                        etPass.setText("");
                        etUsername.setText("");
                        loginAlertDialog.show();
                        GlobalSLCId_AddressFragment = response1.getSLCID();
                        spf.edit().putString(AppConstants.SPF_LOGOUT_SLCID, GlobalSLCId_AddressFragment).apply();

                        Toast.makeText(getActivity(), response1.getMsg(), Toast.LENGTH_SHORT).show();
                        Log.i(AppConstants.TAG, response1.getMsg());
                        Util.addLog("Address Fragment UI" + response1.getMsg());
                    } else {
                        Util.dialogForMessage(getActivity(), response1.getMsg());
                        Util.addLog("Address Fragment UI: Data Saved Failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonResponse2> call, Throwable t) {
                if (dialogForApi.isShowing())
                    dialogForApi.dismiss();
                try {
                    Util.addLog("Address Fragment: New Data saved Failed - Network / Server Error");
                    Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
                } catch (Exception e) {
                }
            }
        });
    }

    void dialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(Html.fromHtml(msg))
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.new_scan), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        if (getActivity() != null) {
                            ((MainActivity) getActivity()).selectScan(true);
                            ((MainActivity) getActivity()).selectPole(false);
                            ((MainActivity) getActivity()).selectSetting(false);
                            ((MainActivity) getActivity()).selectHelp(false);
                        }
                        objUtil.loadFragment(new ScanMacId(), getActivity());

                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.list), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (getActivity() != null) {
                    ((MainActivity) getActivity()).selectScan(false);
                    ((MainActivity) getActivity()).selectPole(true);
                    ((MainActivity) getActivity()).selectSetting(false);
                    ((MainActivity) getActivity()).selectHelp(false);
                }

                objUtil.loadFragment(new PoleDataFragment(), getActivity());
            }
        });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setCancelable(false);
        alert.show();
    }

    private AlertDialog loginDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dilog_login, null);
        etUsername = view.findViewById(R.id.etUsername);
        etPass = view.findViewById(R.id.etPass);

        Button btSend = (Button) view.findViewById(R.id.btSend);

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etUsername.getText().toString().equals("")) {
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.empty_username));
                } else if (etPass.getText().toString().equals("")) {
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.empty_password));
                } else {
                    //login call
                    if (Util.isInternetAvailable(getActivity())) {
                        sendLoginDetailToSever(etUsername.getText().toString(), etPass.getText().toString(), 0.0, 0.0);
                    } else {
                        Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
                    }
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
                        Util.dialogForMessage(getActivity(), getResources().getString(R.string.empty_username));
                    } else if (etPass.getText().toString().equals("")) {
                        Util.dialogForMessage(getActivity(), getResources().getString(R.string.empty_password));
                    } else {
                        //login call
                        sendLoginDetailToSever(etUsername.getText().toString(), etPass.getText().toString(), 0.0, 0.0);
                    }
                }
                return handled;
            }
        });

        builder.setCancelable(false);
        builder.setView(view);
        return builder.create();
    }

    void sendLoginDetailToSever(String username, String password, Double lattitude, Double longitude) {
        String cleintid = spf.getString(AppConstants.CLIENT_ID, null);
        String userId = spf.getString(AppConstants.USER_ID, null);
        loginProgressDialog = new ProgressDialog(getActivity());
        loginProgressDialog.setMessage(getResources().getString(R.string.loading));
        loginProgressDialog.setCancelable(false);
        loginProgressDialog.show();

        Call<LoginResponse> objCheckUnieCall = objApi.checkUserLogin(
                cleintid,
                userId,
                username,
                password,
                lattitude,
                longitude,
                "Android",
                spf.getString(AppConstants.VERSION,"")
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
                        Toast.makeText(getActivity(), objCommonResponse.getMsg().toString(), Toast.LENGTH_SHORT).show();
                        Util.hideKeyboard(getActivity());
                        Util.addLog("Address Fragment UI : Login Successful");

                        if (loginAlertDialog.isShowing())
                            loginAlertDialog.dismiss();

                    } else if (objCommonResponse.getStatus().toString().equalsIgnoreCase("error")) {
                        Util.dialogForMessage(getActivity(), objCommonResponse.getMsg().toString());
                        Util.addLog("Address Fragment UI: Login failed");
                        //startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    } else if (objCommonResponse.getStatus().toString().equalsIgnoreCase("-1")) {
                        //Util.dialogForMessage(getActivity(), objCommonResponse.getMsg().toString());
                        Util.dialogForMessagePlayStoreNavigate(getActivity(),objCommonResponse.getMsg().toString());
                    } else if (objCommonResponse.getStatus().toString().equalsIgnoreCase("0")) {
                        Util.dialogForMessageNavigate(getActivity(), objCommonResponse.getMsg().toString());
                    } else
                        Util.dialogForMessage(getActivity(), getResources().getString(R.string.server_error));
                } else
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.server_error));
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (loginProgressDialog.isShowing()) {
                    loginProgressDialog.dismiss();
                }
                Util.dialogForMessage(getActivity(), t.getLocalizedMessage());
            }
        });
    }

}

/* void getAddress(Double latitude, Double longitude) {
        try {
            Geocoder geo = new Geocoder(getActivity().getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
            if (addresses.isEmpty()) {
                address = "Waiting for Location";
            } else {
                if (addresses.size() > 0) {
                    address = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();
                    //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                    Util.addLog("Selected address: " + addresses);
                    Log.i(AppConstants.TAG, "Address: " + address);
                }
            }
            edtAddress.setText(address);
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }*/


     /*if (spf.getBoolean(AppConstants.SPF_POLE_ID_VISIBILITY, true)) {
                        //loadFragment(new PoleIdFragment());
                        loadFragment(new CameraPreviewFragment());
                        Util.addLog("Location Accept UI: Navigate to POLE ID");
                    } else {
                        if (spf.getBoolean(AppConstants.SPF_OTHER_DATA_VISIBILITY, true)) {
                            Util.addLog("Location Accept UI: Navigate to POLE Details");
                            PoleDataEditFragment objFragment = new PoleDataEditFragment();
                            Bundle mBundle = new Bundle();

                            mBundle.putString("ID", "");//require id
                            mBundle.putBoolean("isNewData", true);
                            mBundle.putString("slcID", slcID);
                            mBundle.putString("poleId", "");
                            mBundle.putDouble("Lat", lattitude);
                            mBundle.putDouble("Long", longgitude);
                            mBundle.putString("MacId", macId);
                            objFragment.setArguments(mBundle);

                            loadFragment(objFragment);
                        } else {
                            //place API call for save new data
                            Util.addLog("Location Accept UI: Calling Save Data");

                            HashMap<String, String> map1 = new HashMap<>();
                            for (int i = 0; i < mList.size(); i++) {
                                if (!mList.get(i).isStaticData()) {
                                    map1.put(mList.get(i).getAttrKey(), "");
                                }
                            }
                            JSONObject json1 = new JSONObject(map1);
                            Log.i(AppConstants.TAG, json1.toString());
                            //saveNewSLCData("No", "", edtAddress.getText().toString(), json1.toString());
                            sendDataToCameraPreview(edtAddress.getText().toString(),json1.toString());
                            spf.edit().putString(AppConstants.ADDRESS, edtAddress.getText().toString()).apply();
                        }
                    }*/