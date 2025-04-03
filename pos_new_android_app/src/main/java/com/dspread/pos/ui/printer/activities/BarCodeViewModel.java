package com.dspread.pos.ui.printer.activities;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.action.printerservice.barcode.Barcode1D;
import com.dspread.pos.ui.printer.activities.base.BasePrinterViewModel;
import com.dspread.pos.ui.printer.activities.base.PrintDialog;
import com.dspread.pos_new_android_app.R;

import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class BarCodeViewModel extends BasePrinterViewModel {
    public ObservableField<String> content = new ObservableField<>("1234567890");
    public ObservableField<String> symbology = new ObservableField<>("CODE_128");
    public ObservableField<String> height = new ObservableField<>("100");
    public ObservableField<String> width = new ObservableField<>("400");
    public ObservableField<String> align = new ObservableField<>("CENTER");
    public ObservableField<String> grayLevel = new ObservableField<>("5");
    public ObservableField<String> speedLevel = new ObservableField<>("5");
    public ObservableField<String> densityLevel = new ObservableField<>("5");
    public ObservableField<Bitmap> barcodeBitmap = new ObservableField<>();

    public SingleLiveEvent<String> showInputDialog = new SingleLiveEvent<>();
    public SingleLiveEvent<String[]> showOptionsDialog = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> showHeightDialog = new SingleLiveEvent<>();
    public SingleLiveEvent<Boolean> showWidthDialog = new SingleLiveEvent<>();

    public BindingCommand onContentClick = new BindingCommand(() -> {
        // 显示内容输入对话框
        showInputDialog.call();
    });
    
    public BindingCommand onSymbologyClick = new BindingCommand(() -> {
        String[] options = {Barcode1D.CODE_128.name(), Barcode1D.CODABAR.name(), Barcode1D.CODE_39.name(), Barcode1D.EAN_8.name(),
                Barcode1D.EAN_13.name(), Barcode1D.UPC_A.name(), Barcode1D.UPC_E.name()};
        showOptionsDialog.setValue(options);
    });
    
    public BindingCommand onHeightClick = new BindingCommand(() -> {
        showHeightDialog.setValue(true);
    });

    public BindingCommand onWidthClick = new BindingCommand(() -> {
        showWidthDialog.setValue(true);

    });

    public BarCodeViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void doPrint() {
//        try {
//            // 实现条形码打印逻辑
//            getPrinter().printBarCode(content.get(),
//                Integer.parseInt(height.get()),
//                Integer.parseInt(width.get()),
//                align.get());
//        } catch (Exception e) {
//            e.printStackTrace();
//            onPrintComplete(false, e.getMessage());
//        }
    }
    
    public void generateBarcode(Bitmap bitmap) {
        // 生成条形码预览图
        // ... 生成条形码的具体实现 ...
        barcodeBitmap.set(bitmap);
    }
}