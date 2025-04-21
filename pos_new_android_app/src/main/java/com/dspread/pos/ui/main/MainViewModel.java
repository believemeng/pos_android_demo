package com.dspread.pos.ui.main;

import android.app.Application;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dspread.pos.MyBaseApplication;
import com.dspread.pos.common.manager.FragmentCacheManager;
import com.dspread.pos.ui.base.TitleProvider;
import com.dspread.pos.ui.home.HomeFragment;
import com.dspread.pos.ui.printer.PrinterHelperFragment;
import com.dspread.pos.ui.scan.ScanFragment;
import com.dspread.pos.ui.setting.configuration.ConfigurationFragment;
import com.dspread.pos.ui.setting.connection.ViewPagerGroupFragment;
import com.dspread.pos.utils.DeviceUtils;
import com.dspread.pos.utils.TRACE;
import com.dspread.pos_new_android_app.R;
import com.dspread.xpos.QPOSService;
import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;
import java.util.List;

import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;


public class MainViewModel extends BaseViewModel {
    // SingleLiveEvent
    public SingleLiveEvent<Integer> fragmentChangeEvent = new SingleLiveEvent<>();
    public SingleLiveEvent<View> changeDrawerLayout = new SingleLiveEvent<>();
    private MainActivity activity;
    public List<Fragment> fragments;

    private WeakReference<MainActivity> activityRef;
    private Fragment currentFragment;
    private MyBaseApplication myBaseApplication;
    public QPOSService pos;

    public MainViewModel(@NonNull Application application, MainActivity activity) {
        super(application);
        TRACE.i("main activity init");
        this.activityRef = new WeakReference<>(activity);
        this.activity = activity;
//        initFragments();
        if(myBaseApplication == null){
            myBaseApplication = (MyBaseApplication) BaseApplication.getInstance();
        }
    }

    public void openDevice(){
        if(DeviceUtils.isSmartDevices()){
            myBaseApplication.open(QPOSService.CommunicationMode.UART, getApplication());
            pos = myBaseApplication.getQposService();
            pos.setDeviceAddress("/dev/ttyS1");
            pos.openUart();
        }
    }

    public BindingCommand<View> onDrawerOpenedCommand = new BindingCommand<>(new BindingConsumer<View>() {

        @Override
        public void call(View drawerLayout) {
            changeDrawerLayout.setValue(drawerLayout);
        }
    });

    public BindingCommand<View> onDrawerClosedCommand  = new BindingCommand<>(new BindingConsumer<View>() {

        @Override
        public void call(View drawerLayout) {
            changeDrawerLayout.setValue(drawerLayout);
        }
    });

    // command for switch Fragment
    public BindingCommand<Integer> switchFragmentCommand = new BindingCommand<>(integer -> {
        // switch Fragment
        TRACE.i("this is switch");
        fragmentChangeEvent.setValue(integer); // 这里可以根据逻辑设置不同的Fragment
        handleNavigationItemClick(integer);
    });

    public void handleNavigationItemClick(int itemId) {
        MainActivity activity = activityRef.get();
        if (activity == null) return;

        Fragment targetFragment;
        // 从缓存中获取Fragment
        if (FragmentCacheManager.getInstance().hasFragment(itemId)) {
            targetFragment = FragmentCacheManager.getInstance().getFragment(itemId);
        } else {
            // 创建新的Fragment并缓存
            targetFragment = createFragment(itemId);
            if (targetFragment != null) {
                FragmentCacheManager.getInstance().putFragment(itemId, targetFragment);
            }
        }

        if (targetFragment != null) {
            switchFragment(targetFragment);
            // 设置标题
            if (targetFragment instanceof TitleProvider) {
                activity.setTitle(((TitleProvider) targetFragment).getTitle());
            }
        }
    }

    private Fragment createFragment(int itemId) {
        switch (itemId) {
            case R.id.nav_home:
                return new HomeFragment();
            case R.id.nav_connection:
                return new ViewPagerGroupFragment();
            case R.id.nav_printer:
                return new PrinterHelperFragment();
            case R.id.nav_scan:
                return new ScanFragment();
            case R.id.nav_configuration:
                return new ConfigurationFragment();
        }

        return null;
    }


    private void switchFragment(Fragment targetFragment) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // 隐藏当前Fragment，显示目标Fragment
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }

        if (!targetFragment.isAdded()) {
            transaction.add(R.id.nav_host_fragment, targetFragment);
        } else {
            transaction.show(targetFragment);
        }

        transaction.commitAllowingStateLoss();
        currentFragment = targetFragment;
    }
}