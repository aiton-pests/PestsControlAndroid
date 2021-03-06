package com.aiton.pestscontrolandroid.ui.me;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.model.SettingModel;
import com.aiton.pestscontrolandroid.data.model.ShpFile;
import com.aiton.pestscontrolandroid.data.model.UcenterMemberOrder;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.ui.login.LoginActivity;
import com.aiton.pestscontrolandroid.ui.login.LoginViewModel;
import com.aiton.pestscontrolandroid.ui.main.MainViewModel;
import com.aiton.pestscontrolandroid.ui.pests.PestsViewModel;
import com.aiton.pestscontrolandroid.utils.BugTest;
import com.aiton.pestscontrolandroid.utils.SPUtil;
import com.huawei.agconnect.remoteconfig.AGConnectConfig;
import com.huawei.agconnect.remoteconfig.ConfigValues;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.util.List;


public class MeActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    MeViewModel meViewModel ;
    private MainViewModel mainViewModel;
    private PestsViewModel pestsViewModel;
    LoginViewModel loginViewModel;
    private ImageView blurImageView;
    private ImageView avatarImageView,ivAbout;
    private Switch swIsScan,swTest,swAutoUpload,swTiandi,swLoadSHP;
    TextView tvMobile,tvNickname,tvMeAbout;
    CardView cardView;
    Button btnLogout;

    private AGConnectConfig config;
    private static final String UI_ME_ABOUT = "UI_ME_ABOUT";
    private static final String SET_MEMBER_IS_TEST = "SET_MEMBER_IS_TEST";

    WorkManager workmanager;
//    WorkManager workmanagerTrap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        workmanager = WorkManager.getInstance(this);
//        workmanagerTrap = WorkManager.getInstance(this);
        // ??????????????????
        config = AGConnectConfig.getInstance();
        config.applyDefault(R.xml.remote_config);

        tvMeAbout = findViewById(R.id.tv_me_about);
        avatarImageView = findViewById(R.id.h_head);
        swTiandi = findViewById(R.id.sw_tiandi);
        swIsScan = findViewById(R.id.sw_is_scan);
        swAutoUpload = findViewById(R.id.sw_auto_upload);
        swLoadSHP = findViewById(R.id.sw_load_shp);
        tvMobile = findViewById(R.id.mobile);
        tvNickname = findViewById(R.id.user_name);
        cardView = findViewById(R.id.aboutme);
        swTest = findViewById(R.id.sw_test);
        btnLogout = findViewById(R.id.btn_logout);
        ivAbout = findViewById(R.id.iv_about);
        ivAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayed();
                fetchAndApply();
            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BugTest bugTest = new BugTest();
                bugTest.bug();
            }
        });
        swTiandi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                meViewModel.saveTianDiMap2SHP(isChecked);
               // meViewModel.getTiandiMap().setValue(isChecked);
            }
        });
        swIsScan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                meViewModel.setIsScan(isChecked);
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        pestsViewModel = new ViewModelProvider(this).get(PestsViewModel.class);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        meViewModel = new ViewModelProvider(this).get(MeViewModel.class);
        meViewModel.loadTianDiMap();
        meViewModel.getTiandiMap().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                swTiandi.setChecked(aBoolean);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        meViewModel.getMutableLiveData().observe(this, new Observer<UcenterMemberOrder>() {
            @Override
            public void onChanged(UcenterMemberOrder model) {
                if (model != null)
                    initMember(model);
            }
        });
        meViewModel.getIsTest().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                swTest.setChecked(aBoolean);
            }
        });
        meViewModel.getIsAutoUpload().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                swAutoUpload.setChecked(aBoolean);
            }
        });
        meViewModel.getIsLoadSHP().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                swLoadSHP.setChecked(aBoolean);
            }
        });
        meViewModel.getIsScan().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                swIsScan.setChecked(aBoolean);
            }
        });
        swTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                meViewModel.setTest(isChecked);
            }
        });
        swAutoUpload.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                meViewModel.setIsAutoUpload(isChecked);
                if (isChecked){
                    LiveEventBus.get(AppConstance.WORK_NOTIFICATION_AUTO_UPLOAD).postDelay(AppConstance.OK,3000);
//                    workmanager.cancelAllWork();
//
//                    // ??????
//                    Data data = new Data.Builder().putString(AppConstance.WORKMANAGER_KEY, "????????????").build();
//                    Constraints constraints = new Constraints.Builder()
//                            .setRequiredNetworkType(NetworkType.CONNECTED)
//                            .build();
//
//                    PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest
//                            .Builder(PestsWork.class,16, TimeUnit.MINUTES)
//                            .setConstraints(constraints)
//                            .setInputData(data)
//                            .build();
//
//// ???????????????  ????????????????????? ENQUEUE????????? ?????????????????????????????????????????? SUCCESS     [???????????????????????????????????????SUCCESS]
//                    // ????????????
//                    workmanager.getWorkInfoByIdLiveData(periodicWorkRequest.getId())
//                            .observe(MeActivity.this, new Observer<WorkInfo>() {
//                                @Override
//                                public void onChanged(WorkInfo workInfo) {
//                                    Log.d(AppConstance.TAG, "?????????" + workInfo.getState().name()); // ENQUEEN   SUCCESS
//                                    if (workInfo.getState().isFinished()) {
//                                        Log.d(AppConstance.TAG, "?????????isFinished=true ????????????????????????????????????...");
//                                    }
//                                }
//                            });
//
//
//                    // ??????
//                    Data dataTrap = new Data.Builder().putString(AppConstance.WORKMANAGER_KEY, "????????????").build();
//                    Constraints constraintsTrap = new Constraints.Builder()
//                            .setRequiredNetworkType(NetworkType.CONNECTED)
//                            .build();
//
//                    PeriodicWorkRequest periodicWorkRequestTrap = new PeriodicWorkRequest
//                            .Builder(TrapWork.class,16, TimeUnit.MINUTES)
//                            .setConstraints(constraintsTrap)
//                            .setInputData(dataTrap)
//                            .build();
//                    workmanager.getWorkInfoByIdLiveData(periodicWorkRequestTrap.getId())
//                            .observe(MeActivity.this, new Observer<WorkInfo>() {
//                                @Override
//                                public void onChanged(WorkInfo workInfo) {
//                                    Log.d(AppConstance.TAG, "?????????" + workInfo.getState().name()); // ENQUEEN   SUCCESS
//                                    if (workInfo.getState().isFinished()) {
//                                        Log.d(AppConstance.TAG, "?????????isFinished=true ????????????????????????????????????...");
//                                    }
//                                }
//                            });
//
//                    List<PeriodicWorkRequest> workRequests = new ArrayList<>();
//                    workRequests.add(periodicWorkRequest);
//                    workRequests.add(periodicWorkRequestTrap);
//                    workmanager.enqueue(workRequests);
                }else{
                    LiveEventBus.get(AppConstance.WORK_NOTIFICATION_AUTO_UPLOAD).postDelay(AppConstance.NO,3000);
//                    workmanager.cancelAllWork();
                }
            }
        });
        swLoadSHP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                meViewModel.setIsLoadSHP(isChecked);
            }
        });
        meViewModel.loadTest();
        meViewModel.loadAutoUpload();
        meViewModel.loadLoadSHP();
        meViewModel.loadIsScan();
    }
    private void fetchAndApply(){
        config.fetch(0).addOnSuccessListener(new OnSuccessListener<ConfigValues>() {
            @Override
            public void onSuccess(ConfigValues configValues) {
                // Apply Network Config to Current Config
                config.apply(configValues);
                updateUI();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                tvMeAbout.setText("fetch setting failed: ");
            }
        });
    }

    private void updateUI(){
        String text = config.getValueAsString(UI_ME_ABOUT);
//        Boolean isBold = config.getValueAsBoolean(SET_BOLD_KEY);
        tvMeAbout.setText(text);
//        if (isBold){
//            tvMeAbout.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//        }
    }


    private void loadUI(){
        config.applyDefault(R.xml.remote_config);
        tvMeAbout.setText(config.getValueAsString(UI_ME_ABOUT));
//        Boolean isBold = config.getValueAsBoolean(SET_BOLD_KEY);
//        if (isBold){
//            textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
//        }

    }
    private void displayed(){
        Pests[] pests = pestsViewModel.findAllObject();
        for (Pests pests1:
                pests) {
            Toast.makeText(getApplicationContext(),pests1.toString(),Toast.LENGTH_LONG).show();
//            Log.e(AppConstance.TAG_ME, "Pests: " + pests1.toString() );
        }

        List<String> operators = SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).getDataList(AppConstance.OPERATOR,String.class);

        for (String op :
                operators) {
//            Log.e("PESTS", "onClick: " + op );
        }

        UcenterMemberOrder loggedInUser = SPUtil.builder(getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.UCENTER_MEMBER_MODEL, UcenterMemberOrder.class);
        if (loggedInUser != null){
//            Log.e(AppConstance.TAG_ME, "UcenterMemberModel: " + loggedInUser.toString() );
            Toast.makeText(getApplicationContext(),loggedInUser.toString(),Toast.LENGTH_LONG).show();
        }
        Boolean tiandi = SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.TIANDIMAP, Boolean.class);
//        Log.e(AppConstance.TAG_ME, "?????????/ArcGIS: " + tiandi );
        Toast.makeText(getApplicationContext(),"?????????/ArcGIS: " + tiandi,Toast.LENGTH_LONG).show();
        Boolean test = SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.ISTEST, Boolean.class);
//        Log.e(AppConstance.TAG_ME, "????????????: " + test );
        Toast.makeText(getApplicationContext(),"????????????: " + test,Toast.LENGTH_LONG).show();
        SettingModel spSetting = SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).getData(AppConstance.SETTING_MODEL,SettingModel.class);
//        Log.e(AppConstance.TAG_ME, "SettingModel: " + spSetting.toString() );
        Toast.makeText(getApplicationContext(),spSetting.toString(),Toast.LENGTH_LONG).show();
        List<ShpFile> shpFiles = SPUtil.builder(getApplication(),AppConstance.APP_SP).getDataList(AppConstance.SHP_FILE, ShpFile.class);
//        Log.e(AppConstance.TAG_ME, "ShpFile: " + shpFiles.toString() );
        Toast.makeText(getApplicationContext(),shpFiles.toString(),Toast.LENGTH_LONG).show();
    }
    private void logout() {
        loginViewModel.logout();
        Intent intent = new Intent(MeActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void initMember(UcenterMemberOrder umm) {
//        RequestOptions options = new RequestOptions()
//                .circleCrop();
//        RequestOptions backOptions = new RequestOptions()
//                .transforms(new BlurTransformation(), new GrayscaleTransformation());
//        Glide.with(this).load("https://pestscontrol.oss-cn-hangzhou.aliyuncs.com/sys/blurImageView.jpg")
//                .apply(backOptions)
//                .into(blurImageView);
//
//        Glide.with(this).load(umm.getAvatar())
//                .apply(options)
//                .into(avatarImageView);
        tvMobile.setText(umm.getMobile());
        tvNickname.setText(umm.getNickname());
//        etNickname.setText(umm.getNickname());
//        etMobile.setText(umm.getMobile());
//        etPassword.setText(umm.getPassword());
//        etGmtModified.setText(umm.getGmtModified());
//        swDisabled.setChecked(umm.getDeleted());

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