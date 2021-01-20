
package com.CL.slcscanner.Pojo.PoleDisplayData;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Asset {

    @SerializedName("AttrKey")
    @Expose
    private String attrKey;
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
    private String selected;

    @SerializedName("Note")
    @Expose
    private String Note;

    boolean isPoleNotesAssets;

    public boolean isPoleNotesAssets() {
        return isPoleNotesAssets;
    }

    public void setPoleNotesAssets(boolean poleNotesAssets) {
        isPoleNotesAssets = poleNotesAssets;
    }

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

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }
}
