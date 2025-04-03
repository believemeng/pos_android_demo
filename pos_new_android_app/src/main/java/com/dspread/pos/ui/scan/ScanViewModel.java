package com.dspread.pos.ui.scan;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.dspread.pos.base.BaseAppViewModel;
import com.dspread.pos_new_android_app.R;

import me.goldze.mvvmhabit.binding.command.BindingAction;
import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class ScanViewModel extends BaseAppViewModel {
    public ObservableBoolean isScanning = new ObservableBoolean(false);
    public ObservableBoolean hasResult = new ObservableBoolean(false);
    public ObservableField<String> scanResult = new ObservableField<>("");
    // 移除 launcher 相关代码
    public SingleLiveEvent<Void> startScanEvent = new SingleLiveEvent<>();


    public BindingCommand startScanCommand = new BindingCommand(new BindingAction() {
        @Override
        public void call() {
            startScan();
        }
    });

    public ScanViewModel(@NonNull Application application) {
        super(application);
    }
    
    private void startScan() {
        isScanning.set(true);
        hasResult.set(false);
        startScanEvent.call();
    }

    public void onScanResult(String result) {
        isScanning.set(false);
        hasResult.set(true);
        scanResult.set(result);
    }
}
