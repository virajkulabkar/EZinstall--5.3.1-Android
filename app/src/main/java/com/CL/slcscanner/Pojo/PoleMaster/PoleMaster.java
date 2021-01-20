
package com.CL.slcscanner.Pojo.PoleMaster;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PoleMaster {

    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;


    @SerializedName("setting")
    @Expose
    private com.CL.slcscanner.Pojo.PoleMaster.Setting setting;

    public com.CL.slcscanner.Pojo.PoleMaster.Setting getSetting() {
        return setting;
    }

    public void setSetting(com.CL.slcscanner.Pojo.PoleMaster.Setting setting) {
        this.setting = setting;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
