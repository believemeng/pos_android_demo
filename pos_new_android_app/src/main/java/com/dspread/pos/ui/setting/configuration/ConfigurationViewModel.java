package com.dspread.pos.ui.setting.configuration;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;

import com.dspread.pos.common.enums.POS_TYPE;
import com.dspread.pos.common.enums.PaymentType;
import com.dspread.pos.common.enums.TransCardMode;

import me.goldze.mvvmhabit.base.BaseViewModel;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.utils.SPUtils;

public class ConfigurationViewModel extends BaseViewModel {
    // 交易类型列表
    public final ObservableArrayList<String> transTypeItems = new ObservableArrayList<>();
    public final ObservableArrayList<String> cardTradeModeItems = new ObservableArrayList<>();
    // 当前选中的交易类型
    public final ObservableField<String> selectedTransType = new ObservableField<>();
    public final SingleLiveEvent<Integer> selectedTradeMode = new SingleLiveEvent<>();
    public ConfigurationViewModel(@NonNull Application application) {
        super(application);
        initTransactionTypes();
        initCardTradeModes();
    }

    private void initTransactionTypes() {
        transTypeItems.clear();
        for(int i = 0; i < PaymentType.getValues().length; i++){
            transTypeItems.add(PaymentType.getValues()[i]);
        }
        selectedTransType.set(transTypeItems.get(0));
    }

    private void initCardTradeModes() {
        cardTradeModeItems.clear();
        for(int i = 0; i < TransCardMode.getCardTradeModes().length; i++){
            cardTradeModeItems.add(TransCardMode.getCardTradeModes()[i]);
        }
        if(!"".equals(SPUtils.getInstance().getString("ConnectionType"))){
            if(POS_TYPE.UART.name().equals(SPUtils.getInstance().getString("ConnectionType"))){
                selectedTradeMode.setValue(0);
            }else {
                selectedTradeMode.setValue(1);
            }
        }else {
            selectedTradeMode.setValue(1);
        }
    }

    // 交易类型选择回调
    public BindingCommand<Integer> onTransTypeSelected = new BindingCommand<>(position -> {
        if (position >= 0 && position < transTypeItems.size()) {
            selectedTransType.set(transTypeItems.get(position));
            SPUtils.getInstance().put("transType",transTypeItems.get(position));
        }
    });

    // 交易类型选择回调
    public BindingCommand<Integer> onCardTradeModeSelected = new BindingCommand<>(position -> {
        if (position >= 0 && position < cardTradeModeItems.size()) {
            selectedTradeMode.setValue(position);
            SPUtils.getInstance().put("cardTradeMode", cardTradeModeItems.get(position));
        }
    });

}