package com.aiton.pestscontrolandroid.ui.me;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import com.aiton.pestscontrolandroid.data.model.UcenterMemberOrder;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.ui.login.LoginActivity;
import com.aiton.pestscontrolandroid.ui.login.LoginViewModel;
import com.aiton.pestscontrolandroid.ui.main.MainActivity;
import com.aiton.pestscontrolandroid.ui.main.MainViewModel;
import com.aiton.pestscontrolandroid.ui.pests.PestsViewModel;
import com.aiton.pestscontrolandroid.utils.SPUtil;

import java.util.List;


public class MeActivity extends AppCompatActivity {
    MeViewModel meViewModel ;
    private MainViewModel mainViewModel;
    private PestsViewModel pestsViewModel;
    LoginViewModel loginViewModel;
    private ImageView blurImageView;
    private ImageView avatarImageView;
    private Switch swDisabled,swTest,swAbout,swTiandi;
    TextView tvMobile,tvNickname;
    Button btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me);
        avatarImageView = findViewById(R.id.h_head);
        swTiandi = findViewById(R.id.sw_tiandi);
        swDisabled = findViewById(R.id.sw_disabled);
        swAbout = findViewById(R.id.sw_about);
        tvMobile = findViewById(R.id.mobile);
        tvNickname = findViewById(R.id.user_name);
        swTest = findViewById(R.id.sw_test);
        btnLogout = findViewById(R.id.btn_logout);
        swTiandi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                meViewModel.saveTianDiMap2SHP(isChecked);
               // meViewModel.getTiandiMap().setValue(isChecked);
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
        meViewModel.loadTest();
        meViewModel.getIsTest().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                swTest.setChecked(aBoolean);
            }
        });
        swTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                meViewModel.setTest(isChecked);
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

                List<String> operators = SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).getDataList(AppConstance.OPERATOR,String.class);

                for (String op :
                        operators) {
                    Log.e("PESTS", "onClick: " + op );
                }

                UcenterMemberOrder loggedInUser = SPUtil.builder(getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.UCENTER_MEMBER_MODEL, UcenterMemberOrder.class);
                if (loggedInUser != null){
                    Log.e(AppConstance.TAG_ME, "UcenterMemberModel: " + loggedInUser.toString() );
                    Toast.makeText(getApplicationContext(),loggedInUser.toString(),Toast.LENGTH_LONG).show();
                }
                Boolean tiandi = SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.TIANDIMAP, Boolean.class);
                Log.e(AppConstance.TAG_ME, "天地图/ArcGIS: " + tiandi );
                Toast.makeText(getApplicationContext(),"天地图/ArcGIS: " + tiandi,Toast.LENGTH_LONG).show();
                Boolean test = SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.ISTEST, Boolean.class);
                Log.e(AppConstance.TAG_ME, "测试用户: " + test );
                Toast.makeText(getApplicationContext(),"测试用户: " + test,Toast.LENGTH_LONG).show();
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
        swDisabled.setChecked(umm.getDeleted());

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