package com.dspread.pos.ui.setting.currency;

public class CurrencyItem {
    private String code;
    private String name;
    private int flagResId;
    private boolean selected;

    public CurrencyItem(String code, String name, int flagResId) {
        this.code = code;
        this.name = name;
        this.flagResId = flagResId;
    }

    // Getter and Setter methods
    public String getCode() { return code; }
    public String getName() { return name; }
    public int getFlagResId() { return flagResId; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}