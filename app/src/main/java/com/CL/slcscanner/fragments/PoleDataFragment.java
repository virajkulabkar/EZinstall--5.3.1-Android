package com.CL.slcscanner.fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.CL.slcscanner.Activities.LoginActivity;
import com.CL.slcscanner.Adapter.PoleAdapter;
import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.ClientType.ClientType;
import com.CL.slcscanner.Pojo.ListResponse.ListResponse;
import com.CL.slcscanner.Pojo.Login.Datum;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.DBHelper;
import com.CL.slcscanner.Utils.EndlessRecyclerViewScrollListener;
import com.CL.slcscanner.Utils.GPS.GPSTracker;
import com.CL.slcscanner.Utils.MyCallbackForControl;
import com.CL.slcscanner.Utils.MyCallbackForMapUtility;
import com.CL.slcscanner.Utils.MyCallbackForMapUtilityLocation;
import com.CL.slcscanner.Utils.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vrajesh on 2/24/2018.
 */

public class PoleDataFragment extends Fragment implements MyCallbackForControl, SearchView.OnQueryTextListener, MyCallbackForMapUtility, MyCallbackForMapUtilityLocation {
    View view;

    @BindView(R.id.rvPole)
    RecyclerView recyclerView;

    @BindView(R.id.pbForPoleList)
    ProgressBar pbForPoleList;

    @BindView(R.id.tvNoPole)
    TextView tvNoPole;

    @BindView(R.id.svSLCs)
    SearchView objSearchView;

    @BindView(R.id.btnPoleMap)
    ImageView btnPoleMap;

    @BindView(R.id.swpRefresshPole)
    SwipeRefreshLayout swpRefresshPole;

    @BindView(R.id.frmNode)
    FrameLayout frmNode;

    @BindView(R.id.tvNodeType)
    TextView tvNodeType;

    @BindView(R.id.llnode_type)
    LinearLayout llnode_type;

    PoleAdapter mAdapter;
    API objApi;

    ArrayList<com.CL.slcscanner.Pojo.ListResponse.List> mList;
    ArrayList<com.CL.slcscanner.Pojo.ListResponse.List> mListAll;

    ProgressDialog objProgressDialog;
    SharedPreferences spf;

    String client_id;
    String user_id;

    String query = "";

    DBHelper mDatabase;
    boolean isViewCalled = false;

    String isPoleClickable;

    EndlessRecyclerViewScrollListener scrollListener;
    //LinearLayoutManager layoutManager;
    //RecyclerView.LayoutManager layoutManager;

    Util objUtil;

    String lat = "0.0", lng = "0.0";
    ProgressDialog dialogForLatlong;
    String token;

    private String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private GPSTracker gps;
    private static final int PERMISSION_ALL = 1;
    ArrayList<com.CL.slcscanner.Pojo.Login.Datum> mClientType;
    ProgressDialog dialog_wait;
    int totalcount = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(AppConstants.TAG2, "onAttach");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i(AppConstants.TAG2, "onCreate");
        /*if(view!=null){
            init();
            isViewCalled=true;
        }*/
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Log.i(AppConstants.TAG2, "onCreateView");
        view = inflater.inflate(R.layout.fragment_pole, null);
        //if(!isViewCalled)
        init();
        return view;
    }

    void init() {
        ButterKnife.bind(this, view);

        getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
        getActivity().findViewById(R.id.llNodeType).setVisibility(View.GONE);

        mList = new ArrayList<>();
        mListAll = new ArrayList<>();
        mClientType = new ArrayList<>();
        Util.hideKeyboard(getActivity());

        dialogForLatlong = new ProgressDialog(getActivity());
        dialogForLatlong.setMessage(getResources().getString(R.string.please_wait));
        dialogForLatlong.setCancelable(false);

        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        token = spf.getString(AppConstants.TOKEN, "");

        isPoleClickable = spf.getString(AppConstants.SPF_CLIENT_SLC_LIST_VIEW, "Yes");

        mDatabase = new DBHelper(getActivity());
        objUtil = new Util();

        client_id = spf.getString(AppConstants.CLIENT_ID, "");
        user_id = spf.getString(AppConstants.USER_ID, "");


        lat = spf.getString(AppConstants.LATTITUDE, "");

        lng = spf.getString(AppConstants.LONGITUDE, "");
        mAdapter = new PoleAdapter(getActivity(), mList, this, isPoleClickable);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        dialog_wait = new ProgressDialog(getActivity());
        dialog_wait.setMessage(getResources().getString(R.string.loading));
        dialog_wait.setCancelable(false);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.d("***", "scroll listner call");
                if (mList.size() < totalcount) {
                    //getData(page + 1);
                    apiCall(objSearchView.getQuery().toString(), false, page + 1);
                    Log.i(AppConstants.TAG, "Pg:" + page + 1);
                }
            }
        };

      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            request6plus();
        } else {
            request6less();
        }*/

        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setAdapter(mAdapter);

        objProgressDialog = new ProgressDialog(getActivity());
        objProgressDialog.setMessage(getResources().getString(R.string.loading));
        objProgressDialog.setCancelable(false);

        objApi = new SLCScanner().networkCall();

        //dummyData();

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        objSearchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        objSearchView.setQueryHint(getResources().getString(R.string.search_by_id));
        objSearchView.setMaxWidth(Integer.MAX_VALUE);
        objSearchView.setOnQueryTextListener(this);
        query = spf.getString("search_text", "").toString();
        objSearchView.setQuery(query.toString(), true);

        // Get the search close button image view
        ImageView closeButton = (ImageView) objSearchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(getActivity());
                objSearchView.setQuery("", false);
            }
        });

        //btnPoleMap.setOnTouchListener(Util.colorFilter());
        //apiCall(query, false,1);

        btnPoleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isInternetAvailable(getActivity())) {
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();

                    AllPoleSLCMapFragment fragment = new AllPoleSLCMapFragment();
                    Bundle mBundle = new Bundle();
                    //mBundle.putSerializable("DATA", (Serializable) mList);
                    fragment.setArguments(mBundle);

                    fragmentTransaction.replace(R.id.frm1, fragment);
                    fragmentTransaction.commit(); // save the changes
                } else {
                    Util.dialogForMessage(getActivity(), getResources().getString(R.string.no_internet_connection));
                }
            }
        });

        swpRefresshPole.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                //Do something after 3000ms
                swpRefresshPole.setRefreshing(false);
            }, 2500);

            apiCall(objSearchView.getQuery().toString(), true, 1);
        });
        spf.edit().putString(AppConstants.SPF_POLE_CURRENT_FRAG, AppConstants.SPF_POLE_LIST_FRAG).apply();

        String nodeType = spf.getString(AppConstants.SELECTED_NODE_TYPE_LIST, getActivity().getResources().getString(R.string.status_all));
        if (nodeType.toString().equalsIgnoreCase("All") || nodeType.toString().equalsIgnoreCase("Todo") || nodeType.toString().equalsIgnoreCase("Todos")) {
            tvNodeType.setText(getActivity().getResources().getString(R.string.status_all));
            spf.edit().putString(AppConstants.SELECTED_NODE_TYPE_LIST, tvNodeType.getText().toString()).apply();
        } else {
            tvNodeType.setText(nodeType);
        }

        frmNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objUtil.ShowNodeTypeDialog(mClientType, getResources().getString(R.string.strNodeTypTitle), getActivity(), spf, AppConstants.LIST_SLC_UI, new Util.ClickNodeType() {
                    @Override
                    public void setOnClickNodeType(Datum objOnClickLampType,int position) {
                        apiCall(objSearchView.getQuery().toString(), false, 1);
                        tvNodeType.setText(objOnClickLampType.getClientType());
                    }
                });
            }
        });
        /*mClientType = Util.getClientTypeList(spf);*/
        //getNode();

        //apiCall(objSearchView.getQuery().toString(), false, 1);

        SharedPreferences.Editor edit=spf.edit();
        edit.remove(AppConstants.SELECTED_NODE_TYPE_EDIT_SLC);
        edit.remove(AppConstants.SELECTED_NODE_TYPE_INDEX_EDIT_SLC);
        edit.apply();

    }

    private void request6plus() {
        if (!Util.hasPermissions(getActivity(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
            return;
        } else {
            getLocation();
        }
    }

    private void request6less() {
        getLocation();
    }


    private void getLocation() {
        gps = new GPSTracker(getActivity(), this, this);
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            Log.i(AppConstants.TAG, latitude + " | " + longitude);

            if (latitude == 0.0 || longitude == 0.0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getLocation();//recursion
                    }
                }, 2000);

            } else {
                if (dialogForLatlong.isShowing())
                    dialogForLatlong.dismiss();
                spf.edit().putString(AppConstants.LATTITUDE, String.valueOf(latitude)).apply();
                spf.edit().putString(AppConstants.LONGITUDE, String.valueOf(longitude)).apply();
                Util.addLog("Current Location:  Lat:" + latitude + " Long:" + longitude);
                lat = String.valueOf(latitude);
                lng = String.valueOf(longitude);

                apiCall("", true, 1);
            }
            //log.alert(HomeActivity.this, "onLocationChanged ->" + LATITUDE + "--" + LONGITUDE).show();
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

                int grandP = 0;
                int denyP = 0;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults.length > 0
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        grandP++;
                    } else {
                        denyP++;
                    }
                }

                if (grandP == permissions.length) {
                    getLocation();
                } else {
                    Util.dialogForPermisionMessage(getActivity(), getResources().getString(R.string.permission));
                }
                return;
            }
        }
    }

    void apiCall(String searchString, boolean isFromSwipe, final int pg) {

        if (Util.isInternetAvailable(getActivity())) {

            if (pg == 1) {
                mList.clear();
                mDatabase.deleteTableData(DBHelper.SLC_TABLE_NAME);
                mAdapter.notifyDataSetChanged();
            }

            Callback<ListResponse> objCallback = new Callback<ListResponse>() {
                @Override
                public void onResponse(Call<ListResponse> call, Response<ListResponse> response) {
                    objUtil.dismissProgressDialog(dialog_wait);
                    try {

                        //objUtil.dismissProgressDialog(objProgressDialog);
                        Util.hideKeyboard(getActivity());
                        ListResponse objPoleMaster = response.body();
                        //mList.clear();
                        if (response.body() != null) {
                            if (objPoleMaster.getStatus().equals("1")) {

                                if (objPoleMaster.getData().getList().size() != 0) {
                                    totalcount = Integer.parseInt(objPoleMaster.getData().getTotNoOfRecords());
                                }

                                //if (swpRefresshPole.isRefreshing())
                                //swpRefresshPole.setRefreshing(false);

                                com.CL.slcscanner.Pojo.ListResponse.Setting objData = objPoleMaster.getSetting();
                                SharedPreferences.Editor editor = spf.edit();

                                isPoleClickable = objData.getClientSlcListView();

                                editor.putString(AppConstants.SPF_CLIENT_SLC_EDIT_VIEW, objData.getClientSlcEditView());
                                editor.putString(AppConstants.SPF_CLIENT_SLC_LIST_VIEW, isPoleClickable);
                                editor.putString(AppConstants.SPF_CLIENT_SLC_POLE_IMAGE_VIEW, objData.getClientSlcPoleImageView());
                                editor.putString(AppConstants.SPF_CLIENT_SLC_POLE_ID, objData.getClientSlcPoleId());
                                editor.putString(AppConstants.SPF_CLIENT_SLC_POLE_ASSETS_VIEW, objData.getClientSlcPoleAssetsView());
                                editor.apply();

                                List<com.CL.slcscanner.Pojo.ListResponse.List> mList1 = objPoleMaster.getData().getList();
                                for (int i = 0; i < mList1.size(); i++) {
                                    com.CL.slcscanner.Pojo.ListResponse.List objDatum = mList1.get(i);
                                    objDatum.setSlcId(mList1.get(i).getSlcId());
                                    objDatum.setPoleId(mList1.get(i).getPoleId());
                                    mList.add(objDatum);

                           /* mDatabase.insertSLCData(objDatum.getID(),
                                    objDatum.getSlcId(),
                                    objDatum.getMacAddress(),
                                    objDatum.getPoleId(),
                                    objDatum.getLat(),
                                    objDatum.getLng());*/
                                }

                                //Util.SaveArraylistInSPF(spf, mList);
                                //Util.SaveArraylistInSPF(spf,mListAll);

                                recyclerView.setVisibility(View.VISIBLE);
                                tvNoPole.setVisibility(View.GONE);
                                mAdapter.notifyDataSetChanged();


                            } else {

                                Util.dialogForMessage(getActivity(), objPoleMaster.getMsg());
                                recyclerView.setVisibility(View.GONE);
                                tvNoPole.setText(response.body().getMsg().toString());
                                tvNoPole.setVisibility(View.VISIBLE);
                                //if (swpRefresshPole.isRefreshing())
                                //   swpRefresshPole.setRefreshing(false);
                            }
                            try {
                                if ((objProgressDialog != null) && objProgressDialog.isShowing())
                                    objProgressDialog.dismiss();

                            } catch (Exception e) {

                            } finally {
                                objProgressDialog = null;
                            }
                        }
                    } catch (Exception e) {
                        objUtil.dismissProgressDialog(objProgressDialog);
                    }
                }

                @Override
                public void onFailure(Call<ListResponse> call, Throwable t) {
                    Util.hideKeyboard(getActivity());
                    //objUtil.dismissProgressDialog(objProgressDialog);
                    objUtil.dismissProgressDialog(dialog_wait);
                    if (objProgressDialog.isShowing())
                        objProgressDialog.dismiss();

                    if (swpRefresshPole.isRefreshing())
                        swpRefresshPole.setRefreshing(false);
                }
            };
            String node_type;
            if (spf.getString(AppConstants.SELECTED_NODE_TYPE_LIST, "").equalsIgnoreCase(getString(R.string.status_all))
                    ||
                    spf.getString(AppConstants.SELECTED_NODE_TYPE_LIST, "").equalsIgnoreCase(getString(R.string.unknown))
            ) {
                node_type ="";
            } else {
                node_type = spf.getString(AppConstants.SELECTED_NODE_TYPE_LIST, "");
            }

            //String node_type = spf.getString(AppConstants.SELECTED_NODE_TYPE_LIST, "");
            try {

                if (Util.isInternetAvailable(getActivity())) {
                    if (!isFromSwipe) {
                        objProgressDialog = new ProgressDialog(getActivity());
                        objProgressDialog.setMessage(getResources().getString(R.string.loading));
                        objProgressDialog.setCancelable(false);
                        //objProgressDialog.show();
                        dialog_wait.show();
                    }
                    String strPg = String.valueOf(pg);
                    //node_type
                    objApi.getFilterData(user_id, searchString, strPg, lat, lng,"").enqueue(objCallback);

                    /*if (searchString.toString().equalsIgnoreCase(""))
                        objApi.getSLCID(user_id, strPg, lat, lng).enqueue(objCallback);
                    else
                        objApi.getFilterData(user_id, searchString, strPg, lat, lng).enqueue(objCallback);*/
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.internent_connection), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onClickForControl(int position, com.CL.slcscanner.Pojo.ListResponse.List objSlcsBean, boolean flag) {
        if (isPoleClickable.equalsIgnoreCase("Yes")) {

            if (getActivity() != null) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();

                PoleDataDisplayFragment objPoleDetailsFragment = new PoleDataDisplayFragment();

                Bundle mBundle = new Bundle();
                mBundle.putSerializable("DATA_FOR_DISPLAY", objSlcsBean);
                mBundle.putBoolean("IS_FROM_MAP", false);
                mBundle.putString("ID", objSlcsBean.getID());

                spf.edit().putString(AppConstants.SPF_ID, objSlcsBean.getID()).apply();
                objPoleDetailsFragment.setArguments(mBundle);

                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frm1, objPoleDetailsFragment);
                fragmentTransaction.commit(); // save the changes
            }
        }

    }

   /* void dummyData() {
        for (int i = 0; i < 5; i++) {
            Datum objDatum = new Datum();
            objDatum.setPoleId("Pole ID " + i);
            objDatum.setSlcId("SLC ID" + i);
            objDatum.setMacAddress("MAC ID" + i);

            mList.add(objDatum);
        }
        mAdapter.notifyDataSetChanged();
    }*/

    @Override
    public boolean onQueryTextSubmit(String query) {
        //mAdapter.getFilter().filter(query);
        spf.edit().putString("search_text", query).apply();
        apiCall(query, false, 1);
        Log.i("***", "onQueryTextSubmit");
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //mAdapter.getFilter().filter(newText);
        //objProgressDialog.show();

            spf.edit().putString("search_text", newText).apply();
            if (newText.toString().trim().equalsIgnoreCase("")) {
                Util.hideKeyboard(getActivity());
                apiCall("", false, 1);
                Log.i("***", "onQueryTextChange");
            }

        return false;
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

    @Override
    public void onResume() {
        super.onResume();
        Util.hideKeyboard(getActivity());
        mAdapter.notifyDataSetChanged();
        objSearchView.clearFocus();
    }


    @Override
    public void onPause() {
        super.onPause();
        //Log.i(AppConstants.TAG2, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        // Log.i(AppConstants.TAG2, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Log.i(AppConstants.TAG2, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(AppConstants.TAG2, "onDestroy");
        new Util().dismissProgressDialog(objProgressDialog);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //Log.i(AppConstants.TAG2, "onDetach");
    }

    @Override
    public void onClickForControl(int scount, float meters) {

    }

    @Override
    public void onClickForControl(Double Lat, Double longgitude, float accuracy) {

    }
}

        /*if (query.toString().equalsIgnoreCase(""))
            apiCall(false, "");
        else
            apiCall(true, query);
         */