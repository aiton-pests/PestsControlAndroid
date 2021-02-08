package com.aiton.pestscontrolandroid.ui.pests;

import android.app.Application;

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

import java.util.List;
import java.util.Map;

public class PestsViewModel  extends AndroidViewModel {
    PestsRepository repository;


    public Pests[] findAllObject(boolean update){
        Pests[] ss = repository.findAllObject(update);
        return ss;
    }
    public LiveData<List<Pests>> findAll(boolean update){
        LiveData<List<Pests>> ss = repository.findAll(update);
        return ss;
    }
    public PestsViewModel(@NonNull Application application) {
        super(application);
        this.repository = new PestsRepository(application);
    }

    public Pests[] findAllObject(){
        Pests[] ss = repository.findAllObject();
        return ss;
    }
    public LiveData<List<Pests>> findAll(){
        LiveData<List<Pests>> ss = repository.findAll();
        return ss;
    }

    public LiveData<List<Pests>> findWithTscId(String userId){
        return repository.findWithUserId(userId);
    }
    public void deleteWithId(int id){
        repository.deleteWithTscId(id);
    }
    public void insert(Pests ... s){
        repository.insert(s);
    }
    public void update(Pests ... s){
        repository.update(s);
    }

    public void deleteAll(){
        repository.deleteAll();
    }

    public void delete(Pests ... s){
        repository.delete(s);
    }
}
