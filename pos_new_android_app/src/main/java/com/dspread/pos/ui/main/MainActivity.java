package com.dspread.pos.ui.main;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;


import com.dspread.pos.common.enums.POS_TYPE;
import com.dspread.pos.posAPI.MyCustomQPOSCallback;
import com.dspread.pos.common.manager.QPOSCallbackManager;
import com.dspread.pos.utils.DevUtils;
import com.dspread.pos.utils.Mydialog;
import com.dspread.pos.utils.TRACE;
import com.dspread.pos_new_android_app.BR;
import com.dspread.pos_new_android_app.R;
import com.dspread.pos_new_android_app.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.tencent.upgrade.core.DefaultUpgradeStrategyRequestCallback;
import com.tencent.upgrade.core.UpgradeManager;

import java.util.Hashtable;

import me.goldze.mvvmhabit.base.BaseActivity;
import me.goldze.mvvmhabit.utils.SPUtils;

public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> implements MyCustomQPOSCallback{

    public void setToolbarTitle(String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private FragmentTransaction transaction;
    private TextView deviceConnectType;
    private TextView tvAppVersion;
    private MenuItem menuItem;

    String deviceModel = Build.MODEL;
    String deviceManufacturer = Build.MANUFACTURER;

    ActionBarDrawerToggle toggle;
    private int currentFragmentIndex = -1;
    private NavController navController;

    @Override
    public void initParam() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public int initContentView(Bundle bundle) {
        return R.layout.activity_main;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
//        // 获取 NavController
//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.nav_host_fragment);
        QPOSCallbackManager.getInstance().registerCallback(MyCustomQPOSCallback.class, this);
        viewModel = new MainViewModel(getApplication(), this);
        binding.setVariable(BR.viewModel, viewModel);
        drawerLayout = binding.drawerLayout;
        navigationView = binding.navView;
        toolbar = binding.appBarMain.toolbar;
        View headerView = navigationView.getHeaderView(0);
        deviceConnectType = headerView.findViewById(R.id.device_connect_type);
        tvAppVersion = headerView.findViewById(R.id.tv_appversion);
        menuItem = navigationView.getMenu().findItem(R.id.nav_printer);
        setSupportActionBar(toolbar);
        navigationView.bringToFront();
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 初始化时隐藏Setting的子菜单
//        hideSettingSubmenu();

        viewModel.openDevice();
    }

    @Override
    public MainViewModel initViewModel() {
        MainViewModelFactory factory = new MainViewModelFactory(getApplication(), this);
        return new ViewModelProvider(this, factory).get(MainViewModel.class);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        // observe Fragment have been changed
        viewModel.fragmentChangeEvent.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer fragmentIndex) {
                drawerLayout.close();
            }
        });
        viewModel.changeDrawerLayout.observe(this, new Observer<View>() {
            @Override
            public void onChanged(View drawerView) {
                String packageVersionName = DevUtils.getPackageVersionName(MainActivity.this, "com.dspread.pos_new_android_app");
                tvAppVersion.setText(getString(R.string.app_version) + packageVersionName);
                hideKeyboard(drawerView);
                checkUpdate();
            }
        });
    }

    private void checkUpdate(){
        UpgradeManager.getInstance().checkUpgrade(true, null, new DefaultUpgradeStrategyRequestCallback());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QPOSCallbackManager.getInstance().unregisterCallback(MyCustomQPOSCallback.class);
        TRACE.i("main is onDestroy");
        SPUtils.getInstance().put("isConnected",false);
        SPUtils.getInstance().put("device_type", "");
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (action == KeyEvent.ACTION_UP) {
                toolbar.setTitle(getString(R.string.menu_payment));
                drawerLayout.close();
                viewModel.handleNavigationItemClick(R.id.nav_home);
                exit();
            }
            return true;
        }
        return super.dispatchKeyEvent(event); // 调用父类的dispatchKeyEvent方法，将事件传递给其他组件
    }

    private static boolean isExit = false;
    Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    private void exit() {
        if (!isExit) {
            isExit = true;
            mHandler.sendEmptyMessageDelayed(0, 1500);
        } else {
            isExit = false;
            Mydialog.manualExitDialog(MainActivity.this, getString(R.string.msg_exit), new Mydialog.OnMyClickListener() {
                @Override
                public void onCancel() {
                    Mydialog.manualExitDialog.dismiss();
                }

                @Override
                public void onConfirm() {
                    finish();
                    Mydialog.manualExitDialog.dismiss();
                }
            });
        }
    }

    public static void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    @Override
    public void onRequestQposConnected() {
        MyCustomQPOSCallback.super.onRequestQposConnected();
        SPUtils.getInstance().put("isConnected",true);
        SPUtils.getInstance().put("device_type", POS_TYPE.UART.name());
        if(viewModel.pos != null){
           Hashtable<String, Object> posIdTable = viewModel.pos.syncGetQposId(5);
            String posId = posIdTable.get("posId") == null ? "" : (String) posIdTable.get("posId");
            SPUtils.getInstance().put("posID",posId);
        }
    }
}



