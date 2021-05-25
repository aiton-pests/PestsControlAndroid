package com.aiton.pestscontrolandroid.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.data.persistence.PestsRepository;
import com.google.gson.internal.LinkedTreeMap;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import cn.com.qiter.common.vo.PestsControlModel;
import cn.hutool.core.date.DateTime;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.aiton.pestscontrolandroid.AppConstance.TAG;

public class PestsOnceWork extends Worker {
    PestsRepository repository;
    private WorkerParameters workerParams;
    private Context context;
  // PestsViewModel pestsViewModel;

    public PestsOnceWork(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
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
        String dataString = workerParams.getInputData().getString("key");
        Pests[] pests = repository.findAllObject(false);
        for (Pests p :
                pests) {
            Log.e(TAG, "doWork: " + p.toString());
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
            uploadServer(pestsModel);
        }
        Log.e(TAG, "doWork: =======================" + dataString);
        // 反馈数据 给 MainActivity
        // 把任务中的数据回传到activity中
        Data outputData = new Data.Builder().putString("success", "ok").build();
        Result success = Result.success(outputData);
        return success;
    }


    public void uploadServer(PestsControlModel pestsModel) {
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
                            Log.e(AppConstance.TAG, "onNext: " + result.toString());
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