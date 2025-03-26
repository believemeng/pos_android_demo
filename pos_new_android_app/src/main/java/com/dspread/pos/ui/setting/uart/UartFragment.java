package com.dspread.pos.ui.setting.uart;

import androidx.lifecycle.Observer;

import com.dspread.pos.enums.POS_TYPE;
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

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.uartSwitchState.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    viewModel.openUart();
                }else {
                    viewModel.close(POS_TYPE.UART);
                }
            }
        });
    }
}
