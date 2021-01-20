
package com.CL.slcscanner.Pojo.PoleMaster;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Setting {

    @SerializedName("client_slc_list_view")
    @Expose
    private String clientSlcListView;
    @SerializedName("client_slc_edit_view")
    @Expose
    private String clientSlcEditView;
    @SerializedName("client_slc_pole_image_view")
    @Expose
    private String clientSlcPoleImageView;
    @SerializedName("client_slc_pole_assets_view")
    @Expose
    private String clientSlcPoleAssetsView;
    @SerializedName("client_slc_pole_id")
    @Expose
    private String clientSlcPoleId;

    public String getClientSlcListView() {
        return clientSlcListView;
    }

    public void setClientSlcListView(String clientSlcListView) {
        this.clientSlcListView = clientSlcListView;
    }

    public String getClientSlcEditView() {
        return clientSlcEditView;
    }

    public void setClientSlcEditView(String clientSlcEditView) {
        this.clientSlcEditView = clientSlcEditView;
    }

    public String getClientSlcPoleImageView() {
        return clientSlcPoleImageView;
    }

    public void setClientSlcPoleImageView(String clientSlcPoleImageView) {
        this.clientSlcPoleImageView = clientSlcPoleImageView;
    }

    public String getClientSlcPoleAssetsView() {
        return clientSlcPoleAssetsView;
    }

    public void setClientSlcPoleAssetsView(String clientSlcPoleAssetsView) {
        this.clientSlcPoleAssetsView = clientSlcPoleAssetsView;
    }

    public String getClientSlcPoleId() {
        return clientSlcPoleId;
    }

    public void setClientSlcPoleId(String clientSlcPoleId) {
        this.clientSlcPoleId = clientSlcPoleId;
    }

}
