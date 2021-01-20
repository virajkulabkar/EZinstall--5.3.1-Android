
package com.CL.slcscanner.Pojo.ListResponse;

//import com.CL.slcscanner.Utils.ClusterLibs.ClusterItem;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

//import com.google.maps.android.clustering.ClusterItem;
//import net.sharewire.googlemapsclustering.ClusterItem;

public class List implements net.sharewire.googlemapsclustering.ClusterItem, Serializable {

    @SerializedName("ID")
    @Expose
    private String ID;

    @SerializedName("id")
    @Expose
    private String id;

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
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("sync")
    @Expose
    private String sync;

    @SerializedName("clientname")
    @Expose
    private String clientname;

    @SerializedName("customer")
    @Expose
    private String customer;

    private String snippet;
    private String title;
    private LatLng mPosition;

    private String tag;

    //Activity activity;


    public List(Double lat, Double lng, Activity activity) {
        mPosition = new LatLng(lat, lng);
      //  this.activity = activity;
    }

    public List() {

    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        this.ID = iD;
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

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    public String getSync() {
        return sync;
    }

    public void setSync(String sync) {
        this.sync = sync;
    }


    public LatLng getPosition() {
        return mPosition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }


    @Override
    public double getLatitude() {
        return mPosition.latitude;
    }

    @Override
    public double getLongitude() {
        return mPosition.longitude;
    }


    @Override
    public String getTitle() {
//        if (activity != null)
//            return activity.getResources().getString(R.string.slc_id) + slcId;
//        else
            return "SLC ID: " + slcId;
    }

    @Override
    public String getSnippet() {

       /* if (activity != null)
            return activity.getResources().getString(R.string.pole_id) + poleId;*/
        //else
            return "Pole ID: " + poleId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}


