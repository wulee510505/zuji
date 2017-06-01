package com.wulee.administrator.zuji.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * SD card 操作
 * @author Richard.Ma
 */
public class SDCardUtils {

    /**
     * 判断sd卡是否有效
     * @return
     */
    public static boolean getExternalStorageCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 检查sd卡是否存储已满或者可用
     * @return
     */
    public static boolean diskSpaceAvailable() {
        File mSDCardDirectory = getESD();
        if (mSDCardDirectory == null) {
            return false;
        }
        StatFs fs = new StatFs(mSDCardDirectory.getAbsolutePath());
        // keep one free block
        return fs.getAvailableBlocks() > 1;
    }

    /**
     * 返回当前sdcard 可用空间大小， 单位byte
     */
    public static long getSdcardFreeSize() {
        // 取得SDCard当前的状态
        String sDcString = Environment.getExternalStorageState();
        long nSDFreeSize = 0;
        if (sDcString.equals(Environment.MEDIA_MOUNTED)) {
            // 取得sdcard文件路径
            File pathFile = getESD();
            StatFs statfs = new StatFs(pathFile.getPath());
            long nBlocSize = statfs.getBlockSize();
            long nAvailaBlock = statfs.getAvailableBlocks();
            nSDFreeSize = nAvailaBlock * nBlocSize;
        }
        return nSDFreeSize;
    }

    /**
     * 获取sd卡的路径
     * @return
     */
    public static String getESDString() {
        return getESD().toString();
    }

    /**
     * 获取sd卡文件路径
     * @return
     */
    public static File getESD() {
        return Environment.getExternalStorageDirectory();
    }
}
