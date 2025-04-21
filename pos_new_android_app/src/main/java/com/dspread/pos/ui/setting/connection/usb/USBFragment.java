package com.dspread.pos.ui.setting.connection.usb;

import androidx.appcompat.app.AlertDialog;

import com.dspread.pos.ui.setting.connection.BaseConnectionFragment;
import com.dspread.pos.ui.setting.connection.ConnectionViewModel;
import com.dspread.pos_new_android_app.BR;
import com.dspread.pos_new_android_app.R;
import com.dspread.pos_new_android_app.databinding.FragmentUsbBinding;

public class USBFragment extends BaseConnectionFragment<FragmentUsbBinding, ConnectionViewModel> {
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_usb;
    }

    @Override
    protected int getVariableId() {
        return BR.viewModel;
    }

    @Override
    protected Class<ConnectionViewModel> getViewModelClass() {
        return ConnectionViewModel.class;
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
// 观察 USB 设备选择对话框事件
        viewModel.showUsbDeviceDialogEvent.observe(this, deviceList -> {
            if (getActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select a Reader");

                if (deviceList.isEmpty()) {
                    builder.setMessage(getString(R.string.setting_disusb));
                    builder.setPositiveButton(getString(R.string.confirm), (dialog, which) -> {
                        dialog.dismiss();
                        viewModel.isUSBConnected.set(false);
                    });
                } else {
                    CharSequence[] items = deviceList.toArray(new CharSequence[0]);
                    builder.setSingleChoiceItems(items, -1, (dialog, item) -> {
                        if (items.length > item) {
                            String selectedDevice = items[item].toString();
                            dialog.dismiss();
                            viewModel.onUsbDeviceSelected(selectedDevice);
                        }
                    });
                }

                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            }
        });
    }
}
