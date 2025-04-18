package com.dspread.pos.ui.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;

import com.dspread.pos.common.base.BaseAppViewModel;
import com.dspread.pos.common.enums.PaymentType;

import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.SPUtils;

public class BaseConnectionViewModel extends BaseAppViewModel {
    private SingleLiveEvent<Boolean> connectionStatus = new SingleLiveEvent<>();
    // 交易类型列表
    public final ObservableArrayList<String> transTypeItems = new ObservableArrayList<>();
    // 当前选中的交易类型
    public final ObservableField<String> selectedTransType = new ObservableField<>();

    public BaseConnectionViewModel(@NonNull Application application) {
        super(application);
        initTransactionTypes();
    }

    public void setConnected(boolean isConnected) {
        connectionStatus.postValue(isConnected);
    }

    private void initTransactionTypes() {
        transTypeItems.clear();
        for(int i = 0; i < PaymentType.getValues().length; i++){
            transTypeItems.add(PaymentType.getValues()[i]);
        }
        selectedTransType.set(transTypeItems.get(0));
    }

    // 交易类型选择回调
    public BindingCommand<Integer> onTransTypeSelected = new BindingCommand<>(position -> {
        if (position >= 0 && position < transTypeItems.size()) {
            selectedTransType.set(transTypeItems.get(position));
            SPUtils.getInstance().put("transType",transTypeItems.get(position));
        }
    });
    
    public LiveData<Boolean> getConnectionStatus() {
        return connectionStatus;
    }

}