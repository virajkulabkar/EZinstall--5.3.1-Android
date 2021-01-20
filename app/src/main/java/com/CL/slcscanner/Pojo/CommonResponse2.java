
package com.CL.slcscanner.Pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommonResponse2 {

    @SerializedName("address")
    @Expose
    private  String address;

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("SLCID")
    @Expose
    private String SLCID;

    @SerializedName("slccount")
    @Expose
    private String slccount;

    @SerializedName("shortaddress")
    @Expose
    private String shortaddress;

    public String getShortaddress() {
        return shortaddress;
    }

    public void setShortaddress(String shortaddress) {
        this.shortaddress = shortaddress;
    }

    public String getSlccount() {
        return slccount;
    }

    public void setSlccount(String slccount) {
        this.slccount = slccount;
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

    public String getSLCID() {
        return SLCID;
    }

    public void setSLCID(String SLCID) {
        this.SLCID = SLCID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
