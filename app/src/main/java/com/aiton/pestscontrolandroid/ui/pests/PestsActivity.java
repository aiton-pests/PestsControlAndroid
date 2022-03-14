package com.aiton.pestscontrolandroid.ui.pests;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.CameraActivity;
import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.model.Result;
import com.aiton.pestscontrolandroid.data.model.UcenterMemberOrder;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.service.PestsOnceWork;
import com.aiton.pestscontrolandroid.service.RetrofitUtil;
import com.aiton.pestscontrolandroid.utils.SPUtil;
import com.bumptech.glide.Glide;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cn.com.qiter.common.vo.PestsControlModel;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PestsActivity extends AppCompatActivity {
    private static final String TAG = "PestsActivity";
    private Button btnCancel,btnSave;
    private EditText etQrcode,etVillage,etDb,etXb,etTown,etPestsCodeInt;
    private ImageButton ibFellPic,ibStumpPic,ibFinishPic;
    private Spinner spPestsType, spTreeWalk, spBags;
    private AutoCompleteTextView acOperator;
    private PestsViewModel pestsViewModel;
    private TextView tv_address;
    private ImageView ivFinish;
    private ImageView ivStump;
    private ImageView ivFell;
    private ImageView ivAddOperator;
    //pests 操作人员记录和自动显示
    private ArrayAdapter<String> adapter;
    private Dialog mDialog;
    private String fellPath ="";
    private String stumpPath ="";
    private String finishPath ="";
    WorkManager workmanager;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstance.CAMERAX_REQUEST_CODE_FELL){
            if (resultCode == RESULT_OK) {
                String url = data.getStringExtra("data");
                Glide.with(this).load(url).into(ivFell);
                fellPath = url;
//                ivFell.setImageURI(Uri.fromFile(new File(url)));
            }else {
                Toast.makeText(this,"取消",Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == AppConstance.CAMERAX_REQUEST_CODE_STUMP){
            if (resultCode == RESULT_OK) {
                String url = data.getStringExtra("data");
                stumpPath = url;
                Glide.with(this).load(url).into(ivStump);
//                ivStump.setImageBitmap(BitmapFactory.decodeFile(url));
            }else {
                Toast.makeText(this,"取消",Toast.LENGTH_LONG).show();
            }
        }else if (requestCode == AppConstance.CAMERAX_REQUEST_CODE_FINISH){
            if (resultCode == RESULT_OK) {
                String url = data.getStringExtra("data");
                finishPath = url;
                Glide.with(this).load(url).into(ivFinish);
//                ivFinish.setImageBitmap(BitmapFactory.decodeFile(url));
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
    private void setDefaultOper(String operator) {
        SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).setData(AppConstance.PESTS_OPERATOR_DEFAULT,operator);
    }
    public void displayDefaultOperator(){
        String defaultOper = SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).getData(AppConstance.PESTS_OPERATOR_DEFAULT,String.class);
        acOperator.setText(defaultOper);
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
    private void getDict(){
        RetrofitUtil.getInstance().getDictService().getDictByName(AppConstance.DICT_NAME_PESTS_TYPE).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.rxjava3.core.Observer<Result>() {
                    Disposable disposable;
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(@NonNull Result result) {
                        if (result.getSuccess()) {
                            List<LinkedTreeMap> list = (List) result.getData().get("items");
                            List<String> strings = new ArrayList<>();
                            for (LinkedTreeMap map :
                                    list) {
                                strings.add(String.valueOf(map.get("value")));
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, strings);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spPestsType.setAdapter(adapter);
                            String defaultSelection = SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).getData(AppConstance.PESTS_TYPE_DEFAULT,String.class);
                            if (StrUtil.isEmpty(defaultSelection) || StrUtil.isNullOrUndefined(defaultSelection)){
                                defaultSelection = "0";
                            }
                            spPestsType.setSelection(Integer.valueOf(defaultSelection),true);
                            // Log.e(AppConstance.TAG, "onNext: " + strings.toString());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        disposable.dispose();

                        String[] mItems = getResources().getStringArray(R.array.array_pests_type);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, mItems);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spPestsType.setAdapter(adapter);
                        String defaultSelection = SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).getData(AppConstance.PESTS_TYPE_DEFAULT,String.class);
                        if (StrUtil.isEmpty(defaultSelection) || StrUtil.isNullOrUndefined(defaultSelection)){
                            defaultSelection = "0";
                        }
                        spPestsType.setSelection(Integer.valueOf(defaultSelection),true);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pests);
        workmanager = WorkManager.getInstance(this);

        etQrcode = findViewById(R.id.et_pests_qrcode);
        pestsViewModel = new ViewModelProvider(this).get(PestsViewModel.class);
        ivAddOperator = findViewById(R.id.iv_add_operator);
        acOperator = findViewById(R.id.ac_operator);
        /*
         * 1.使用手工方式的list数组适配器
         */
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, getDataSource());
        acOperator.setAdapter(adapter);
        tv_address = findViewById(R.id.tv_address);
        ibFellPic = findViewById(R.id.ib_fell_pic);
        ibStumpPic = findViewById(R.id.ib_stump_pic);
        ibFinishPic = findViewById(R.id.ib_finish_pic);
        etPestsCodeInt = findViewById(R.id.et_code_int);
        etVillage = findViewById(R.id.et_village);
        etTown = findViewById(R.id.et_town);
        etDb = findViewById(R.id.et_db);
        etXb = findViewById(R.id.et_xb);
        spBags = findViewById(R.id.sp_bags);
        spTreeWalk = findViewById(R.id.sp_tree_walk);
        spPestsType = findViewById(R.id.sp_pests_type);
        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        ivFell = findViewById(R.id.iv_fell);
        ivFinish = findViewById(R.id.iv_finish);
        ivStump = findViewById(R.id.iv_stump);

        ivAddOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String operator = acOperator.getText().toString();
                Toast.makeText(getApplicationContext(),"Operator ; " + operator , Toast.LENGTH_LONG).show();
                Log.e(TAG, "operator: " + operator);
                setDataSource(operator);
            }
        });
        pestsViewModel.findAll().observe(this, new Observer<List<Pests>>() {
            @Override
            public void onChanged(List<Pests> pests) {
                if (pests != null){
//                    Log.e(TAG, "onChanged: " + pests.toString() );

                }
            }
        });
        pestsViewModel.getCodeInt().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != null){
                    if (integer.compareTo(0) ==0){
                        Toast.makeText(getApplicationContext(),"二维码不存在，请联系管理员！",Toast.LENGTH_SHORT).show();
                    }else {
                        etPestsCodeInt.setText(String.valueOf(integer));
                    }

                }
            }
        });
        PestsControlModel pestsModel = (PestsControlModel) getIntent().getSerializableExtra(AppConstance.PESTSMODEL);
        etQrcode.setText(pestsModel.getQrcode());
        if (!StrUtil.isNullOrUndefined(pestsModel.getQrcode()) && !StrUtil.isEmpty(pestsModel.getQrcode())){
            pestsViewModel.updateCodeInt(pestsModel.getQrcode());
        }
        HashMap<String,String> fam = (HashMap<String, String>) getIntent().getSerializableExtra(AppConstance.FEATURE_ATTRIBUTE_MAP);
        etDb.setText(fam.get(AppConstance.DBH));
        etXb.setText(fam.get(AppConstance.XBH));
        etVillage.setText(fam.get(AppConstance.CGQNAME));
        etTown.setText(fam.get(AppConstance.JYXZCNAME));
        tv_address.setText(fam.get(AppConstance.JYXZCNAME) + fam.get(AppConstance.CGQNAME));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pests[] p = pestsViewModel.findAllObject();
//                Log.e(TAG, "onClick: " + p.toString() );
                Snackbar.make(view, getResources().getString(R.string.pests_cancel), Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                                Intent intent = new Intent(PestsActivity.this, MainActivity.class);
//                                startActivity(intent);
                                Intent intent = getIntent();
                                intent.putExtra("data", "NO");
                                setResult(RESULT_CANCELED,intent);
                                finish();
                            }
                        }).show();

            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                UcenterMemberOrder userModel = SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.UCENTER_MEMBER_MODEL, UcenterMemberOrder.class);
                Pests pests = new Pests();
                pests.setDeviceId(userModel.getNickname());
                pests.setDb(fam.get(AppConstance.DBH));
                pests.setXb(fam.get(AppConstance.XBH));
                pests.setVillage(fam.get(AppConstance.CGQNAME));
                pests.setTown(fam.get(AppConstance.JYXZCNAME));
                pests.setBagNumber(spBags.getSelectedItem().toString());
                pests.setOperator(acOperator.getText().toString());

                SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).setData(AppConstance.PESTS_TYPE_DEFAULT,String.valueOf(spPestsType.getSelectedItemPosition()));
                pests.setPestsType(spPestsType.getSelectedItem().toString());
                pests.setQrcode(etQrcode.getText().toString());
                pests.setUserId(userModel.getId());
                pests.setStime(DateUtil.formatDateTime(DateUtil.date()));
                pests.setTreeWalk(spTreeWalk.getSelectedItem().toString());
                pests.setPositionError(String.valueOf(new Random().nextInt(10)));
                pests.setLongitude(Double.valueOf(fam.get(AppConstance.LONGITUDE)));
                pests.setLatitude(Double.valueOf(fam.get(AppConstance.LATIDUTE)));
                pests.setCodeInt(etPestsCodeInt.getText().toString());
                pests.setFellPic(fellPath);
                pests.setStumpPic(stumpPath);
                pests.setFinishPic(finishPath);
                pests.setUpdateServer(false);
                pestsViewModel.insert(pests);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         *要执行的操作
                         */
                        Pests p = pestsViewModel.findByLatLonAndUserIdAndStime(pests.getLatitude(),pests.getLongitude(),pests.getStime(),pests.getUserId(),pests.getQrcode());
                        workManagerUpload(p);
                        Log.e(TAG, "run: 延时3000");
                    }
                }, 3000);//3秒后执行Runnable中的run方法

                //设置默认操作员
                setDefaultOper(pests.getOperator());
//                Intent intent = new Intent(PestsActivity.this,MainActivity.class);
//                startActivity(intent);

                Intent intent = getIntent();
                intent.putExtra("data", pests);
                setResult(RESULT_OK,intent);
                finish();

            }
        });
        ibFellPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PestsActivity.this, CameraActivity.class);
                startActivityForResult(intent,AppConstance.CAMERAX_REQUEST_CODE_FELL);
            }
        });

        ibFinishPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PestsActivity.this, CameraActivity.class);
                startActivityForResult(intent,AppConstance.CAMERAX_REQUEST_CODE_FINISH);
            }
        });
        ibStumpPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PestsActivity.this, CameraActivity.class);
                startActivityForResult(intent,AppConstance.CAMERAX_REQUEST_CODE_STUMP);
            }
        });

        getDict();
        displayDefaultOperator();
    }
    private static ObjectMapper mapper = new ObjectMapper();
    private void workManagerUpload(Pests pests){
        PestsControlModel pestsControlModel = new PestsControlModel();
        try {
            pestsControlModel.setAppId(pests.getId());
            pestsControlModel.setBagNumber(pests.getBagNumber());
            pestsControlModel.setDb(pests.getDb());
            pestsControlModel.setDeviceId(pests.getDeviceId());
            pestsControlModel.setFellPic(pests.getFellPic());
            pestsControlModel.setFinishPic(pests.getFinishPic());
            pestsControlModel.setIsChecked(pests.isChecked());
            pestsControlModel.setLatitude(pests.getLatitude());
            pestsControlModel.setLongitude(pests.getLongitude());
            pestsControlModel.setOperator(pests.getOperator());
            pestsControlModel.setPestsType(pests.getPestsType());
            pestsControlModel.setPositionError(pests.getPositionError());
            pestsControlModel.setQrcode(pests.getQrcode());
            pestsControlModel.setStime(pests.getStime());
            pestsControlModel.setStumpPic(pests.getStumpPic());
            pestsControlModel.setTown(pests.getTown());
            pestsControlModel.setTreeWalk(pests.getTreeWalk());
            pestsControlModel.setUserId(pests.getUserId());
            pestsControlModel.setVillage(pests.getVillage());
            pestsControlModel.setXb(pests.getXb());

            String pcmStr = mapper.writeValueAsString(pestsControlModel);
            // 数据
            Data data = new Data.Builder().putString(AppConstance.WORKMANAGER_KEY, pcmStr).build();
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();
            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(PestsOnceWork.class)
                    .setInputData(data) // 数据的携带
                    .setConstraints(constraints)
                    .build();

// 【状态机】  为什么一直都是 ENQUEUE，因为 你是轮询的任务，所以你看不到 SUCCESS     [如果你是单个任务，就会看到SUCCESS]
            // 监听状态
            workmanager.getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                    .observe(PestsActivity.this, new Observer<WorkInfo>() {
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
    /**
     * 禁用返回键
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            //do something.
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

}