package com.dspread.pos.ui.setting.connection_settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.dspread.pos.common.enums.POS_TYPE;
import com.dspread.pos.ui.base.TitleProvider;
import com.dspread.pos.ui.setting.device_config.DeviceConfigActivity;
import com.dspread.pos.ui.setting.device_selection.DeviceSelectionActivity;
import com.dspread.pos.utils.TRACE;
import com.dspread.pos_new_android_app.BR;
import com.dspread.pos_new_android_app.R;
import com.dspread.pos_new_android_app.databinding.FragmentConnectionSettingsBinding;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class ConnectionSettingsFragment extends BaseFragment<FragmentConnectionSettingsBinding, ConnectionSettingsViewModel> implements TitleProvider {
    private final int REQUEST_CODE_CURRENCY = 1000;
    private final int REQUEST_TRANSACTION_TYPE = 1001;
    private final int REQUEST_CARD_MODE = 1002;

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_connection_settings;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public ConnectionSettingsViewModel initViewModel() {
        return new ViewModelProvider(this).get(ConnectionSettingsViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();

        // 设置事件监听
        setupEventListeners();
    }

    /**
     * 设置事件监听
     */
    private void setupEventListeners() {
        // 选择设备事件
        viewModel.selectDeviceEvent.observe(this, v -> {
            navigateToDeviceSelection();
        });

        // 交易类型点击事件
        viewModel.transactionTypeClickEvent.observe(this, v -> {
            Intent intent = new Intent(getActivity(), DeviceConfigActivity.class);
            intent.putExtra(DeviceConfigActivity.EXTRA_LIST_TYPE,
                    DeviceConfigActivity.TYPE_TRANSACTION);
            startActivityForResult(intent, REQUEST_TRANSACTION_TYPE);
        });

        // 卡片模式点击事件
        viewModel.cardModeClickEvent.observe(this, v -> {
            Intent intent = new Intent(getActivity(), DeviceConfigActivity.class);
            intent.putExtra(DeviceConfigActivity.EXTRA_LIST_TYPE,
                    DeviceConfigActivity.TYPE_CARD_MODE);
            startActivityForResult(intent, REQUEST_CARD_MODE);
        });

        // 货币代码点击事件
        viewModel.currencyCodeClickEvent.observe(this, v -> {
//            showCurrencyCodeDialog();
            Intent intent = new Intent(getActivity(), DeviceConfigActivity.class);
            intent.putExtra(DeviceConfigActivity.EXTRA_LIST_TYPE,
                    DeviceConfigActivity.TYPE_CURRENCY);
            startActivityForResult(intent, REQUEST_CODE_CURRENCY);
        });
    }

    /**
     * 导航到设备选择界面
     */
    private void navigateToDeviceSelection() {
        // 创建Intent
        Intent intent = new Intent(getActivity(), DeviceSelectionActivity.class);
        // 启动Activity并等待结果
        startActivityForResult(intent, DeviceSelectionActivity.REQUEST_CODE_SELECT_DEVICE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == DeviceSelectionActivity.REQUEST_CODE_SELECT_DEVICE) {
                // 获取设备名称
                String deviceName = data.getStringExtra(DeviceSelectionActivity.EXTRA_DEVICE_NAME);

                // 获取连接类型
                String connectionType = data.getStringExtra(DeviceSelectionActivity.EXTRA_CONNECTION_TYPE);

                // 更新设备名称
                if (deviceName != null) {
                    viewModel.updateDeviceName(connectionType + "(" + deviceName + ")");
                } else {
                    viewModel.updateDeviceName(connectionType);
                }

                // 更新设备连接状态
                if (connectionType != null) {
                    POS_TYPE posType = POS_TYPE.valueOf(connectionType);
                    viewModel.deviceConnected.set(posType != null);
                    viewModel.saveSettings();
                }

                ToastUtils.showShort("Selected Devices " + deviceName);
            } else if (requestCode == REQUEST_CODE_CURRENCY) {
                String currencyName = data.getStringExtra("currency_name");
                viewModel.currencyCode.set(currencyName);
                TRACE.i("currency code = " + currencyName);
            } else if (requestCode == REQUEST_TRANSACTION_TYPE) {
                String transactionType = data.getStringExtra("transaction_type");
                viewModel.transactionType.set(transactionType);
                TRACE.i("transactionType = " + transactionType);
                // 处理交易类型选择结果
            }else if(requestCode == REQUEST_CARD_MODE){
                String cardMode = data.getStringExtra("card_mode");
                viewModel.cardMode.set(cardMode);
                TRACE.i("cardMode = " + cardMode);
            }else {
                viewModel.loadSettings();
            }
        }
    }

    @Override
    public String getTitle() {
        return "Settings";
    }
}
