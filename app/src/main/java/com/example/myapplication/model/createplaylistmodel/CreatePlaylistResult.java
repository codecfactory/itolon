
package com.example.myapplication.model.createplaylistmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreatePlaylistResult {

    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("playlist_id")
    @Expose
    private Integer playlistId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("desc")
    @Expose
    private String desc;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Integer playlistId) {
        this.playlistId = playlistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
