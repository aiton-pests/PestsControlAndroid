package com.aiton.pestscontrolandroid.service;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.meituan.robust.Patch;
import com.meituan.robust.PatchManipulate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanjunhua on 2017/5/17.
 */

public class PatchManipulateImp extends PatchManipulate {
    @Override
    protected List<Patch> fetchPatchList(Context context) {

        Patch patch = new Patch();
        patch.setName("test patch");

        StringBuilder path = new StringBuilder();
        path.append(Environment.getExternalStorageDirectory()
                .getPath());
        path.append(File.separator);// '/'
        path.append("HotFix");// /mnt/sdcard/HotFix
        path.append(File.separator);
        path.append("patch");// /mnt/sdcard/HotFix/patch

        patch.setLocalPath(path.toString());
        Log.e("error-hotfix", "PatchManipulateImp 地址="+path);

        patch.setPatchesInfoImplClassFullName("com.draem.application20170516.PatchesInfoImpl");
        List<Patch> patchList = new ArrayList<>();
        patchList.add(patch);
        return patchList;
    }


    @Override
    protected boolean verifyPatch(Context context, Patch patch) {
        //do your verification, put the real patch to patch
        //放到app的私有目录
        StringBuilder path = new StringBuilder();
        path.append(context.getCacheDir());
        path.append(File.separator);// '/'
        path.append("HotFix");// /mnt/sdcard/HotFix
        path.append(File.separator);
        path.append("patch");// /mnt/sdcard/HotFix/patch
        patch.setTempPath(path.toString());
        //in the sample we just copy the file
        try {
            Log.e("error-hotfix", "patch.getLocalPath="+patch.getLocalPath()+"--->patch.getTempPath="+patch.getTempPath());
            copy(patch.getLocalPath(), patch.getTempPath());
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("copy source patch to local patch error, no patch execute in path "+patch.getTempPath());
        }

        return true;
    }

    @Override
    protected boolean ensurePatchExist(Patch patch) {
        return true;
    }



    public void copy(String srcPath,String dstPath) throws IOException {
        File src = new File(srcPath);
        if(!src.exists()){
            try {
                Log.e("error-hitfix", "资源不存在哦  srcPath="+srcPath);
                Log.e("error-hitfix", "资源不存在哦  srcPath="+src.toString());
                Log.e("error-hitfix", "资源不存在哦  srcPath="+src.length());
            } catch (Exception e) {
                e.printStackTrace();
            }

            throw new RuntimeException("source patch does not exist ");
        }
        File dst=new File(dstPath);
        if(!dst.getParentFile().exists()){
            dst.getParentFile().mkdirs();
        }
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}