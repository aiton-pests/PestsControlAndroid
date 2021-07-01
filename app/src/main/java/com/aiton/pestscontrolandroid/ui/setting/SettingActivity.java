package com.aiton.pestscontrolandroid.ui.setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.model.Result;
import com.aiton.pestscontrolandroid.data.model.SettingModel;
import com.aiton.pestscontrolandroid.data.model.ShpFile;
import com.aiton.pestscontrolandroid.service.RetrofitInterface;
import com.aiton.pestscontrolandroid.service.RetrofitUtil;
import com.aiton.pestscontrolandroid.ui.main.MainActivity;
import com.aiton.pestscontrolandroid.utils.FileUtil;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.alibaba.sdk.android.oss.model.OSSObjectSummary;
import com.google.gson.internal.LinkedTreeMap;
import com.huawei.hms.framework.common.StringUtils;
import com.leon.lfilepickerlibrary.LFilePicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";
    int REQUESTCODE_FROM_ACTIVITY = 1000;
    private TextView tvLoadShp, tvGalleryMap;
    private ProgressBar progressBar;
    private SettingViewModel settingViewModel;
    private ImageView ivLoadShp;
    private RecyclerView recyclerView;
    ShpAdapter adapter;
    DownloadZipFileTask downloadZipFileTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        settingViewModel = new ViewModelProvider(this).get(SettingViewModel.class);
        if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    1000);
        }
        progressBar = findViewById(R.id.progressBar3);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        recyclerView = findViewById(R.id.rv_shp);
        ivLoadShp = findViewById(R.id.iv_load_file);
//        imageView2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e(AppConstance.TAG, "getSettingModel4SP: " + settingViewModel.getSettingModel4SP().toString() );
//                Log.e(AppConstance.TAG, "getSettingmodel:getValue " + settingViewModel.getSettingmodel().getValue().toString() );
//            }
//        });
        tvLoadShp = findViewById(R.id.tv_load_shp);
        tvLoadShp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RetrofitUtil.getInstance().getDictService().getDictByName(AppConstance.DICT_NAME_PESTS_GEODATABASE).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new io.reactivex.rxjava3.core.Observer<Result>() {
                            @Override
                            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Result result) {
                                if (result.getSuccess()){
                                    List<LinkedTreeMap> list = (List) result.getData().get("items");
                                    List<String> strings = new ArrayList<>();
                                    for (LinkedTreeMap map :
                                            list) {
                                        strings.add(String.valueOf(map.get("value")));
                                        downloadZipFile(String.valueOf(map.get("value")));
                                    }
                                }
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ShpAdapter(settingViewModel);
        recyclerView.setAdapter(adapter);

        settingViewModel.getShpFile().observe(this, new Observer<List<ShpFile>>() {
            @Override
            public void onChanged(List<ShpFile> shpFiles) {
                int temp = adapter.getItemCount();
                adapter.setShpFiles(shpFiles);
                if (shpFiles.size() != temp) {
                    adapter.notifyDataSetChanged();
                }

            }
        });
        try {
            ivLoadShp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.setType("*/*");//无类型限制
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    startActivityForResult(intent, REQUESTCODE_FROM_ACTIVITY);
                    List<File> files = new ArrayList<>();
                    String path = getFilesDir().getAbsolutePath();
                    files = FileUtil.traverseFolder(files,path);
                    List<File> files1 = FileUtil.filterGeoDatabase(files, "geodatabase");
//                    List<String> files = FileUtil.getFiles("/storage/emulated/0/Download","geodatabase",true);  /storage/emulated/0/Download/53ad76ba959340fabd7fe8ee7adbace3b3e2489915484baaafc555d544b0cd7d.jpg
                 //   files1.add(new File("/data/data/com.aiton.pestscontrolandroid/files/xiamen_xiangan.geodatabase"));
                    Log.e(AppConstance.TAG, "onClick: " + files1.toString());
                    List<ShpFile> shpFiles = FileUtil.convertGeoFile2ShpFile(files1);
                    settingViewModel.getShpFile().setValue(shpFiles);
                    if (files1.isEmpty() || files1.size() == 0){
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.no_file_exist),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void downloadZipFile(String fileName) {

        RetrofitInterface downloadService = createService(RetrofitInterface.class, "https://pestscontrol.oss-cn-hangzhou.aliyuncs.com/geodatabase/");
        Call<ResponseBody> call = downloadService.downloadFileByUrl(fileName);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Got the body for the file");

                    Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();

                    downloadZipFileTask = new DownloadZipFileTask(fileName);
                    downloadZipFileTask.execute(response.body());
                } else {
                    Log.d(TAG, "Connection failed " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public <T> T createService(Class<T> serviceClass, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(new OkHttpClient.Builder().build())
                .build();
        return retrofit.create(serviceClass);
    }

    private class DownloadZipFileTask extends AsyncTask<ResponseBody, Pair<Integer, Long>, String> {
        private String fileName="cc.geodatabase";

        public DownloadZipFileTask(String fileName) {
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(ResponseBody... urls) {
            //Copy you logic to calculate progress and call

            saveToDisk(urls[0], fileName);
            return null;
        }

        protected void onProgressUpdate(Pair<Integer, Long>... progress) {

            Log.d("API123", progress[0].second + " ");

            if (progress[0].first == 100)
                Toast.makeText(getApplicationContext(), "File downloaded successfully", Toast.LENGTH_SHORT).show();


            if (progress[0].second > 0) {
                int currentProgress = (int) ((double) progress[0].first / (double) progress[0].second * 100);
                progressBar.setProgress(currentProgress);

                tvLoadShp.setText("Progress " + currentProgress + "%");

            }

            if (progress[0].first == -1) {
                Toast.makeText(getApplicationContext(), "Download failed", Toast.LENGTH_SHORT).show();
            }

        }

        public void doProgress(Pair<Integer, Long> progressDetails) {
            publishProgress(progressDetails);
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
    private void saveToDisk(ResponseBody body, String filename) {
        try {

            String path = getFilesDir().getAbsolutePath();
            File destinationFile = new File(getFilesDir(), filename);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(destinationFile);
                byte data[] = new byte[4096];
                int count;
                int progress = 0;
                long fileSize = body.contentLength();
                Log.d(TAG, "File Size=" + fileSize);
                while ((count = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, count);
                    progress += count;
                    Pair<Integer, Long> pairs = new Pair<>(progress, fileSize);
                    downloadZipFileTask.doProgress(pairs);
                    Log.d(TAG, "Progress: " + progress + "/" + fileSize + " >>>> " + (float) progress / fileSize);
                }

                outputStream.flush();

                Log.d(TAG, destinationFile.getParent());
                Pair<Integer, Long> pairs = new Pair<>(100, 100L);
                downloadZipFileTask.doProgress(pairs);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Pair<Integer, Long> pairs = new Pair<>(-1, Long.valueOf(-1));
                downloadZipFileTask.doProgress(pairs);
                Log.d(TAG, "Failed to save the file!");
                return;
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to save the file!");
            return;
        }
    }


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(SettingActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(SettingActivity.this, permission)) {
                ActivityCompat.requestPermissions(SettingActivity.this, new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(SettingActivity.this, new String[]{permission}, requestCode);
            }
        } else if (ContextCompat.checkSelfPermission(SettingActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(), "Permission was denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //同意申请权限
            } else {
                // 用户拒绝申请权限
                Toast.makeText(SettingActivity.this, getResources().getString(R.string.storage_permission), Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (requestCode == 101)
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Deprecated
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                Uri uri = data.getData();
                String files = uri.getScheme();
                Log.e(AppConstance.TAG, "onActivityResult: " + files);
                String path = uri.getPath();
                Log.e(AppConstance.TAG, "onActivityResult: " + path);
                if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                    path = uri.getPath();
                    Toast.makeText(this, getResources().getString(R.string.storage_permission) + path, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                    // String path = getPath(this, uri);
//                    List<String> filess = FileUtil.getFiles("/storage/sdcard1/Download/geodatabase", "geodatabase", true);
                    Log.e(AppConstance.TAG, "onClick: " + files.toString());
//                    Toast.makeText(this, path + "11111", Toast.LENGTH_SHORT).show();
                } else {//4.4以下下系统调用方法
                    // String path = getRealPathFromURI(uri);
                }
            }
        }
    }
}