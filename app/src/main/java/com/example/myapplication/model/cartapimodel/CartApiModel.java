
package com.example.myapplication.model.cartapimodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CartApiModel {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("result")
    @Expose
    private Result result;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

}
