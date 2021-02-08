package com.aiton.pestscontrolandroid.data.model;

import java.io.Serializable;

public class ShpFile implements Serializable {
    private String url;
    private boolean selected;
    private String other;

    public ShpFile() {
    }

    public ShpFile(String url, boolean selected) {
        this.url = url;
        this.selected = selected;
    }

    @Override
    public String toString() {
        return "ShpFile{" +
                "url='" + url + '\'' +
                ", selected=" + selected +
                ", other='" + other + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
