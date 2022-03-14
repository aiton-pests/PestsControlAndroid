package com.aiton.pestscontrolandroid.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.data.persistence.Trap;
import com.aiton.pestscontrolandroid.data.persistence.TrapRepository;
import com.google.gson.internal.LinkedTreeMap;
import com.jeremyliao.liveeventbus.LiveEventBus;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import cn.com.qiter.common.vo.PestsTrapModel;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.aiton.pestscontrolandroid.AppConstance.TAG;

public class TrapAllOnceWork extends Worker {
    TrapRepository repository;
    private WorkerParameters workerParams;
    private Context context;
  // PestsViewModel pestsViewModel;

    public TrapAllOnceWork(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.repository = new TrapRepository(context);
        //pestsViewModel = new ViewModelProvider(context.getApplicationContext()).get(PestsViewModel.class);
        this.workerParams = workerParams;
        initOss(context);
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        String dataString = workerParams.getInputData().getString(AppConstance.WORKMANAGER_KEY);
        Trap[] pests = repository.findAllObject(false);
        for (Trap p :
                pests) {
//            Log.e(TAG, "doWork: " + p.toString());
            PestsTrapModel pestsModel = new PestsTrapModel();
            pestsModel.setDb(p.getDb());
            pestsModel.setAppId(p.getId());
            pestsModel.setDeviceId(p.getDeviceId());
            pestsModel.setIsChecked(p.isChecked());
            pestsModel.setLatitude(p.getLatitude());
            pestsModel.setLongitude(p.getLongitude());
            pestsModel.setLureReplaced(p.getLureReplaced());
            if (p.getPic1() != null) {
                File fellpic = new File(p.getPic1());
                String filepath = ossUpload(fellpic);
                pestsModel.setPic1("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
            } else {
                pestsModel.setPic1("");
            }

            if (p.getPic2() != null) {
                File stumpPic = new File(p.getPic2());
                String filepath = ossUpload(stumpPic);
                pestsModel.setPic2("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
            } else {
                pestsModel.setPic2("");
            }
            pestsModel.setOperator(p.getOperator());
            pestsModel.setPositionError(p.getPositionError());
            pestsModel.setProjectId(p.getProjectId());
            pestsModel.setQrcode(p.getQrcode());
            pestsModel.setRemark(p.getRemark());
            pestsModel.setScount(p.getScount());
            pestsModel.setStime(p.getStime());
            pestsModel.setTown(p.getTown());
            pestsModel.setUserId(p.getUserId());
            pestsModel.setVillage(p.getVillage());
            pestsModel.setXb(p.getXb());
            uploadServer(pestsModel);
        }
        Log.e(TAG, "doWork: =======================TrapAllOnceWork");
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
//                            Log.e(AppConstance.TAG, "onNext: " + result.toString());
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
                                LiveEventBus.get(AppConstance.TRAP_ALL_ONCE_WORK_NOTIFICATION).postDelay(AppConstance.OK,3000);
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
