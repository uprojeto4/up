package br.ufc.quixada.up.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Macelo on 17/11/2017.
 */

public abstract class DateTimeControl {

    public static long getCurrentDateTime() {
        return System.currentTimeMillis();
    }

    public static String formatMillisToDate(long millis) {
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(millis);
//        return formatter.format(calendar.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(new Date(millis));
    }
}
