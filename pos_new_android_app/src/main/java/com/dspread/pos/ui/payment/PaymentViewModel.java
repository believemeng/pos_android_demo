package com.dspread.pos.ui.payment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.Observer;

import com.dspread.pos.base.BaseAppViewModel;
import com.dspread.pos.enums.POS_TYPE;
import com.dspread.pos.http.RetrofitClient;
import com.dspread.pos.http.api.DingTalkApiService;
import com.dspread.pos.http.model.DingTalkRequest;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;
import me.goldze.mvvmhabit.http.BaseResponse;
import me.goldze.mvvmhabit.utils.RxUtils;
import me.goldze.mvvmhabit.utils.ToastUtils;


public class PaymentViewModel extends BaseAppViewModel {
    private static final String DINGTALK_URL = "https://oapi.dingtalk.com/robot/send?access_token=83e8afc691a1199c70bb471ec46d50099e6dd078ce10223bbcc56c0485cb5cc3";
    private DingTalkApiService apiService;
    
    public PaymentViewModel(@NonNull Application application) {
        super(application);
        apiService = RetrofitClient.getInstance().create(DingTalkApiService.class);
    }
    
    public ObservableField<String> loadingText = new ObservableField<>("");
    public ObservableBoolean isLoading = new ObservableBoolean(false);
    public ObservableField<String> transactionResult = new ObservableField<>("");
    public ObservableField<String> amount = new ObservableField<>("");
    public ObservableField<String> titleText = new ObservableField<>("Payment");
    public ObservableBoolean isWaiting = new ObservableBoolean(true);
    public ObservableBoolean isSuccess = new ObservableBoolean(false);
    public SingleLiveEvent<Boolean> isOnlineSuccess = new SingleLiveEvent();
    public SingleLiveEvent<Boolean> isContinueTrx = new SingleLiveEvent();
    public ObservableBoolean showPinpad = new ObservableBoolean(false);
    public ObservableBoolean showResultStatus = new ObservableBoolean(false);
    
    public void setTransactionSuccess(String message) {
        setTransactionSuccess();
        transactionResult.set(message);
    }
    
    public void setTransactionFailed(String message) {
        titleText.set("Payment finished");
        stopLoading();
        showPinpad.set(false);
        isSuccess.set(false);
        showResultStatus.set(true);
        isWaiting.set(false);
        transactionResult.set(message);
    }

    public void setTransactionResult(String newStatus) {
        transactionResult.set(newStatus);
    }
    
    public void setAmount(String newAmount) {
        amount.set("¥" + newAmount);
    }

    public void setWaitingStatus(boolean isWaitings){
        isWaiting.set(isWaitings);
    }
    
    public void setTransactionSuccess() {
        titleText.set("Payment finished");
        stopLoading();
        showPinpad.set(false);
        isSuccess.set(true);
        isWaiting.set(false);
        showResultStatus.set(true);
    }
    
    public void startLoading(String text) {
        isWaiting.set(false);
        isLoading.set(true);
        loadingText.set(text);
    }
    
    public void stopLoading() {
        isLoading.set(false);
        isWaiting.set(false);
        loadingText.set("");
    }

    public BindingCommand continueTxnsCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            titleText.set("Payment");
            stopLoading();
            showPinpad.set(false);
            isSuccess.set(false);
            isWaiting.set(false);
            showResultStatus.set(false);
            isContinueTrx.setValue(true);
        }
    });


    
    // 修改发送消息的方法
    public void sendDingTalkMessage(boolean isICC, String tlvData, String message) {
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("text", message);
        textContent.put("title","issues");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("msgtype", "markdown");
        requestBody.put("markdown", textContent);

        addSubscribe(apiService.sendMessage(DINGTALK_URL, requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse>() {
                    @Override
                    public void accept(BaseResponse response) throws Exception {
                        if (response.isOk()) {
                            ToastUtils.showShort("Send online success");
                            isOnlineSuccess.setValue(true);
                            if(!isICC){
                                transactionResult.set(tlvData);
                            }
                        } else {
                            isOnlineSuccess.setValue(false);
                            transactionResult.set("Send online failed：" + response.getMessage());
                            ToastUtils.showShort("Send online failed：" + response.getMessage());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        isOnlineSuccess.setValue(false);
                        ToastUtils.showShort("The network is failed：" + throwable.getMessage());
                        transactionResult.set("The network is failed：" + throwable.getMessage());
                    }
                }));
        }


}