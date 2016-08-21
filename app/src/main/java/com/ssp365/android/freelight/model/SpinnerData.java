package com.ssp365.android.freelight.model;

public class SpinnerData {

    //Spinner的值
    private String value = "";
    //Spinner的显示内容
    private String text = "";

    public SpinnerData() {
        value = "";
        text = "";
    }

    public SpinnerData(String _value, String _text) {
        value = _value;
        text = _text;
    }

    @Override
    public String toString() {

        return text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }

    public static int getSpinnerPosition(String key, String[] keys) {
        int reInt = -1;
        for (int i = 0; i < keys.length; i++) {
            if (key.equals(keys[i])) {
                return i;
            }
        }
        return reInt;
    }

}
