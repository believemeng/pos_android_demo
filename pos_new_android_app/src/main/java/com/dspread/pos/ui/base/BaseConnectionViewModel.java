package com.dspread.pos.ui.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dspread.pos.base.BaseAppViewModel;

import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class BaseConnectionViewModel extends BaseAppViewModel {
    private SingleLiveEvent<Boolean> connectionStatus = new SingleLiveEvent<>();

    public BaseConnectionViewModel(@NonNull Application application) {
        super(application);
    }

    public void setConnected(boolean isConnected) {
        connectionStatus.postValue(isConnected);
    }
    
    public LiveData<Boolean> getConnectionStatus() {
        return connectionStatus;
    }
}