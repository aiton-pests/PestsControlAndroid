package com.aiton.pestscontrolandroid.ui.myjob;

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
import androidx.work.WorkRequest;

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
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.data.persistence.Trap;
import com.aiton.pestscontrolandroid.service.OssService;
import com.aiton.pestscontrolandroid.service.PestsAllOnceWork;
import com.aiton.pestscontrolandroid.service.PestsOnceWork;
import com.aiton.pestscontrolandroid.service.PestsWork;
import com.aiton.pestscontrolandroid.service.RetrofitUtil;
import com.aiton.pestscontrolandroid.service.UploadPestsService;
import com.aiton.pestscontrolandroid.ui.pests.PestsViewModel;
import com.jeremyliao.liveeventbus.LiveEventBus;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.date.DateTime;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlinx.coroutines.Job;

public class MyJobActivity extends AppCompatActivity {
    private static final String TAG = "MyJobActivity";
    private RecyclerView recyclerView;
    private PestsViewModel pestsViewModel;
    PestsAdapter adapter;
    ProgressBar progressBar2;
    private Button pestsUpdate,pestsUpdateAgain,pestsDelete;
    //add method
    WorkManager workmanager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_job);

        workmanager = WorkManager.getInstance(this);
        initOss();
//        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
        pestsUpdate = findViewById(R.id.pests_update);
        pestsUpdateAgain = findViewById(R.id.pests_update_again);
        pestsDelete = findViewById(R.id.pests_delete);
        recyclerView = findViewById(R.id.rv_pests);
        progressBar2 = findViewById(R.id.progressBar2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pestsViewModel = new ViewModelProvider(this).get(PestsViewModel.class);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        adapter = new PestsAdapter(pestsViewModel);
        recyclerView.setAdapter(adapter);

        pestsViewModel.findAll().observe(this, new Observer<List<Pests>>() {
            @Override
            public void onChanged(List<Pests> pests) {
                if (pests !=null){
                    int temp = adapter.getItemCount();
                    adapter.setPests(pests);
                    if (pests.size() != temp) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        pestsViewModel.getProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer.compareTo(100) == 0){
                    Toast.makeText(getApplicationContext(), "上传完成", Toast.LENGTH_SHORT).show();
//                    Pests[] rr = pestsViewModel.findAllObject();
//                    for (Pests pp :
//                            rr) {
//                        Log.e(TAG, "onChanged: "+pp.toString());
//                    }
                    adapter.notifyDataSetChanged();
                }else{

                }
            }
        });
        LiveEventBus
                .get(AppConstance.PESTS_ALL_ONCE_WORK_NOTIFICATION, String.class)
                .observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String s) {
                        Log.e(TAG, "onChanged: " + s);
                        adapter.notifyDataSetChanged();
                    }
                });

        pestsUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 数据
                Data data = new Data.Builder().putString(AppConstance.WORKMANAGER_KEY, "数据传递").build();
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();
//                PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest
//                        .Builder(PestsWork.class,3, TimeUnit.SECONDS)
//                        .setConstraints(constraints)
//                        .setInputData(data)
//                        .build();
                OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(PestsAllOnceWork.class)
                        .setInputData(data) // 数据的携带
                        .setConstraints(constraints)
                        .build();
// 【状态机】  为什么一直都是 ENQUEUE，因为 你是轮询的任务，所以你看不到 SUCCESS     [如果你是单个任务，就会看到SUCCESS]
                // 监听状态
                workmanager.getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                        .observe(MyJobActivity.this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                Log.d(AppConstance.TAG, "状态：" + workInfo.getState().name()); // ENQUEEN   SUCCESS
                                if (workInfo.getState().isFinished()) {
                                    Log.d(AppConstance.TAG, "状态：isFinished=true 注意：后台任务已经完成了...");
                                   // adapter.notifyDataSetChanged();
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
//                                Toast.makeText(getApplicationContext(),"网络异常无法连接服务器！",Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });

            }
        });
        pestsUpdateAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pests[] traps1 = pestsViewModel.findAllObject();
                for (Pests t :
                        traps1) {
                    Log.e(TAG, "onClick: " + t.toString());
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
//                                if (result.getSuccess())
//                                    updateServer(true);
//                            }
//
//                            @Override
//                            public void onError(@NonNull Throwable e) {
//
//                                disposable.dispose();
//                                Toast.makeText(getApplicationContext(),"网络异常无法连接服务器！",Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onComplete() {
//
//                            }
//                        });

            }
        });
        pestsDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MyJobActivity.this, UploadPestsService.class));
                deletePestsByUpdate(true);
            }
        });
    }
    OssService ossService;
    public void initOss(){
        //初始化OssService类，参数分别是Content，accessKeyId，accessKeySecret，endpoint，bucketName（后4个参数是您自己阿里云Oss中参数）
        ossService = new OssService(getApplication(), AppConstance.ACCESS_KEY_ID, AppConstance.ACCESS_KEY_SECRET, AppConstance.ENDPOINT, AppConstance.BUCKETNAME);
//初始化OSSClient
        ossService.initOSSClient();
    }
    public String ossUpload(File file) {

        String filePath = new DateTime().toString("yyyy/MM/dd");
        String appPath = "app/pests/" + filePath;
        String appFilePath = appPath + "/" + file.getName();
//开始上传，参数分别为content，上传的文件名filename，上传的文件路径filePath
        ossService.beginupload(getApplication().getApplicationContext(), appFilePath, file.getAbsolutePath());
//上传的进度回调
        ossService.setProgressCallback(new OssService.ProgressCallback() {
            @Override
            public void onProgressCallback(double progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        progressBar2.setProgress((int)progress,true);
//                        Log.e(TAG, "run: " + progress);
                    }
                });
            }
        });
        return appFilePath;
    }
    private void deletePestsByUpdate(boolean isUpdate) {
        Pests[] pests = pestsViewModel.findAllObject(isUpdate);
        for (Pests p :
                pests) {
            pestsViewModel.delete(p);
        }
    }
    private void updateServer(boolean isUpdate) {
        Pests[] pests = pestsViewModel.findAllObject(isUpdate);
        Intent work  = new Intent();
        ArrayList<PestsParcelable> list = new ArrayList();

        for (Pests p :
                pests) {
            // UploadPestsService    server side save a new record
            PestsParcelable parcelable = new PestsParcelable();
            parcelable.setBagNumber(p.getBagNumber());
            parcelable.setCodeInt(p.getCodeInt());
            parcelable.setDb(p.getDb());
            parcelable.setDeviceId(p.getDeviceId());
            parcelable.setFellPic(p.getFellPic());
            parcelable.setFinishPic(p.getFinishPic());
            parcelable.setId(p.getId());
            parcelable.setLatitude(p.getLatitude());
            parcelable.setLongitude(p.getLongitude());
            parcelable.setOperator(p.getOperator());
            parcelable.setPestsType(p.getPestsType());
            parcelable.setPositionError(p.getPositionError());
            parcelable.setQrcode(p.getQrcode());
            parcelable.setStime(p.getStime());
            parcelable.setStumpPic(p.getStumpPic());
            parcelable.setTown(p.getTown());
            parcelable.setTreeWalk(p.getTreeWalk());
            parcelable.setUpdateServer(p.isUpdateServer());
            parcelable.setUserId(p.getUserId());
            parcelable.setVillage(p.getVillage());
            parcelable.setXb(p.getXb());
            list.add(parcelable);
            p.setUpdateServer(true);
            pestsViewModel.update(p);
            Log.e(TAG, "updateServer: " + p.toString());
        }
//        pestsViewModel.getProgress().setValue(100);
        work.putParcelableArrayListExtra("parcelable",list);
        UploadPestsService.enqueueWork(getApplicationContext(), work);
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
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            Toast.makeText(getApplicationContext(),"数据上传中禁用退回键",Toast.LENGTH_SHORT).show();
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_MENU) {//MENU键
            //监控/拦截菜单键
            Toast.makeText(getApplicationContext(),"数据上传中禁用菜单键",Toast.LENGTH_SHORT).show();
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_HOME) {//MENU键
            //监控/拦截菜单键
            Toast.makeText(getApplicationContext(),"数据上传中禁用HOME键",Toast.LENGTH_SHORT).show();
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_POWER) {//MENU键
            //监控/拦截菜单键
            Toast.makeText(getApplicationContext(),"数据上传中禁用电源键",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}