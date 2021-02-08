package com.aiton.pestscontrolandroid.ui.setting;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.data.model.SettingModel;
import com.aiton.pestscontrolandroid.data.model.ShpFile;
import com.aiton.pestscontrolandroid.utils.SPUtil;

import java.util.List;

public class SettingViewModel  extends AndroidViewModel {
    private MutableLiveData<SettingModel> settingmodel;
    private MutableLiveData<List<ShpFile>> shpFile;
    SavedStateHandle handle;
    public SettingViewModel(@NonNull Application application, SavedStateHandle handle) {
        super(application);
        this.handle = handle;
        if (shpFile == null){
            shpFile = new MutableLiveData<>();
        }

        if (!handle.contains(AppConstance.SETTING_MODEL)){
            SettingModel spSetting = SPUtil.builder(application,AppConstance.APP_SP).getData(AppConstance.SETTING_MODEL,SettingModel.class);
            if (spSetting == null){
                spSetting = new SettingModel();
                spSetting.setTiandiShow(new Boolean(true));
                spSetting.setaMapShow(new Boolean(true));
                spSetting.setGoogleShow(new Boolean(false));
                spSetting.setOsmShow(new Boolean(true));
                spSetting.setArcgisShow(new Boolean(true));
                spSetting.setGeoShow(new Boolean(true));
                SPUtil.builder(application,AppConstance.APP_SP).setData(AppConstance.SETTING_MODEL,spSetting);
            }
            settingmodel = new MutableLiveData<>();
            settingmodel.setValue(spSetting);
        }
    }

    public void saveShpFile2SP(){
        List<ShpFile> shpFiles = getShpFile().getValue();
        if (shpFiles != null){
            handle.set(AppConstance.SHP_FILE,shpFiles);
            SPUtil.builder(getApplication(),AppConstance.APP_SP).setDataList(AppConstance.SHP_FILE,shpFiles);
        }

    }
    public List<ShpFile> getShpFile4SP(){
        List<ShpFile> shpFiles = null;
        if (!handle.contains(AppConstance.SHP_FILE)){
            shpFiles =  SPUtil.builder(getApplication(),AppConstance.APP_SP).getDataList(AppConstance.SHP_FILE,ShpFile.class);
        }else{
            shpFiles = handle.get(AppConstance.SHP_FILE);
        }
        return shpFiles;
    }
    public void saveSettingModel2SP(){
        SettingModel model = getSettingmodel().getValue();
        if (model != null){
            handle.set(AppConstance.SETTING_MODEL,model);
            SPUtil.builder(getApplication(),AppConstance.APP_SP).setData(AppConstance.SETTING_MODEL,model);
        }
    }

    public SettingModel getSettingModel4SP(){
        SettingModel model = null;
        if (!handle.contains(AppConstance.SETTING_MODEL)){
            model = SPUtil.builder(getApplication(),AppConstance.APP_SP).getData(AppConstance.SETTING_MODEL,SettingModel.class);
        }else{
            model = handle.get(AppConstance.SETTING_MODEL);
        }
        return model;
    }
    public MutableLiveData<List<ShpFile>> getShpFile() {
        return shpFile;
    }

    public MutableLiveData<SettingModel> getSettingmodel() {
        return settingmodel;
    }
}
