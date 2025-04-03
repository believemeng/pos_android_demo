package com.dspread.pos.ui.printer.activities;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.action.printerservice.PrintStyle;
import com.dspread.pos.ui.printer.activities.base.BasePrinterViewModel;
import com.dspread.pos_new_android_app.R;
import com.dspread.print.device.bean.PrintLineStyle;
import com.dspread.print.widget.PrintLine;

import me.goldze.mvvmhabit.binding.command.BindingCommand;
import me.goldze.mvvmhabit.bus.event.SingleLiveEvent;

public class PrintTextViewModel extends BasePrinterViewModel {
    public ObservableField<String> alignText = new ObservableField<>("CENTER");
    public ObservableField<String> fontStyle = new ObservableField<>("NORMAL");
    public ObservableField<String> textSize = new ObservableField<>("24");
    public ObservableField<String> maxHeight = new ObservableField<>("100");
    public ObservableField<String> printContent = new ObservableField<>("");
    public SingleLiveEvent<String[]> showAlignDialog = new SingleLiveEvent<>();
    public SingleLiveEvent<String[]> showFontStyleDialog = new SingleLiveEvent<>();
    public SingleLiveEvent<String> showTextSizeDialog = new SingleLiveEvent<>();
    public SingleLiveEvent<String> showMaxHeightDialog = new SingleLiveEvent<>();
    
    public BindingCommand onAlignClick = new BindingCommand(() -> {
        String[] alignOptions = new String[]{
            getApplication().getString(R.string.at_the_left),
            getApplication().getString(R.string.at_the_right),
            getApplication().getString(R.string.at_the_center)
        };
        showAlignDialog.setValue(alignOptions);
    });
    
    public BindingCommand onFontStyleClick = new BindingCommand(() -> {
        String[] fontStyles = new String[]{
            getApplication().getString(R.string.fontStyle_normal),
            getApplication().getString(R.string.fontStyle_bold),
            getApplication().getString(R.string.fontStyle_italic),
            getApplication().getString(R.string.fontStyle_bold_italic)
        };
        showFontStyleDialog.setValue(fontStyles);
    });

    public BindingCommand onFontSizeClick = new BindingCommand(() -> {
        showTextSizeDialog.call();
    });

    public BindingCommand onMaxHeightClick = new BindingCommand(() -> {
        showMaxHeightDialog.call();
    });
    


    public PrintTextViewModel(@NonNull Application application) {
        super(application);
    }

    public void setAlignment(String align) {
        alignText.set(align);
    }
    
    public void setFontStyle(String style) {
        fontStyle.set(style);
    }

    public void setTextSize(String textSize) {
        this.textSize.set(textSize);
    }

    public void setMaxHeight(String maxHeight) {
        this.maxHeight.set(maxHeight);
    }

    @Override
    protected void doPrint() {
        try {
            PrintLineStyle style = new PrintLineStyle();
            // 设置对齐方式
            switch (alignText.get()) {
                case "LEFT":
                    style.setAlign(PrintLine.LEFT);
                    break;
                case "RIGHT":
                    style.setAlign(PrintLine.RIGHT);
                    break;
                case "CENTER":
                    style.setAlign(PrintLine.CENTER);
                    break;
            }
            
            // 设置字体样式
            switch (fontStyle.get()) {
                case "NORMAL":
                    style.setFontStyle(PrintStyle.FontStyle.NORMAL);
                    break;
                case "BOLD":
                    style.setFontStyle(PrintStyle.FontStyle.BOLD);
                    break;
                case "ITALIC":
                    style.setFontStyle(PrintStyle.FontStyle.ITALIC);
                    break;
                case "BOLD_ITALIC":
                    style.setFontStyle(PrintStyle.FontStyle.BOLD_ITALIC);
                    break;
            }
            
            style.setFontSize(Integer.parseInt(textSize.get()));
            getPrinter().setPrintStyle(style);
            getPrinter().setFooter(30);
            getPrinter().printText(printContent.get());
        } catch (Exception e) {
            e.printStackTrace();
            onPrintComplete(false, e.getMessage());
        }
    }
}