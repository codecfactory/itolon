
package com.example.myapplication.model.screenimagesmodel;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ImageResult {

    @SerializedName("folder_path")
    @Expose
    private String folderPath;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;

    public String getFolderPath() {
        return folderPath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

}
