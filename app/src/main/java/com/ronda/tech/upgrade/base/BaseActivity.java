package com.ronda.tech.upgrade.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ronda.tech.upgrade.R;
import com.ronda.tech.upgrade.utils.LogUtils;
import com.ronda.tech.upgrade.view.widget.MyProgress;


/**
 * @author snow.huang
 * created 2021/1/23 14:09
 *
 * Activity基类
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    private long exitTime = 0;
    private MyProgress mProfress;
    private Toast mToast;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindow();
        setContentView(getLayoutRes());
        init();
    }

    /**
     * 初始化方法
     */
    protected abstract void init();

    /**
     * 初始化layout
     * @return
     */
    protected abstract int getLayoutRes();

    /**
     * 设置状态栏透明
     */
    private void setWindow() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 触发loading
     * @param message
     */
    public void showLoading(String message, Context context) {
        if (context instanceof Activity && ((Activity) context).isFinishing()) {
            return ;
        }
        try {
            if (mProfress == null){
                mProfress = MyProgress.create(context,mOnKeyListener)
                        .setStyle(MyProgress.Style.SPIN_INDETERMINATE);
            }
            mProfress.show();
        }catch (Exception e){
            LogUtils.e(TAG,e.getMessage());
        }
    }


    /**
     * 取消loading
     */
    public void dismissLoading() {
        try {
            if (mProfress != null && mProfress.isShowing()) {
                LogUtils.e(TAG,"取消loading");
                mProfress.dismiss();
            }
        }catch (Exception e){
            LogUtils.e(TAG,"取消loading异常");
        }

    }

    /**
     * dialog监听
     */
    DialogInterface.OnKeyListener mOnKeyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                if ((System.currentTimeMillis() - exitTime) > 3000) {
                    showToast(getString(R.string.dismiss_loading));
                    exitTime = System.currentTimeMillis();
                } else {
                    dismissLoading();
                }
                return true;
            }
            return true;
        }
    };


    protected void showToast(String msg){
        if (null == mToast) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
