package com.aiton.pestscontrolandroid.ui.trap;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.data.persistence.Trap;
import com.aiton.pestscontrolandroid.service.OssService;
import com.aiton.pestscontrolandroid.ui.myjob.PestsAdapter;
import com.aiton.pestscontrolandroid.ui.pests.PestsViewModel;

import java.io.File;
import java.util.List;

import cn.com.qiter.pests.PestsModel;
import cn.com.qiter.pests.TrapModel;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

public class TrayJobActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TrapViewModel trapViewModel;
    private Button trapUpdate,trapUpdateAgain,trapDelete;
    TrapAdapter adapter;
    private static final String TAG = "TrayJobActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tray_job);
        trapUpdate = findViewById(R.id.trap_update);
        trapUpdateAgain = findViewById(R.id.trap_update_again);
        trapDelete = findViewById(R.id.trap_delete_btn);
        recyclerView = findViewById(R.id.rv_trap);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        trapViewModel = new ViewModelProvider(this).get(TrapViewModel.class);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        adapter = new TrapAdapter(trapViewModel);
        recyclerView.setAdapter(adapter);
        trapViewModel.getProgress().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer.compareTo(100) == 0){
                    Toast.makeText(getApplicationContext(),"上传完成",Toast.LENGTH_SHORT).show();

                    adapter.notifyDataSetChanged();
                }
            }
        });
        trapViewModel.findAll().observe(this, new Observer<List<Trap>>() {
            @Override
            public void onChanged(List<Trap> traps) {
                if (traps !=null){
                    int temp = adapter.getItemCount();
                    adapter.setTraps(traps);
                    if (traps.size() != temp) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        trapUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateServer(false);
            }
        });
        trapUpdateAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateServer(true);
            }
        });
        trapDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteByUpdate(true);
            }
        });
    }

    public String ossUpload(File file) {
        //初始化OssService类，参数分别是Content，accessKeyId，accessKeySecret，endpoint，bucketName（后4个参数是您自己阿里云Oss中参数）
        OssService ossService = new OssService(getApplication(), AppConstance.ACCESS_KEY_ID, AppConstance.ACCESS_KEY_SECRET, AppConstance.ENDPOINT, AppConstance.BUCKETNAME);
//初始化OSSClient
        ossService.initOSSClient();
        String filePath = new DateTime().toString("yyyy/MM/dd");
        String appPath = "app/trap/" + filePath;
        String appFilePath = appPath + "/" + file.getName();
//开始上传，参数分别为content，上传的文件名filename，上传的文件路径filePath
        ossService.beginupload(getApplication().getApplicationContext(), appFilePath, file.getAbsolutePath());
//上传的进度回调
        ossService.setProgressCallback(new OssService.ProgressCallback() {
            @Override
            public void onProgressCallback(double progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "run: " + progress);
                    }
                });
            }
        });
        return appFilePath;
    }
    private void deleteByUpdate(boolean isUpdate) {
        Trap[] traps = trapViewModel.findAllObject(isUpdate);
        for (Trap p :
                traps) {
            trapViewModel.delete(p);
        }
    }
    private void updateServer(boolean isUpdate) {
        Trap[] traps1 = trapViewModel.findAllObject();
        Trap[] traps = trapViewModel.findAllObject(isUpdate);
        for (Trap p :
                traps) {
            p.setUpdateServer(true);
            trapViewModel.update(p);

            Log.e(TAG, "服务器数据上传一条: " + p.toString());
            TrapModel model = new TrapModel();
            model.setQrcode(p.getQrcode());
            model.setAppId(p.getId());
            model.setLureReplaced(p.getLureReplaced());
            model.setDb(p.getDb());
            model.setXb(p.getXb());
            model.setDeviceId(p.getDeviceId());
            if (p.getPic1() != null) {
                File pic1 = new File(p.getPic1());
                String filepath = ossUpload(pic1);
                model.setPic1("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
            } else {
                model.setPic1("");
            }
            if (p.getPic2() != null) {
                File pic2 = new File(p.getPic2());
                String filepath = ossUpload(pic2);
                model.setPic2("http://" + AppConstance.BUCKETNAME + "." + AppConstance.ENDPOINT + "/" + filepath);
            } else {
                model.setPic2("");
            }
            model.setLatitude(p.getLatitude());
            model.setLongitude(p.getLongitude());
            model.setOperator(p.getOperator());
            model.setRemark(p.getRemark());
            model.setPositionError(p.getPositionError());
            model.setTown(p.getTown());
            model.setScount(p.getScount());
            model.setUserId(p.getUserId());
            model.setVillage(p.getVillage());
            model.setStime(p.getStime());
            trapViewModel.uploadServer(model);
            Log.e(TAG, "updateServer: " + p.toString());

        }
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
}