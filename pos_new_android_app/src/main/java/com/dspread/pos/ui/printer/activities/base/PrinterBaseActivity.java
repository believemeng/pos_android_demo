package com.dspread.pos.ui.printer.activities.base;

import android.os.Bundle;

import androidx.databinding.ViewDataBinding;

import com.dspread.pos.ui.printer.printeractivity.PrinterAlertDialog;
import com.dspread.pos.utils.DeviceUtils;
import com.dspread.pos_new_android_app.BR;
import com.dspread.pos_new_android_app.R;
import com.dspread.print.device.PrintListener;
import com.dspread.print.device.PrinterDevice;
import com.dspread.print.device.PrinterInitListener;
import com.dspread.print.device.PrinterManager;

import me.goldze.mvvmhabit.base.BaseActivity;

public abstract class PrinterBaseActivity<V extends ViewDataBinding, VM extends BasePrinterViewModel> extends BaseActivity<V, VM> {
    protected PrinterDevice mPrinter;

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_printer_base;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        PrinterManager instance = PrinterManager.getInstance();
        mPrinter = instance.getPrinter();
        if (mPrinter == null) {
            PrinterAlertDialog.showAlertDialog(this);
            return;
        }
        
        // 设置打印机实例到 ViewModel
        viewModel.setPrinter(mPrinter);
        
        initPrinter();
        MyPrinterListener myPrinterListener = new MyPrinterListener();
        mPrinter.setPrintListener(myPrinterListener);
    }

    private void initPrinter() {
        if (!DeviceUtils.isAppInstalled(getApplicationContext(), DeviceUtils.UART_AIDL_SERVICE_APP_PACKAGE_NAME)) {
            mPrinter.initPrinter(this, new PrinterInitListener() {
                @Override
                public void connected() {
                    mPrinter.setPrinterTerminatedState(PrinterDevice.PrintTerminationState.PRINT_STOP);
                }
                @Override
                public void disconnected() {
                }
            });
        } else {
            mPrinter.initPrinter(this);
        }
    }

    protected abstract void onReturnPrintResult(boolean isSuccess, String status, PrinterDevice.ResultType resultType);

    class MyPrinterListener implements PrintListener {
        @Override
        public void printResult(boolean b, String s, PrinterDevice.ResultType resultType) {
            onReturnPrintResult(b, s, resultType);
        }
    }
}
