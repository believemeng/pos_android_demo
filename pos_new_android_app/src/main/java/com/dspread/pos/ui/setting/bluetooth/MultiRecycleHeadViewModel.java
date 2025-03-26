package com.dspread.pos.ui.setting.bluetooth;

import androidx.annotation.NonNull;

import com.dspread.pos.ui.setting.ConnectionViewModel;

import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * Create Author：Qianmeng Chen
 * Create Date：2025/03/19
 * Description：
 */

public class MultiRecycleHeadViewModel extends MultiItemViewModel<ConnectionViewModel> {

    public MultiRecycleHeadViewModel(@NonNull ConnectionViewModel viewModel) {
        super(viewModel);
    }

    //条目的点击事件
    public BindingCommand itemClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            // 清空列表
            viewModel.observableList.clear();
            // 重新添加头部
            viewModel.initData();
            // 重新扫描蓝牙设备
            viewModel.scanBluetooth(viewModel.getCurrentPosType());
            ToastUtils.showShort("Click here to refresh the bluetooth");
        }
    });
}
