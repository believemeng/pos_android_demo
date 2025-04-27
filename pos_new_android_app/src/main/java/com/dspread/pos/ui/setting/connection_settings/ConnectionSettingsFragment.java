package com.dspread.pos.ui.setting.connection_settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.dspread.pos.common.enums.POS_TYPE;
import com.dspread.pos.ui.base.TitleProvider;
import com.dspread.pos.ui.setting.device_selection.DeviceSelectionActivity;
import com.dspread.pos_new_android_app.BR;
import com.dspread.pos_new_android_app.R;
import com.dspread.pos_new_android_app.databinding.FragmentConnectionSettingsBinding;

import me.goldze.mvvmhabit.base.BaseFragment;
import me.goldze.mvvmhabit.utils.ToastUtils;

public class ConnectionSettingsFragment extends BaseFragment<FragmentConnectionSettingsBinding, ConnectionSettingsViewModel> implements TitleProvider {

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
            showTransactionTypeDialog();
        });

        // 卡片模式点击事件
        viewModel.cardModeClickEvent.observe(this, v -> {
            showCardModeDialog();
        });

        // 货币代码点击事件
        viewModel.currencyCodeClickEvent.observe(this, v -> {
            showCurrencyCodeDialog();
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

        // 处理设备选择结果
        if (requestCode == DeviceSelectionActivity.REQUEST_CODE_SELECT_DEVICE && resultCode == Activity.RESULT_OK && data != null) {
            // 获取设备名称
            String deviceName = data.getStringExtra(DeviceSelectionActivity.EXTRA_DEVICE_NAME);

            // 获取连接类型
            String connectionType = data.getStringExtra(DeviceSelectionActivity.EXTRA_CONNECTION_TYPE);

            // 更新设备名称
            if (deviceName != null) {
                viewModel.updateDeviceName(connectionType+"("+deviceName+")");
            }else {
                viewModel.updateDeviceName(connectionType);
            }

            // 更新设备连接状态
            if (connectionType != null) {
                POS_TYPE posType = POS_TYPE.valueOf(connectionType);
                viewModel.deviceConnected.set(posType != null);
                viewModel.saveSettings();
            }

            ToastUtils.showShort("已选择设备: " + deviceName);
        }
    }

    /**
     * 显示交易类型选择对话框
     */
    private void showTransactionTypeDialog() {
        String[] transactionTypes = {"消费", "撤销", "退货", "预授权", "预授权完成", "预授权撤销", "预授权完成撤销"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择交易类型");
        builder.setItems(transactionTypes, (dialog, which) -> {
            viewModel.updateTransactionType(transactionTypes[which]);
        });
        builder.show();
    }

    /**
     * 显示卡片模式选择对话框
     */
    private void showCardModeDialog() {
        String[] cardModes = {"刷卡+插卡+挥卡", "仅刷卡", "仅插卡", "仅挥卡", "刷卡+插卡", "刷卡+挥卡", "插卡+挥卡"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择卡片模式");
        builder.setItems(cardModes, (dialog, which) -> {
            viewModel.updateCardMode(cardModes[which]);
        });
        builder.show();
    }

    /**
     * 显示货币代码选择对话框
     */
    private void showCurrencyCodeDialog() {
        String[] currencyCodes = {"CNY", "USD", "EUR", "GBP", "JPY", "HKD"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("选择货币代码");
        builder.setItems(currencyCodes, (dialog, which) -> {
            viewModel.updateCurrencyCode(currencyCodes[which]);
        });
        builder.show();
    }

    @Override
    public String getTitle() {
        return "Settings";
    }
}
