
package com.CL.slcscanner.Pojo.SettingMaster;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SettingMaster {

    @SerializedName("label")
    @Expose
    private String label;

    @SerializedName("msg")
    @Expose
    private  String msg;

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
