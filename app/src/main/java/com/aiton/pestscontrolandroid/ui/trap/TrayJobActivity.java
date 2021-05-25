package com.aiton.pestscontrolandroid.ui.trap;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
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
import com.aiton.pestscontrolandroid.service.PestsWork;
import com.aiton.pestscontrolandroid.service.RetrofitUtil;
import com.aiton.pestscontrolandroid.service.TrapWork;
import com.aiton.pestscontrolandroid.service.UploadPestsService;
import com.aiton.pestscontrolandroid.service.UploadTrapService;
import com.aiton.pestscontrolandroid.ui.myjob.MyJobActivity;

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
                    Toast.makeText(getApplicationContext(),"上传完成",Toast.LENGTH_SHORT).show();
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
        trapUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 数据
                Data data = new Data.Builder().putString("key", "数据传递").build();
                Constraints constraints = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();
                PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest
                        .Builder(TrapWork.class,3, TimeUnit.SECONDS)
                        .setConstraints(constraints)
                        .setInputData(data)
                        .build();
// 【状态机】  为什么一直都是 ENQUEUE，因为 你是轮询的任务，所以你看不到 SUCCESS     [如果你是单个任务，就会看到SUCCESS]
                // 监听状态
                workmanager.getWorkInfoByIdLiveData(periodicWorkRequest.getId())
                        .observe(TrayJobActivity.this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                Log.d(AppConstance.TAG, "状态：" + workInfo.getState().name()); // ENQUEEN   SUCCESS
                                if (workInfo.getState().isFinished()) {
                                    Log.d(AppConstance.TAG, "状态：isFinished=true 注意：后台任务已经完成了...");
                                }
                            }
                        });
                workmanager.enqueue(periodicWorkRequest);
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
        trapUpdateAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trap[] traps1 = trapViewModel.findAllObject();
                for (Trap t :
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
//                                if (result.getSuccess()){
//                                    updateServer(true);
//                                }
//
//                            }
//
//                            @Override
//                            public void onError(@NonNull Throwable e) {
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
        trapDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteByUpdate(true);
            }
        });
    }

    public String ossUpload(File file) {
        //初始化OssService类，参数分别是Content，accessKeyId，accessKeySecret，endpoint，bucketName（后4个参数是您自己阿里云Oss中参数）
        OssService ossService = new OssService(getApplication(), AppConstance.ACCESS_KEY_ID, AppConstance.ACCESS_KEY_SECRET, AppConstance.ENDPOINT, AppConstance.BUCKETNAME);
//初始化OSSClient
        ossService.initOSSClient();
        String filePath = new DateTime().toString("yyyy/MM/dd");
        String appPath = "app/trap/" + filePath;
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
            Log.e(TAG, "updateServer: " + p.toString());
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