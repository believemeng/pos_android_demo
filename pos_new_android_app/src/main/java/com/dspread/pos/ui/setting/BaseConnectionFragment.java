package com.dspread.pos.ui.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.dspread.demoui.enums.POS_TYPE;
import com.dspread.demoui.interfaces.MyCustomQPOSCallback;
import com.dspread.demoui.manager.QPOSCallbackManager;
import com.dspread.demoui.ui.setting.connection.USBFragment;
import com.dspread.demoui.ui.setting.connection.uart.UartFragment;
import com.dspread.pos.ui.setting.bluetooth.BluetoothFragment;

import me.goldze.mvvmhabit.base.BaseFragment;

public abstract class BaseConnectionFragment<T,VM extends ViewModel> extends BaseFragment implements MyCustomQPOSCallback {
    protected VM viewModel;
    private boolean isInitialized = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(getViewModelClass());
    }

    @Override
    public void initData() {
        super.initData();
        isInitialized = true;
        // 只在当前显示的 Fragment 中注册回调
        if (getUserVisibleHint()) {
            QPOSCallbackManager.getInstance().registerCallback(MyCustomQPOSCallback.class, this);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isInitialized) {
            QPOSCallbackManager.getInstance().registerCallback(MyCustomQPOSCallback.class, this);
        }
    }

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getLayoutResId();
    }

    @Override
    public int initVariableId() {
        return getVariableId();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        QPOSCallbackManager.getInstance().unregisterCallback(MyCustomQPOSCallback.class);
    }


    // 抽象方法，由子类实现具体布局资源 ID
    protected abstract int getLayoutResId();

    // 抽象方法，由子类实现具体变量 ID
    protected abstract int getVariableId();
    // 抽象方法，由子类实现具体 ViewModel 类型
    protected abstract Class<VM> getViewModelClass();

    @Override
    public void onRequestQposConnected() {
        if (viewModel instanceof ConnectionViewModel) {
            ConnectionViewModel connectionViewModel = (ConnectionViewModel) viewModel;
            POS_TYPE currentType = connectionViewModel.getCurrentPosType();
            if (this instanceof BluetoothFragment && (currentType == POS_TYPE.BLUETOOTH || currentType == POS_TYPE.BLUETOOTH_BLE)) {
                // 蓝牙连接成功处理
                connectionViewModel.onBluetoothConnected(true, connectionViewModel.getBlueTootchName());
            } else if (this instanceof UartFragment && currentType == POS_TYPE.UART) {
                // UART连接成功处理
                connectionViewModel.onUartConnected();
            } else if (this instanceof USBFragment && currentType == POS_TYPE.USB) {
                // USB连接成功处理
                connectionViewModel.onUsbConnected();
            }
        }
    }

    @Override
    public void onRequestQposDisconnected() {
        if (viewModel instanceof ConnectionViewModel) {
            ConnectionViewModel connectionViewModel = (ConnectionViewModel) viewModel;

            POS_TYPE currentType = connectionViewModel.getCurrentPosType();

            if (this instanceof BluetoothFragment && (currentType == POS_TYPE.BLUETOOTH || currentType == POS_TYPE.BLUETOOTH_BLE)) {
                // 蓝牙连接成功处理
                connectionViewModel.onBluetoothConnected(false, null);
            } else if (this instanceof UartFragment && currentType == POS_TYPE.UART) {
                // UART连接成功处理
                connectionViewModel.onDeviceDisconnected(POS_TYPE.UART);
            } else if (this instanceof USBFragment && currentType == POS_TYPE.USB) {
                // USB连接成功处理
                connectionViewModel.onDeviceDisconnected(POS_TYPE.USB);
            }
        }
    }

    @Override
    public void onRequestNoQposDetected() {
        if (viewModel instanceof ConnectionViewModel) {
            ConnectionViewModel connectionViewModel = (ConnectionViewModel) viewModel;
            POS_TYPE currentType = connectionViewModel.getCurrentPosType();

            if (this instanceof BluetoothFragment && (currentType == POS_TYPE.BLUETOOTH || currentType == POS_TYPE.BLUETOOTH_BLE)) {
                // 蓝牙连接成功处理
                connectionViewModel.onBluetoothConnected(false, null);
            } else if (this instanceof UartFragment && currentType == POS_TYPE.UART) {
                // UART连接成功处理
                connectionViewModel.onNoDeviceDetected(POS_TYPE.UART);
            } else if (this instanceof USBFragment && currentType == POS_TYPE.USB) {
                // USB连接成功处理
                connectionViewModel.onNoDeviceDetected(POS_TYPE.USB);
            }
        }
    }

}
