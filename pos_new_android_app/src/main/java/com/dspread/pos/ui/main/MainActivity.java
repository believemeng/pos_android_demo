package com.dspread.pos.ui.main;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;


import com.dspread.pos.interfaces.MyCustomQPOSCallback;
import com.dspread.pos.manager.QPOSCallbackManager;
import com.dspread.pos.ui.base.TitleProvider;
import com.dspread.pos.utils.DevUtils;
import com.dspread.pos.utils.TRACE;
import com.dspread.pos_new_android_app.BR;
import com.dspread.pos_new_android_app.R;
import com.dspread.pos_new_android_app.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

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
        if (navController != null) {
            // 设置预加载数量
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                // 预加载下一个 Fragment
                if (destination.getId() == R.id.nav_home) {
                    navController.navigate(R.id.nav_setting, null, null, null);
                    navController.popBackStack();
                }
            });
        }
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
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QPOSCallbackManager.getInstance().unregisterCallback(MyCustomQPOSCallback.class);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            toolbar.setTitle(getString(R.string.menu_payment));
//            switchFragment(0);
            drawerLayout.close();
            exit();
            return true;
        }
        else if (viewModel.fragments.get(0)!= null) {
//            HomeFragment homeFragment = (HomeFragment) viewModel.fragments.get(0);
//            return homeFragment.onKeyDown(keyCode, event);  // 让 Fragment 处理按键事件
        }
        TRACE.i("main keyode = " + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    public void switchFragment(List<Fragment> mFragments,int index) {
        if (currentFragmentIndex == index) {
            return;
        }
        transaction = getSupportFragmentManager().beginTransaction();

        if(currentFragmentIndex != -1) {
            transaction.hide(mFragments.get(currentFragmentIndex));
        }
        if (!mFragments.get(index).isAdded()) {
            transaction.add(R.id.nav_host_fragment, mFragments.get(index), String.valueOf(index));
        }
        transaction.show(mFragments.get(index));
        transaction.commitAllowingStateLoss();
        currentFragmentIndex = index;
         if (mFragments.get(currentFragmentIndex) instanceof TitleProvider) {
            setToolbarTitle(((TitleProvider) mFragments.get(currentFragmentIndex)).getTitle());
        }
    }

    public void exitApp(){
//        Mydialog.manualExitDialog(MainActivity.this, getString(R.string.msg_exit), new Mydialog.OnMyClickListener() {
//            @Override
//            public void onCancel() {
//                Mydialog.manualExitDialog.dismiss();
//            }
//
//            @Override
//            public void onConfirm() {
////                        finish();
//                finishAllActivities();
//                Mydialog.manualExitDialog.dismiss();
//            }
//        });
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
//            Mydialog.manualExitDialog(MainActivity.this, getString(R.string.msg_exit), new Mydialog.OnMyClickListener() {
//                @Override
//                public void onCancel() {
//                    Mydialog.manualExitDialog.dismiss();
//                }
//
//                @Override
//                public void onConfirm() {
////                    finish();
//                    finishAllActivities();
//                    Mydialog.manualExitDialog.dismiss();
//                }
//            });
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
    }
}



