package com.aiton.pestscontrolandroid.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.data.persistence.Trap;
import com.aiton.pestscontrolandroid.data.persistence.TrapRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.internal.LinkedTreeMap;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import cn.com.qiter.common.vo.PestsControlModel;
import cn.com.qiter.common.vo.PestsTrapModel;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.aiton.pestscontrolandroid.AppConstance.TAG;

public class TrapOnceWork extends Worker {
    TrapRepository repository;
    private WorkerParameters workerParams;
    private Context context;
  // PestsViewModel pestsViewModel;

    public TrapOnceWork(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.repository = new TrapRepository(context);
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
            PestsTrapModel pestsTrapModel = mapper.readValue(dataString,PestsTrapModel.class);
            if (pestsTrapModel.getPic1() != null) {
                File fellpic = new File(pestsTrapModel.getPic1());
                String filepath = ossUpload(fellpic);
                pestsTrapModel.setPic1("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
            } else {
                pestsTrapModel.setPic1("");
            }

            if (pestsTrapModel.getPic2() != null) {
                File stumpPic = new File(pestsTrapModel.getPic2());
                String filepath = ossUpload(stumpPic);
                pestsTrapModel.setPic2("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
            } else {
                pestsTrapModel.setPic2("");
            }

            uploadServer(pestsTrapModel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "doWork: =======================TrapOnceWork");
        // 反馈数据 给 MainActivity
        // 把任务中的数据回传到activity中
        Data outputData = new Data.Builder().putString(AppConstance.WORKMANAGER_KEY, "ok").build();
        Result success = Result.success(outputData);
        return success;
    }

    public void uploadServer(PestsTrapModel pestsModel) {
        RetrofitUtil.getInstance().getTrapService().save(pestsModel)
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
                                Trap p = repository.findByLatLonAndUserIdAndStime(latitude,longitude,time,userId,qrcode);
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
                        Log.e(AppConstance.TAG, "------------------------------- " + e.toString());
                    }

                    @Override
                    public void onComplete() {

                        Log.e(AppConstance.TAG, "============================== ");
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
        String appPath = "app/trap/" + filePath;
        String appFilePath = appPath + "/" + file.getName();
        Log.e(TAG, "ossUpload: "+appFilePath);
//开始上传，参数分别为content，上传的文件名filename，上传的文件路径filePath
        ossService.beginupload(context, appFilePath, file.getAbsolutePath());
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
