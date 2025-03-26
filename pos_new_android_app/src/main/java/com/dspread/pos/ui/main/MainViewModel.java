package com.dspread.pos.ui.main;

import android.app.Application;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dspread.pos.ui.base.TitleProvider;
import com.dspread.pos.ui.home.HomeFragment;
import com.dspread.pos.ui.setting.ViewPagerGroupFragment;
import com.dspread.pos.utils.TRACE;
import com.dspread.pos_new_android_app.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

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

    public MainViewModel(@NonNull Application application, MainActivity activity) {
        super(application);
        this.activityRef = new WeakReference<>(activity);
        initFragments();
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

    private void initFragments() {
        fragments = new ArrayList<>();
        // add fragment to list
        fragments.add(new HomeFragment());
        fragments.add(new ViewPagerGroupFragment());
//        fragments.add(new DeviceInfoFragment());
//        fragments.add(new DeviceUpdataFragment());
//        fragments.add(new AboutFragment());
//        fragments.add(new LogsFragment());
//        fragments.add(new PrinterHelperFragment());
//        fragments.add(new ScanFragment());
//        fragments.add(new MifareCardsFragment());
    }

    private void handleNavigationItemClick(int itemId) {
        MainActivity activity = activityRef.get();
        if (activity == null) return;
        int fragmentIndex = -1;
        switch (itemId) {
            case R.id.nav_home:
                fragmentIndex = 0;
                break;
            case R.id.nav_printer:
                fragmentIndex = 1;
                break;
            case R.id.nav_scan:
                fragmentIndex = 2;
                break;
            case R.id.nav_setting:
                fragmentIndex = 3;
                break;
//            case R.id.nav_deviceinfo:
//                fragmentIndex = 2;
//                break;
//            case R.id.nav_deviceupdate:
//                fragmentIndex = 3;
//                break;
//            case R.id.nav_about:
//                fragmentIndex = 4;
//                break;
//            case R.id.nav_log:
//                fragmentIndex = 5;
//                break;

//            case R.id.nav_mifareCards:
//                fragmentIndex = 8;
//                break;
//            case R.id.nav_exit:
//                fragmentIndex = 9;
//                break;
        }
        if (fragmentIndex != -1 && fragmentIndex <9) {
            activity.switchFragment(fragments,fragmentIndex);
            // 设置标题
            Fragment currentFragment = fragments.get(fragmentIndex);
            if (currentFragment instanceof TitleProvider) {
                activity.setTitle(((TitleProvider) currentFragment).getTitle());
            }
        }else if(fragmentIndex == 9){
            activity.exitApp();
        }
    }

}