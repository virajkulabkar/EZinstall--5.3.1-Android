
package com.CL.slcscanner.Pojo.SLCDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Data implements Serializable{

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
    @SerializedName("measurement_units")
    @Expose
    private String measurementUnits;
    @SerializedName("pole_id")
    @Expose
    private String poleId;
    @SerializedName("pole_height")
    @Expose
    private String poleHeight;
    @SerializedName("mounting_method")
    @Expose
    private String mountingMethod;
    @SerializedName("pole_type")
    @Expose
    private String poleType;
    @SerializedName("arm_lenght")
    @Expose
    private String armLenght;
    @SerializedName("pole_diameter")
    @Expose
    private String poleDiameter;
    @SerializedName("pole_config")
    @Expose
    private String poleConfig;
    @SerializedName("pole_condition")
    @Expose
    private String poleCondition;
    @SerializedName("fixtures_per_pole")
    @Expose
    private String fixturesPerPole;
    @SerializedName("fixture_manufacturer")
    @Expose
    private String fixtureManufacturer;
    @SerializedName("fixture_wattage")
    @Expose
    private String fixtureWattage;
    @SerializedName("date_of_installation")
    @Expose
    private String dateOfInstallation;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("ipaddress")
    @Expose
    private String ipaddress;
    @SerializedName("source")
    @Expose
    private String source;

    @SerializedName("pole_power_feed")
    @Expose
    private String pole_power_feed;

    @SerializedName("pole_owner")
    @Expose
    private String pole_owner;

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

    public String getPoleHeight() {
        return poleHeight;
    }

    public void setPoleHeight(String poleHeight) {
        this.poleHeight = poleHeight;
    }

    public String getMountingMethod() {
        return mountingMethod;
    }

    public void setMountingMethod(String mountingMethod) {
        this.mountingMethod = mountingMethod;
    }

    public String getPoleType() {
        return poleType;
    }

    public void setPoleType(String poleType) {
        this.poleType = poleType;
    }

    public String getArmLenght() {
        return armLenght;
    }

    public void setArmLenght(String armLenght) {
        this.armLenght = armLenght;
    }

    public String getPoleDiameter() {
        return poleDiameter;
    }

    public void setPoleDiameter(String poleDiameter) {
        this.poleDiameter = poleDiameter;
    }

    public String getPoleConfig() {
        return poleConfig;
    }

    public void setPoleConfig(String poleConfig) {
        this.poleConfig = poleConfig;
    }

    public String getPoleCondition() {
        return poleCondition;
    }

    public void setPoleCondition(String poleCondition) {
        this.poleCondition = poleCondition;
    }

    public String getFixturesPerPole() {
        return fixturesPerPole;
    }

    public void setFixturesPerPole(String fixturesPerPole) {
        this.fixturesPerPole = fixturesPerPole;
    }

    public String getFixtureManufacturer() {
        return fixtureManufacturer;
    }

    public void setFixtureManufacturer(String fixtureManufacturer) {
        this.fixtureManufacturer = fixtureManufacturer;
    }

    public String getFixtureWattage() {
        return fixtureWattage;
    }

    public void setFixtureWattage(String fixtureWattage) {
        this.fixtureWattage = fixtureWattage;
    }

    public String getDateOfInstallation() {
        return dateOfInstallation;
    }

    public void setDateOfInstallation(String dateOfInstallation) {
        this.dateOfInstallation = dateOfInstallation;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getIpaddress() {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPole_power_feed() {
        return pole_power_feed;
    }

    public void setPole_power_feed(String pole_power_feed) {
        this.pole_power_feed = pole_power_feed;
    }

    public String getPole_owner() {
        return pole_owner;
    }

    public void setPole_owner(String pole_owner) {
        this.pole_owner = pole_owner;
    }
}
