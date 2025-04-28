package com.dspread.pos.ui.setting.currency;

import android.app.Application;
import android.content.res.Resources;
import android.icu.util.Currency;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableList;
import androidx.lifecycle.MutableLiveData;


import com.mynameismidori.currencypicker.ExtendedCurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.goldze.mvvmhabit.base.BaseViewModel;

public class CurrencySelectionViewModel extends BaseViewModel {
    public ObservableField<String> searchText = new ObservableField<>("");
    public MutableLiveData<List<CurrencyItem>> currencyList = new MutableLiveData<>();
    private List<CurrencyItem> allCurrencies = new ArrayList<>();
    
    public CurrencySelectionViewModel(@NonNull Application application) {
        super(application);
        initCurrencyList();
    }

    private void initCurrencyList() {
        List<ExtendedCurrency> currencies = ExtendedCurrency.getAllCurrencies();
        for (ExtendedCurrency currency : currencies) {
            allCurrencies.add(new CurrencyItem(
                    currency.getCode(),
                    currency.getName(),
                    currency.getFlag()  // 这里直接使用库提供的国旗资源ID
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