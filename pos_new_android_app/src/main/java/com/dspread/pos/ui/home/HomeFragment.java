package com.dspread.pos.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.dspread.pos.common.base.BaseFragment;
import com.dspread.pos.ui.base.TitleProvider;
import com.dspread.pos.ui.payment.PaymentActivity;
import com.dspread.pos_new_android_app.BR;
import com.dspread.pos_new_android_app.R;
import com.dspread.pos_new_android_app.databinding.FragmentHomeBinding;


public class HomeFragment extends BaseFragment<FragmentHomeBinding, HomeViewModel> implements TitleProvider {
//    private KeyboardUtil keyboardUtil;
    private boolean canshow = true;
    private CountDownTimer showTimer;
    
    @Override
    public int initContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return R.layout.fragment_home;
    }
    
    @Override
    public int initVariableId() {
        return BR.viewModel;
    }
    
    @Override
    public void initData() {
        getActivity().setTitle(getString(R.string.menu_payment));
        initTimer();
    }
    
    private void initTimer() {
        showTimer = new CountDownTimer(500, 500) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                canshow = true;
            }
        };
    }
    
    @Override
    public void initViewObservable() {
        viewModel.paymentStartEvent.observe(this, inputMoney -> {
            if (!canshow) return;
            canshow = false;
            showTimer.start();
            Intent intent = new Intent(getActivity(), PaymentActivity.class);
            intent.putExtra("amount", String.valueOf(inputMoney));
            startActivity(intent);
        });
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode != KeyEvent.KEYCODE_BACK) {
//            keyboardUtil.getmOnKeyboardActionListener().onKey(keyCode, null);
        }
        return true;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (showTimer != null) {
            showTimer.cancel();
        }
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public String getTitle() {
        return "Payment";
    }
}

