/**
 * Title: ImageUtils.java<br/>
 * Package com.maple.common.utils<br/>
 * Description: TODO<br/>
 * author maple<br/>
 * version V1.0<br/>
 * date 2014-2-17 上午11:46:12<br/>
 * Copyright 中国合伙人 2014<br/>
 */
package com.android.msx7.followinstagram.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * ProjectName：AndroidCommon<br/>
 * ClassName：ImageUtils<br/>
 * Description：图片处理工具类<br/>
 * Author：maple<br/>
 * CreateTime：2014-2-17 上午11:46:12<br/>
 *
 * @version v1.0<br/>
 */
public class ImageUtils {

    /**
     * Title: calculateInSampleSize<br/>
     * Description: 计算源图片到目标图片的缩放倍数<br/>
     * param @param options<br/>
     * param @param reqWidth目标图片的宽度<br/>
     * param @param reqHeight目标图片的高度<br/>
     * param @return<br/>
     * return int<br/>
     * throws<br/>
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * Title: decodeSampledBitmapFromResource<br/>
     * Description:把res目录下指定id的图片进行合理的缩放<br/>
     * param @param res<br/>
     * param @param resId资源图片id<br/>
     * param @param reqWidth目标图片的宽度<br/>
     * param @param reqHeight目标图片的高度<br/>
     * param @return<br/>
     * return Bitmap<br/>
     * throws<br/>
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用上面定义的方法计算inSampleSize值
        if (reqWidth != 0 && reqHeight != 0) {
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        }
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Title: decodeSampledBitmapFromSdCard<br/>
     * Description: 把sd卡指定路径的图片进行合理的缩放<br/>
     * param @param pathName源图片路径<br/>
     * param @param reqWidth目标图片的宽度<br/>
     * param @param reqHeight目标图片的高度<br/>
     * param @return<br/>
     * return Bitmap<br/>
     * throws<br/>
     */
    public static Bitmap decodeSampledBitmapFromSdCard(String pathName, int reqWidth, int reqHeight) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        // 调用上面定义的方法计算inSampleSize值
        if (reqWidth != 0 && reqHeight != 0) {
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        }
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    /**
     * 把Bitmap转Byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 把字节数组保存为一个文件
     */
    public static File getFileFromBytes(byte[] b, String outputFile) {
        BufferedOutputStream stream = null;
        File file = null;
        try {
            file = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(file);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return file;
    }

    /**
     * 对指定路径的图片进行压缩处理,使之尽可能接近指定大小
     *
     * @param path
     * @param maxSize       期望的压缩后大小
     * @param targetFileDir
     * @param reqWidth      期望压缩后的宽度 0-表示分辨率不改变
     * @param reqHeight     期望压缩后的高度  0-表示分辨率不改变
     * @return
     */
    public static File compress(String path, int maxSize, String targetFileDir, int reqWidth, int reqHeight) {
        File targetFile = null;
        try {
            File imgFile = new File(path);
            int orn = readPictureDegree(path);
//            Log.i("ImageGridActivity","compress()--->path=" + path + ", orn=" + orn);
            Bitmap bm = decodeSampledBitmapFromSdCard(path, reqWidth, reqHeight);
//            Log.i("ImageGridActivity", "compress()--->width=" + bm.getWidth() + ", " +
//                    "height=" + bm.getHeight());
            if (orn != 0) {
                bm = rotaingImageView(orn, bm);
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);       //压缩率60%，表示压缩40%
            String filePath = targetFileDir + File.separator + imgFile
                    .getName();

            int lastPointIndex = filePath.lastIndexOf(".");
            filePath = filePath.substring(0, lastPointIndex) + ".jpg";
//            Log.i("ImageGridActivity","compress()--->filePath=" + filePath);
            filePath = TextUtils.isEmpty(filePath) ? "" : filePath.toLowerCase();
            targetFile = new File(filePath);
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
//            Log.i("ImageGridActivity","compress()--->targetFilePath=" + targetFile.getPath());
            FileOutputStream fos = new FileOutputStream(targetFile);
            int options = 100;
            // 如果大于maxSizekb则再次压缩,最多压缩三次
            while (baos.toByteArray().length / 1024 > maxSize && options != 10) {
                // 清空baos
                baos.reset();
                // 这里压缩options%，把压缩后的数据存放到baos中
                bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
                options -= 30;
            }
            fos.write(baos.toByteArray());
            fos.close();
            baos.close();
            return targetFile;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ImageGridActivity", "Exception=" + e.getMessage());
            if (targetFile != null) {
                targetFile.delete();
            }
            return null;
        }
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }
}
