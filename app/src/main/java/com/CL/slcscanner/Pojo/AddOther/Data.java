package com.CL.slcscanner.Pojo.AddOther;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("AttrKey")
    @Expose
    private String attrKey;
    @SerializedName("AssetName")
    @Expose
    private String assetName;
    @SerializedName("Note")
    @Expose
    private String note;
    @SerializedName("BtnText")
    @Expose
    private String btnText;
    @SerializedName("AttributeName")
    @Expose
    private String attributeName;
    @SerializedName("Values")
    @Expose
    private List<String> values = null;

    public String getAttrKey() {
        return attrKey;
    }

    public void setAttrKey(String attrKey) {
        this.attrKey = attrKey;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

}