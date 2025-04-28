package com.dspread.pos.ui.setting.currency;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.dspread.pos_new_android_app.BR;
import com.dspread.pos_new_android_app.R;
import com.dspread.pos_new_android_app.databinding.ActivityCurrencySelectionBinding;

import me.goldze.mvvmhabit.base.BaseActivity;

public class CurrencySelectionActivity extends BaseActivity<ActivityCurrencySelectionBinding, CurrencySelectionViewModel> {
    
    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.activity_currency_selection;
    }


    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();
        
        // 设置返回按钮点击事件
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        
        // 设置搜索监听
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filterCurrencies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        
        // 设置RecyclerView适配器
        CurrencyAdapter adapter = new CurrencyAdapter(item -> {
            // 处理货币选择
            Intent resultIntent = new Intent();
            resultIntent.putExtra("currency_code", item.getCode());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
        binding.currencyRecyclerView.setAdapter(adapter);
        // 观察 ViewModel 中的货币列表数据变化
        viewModel.currencyList.observe(this, currencyItems -> {
            adapter.setItems(currencyItems);
        });
    }
}