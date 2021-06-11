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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.model.SettingModel;
import com.aiton.pestscontrolandroid.data.model.ShpFile;
import com.aiton.pestscontrolandroid.ui.main.MainActivity;
import com.aiton.pestscontrolandroid.utils.FileUtil;
import com.huawei.hms.framework.common.StringUtils;
import com.leon.lfilepickerlibrary.LFilePicker;

import java.io.File;
import java.net.URISyntaxException;
import java.security.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    int REQUESTCODE_FROM_ACTIVITY = 1000;
    private TextView tvLoadShp, tvGalleryMap;
    private SettingViewModel settingViewModel;
    private ImageView ivLoadShp;
    private RecyclerView recyclerView;
    ShpAdapter adapter;

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


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ShpAdapter(settingViewModel);
        recyclerView.setAdapter(adapter);
//        settingViewModel.getSettingmodel().observe(this, new Observer<SettingModel>() {
//            @Override
//            public void onChanged(SettingModel settingModel) {
//                if (settingModel != null) {
//                    rbAmap.setChecked(settingModel.getaMapShow());
//                    rbArcGIS.setChecked(settingModel.getArcgisShow());
//                    rbGeoq.setChecked(settingModel.getGeoShow());
//                    rbGoogle.setChecked(settingModel.getGoogleShow());
//                    rbOSM.setChecked(settingModel.getOsmShow());
//                    rbTiandi.setChecked(settingModel.getTiandiShow());
//                    settingViewModel.saveSettingModel2SP();
//                }
//            }
//        });
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
                    files = FileUtil.traverseFolder(files, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
                    List<File> files1 = FileUtil.filterGeoDatabase(files, "geodatabase");
//                    List<String> files = FileUtil.getFiles("/storage/sdcard0/Download","geodatabase",true);
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
//
//        rbArcGIS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                SettingModel model = settingViewModel.getSettingmodel().getValue();
//                if (b) {
//                    model.setArcgisShow(true);
//                } else {
//                    model.setArcgisShow(false);
//                }
//                settingViewModel.getSettingmodel().setValue(model);
//            }
//        });
//        rbAmap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                SettingModel model = settingViewModel.getSettingmodel().getValue();
//                if (b) {
//                    model.setaMapShow(true);
//                } else {
//                    model.setaMapShow(false);
//                }
//                settingViewModel.getSettingmodel().setValue(model);
//            }
//        });
//        rbGeoq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                SettingModel model = settingViewModel.getSettingmodel().getValue();
//                if (b) {
//                    model.setGeoShow(true);
//                } else {
//                    model.setGeoShow(false);
//                }
//                settingViewModel.getSettingmodel().setValue(model);
//            }
//        });
//        rbGoogle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                SettingModel model = settingViewModel.getSettingmodel().getValue();
//                if (b) {
//                    model.setGoogleShow(true);
//                } else {
//                    model.setGoogleShow(false);
//                }
//                settingViewModel.getSettingmodel().setValue(model);
//            }
//        });
//        rbOSM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                SettingModel model = settingViewModel.getSettingmodel().getValue();
//                if (b) {
//                    model.setOsmShow(true);
//                } else {
//                    model.setOsmShow(false);
//                }
//                settingViewModel.getSettingmodel().setValue(model);
//            }
//        });
//        rbTiandi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                SettingModel model = settingViewModel.getSettingmodel().getValue();
//                if (b) {
//                    model.setTiandiShow(true);
//                } else {
//                    model.setTiandiShow(false);
//                }
//                settingViewModel.getSettingmodel().setValue(model);
//            }
//        });

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