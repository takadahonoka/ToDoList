package local.hal.st32.android.todo60143;

import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ToDoListActivity extends AppCompatActivity {

    static final int MODE_INSERT = 1;
    static final int MODE_EDIT = 2;
    private ListView _lvTasksList;
    private int page = 0;
    private String strPage = "PAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        _lvTasksList = findViewById(R.id.lvTasksList);
        _lvTasksList.setOnItemClickListener(new ListItemClickListener());

        registerForContextMenu(_lvTasksList);

        String[] from = {"name","deadline2","done"};
        int[] to = {R.id.tvRowTitle,R.id.tvRowDate,R.id.cbFinishCheck};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(ToDoListActivity.this,R.layout.row,null,from,to,0);
        adapter.setViewBinder(new CustomViewBinder());
        _lvTasksList.setAdapter(adapter);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu , View view , ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu , view , menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu , menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        long id = info.id;
        int position = info.position;
        int itemId = item.getItemId();
        DatabaseHelper helper = new DatabaseHelper(ToDoListActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        switch (itemId){
            case R.id.menuContextFinish:
                DataAccess.update2(db , (int)id , "1");
                break;
            case R.id.menuContextIncomplete:
                DataAccess.update2(db , (int)id , "0");
                break;
            case R.id.menuContextOther:
                Intent intent = new Intent(ToDoListActivity.this , ToDoEditActivity.class);
                intent.putExtra("mode" , MODE_EDIT);
                intent.putExtra("idNo" , (int)id);
                startActivity(intent);
                break;
        }
        setNewCursor();
        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        DatabaseHelper helper = new DatabaseHelper(ToDoListActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();

        SharedPreferences setting = getSharedPreferences(strPage , 0);
        SharedPreferences.Editor editor = setting.edit();
        page = setting.getInt(strPage , 0);
        editor.putInt(strPage , page);
        editor.commit();

        TextView tvTitleList = findViewById(R.id.tvTitleList);
        switch (page){
            case 2:/*完了済み表示*/
                tvTitleList.setText("完了済みリスト");
                break;
            case 3:/*未完了表示*/
                tvTitleList.setText("未完了リスト");
                break;
            default:
                tvTitleList.setText("ToDo全リスト");
                break;
        }

        setNewCursor();
    }

    /**
     * 新規ボタンが押された時のイベント処理用メソッド。
     * @param view 画面部品
     * */
    public void onNewButtonClick(View view){
        Intent intent = new Intent(ToDoListActivity.this , ToDoEditActivity.class);
        intent.putExtra("mode" , MODE_INSERT);
        int intNo = 0;
        intent.putExtra("DATA1" , intNo);
        String strClickDate = "";
        Calendar cal = Calendar.getInstance();
        int intYear = cal.get(Calendar.YEAR);
        int intMonth = cal.get(Calendar.MONTH)+1;
        String strMonth = String.valueOf(intMonth);
        if(intMonth<10){strMonth = "0"+String.valueOf(intMonth);}
        int intDate = cal.get(Calendar.DATE);
        String strDate = String.valueOf(intDate);
        if(intDate<10){strDate = "0"+String.valueOf(intDate);}
        intent.putExtra("DATA2" , String.valueOf(intYear)+"年"+strMonth+"月"+strDate+"日");
        startActivity(intent);
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

            Intent intent = new Intent(ToDoListActivity.this , ToDoEditActivity.class);
            intent.putExtra("mode" , MODE_EDIT);
            intent.putExtra("idNo" , idNo);
            //intent.putExtra("idNo" , (int)id);
            startActivity(intent);
        }
    }

    /**
     * オプションメニュー
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.to_do_list , menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){

        TextView tvTitleList = findViewById(R.id.tvTitleList);

        int itemId = item.getItemId();

        switch (itemId){
            case R.id.menuDisplayAll:/*全て表示*/
                page = 1;
                tvTitleList.setText("ToDo全リスト");
                break;
            case R.id.menuDisplayFinish:/*完了済みのみ表示*/
                page = 2;
                tvTitleList.setText("完了済みリスト");
                break;
            case R.id.menuDisplayIncomplete:/*未完のみ表示*/
                page = 3;
                tvTitleList.setText("未完了リスト");
                break;
        }

        /**
         * データ保存。
         */
        SharedPreferences setting = getSharedPreferences(strPage , 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putInt(strPage , page);
        editor.commit();

        Intent intent = new Intent();
        intent.putExtra("PAGE" , page);

        setNewCursor();
        return super.onOptionsItemSelected(item);
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
                    cbFinishCheck.setOnClickListener(new OnCheckBoxClickListener());
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
                    Log.e("確認2",rowDate);

                    Calendar cal = Calendar.getInstance();
                    int intYear = cal.get(Calendar.YEAR);
                    int intMonth = cal.get(Calendar.MONTH)+1;
                    int intDate = cal.get(Calendar.DATE);


                    intTextColor = Color.BLACK;
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

                    Log.e("確認1",strNowDate);
                    Log.e("確認2",rowDate);

                    if (rowDate.equals(strNowDate)){
                        rowDate = "今日";
                        intTextColor = Color.RED;
                    }
                    Log.e("確認1",strNowDate);
//                    Log.e("確認3",rowDate);
                    tvRowDate.setTextColor(intTextColor);
                    tvRowDate.setText(rowDate);
                    intTextColor = Color.BLACK;
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
            DatabaseHelper helper = new DatabaseHelper(ToDoListActivity.this);
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
     * カーソルアダプタ内のカーソルを更新するメソッド。
     */
    private void setNewCursor(){
        DatabaseHelper helper = new DatabaseHelper(ToDoListActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = DataAccess.findAll(db,page,"");
        SimpleCursorAdapter adapter = (SimpleCursorAdapter)_lvTasksList.getAdapter();
        adapter.changeCursor(cursor);
    }

    /**
     *カレンダー表示ボタンが押されたとき。
     */
    public void onNextCalendarButtonClick(View view){
        Intent intent = new Intent(ToDoListActivity.this , CalendarListActivity.class);
//        intent.putExtra("mode" , MODE_INSERT);
        startActivity(intent);
    }

}
