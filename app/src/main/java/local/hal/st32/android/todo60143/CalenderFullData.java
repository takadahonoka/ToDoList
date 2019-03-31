package local.hal.st32.android.todo60143;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalenderFullData {

    Calendar mCalendar;

    //コンストラクタ。
    public CalenderFullData(){
        mCalendar = Calendar.getInstance();
    }
    public CalenderFullData(String strDate) throws ParseException {
        mCalendar = Calendar.getInstance();
        SimpleDateFormat sdCalendarFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dtYear = sdCalendarFormat.parse(strDate);
        String strYear = new SimpleDateFormat("yyyy").format(dtYear);
        String strMonth = new SimpleDateFormat("MM").format(dtYear);
        mCalendar.set(Integer.parseInt(strYear), (Integer.parseInt(strMonth)-1), 01);
    }

    //現在の月を取得する。
    public List<Date> getDays(){

        Date startDate = mCalendar.getTime();

        int count = getWeeks() * 7 ;
        mCalendar.set(Calendar.DATE, 1);
        int dayOfWeek = mCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        mCalendar.add(Calendar.DATE, -dayOfWeek);

        List<Date> days = new ArrayList<>();

        for (int i = 0; i < count; i ++){
            days.add(mCalendar.getTime());
            mCalendar.add(Calendar.DATE, 1);
        }
        mCalendar.setTime(startDate);

        return days;
    }

    //現在の月かどうか確認
    public boolean isCurrentMonth(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM", Locale.US);
        String currentMonth = format.format(mCalendar.getTime());
        if (currentMonth.equals(format.format(date))){
            return true;
        }else {
            return false;
        }
    }

    //週数を取得する。
    public int getWeeks(){
        return mCalendar.getActualMaximum(Calendar.WEEK_OF_MONTH);
    }

    //曜日を取得する。
    public int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    //次の月へ。
    public void nextMonth(){
        mCalendar.add(Calendar.MONTH, 1);
    }

    //前の月へ。
    public void prevMonth(){
        mCalendar.add(Calendar.MONTH, -1);
    }
}
