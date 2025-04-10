package com.dspread.pos.ui.printer.activities;

import android.app.Application;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

import com.action.printerservice.PrintStyle;
import com.action.printerservice.barcode.Barcode1D;
import com.action.printerservice.barcode.Barcode2D;
import com.dspread.pos.ui.printer.activities.base.BasePrinterViewModel;
import com.dspread.print.device.bean.PrintLineStyle;
import com.dspread.print.widget.PrintLine;


public class PrintTicketViewModel extends BasePrinterViewModel {

    public ObservableField<String> info = new ObservableField<>();
    public PrintTicketViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void doPrint() {
        try {
            if (getPrinter() != null) {
                getPrinter().addPrintLintStyle(new PrintLineStyle(PrintStyle.FontStyle.BOLD, PrintLine.CENTER, 16));
                getPrinter().addText("Testing");
                getPrinter().addText("POS Signing of purchase orders");
                getPrinter().addText("MERCHANT COPY");
                getPrinter().addPrintLintStyle(new PrintLineStyle(PrintStyle.FontStyle.NORMAL, PrintLine.CENTER, 14));
                getPrinter().addText("- - - - - - - - - - - - - -");
                getPrinter().addPrintLintStyle(new PrintLineStyle(PrintStyle.FontStyle.NORMAL, PrintLine.LEFT, 14));
                getPrinter().addText("ISSUER Agricultural Bank of China");
                getPrinter().addText("ACQ 48873110");
                getPrinter().addText("CARD number.");
                getPrinter().addPrintLintStyle(new PrintLineStyle(PrintStyle.FontStyle.NORMAL, PrintLine.LEFT, 14));
                getPrinter().addText("6228 48******8 116 S");
                getPrinter().addText("TYPE of transaction(TXN TYPE)");
                getPrinter().addText("SALE");
                getPrinter().addPrintLintStyle(new PrintLineStyle(PrintStyle.FontStyle.NORMAL, PrintLine.CENTER, 14));
                getPrinter().addText("- - - - - - - - - - - - - -");
                getPrinter().addTexts(new String[]{"BATCH NO", "000043"}, new int[]{5, 5}, new int[]{PrintStyle.Alignment.NORMAL, PrintStyle.Alignment.CENTER});
                getPrinter().addTexts(new String[]{"VOUCHER NO", "000509"}, new int[]{5, 5}, new int[]{PrintStyle.Alignment.NORMAL, PrintStyle.Alignment.CENTER});
                getPrinter().addTexts(new String[]{"AUTH NO", "000786"}, new int[]{5, 5}, new int[]{PrintStyle.Alignment.NORMAL, PrintStyle.Alignment.CENTER});
                getPrinter().addTexts(new String[]{"DATE/TIME", "2010/12/07 16:15:17"}, new int[]{5, 5}, new int[]{PrintStyle.Alignment.NORMAL, PrintStyle.Alignment.CENTER});
                getPrinter().addTexts(new String[]{"REF NO", "000001595276"}, new int[]{5, 5}, new int[]{PrintStyle.Alignment.NORMAL, PrintStyle.Alignment.CENTER});
                getPrinter().addTexts(new String[]{"2014/12/07 16:12:17", ""}, new int[]{5, 5}, new int[]{PrintStyle.Alignment.NORMAL, PrintStyle.Alignment.CENTER});
                getPrinter().addTexts(new String[]{"AMOUNT:", ""}, new int[]{5, 5}, new int[]{PrintStyle.Alignment.NORMAL, PrintStyle.Alignment.CENTER});
                getPrinter().addText("RMB:249.00");
                getPrinter().addPrintLintStyle(new PrintLineStyle(PrintStyle.FontStyle.NORMAL, PrintLine.CENTER, 12));
                getPrinter().addText("- - - - - - - - - - - - - -");
                getPrinter().addText("Please scan the QRCode for getting more information: ");
                getPrinter().addBarCode(getApplication(), Barcode1D.CODE_128.name(), 400, 100, "123456", PrintLine.CENTER);
                getPrinter().addText("Please scan the QRCode for getting more information:");
                getPrinter().addQRCode(300, Barcode2D.QR_CODE.name(), "123456", PrintLine.CENTER);
                getPrinter().setFooter(20);
                getPrinter().print(getApplication());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrintComplete(boolean isSuccess, String status) {
        super.onPrintComplete(isSuccess, status);
        info.set("Print Result: " + (isSuccess ? "Success" : "Failed") + "\nStatus: " + status);
    }
}