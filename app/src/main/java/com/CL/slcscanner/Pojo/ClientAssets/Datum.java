
package com.CL.slcscanner.Pojo.ClientAssets;


import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("AttrKey")
    @Expose
    private String AttrKey;

    @SerializedName("AssetName")
    @Expose
    private String assetName;
    @SerializedName("BtnText")
    @Expose
    private String btnText;
    @SerializedName("AttributeName")
    @Expose
    private String attributeName;
    @SerializedName("Values")
    @Expose
    private List<String> values = null;

    @SerializedName("Selected")
    @Expose
    private String selectected;

    @SerializedName("Note")
    @Expose
    private String Note;

    @SerializedName("isRequire")
    @Expose
    private String isRequire;
    @SerializedName("ispicklist")
    @Expose
    private String ispicklist;
    @SerializedName("type")
    @Expose
    private String type;

    boolean isPoleNoteAsset=false;


    boolean isStaticData = false;
    boolean isNoteData = false;
    boolean isClickable = false;
    boolean isMacIdClick = false;
    boolean isNoAddressFound=false;

    public boolean isNoAddressFound() {
        return isNoAddressFound;
    }

    public void setNoAddressFound(boolean noAddressFound) {
        isNoAddressFound = noAddressFound;
    }

    public boolean isPoleNoteAsset() {
        return isPoleNoteAsset;
    }

    public void setPoleNoteAsset(boolean poleNoteAsset) {
        isPoleNoteAsset = poleNoteAsset;
    }

    public String getIsRequire() {
        return isRequire;
    }

    public void setIsRequire(String isRequire) {
        this.isRequire = isRequire;
    }

    public String getIspicklist() {
        return ispicklist;
    }

    public void setIspicklist(String ispicklist) {
        this.ispicklist = ispicklist;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isMacIdClick() {
        return isMacIdClick;
    }

    public void setMacIdClick(boolean macIdClick) {
        isMacIdClick = macIdClick;
    }

    public boolean isClickable() {
        return isClickable;
    }

    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getAttrKey() {
        return AttrKey;
    }

    public void setAttrKey(String attrKey) {
        AttrKey = attrKey;
    }

    public boolean isStaticData() {
        return isStaticData;
    }

    public void setStaticData(boolean isStaticData) {
        this.isStaticData = isStaticData;
    }

    public String getSelectected() {
        return selectected;
    }

    public void setSelectected(String selectected) {
        this.selectected = selectected;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public boolean isNoteData() {
        return isNoteData;
    }

    public void setNoteData(boolean b) {
        isNoteData = b;
    }

    @Override
    public String toString() {
        return "Datum{" +
                "AttrKey='" + AttrKey + '\'' +
                ", assetName='" + assetName + '\'' +
                ", btnText='" + btnText + '\'' +
                ", attributeName='" + attributeName + '\'' +
                ", values=" + values +
                ", selectected='" + selectected + '\'' +
                ", Note='" + Note + '\'' +
                ", isRequire='" + isRequire + '\'' +
                ", ispicklist='" + ispicklist + '\'' +
                ", type='" + type + '\'' +
                ", isStaticData=" + isStaticData +
                ", isNoteData=" + isNoteData +
                ", isClickable=" + isClickable +
                ", isMacIdClick=" + isMacIdClick +
                '}';
    }
}
