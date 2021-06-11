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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.internal.LinkedTreeMap;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import cn.com.qiter.common.vo.PestsControlModel;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
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

    private static ObjectMapper mapper = new ObjectMapper();
    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        String dataString = workerParams.getInputData().getString(AppConstance.WORKMANAGER_KEY);
        try {
            PestsControlModel pestsControlModel = mapper.readValue(dataString,PestsControlModel.class);
            if (pestsControlModel.getFellPic() != null) {
                File fellpic = new File(pestsControlModel.getFellPic());
                String filepath = ossUpload(fellpic);
                pestsControlModel.setFellPic("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
            } else {
                pestsControlModel.setFellPic("");
            }

            if (pestsControlModel.getStumpPic() != null) {
                File stumpPic = new File(pestsControlModel.getStumpPic());
                String filepath = ossUpload(stumpPic);
                pestsControlModel.setStumpPic("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
            } else {
                pestsControlModel.setStumpPic("");
            }
            if (pestsControlModel.getFinishPic() != null) {
                File finishPic = new File(pestsControlModel.getFinishPic());
                String filepath = ossUpload(finishPic);
                pestsControlModel.setFinishPic("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
            } else {
                pestsControlModel.setFinishPic("");
            }
            uploadServer(pestsControlModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "doWork: =======================PestsOnceWork " + dataString);
        // 反馈数据 给 MainActivity
        // 把任务中的数据回传到activity中
        Data outputData = new Data.Builder().putString(AppConstance.WORKMANAGER_KEY, "ok").build();
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
