
package com.example.myapplication.model.allartistmodel;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllArtistModel {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("result")
    @Expose
    private List<AllArtistResult> result = null;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<AllArtistResult> getResult() {
        return result;
    }

    public void setResult(List<AllArtistResult> result) {
        this.result = result;
    }

}
