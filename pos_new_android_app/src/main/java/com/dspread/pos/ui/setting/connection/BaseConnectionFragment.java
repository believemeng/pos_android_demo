package com.dspread.pos.ui.setting.connection;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import com.dspread.pos.common.enums.POS_TYPE;
import com.dspread.pos.posAPI.MyCustomQPOSCallback;
import com.dspread.pos.common.manager.QPOSCallbackManager;
import com.dspread.pos.ui.base.BaseConnectionViewModel;
import com.dspread.pos.ui.setting.connection.bluetooth.BluetoothFragment;
import com.dspread.pos.ui.setting.connection.uart.UartFragment;
import com.dspread.pos.ui.setting.connection.usb.USBFragment;
import com.dspread.pos_new_android_app.R;

import me.goldze.mvvmhabit.base.BaseFragment;

public abstract class BaseConnectionFragment<V extends ViewDataBinding,VM extends BaseConnectionViewModel> extends BaseFragment<V, VM> implements MyCustomQPOSCallback {
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
    public void initViewObservable() {
        // 观察连接状态变化
        viewModel.getConnectionStatus().observe(getViewLifecycleOwner(), isConnected -> {
            // 这里的代码会在主线程执行
            if (isConnected) {
                // 处理连接成功的UI逻辑
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
            }else {
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
        });
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
        viewModel.setConnected(true);
    }

    @Override
    public void onRequestQposDisconnected() {
        viewModel.setConnected(false);
    }

    @Override
    public void onRequestNoQposDetected() {
        viewModel.setConnected(false);
    }

}
