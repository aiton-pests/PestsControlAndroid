package com.aiton.pestscontrolandroid.data.persistence;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.aiton.pestscontrolandroid.data.PestsDatabase;

import java.util.List;

public class TrapRepository {
    private LiveData<List<Trap>> all;
    private TrapDao dao;


    public TrapRepository(Context context) {
        PestsDatabase db = PestsDatabase.getInstance(context.getApplicationContext());
        this.dao = db.getTrapDao();
        all = dao.findAll();
    }
    public Trap findById(int id){
        Trap pests =  dao.findById(id);
        return pests;

    }
    public Trap findByLatLonAndUserIdAndStime(Double lat,Double lon, String stime,String userId,String qrcode){
        Trap pests =  dao.findByLatLonAndUserIdAndStime(lat,lon,  stime, userId,qrcode);
        return pests;

    }
    public Trap[] findAllObject(boolean update){
        Trap[] pests =  dao.findAllObject(update);
        return pests;

    }
    public Trap[] findAllObject(){
        Trap[] pests =  dao.findAllObject();
        return pests;

    }
    public LiveData<List<Trap>> findAll(boolean update){
        LiveData<List<Trap>> pests =  dao.findAll(update);
        return pests;

    }
    public LiveData<List<Trap>> findAll(){
        LiveData<List<Trap>> pests =  dao.findAll();
        return pests;

    }
    public LiveData<List<Trap>> findWithUserId(String userId){
        return dao.findWithUserId(userId);
    }


    public void insert(Trap ...ss){
        new TrapRepository.InsertAsyncTask(dao).execute(ss);
    }

    public void update(Trap... ss){
        new TrapRepository.UpdateAsyncTask(dao).execute(ss);
    }

    public void deleteAll(){
        new TrapRepository.DeleteAllAsyncTask(dao).execute();
    }
    public void deleteWithTscId(int id){
        new TrapRepository.DeleteWithIdAsyncTask(dao).execute(id);
    }
    public void delete(Trap... ss){
        new TrapRepository.DeleteAsyncTask(dao).execute(ss);
    }
    static class InsertAsyncTask extends AsyncTask<Trap,Void,Void> {
        private TrapDao dao;

        public InsertAsyncTask(TrapDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Trap... ss) {
            dao.insert(ss);
            return null;
        }
    }

    static class UpdateAsyncTask extends AsyncTask<Trap,Void,Void>{
        private TrapDao dao;

        public UpdateAsyncTask(TrapDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Trap... ss) {
            dao.update(ss);
            return null;
        }
    }

    static class DeleteAsyncTask extends AsyncTask<Trap,Void ,Void>{
        private TrapDao dao;

        public DeleteAsyncTask(TrapDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Trap... ss) {
            dao.delete(ss);
            return null;
        }
    }

    static class DeleteAllAsyncTask extends AsyncTask<Trap,Void,Void>{
        private TrapDao dao;

        public DeleteAllAsyncTask(TrapDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Trap... ss) {
            dao.deleteAll();
            return null;
        }
    }
    static class DeleteWithIdAsyncTask extends  AsyncTask<Integer,Void,Void>{
        private TrapDao dao;

        public DeleteWithIdAsyncTask(TrapDao dao) {
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
