package com.dspread.pos.ui.setting;

import android.app.AlertDialog;
import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.hardware.usb.UsbDevice;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;

import com.dspread.pos.MyBaseApplication;
import com.dspread.pos.enums.POS_TYPE;
import com.dspread.pos.ui.setting.bluetooth.MultiRecycleHeadViewModel;
import com.dspread.pos.ui.setting.bluetooth.MultiRecycleLeftItemViewModel;
import com.dspread.pos.ui.setting.usb.USBClass;
import com.dspread.pos.utils.TRACE;
import com.dspread.pos_new_android_app.BR;
import com.dspread.pos_new_android_app.R;
import com.dspread.xpos.QPOSService;

import java.util.ArrayList;

import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.binding.command.BindingConsumer;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.SPUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;
import me.tatarka.bindingcollectionadapter2.ItemBinding;
import me.tatarka.bindingcollectionadapter2.OnItemBind;

public class ConnectionViewModel extends BaseViewModel {
    public SingleLiveEvent<RadioButton> connectionChangeEvent = new SingleLiveEvent<>();
    public MutableLiveData<Boolean> uartSwitchState = new MutableLiveData<>();
    public ObservableField<String> connectionStatus = new ObservableField<>("Connection Method");
    public ObservableField<String> uartConnectionStatus = new ObservableField<>("Connection Method");
    public ObservableField<String> usbConnectionStatus = new ObservableField<>("Connection Disconnected");
    public ObservableField<Boolean> isConnecting = new ObservableField<>(false);
    public ObservableField<Boolean> isConnected = new ObservableField<>(false);
    public ObservableField<Boolean> isUSBConnected = new ObservableField<>(false);
    private static final String MultiRecycleType_Head = "head";
    private static final String MultiRecycleType_Left = "left";

    private QPOSService pos;
    private UsbDevice usbDevice;
    public AlertDialog alertDialog;
    private MyBaseApplication myBaseApplication;
    private SPUtils spUtils;
    private POS_TYPE currentPosType;
    private String blueTootchName;
    public SingleLiveEvent<ArrayList<String>> showUsbDeviceDialogEvent = new SingleLiveEvent<>();

    private String connType;
    public ObservableField<Boolean> isScanning = new ObservableField<>(false);

    //add ObservableList for RecyclerView
    public ObservableList<MultiItemViewModel> observableList = new ObservableArrayList<>();
    public ConnectionViewModel(@NonNull Application application) {
        super(application);
        if(myBaseApplication == null){
            myBaseApplication = (MyBaseApplication) BaseApplication.getInstance();
        }

        pos = myBaseApplication.getQposService();
        spUtils = SPUtils.getInstance();

    }

    public void initData() {
        // 模拟10个条目，数据源可以来自网络
        MultiItemViewModel item = new MultiRecycleHeadViewModel(this);
        // 条目类型为头布局
        item.multiItemType(MultiRecycleType_Head);
        observableList.add(item);
    }

    // 动态添加数据的方法
    public void addData(BluetoothDevice device) {
        // 检查是否已存在相同地址的设备
        boolean deviceExists = false;
        isScanning.set(true);
        for (MultiItemViewModel item : observableList) {
            if (item instanceof MultiRecycleLeftItemViewModel) {
                MultiRecycleLeftItemViewModel leftItem = (MultiRecycleLeftItemViewModel) item;
                // 从现有项中提取蓝牙地址
                String existingAddress = leftItem.text.get();
                if (existingAddress.contains(device.getAddress())) {
                    deviceExists = true;
                    break;
                }
            }
        }
        if(!deviceExists) {
            int imageId = device.getBondState() == BluetoothDevice.BOND_BONDED ? Integer.valueOf(R.mipmap.bluetooth_blue) : Integer.valueOf(R.mipmap.bluetooth_blue_unbond);
            MultiItemViewModel item = new MultiRecycleLeftItemViewModel(this, device.getName() + "(" + device.getAddress() + ")", imageId);
            item.multiItemType(MultiRecycleType_Left);
            observableList.add(item);
        }
    }

    // 动态删除数据的方法
    public void removeData() {
        if (!observableList.isEmpty()) {
            observableList.remove(observableList.size() - 1);
        }
    }

    public ItemBinding<MultiItemViewModel> itemBinding = ItemBinding.of(new OnItemBind<MultiItemViewModel>() {
        @Override
        public void onItemBind(ItemBinding itemBinding, int position, MultiItemViewModel item) {
            //通过item的类型, 动态设置Item加载的布局
            String itemType = (String) item.getItemType();
            if (MultiRecycleType_Head.equals(itemType)) {
                //设置头布局
                itemBinding.set(BR.viewModel, R.layout.item_layout_bluetooth_header);
            } else if (MultiRecycleType_Left.equals(itemType)) {
                //设置左布局
                itemBinding.set(BR.viewModel, R.layout.item_bluetooth_left);
            }
        }
    });

    public BindingCommand<RadioButton> bluConnCommand = new BindingCommand(new BindingConsumer<RadioButton>() {

        @Override
        public void call(RadioButton radioButton) {
            TRACE.i("fo 12312321"+radioButton.getText());
            connectionChangeEvent.setValue(radioButton);
        }
    });

    public BindingCommand<RadioButton> bleConnCommand = new BindingCommand(new BindingConsumer<RadioButton>() {

        @Override
        public void call(RadioButton radioButton) {
            TRACE.i("fo 12312321"+radioButton.getText());
            connectionChangeEvent.setValue(radioButton);
        }
    });

    // USB 连接命令
    public BindingCommand usbConnCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            if (isUSBConnected.get()) {
                close(POS_TYPE.USB);
            } else {
                openUSB();
            }
        }
    });

    public void close(POS_TYPE posType) {
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

    public void scanBluetooth(POS_TYPE posType){
        if (posType == null) {
            TRACE.d("return close");
        } else if (posType == POS_TYPE.BLUETOOTH) {
            myBaseApplication.open(QPOSService.CommunicationMode.BLUETOOTH, getApplication());
            pos = myBaseApplication.getQposService();
            pos.scanQPos2Mode(getApplication(),20);
        } else if (posType == POS_TYPE.BLUETOOTH_BLE) {
            myBaseApplication.open(QPOSService.CommunicationMode.BLUETOOTH_BLE, getApplication());
            pos = myBaseApplication.getQposService();
            pos.startScanQposBLE(20);
        }
    }

    public POS_TYPE getCurrentPosType() {
        return currentPosType;
    }

    public void setCurrentPosType(POS_TYPE posType) {
        this.currentPosType = posType;
    }

    public String getBlueTootchName() {
        return blueTootchName;
    }

    public void setBlueTootchName(String blueTootchName) {
        this.blueTootchName = blueTootchName;
    }

    public void connectBluetooth(POS_TYPE posType, String blueTootchAddress){
        isConnecting.set(true);
        if (posType == null) {
            TRACE.d("return close");
        } else if (posType == POS_TYPE.BLUETOOTH) {
            pos.stopScanQPos2Mode();
            pos.connectBluetoothDevice(true, 25, blueTootchAddress);
        } else if (posType == POS_TYPE.BLUETOOTH_BLE) {
           pos.stopScanQposBLE();
           pos.connectBLE(blueTootchAddress);
        }
    }

    public void openUart(){
        currentPosType = POS_TYPE.UART;
        myBaseApplication.open(QPOSService.CommunicationMode.UART, getApplication());
        pos = myBaseApplication.getQposService();
        pos.setDeviceAddress("/dev/ttyS1");
        pos.openUart();
    }

    public void openUSB(){
        currentPosType = POS_TYPE.USB;
        openUSBDevice();
    }

    private void openUSBDevice() {
        USBClass usb = new USBClass();
        ArrayList<String> deviceList = usb.GetUSBDevices(getApplication());

        if (deviceList == null) {
            Toast.makeText(getApplication(), "No Permission", Toast.LENGTH_SHORT).show();
            isUSBConnected.set(false);
            return;
        }

        if (deviceList.size() == 1) {
            String selectedDevice = deviceList.get(0);
            onUsbDeviceSelected(selectedDevice);
        } else {
            // 触发事件，让 Fragment 显示对话框
            showUsbDeviceDialogEvent.setValue(deviceList);
        }
    }

    // 添加处理设备选择的方法
    public void onUsbDeviceSelected(String selectedDevice) {
        usbDevice = USBClass.getMdevices().get(selectedDevice);
        myBaseApplication.open(QPOSService.CommunicationMode.USB_OTG_CDC_ACM, getApplication());
        pos = myBaseApplication.getQposService();
        pos.openUsb(usbDevice);
    }

    public void onBluetoothConnected(boolean isConnected, String deviceName) {
        TRACE.i("con = "+deviceName);
        isConnecting.set(false);
        isScanning.set(false);
        if(isConnected) {
            connectionStatus.set("Connected the device: " + deviceName);
        }else {
            connectionStatus.set("Connection Method");
        }
    }

    public void onUartConnected() {
        uartSwitchState.setValue(true);
        isConnected.set(true);
        uartConnectionStatus.set("Connected Successfully!");
        ToastUtils.showShort("Uart has been connected!");
    }

    public void onUsbConnected() {
        isUSBConnected.set(true);
        usbConnectionStatus.set("Connected Successfully!");
    }

    public void onDeviceDisconnected(POS_TYPE posType) {
        if(posType == POS_TYPE.UART){
            ToastUtils.showShort("Uart has been disconnected!");
            uartConnectionStatus.set("Connected Failed!");
            isConnected.set(false);
        }else if(posType == POS_TYPE.USB){
            isUSBConnected.set(false);
            usbConnectionStatus.set("Connection Disconnected!");
        }
    }

    public void onNoDeviceDetected(POS_TYPE posType) {
        if(posType == POS_TYPE.UART){
            ToastUtils.showShort("Uart has been disconnected!");
            uartConnectionStatus.set("Connected Failed!");
            isConnected.set(false);
        }else if(posType == POS_TYPE.USB){
            isUSBConnected.set(false);
            usbConnectionStatus.set("Connection Disconnected!");
        }
    }


}
