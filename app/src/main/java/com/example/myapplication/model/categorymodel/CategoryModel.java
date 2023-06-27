
package com.example.myapplication.model.categorymodel;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CategoryModel {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("result")
    @Expose
    private List<CategoryResult> result = null;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<CategoryResult> getResult() {
        return result;
    }

    public void setResult(List<CategoryResult> result) {
        this.result = result;
    }

}
