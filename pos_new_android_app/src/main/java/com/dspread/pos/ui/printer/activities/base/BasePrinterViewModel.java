package com.dspread.pos.ui.printer.activities.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.dspread.pos.base.BaseAppViewModel;
import com.dspread.print.device.PrinterDevice;

import me.goldze.mvvmhabit.binding.command.BindingCommand;

public abstract class BasePrinterViewModel extends BaseAppViewModel {
    protected PrinterDevice mPrinter;

    public void setPrinter(PrinterDevice printer) {
        this.mPrinter = printer;
    }

    protected PrinterDevice getPrinter() {
        return mPrinter;
    }
    public ObservableField<String> title = new ObservableField<>();
    public ObservableBoolean isLoading = new ObservableBoolean(false);
    public ObservableField<String> resultText = new ObservableField<>();
    
    public BasePrinterViewModel(@NonNull Application application) {
        super(application);
    }
    
    public BindingCommand printCommand = new BindingCommand(() -> {
        isLoading.set(true);
        doPrint();
    });
    
    protected abstract void doPrint();
    
    public void onPrintComplete(boolean success, String message) {
        isLoading.set(false);
        resultText.set(message);
    }
}