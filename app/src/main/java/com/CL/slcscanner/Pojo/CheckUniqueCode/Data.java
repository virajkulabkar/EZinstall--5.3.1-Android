
package com.CL.slcscanner.Pojo.CheckUniqueCode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("ClientID")
    @Expose
    private String clientID;
    @SerializedName("userid")
    @Expose
    private String userid;

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

}
