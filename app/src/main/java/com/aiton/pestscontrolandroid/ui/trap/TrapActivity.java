package com.aiton.pestscontrolandroid.ui.trap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.persistence.Trap;
import com.aiton.pestscontrolandroid.ui.main.MainActivity;
import com.aiton.pestscontrolandroid.utils.SPUtil;
import com.google.android.material.snackbar.Snackbar;

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

import cn.com.qiter.pests.PestsModel;
import cn.com.qiter.pests.TrapModel;
import cn.com.qiter.pests.UcenterMemberModel;
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
    private String mCameraImagePathPIC1,mCameraImagePathPIC2;

    // 拍照的requestCode  分别为Fell PIC
    private static final int CAMERA_REQUEST_CODE_PIC1 = 0x00000017;
    // 拍照的requestCode  分别为stump PIC
    private static final int CAMERA_REQUEST_CODE_PIC2 = 0x00000018;
    TrapViewModel trapViewModel;

    //用于保存拍照图片的uri
    private Uri mCameraUriPIC1 ,mCameraUriPIC2;
    // 申请相机权限的requestCode
    private static final int PERMISSION_CAMERA_REQUEST_CODE_PIC1 = 0x00000112;
    private static final int PERMISSION_CAMERA_REQUEST_CODE_PIC2 = 0x00000113;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trap);
        trapViewModel = new ViewModelProvider(this).get(TrapViewModel.class);

        initRemarkDataSource();
        etTrapCodeInt = findViewById(R.id.et_trap_code_int);
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
                checkPermissionAndCamera(CAMERA_REQUEST_CODE_PIC1,PERMISSION_CAMERA_REQUEST_CODE_PIC1);
            }
        });
        ibSecond = findViewById(R.id.trap_second);
        ibSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndCamera(CAMERA_REQUEST_CODE_PIC2,PERMISSION_CAMERA_REQUEST_CODE_PIC2);
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
        trapRemark.setText("gss挂设");
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
        TrapModel model = (TrapModel) getIntent().getSerializableExtra(AppConstance.TRAPMODEL);
        etQrcode.setText(model.getQrcode());
//        trapViewModel.getCodeInt().observe(this, new Observer<Integer>() {
//            @Override
//            public void onChanged(Integer integer) {
//                if (integer != null){
//                    if (integer.compareTo(0) == 0){
//                        Toast.makeText(getApplicationContext(),"二维码不存在，请联系管理员！",Toast.LENGTH_SHORT).show();
//                    } else {
//                        etTrapCodeInt.setText(String.valueOf(integer));
//                    }
//
//                }
//            }
//        });
        if (!StrUtil.isNullOrUndefined(model.getQrcode()) && !StrUtil.isEmpty(model.getQrcode())){
//            trapViewModel.updateCodeInt(model.getQrcode());
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
                                Intent intent = new Intent(TrapActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }).show();
            }
        });
        btnSave = findViewById(R.id.trap_submit);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UcenterMemberModel userModel = SPUtil.builder(getApplication().getApplicationContext(), AppConstance.APP_SP).getData(AppConstance.UCENTER_MEMBER_MODEL, UcenterMemberModel.class);
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
                if (isAndroidQ){
                    if (mCameraUriPIC1 != null){
                        File fi = uriToFileApiQ(mCameraUriPIC1);
                        if (fi != null){
                            String url = fi.getAbsolutePath();
                            trap.setPic1(url);
                            Log.e(TAG, "onClick: " + fi.toString() );
                        }
                    }else{
                        Toast.makeText(TrapActivity.this, "请先拍《第一张照片》", Toast.LENGTH_SHORT).show();
                    }
                    if (mCameraUriPIC2 != null){
                        File fis = uriToFileApiQ(mCameraUriPIC2);
                        if (fis != null) {
                            String url = fis.getAbsolutePath();
                            trap.setPic2(url);
                        }
                    }else{
                        Toast.makeText(TrapActivity.this, "请先拍《第二张照片》", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if (mCameraImagePathPIC1 != null){
                        trap.setPic1(mCameraImagePathPIC1);
                    }
                    if (mCameraImagePathPIC2 != null){
                        trap.setPic2(mCameraImagePathPIC2);
                    }
                }

                trap.setUpdateServer(false);
                trapViewModel.insert(trap);
                setDefaultOper(trap.getOperator());
                Intent intent = new Intent(TrapActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        displayDefaultOperator();
    }

    private void setDefaultOper(String operator) {
        SPUtil.builder(getApplicationContext(),AppConstance.APP_SP).setData(AppConstance.OPERATOR_DEFAULT,operator);
    }


    public File uriToFileApiQ(Uri uri) {
        File file = null;
        //android10以上转换
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //把文件复制到沙盒目录
            ContentResolver contentResolver = TrapActivity.this.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                try {
                    InputStream is = contentResolver.openInputStream(uri);
                    File cache = new File(TrapActivity.this.getExternalCacheDir().getAbsolutePath(), Math.round((Math.random() + 1) * 1000) + displayName);
                    FileOutputStream fos = new FileOutputStream(cache);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        FileUtils.copy(is, fos);
                    }
                    file = cache;
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }
    // 是否是Android 10以上手机
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;
    /**
     * 检查权限并拍照。
     * 调用相机前先检查权限。
     */
    private void checkPermissionAndCamera(int cameraRequestCode,int permissionRequestCode) {
        int hasCameraPermission = ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.CAMERA);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
            //有调起相机拍照。
            openCamera(cameraRequestCode);
        } else {
            //没有权限，申请权限。
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},
                    permissionRequestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE_PIC1) {
            if (resultCode == RESULT_OK) {
                if (isAndroidQ) {
                    // Android 10 使用图片uri加载
                    Log.e(TAG, "onActivityResult: " + mCameraUriPIC1.toString() );
                    ivFirst.setImageURI(mCameraUriPIC1);
                } else {
                    // 使用图片路径加载
                    Log.e(TAG, "onActivityResult: " +mCameraImagePathPIC1 );
                    ivFirst.setImageBitmap(BitmapFactory.decodeFile(mCameraImagePathPIC1));
                }
            } else {
                Toast.makeText(this,"取消",Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CAMERA_REQUEST_CODE_PIC2) {
            if (resultCode == RESULT_OK) {
                if (isAndroidQ) {
                    // Android 10 使用图片uri加载
                    Log.e(TAG, "onActivityResult: " + mCameraUriPIC2.toString() );
                    ivSecond.setImageURI(mCameraUriPIC2);
                } else {
                    // 使用图片路径加载
                    Log.e(TAG, "onActivityResult: " +mCameraImagePathPIC2);
                    ivSecond.setImageBitmap(BitmapFactory.decodeFile(mCameraImagePathPIC2));
                }
            } else {
                Toast.makeText(this,"取消",Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 处理权限申请的回调。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE_PIC1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，有调起相机拍照。
                openCamera(CAMERA_REQUEST_CODE_PIC1);
            } else {
                //拒绝权限，弹出提示框。
                Toast.makeText(this,"拍照权限被拒绝",Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PERMISSION_CAMERA_REQUEST_CODE_PIC2){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，有调起相机拍照。
                openCamera(CAMERA_REQUEST_CODE_PIC2);
            } else {
                //拒绝权限，弹出提示框。
                Toast.makeText(this,"拍照权限被拒绝",Toast.LENGTH_LONG).show();
            }
        }
    }
    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }


    /**
     * 创建保存图片的文件
     */
    private File createImageFile() throws IOException {
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, imageName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }
    /**
     * 调起相机拍照
     */
    private void openCamera(int requestCode) {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断是否有相机
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            Uri photoUri = null;

            if (isAndroidQ) {
                // 适配android 10
                photoUri = createImageUri();
            } else {
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (photoFile != null) {
                    if (requestCode == CAMERA_REQUEST_CODE_PIC1){
                        mCameraImagePathPIC1 = photoFile.getAbsolutePath();
                    }else if (requestCode == CAMERA_REQUEST_CODE_PIC2){
                        mCameraImagePathPIC2 = photoFile.getAbsolutePath();
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                    } else {
                        photoUri = Uri.fromFile(photoFile);
                    }
                }
            }
            if (requestCode == CAMERA_REQUEST_CODE_PIC1){
                mCameraUriPIC1 = photoUri;
            }else if (requestCode == CAMERA_REQUEST_CODE_PIC2){
                mCameraUriPIC2 = photoUri;
            }

            if (photoUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(captureIntent, requestCode);
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