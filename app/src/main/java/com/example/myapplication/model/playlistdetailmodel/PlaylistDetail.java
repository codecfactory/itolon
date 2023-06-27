
package com.example.myapplication.model.playlistdetailmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PlaylistDetail implements Serializable {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("result")
    @Expose
    private PlaylistResult result;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public PlaylistResult getResult() {
        return result;
    }

    public void setResult(PlaylistResult result) {
        this.result = result;
    }

}
