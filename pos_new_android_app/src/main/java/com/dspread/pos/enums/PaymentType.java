package com.dspread.pos.enums;

public enum PaymentType {
    GOODS("GOODS"),
    SERVICES("SERVICES"),
    CASH("CASH"),
    CASHBACK("CASHBACK"),
    PURCHASE_REFUND("PURCHASE_REFUND"),
    INQUIRY("INQUIRY"),
    TRANSFER("TRANSFER"),
    ADMIN("ADMIN"),
    CASHDEPOSIT("CASHDEPOSIT"),
    PAYMENT("PAYMENT"),
    PBOCLOG("PBOCLOG||ECQ_INQUIRE_LOG"),
    SALE("SALE"),
    PREAUTH("PREAUTH"),
    ECQ_DESIGNATED_LOAD("ECQ_DESIGNATED_LOAD"),
    ECQ_UNDESIGNATED_LOAD("ECQ_UNDESIGNATED_LOAD"),
    ECQ_CASH_LOAD("ECQ_CASH_LOAD"),
    ECQ_CASH_LOAD_VOID("ECQ_CASH_LOAD_VOID"),
    CHANGE_PIN("CHANGE_PIN"),
    REFOUND("REFOUND"),
    SALES_NEW("SALES_NEW");

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String[] getValues() {
        PaymentType[] types = PaymentType.values();
        String[] values = new String[types.length];
        for (int i = 0; i < types.length; i++) {
            values[i] = types[i].getValue();
        }
        return values;
    }
}