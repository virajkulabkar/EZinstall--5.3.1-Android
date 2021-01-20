package com.CL.slcscanner.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.CL.slcscanner.R;
import com.CL.slcscanner.Utils.AppConstants;
import com.CL.slcscanner.Utils.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vrajesh on 2/24/2018.
 */

public class ContactFragment extends Fragment implements View.OnClickListener {


    @BindView(R.id.ivContactBg)
    ImageView ivContactBg;

    @BindView(R.id.ivCall)
    ImageView ivCall;

    @BindView(R.id.ivEmail)
    ImageView ivEmail;

    @BindView(R.id.tvEmail)
    TextView tvEmail;

    View view;
    SharedPreferences spf;

    int PERMISSION_ALL = 1;
    String number="+1978-320-4002";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact, null);
        init();
        return view;
    }

    void init() {
        getActivity().findViewById(R.id.appBarMainn).setVisibility(View.GONE);
        getActivity().findViewById(R.id.txtTest).setVisibility(View.GONE);
        getActivity().findViewById(R.id.llNodeType).setVisibility(View.GONE);

        ButterKnife.bind(this, view);
        //Glide.with(this).load(R.drawable.dark_blue_bg).into(ivContactBg);
        spf = getActivity().getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);

        ivCall.setOnClickListener(this);
        ivEmail.setOnClickListener(this);

        ivCall.setOnTouchListener(Util.colorFilter());
        ivEmail.setOnTouchListener(Util.colorFilter());

    }

     boolean permissionCheck() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

                return  false;
            }else
                return  true;
        }else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void placeCall(String call){

        if(!permissionCheck())
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_ALL);
        else{
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+call));
            startActivity(callIntent);

        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ivCall:
                //CallDialog(number);
                break;
            case R.id.ivEmail:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{tvEmail.getText().toString()});
                i.putExtra(Intent.EXTRA_SUBJECT, "");
                i.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /*void CallDialog(String num){
       AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(num)
                .setCancelable(true)
                .setTitle("Do you want to Call ?")
                .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        placeCall(number);
                        dialogInterface.dismiss();
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
        //Setting the title manually
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();
    }*/
}