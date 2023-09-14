package com.system.finalcapstoneproject.reportingsystem;

import android.text.InputFilter;
import android.text.Spanned;

public class MaxLengthFilter extends InputFilter.LengthFilter {
    public MaxLengthFilter(int max) {
        super(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        // This prevents the user from inputting leading zeros
        if (dest.toString().equals("0") && source.toString().equals("0")) {
            return "";
        }
        return super.filter(source, start, end, dest, dstart, dend);
    }
}

