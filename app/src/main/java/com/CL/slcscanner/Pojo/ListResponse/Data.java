
package com.CL.slcscanner.Pojo.ListResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @SerializedName("List")
    @Expose
    private List<com.CL.slcscanner.Pojo.ListResponse.List> list;
    @SerializedName("tot_no_of_records")
    @Expose
    private String totNoOfRecords;

    public List<com.CL.slcscanner.Pojo.ListResponse.List> getList() {
        return list;
    }

    public void setList(List<com.CL.slcscanner.Pojo.ListResponse.List> list) {
        this.list = list;
    }

    public String getTotNoOfRecords() {
        return totNoOfRecords;
    }

    public void setTotNoOfRecords(String totNoOfRecords) {
        this.totNoOfRecords = totNoOfRecords;
    }

}
