package com.dspread.pos.ui.setting.connection.bluetooth;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dspread.pos.common.enums.POS_TYPE;
import com.dspread.pos.posAPI.MyCustomQPOSCallback;
import com.dspread.pos.ui.setting.connection.BaseConnectionFragment;
import com.dspread.pos.ui.setting.connection.ConnectionViewModel;
import com.dspread.pos.utils.TRACE;
import com.dspread.pos_new_android_app.BR;
import com.dspread.pos_new_android_app.R;
import com.dspread.pos_new_android_app.databinding.FragmentBluetoothBinding;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;
import java.util.Objects;

import io.reactivex.functions.Consumer;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class BluetoothFragment extends BaseConnectionFragment<FragmentBluetoothBinding, ConnectionViewModel> implements MyCustomQPOSCallback {
    private RadioButton tBtnClicked;
    private POS_TYPE posType;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_bluetooth;
    }

    @Override
    protected int getVariableId() {
        return BR.viewModel;
    }

    @Override
    protected Class getViewModelClass() {
        return ConnectionViewModel.class;
    }

    @Override
    public void initData() {
        super.initData();

        // 确保 ViewModel 已初始化

        if (viewModel == null) {
            viewModel = new ViewModelProvider(this).get(ConnectionViewModel.class);
        }


    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.connectionChangeEvent.observe(this, new Observer<RadioButton>() {
            @Override
            public void onChanged(RadioButton radioButton) {
                TRACE.i("go to here");
                bluetoothRelaPer(radioButton);
            }
        });

    }

    private void handleConnection(POS_TYPE newType, RadioButton button) {
        if(tBtnClicked != null){
            viewModel.close(posType);
            if(tBtnClicked == button){
                button.setChecked(false);
                viewModel.onBluetoothConnected(false,null);
                tBtnClicked = null;
                return;
            }
        }

        tBtnClicked = button;
        viewModel.observableList.clear();
        viewModel.setCurrentPosType(newType);
        viewModel.scanBluetooth(newType);
        viewModel.initData();
        posType = newType;
    }

    @SuppressLint("CheckResult")
    private void requestBluetoothPermissions(RadioButton radioButton) {
        // 请求蓝牙权限
        RxPermissions rxPermissions = new RxPermissions(Objects.requireNonNull(getActivity()));
        rxPermissions.request(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if(aBoolean){
                    TRACE.i("permission grant ---");
                    if(radioButton.getText().equals("Bluetooth")){
                        handleConnection(POS_TYPE.BLUETOOTH, radioButton);
                    }else if(radioButton.getText().equals("BLE")){
                        handleConnection(POS_TYPE.BLUETOOTH_BLE, radioButton);
                    }
                }else {
                    Toast.makeText(getContext(), "Permissions are denied, pls try again!", Toast.LENGTH_LONG).show();
                    TRACE.i("permission not grant ---");
                }
            }
        });
    }

    public void bluetoothRelaPer(RadioButton radioButton) {
        android.bluetooth.BluetoothAdapter adapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
        if (adapter != null && !adapter.isEnabled()) {//if bluetooth is disabled, add one fix
            Intent enabler = new Intent(android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE);
            try {
                startActivity(enabler);
            }catch (SecurityException e){
                Toast.makeText(getActivity(),"Pls open the bluetooth in device Setting",Toast.LENGTH_LONG).show();
            }
        }
        LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        List<String> listProvider = lm.getAllProviders();
        for (String str : listProvider) {
            TRACE.i("provider : " + str);
        }
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (ok) {//Location service is on
            requestBluetoothPermissions(radioButton);
        } else {
            Toast.makeText(getActivity(), "System detects that the GPS location service is not turned on", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            //ACTION_LOCATION_SOURCE_SETTINGS
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            try {
                ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                    }
                });
                launcher.launch(intent);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Pls open the LOCATION in your device settings! ", Toast.LENGTH_SHORT).show();
            }

        }
    }


    @Override
    public void onRequestDeviceScanFinished() {
        ToastUtils.showShort("Scan finished!");
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDeviceFound(BluetoothDevice device) {
        TRACE.i("scan = "+device);
        if (device != null && device.getName() != null) {
            viewModel.addData(device);
        }
    }
}
