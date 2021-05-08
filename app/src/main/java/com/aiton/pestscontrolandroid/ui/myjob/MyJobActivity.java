package com.aiton.pestscontrolandroid.ui.myjob;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.model.ShpFile;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.service.OssService;
import com.aiton.pestscontrolandroid.service.RetrofitUtil;
import com.aiton.pestscontrolandroid.ui.pests.PestsViewModel;
import com.aiton.pestscontrolandroid.ui.setting.ShpAdapter;

import java.io.File;
import java.util.List;

import cn.com.qiter.common.Result;
import cn.com.qiter.common.vo.PestsControlModel;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MyJobActivity extends AppCompatActivity {
    private static final String TAG = "MyJobActivity";
    private RecyclerView recyclerView;
    private PestsViewModel pestsViewModel;
    PestsAdapter adapter;
    ProgressBar progressBar2;
    private Button pestsUpdate,pestsUpdateAgain,pestsDelete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_job);
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
                    adapter.notifyDataSetChanged();
                }else{

                }
            }
        });
        pestsUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitUtil.getInstance().getPestsService().aLive()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new io.reactivex.rxjava3.core.Observer<Result>() {
                            Disposable disposable;
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                disposable = d;
                            }

                            @Override
                            public void onNext(@NonNull Result result) {
                                if (result.getSuccess())
                                    updateServer(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {

                                disposable.dispose();
                                Toast.makeText(getApplicationContext(),"网络异常无法连接服务器！",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });

            }
        });
        pestsUpdateAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitUtil.getInstance().getPestsService().aLive()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new io.reactivex.rxjava3.core.Observer<Result>() {
                            Disposable disposable;
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                disposable = d;
                            }

                            @Override
                            public void onNext(@NonNull Result result) {
                                if (result.getSuccess())
                                    updateServer(true);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {

                                disposable.dispose();
                                Toast.makeText(getApplicationContext(),"网络异常无法连接服务器！",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });

            }
        });
        pestsDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePestsByUpdate(true);
            }
        });
    }

    public String ossUpload(File file) {
        //初始化OssService类，参数分别是Content，accessKeyId，accessKeySecret，endpoint，bucketName（后4个参数是您自己阿里云Oss中参数）
        OssService ossService = new OssService(getApplication(), AppConstance.ACCESS_KEY_ID, AppConstance.ACCESS_KEY_SECRET, AppConstance.ENDPOINT, AppConstance.BUCKETNAME);
//初始化OSSClient
        ossService.initOSSClient();
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
                        progressBar2.setProgress((int)progress,true);
                        Log.e(TAG, "run: " + progress);
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
        for (Pests p :
                pests) {
            p.setUpdateServer(true);
            pestsViewModel.update(p);

            Log.e(TAG, "服务器数据上传一条: " + pests.toString());
            PestsControlModel pestsModel = new PestsControlModel();
            pestsModel.setQrcode(p.getQrcode());
            pestsModel.setAppId(p.getId());
            pestsModel.setBagNumber(p.getBagNumber());
            pestsModel.setDb(p.getDb());
            pestsModel.setXb(p.getXb());
            pestsModel.setDeviceId(p.getDeviceId());
            if (p.getFellPic() != null) {
                File fellpic = new File(p.getFellPic());
                String filepath = ossUpload(fellpic);
                pestsModel.setFellPic("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
            } else {
                pestsModel.setFellPic("");
            }

            if (p.getStumpPic() != null) {
                File stumpPic = new File(p.getStumpPic());
                String filepath = ossUpload(stumpPic);
                pestsModel.setStumpPic("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
            } else {
                pestsModel.setStumpPic("");
            }
            if (p.getFinishPic() != null) {
                File finishPic = new File(p.getFinishPic());
                String filepath = ossUpload(finishPic);
                pestsModel.setFinishPic("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
            } else {
                pestsModel.setFinishPic("");
            }
            pestsModel.setLatitude(p.getLatitude());
            pestsModel.setLongitude(p.getLongitude());
            pestsModel.setOperator(p.getOperator());
            pestsModel.setPestsType(p.getPestsType());
            pestsModel.setPositionError(p.getPositionError());
            pestsModel.setTown(p.getTown());
            pestsModel.setTreeWalk(p.getTreeWalk());
            pestsModel.setUserId(p.getUserId());
            pestsModel.setVillage(p.getVillage());
            pestsModel.setStime(p.getStime());
            pestsViewModel.uploadServer(pestsModel);
            Log.e(TAG, "updateServer: " + p.toString());
        }
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