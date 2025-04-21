package com.dspread.pos.ui.setting.connection;

import androidx.fragment.app.Fragment;

import com.dspread.pos.ui.base.TitleProvider;
import com.dspread.pos.ui.base.fragment.BasePagerFragment;
import com.dspread.pos.ui.setting.connection.bluetooth.BluetoothFragment;
import com.dspread.pos.ui.setting.connection.uart.UartFragment;
import com.dspread.pos.ui.setting.connection.usb.USBFragment;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerGroupFragment extends BasePagerFragment implements TitleProvider {
    @Override
    public String getTitle() {
        return "Connection";
    }

    @Override
    public void initData() {
        super.initData();
        // 设置 ViewPager 不预加载
        binding.viewPager.setOffscreenPageLimit(1);

    }

    @Override
    protected List<Fragment> pagerFragment() {
        List<Fragment> list = new ArrayList<>();
        list.add(new BluetoothFragment());
        list.add(new UartFragment());
        list.add(new USBFragment());
        return list;
    }

    @Override
    protected List<String> pagerTitleString() {
        List<String> list = new ArrayList<>();
        list.add("Bluetooth");
        list.add("UART");
        list.add("USB");
        return list;
    }
}
