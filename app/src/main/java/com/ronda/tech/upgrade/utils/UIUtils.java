package com.ronda.tech.upgrade.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ronda.tech.upgrade.base.MyApplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * @author snow.huang
 * created 2021/1/23 14:33
 *
 */
public class UIUtils {

    private static File mFile;
    public static double pi = 3.141592653589793 * 3000.0 / 180.0;

    /**
     * 得到上下文
     */
    public static Context getContext() {
        return MyApplication.getContext();
    }


    /**
     * 得到Resource对象
     */
    public static Resources getResources() {
        return getContext().getResources();
    }


    /**
     * 得到String.xml中的字符串
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }


    /**
     * 得到String.xml中字符串数组
     */
    public static String[] getStrings(int resId) {
        return getResources().getStringArray(resId);
    }


    /**
     * 得到Color.xml中的颜色信息
     */
    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }


    /**
     * 得到应用程序的包名
     *
     * @return
     */
    public static String getPackageName() {
        return getContext().getPackageName();
    }


    /**
     * dip-->px
     *
     * @param dip
     * @return
     */
    public static int dp2px(int dip) {
        //dip和px的转换关系
        //1. px/(ppi/160) = dp
        //2  px/dip = density
        float density = getResources().getDisplayMetrics().density;
        int px = (int) (density * dip + .5f);
        return px;
    }


    /**
     * 获取版本信息
     *
     * @return
     */
    public static String getVersion() {
        PackageManager packageManager = getContext().getPackageManager();
        String version = "";
        try {
            PackageInfo info = packageManager.getPackageInfo(getPackageName(), 0);
            String versionName = info.versionName;
            version = versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }


    /**
     * 获取版本号
     *
     * @return
     */
    public static int getVersionCode() {
        PackageManager packageManager = getContext().getPackageManager();
        int version = 1;
        try {
            PackageInfo info = packageManager.getPackageInfo(getPackageName(), 0);
            int versionCode = info.versionCode;
            version = versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * px-->dip
     *
     * @param px
     * @return
     */
    public static int px2Dip(int px) {
        //dip和px的转换关系
        //1. px/(ppi/160) = dp
        //2  px/dip = density
        float density = getResources().getDisplayMetrics().density;
        int dip = (int) (px / density + .5f);
        return dip;
    }


    /**
     * 是否存在sdcard
     *
     * @return
     */
    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 将bitmap保存file路径
     *
     * @param bitmap
     */
    public static void saveBitmapFile(Bitmap bitmap) {
        File filesDir = MyApplication.getContext().getFilesDir();
        Log.d("BabyDataActivity", "saveBitmapFile: " + filesDir);
        //将要保存图片的路径
        mFile = new File(String.valueOf(filesDir) + "/01.jpg");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(mFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {

        }
    }


    /**
     * 获取存储图片的路径
     *
     * @return
     */
    public static File getBitmapFile() {
        return mFile;
    }


    /**
     * 获取活动网路信息
     *
     * @param context 上下文
     * @return NetworkInfo
     */
    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }


    /**
     * 判断网络是否可用
     * <p>需添加权限 android.permission.ACCESS_NETWORK_STATE</p>
     */
    public static boolean isAvailable(Context context) {
        NetworkInfo info = getActiveNetworkInfo(context);
        return info != null && info.isAvailable();
    }


    /**
     * 根据图片的Uri获取图片的绝对路径(适配多种API)
     *
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion < 11) return getRealPathFromUri_BelowApi11(context, uri);
        if (sdkVersion < 19) return getRealPathFromUri_Api11To18(context, uri);
        else return getRealPathFromUri_AboveApi19(context, uri);
    }

    /**
     * 适配api19以上,根据uri获取图片的绝对路径
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        String filePath = null;
        String wholeID = DocumentsContract.getDocumentId(uri);

        // 使用':'分割
        String[] ids = wholeID.split(":");
        String id = null;
        if (ids == null) {
            return null;
        }
        if (ids.length > 1) {
            id = ids[1];
        } else {
            id = ids[0];
        }

        String[] projection = {MediaStore.Images.Media.DATA};
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = {id};

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,//
                projection, selection, selectionArgs, null);
        int columnIndex = cursor.getColumnIndex(projection[0]);
        if (cursor.moveToFirst()) filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    /**
     * 适配api11-api18,根据uri获取图片的绝对路径
     */
    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

    /**
     * 适配api11以下(不包括api11),根据uri获取图片的绝对路径
     */
    private static String getRealPathFromUri_BelowApi11(Context context, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }


    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 将 GCJ-02 坐标转换成 BD-09 坐标
     *
     * @param gg_lat
     * @param gg_lon
     * @return
     */
//    public static LatLng gcj02_To_Bd09(double gg_lon, double gg_lat) {
//        double x = gg_lon, y = gg_lat;
//        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * pi);
//        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * pi);
//        double bd_lon = z * Math.cos(theta) + 0.0065;
//        double bd_lat = z * Math.sin(theta) + 0.006;
//        return new LatLng(bd_lon, bd_lat);
//    }


    /**
     * 已知系统类型时，设置状态栏黑色字体图标。
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     * @param activity
     * @param type 1:MIUUI 2:Flyme 3:android6.0
     */
    public static void StatusBarLightMode(Activity activity, int type){
        if(type==1){
            boolean statusBarLightMode = MIUISetStatusBarLightMode(activity.getWindow(), true);
            LogUtils.e("BaseActivity","statusBarLightMode = " +statusBarLightMode);
        }else if(type==2){
            boolean barLightMode = FlymeSetStatusBarLightMode(activity.getWindow(), true);
            LogUtils.e("BaseActivity","barLightMode = " +barLightMode);
        }else if(type==3){
            activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    /**
     *设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     * @param activity
     * @return 1:MIUUI 2:Flyme 3:android6.0
     */
    public static int StatusBarLightMode(Activity activity){
        int result=0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(MIUISetStatusBarLightMode(activity.getWindow(), true)){
                result=1;
            }else if(FlymeSetStatusBarLightMode(activity.getWindow(), true)){
                result=2;
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                result=3;
            }
        }
        return result;
    }



    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     *
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }


    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     *
     */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if(dark){
                    extraFlagField.invoke(window,darkModeFlag,darkModeFlag);//状态栏透明且黑色字体
                }else{
                    extraFlagField.invoke(window, 0, darkModeFlag)

                    ;//清除黑色字体
                }
                result=true;



            }catch (Exception e){



            }



        }
        return result;
    }
    /**

     * Flag只有在使用了FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS

     * 并且没有使用 FLAG_TRANSLUCENT_STATUS的时候才有效，也就是只有在状态栏全透明的时候才有效。

     * @param activity

     * @param bDark

     */

    public static void setStatusBarMode(Activity activity, boolean bDark) {

//6.0以上

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            View decorView = activity.getWindow().getDecorView();

            if (decorView != null) {

                int vis = decorView.getSystemUiVisibility();

                if (bDark) {

                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

                } else {

                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

                }
                decorView.setSystemUiVisibility(vis);

            }

        }
    }
}
