
package com.CL.slcscanner.Pojo.CheckUniqueCode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckUniqueCode {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("ClientID")
    @Expose
    private String clientID;
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("data")
    @Expose
    private Data data;

    @SerializedName("ScanLBL")
    @Expose
    private String scanLBL;

    @SerializedName("ScanPH")
    @Expose
    private String scanPH;

    @SerializedName("setting")
    @Expose
    private Setting setting;

    @SerializedName("LGVersion")
    @Expose
    private String LGVersion;

    public String getLGVersion() {
        return LGVersion;
    }

    public void setLGVersion(String LGVersion) {
        this.LGVersion = LGVersion;
    }

    public Setting getSetting() {
        return setting;
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getScanLBL() {
        return scanLBL;
    }

    public void setScanLBL(String scanLBL) {
        this.scanLBL = scanLBL;
    }

    public String getScanPH() {
        return scanPH;
    }

    public void setScanPH(String scanPH) {
        this.scanPH = scanPH;
    }

    @Override
    public String toString() {
        return "CheckUniqueCode{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                ", clientID='" + clientID + '\'' +
                ", userid='" + userid + '\'' +
                ", data=" + data +
                '}';
    }
}
