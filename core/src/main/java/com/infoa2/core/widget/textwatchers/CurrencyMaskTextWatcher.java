package com.infoa2.core.widget.textwatchers;

import android.text.Editable;
import android.text.TextWatcher;
import com.infoa2.core.widget.EditText;
import com.infoa2.core.widget.interfaces.ValidatorInputType;

import java.text.NumberFormat;

/**
 * Created by caio on 23/02/17.
 */

public class CurrencyMaskTextWatcher implements TextWatcher {
    private String current = "";
    private int index;
    private boolean deletingDecimalPoint;
    private int currencyDecimalPlaces;
    private final EditText editText;
    private OnTextChanged onTextChanged;
    private ValidatorInputType validatorInputType;
    private String v_formattedValue;
    private NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

    public interface OnTextChanged {
        void onTextChanged(String text);
    }

    public CurrencyMaskTextWatcher(EditText p_currency) {
        editText = p_currency;
    }

    public CurrencyMaskTextWatcher(EditText p_currency, ValidatorInputType validatorInputType) {
        editText = p_currency;
        this.validatorInputType = validatorInputType;

    }

    public CurrencyMaskTextWatcher(EditText p_currency, OnTextChanged onTextChanged) {
        editText = p_currency;
        this.onTextChanged = onTextChanged;

    }

    public static CurrencyMaskTextWatcher insert(EditText editText){
        return new CurrencyMaskTextWatcher(editText);

    }
    public static TextWatcher insert(EditText editText, ValidatorInputType validatorInputType) {
        return new CurrencyMaskTextWatcher(editText, validatorInputType);
    }


    @Override
    public void onTextChanged(
            CharSequence p_s, int p_start, int p_before, int p_count) {
        // nothing to do
    }
    @Override
    public void beforeTextChanged(
            CharSequence p_s, int p_start, int p_count, int p_after
    ) {
        if (p_after>0) {
            index = p_s.length() - p_start;
        } else {
            index = p_s.length() - p_start - 1;
        }

        if (p_count>0 && p_s.charAt(p_start)=='.') {
            deletingDecimalPoint = true;
        } else {
            deletingDecimalPoint = false;
        }
    }
    @Override
    public synchronized void afterTextChanged(Editable p_s) {
        if(!p_s.toString().equals(current)){
            editText.removeTextChangedListener(this);
            if (deletingDecimalPoint) {
                p_s.delete(p_s.length()-index-1, p_s.length()-index);
            }
            // Currency char may be retrieved from NumberFormat.getCurrencyInstance()
            String v_text = p_s.toString().replaceAll("[^0-9]*", "");
            double v_value = 0;
            if (v_text!=null && v_text.length()>0) {
                v_value = Double.parseDouble(v_text);
            }
            // Currency instance may be retrieved from a static member.
            v_formattedValue = numberFormat.format((v_value/100));
            current = v_formattedValue;
            editText.setText(v_formattedValue);
            if (index>v_formattedValue.length()) {
                editText.setSelection(v_formattedValue.length());
            } else {
                editText.setSelection(v_formattedValue.length()-index);
            }
            // inlude here anything you may want to do after the formatting is completed.
            editText.addTextChangedListener(this);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(onTextChanged != null) {
                        onTextChanged.onTextChanged(v_formattedValue);
                    }

                    if(validatorInputType != null){
                        validatorInputType.isValid();
                    }

                }
            });
        }
    }

    public static Float toFloat(EditText currency){
        return toFloat(currency.getText().toString());

    }

    public static Float toFloat(String currency){
        String value = currency.replaceAll("[R$,.]", "");
        if(value.isEmpty()){
            return new Float(0);

        }
        return Float.valueOf(value) / 100;

    }

    public static Double toDouble(EditText currency){
        return toDouble(currency.getText().toString());

    }

    public static Double toDouble(String currency){
        String value = currency.replaceAll("[R$,.]", "");
        if(value.isEmpty()){
            return new Double(0);

        }
        return Double.parseDouble(value) / 100;

    }
}