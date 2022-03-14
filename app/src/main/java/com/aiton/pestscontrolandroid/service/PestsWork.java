package com.aiton.pestscontrolandroid.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.data.model.Result;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.data.persistence.PestsRepository;
import com.aiton.pestscontrolandroid.ui.pests.PestsViewModel;
import com.google.gson.internal.LinkedTreeMap;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.com.qiter.common.vo.PestsControlModel;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.aiton.pestscontrolandroid.AppConstance.TAG;

public class PestsWork extends Worker {
    PestsRepository repository;
    private WorkerParameters workerParams;
    private Context context;
  // PestsViewModel pestsViewModel;

    public PestsWork(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.repository = new PestsRepository(context);
        //pestsViewModel = new ViewModelProvider(context.getApplicationContext()).get(PestsViewModel.class);
        this.workerParams = workerParams;
        initOss(context);
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        String dataString = workerParams.getInputData().getString(AppConstance.WORKMANAGER_KEY);
        Pests[] pests = repository.findAllObject();
        for (Pests p :
                pests) {
//            Log.e(TAG, "doWork: " + p.toString());
            PestsControlModel pestsModel = new PestsControlModel();
            pestsModel.setQrcode(p.getQrcode());
            pestsModel.setAppId(p.getId());
            pestsModel.setBagNumber(p.getBagNumber());
            pestsModel.setDb(p.getDb());
            pestsModel.setXb(p.getXb());
            pestsModel.setDeviceId(p.getDeviceId());
            pestsModel.setStumpPic(p.getStumpPic());
            pestsModel.setFinishPic(p.getFinishPic());
            pestsModel.setFellPic(p.getFellPic());
//            if (p.getFellPic() != null) {
//                File fellpic = new File(p.getFellPic());
//                String filepath = ossUpload(fellpic);
//                pestsModel.setFellPic("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
//            } else {
//                pestsModel.setFellPic("");
//            }
//
//            if (p.getStumpPic() != null) {
//                File stumpPic = new File(p.getStumpPic());
//                String filepath = ossUpload(stumpPic);
//                pestsModel.setStumpPic("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
//            } else {
//                pestsModel.setStumpPic("");
//            }
//            if (p.getFinishPic() != null) {
//                File finishPic = new File(p.getFinishPic());
//                String filepath = ossUpload(finishPic);
//                pestsModel.setFinishPic("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
//            } else {
//                pestsModel.setFinishPic("");
//            }
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
//            uploadServer(pestsModel);
            checkUpload(pestsModel);
        }
        Log.e(TAG, "doWork: =======================PestsWork  " + dataString);
        // 反馈数据 给 MainActivity
        // 把任务中的数据回传到activity中
        Data outputData = new Data.Builder().putString(AppConstance.WORKMANAGER_KEY, "ok").build();
        Result success = Result.success(outputData);
        return success;
    }
    public void checkUpload(PestsControlModel pestsModel){

        RetrofitUtil.getInstance().getPestsService().checkUpload(pestsModel.getQrcode(),pestsModel.getLatitude(),pestsModel.getLongitude(),pestsModel.getUserId(),pestsModel.getStime())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<com.aiton.pestscontrolandroid.data.model.Result>() {
                    Disposable disposable;
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(com.aiton.pestscontrolandroid.data.model.@io.reactivex.rxjava3.annotations.NonNull Result result) {
                        //后台记录是否有
                        if (result.getSuccess()) {
//                            Log.e(AppConstance.TAG, "onNext: " + result.toString());
                            // repository.update();
                            Double longitude = (Double) ((LinkedTreeMap)result.getData().get("row")).get("longitude");
                            Double latitude = (Double) ((LinkedTreeMap)result.getData().get("row")).get("latitude");
                            String userId = (String) ((LinkedTreeMap)result.getData().get("row")).get("userId");
                            String qrcode = (String) ((LinkedTreeMap)result.getData().get("row")).get("qrcode");
                            Double stime = (Double) ((LinkedTreeMap)result.getData().get("row")).get("stime");
                            DateTime ddd = DateUtil.date(stime.longValue());
                            DateTime dt = DateUtil.offsetHour(ddd,-8);
                            String time = DateUtil.format( dt,"yyyy-MM-dd HH:mm:ss");
                            Pests p = repository.findByLatLonAndUserIdAndStime(latitude,longitude,time,userId,qrcode);
                            p.setUpdateServer(true);
                            repository.update(p);
                        }else{
//                            Log.e(AppConstance.TAG, "onNext: " + result.toString());
                            try {
                                //PestsControlModel pestsModel = new PestsControlModel();
                                uploadServer(pestsModel);
                            }catch (Exception e){
                                Log.e(TAG, "onNext: " + e.toString() );
                            }
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void uploadServer(PestsControlModel pestsModel) {
        if (pestsModel.getFellPic() != null) {
            File fellpic = new File(pestsModel.getFellPic());
            String filepath = ossUpload(fellpic);
            pestsModel.setFellPic("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
        } else {
            pestsModel.setFellPic("");
        }

        if (pestsModel.getStumpPic() != null) {
            File stumpPic = new File(pestsModel.getStumpPic());
            String filepath = ossUpload(stumpPic);
            pestsModel.setStumpPic("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
        } else {
            pestsModel.setStumpPic("");
        }
        if (pestsModel.getFinishPic() != null) {
            File finishPic = new File(pestsModel.getFinishPic());
            String filepath = ossUpload(finishPic);
            pestsModel.setFinishPic("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
        } else {
            pestsModel.setFinishPic("");
        }
        RetrofitUtil.getInstance().getPestsService().savePests(pestsModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.rxjava3.core.Observer<com.aiton.pestscontrolandroid.data.model.Result>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull com.aiton.pestscontrolandroid.data.model.Result result) {
                        if (result.getSuccess()) {
//                            Log.e(AppConstance.TAG, "onNext: " + result.toString());
                           // repository.update();
                            try {
                                Double id = (Double) ((LinkedTreeMap)result.getData().get("row")).get("appId");
                                Pests p = repository.findById(id.intValue());
                                p.setUpdateServer(true);
                                repository.update(p);
                            }catch (Exception e){
                                Log.e(TAG, "onNext: " + e.toString() );
                            }
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
    public static OssService ossService;
    public static void initOss(Context ctx){

        //初始化OssService类，参数分别是Content，accessKeyId，accessKeySecret，endpoint，bucketName（后4个参数是您自己阿里云Oss中参数）
        ossService = new OssService(ctx, AppConstance.ACCESS_KEY_ID, AppConstance.ACCESS_KEY_SECRET, AppConstance.ENDPOINT, AppConstance.BUCKETNAME);
//初始化OSSClient
        ossService.initOSSClient();
    }
    public String ossUpload(File file) {
        Log.e(TAG, "ossUpload: "+file.getAbsolutePath());
        String filePath = new DateTime().toString("yyyy/MM/dd");
        String appPath = "app/pests/" + filePath;
        String appFilePath = appPath + "/" + file.getName();
        Log.e(TAG, "ossUpload: "+appFilePath);
//开始上传，参数分别为content，上传的文件名filename，上传的文件路径filePath
        ossService.beginupload(context.getApplicationContext(), appFilePath, file.getAbsolutePath());
//上传的进度回调
        ossService.setProgressCallback(new OssService.ProgressCallback() {
            @Override
            public void onProgressCallback(double progress) {
//                Log.e(TAG, "run: " + progress);
            }
        });
        return appFilePath;
    }
}
