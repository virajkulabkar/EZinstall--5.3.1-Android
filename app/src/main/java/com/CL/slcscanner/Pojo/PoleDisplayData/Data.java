
package com.CL.slcscanner.Pojo.PoleDisplayData;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("ID")
    @Expose
    private String iD;
    @SerializedName("client_id")
    @Expose
    private String clientId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("mac_address")
    @Expose
    private String macAddress;
    @SerializedName("slc_id")
    @Expose
    private String slcId;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lng")
    @Expose
    private String lng;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("measurement_units")
    @Expose
    private String measurementUnits;
    @SerializedName("pole_id")
    @Expose
    private String poleId;
    @SerializedName("savedAssets")
    @Expose
    private String savedAssets;
    @SerializedName("date_of_installation")
    @Expose
    private String dateOfInstallation;
    @SerializedName("Assets")
    @Expose
    private List<Asset> assets = null;
    @SerializedName("pole_image_url")
    @Expose
    private String pole_image_url;

    @SerializedName("notes")
    @Expose
    private  String notes;

    @SerializedName("pole_options")
    @Expose
    private  String[] pole_option;

    @SerializedName("node_type")
    @Expose
    private String node_type;

    public String getNode_type() {
        return node_type;
    }

    public void setNode_type(String node_type) {
        this.node_type = node_type;
    }

    public String[] getPole_option() {
        return pole_option;
    }

    public void setPole_option(String[] pole_option) {
        this.pole_option = pole_option;
    }

    public String getNotes() {
        return notes;
    }
    /*@SerializedName("pole_options")
    @Expose
    private  String pole_options;

    public String getNotes() {
        return notes;
    }*/

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getID() {
        return iD;
    }

    public void setID(String iD) {
        this.iD = iD;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMeasurementUnits() {
        return measurementUnits;
    }

    public void setMeasurementUnits(String measurementUnits) {
        this.measurementUnits = measurementUnits;
    }

    public String getPoleId() {
        return poleId;
    }

    public void setPoleId(String poleId) {
        this.poleId = poleId;
    }

    public String getSavedAssets() {
        return savedAssets;
    }

    public void setSavedAssets(String savedAssets) {
        this.savedAssets = savedAssets;
    }

    public String getDateOfInstallation() {
        return dateOfInstallation;
    }

    public void setDateOfInstallation(String dateOfInstallation) {
        this.dateOfInstallation = dateOfInstallation;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public String getPole_image_url() {
        return pole_image_url;
    }

    public void setPole_image_url(String pole_image_url) {
        this.pole_image_url = pole_image_url;
    }
}