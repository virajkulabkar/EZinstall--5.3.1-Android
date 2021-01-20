
package com.CL.slcscanner.Pojo.PoleMaster;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

public class Datum implements Serializable, ClusterItem {


    private LatLng mPosition;

    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("mac_address")
    @Expose
    private String macAddress;
    @SerializedName("slc_id")
    @Expose
    private String slcId;
    @SerializedName("pole_id")
    @Expose
    private String poleId;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;

    private String tag;

    public Datum(){

    }
    public Datum(Double lat, Double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getSlcId() {
        return slcId;
    }

    public void setSlcId(String slcId) {
        this.slcId = slcId;
    }

    public String getPoleId() {
        return poleId;
    }

    public void setPoleId(String poleId) {
        this.poleId = poleId;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getSnippet() {
        return null;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
