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

    public static String generateChatTimestamp(long millis) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(millis);;
        int mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        int mMonth = mCalendar.get(Calendar.MONTH) + 1;
        int mYear = mCalendar.get(Calendar.YEAR);
        int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = mCalendar.get(Calendar.MINUTE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        //hoje
        if (mDay == day && mMonth == month && mYear == year) {
            return addZeros(mHour) + ":" + addZeros(mMinute);
        //ontem
        } else if (mDay + 1 == day && mMonth == month && mYear == year) {
            return "Ontem, " + addZeros(mHour) + ":" + addZeros(mMinute);
        //outro dia antes de ontem ainda desse mesmo ano
        } else if (mDay != day && mMonth == month && mYear == year){
            return addZeros(mDay) + "/" + addZeros(mMonth);
        //outro dia de outro ano
        } else {
            return addZeros(mDay) + "/" + addZeros(mMonth) + "/" + addZeros(mYear);
        }
    }

    public static String addZeros(int value) {
        if (value < 10) {
            return "0" + Integer.toString(value);
        } else {
            return Integer.toString(value);
        }
    }
}
