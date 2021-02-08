package com.aiton.pestscontrolandroid.utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraXManager {

    private Context context;
    private PreviewView previewView;
    private Executor executor = Executors.newSingleThreadExecutor();
    private ImageCapture imageCapture;
    private TakePicBack takePicBack;
    private Camera camera;

    public void setTakePicBack(TakePicBack takePicBack) {
        this.takePicBack = takePicBack;
    }

    public CameraXManager(Context context, PreviewView previewView) {
        this.context = context;
        this.previewView = previewView;
    }


    public void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(context));
    }

    @SuppressLint("WrongConstant")
    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        cameraProvider.unbindAll();

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        camera = cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, preview);

        if(previewView == null){
            imageCapture =
                    new ImageCapture.Builder()
                            .setFlashMode(ImageCapture.FLASH_MODE_AUTO)   // 设置闪光灯模式
                            .setTargetRotation(0)
                            .build();
        }else{
            imageCapture =
                    new ImageCapture.Builder()
                            .setFlashMode(ImageCapture.FLASH_MODE_AUTO)   // 设置闪光灯模式
                            .setTargetRotation(previewView.getDisplay().getRotation())
                            .build();
        }

        imageCapture.setFlashMode(ImageCapture.FLASH_MODE_ON);

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        .setTargetResolution(new Size(previewView.getWidth(), previewView.getHeight()))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

        cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, imageCapture, imageAnalysis, preview);
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    public void takePicture() {
        imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy im) {
                Bitmap bitmap = BitmapUtil.imageToBitMap(im.getImage());
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (takePicBack != null) {
                            takePicBack.takePicBack(bitmap);
                        }
                    }
                });
                super.onCaptureSuccess(im);
            }
        });
    }


    public void tun(){


    }


    public void cameraDestroy(){
        Log.d("KnightDuke","cameraDestroy");
        if(previewView!=null){
            previewView = null;
        }

    }


    public interface TakePicBack {
        void takePicBack(Bitmap bitmap);
    }

}