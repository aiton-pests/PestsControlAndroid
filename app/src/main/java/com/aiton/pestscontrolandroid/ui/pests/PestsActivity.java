package com.aiton.pestscontrolandroid.ui.pests;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
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

import com.aiton.pestscontrolandroid.AppConstance;
import com.aiton.pestscontrolandroid.R;
import com.aiton.pestscontrolandroid.data.persistence.Pests;
import com.aiton.pestscontrolandroid.service.RetrofitUtil;
import com.aiton.pestscontrolandroid.ui.main.MainActivity;
import com.aiton.pestscontrolandroid.utils.SPUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.internal.LinkedTreeMap;

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

import cn.com.qiter.common.Result;

import cn.com.qiter.common.ordervo.UcenterMemberOrder;
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
//    private CameraXManager manager;
//    private PreviewView previewView;
    private ImageView ivFinish;
    private ImageView ivStump;
    private ImageView ivFell;
    private ImageView ivAddOperator;
    //pests 操作人员记录和自动显示
    private ArrayAdapter<String> adapter;
    // 拍照的requestCode  分别为Fell PIC
    private static final int CAMERA_REQUEST_CODE_FELL = 0x00000007;
    // 拍照的requestCode  分别为stump PIC
    private static final int CAMERA_REQUEST_CODE_STUMP = 0x00000008;
    // 拍照的requestCode  分别为finish PIC
    private static final int CAMERA_REQUEST_CODE_FINISH = 0x00000009;
    // 申请相机权限的requestCode
    private static final int PERMISSION_CAMERA_REQUEST_CODE_FELL = 0x00000012;
    private static final int PERMISSION_CAMERA_REQUEST_CODE_STUMP = 0x00000013;
    private static final int PERMISSION_CAMERA_REQUEST_CODE_FINISH = 0x00000014;



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

    /**
     * 处理权限申请的回调。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE_FELL) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，有调起相机拍照。
                openCamera(CAMERA_REQUEST_CODE_FELL);
            } else {
                //拒绝权限，弹出提示框。
                Toast.makeText(this,"拍照权限被拒绝",Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PERMISSION_CAMERA_REQUEST_CODE_STUMP){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，有调起相机拍照。
                openCamera(CAMERA_REQUEST_CODE_STUMP);
            } else {
                //拒绝权限，弹出提示框。
                Toast.makeText(this,"拍照权限被拒绝",Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == PERMISSION_CAMERA_REQUEST_CODE_FINISH){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，有调起相机拍照。
                openCamera(CAMERA_REQUEST_CODE_FINISH);
            } else {
                //拒绝权限，弹出提示框。
                Toast.makeText(this,"拍照权限被拒绝",Toast.LENGTH_LONG).show();
            }
        }
    }

    //用于保存拍照图片的uri
    private Uri mCameraUriFell ,mCameraUriStump, mCameraUriFinish;

    // 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
    private String mCameraImagePathFell,mCameraImagePathStump,mCameraImagePathFinish;

    // 是否是Android 10以上手机
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;

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
                    if (requestCode == CAMERA_REQUEST_CODE_FELL){
                        mCameraImagePathFell = photoFile.getAbsolutePath();
                    }else if (requestCode == CAMERA_REQUEST_CODE_STUMP){
                        mCameraImagePathStump = photoFile.getAbsolutePath();
                    }else if (requestCode == CAMERA_REQUEST_CODE_FINISH){
                        mCameraImagePathFinish = photoFile.getAbsolutePath();
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                    } else {
                        photoUri = Uri.fromFile(photoFile);
                    }
                }
            }
            if (requestCode == CAMERA_REQUEST_CODE_FELL){
                mCameraUriFell = photoUri;
            }else if (requestCode == CAMERA_REQUEST_CODE_STUMP){
                mCameraUriStump = photoUri;
            }else if (requestCode == CAMERA_REQUEST_CODE_FINISH){
                mCameraUriFinish = photoUri;
            }

            if (photoUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(captureIntent, requestCode);
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
     * user转换为file文件
     * 返回值为file类型
     *
     * @param uri
     * @return
     */
    private File uri2File(Activity activity, Uri uri) {
        String img_path;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = activity.managedQuery(uri, proj, null, null, null);
        if (actualimagecursor == null) {
            img_path = uri.getPath();
        } else {
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            img_path = actualimagecursor.getString(actual_image_column_index);
        }
        File file = new File(img_path + ".jpg");
        return file;
    }


    public File uriToFileApiQ(Uri uri) {
        File file = null;
        //android10以上转换
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //把文件复制到沙盒目录
            ContentResolver contentResolver = PestsActivity.this.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                try {
                    InputStream is = contentResolver.openInputStream(uri);
                    File cache = new File(PestsActivity.this.getExternalCacheDir().getAbsolutePath(), Math.round((Math.random() + 1) * 1000) + displayName);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE_FELL) {
            if (resultCode == RESULT_OK) {
                if (isAndroidQ) {
                    // Android 10 使用图片uri加载
                    Log.e(TAG, "onActivityResult: " + mCameraUriFell.toString() );
                    ivFell.setImageURI(mCameraUriFell);
                } else {
                    // 使用图片路径加载
                    Log.e(TAG, "onActivityResult: " +mCameraImagePathFell );
                    ivFell.setImageBitmap(BitmapFactory.decodeFile(mCameraImagePathFell));
                }
            } else {
                Toast.makeText(this,"取消",Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CAMERA_REQUEST_CODE_STUMP) {
            if (resultCode == RESULT_OK) {
                if (isAndroidQ) {
                    // Android 10 使用图片uri加载
                    Log.e(TAG, "onActivityResult: " + mCameraUriStump.toString() );
                    ivStump.setImageURI(mCameraUriStump);
                } else {
                    // 使用图片路径加载
                    Log.e(TAG, "onActivityResult: " +mCameraImagePathStump );
                    ivStump.setImageBitmap(BitmapFactory.decodeFile(mCameraImagePathStump));
                }
            } else {
                Toast.makeText(this,"取消",Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CAMERA_REQUEST_CODE_FINISH) {
            if (resultCode == RESULT_OK) {
                if (isAndroidQ) {
                    // Android 10 使用图片uri加载
                    Log.e(TAG, "onActivityResult: " + mCameraUriFinish.toString() );
                    ivFinish.setImageURI(mCameraUriFinish);
                } else {
                    // 使用图片路径加载
                    Log.e(TAG, "onActivityResult: " +mCameraImagePathFinish );
                    ivFinish.setImageBitmap(BitmapFactory.decodeFile(mCameraImagePathFinish));
                }
            } else {
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
            String name = "lrh鲁仁华";
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
        etPestsCodeInt = findViewById(R.id.et_pests_code_int_it);
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
        etQrcode = findViewById(R.id.et_qrcode);
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
                    Log.e(TAG, "onChanged: " + pests.toString() );

                }
            }
        });
//        pestsViewModel.getCodeInt().observe(this, new Observer<Integer>() {
//            @Override
//            public void onChanged(Integer integer) {
//                if (integer != null){
//                    if (integer.compareTo(0) ==0){
//                        Toast.makeText(getApplicationContext(),"二维码不存在，请联系管理员！",Toast.LENGTH_SHORT).show();
//                    }else {
//                        etPestsCodeInt.setText(String.valueOf(integer));
//                    }
//
//                }
//            }
//        });
        PestsControlModel pestsModel = (PestsControlModel) getIntent().getSerializableExtra(AppConstance.PESTSMODEL);
        etQrcode.setText(pestsModel.getQrcode());
//        if (!StrUtil.isNullOrUndefined(pestsModel.getQrcode()) && !StrUtil.isEmpty(pestsModel.getQrcode())){
//            pestsViewModel.updateCodeInt(pestsModel.getQrcode());
//        }
        HashMap<String,String> fam = (HashMap<String, String>) getIntent().getSerializableExtra(AppConstance.FEATURE_ATTRIBUTE_MAP);
        etDb.setText(fam.get(AppConstance.DBH));
        etXb.setText(fam.get(AppConstance.XBH));
        etVillage.setText(fam.get(AppConstance.CGQNAME));
        etTown.setText(fam.get(AppConstance.JYXZCNAME));
        tv_address.setText(fam.get(AppConstance.JYXZCNAME) + fam.get(AppConstance.CGQNAME));
//        previewView = findViewById(R.id.previewView);
//        manager = new CameraXManager(this,previewView);
//        manager.startCamera();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pests[] p = pestsViewModel.findAllObject();
                Log.e(TAG, "onClick: " + p.toString() );
                Snackbar.make(view, getResources().getString(R.string.pests_cancel), Snackbar.LENGTH_LONG)
                        .setAction("Action", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(PestsActivity.this, MainActivity.class);
                                startActivity(intent);
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
                if (isAndroidQ){
                    if (mCameraUriFell != null){
                        File fi = uriToFileApiQ(mCameraUriFell);
                        String url = fi.getAbsolutePath();
                        pests.setFellPic(url);
                        Log.e(TAG, "onClick: " + fi.toString() );
                    }else{
                        Toast.makeText(PestsActivity.this, "请先拍《砍倒照片》", Toast.LENGTH_SHORT).show();
                    }
                    if (mCameraUriFinish != null){
                        File fis = uriToFileApiQ(mCameraUriFinish);
                        String url = fis.getAbsolutePath();
                        pests.setStumpPic(url);
                    }else{
                        Toast.makeText(PestsActivity.this, "请先拍《树桩照片》", Toast.LENGTH_SHORT).show();
                    }
                    if (mCameraUriStump != null){
                        File fiss = uriToFileApiQ(mCameraUriStump);
                        String url = fiss.getAbsolutePath();
                        pests.setFinishPic(url);
                    }else{
                        Toast.makeText(PestsActivity.this, "请先拍《处理好照片》", Toast.LENGTH_SHORT).show();
                    }


                }else{
                    if (mCameraImagePathFinish != null){
                        pests.setFinishPic(mCameraImagePathFinish);
                    }
                    if (mCameraImagePathStump != null){
                        pests.setStumpPic(mCameraImagePathStump);
                    }
                    if (mCameraImagePathFell != null){
                        pests.setFellPic(mCameraImagePathFell);
                    }

//                    mCutUri = Uri.fromFile(mCameraImagePathFinish);
//                    mCutFile = imgFile;
                }

                pests.setUpdateServer(false);
                pestsViewModel.insert(pests);
                setDefaultOper(pests.getOperator());
                Intent intent = new Intent(PestsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        ibFellPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndCamera(CAMERA_REQUEST_CODE_FELL,PERMISSION_CAMERA_REQUEST_CODE_FELL);
//                manager.takePicture();
            }
        });

        ibFinishPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndCamera(CAMERA_REQUEST_CODE_FINISH,PERMISSION_CAMERA_REQUEST_CODE_FINISH);
            }
        });
        ibStumpPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionAndCamera(CAMERA_REQUEST_CODE_STUMP,PERMISSION_CAMERA_REQUEST_CODE_STUMP);
            }
        });

        getDict();
        displayDefaultOperator();
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