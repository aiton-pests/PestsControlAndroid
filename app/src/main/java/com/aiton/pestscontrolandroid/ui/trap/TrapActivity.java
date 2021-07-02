package com.aiton.pestscontrolandroid.ui.trap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.CameraActivity;
import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.model.UcenterMemberOrder;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.data.persistence.Trap;
import com.aiton.pestscontrolandroid.service.PestsOnceWork;
import com.aiton.pestscontrolandroid.service.TrapOnceWork;
import com.aiton.pestscontrolandroid.ui.main.MainActivity;
import com.aiton.pestscontrolandroid.ui.pests.PestsActivity;
import com.aiton.pestscontrolandroid.utils.SPUtil;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cn.com.qiter.common.vo.PestsControlModel;
import cn.com.qiter.common.vo.PestsTrapModel;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

public class TrapActivity extends AppCompatActivity {
    private static final String TAG = "TrapActivity";
    private ImageView operatorAdd,ivFirst,ivSecond,remarkAdd;
    private EditText trapCount,trapTown,trapVillage,trapDb,trapXb,etQrcode,etTrapCodeInt;
    private ImageButton ibFirst,ibSecond;
    private AutoCompleteTextView trapOperator,trapRemark;
    private Button btnCancel,btnSave;
    private Switch swLureReplace;
    //pests 操作人员记录和自动显示
    private ArrayAdapter<String> adapter,remarkAdapter;
    // 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
    private String mCameraImagePathPIC1 ="";
    private String mCameraImagePathPIC2 ="";

    TrapViewModel trapViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trap);
        trapViewModel = new ViewModelProvider(this).get(TrapViewModel.class);

        workmanager = WorkManager.getInstance(this);
        initRemarkDataSource();
        etTrapCodeInt = findViewById(R.id.etCodeInt);
        etQrcode = findViewById(R.id.etQrcode);
        swLureReplace = findViewById(R.id.sw_lure_replace);
        operatorAdd = findViewById(R.id.trap_operator_add);
        remarkAdd = findViewById(R.id.trap_remark_add);
        ivFirst = findViewById(R.id.trap_iv_first);
        ivSecond = findViewById(R.id.trap_iv_second);
        ibFirst = findViewById(R.id.trap_first);
        ibFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrapActivity.this, CameraActivity.class);
                startActivityForResult(intent,AppConstance.CAMERAX_REQUEST_CODE_PIC1);
            }
        });
        ibSecond = findViewById(R.id.trap_second);
        ibSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrapActivity.this, CameraActivity.class);
                startActivityForResult(intent,AppConstance.CAMERAX_REQUEST_CODE_PIC2);
            }
        });
        trapTown = findViewById(R.id.trap_town);
        trapVillage = findViewById(R.id.trap_village);
        trapXb = findViewById(R.id.trap_xb);
        trapDb = findViewById(R.id.trap_db);
        trapRemark = findViewById(R.id.trap_remark);
        remarkAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, getRemarkDataSource());
        trapRemark.setAdapter(remarkAdapter);
        trapRemark.setText("挂设");
        remarkAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String operator = trapRemark.getText().toString();
                setRemarkDataSource(operator);
            }
        });
        trapCount = findViewById(R.id.trap_card_count);
        trapOperator = findViewById(R.id.trap_operator);
        /*
         * 1.使用手工方式的list数组适配器
         */
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, getDataSource());
        trapOperator.setAdapter(adapter);

        operatorAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String operator = trapOperator.getText().toString();
                setDataSource(operator);
            }
        });
        trapViewModel.getTrapCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != null){
                    if (integer.compareTo(0) ==0){
                        trapRemark.setText(getResources().getString(R.string.trap_operator_set));
                    } else { //第二次收虫
                        trapRemark.setText(getResources().getString(R.string.trap_operator_d) + integer + getResources().getString(R.string.trap_operator_sc));
                    }
                }
            }
        });
        PestsTrapModel model = (PestsTrapModel) getIntent().getSerializableExtra(AppConstance.TRAPMODEL);
        etQrcode.setText(model.getQrcode());
        trapViewModel.getCodeInt().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != null){
                    if (integer.compareTo(0) == 0){
                        Toast.makeText(getApplicationContext(),"二维码不存在，请联系管理员！",Toast.LENGTH_SHORT).show();
                    } else {
                        etTrapCodeInt.setText(String.valueOf(integer));
                    }

                }
            }
        });
        if (!StrUtil.isNullOrUndefined(model.getQrcode()) && !StrUtil.isEmpty(model.getQrcode())){
            trapViewModel.updateCodeInt(model.getQrcode());
            trapViewModel.countTrap(model.getQrcode());
        }
        HashMap<String,String> fam = (HashMap<String, String>) getIntent().getSerializableExtra(AppConstance.FEATURE_ATTRIBUTE_MAP);
        trapDb.setText(fam.get(AppConstance.DBH));
        trapXb.setText(fam.get(AppConstance.XBH));
        trapVillage.setText(fam.get(AppConstance.CGQNAME));
        trapTown.setText(fam.get(AppConstance.JYXZCNAME));
        btnCancel = findViewById(R.id.trap_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, getResources().getString(R.string.pests_cancel), Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                Intent intent = new Intent(TrapActivity.this, MainActivity.class);
//                                startActivity(intent);
                                Intent intent = getIntent();
                                intent.putExtra("data", "NO");
                                setResult(RESULT_CANCELED,intent);
                                finish();
                            }
                        }).show();
            }
        });
        btnSave = findViewById(R.id.trap_submit);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UcenterMemberOrder userModel = SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.UCENTER_MEMBER_MODEL, UcenterMemberOrder.class);
                Trap trap = new Trap();
                trap.setDeviceId(userModel.getNickname());
                trap.setDb(fam.get(AppConstance.DBH));
                trap.setXb(fam.get(AppConstance.XBH));
                trap.setVillage(fam.get(AppConstance.CGQNAME));
                trap.setTown(fam.get(AppConstance.JYXZCNAME));
                int i = 0;
                if (swLureReplace.isChecked()){
                    i = 1;
                }else{
                    i = 0;
                }
                trap.setLureReplaced(i);
                trap.setOperator(trapOperator.getText().toString());
                trap.setRemark(trapRemark.getText().toString());
                trap.setQrcode(etQrcode.getText().toString());
                trap.setUserId(userModel.getId());
                trap.setStime(DateUtil.formatDateTime(DateUtil.date()));
                String scount = trapCount.getText().toString();
                if (StrUtil.isNullOrUndefined(scount) || StrUtil.isEmpty(scount)){
                    scount = "0";
                }
                trap.setScount(Integer.valueOf(scount));
                trap.setPositionError(String.valueOf(new Random().nextInt(15)));
                trap.setLongitude(Double.valueOf(fam.get(AppConstance.LONGITUDE)));
                trap.setLatitude(Double.valueOf(fam.get(AppConstance.LATIDUTE)));
                trap.setCodeInt(etTrapCodeInt.getText().toString());
                trap.setPic2(mCameraImagePathPIC2);
                trap.setPic1(mCameraImagePathPIC1);

                trap.setUpdateServer(false);
                trapViewModel.insert(trap);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         *要执行的操作
                         */
                        Trap p = trapViewModel.findByLatLonAndUserIdAndStime(trap.getLatitude(),trap.getLongitude(),trap.getStime(),trap.getUserId(),trap.getQrcode());

                        workmanagerUpload(trap);
                        Log.e(TAG, "run: 延时3000");
                    }
                }, 3000);//3秒后执行Runnable中的run方法


                setDefaultOper(trap.getOperator());
//                Intent intent = new Intent(TrapActivity.this,MainActivity.class);
//                startActivity(intent);

                Intent intent = getIntent();
                intent.putExtra("data", trap);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        displayDefaultOperator();
    }

    WorkManager workmanager;
    private static ObjectMapper mapper = new ObjectMapper();
    private void workmanagerUpload(Trap pests){
        PestsTrapModel pestsTrapModel = new PestsTrapModel();
        try {
            pestsTrapModel.setAppId(pests.getId());
            pestsTrapModel.setDb(pests.getDb());
            pestsTrapModel.setDeviceId(pests.getDeviceId());
            pestsTrapModel.setIsChecked(pests.isChecked());
            pestsTrapModel.setLatitude(pests.getLatitude());
            pestsTrapModel.setLongitude(pests.getLongitude());
            pestsTrapModel.setLureReplaced(pests.getLureReplaced());
            pestsTrapModel.setOperator(pests.getOperator());
            pestsTrapModel.setPic1(pests.getPic1());
            pestsTrapModel.setPic2(pests.getPic2());
            pestsTrapModel.setPositionError(pests.getPositionError());
            pestsTrapModel.setProjectId(pests.getProjectId());
            pestsTrapModel.setQrcode(pests.getQrcode());
            pestsTrapModel.setStime(pests.getStime());
            pestsTrapModel.setRemark(pests.getRemark());
            pestsTrapModel.setTown(pests.getTown());
            pestsTrapModel.setScount(pests.getScount());
            pestsTrapModel.setUserId(pests.getUserId());
            pestsTrapModel.setVillage(pests.getVillage());
            pestsTrapModel.setXb(pests.getXb());

            String pcmStr = mapper.writeValueAsString(pestsTrapModel);
            // 数据
            Data data = new Data.Builder().putString(AppConstance.WORKMANAGER_KEY, pcmStr).build();
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();
            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(TrapOnceWork.class)
                    .setInputData(data) // 数据的携带
                    .setConstraints(constraints)
                    .build();

// 【状态机】  为什么一直都是 ENQUEUE，因为 你是轮询的任务，所以你看不到 SUCCESS     [如果你是单个任务，就会看到SUCCESS]
            // 监听状态
            workmanager.getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                    .observe(TrapActivity.this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            Log.d(AppConstance.TAG, "状态：" + workInfo.getState().name()); // ENQUEEN   SUCCESS
                            if (workInfo.getState().isFinished()) {
                                Log.d(AppConstance.TAG, "状态：isFinished=true 注意：后台任务已经完成了..."+workInfo.getOutputData().getString(AppConstance.WORKMANAGER_KEY));
                            }
                        }
                    });
            workmanager.enqueue(oneTimeWorkRequest);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


    }
    private void setDefaultOper(String operator) {
        SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).setData(AppConstance.OPERATOR_DEFAULT,operator);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstance.CAMERAX_REQUEST_CODE_PIC1){
            if (resultCode == RESULT_OK) {
                String url = data.getStringExtra("data");
                Glide.with(this).load(url).into(ivFirst);
                mCameraImagePathPIC1 = url;
//                ivFell.setImageURI(Uri.fromFile(new File(url)));
            }else {
                Toast.makeText(this,"取消",Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == AppConstance.CAMERAX_REQUEST_CODE_PIC2){
            if (resultCode == RESULT_OK) {
                String url = data.getStringExtra("data");
                mCameraImagePathPIC2 = url;
                Glide.with(this).load(url).into(ivSecond);
//                ivStump.setImageBitmap(BitmapFactory.decodeFile(url));
            }else {
                Toast.makeText(this,"取消",Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean setDataSource(String operator){
        if (operator != null){
            List<String> operators = SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).getDataList(AppConstance.OPERATOR,String.class);
            if (operators != null){
                if (!operators.contains(operator)){
                    operators.add(operator);
                    SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).setDataList(AppConstance.OPERATOR,operators);
                }

                adapter.addAll(operators);
                adapter.notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }
    public void displayDefaultOperator(){
        String defaultOper = SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).getData(AppConstance.OPERATOR_DEFAULT,String.class);
        trapOperator.setText(defaultOper);
    }
    private List<String> getDataSource() {
        List<String> operators = SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).getDataList(AppConstance.OPERATOR,String.class);
        if (operators != null){
            return operators;
        }else{
            String name = "鲁仁华";
            List<String> list = new ArrayList<>();
            list.add(name);
            setDataSource(name);
            return list;
        }

    }

    private boolean setRemarkDataSource(String operator){
        if (operator != null){
            List<String> operators = SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).getDataList(AppConstance.TRAP_REMARK,String.class);
            if (operators != null){
                if (!operators.contains(operator)){
                    operators.add(operator);
                    SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).setDataList(AppConstance.TRAP_REMARK,operators);
                }

                remarkAdapter.addAll(operators);
                remarkAdapter.notifyDataSetChanged();
                return true;
            }
        }
        return false;
    }
    private void initRemarkDataSource(){
        List<String> operators = SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).getDataList(AppConstance.TRAP_REMARK,String.class);
        if (operators == null && operators.size() == 0) {
            List<String> list = new ArrayList<>();
            list.add("挂设");
            list.add("第1次收虫");
            list.add("第2次收虫");
            list.add("第3次收虫");
            list.add("第4次收虫");
            list.add("第5次收虫");
            list.add("第6次收虫");
            list.add("第7次收虫");
            list.add("第8次收虫");
            list.add("第9次收虫");
            list.add("第10次收虫");
            list.add("第11次收虫");
            list.add("第12次收虫");
            list.add("第13次收虫");
            list.add("第14次收虫");
            list.add("第15次收虫");
            SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).setDataList(AppConstance.TRAP_REMARK,list);
        }


    }
    private List<String> getRemarkDataSource() {
        List<String> operators = SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).getDataList(AppConstance.TRAP_REMARK,String.class);
        if (operators != null && operators.size() != 0){
            return operators;
        }else{
            List<String> list = new ArrayList<>();
            list.add("挂设");
            list.add("第1次收虫");
            list.add("第2次收虫");
            list.add("第3次收虫");
            list.add("第4次收虫");
            list.add("第5次收虫");
            list.add("第6次收虫");
            list.add("第7次收虫");
            list.add("第8次收虫");
            list.add("第9次收虫");
            list.add("第10次收虫");
            list.add("第11次收虫");
            list.add("第12次收虫");
            list.add("第13次收虫");
            list.add("第14次收虫");
            list.add("第15次收虫");
            return list;
        }

    }
}