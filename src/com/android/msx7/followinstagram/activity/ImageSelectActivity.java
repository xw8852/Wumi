package com.android.msx7.followinstagram.activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.android.msx7.followinstagram.R;
import com.android.msx7.followinstagram.common.BaseActivity;
import com.android.msx7.followinstagram.common.YohoField;
import com.android.msx7.followinstagram.ui.pic.SelectPicPopupWindow;
import com.android.msx7.followinstagram.util.L;
import com.android.msx7.followinstagram.util.ToastUtil;

import java.io.File;

/**
 * Created by xiaowei on 2015/9/8.
 */
public abstract class ImageSelectActivity extends BaseActivity {
    protected File mTempFile;
    protected Uri mFileUri;
    protected Uri mTargetFileUri;
    private static final int CROP_WIDTH_DEF = 300;       //裁剪后的图片宽度
    private static final int CROP_HEIGHT_DEF = 300;      //裁剪后的图片高度
    protected static final int OPEN_CAMERA_CODE = 10;// 打开相机
    protected static final int OPEN_GALLERY_CODE = 11;// 打开相册
    protected static final int OPEN_PIC_KITKAT = 12;// 打开相册
    protected static final int OPEN_PIC = 13;// 打开相册
    protected static final int CROP_PHOTO_CODE = 14;// 裁剪图片


    private void initFile() {
        String path = Environment.getExternalStorageDirectory().getPath();
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && !TextUtils.isEmpty(path)) {
            ToastUtil.show(R.string.toast_sd_error);
            return;
        }
        String timeStamp = String.valueOf(System.currentTimeMillis());
        File file = new File(getImageDir() + File.separator +
                getImagePrefix() + timeStamp + ".jpg");
        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }
        mFileUri = Uri.fromFile(new File(getImageDir() + File.separator +
                getImagePrefix() + timeStamp + ".jpg"));
    }

    /**
     * 调用相机
     */
    protected void openCamera() {
        initFile();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 打开相机
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
        startActivityForResult(intent, OPEN_CAMERA_CODE);
    }

    /**
     * 打开相册
     */
    protected void openGallery() {
        initFile();
//        Intent intent = new Intent(Intent.ACTION_PICK);// 打开相册
//        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
//        startActivityForResult(intent, OPEN_GALLERY_CODE);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            startActivityForResult(intent, OPEN_PIC_KITKAT);
        } else {
            startActivityForResult(intent, OPEN_PIC);
        }
    }

    public static String getImageDir() {
        String path = Environment.getExternalStorageDirectory().getPath();
        return path + File.separator + YohoField.BASE_DIR + File.separator + "WUMI_IMG" + File.separator;
    }


    /**
     * 裁剪图片
     *
     * @param uri
     * @param uri
     */
    protected void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");//裁剪前的文件
        mTempFile = new File(getImageDir() + File.separator + "tmp" + System.currentTimeMillis() + ".jpg");
        if (!mTempFile.getParentFile().exists())
            mTempFile.getParentFile().mkdirs();
        mTargetFileUri = Uri.fromFile(mTempFile);
        L.d("Uri---" + mTargetFileUri.toString());
        L.d("Uri---" + mTempFile.getParentFile().exists());
        intent.putExtra("output", mTargetFileUri);//裁剪后的文件
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        Crop crop = getCrop();
        if (crop != null) {
            intent.putExtra("outputX", crop.getWidth());
            intent.putExtra("outputY", crop.getHeight());
        }
        startActivityForResult(intent, CROP_PHOTO_CODE);
    }

    protected File getImageFile() {
        if (mTempFile == null) {
            return null;
        }
        if (mTempFile.length() > 0) {
            return mTempFile;
        }
        return null;
    }

    SelectPicPopupWindow mMenu;
    View.OnClickListener mMenuOnClickLisenter = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_take_photo) {
                openCamera();
            } else {
                openGallery();
            }
        }
    };

    /**
     * @param
     * @return void
     * @throws
     * @Title: dismissMenu
     * @Description: 关闭头像选择菜单
     */
    protected void dismissMenu() {
        if (mMenu != null) {
            mMenu.dismiss();
        }
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: showMenu
     * @Description: 弹出头像选择菜单
     */
    protected void showMenu() {
        if (mMenu == null) {
            mMenu = new SelectPicPopupWindow(this, mMenuOnClickLisenter);
        }
        // 显示窗口
        mMenu.showAtLocation(getImageView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
    }

    public abstract View getImageView();

    protected Crop getCrop() {
        return new Crop(CROP_WIDTH_DEF, CROP_HEIGHT_DEF);
    }

    protected abstract String getImagePrefix();

    public class Crop {
        private int width;
        private int height;

        public Crop(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        float _radius = Math.min(sbmp.getWidth(), sbmp.getHeight());
        final Rect rect = new Rect(0, 0, (int) _radius, (int) _radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));

        canvas.drawCircle(_radius / 2 + 0.7f,
                _radius / 2 + 0.7f, _radius / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

    /**
     * 兼容4.4以上的图片选择，通过uri获取实际的路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}

