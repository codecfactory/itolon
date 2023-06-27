
package com.example.myapplication.model.artisttracks;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ArtistTrack {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("result")
    @Expose
    private List<TrackResult> result = null;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<TrackResult> getResult() {
        return result;
    }

    public void setResult(List<TrackResult> result) {
        this.result = result;
    }

}
