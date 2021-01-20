
package com.CL.slcscanner.Pojo.SettingMaster;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("pole_height")
    @Expose
    private List<String> pole_height=null;

    @SerializedName("mounting_method")
    @Expose
    private List<String> mountingMethod = null;

    @SerializedName("pole_type")
    @Expose
    private List<String> poleType = null;

    @SerializedName("arm_lenght")
    @Expose
    private List<String> armLenght = null;
    @SerializedName("pole_diameter")
    @Expose
    private List<String> poleDiameter = null;
    @SerializedName("pole_config")
    @Expose
    private List<String> poleConfig = null;
    @SerializedName("pole_condition")
    @Expose
    private List<String> poleCondition = null;
    @SerializedName("fixtures_per_pole")
    @Expose
    private List<String> fixturesPerPole = null;
    @SerializedName("fixture_manufacturer")
    @Expose
    private List<String> fixtureManufacturer = null;
    @SerializedName("fixture_wattage")
    @Expose
    private List<String> fixtureWattage = null;

    @SerializedName("pole_power_feed")
    @Expose
    private List<String> pole_power_feed = null;

    @SerializedName("pole_owner")
    @Expose
    private List<String> pole_owner = null;



    public List<String> getPole_height() {
        return pole_height;
    }

    public void setPole_height(List<String> pole_height) {
        this.pole_height = pole_height;
    }

    public List<String> getMountingMethod() {
        return mountingMethod;
    }

    public void setMountingMethod(List<String> mountingMethod) {
        this.mountingMethod = mountingMethod;
    }

    public List<String> getPoleType() {
        return poleType;
    }

    public void setPoleType(List<String> poleType) {
        this.poleType = poleType;
    }

    public List<String> getArmLenght() {
        return armLenght;
    }

    public void setArmLenght(List<String> armLenght) {
        this.armLenght = armLenght;
    }

    public List<String> getPoleDiameter() {
        return poleDiameter;
    }

    public void setPoleDiameter(List<String> poleDiameter) {
        this.poleDiameter = poleDiameter;
    }

    public List<String> getPoleConfig() {
        return poleConfig;
    }

    public void setPoleConfig(List<String> poleConfig) {
        this.poleConfig = poleConfig;
    }

    public List<String> getPoleCondition() {
        return poleCondition;
    }

    public void setPoleCondition(List<String> poleCondition) {
        this.poleCondition = poleCondition;
    }

    public List<String> getFixturesPerPole() {
        return fixturesPerPole;
    }

    public void setFixturesPerPole(List<String> fixturesPerPole) {
        this.fixturesPerPole = fixturesPerPole;
    }

    public List<String> getFixtureManufacturer() {
        return fixtureManufacturer;
    }

    public void setFixtureManufacturer(List<String> fixtureManufacturer) {
        this.fixtureManufacturer = fixtureManufacturer;
    }

    public List<String> getFixtureWattage() {
        return fixtureWattage;
    }

    public void setFixtureWattage(List<String> fixtureWattage) {
        this.fixtureWattage = fixtureWattage;
    }

    public List<String> getPole_power_feed() {
        return pole_power_feed;
    }

    public void setPole_power_feed(List<String> pole_power_feed) {
        this.pole_power_feed = pole_power_feed;
    }

    public List<String> getPole_owner() {
        return pole_owner;
    }

    public void setPole_owner(List<String> pole_owner) {
        this.pole_owner = pole_owner;
    }
}
