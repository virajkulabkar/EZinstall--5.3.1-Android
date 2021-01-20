
package com.CL.slcscanner.Pojo.ClientAssets;


import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ClientAssestMaster {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    @SerializedName("pole_id")
    @Expose
    private String pole_id;

    @SerializedName("measurement_units")
    @Expose
    private String measurement_units;

    @SerializedName("pole_image_url")
    @Expose
    private String pole_image_url;

    @SerializedName("pole_image")
    @Expose
    private String pole_image;

    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("slccount")
    @Expose
    private String slccount;


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public String getPole_id() {
        return pole_id;
    }

    public void setPole_id(String pole_id) {
        this.pole_id = pole_id;
    }

    public String getMeasurement_units() {
        return measurement_units;
    }

    public void setMeasurement_units(String measurement_units) {
        this.measurement_units = measurement_units;
    }

    public String getPole_image_url() {
        return pole_image_url;
    }

    public void setPole_image_url(String pole_image_url) {
        this.pole_image_url = pole_image_url;
    }

    public String getPole_image() {
        return pole_image;
    }

    public void setPole_image(String pole_image) {
        this.pole_image = pole_image;
    }

    public String getSlccount() {
        return slccount;
    }

    public void setSlccount(String slccount) {
        this.slccount = slccount;
    }
}
