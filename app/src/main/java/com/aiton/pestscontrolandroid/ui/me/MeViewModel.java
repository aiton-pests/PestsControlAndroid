package com.aiton.pestscontrolandroid.ui.me;

import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.utils.SPUtil;

import cn.com.qiter.pests.UcenterMemberModel;

public class MeViewModel extends AndroidViewModel {
    private MutableLiveData<UcenterMemberModel> mutableLiveData;
    private MutableLiveData<Integer> isTest;
    public MeViewModel(@NonNull Application application) {
        super(application);
        if (isTest == null){
            isTest = new MutableLiveData<>();
            isTest.setValue(0);
        }
        if (mutableLiveData == null){
            UcenterMemberModel umm = SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.UCENTER_MEMBER_MODEL, UcenterMemberModel.class);
            mutableLiveData = new MutableLiveData<>();
            if (umm != null)
                mutableLiveData.setValue(umm);
        }
    }

    public MutableLiveData<UcenterMemberModel> getMutableLiveData() {
        return mutableLiveData;
    }

    public MutableLiveData<Integer> getIsTest() {
        return isTest;
    }

    public void setTest(int test){
        isTest.setValue(test);
        SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).setData(AppConstance.ISTEST, test);
    }
    public Integer getTest(){

        Integer test = SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.ISTEST, Integer.class);
        isTest.setValue(test);
        return test;
    }
}
