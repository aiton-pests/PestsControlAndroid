package com.aiton.pestscontrolandroid.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.data.model.PestsParcelable;
import com.aiton.pestscontrolandroid.data.model.PestsTrapModel;
import com.aiton.pestscontrolandroid.data.model.Result;
import com.aiton.pestscontrolandroid.data.model.TrapParcelable;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import cn.hutool.core.date.DateTime;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.aiton.pestscontrolandroid.AppConstance.TAG;

public class UploadTrapService  extends JobIntentService {
    /**
     * 这个Service 唯一的id
     */
    static final int JOB_ID = 10112;
    /**
     * Convenience method for enqueuing work in to this service.
     */

    Context context;
    public static void enqueueWork(Context context, Intent work) {
        initOss(context);
        enqueueWork(context, UploadTrapService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull @NotNull Intent intent) {
        ArrayList<TrapParcelable> parcelable = intent.getParcelableArrayListExtra("parcelable");
//        for (int i=0 ; i <1000; i++){
//            parcelable.add(parcelable.get(0));
//        }
        Log.e(TAG, "onHandleWork: "+parcelable.size());
        for (TrapParcelable p :
                parcelable) {
            Log.e(TAG, "服务器数据上传一条: " + p.toString());
            // app side update data
            PestsTrapModel pestsModel = new PestsTrapModel();
            pestsModel.setDb(p.getDb());
            pestsModel.setDeviceId(p.getDeviceId());
            pestsModel.setIsChecked(p.getIsChecked());
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
            Log.d("MyJobIntentService所在线程", Thread.currentThread().getId() + "");
        }

        Log.d("MyJobIntentService服务#", "开始工作了"+parcelable.toString());

    }

    public void uploadServer(PestsTrapModel pestsModel) {
        RetrofitUtil.getInstance().getTrapService().save(pestsModel)
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
