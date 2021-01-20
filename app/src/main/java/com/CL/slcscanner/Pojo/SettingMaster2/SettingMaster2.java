
package com.CL.slcscanner.Pojo.SettingMaster2;

import com.CL.slcscanner.Pojo.SettingMaster.*;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SettingMaster2 {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private com.CL.slcscanner.Pojo.SettingMaster2.Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public com.CL.slcscanner.Pojo.SettingMaster2.Data getData() {
        return data;
    }

    public void setData(com.CL.slcscanner.Pojo.SettingMaster2.Data data) {
        this.data = data;
    }

}
