package com.dspread.pos.ui.setting.currency;

import android.app.Application;
import android.content.res.Resources;
import android.icu.util.Currency;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;


import com.dspread.pos.common.enums.PaymentType;
import com.dspread.pos.common.enums.TransCardMode;
import com.dspread.pos_new_android_app.R;
import com.mynameismidori.currencypicker.ExtendedCurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class CurrencySelectionViewModel extends BaseViewModel {
    public ObservableField<String> searchText = new ObservableField<>("");
    public MutableLiveData<List<CurrencyItem>> currencyList = new MutableLiveData<>();
    private List<CurrencyItem> allCurrencies = new ArrayList<>();

    public void init(int type) {
        allCurrencies.clear(); // 清除旧数据
        switch (type) {
            case CurrencySelectionActivity.TYPE_CURRENCY:
                initCurrencyList();
                break;
            case CurrencySelectionActivity.TYPE_TRANSACTION:
                initTransactionList();
                break;
            case CurrencySelectionActivity.TYPE_CARD_MODE:
                initCardModeList();
                break;
        }
    }
    
    public CurrencySelectionViewModel(@NonNull Application application) {
        super(application);
    }

    private void initTransactionList() {
        String[] types = PaymentType.getValues();
        for (int i = 0 ; i < types.length; i++) {
            String type = types[i];
            allCurrencies.add(new CurrencyItem(
                    type,
                    type,
                    R.drawable.ic_transaction_type,
                    0
            ));
        }
        currencyList.setValue(allCurrencies);
    }

    private void initCurrencyList() {
        List<ExtendedCurrency> currencies = ExtendedCurrency.getAllCurrencies();
        for (ExtendedCurrency currency : currencies) {
            try {
                Currency currencyInstance = Currency.getInstance(currency.getCode());
                int numericCode = currencyInstance.getNumericCode();
                allCurrencies.add(new CurrencyItem(
                        currency.getCode(),
                        currency.getName(),
                        currency.getFlag(),  // 这里直接使用库提供的国旗资源ID
                        numericCode
                ));
            }catch (Exception e){
                continue;
            }
        }
        currencyList.setValue(allCurrencies);
    }

    private void initCardModeList() {
        String[] modes = TransCardMode.getCardTradeModes();
        for (int i = 0 ; i < modes.length; i++) {
            String mode = modes[i];
            allCurrencies.add(new CurrencyItem(
                    "",
                    mode,
                    R.drawable.ic_transaction_type,
                    0
            ));
        }
        currencyList.setValue(allCurrencies);
    }

    public void filterCurrencies(String query) {
        if (query.isEmpty()) {
            currencyList.setValue(allCurrencies);
        } else {
            List<CurrencyItem> filteredList = new ArrayList<>();
            for (CurrencyItem currency : allCurrencies) {
                if (currency.getName().toLowerCase().contains(query.toLowerCase()) ||
                        currency.getCode().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(currency);
                }
            }
            currencyList.setValue(filteredList);
        }
    }
}