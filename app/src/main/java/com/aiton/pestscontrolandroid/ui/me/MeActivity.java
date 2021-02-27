package com.aiton.pestscontrolandroid.ui.me;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.model.SettingModel;
import com.aiton.pestscontrolandroid.data.model.ShpFile;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.ui.login.LoginActivity;
import com.aiton.pestscontrolandroid.ui.login.LoginViewModel;
import com.aiton.pestscontrolandroid.ui.main.MainActivity;
import com.aiton.pestscontrolandroid.ui.main.MainViewModel;
import com.aiton.pestscontrolandroid.ui.pests.PestsViewModel;
import com.aiton.pestscontrolandroid.utils.SPUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import cn.com.qiter.pests.UcenterMemberModel;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;

public class MeActivity extends AppCompatActivity {
    MeViewModel meViewModel ;
    private MainViewModel mainViewModel;
    private PestsViewModel pestsViewModel;
    LoginViewModel loginViewModel;
    private ImageView blurImageView;
    private ImageView avatarImageView;
//    private EditText etGmtModified,etPassword ,etMobile ,etNickname;
    private Switch swDisabled,swTest,swAbout;
    TextView tvMobile,tvNickname;
    Button btnLogout,btnDisplayTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        blurImageView = findViewById(R.id.h_back);
        avatarImageView = findViewById(R.id.h_head);
//        etNickname = findViewById(R.id.et_nickname);
////        etMobile = findViewById(R.id.et_mobile);
//        etPassword = findViewById(R.id.et_password);
//        etGmtModified = findViewById(R.id.ed_modify_time);
        swDisabled = findViewById(R.id.sw_disabled);
        swAbout = findViewById(R.id.sw_about);
        tvMobile = findViewById(R.id.user_val);
        tvNickname = findViewById(R.id.user_name);
        swTest = findViewById(R.id.sw_test);
        btnLogout = findViewById(R.id.btn_logout);
        btnDisplayTest = findViewById(R.id.btn_display_test);
        pestsViewModel = new ViewModelProvider(this).get(PestsViewModel.class);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        meViewModel = new ViewModelProvider(this).get(MeViewModel.class);
        btnDisplayTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pests[] pests = pestsViewModel.findAllObject();
                for (Pests pests1:
                        pests) {
                    Log.e(AppConstance.TAG, "Pests: " + pests1.toString() );
                }
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        meViewModel.getMutableLiveData().observe(this, new Observer<UcenterMemberModel>() {
            @Override
            public void onChanged(UcenterMemberModel model) {
                initMember(model);
            }
        });
        meViewModel.getIsTest().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer.compareTo(0) == 0){
                    swTest.setChecked(true);
                }else{
                    swTest.setChecked(false);
                }
            }
        });
        swTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    meViewModel.setTest(0);
                }else{
                    meViewModel.setTest(1);
                }
            }
        });
        swAbout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Pests[] pests = pestsViewModel.findAllObject();
                for (Pests pests1:
                        pests) {
                    Toast.makeText(getApplicationContext(),pests1.toString(),Toast.LENGTH_LONG).show();
                    Log.e(AppConstance.TAG_ME, "Pests: " + pests1.toString() );
                }

                UcenterMemberModel loggedInUser = SPUtil.builder(getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.UCENTER_MEMBER_MODEL, UcenterMemberModel.class);
                Log.e(AppConstance.TAG_ME, "UcenterMemberModel: " + loggedInUser.toString() );
                Toast.makeText(getApplicationContext(),loggedInUser.toString(),Toast.LENGTH_LONG).show();

                Integer test = SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.ISTEST, Integer.class);
                Log.e(AppConstance.TAG_ME, "test: " + test );
                Toast.makeText(getApplicationContext(),"test: " + test.toString(),Toast.LENGTH_LONG).show();
                SettingModel spSetting = SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).getData(AppConstance.SETTING_MODEL,SettingModel.class);
                Log.e(AppConstance.TAG_ME, "SettingModel: " + spSetting.toString() );
                Toast.makeText(getApplicationContext(),spSetting.toString(),Toast.LENGTH_LONG).show();
                List<ShpFile> shpFiles = SPUtil.builder(getApplication(),AppConstance.APP_SP).getDataList(AppConstance.SHP_FILE, ShpFile.class);
                Log.e(AppConstance.TAG_ME, "ShpFile: " + shpFiles.toString() );
                Toast.makeText(getApplicationContext(),shpFiles.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logout() {
        loginViewModel.logout();
        Intent intent = new Intent(MeActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void initMember(UcenterMemberModel umm) {
        RequestOptions options = new RequestOptions()
                .circleCrop();
        RequestOptions backOptions = new RequestOptions()
                .transforms(new BlurTransformation(), new GrayscaleTransformation());
        Glide.with(this).load("https://pestscontrol.oss-cn-hangzhou.aliyuncs.com/sys/blurImageView.jpg")
                .apply(backOptions)
                .into(blurImageView);

        Glide.with(this).load(umm.getAvatar())
                .apply(options)
                .into(avatarImageView);
        tvMobile.setText(umm.getMobile());
        tvNickname.setText(umm.getNickname());
//        etNickname.setText(umm.getNickname());
//        etMobile.setText(umm.getMobile());
//        etPassword.setText(umm.getPassword());
//        etGmtModified.setText(umm.getGmtModified());
        swDisabled.setChecked(umm.getIsDisabled());

    }
}