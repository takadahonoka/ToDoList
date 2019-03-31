package local.hal.st32.android.todo60143;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarListActivity extends AppCompatActivity {

    static final int MODE_INSERT = 1;
    static final int MODE_EDIT = 2;
    private ListView _lvTasksList;
    private int page = 0;
    private String strWhere = "";
    private CalendarAdapter mCalendarAdapter;
    private String strNowYear = "";
    private String strNowMonth = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_list);

        // アクションバーに前画面に戻る機能をつける
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //GridViewについて。
        GridView gridview = findViewById(R.id.calendarGridView);
        gridview.setOnItemClickListener(new GridOnItemClick());
        try {mCalendarAdapter= new CalendarAdapter(this,strNowYear,strNowMonth) {};} catch (ParseException e) {e.printStackTrace();}
        gridview.setAdapter(mCalendarAdapter);

        //ListViewについて。
        strWhere = "";
        _lvTasksList = findViewById(R.id.lvTasksList);
        _lvTasksList.setOnItemClickListener(new ListItemClickListener());
        registerForContextMenu(_lvTasksList);
        String[] from = {"name","deadline2","done"};
        int[] to = {R.id.tvRowTitle,R.id.tvRowDate,R.id.cbFinishCheck};
        SimpleCursorAdapter adapter2 = new SimpleCursorAdapter(CalendarListActivity.this,R.layout.row,null,from,to,0);
        adapter2.setViewBinder(new CustomViewBinder());
        _lvTasksList.setAdapter(adapter2);

        //TextViewについて。
        TextView _tvClickDate = findViewById(R.id.tvClickDate);
        _tvClickDate.setText("全件");
        TextView _tvCalTitle = findViewById(R.id.tvCalTitle);
        _tvCalTitle.setText(String.valueOf(mCalendarAdapter.getTitle()));

    }

    @Override
    public void onResume(){
        super.onResume();
        setNewCursor();
    }

    /**
     * 来月ボタンが押された時のイベント処理用メソッド。
     * @param view 画面部品
     * */
    public void onNextMonthButtonClick(View view){
        mCalendarAdapter.nextMonth();
        TextView _tvCalTitle = findViewById(R.id.tvCalTitle);
        _tvCalTitle.setText(mCalendarAdapter.getTitle());
    }

    /**
     * 先月ボタンが押された時のイベント処理用メソッド。
     * @param view 画面部品
     * */
    public void onBackMonthButtonClick(View view){
        mCalendarAdapter.prevMonth();
        TextView _tvCalTitle = findViewById(R.id.tvCalTitle);
        _tvCalTitle.setText(mCalendarAdapter.getTitle());
    }

    /**
     * 新規ボタンが押された時のイベント処理用メソッド。
     * @param view 画面部品
     * */
    public void onNewButtonClick(View view) throws ParseException {
        Intent intent = new Intent(CalendarListActivity.this , ToDoEditActivity.class);
        intent.putExtra("mode" , MODE_INSERT);
        int intNo = 0;
        intent.putExtra("DATA1" , intNo);
        TextView txClickDate = findViewById(R.id.tvClickDate);
        String strClickDate = txClickDate.getText().toString();
        String strOutput = "";
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy年MM月dd日");
//        SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd");
        if("全件".equals(strClickDate)){
            Date sourceDate = new Date();
            strOutput = sourceFormat.format(sourceDate);
        }else {
            Date sourceDate = null;
            sourceDate = sourceFormat.parse(strClickDate);
            strOutput = sourceFormat.format(sourceDate);
        }
        intent.putExtra("DATA2" , strOutput);
        startActivity(intent);
    }

    /**
     * グリッドビューがクリックされた時のリスナクラス。
     * */
    private class GridOnItemClick  implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent , View view , int position , long id){

            int intI = (int)parent.getItemIdAtPosition(position);//タップされた行番号。(0始まり)

            //日にちを取得。
            List<Date> listAllDate = mCalendarAdapter.getDateArray();
            SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat formatDate2 = new SimpleDateFormat("yyyy年MM月dd日", Locale.US);
            String strDate = formatDate.format(listAllDate.get(intI));
            String strDate2 = formatDate2.format(listAllDate.get(intI));

            strWhere = " WHERE deadline = '"+strDate+"' ";

            SimpleDateFormat sdYear = new SimpleDateFormat("yyyy", Locale.US);
            SimpleDateFormat sdMonth = new SimpleDateFormat("MM", Locale.US);
            strNowYear = sdYear.format(listAllDate.get(intI));
            strNowMonth = sdMonth.format(listAllDate.get(intI));

            setNewCursor();

            //タイトル。
            TextView _tvClickDate = findViewById(R.id.tvClickDate);
            _tvClickDate.setText(strDate2);
            TextView _tvCalTitle = findViewById(R.id.tvCalTitle);
            _tvCalTitle.setText(String.valueOf(mCalendarAdapter.getTitle()));
        }
    }

    /**
     * アクションバーの戻るボタン用。
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * カーソルアダプタ内のカーソルを更新するメソッド。
     */
    private void setNewCursor(){
        //ListVIewについて。
        DatabaseHelper helper = new DatabaseHelper(CalendarListActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = DataAccess.findAll(db,page,strWhere);
        SimpleCursorAdapter adapter = (SimpleCursorAdapter)_lvTasksList.getAdapter();
        adapter.changeCursor(cursor);
        //GridViewについて。
        GridView gridview = findViewById(R.id.calendarGridView);
        gridview.setOnItemClickListener(new GridOnItemClick());
        try {mCalendarAdapter= new CalendarAdapter(this,strNowYear,strNowMonth) {};} catch (ParseException e) {e.printStackTrace();}
        gridview.setAdapter(mCalendarAdapter);
    }

    /**
     * リストビューのカスタムビューバインダークラス。
     */
    private class CustomViewBinder implements SimpleCursorAdapter.ViewBinder{

        private int intTextColor = Color.BLACK;

        @Override
        public boolean setViewValue(View view,Cursor cursor,int columnIndex) {

            int viewId = view.getId();

            switch (viewId) {
                case R.id.cbFinishCheck://リストのチェックボタン。
                    int idIdx = cursor.getColumnIndex("_id");
                    long id = cursor.getLong(idIdx);

                    CheckBox cbFinishCheck = (CheckBox) view;
                    int finishCheck = cursor.getInt(columnIndex);
                    boolean checked = false;
                    LinearLayout row = (LinearLayout) cbFinishCheck.getParent();

                    int rColor = android.support.v7.appcompat.R.drawable.abc_list_selector_holo_light;//abc_list_selector_background_transition_holo_light
                    if (finishCheck == 1) {
                        checked = true;
                        rColor = android.support.v7.appcompat.R.drawable.abc_list_selector_disabled_holo_dark;//abc_list_selector_background_transition_holo_dark
                    }
                    row.setBackgroundResource(rColor);

                    cbFinishCheck.setChecked(checked);
                    cbFinishCheck.setTag(id);
                    cbFinishCheck.setOnClickListener(new CalendarListActivity.OnCheckBoxClickListener());
                    return true;
                case R.id.tvRowTitle:
                    TextView tvRowTitle = (TextView) view;
                    String roeTitle = cursor.getString(columnIndex);
                    tvRowTitle.setTextColor(intTextColor);
                    tvRowTitle.setText(roeTitle);
                    return true;
                case R.id.tvRowDate:
                    TextView tvRowDate = (TextView) view;
                    String rowDate = cursor.getString(columnIndex);

                    Calendar cal = Calendar.getInstance();
                    int intYear = cal.get(Calendar.YEAR);
                    int intMonth = cal.get(Calendar.MONTH)+1;
                    int intDate = cal.get(Calendar.DATE);

                    int intTextColor = Color.BLACK;
                    String strNowDate = String.valueOf(intYear)+"年";
                    if(intMonth<10){
                        strNowDate += "0"+String.valueOf(intMonth)+"月";
                    }else{
                        strNowDate += String.valueOf(intMonth)+"月";
                    }
                    if(intDate<10){
                        strNowDate += "0"+String.valueOf(intDate)+"日";
                    }else{
                        strNowDate += String.valueOf(intDate)+"日";
                    }

                    if (rowDate.equals(strNowDate)){
                        rowDate = "今日";
                        intTextColor = Color.RED;
                    }
//                    Log.e("確認1",strNowDate);
//                    Log.e("確認3",rowDate);
                    tvRowDate.setText(rowDate);
                    tvRowDate.setTextColor(intTextColor);
                    return true;
                case R.id.dateText:
                    TextView _dateText = (TextView) view;
                    _dateText.setTextColor(Color.RED);
                    return true;
            }
            return false;
        }
    }

    /**
     * チェックボックスのチェック状態が変更された時のリスナクラス。
     */
    private class OnCheckBoxClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view){
            CheckBox cbFinishCheck = (CheckBox) view;
            boolean isChecked = cbFinishCheck.isChecked();
            long id = (Long) cbFinishCheck.getTag();
            DatabaseHelper helper = new DatabaseHelper(CalendarListActivity.this);
            SQLiteDatabase db = helper.getWritableDatabase();
            try{
                DataAccess.changeFinishChecked(db,id,isChecked);
            }catch (Exception ex){
                Log.e("ERROR",ex.toString());
            }finally {
                db.close();
            }
            setNewCursor();
        }
    }

    /**
     * リストがクリックされた時のリスナクラス。
     * */
    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent , View view , int position , long id){
            Cursor item = (Cursor)parent.getItemAtPosition(position);
            int idxId = item.getColumnIndex("_id");
            int idNo = item.getInt(idxId);

            Intent intent = new Intent(CalendarListActivity.this , ToDoEditActivity.class);
            intent.putExtra("mode" , MODE_EDIT);
            intent.putExtra("idNo" , idNo);
            //intent.putExtra("idNo" , (int)id);
            startActivity(intent);
        }
    }

}
