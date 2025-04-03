package com.dspread.pos.ui.printer.activities;

import android.util.Log;

import androidx.lifecycle.Observer;

import com.dspread.pos.ui.printer.activities.base.PrintDialog;
import com.dspread.pos.ui.printer.activities.base.PrinterBaseActivity;
import com.dspread.pos_new_android_app.R;
import com.dspread.pos_new_android_app.databinding.ActivityBarCodeBinding;
import com.dspread.pos_new_android_app.databinding.ActivityPrinterBaseBinding;
import com.dspread.print.device.PrinterDevice;

public class BarCodeActivity extends PrinterBaseActivity<ActivityPrinterBaseBinding, BarCodeViewModel> {
    private ActivityBarCodeBinding contentBinding;

    @Override
    public void initData() {
        super.initData();
        contentBinding = ActivityBarCodeBinding.inflate(getLayoutInflater());
        contentBinding.setViewModel(viewModel);
        binding.contentContainer.addView(contentBinding.getRoot());
    }

    @Override
    public void initViewObservable() {
        viewModel.showInputDialog.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                PrintDialog.printInputDialog(BarCodeActivity.this, getString(R.string.input_barcode), new PrintDialog.PrintClickListener() {
                    @Override
                    public void onCancel() {
                        PrintDialog.printInputDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String str) {
                        viewModel.content.set(str);
                    }
                });
            }
        });

        viewModel.showOptionsDialog.observe(this, symbology ->
                PrintDialog.setDialog(BarCodeActivity.this, getString(R.string.symbology_barcode), symbology, new PrintDialog.PrintClickListener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onConfirm(String str) {
                viewModel.symbology.set(str);
            }
        }));
        viewModel.showHeightDialog.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    PrintDialog.showSeekBarDialog(BarCodeActivity.this, getResources().getString(R.string.Barcode_height), 1, 200, contentBinding.txtHeight, new PrintDialog.PrintClickListener() {
                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onConfirm(String str) {
                            Log.w("height", "heigt=" + str);
                            viewModel.height.set(str);
                        }
                    });

                }
            }
        });
        viewModel.showWidthDialog.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    PrintDialog.showSeekBarDialog(getApplication(), getApplication().getResources().getString(R.string.Barcode_width), 1, 600, contentBinding.txtWidth, new PrintDialog.PrintClickListener() {
                        @Override
                        public void onCancel() {

                        }

                        @Override
                        public void onConfirm(String str) {
                            Log.w("width", "width=" + str);
                            viewModel.width.set(str);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onReturnPrintResult(boolean isSuccess, String status, PrinterDevice.ResultType resultType) {
        viewModel.onPrintComplete(isSuccess, status);
    }
}