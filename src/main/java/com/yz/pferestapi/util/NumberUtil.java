package com.yz.pferestapi.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberUtil {
    // format Double to String
    public static String format(Double v) {
        // Create a DecimalFormat instance with Locale.US to ensure dot as decimal separator
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);

        // In Java, when you convert a double to a String, the .toString() method provided by the
        // Double class will append .0 to the end if the number doesn't already have a decimal point.
        // To remove this behavior, you can format the double using DecimalFormat
        DecimalFormat df = new DecimalFormat("#.###", symbols);

        return df.format(v);
    }
}
