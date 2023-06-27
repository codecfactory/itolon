
package com.example.myapplication.model.screenimagesmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ScreenImages {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("result")
    @Expose
    private ImageResult result;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public ImageResult getResult() {
        return result;
    }

    public void setResult(ImageResult result) {
        this.result = result;
    }

}
