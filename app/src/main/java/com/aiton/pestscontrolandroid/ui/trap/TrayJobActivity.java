package com.aiton.pestscontrolandroid.ui.trap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.model.PestsParcelable;
import com.aiton.pestscontrolandroid.data.model.Result;
import com.aiton.pestscontrolandroid.data.model.TrapParcelable;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.data.persistence.Trap;
import com.aiton.pestscontrolandroid.service.OssService;
import com.aiton.pestscontrolandroid.service.PestsAllOnceWork;
import com.aiton.pestscontrolandroid.service.PestsWork;
import com.aiton.pestscontrolandroid.service.RetrofitUtil;
import com.aiton.pestscontrolandroid.service.TrapAllOnceWork;
import com.aiton.pestscontrolandroid.service.TrapWork;
import com.aiton.pestscontrolandroid.service.UploadPestsService;
import com.aiton.pestscontrolandroid.service.UploadTrapService;
import com.aiton.pestscontrolandroid.ui.myjob.MyJobActivity;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TrayJobActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TrapViewModel trapViewModel;
    private Button trapUpdate,trapUpdateAgain,trapDelete;
    TrapAdapter adapter;
    private static final String TAG = "TrayJobActivity";
    ProgressBar progressBar;
    WorkManager workmanager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tray_job);
        workmanager = WorkManager.getInstance(this);
        trapUpdate = findViewById(R.id.trap_update);
        trapUpdateAgain = findViewById(R.id.trap_update_again);
        trapDelete = findViewById(R.id.trap_delete_btn);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.rv_trap);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        trapViewModel = new ViewModelProvider(this).get(TrapViewModel.class);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        adapter = new TrapAdapter(trapViewModel);
        recyclerView.setAdapter(adapter);
        trapViewModel.getProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer.compareTo(100) == 0){
                    Toast.makeText(getApplicationContext(),"????????????",Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
            }
        });
        trapViewModel.findAll().observe(this, new Observer<List<Trap>>() {
            @Override
            public void onChanged(List<Trap> traps) {
                if (traps !=null){
                    int temp = adapter.getItemCount();
                    adapter.setTraps(traps);
                    if (traps.size() != temp) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        LiveEventBus
                .get(AppConstance.TRAP_ALL_ONCE_WORK_NOTIFICATION, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Log.e(TAG, "onChanged: " + s);
                        adapter.notifyDataSetChanged();
                    }
                });
        trapUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ??????
                Data data = new Data.Builder().putString(AppConstance.WORKMANAGER_KEY, "????????????").build();
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();
                OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(TrapAllOnceWork.class)
                        .setInputData(data) // ???????????????
                        .setConstraints(constraints)
                        .build();
// ???????????????  ????????????????????? ENQUEUE????????? ?????????????????????????????????????????? SUCCESS     [???????????????????????????????????????SUCCESS]
                // ????????????
                workmanager.getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                        .observe(TrayJobActivity.this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                Log.d(AppConstance.TAG, "?????????" + workInfo.getState().name()); // ENQUEEN   SUCCESS
                                if (workInfo.getState().isFinished()) {
                                    Log.d(AppConstance.TAG, "?????????isFinished=true ????????????????????????????????????...");
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                workmanager.enqueue(oneTimeWorkRequest);

//                RetrofitUtil.getInstance().getPestsService().aLive()
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new io.reactivex.rxjava3.core.Observer<Result>() {
//                            Disposable disposable;
//                            @Override
//                            public void onSubscribe(@NonNull Disposable d) {
//                                disposable = d;
//                            }
//
//                            @Override
//                            public void onNext(@NonNull Result result) {
//                                if (result.getSuccess())
//                                    updateServer(false);
//                            }
//
//                            @Override
//                            public void onError(@NonNull Throwable e) {
//
//                                disposable.dispose();
//                                Toast.makeText(getApplicationContext(),"????????????????????????????????????",Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });
            }
        });
        trapUpdateAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trap[] traps1 = trapViewModel.findAllObject();
                for (Trap t :
                        traps1) {
//                    Log.e(TAG, "onClick: " + t.toString());
                }
//                RetrofitUtil.getInstance().getPestsService().aLive()
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new io.reactivex.rxjava3.core.Observer<Result>() {
//                            Disposable disposable;
//                            @Override
//                            public void onSubscribe(@NonNull Disposable d) {
//                                disposable = d;
//                            }
//
//                            @Override
//                            public void onNext(@NonNull Result result) {
//                                if (result.getSuccess()){
//                                    updateServer(true);
//                                }
//
//                            }
//
//                            @Override
//                            public void onError(@NonNull Throwable e) {
//                                disposable.dispose();
//                                Toast.makeText(getApplicationContext(),"????????????????????????????????????",Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });
            }
        });
        trapDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteByUpdate(true);
            }
        });
    }

    public String ossUpload(File file) {
        //?????????OssService?????????????????????Content???accessKeyId???accessKeySecret???endpoint???bucketName??????4??????????????????????????????Oss????????????
        OssService ossService = new OssService(getApplication(), AppConstance.ACCESS_KEY_ID, AppConstance.ACCESS_KEY_SECRET, AppConstance.ENDPOINT, AppConstance.BUCKETNAME);
//?????????OSSClient
        ossService.initOSSClient();
        String filePath = new DateTime().toString("yyyy/MM/dd");
        String appPath = "app/trap/" + filePath;
        String appFilePath = appPath + "/" + file.getName();
//??????????????????????????????content?????????????????????filename????????????????????????filePath
        ossService.beginupload(getApplication().getApplicationContext(), appFilePath, file.getAbsolutePath());
//?????????????????????
        ossService.setProgressCallback(new OssService.ProgressCallback() {
            @Override
            public void onProgressCallback(double progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        progressBar.setProgress((int)progress,true);
//                        Log.e(TAG, "run: " + progress);
                    }
                });
            }
        });
        return appFilePath;
    }
    private void deleteByUpdate(boolean isUpdate) {
        Trap[] traps = trapViewModel.findAllObject(isUpdate);
        for (Trap p :
                traps) {
            trapViewModel.delete(p);
        }
    }
    private void updateServer(boolean isUpdate) {
        Trap[] pests = trapViewModel.findAllObject(isUpdate);
        Intent work  = new Intent();
        ArrayList<TrapParcelable> list = new ArrayList();

        for (Trap p :
                pests) {
            //  server side save a new record
            TrapParcelable parcelable = new TrapParcelable();
            parcelable.setCodeInt(p.getCodeInt());
            parcelable.setDb(p.getDb());
            parcelable.setDeviceId(p.getDeviceId());
            parcelable.setChecked(p.isChecked());
            parcelable.setLatitude(p.getLatitude());
            parcelable.setLongitude(p.getLongitude());
            parcelable.setId(p.getId());
            parcelable.setLureReplaced(p.getLureReplaced());
            parcelable.setOperator(p.getOperator());
            parcelable.setPic1(p.getPic1());
            parcelable.setPic2(p.getPic2());
            parcelable.setPositionError(p.getPositionError());
            parcelable.setProjectId(p.getProjectId());
            parcelable.setQrcode(p.getQrcode());
            parcelable.setRemark(p.getRemark());
            parcelable.setScount(p.getScount());
            parcelable.setStime(p.getStime());
            parcelable.setTown(p.getTown());
            parcelable.setUpdateServer(p.isUpdateServer());
            parcelable.setUserId(p.getUserId());
            parcelable.setVillage(p.getVillage());
            parcelable.setXb(p.getXb());
            list.add(parcelable);
            p.setUpdateServer(true);
            trapViewModel.update(p);
//            Log.e(TAG, "updateServer: " + p.toString());
           // adapter.notifyDataSetChanged();
        }
//        trapViewModel.getProgress().setValue(100);
        work.putParcelableArrayListExtra("parcelable",list);
        UploadTrapService.enqueueWork(getApplicationContext(), work);
        adapter.notifyDataSetChanged();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //??????/??????/???????????????
            Toast.makeText(getApplicationContext(),"??????????????????????????????",Toast.LENGTH_SHORT).show();
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_MENU) {//MENU???
            //??????/???????????????
            Toast.makeText(getApplicationContext(),"??????????????????????????????",Toast.LENGTH_SHORT).show();
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_HOME) {//MENU???
            //??????/???????????????
            Toast.makeText(getApplicationContext(),"?????????????????????HOME???",Toast.LENGTH_SHORT).show();
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_POWER) {//MENU???
            //??????/???????????????
            Toast.makeText(getApplicationContext(),"??????????????????????????????",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}