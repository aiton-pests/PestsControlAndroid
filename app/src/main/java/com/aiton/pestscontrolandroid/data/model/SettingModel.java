package com.aiton.pestscontrolandroid.data.model;

import java.io.Serializable;
import java.util.List;



public class SettingModel implements Serializable {
    private Boolean osmShow = false;
    private Boolean aMapShow = false;
    private Boolean tiandiShow = false;
    private Boolean googleShow = false;
    private Boolean arcgisShow = false;
    private Boolean geoShow = false;

    public SettingModel() {
    }

    public Boolean getOsmShow() {
        return osmShow;
    }

    public Boolean getArcgisShow() {
        return arcgisShow;
    }

    public void setArcgisShow(Boolean arcgisShow) {
        this.arcgisShow = arcgisShow;
    }

    public Boolean getGeoShow() {
        return geoShow;
    }

    public void setGeoShow(Boolean geoShow) {
        this.geoShow = geoShow;
    }

    public void setOsmShow(Boolean osmShow) {
        this.osmShow = osmShow;
    }

    public Boolean getaMapShow() {
        return aMapShow;
    }

    public void setaMapShow(Boolean aMapShow) {
        this.aMapShow = aMapShow;
    }

    public Boolean getTiandiShow() {
        return tiandiShow;
    }

    public void setTiandiShow(Boolean tiandiShow) {
        this.tiandiShow = tiandiShow;
    }

    public Boolean getGoogleShow() {
        return googleShow;
    }

    public void setGoogleShow(Boolean googleShow) {
        this.googleShow = googleShow;
    }

    @Override
    public String toString() {
        return "SettingModel{" +
                "osmShow=" + osmShow +
                ", aMapShow=" + aMapShow +
                ", tiandiShow=" + tiandiShow +
                ", googleShow=" + googleShow +
                ", arcgisShow=" + arcgisShow +
                ", geoShow=" + geoShow +
                '}';
    }

    public SettingModel(Boolean osmShow, Boolean aMapShow, Boolean tiandiShow, Boolean googleShow, Boolean arcgisShow, Boolean geoShow) {
        this.osmShow = osmShow;
        this.aMapShow = aMapShow;
        this.tiandiShow = tiandiShow;
        this.googleShow = googleShow;
        this.arcgisShow = arcgisShow;
        this.geoShow = geoShow;
    }
}
