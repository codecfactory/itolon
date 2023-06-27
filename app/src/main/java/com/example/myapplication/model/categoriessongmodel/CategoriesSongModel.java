
package com.example.myapplication.model.categoriessongmodel;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoriesSongModel implements Serializable {

    @SerializedName("meta")
    @Expose

    private Meta meta;
    @SerializedName("result")
    @Expose
    private List<CategoriesResult> result = null;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<CategoriesResult> getResult() {
        return result;
    }

    public void setResult(List<CategoriesResult> result) {
        this.result = result;
    }

}
