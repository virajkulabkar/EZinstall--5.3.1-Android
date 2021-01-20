
package com.CL.slcscanner.Pojo.Login;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("token")
    @Expose
    private String token;

    /*@SerializedName("node_type_lg")
    @Expose
    private NodeTypeLg nodeTypeLg;*/

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

   /* public NodeTypeLg getNodeTypeLg() {
        return nodeTypeLg;
    }
    public void setNodeTypeLg(NodeTypeLg nodeTypeLg) {
        this.nodeTypeLg = nodeTypeLg;
    }*/

}
