package com.aiton.pestscontrolandroid.ui.pests;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.data.persistence.PestsRepository;
import com.aiton.pestscontrolandroid.service.RetrofitUtil;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;
import java.util.Map;

import cn.com.qiter.common.Result;
import cn.com.qiter.common.vo.PestsControlModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PestsViewModel extends AndroidViewModel {
    PestsRepository repository;
    private MutableLiveData<Integer> progress = new MutableLiveData<Integer>();

    public MutableLiveData<Integer> getProgress() {
        return progress;
    }
    private MutableLiveData<Integer> codeInt = new MutableLiveData<>();

    public MutableLiveData<Integer> getCodeInt() {
        return codeInt;
    }
    public void updateCodeInt(String codeNumber){
        RetrofitUtil.getInstance().getQrcodeService().getQrcode(codeNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.rxjava3.core.Observer<Result>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Result result) {
                        if (result.getSuccess()) {
                            LinkedTreeMap ltm = (LinkedTreeMap) result.getData().get("teacher");
                            if (ltm != null) {
                                Double codeint = (Double) ltm.get("codeInt");
                                int codeIntInt = codeint.intValue();
                                getCodeInt().setValue(codeIntInt);
                            }else {
                                getCodeInt().setValue(0);
                            }
                            Log.e(AppConstance.TAG, "onNext: " + result.toString());
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        disposable.dispose();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    public void uploadServer(PestsControlModel pestsModel) {
        RetrofitUtil.getInstance().getPestsService().savePests(pestsModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.rxjava3.core.Observer<Result>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Result result) {
                        if (result.getSuccess()) {
                            progress.setValue(100);

                            Log.e(AppConstance.TAG, "onNext: " + result.toString());
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        disposable.dispose();
                        progress.setValue(-1);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public Pests[] findAllObject(boolean update) {
        Pests[] ss = repository.findAllObject(update);
        return ss;
    }

    public LiveData<List<Pests>> findAll(boolean update) {
        LiveData<List<Pests>> ss = repository.findAll(update);
        return ss;
    }

    public PestsViewModel(@NonNull Application application) {
        super(application);
        this.repository = new PestsRepository(application);
        progress.setValue(0);
    }

    public Pests[] findAllObject() {
        Pests[] ss = repository.findAllObject();
        return ss;
    }

    public LiveData<List<Pests>> findAll() {
        LiveData<List<Pests>> ss = repository.findAll();
        return ss;
    }

    public LiveData<List<Pests>> findWithTscId(String userId) {
        return repository.findWithUserId(userId);
    }

    public void deleteWithId(int id) {
        repository.deleteWithTscId(id);
    }

    public void insert(Pests... s) {
        repository.insert(s);
    }

    public void update(Pests... s) {
        repository.update(s);
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void delete(Pests... s) {
        repository.delete(s);
    }
}
