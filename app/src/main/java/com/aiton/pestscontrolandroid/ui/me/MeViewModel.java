package com.aiton.pestscontrolandroid.ui.me;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.data.model.UcenterMemberOrder;
import com.aiton.pestscontrolandroid.utils.SPUtil;



public class MeViewModel extends AndroidViewModel {
    private MutableLiveData<UcenterMemberOrder> mutableLiveData;
    private MutableLiveData<Boolean> isTest;

    ///天地图或ArcGIS地图
    private MutableLiveData<Boolean> tiandiMap;
    public MeViewModel(@NonNull Application application) {
        super(application);
        if (isTest == null){
            isTest = new MutableLiveData<>();
            isTest.setValue(true);
            Boolean istest = SPUtil.builder(getApplication(), AppConstance.APP_SP).getData(AppConstance.ISTEST, Boolean.class);
            if (istest == null){
                SPUtil.builder(getApplication(), AppConstance.APP_SP).setData(AppConstance.ISTEST, true);
            }
        }

        if (tiandiMap == null){
            tiandiMap = new MutableLiveData<>();
            tiandiMap.setValue(true);
            Boolean tiandi = SPUtil.builder(getApplication(), AppConstance.APP_SP).getData(AppConstance.TIANDIMAP, Boolean.class);
            if (tiandi == null){
                SPUtil.builder(getApplication(), AppConstance.APP_SP).setData(AppConstance.TIANDIMAP, true);
            }
        }
        if (mutableLiveData == null){
            UcenterMemberOrder umm = SPUtil.builder(getApplication(), AppConstance.APP_SP).getData(AppConstance.UCENTER_MEMBER_MODEL, UcenterMemberOrder.class);
            mutableLiveData = new MutableLiveData<>();
            if (umm != null)
                mutableLiveData.setValue(umm);
        }
    }

    public MutableLiveData<UcenterMemberOrder> getMutableLiveData() {
        return mutableLiveData;
    }

    public MutableLiveData<Boolean> getTiandiMap() {
        return tiandiMap;
    }

    public MutableLiveData<Boolean> getIsTest() {
        return isTest;
    }
    public void saveTianDiMap2SHP(Boolean tiandi){
        SPUtil.builder(getApplication(),AppConstance.APP_SP).setData(AppConstance.TIANDIMAP,tiandi);
    }
    public void loadTianDiMap(){
        Boolean tiandi = SPUtil.builder(getApplication(),AppConstance.APP_SP).getData(AppConstance.TIANDIMAP,Boolean.class);
        tiandiMap.setValue(tiandi);
    }
    public void setTest(Boolean test){
        SPUtil.builder(getApplication(), AppConstance.APP_SP).setData(AppConstance.ISTEST, test);
    }
    public void loadTest(){
        Boolean test = SPUtil.builder(getApplication(), AppConstance.APP_SP).getData(AppConstance.ISTEST, Boolean.class);
        isTest.setValue(test);
    }
}
