package com.dspread.pos;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;


import com.dspread.pos.base.MyQposClass;
import com.dspread.pos.ui.main.MainActivity;
import com.dspread.pos.utils.TRACE;
import com.dspread.pos_new_android_app.R;
import com.dspread.xpos.QPOSService;

import java.util.Stack;
import java.util.concurrent.TimeUnit;

import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.crash.CaocConfig;
import okhttp3.OkHttpClient;


/**
 * @author user
 */
public class MyBaseApplication extends BaseApplication {
    public static Context getApplicationInstance;
    private MyBaseApplication baseApplication;
    private QPOSService pos;
    public static Handler handler;

    public QPOSService getQposService(){
        if(pos != null){
            return pos;
        }
        return null;
    }

    public void setQposService(QPOSService pos){
      this.pos = pos;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getApplicationInstance = this;
        initCrash();
    }
    public void open(QPOSService.CommunicationMode mode,Context context) {
        TRACE.d("open");
       MyQposClass listener = new MyQposClass();
        pos = QPOSService.getInstance(context, mode);
        if (pos == null) {
            return;
        }
        if (mode == QPOSService.CommunicationMode.USB_OTG_CDC_ACM) {
            pos.setUsbSerialDriver(QPOSService.UsbOTGDriver.CDCACM);
        }
        pos.setD20Trade(true);

        pos.setContext(this);

        handler = new Handler(Looper.myLooper());
//        pos.initListener(handler, listener);
        pos.initListener(listener);

    }

    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
                .errorDrawable(R.mipmap.ic_launcher) //错误图标
                .restartActivity(MainActivity.class) //重新启动后的activity
//                .errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
//                .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
    }
}
