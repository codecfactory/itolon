
package com.example.myapplication.model.addcommentsmodel;

import java.io.Serializable;
import java.util.List;

import com.example.myapplication.model.postsmodel.Comment;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddCommentModel implements Serializable {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("result")
    @Expose
    private List<Comment> result = null;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Comment> getResult() {
        return result;
    }

    public void setResult(List<Comment> result) {
        this.result = result;
    }

}
