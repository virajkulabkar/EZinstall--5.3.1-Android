package com.CL.slcscanner.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.CL.slcscanner.Activities.MainActivity;
import com.CL.slcscanner.Adapter.MapTypeAdapter;
import com.CL.slcscanner.Pojo.MapTypePojo;
import com.CL.slcscanner.R;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.MyCallbackForMapType;
import com.CL.slcscanner.Utils.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by msaqib on 10/8/2018.
 */

public class MapTypeFragment extends Fragment implements MyCallbackForMapType {

    View view;

    @BindView(R.id.rvMapType)
    RecyclerView rvMapType;

    @BindView(R.id.tvPoleGlobe)
    TextView tvPoleGlobe;

    MapTypeAdapter mAdapter;
    List<MapTypePojo> mList;

    SharedPreferences spf;
    String selectedMap;

    String fragmentFrom = "";

    Util objUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_map_type, null);
        init();
        return view;
    }

    void init() {

        ButterKnife.bind(this, view);
        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);

        Bundle mBundle = getArguments();
        objUtil=new Util();
        if (mBundle != null) {
            fragmentFrom = mBundle.getString(AppConstants.KEY_MAP_TYPE_UI_TRANSFER);
        }

        getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
        getActivity().findViewById(R.id.llNodeType).setVisibility(View.GONE);

        selectedMap = spf.getString(AppConstants.SELCTED_MAP_TYPE, getString(R.string.google_map_satellite));

        mList = new ArrayList<>();
        mList.clear();

        mAdapter = new MapTypeAdapter(getActivity(), mList, this);
        //rvMapType.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        rvMapType.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rvMapType.setLayoutManager(mLayoutManager);
        rvMapType.setAdapter(mAdapter);

        setMapType();

        tvPoleGlobe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentFrom.equalsIgnoreCase(AppConstants.SPF_POLE_ALL_MAP)) {
                 objUtil.loadFragment(new AllPoleSLCMapFragment(),getActivity());
                } else if (fragmentFrom.equalsIgnoreCase(AppConstants.SPF_SELECT_POLE_LOCATION_FRAG)) {
                    objUtil.loadFragment(new SelectLocationPoleFragment(),getActivity());
                } else if (fragmentFrom.equalsIgnoreCase(AppConstants.SPF_SETTING)) {
                    objUtil.loadFragment(new SettingFragment(),getActivity());
                } else if (fragmentFrom.equalsIgnoreCase(AppConstants.SPF_POLE_LIST_FRAG)) {
                    objUtil.loadFragment(new PoleDataFragment(),getActivity());
                } else if (fragmentFrom.equalsIgnoreCase(AppConstants.SPF_SCANNER_FRAG)) {
                    objUtil.loadFragment(new ScanMacId(),getActivity());
                } else if (fragmentFrom.equalsIgnoreCase(AppConstants.SPF_POLEID_FRAG)) {
                    objUtil.loadFragment(new PoleIdFragment(),getActivity());
                } else if (fragmentFrom.equalsIgnoreCase(AppConstants.SPF_SLCID_FRAG)) {
                    objUtil.loadFragment(new SLCIdFragment(),getActivity());
                } else if (fragmentFrom.equalsIgnoreCase(AppConstants.SPF_ADDRESS_FRAG)) {
                    objUtil.loadFragment(new AddressFragement(),getActivity());
                } else if (fragmentFrom.equalsIgnoreCase(AppConstants.SPF_CAMERA_FRAG)) {
                    objUtil.loadFragment(new CameraPreviewFragment(),getActivity());
                } else if (fragmentFrom.equalsIgnoreCase(AppConstants.SPF_SLC_ID_SCANNER_FRAG)) {
                    objUtil.loadFragment(new ScanSLCId(),getActivity());
                } else if (fragmentFrom.equalsIgnoreCase(AppConstants.SPF_POLE_DISPLAY_FRAG)) {
                    objUtil.loadFragment(new PoleDataFragment(),getActivity());
                } else if (fragmentFrom.equalsIgnoreCase(AppConstants.SETTING_TAB_SELECTED)) {
                    objUtil.loadFragment(new SettingFragment(),getActivity());
                } else if (fragmentFrom.equalsIgnoreCase(AppConstants.SPF_POLE_EDIT_FRAG)
                        || fragmentFrom.equals(AppConstants.SPF_SCANNER_EDIT_FRAG)) {

                    //String result1 = spf.getString(AppConstants.SPF_POLE_CURRENT_FRAG, "");
                    //String result = spf.getString(AppConstants.SPF_SCANNER_CURRENT_FRAG, "");

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    PoleDataEditFragment fragment = new PoleDataEditFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isNewData", false);
                    bundle.putBoolean("IS_FROM_MAP", spf.getBoolean(AppConstants.SPF_ISFROMMAP, false));
                    bundle.putString("ID", spf.getString(AppConstants.SPF_ID, ""));
                    bundle.putString("slcID", spf.getString(AppConstants.SPF_TEMP_SLCID, ""));
                    bundle.putString("poleId", spf.getString(AppConstants.SPF_TEMP_POLE_ID, ""));
                    bundle.putDouble("Lat", Double.valueOf(spf.getString(AppConstants.SPF_POLE_DISPLAY_LAT, "0.0")));
                    bundle.putDouble("Long", Double.valueOf(spf.getString(AppConstants.SPF_POLE_DISPLAY_Long, "0.0")));
                    bundle.putString("MacId", spf.getString(AppConstants.SPF_TEMP_MACID, ""));
                    bundle.putBoolean(AppConstants.isfromNote,false);
                    fragment.setArguments(bundle);
                    fragmentTransaction.replace(R.id.frm1, fragment);
                    fragmentTransaction.commit(); // save the changes
                } else
                    objUtil.loadFragment(new PoleDataFragment(),getActivity());
            }
        });
        if(getActivity()!=null) {
            ((MainActivity) getActivity()).selectScan(false);
            ((MainActivity) getActivity()).selectPole(false);
            ((MainActivity) getActivity()).selectSetting(false);
            ((MainActivity) getActivity()).selectMap(true);
        }
    }

    @Override
    public void onClickForControl(int position, MapTypePojo objSlcsBean) {

        Log.i(AppConstants.TAG, "Selected map: " + objSlcsBean.getValue());
        if (objSlcsBean.isStyle())
            spf.edit().putString(AppConstants.SELCTED_MAP_TYPE, objSlcsBean.getMapLink().toString()).apply();
         else
            spf.edit().putString(AppConstants.SELCTED_MAP_TYPE, objSlcsBean.getValue().toString()).apply();

        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getValue().toString().equalsIgnoreCase(objSlcsBean.getValue())) {
                mList.get(i).setChecked(true);

                mAdapter.notifyItemChanged(position);
            } else {
                mList.get(i).setChecked(false);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    void setMapType() {

        MapTypePojo obj1 = new MapTypePojo();
        obj1.setValue(getString(R.string.google_map_standard));
        obj1.setPosition(0);
        obj1.setStyle(false);

        if (selectedMap.equalsIgnoreCase(getString(R.string.google_map_standard)))
            obj1.setChecked(true);
        else
            obj1.setChecked(false);
        mList.add(obj1);

        MapTypePojo obj2 = new MapTypePojo();
        obj2.setValue(getString(R.string.google_map_satellite));
        obj2.setPosition(1);
        obj2.setStyle(false);
        if (selectedMap.equalsIgnoreCase(getString(R.string.google_map_satellite)))
            obj2.setChecked(true);
        else
            obj2.setChecked(false);
        mList.add(obj2);

        MapTypePojo obj3 = new MapTypePojo();
        obj3.setValue(getString(R.string.google_map_hybride));
        obj3.setPosition(2);
        obj3.setStyle(false);
        if (selectedMap.equalsIgnoreCase(getString(R.string.google_map_hybride)))
            obj3.setChecked(true);
        else
            obj3.setChecked(false);
        mList.add(obj3);

        MapTypePojo obj4 = new MapTypePojo();
        obj4.setValue(getString(R.string.open_street_standard));
        obj4.setPosition(3);
        obj4.setStyle(true);
        obj4.setMapLink(AppConstants.OPEN_STREET_MAP_STANDARD_URL);
        if (selectedMap.equalsIgnoreCase(getString(R.string.open_street_standard)))
            obj4.setChecked(true);
        else
            obj4.setChecked(false);
        mList.add(obj4);

/*        MapTypePojo obj11 = new MapTypePojo();
        obj11.setValue(getString(R.string.google_map_terrian));
        obj11.setPosition(4);
        if (selectedMap.equalsIgnoreCase(getString(R.string.google_map_terrian)))
            obj11.setChecked(true);
        else
            obj11.setChecked(false);
        mList.add(obj11);

        MapTypePojo obj5 = new MapTypePojo();
        obj5.setValue(getString(R.string.open_street_cycle));
        obj5.setPosition(4);
        if (selectedMap.equalsIgnoreCase(getString(R.string.open_street_cycle)))
            obj5.setChecked(true);
        else
            obj5.setChecked(false);
        mList.add(obj5);

        MapTypePojo obj6 = new MapTypePojo();
        obj6.setValue(getString(R.string.open_street_humantarian));
        obj6.setPosition(5);
        if (selectedMap.equalsIgnoreCase(getString(R.string.open_street_humantarian)))
            obj6.setChecked(true);
        else
            obj6.setChecked(false);
        mList.add(obj6);

        MapTypePojo obj7 = new MapTypePojo();
        obj7.setValue(getString(R.string.stamen_terriean));
        obj7.setPosition(6);
        if (selectedMap.equalsIgnoreCase(getString(R.string.stamen_terriean)))
            obj7.setChecked(true);
        else
            obj7.setChecked(false);
        mList.add(obj7);

        MapTypePojo obj8 = new MapTypePojo();
        obj8.setValue(getString(R.string.stamen_tonner));
        obj8.setPosition(7);
        if (selectedMap.equalsIgnoreCase(getString(R.string.stamen_tonner)))
            obj8.setChecked(true);
        else
            obj8.setChecked(false);
        mList.add(obj8);

        MapTypePojo obj9 = new MapTypePojo();
        obj9.setValue(getString(R.string.stamen_watercolor));
        obj9.setPosition(8);
        if (selectedMap.equalsIgnoreCase(getString(R.string.stamen_watercolor)))
            obj9.setChecked(true);
        else
            obj9.setChecked(false);
        mList.add(obj9);

        MapTypePojo obj10 = new MapTypePojo();
        obj10.setValue(getString(R.string.carto_db_dark));
        obj10.setPosition(9);
        if (selectedMap.equalsIgnoreCase(getString(R.string.carto_db_dark)))
            obj10.setChecked(true);
        else
            obj10.setChecked(false);
        mList.add(obj10);

        MapTypePojo obj11 = new MapTypePojo();
        obj11.setValue(getString(R.string.carto_db_light_positron));
        obj11.setPosition(10);
        if (selectedMap.equalsIgnoreCase(getString(R.string.carto_db_light_positron)))
            obj11.setChecked(true);
        else
            obj11.setChecked(false);
        mList.add(obj11);*/

        mAdapter.notifyDataSetChanged();
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.frm1, fragment);
        fragmentTransaction.commit(); // save the changes
    }
}