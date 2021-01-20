
package com.CL.slcscanner.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommonResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("ScanLBL")
    @Expose
    private String scanLBL;

    @SerializedName("ScanPH")
    @Expose
    private String scanPH;

    @SerializedName("slc_id")
    @Expose
    private String slc_id;

    @SerializedName("mac_address_type")
    @Expose
    private String mac_address_type;

    @SerializedName("token ")
    @Expose
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMac_address_type() {
        return mac_address_type;
    }

    public void setMac_address_type(String mac_address_type) {
        this.mac_address_type = mac_address_type;
    }

    public String getSlc_id() {
        return slc_id;
    }

    public void setSlc_id(String slc_id) {
        this.slc_id = slc_id;
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
}
