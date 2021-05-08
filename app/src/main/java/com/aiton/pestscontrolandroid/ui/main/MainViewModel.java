package com.aiton.pestscontrolandroid.ui.main;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;


import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.service.OssService;
import com.aiton.pestscontrolandroid.service.RetrofitUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.qiter.common.Result;
import cn.com.qiter.common.vo.PestsControlModel;
import cn.hutool.core.date.DateTime;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {
    public final static String PESTS_HANDLE = "PESTS_HANDLE";
    public boolean loadedShp = false;
    private Map<String, String> map = null;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public int getSelectedFeature() {
        if (!handle.contains(AppConstance.SELECTED_FEATURE)) {
            return 0;
        }
        return handle.get(AppConstance.SELECTED_FEATURE);
    }

    public void setSelectedFeature(int selectedFeature) {
        handle.set(AppConstance.SELECTED_FEATURE, selectedFeature);
    }

    public boolean isLoadedShp() {
        return loadedShp;
    }

    public void setLoadedShp(boolean loadedShp) {
        this.loadedShp = loadedShp;
    }

    private MutableLiveData<PestsControlModel> pestsModelMutableLiveData;
    SavedStateHandle handle;

    private MutableLiveData<HashMap<String, Object>> selectedFeatureAttribute;

    public MutableLiveData<PestsControlModel> getPestsModelMutableLiveData() {
        return pestsModelMutableLiveData;
    }

    public MutableLiveData<HashMap<String, Object>> getSelectedFeatureAttribute() {
        return selectedFeatureAttribute;
    }

    public MainViewModel(@NonNull Application application, SavedStateHandle handle) {
        super(application);
        this.handle = handle;
        if (selectedFeatureAttribute == null) {
            selectedFeatureAttribute = new MutableLiveData<>();
        }

        if (!handle.contains(AppConstance.SELECTED_FEATURE)) {
            handle.set(AppConstance.SELECTED_FEATURE, 0);
        }

        this.handle = handle;
        if (!handle.contains(AppConstance.FEATURE_ATTRIBUTE)) {
            handle.set(AppConstance.FEATURE_ATTRIBUTE, selectedFeatureAttribute.getValue());
        }
        pestsModelMutableLiveData = new MutableLiveData<>();
    }

    public void setPests2Handle(PestsControlModel pestsModel) {
        handle.set(PESTS_HANDLE, pestsModel);
    }

    public PestsControlModel getPests2Handle() {
        if (handle.contains(PESTS_HANDLE)) {
            return handle.get(PESTS_HANDLE);
        } else {
            return null;
        }
    }

}
