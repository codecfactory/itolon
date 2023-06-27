
package com.example.myapplication.model.createplaylistmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreatePlaylistModel {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("result")
    @Expose
    private CreatePlaylistResult result;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public CreatePlaylistResult getResult() {
        return result;
    }

    public void setResult(CreatePlaylistResult result) {
        this.result = result;
    }

}
