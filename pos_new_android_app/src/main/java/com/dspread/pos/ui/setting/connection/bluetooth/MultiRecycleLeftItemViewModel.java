package com.dspread.pos.ui.setting.connection.bluetooth;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;


import com.dspread.pos.ui.setting.connection.ConnectionViewModel;

import me.goldze.mvvmhabit.base.MultiItemViewModel;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.utils.ToastUtils;

/**
 * Create Author：Qianmeng
 * Create Date：2025/03/20
 * Description：
 */

public class MultiRecycleLeftItemViewModel extends MultiItemViewModel<ConnectionViewModel> {
    public ObservableField<String> text = new ObservableField<>("");
    public ObservableField<Integer> imageRes = new ObservableField<>(); // 添加图片资源的 ObservableField

    public MultiRecycleLeftItemViewModel(@NonNull ConnectionViewModel viewModel, String text, int imageResId) {
        super(viewModel);
        this.text.set(text);
        this.imageRes.set(imageResId);
    }

    //条目的点击事件
    public BindingCommand itemClick = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            //拿到position
            int position = viewModel.observableList.indexOf(MultiRecycleLeftItemViewModel.this);
            MultiRecycleLeftItemViewModel itemViewModel = (MultiRecycleLeftItemViewModel) viewModel.observableList.get(position);
            String itemText = itemViewModel.text.get();
            // 提取括号中的MAC地址
            String deviceAddress = itemText.substring(itemText.indexOf("(") + 1, itemText.indexOf(")"));
            String deviceName = itemText.substring(0,itemText.indexOf("("));
            viewModel.connectBluetooth(viewModel.getCurrentPosType(),deviceAddress);
            viewModel.setBlueTootchName(deviceName);
            ToastUtils.showShort("position：" + position);
        }
    });
}
