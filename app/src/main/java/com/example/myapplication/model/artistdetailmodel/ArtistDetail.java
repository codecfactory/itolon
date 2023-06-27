
package com.example.myapplication.model.artistdetailmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ArtistDetail implements Serializable {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("result")
    @Expose
    private ArtistDetailResult result;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public ArtistDetailResult getResult() {
        return result;
    }

    public void setResult(ArtistDetailResult result) {
        this.result = result;
    }

}
