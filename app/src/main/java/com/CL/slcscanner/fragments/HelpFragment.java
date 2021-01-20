package com.CL.slcscanner.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.CL.slcscanner.Activities.MainActivity;
import com.CL.slcscanner.R;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Activities.LoginActivity;
import com.CL.slcscanner.Utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vrajesh on 2/24/2018.
 */

public class HelpFragment extends Fragment {

    @BindView(R.id.btnHelp)
    Button btnHelp;

    SharedPreferences spf;
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_help,null);
        init();
        return  view;
    }

    void init(){

        getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
        getActivity().findViewById(R.id.llNodeType).setVisibility(View.GONE);

        ButterKnife.bind(this,view);
        spf=getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
        dialog();

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });

    }

    void dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to Logout ?")
                .setCancelable(true)
                .setTitle("Logout")
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        spf.edit().clear().apply();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        dialogInterface.dismiss();
                        Util.addLog("Logout successfully");
                    }
                });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();
    }
}