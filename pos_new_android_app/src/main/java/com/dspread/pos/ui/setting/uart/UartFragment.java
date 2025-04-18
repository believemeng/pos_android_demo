package com.dspread.pos.ui.setting.uart;

import com.dspread.pos.ui.setting.BaseConnectionFragment;
import com.dspread.pos.ui.setting.ConnectionViewModel;
import com.dspread.pos_new_android_app.BR;
import com.dspread.pos_new_android_app.R;
import com.dspread.pos_new_android_app.databinding.FragmentUartBinding;

public class UartFragment extends BaseConnectionFragment<FragmentUartBinding, ConnectionViewModel> {
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_uart;
    }

    @Override
    protected int getVariableId() {
        return BR.viewModel;
    }

    @Override
    protected Class<ConnectionViewModel> getViewModelClass() {
        return ConnectionViewModel.class;
    }

}
