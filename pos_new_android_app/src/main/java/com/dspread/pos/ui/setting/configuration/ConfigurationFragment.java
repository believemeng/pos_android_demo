package com.dspread.pos.ui.setting.configuration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.dspread.pos.common.base.BaseFragment;
import com.dspread.pos.ui.base.TitleProvider;
import com.dspread.pos_new_android_app.BR;
import com.dspread.pos_new_android_app.R;
import com.dspread.pos_new_android_app.databinding.FragmentConfigurationBinding;

import me.goldze.mvvmhabit.utils.SPUtils;

public class ConfigurationFragment extends BaseFragment<FragmentConfigurationBinding, ConfigurationViewModel> implements TitleProvider {

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_configuration;
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        super.initData();

        // 添加动画效果
        applyAnimations();

        // 初始化交易类型下拉框
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item, viewModel.transTypeItems);
        typeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        binding.spinnerTransType.setAdapter(typeAdapter);

        // 初始化交易模式下拉框
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(getContext(),
                R.layout.spinner_item, viewModel.cardTradeModeItems);
        modeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        binding.spinnerTransMode.setAdapter(modeAdapter);

        // 设置选择监听器
        binding.spinnerTransType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.onTransTypeSelected.execute(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.spinnerTransMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.onCardTradeModeSelected.execute(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    /**
     * 应用动画效果
     */
    private void applyAnimations() {
        // 加载动画
        android.view.animation.Animation animation = android.view.animation.AnimationUtils.loadAnimation(
                getContext(), R.anim.item_animation_from_bottom);

        // 应用动画到各个视图
        binding.spinnerTransType.startAnimation(animation);
        binding.spinnerTransMode.startAnimation(animation);
    }

    @Override
    public void initViewObservable() {
        super.initViewObservable();
        viewModel.selectedTradeMode.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer i) {
                binding.spinnerTransMode.setSelection(i);
            }
        });
    }

    @Override
    public String getTitle() {
        return "Configuration";
    }
}