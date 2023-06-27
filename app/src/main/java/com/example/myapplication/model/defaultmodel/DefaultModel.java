
package com.example.myapplication.model.defaultmodel;

import com.example.myapplication.model.deleteartistsongmodel.Meta;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DefaultModel {

    @SerializedName("meta")
    @Expose
    private Meta meta;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

}
