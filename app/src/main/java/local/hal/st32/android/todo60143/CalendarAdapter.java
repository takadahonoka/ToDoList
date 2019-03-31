package local.hal.st32.android.todo60143;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class CalendarAdapter extends BaseAdapter {

    private List<Date> dateArray = new ArrayList();//日にちの一覧。
    private Context mContext;
    private CalenderFullData mFullDate;//日にちを取得するクラス。
    private LayoutInflater mLayoutInflater;

    public CalendarAdapter(Context context,String strYear,String strMonth) throws ParseException {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        if("".equals(strYear)){
            mFullDate = new CalenderFullData();
        }else {
            mFullDate = new CalenderFullData((strYear+"-"+strMonth+"-01"));
        }
        dateArray = mFullDate.getDays();
    }

    //日付の一覧取得。
    public List<Date> getDateArray(){
        return dateArray;
    }

    //日にちを表示するところ。
    private static class ViewHolder {
        public TextView dateText;
    }

    /**
     * カレンダー(GridView)のセル毎の、カスタム用クラス。
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.calendar_cell, null);
            holder = new ViewHolder();
            holder.dateText = convertView.findViewById(R.id.dateText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //日付を表示。
        SimpleDateFormat dateFormat = new SimpleDateFormat("d", Locale.US);
        holder.dateText.setText(dateFormat.format(dateArray.get(position)));

        //日曜日を赤、土曜日を青にする。
        int colorId;
        switch (mFullDate.getDayOfWeek(dateArray.get(position))){
            case 1:
                colorId = Color.RED;
                break;
            case 7:
                colorId = Color.BLUE;
                break;

            default:
                colorId = Color.BLACK;
                break;
        }
        holder.dateText.setTextColor(colorId);

        //SQLのWHERE用。
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String strDeadLine = formatDate.format(dateArray.get(position));
        //SQLiteDatabase(その日に、予定があるかどうかの検索)。
        DatabaseHelper helper = new DatabaseHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = DataAccess.findAll(db,0," WHERE deadline = '"+ strDeadLine +"' ");
        Boolean flag = false;
        if(cursor.moveToNext()){
            flag = true;
        }

        //日と予定有無で背景色を変える。
        if(mFullDate.isCurrentMonth(dateArray.get(position)) && flag){//現在の月で、予定があった時。
            convertView.setBackgroundColor(Color.parseColor("#FF8C00"));
        }else if (mFullDate.isCurrentMonth(dateArray.get(position)) && !flag) {//現在の月で、予定がない時。
            convertView.setBackgroundColor(Color.WHITE);
        }else if (!mFullDate.isCurrentMonth(dateArray.get(position)) && flag) {//現在の月ではないけど、予定があった時。
            convertView.setBackgroundColor(Color.parseColor("#DEB887"));
        } else {//現在の月でもなく、予定もない。
            convertView.setBackgroundColor(Color.LTGRAY);
        }
        return convertView;
    }

    //表示する月を取得。
    public String getTitle(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月", Locale.US);
        return format.format(mFullDate.mCalendar.getTime());
    }

    //翌月。
    public void nextMonth(){
        mFullDate.nextMonth();
        dateArray = mFullDate.getDays();
        this.notifyDataSetChanged();
    }

    //前月。
    public void prevMonth(){
        mFullDate.prevMonth();
        dateArray = mFullDate.getDays();
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return dateArray.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

}
