package com.aiton.pestscontrolandroid.utils;

import com.aiton.pestscontrolandroid.data.model.ShpFile;

import java.io.File;
import java.security.cert.Extension;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {


    public static List<String> getFiles(String Path, String Extension, boolean IsIterative)  //搜索目录，扩展名，是否进入子文件夹
    {
        File[] files = new File(Path).listFiles();
        List<String> lstFile = new ArrayList<String>();  //结果 List
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isFile()) {
                if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension))  //判断扩展名
                    lstFile.add(f.getPath());

                if (!IsIterative)
                    break;
            } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1)  //忽略点文件（隐藏文件/文件夹）
                getFiles(f.getPath(), Extension, IsIterative);
        }
        return lstFile;
    }
    public static List<ShpFile> convertGeoFile2ShpFile(List<File> files){
        List<ShpFile> shpFiles = new ArrayList<>();
        for (File f :
                files) {
            ShpFile s = new ShpFile();
            s.setUrl(f.getAbsolutePath());
            s.setSelected(false);
            s.setOther(f.getName());
            shpFiles.add(s);

        }
        return shpFiles;
    }
    public static List<File> filterGeoDatabase(List<File> files,String extension){
        List<File> lstFile = new ArrayList<File>();  //结果 List
        for (File f :
                files) {
            if (f.isFile()) {
                if (f.getPath().substring(f.getPath().length() - extension.length()).equals(extension))  //判断扩展名
                    lstFile.add(f);
            }
        }
        return lstFile;
    }


    public static List<File> traverseFolder(List<File> fileList,String path) {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
                System.out.println("文件夹是空的!");
                return fileList;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        System.out.println("文件夹:" + file2.getAbsolutePath());
                        traverseFolder(fileList,file2.getAbsolutePath());
                    } else {
                        fileList.add(file2);
                        System.out.println("文件:" + file2.getAbsolutePath());
                    }
                }
                return fileList;
            }
        } else {
            return fileList;
        }
    }
}
