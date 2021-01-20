package com.CL.slcscanner.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.CL.slcscanner.Networking.API;
import com.CL.slcscanner.Pojo.PoleMaster.Datum;
import com.CL.slcscanner.R;
import com.CL.slcscanner.SLCScanner;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by vrajesh on 3/16/2018.
 */

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Activity context;
    boolean isSinglePin = false;
    boolean isFromDialog = false;
    String isPoleDetailsVisible;

    API objAPI;
    String clientId;
    String snipetStr;
    LayoutInflater inflater;
    View view;

    public CustomInfoWindowGoogleMap(Activity ctx, boolean isSinglePin, String isPoleDetailsVisible, String snipetStr) {
        this.context = ctx;
        this.isSinglePin = isSinglePin;
        this.isPoleDetailsVisible=isPoleDetailsVisible;
        objAPI= new SLCScanner().networkCall();
        this.snipetStr=snipetStr;

        if(ctx!=null) {
            SharedPreferences spf = ctx.getSharedPreferences(AppConstants.SPF, Context.MODE_PRIVATE);
            clientId = spf.getString(AppConstants.CLIENT_ID, "");
        }
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view =inflater.inflate(R.layout.raw_google_marker, null);

    }

    @Override
    public View getInfoWindow(Marker marker) {

            Datum objGoogleMapInfo = (Datum) marker.getTag();
            final TextView details_tv = view.findViewById(R.id.tvInfo);
            final ImageView iv = view.findViewById(R.id.ivInfo);

            if (!isSinglePin) {
                details_tv.setText(Html.fromHtml(
                         context.getResources().getString(R.string.slc_id)+" : "+ objGoogleMapInfo.getSlcId()
                                + "<br>"+context.getResources().getString(R.string.pole_id)+" : " + objGoogleMapInfo.getPoleId()));

                if (isPoleDetailsVisible.equalsIgnoreCase("Yes"))
                    iv.setVisibility(View.VISIBLE);
                else
                    iv.setVisibility(View.GONE);

            } else {
                details_tv.setText(snipetStr);
                //details_tv.setText(context.getResources().getString(R.string.single_pin));
                iv.setVisibility(View.GONE);
            }

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        //this will take default background of google map
        return null;
    }

}