package com.eipna.easybank.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static String getDate(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd yyyy 'at' hh:mm a", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }
}