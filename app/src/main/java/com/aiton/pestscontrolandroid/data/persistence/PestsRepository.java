package com.aiton.pestscontrolandroid.data.persistence;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aiton.pestscontrolandroid.data.PestsDatabase;

import java.util.List;

public class PestsRepository {
    private LiveData<List<Pests>> all;
    private PestsDao dao;


    public PestsRepository(Context context) {
        PestsDatabase db = PestsDatabase.getInstance(context.getApplicationContext());
        this.dao = db.getPestsDao();
        all = dao.findAll();
    }
    public Pests findById(int id){
        Pests pests =  dao.findById(id);
        return pests;

    }
    public Pests findByLatLonAndUserIdAndStime(Double lat,Double lon, String stime,String userId,String qrcode){
        Pests pests =  dao.findByLatLonAndUserIdAndStime(lat,lon,stime,userId,qrcode);
        return pests;

    }
    public Pests[] findAllObject(boolean update){
        Pests[] pests =  dao.findAllObject(update);
        return pests;

    }
    public Pests[] findAllObject(){
        Pests[] pests =  dao.findAllObject();
        return pests;

    }
    public LiveData<List<Pests>> findAll(boolean update){
        LiveData<List<Pests>> pests =  dao.findAll(update);
        return pests;

    }
    public LiveData<List<Pests>> findAll(){
        LiveData<List<Pests>> pests =  dao.findAll();
        return pests;

    }
    public LiveData<List<Pests>> findWithUserId(String userId){
        return dao.findWithUserId(userId);
    }


    public void insert(Pests ...ss){
        new PestsRepository.InsertAsyncTask(dao).execute(ss);
    }

    public void update(Pests... ss){
        new PestsRepository.UpdateAsyncTask(dao).execute(ss);
    }

    public void deleteAll(){
        new PestsRepository.DeleteAllAsyncTask(dao).execute();
    }
    public void deleteWithTscId(int id){
        new PestsRepository.DeleteWithIdAsyncTask(dao).execute(id);
    }
    public void delete(Pests... ss){
        new PestsRepository.DeleteAsyncTask(dao).execute(ss);
    }
    static class InsertAsyncTask extends AsyncTask<Pests,Void,Void> {
        private PestsDao dao;

        public InsertAsyncTask(PestsDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Pests... ss) {
            dao.insert(ss);
            return null;
        }
    }

    static class UpdateAsyncTask extends AsyncTask<Pests,Void,Void>{
        private PestsDao dao;

        public UpdateAsyncTask(PestsDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Pests... ss) {
            dao.update(ss);
            return null;
        }
    }

    static class DeleteAsyncTask extends AsyncTask<Pests,Void ,Void>{
        private PestsDao dao;

        public DeleteAsyncTask(PestsDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Pests... ss) {
            dao.delete(ss);
            return null;
        }
    }

    static class DeleteAllAsyncTask extends AsyncTask<Pests,Void,Void>{
        private PestsDao dao;

        public DeleteAllAsyncTask(PestsDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Pests... ss) {
            dao.deleteAll();
            return null;
        }
    }
    static class DeleteWithIdAsyncTask extends  AsyncTask<Integer,Void,Void>{
        private PestsDao dao;

        public DeleteWithIdAsyncTask(PestsDao dao) {
            this.dao = dao;
        }


        @Override
        protected Void doInBackground(Integer... integers) {
            for (Integer i :
                    integers) {
                dao.deleteWithId(i);
            }
            return null;
        }
    }
}
