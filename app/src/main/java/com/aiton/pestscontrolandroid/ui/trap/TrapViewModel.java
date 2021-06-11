package com.aiton.pestscontrolandroid.ui.trap;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.data.model.Result;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.data.persistence.PestsRepository;
import com.aiton.pestscontrolandroid.data.persistence.Trap;
import com.aiton.pestscontrolandroid.data.persistence.TrapRepository;
import com.aiton.pestscontrolandroid.service.RetrofitUtil;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

import cn.com.qiter.common.vo.PestsTrapModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TrapViewModel extends AndroidViewModel {
    TrapRepository repository;
    private MutableLiveData<Integer> progress = new MutableLiveData<>();

    public MutableLiveData<Integer> getProgress() {
        return progress;
    }

    private MutableLiveData<Integer> trapCount = new MutableLiveData<>();

    public MutableLiveData<Integer> getTrapCount() {
        return trapCount;
    }

    public void countTrap(String qrcode){
        RetrofitUtil.getInstance().getTrapService().getByQrcode(qrcode)
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
                            ArrayList ltm = (ArrayList) result.getData().get("trap");
                            getTrapCount().setValue(ltm.size());
                            Log.e(AppConstance.TAG, "onNext: " + result.toString());
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        disposable.dispose();
                        getTrapCount().setValue(0);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
                            Double codeint = (Double) ltm.get("codeInt");
                            int codeIntInt = codeint.intValue();
                            getCodeInt().setValue(codeIntInt);
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

    private MutableLiveData<Trap> trapMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<Trap> getTrapMutableLiveData() {
        return trapMutableLiveData;
    }

    public void uploadServer(PestsTrapModel model) {
        RetrofitUtil.getInstance().getTrapService().save(model)
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
    public Trap[] findAllObject(boolean update){
        Trap[] ss = repository.findAllObject(update);
        return ss;
    }
    public Trap findByLatLonAndUserIdAndStime(Double lat,Double lon, String stime,String userId,String qrcode){
        Trap pests =  repository.findByLatLonAndUserIdAndStime(lat,lon,  stime, userId,qrcode);
        return pests;
    }
    public LiveData<List<Trap>> findAll(boolean update){
        LiveData<List<Trap>> ss = repository.findAll(update);
        return ss;
    }
    public TrapViewModel(@NonNull Application application) {
        super(application);
        this.repository = new TrapRepository(application);
        progress.setValue(0);
    }

    public Trap[] findAllObject(){
        Trap[] ss = repository.findAllObject();
        return ss;
    }
    public LiveData<List<Trap>> findAll(){
        LiveData<List<Trap>> ss = repository.findAll();
        return ss;
    }

    public LiveData<List<Trap>> findWithTscId(String userId){
        return repository.findWithUserId(userId);
    }
    public void deleteWithId(int id){
        repository.deleteWithTscId(id);
    }
    public void insert(Trap ... s){
        repository.insert(s);
    }
    public void update(Trap ... s){
        repository.update(s);
    }

    public void deleteAll(){
        repository.deleteAll();
    }

    public void delete(Trap ... s){
        repository.delete(s);
    }
}
