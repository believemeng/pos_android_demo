package com.dspread.pos.ui.printer.activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.action.printerservice.PrintStyle;
import com.dspread.pos.ui.printer.activities.base.BasePrinterViewModel;

public class PrintFunctionMultiViewModel extends BasePrinterViewModel {
    public ObservableField<String> info = new ObservableField<>();

    public PrintFunctionMultiViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void doPrint() {
        try {
            if (getPrinter() != null) {
                getPrinter().setFooter(30);
                getPrinter().addTexts(new String[]{"TEST1"}, new int[]{1}, new int[]{PrintStyle.Alignment.NORMAL});
                getPrinter().addTexts(new String[]{"TEST1", "TEST2"}, new int[]{1, 4}, new int[]{PrintStyle.Alignment.NORMAL, PrintStyle.Alignment.CENTER});
                getPrinter().addTexts(new String[]{"TEST1", "TEST2", "TEST3"}, new int[]{1, 2, 2}, new int[]{PrintStyle.Alignment.NORMAL, PrintStyle.Alignment.CENTER, PrintStyle.Alignment.ALIGN_OPPOSITE});
                getPrinter().addTexts(new String[]{"TEST1", "TEST2", "TEST3", "TEST4"}, new int[]{1, 1, 1, 2}, new int[]{PrintStyle.Alignment.NORMAL, PrintStyle.Alignment.CENTER, PrintStyle.Alignment.CENTER, PrintStyle.Alignment.ALIGN_OPPOSITE});
                getPrinter().addTexts(new String[]{"TEST1", "TEST2", "TEST3", "TEST4", "TEST5"}, new int[]{1, 1, 1, 1, 1}, new int[]{PrintStyle.Alignment.NORMAL, PrintStyle.Alignment.CENTER, PrintStyle.Alignment.CENTER, PrintStyle.Alignment.CENTER, PrintStyle.Alignment.ALIGN_OPPOSITE});
                getPrinter().addText(" ");
                getPrinter().print(getApplication());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrintComplete(boolean isSuccess, String status) {
        super.onPrintComplete(isSuccess, status);
        info.set("Print Result: " + (isSuccess ? "Success" : "Failed") + "\nStatus: " + status);
    }
}