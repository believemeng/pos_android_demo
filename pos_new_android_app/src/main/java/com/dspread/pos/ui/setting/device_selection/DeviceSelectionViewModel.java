package com.dspread.pos.ui.setting.device_selection;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;

import com.dspread.pos.MyBaseApplication;
import com.dspread.pos.common.enums.POS_TYPE;
import com.dspread.pos.utils.TRACE;
import com.dspread.pos_new_android_app.R;
import com.dspread.xpos.QPOSService;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class DeviceSelectionViewModel extends BaseViewModel {
    // 当前选中的连接方式
    public final ObservableField<String> selectedConnectionMethod = new ObservableField<>();
    public final ObservableField<String> connectBtnTitle = new ObservableField<>("Connect");
    public final ObservableField<String> bluetoothAddress = new ObservableField<>();
    public final ObservableField<String> bluetoothName = new ObservableField<>();

    // 连接方式选项
    public final String[] connectionMethods = {"BLUETOOTH", "UART", "USB"};

    // 连接方式对应的POS_TYPE
    public final POS_TYPE[] posTypes = {POS_TYPE.BLUETOOTH, POS_TYPE.UART, POS_TYPE.USB};

    // 事件：连接方式选择完成
    public final SingleLiveEvent<POS_TYPE> connectionMethodSelectedEvent = new SingleLiveEvent<>();

    public final SingleLiveEvent<POS_TYPE> startScanBluetoothEvent = new SingleLiveEvent<>();

    // 当前选中的连接方式索引
    public final MutableLiveData<Integer> selectedIndex = new MutableLiveData<>(-1);
    private String connectedDeviceName;
    private MyBaseApplication myBaseApplication;
    private QPOSService pos;
    public POS_TYPE currentPOSType;

    public DeviceSelectionViewModel(@NonNull Application application) {
        super(application);
        if(myBaseApplication == null){
            myBaseApplication = (MyBaseApplication) BaseApplication.getInstance();
        }
        if(!"".equals(SPUtils.getInstance().getString("device_type"))){
            pos = MyBaseApplication.getQposService();
            connectedDeviceName = SPUtils.getInstance().getString("device_type");
            if(connectedDeviceName.equals(POS_TYPE.UART.name())){
                currentPOSType = POS_TYPE.UART;
            }else if(connectedDeviceName.equals(POS_TYPE.USB.name())){
                currentPOSType = POS_TYPE.USB;
            }else {
                currentPOSType = POS_TYPE.BLUETOOTH;
            }
            loadSelectedConnectionMethod(connectedDeviceName);
        }

    }



    /**
     * 加载已选择的连接方式
     */
    private void loadSelectedConnectionMethod(String savedConnectionType) {
        // 根据保存的连接类型设置选中项
        for (int i = 0; i < posTypes.length; i++) {
            if (posTypes[i].name().equals(savedConnectionType)) {
                selectedIndex.setValue(i);
                selectedConnectionMethod.set(connectionMethods[i]);
                break;
            }
        }
    }

    /**
     * 连接方式选择命令
     */
    public BindingCommand<String> connectionMethodRadioSelectedCommand = new BindingCommand<>(radioText -> {
        TRACE.i("radio btn selected ="+radioText);

        if(connectedDeviceName != null && connectedDeviceName.equals(radioText)){
            connectBtnTitle.set(getApplication().getString(R.string.disconnect));
        } else{
            connectBtnTitle.set(getApplication().getString(R.string.str_connect));
            if(connectionMethods[0].equals(radioText)){
                selectedIndex.setValue(0);
                myBaseApplication.open(QPOSService.CommunicationMode.BLUETOOTH, getApplication());
                pos = myBaseApplication.getQposService();
                startScanBluetoothEvent.setValue(POS_TYPE.BLUETOOTH);
            }else if(connectionMethods[1].equals(radioText)){
                selectedIndex.setValue(1);
            }else {
                selectedIndex.setValue(2);
            }
        }


    });

    public void startScanBluetooth(){
        pos.scanQPos2Mode(getApplication(),20);
    }

    /**
     * 确认选择命令
     */
    public BindingCommand confirmSelectionCommand = new BindingCommand(() -> {
        Integer index = selectedIndex.getValue();
        if (index != null && index >= 0 && index < connectionMethods.length && !getApplication().getString(R.string.disconnect).equals(connectBtnTitle.get())) {
            // 触发选择完成事件
            close(currentPOSType);
            openDevice(posTypes[index]);
        } else if(getApplication().getString(R.string.disconnect).equals(connectBtnTitle.get())){
            close(currentPOSType);
        }else {
            ToastUtils.showShort("Pls choose one connection method!");
        }
    });

    public void openDevice(POS_TYPE posType){
        if(posType == POS_TYPE.USB){
            myBaseApplication.open(QPOSService.CommunicationMode.USB, getApplication());
        }else if(posType == POS_TYPE.UART){
            myBaseApplication.open(QPOSService.CommunicationMode.UART, getApplication());
            pos = myBaseApplication.getQposService();
            pos.setDeviceAddress("/dev/ttyS1");
            pos.openUart();
        }else {
            connectBluetooth(posType, bluetoothAddress.get());
        }
    }

    /**
     * 连接蓝牙设备
     */
    public void connectBluetooth(POS_TYPE posType, String blueTootchAddress){

        if (posType == null || blueTootchAddress == null) {
            TRACE.d("return close");
        } else if (posType == POS_TYPE.BLUETOOTH) {
            pos.stopScanQPos2Mode();
            pos.connectBluetoothDevice(true, 25, blueTootchAddress);
        } else if (posType == POS_TYPE.BLUETOOTH_BLE) {
            pos.stopScanQposBLE();
            pos.connectBLE(blueTootchAddress);
        }
    }

    public void close(POS_TYPE posType) {
        TRACE.d("start close");
        if (pos == null || posType == null) {
            TRACE.d("return close");
        } else if (posType == POS_TYPE.BLUETOOTH) {
            pos.disconnectBT();
        } else if (posType == POS_TYPE.BLUETOOTH_BLE) {
            pos.disconnectBLE();
        } else if (posType == POS_TYPE.UART) {
            pos.closeUart();
        } else if (posType == POS_TYPE.USB) {
            pos.closeUsb();
        }
    }
}
