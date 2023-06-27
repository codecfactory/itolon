
package com.example.myapplication.model.playlistmodel;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlaylistModel {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("result")
    @Expose
    private List<PlaylistResult> result = null;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<PlaylistResult> getResult() {
        return result;
    }

    public void setResult(List<PlaylistResult> result) {
        this.result = result;
    }

}
