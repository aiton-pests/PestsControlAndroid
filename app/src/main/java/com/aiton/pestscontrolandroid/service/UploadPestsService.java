package com.aiton.pestscontrolandroid.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.data.model.PestsControlModel;
import com.aiton.pestscontrolandroid.data.model.PestsParcelable;
import com.aiton.pestscontrolandroid.data.model.Result;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import cn.hutool.core.date.DateTime;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.aiton.pestscontrolandroid.AppConstance.TAG;

public class UploadPestsService extends JobIntentService {
    public UploadPestsService() {

    }
    /**
     * 这个Service 唯一的id
     */
    static final int JOB_ID = 10111;
    /**
     * Convenience method for enqueuing work in to this service.
     */

    Context context;
    public static void enqueueWork(Context context, Intent work) {
        initOss(context);
        enqueueWork(context, UploadPestsService.class, JOB_ID, work);
    }

    public void uploadServer(PestsControlModel pestsModel) {
        RetrofitUtil.getInstance().getPestsService().savePests(pestsModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.rxjava3.core.Observer<Result>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull Result result) {
                        if (result.getSuccess()) {
                            Log.e(AppConstance.TAG, "111111111111111111111111111111" + result.toString());
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
    @Override
    protected void onHandleWork(@NonNull @NotNull Intent intent) {
        ArrayList<PestsParcelable> parcelable = intent.getParcelableArrayListExtra("parcelable");
//        for (int i=0 ; i <1000; i++){
//            parcelable.add(parcelable.get(0));
//        }
        Log.e(TAG, "onHandleWork: "+parcelable.size());
        for (PestsParcelable p :
                parcelable) {
            Log.e(TAG, "服务器数据上传一条: " + p.toString());
            // app side update data
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
            Log.d("MyJobIntentService所在线程", Thread.currentThread().getId() + "");
        }

        Log.d("MyJobIntentService服务#", "开始工作了"+parcelable.toString());


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
        ossService.beginupload(getApplication().getApplicationContext(), appFilePath, file.getAbsolutePath());
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