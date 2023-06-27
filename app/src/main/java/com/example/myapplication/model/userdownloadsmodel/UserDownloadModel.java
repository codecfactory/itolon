
package com.example.myapplication.model.userdownloadsmodel;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDownloadModel {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("result")
    @Expose
    private List<UserDownloadResult> result = null;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<UserDownloadResult> getResult() {
        return result;
    }

    public void setResult(List<UserDownloadResult> result) {
        this.result = result;
    }

}
