package local.hal.st32.android.todo60143;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ToDoEditActivity extends AppCompatActivity {

    private int _mode = ToDoListActivity.MODE_INSERT;
    private int _idNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_edit);

        // アクションバーに前画面に戻る機能をつける
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        _mode = intent.getIntExtra("mode", ToDoListActivity.MODE_INSERT);

        if (_mode == ToDoListActivity.MODE_INSERT) {
            TextView tvTitleEdit = findViewById(R.id.tvTitleEdit);
            tvTitleEdit.setText(R.string.tv_title_insert);

            String strClickDate = intent.getStringExtra("DATA2");
            TextView etInputDeadLine = findViewById(R.id.etInputDeadLine);
            etInputDeadLine.setText(strClickDate);

        } else {
            _idNo = intent.getIntExtra("idNo", 0);
            DatabaseHelper helper = new DatabaseHelper(ToDoEditActivity.this);
            SQLiteDatabase db = helper.getWritableDatabase();
            try {
                Tasks memoData = DataAccess.findByPK(db, _idNo);

                EditText etInputTitle = findViewById(R.id.etInputName);
                etInputTitle.setText(memoData.getName());

                TextView etInputDeadLine = findViewById(R.id.etInputDeadLine);
                String strDeadLine = memoData.getDeadLine();
                SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy年MM月dd日");
                SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date sourceDate = null;
                try {sourceDate = convertFormat.parse(strDeadLine);} catch (ParseException e) {e.printStackTrace();}
                strDeadLine = sourceFormat.format(sourceDate);
                etInputDeadLine.setText(strDeadLine);

                EditText etInputContent = findViewById(R.id.etInputContent);
                etInputContent.setText(memoData.getNote());

                Switch swInputDone = findViewById(R.id.etInputDone);
                Boolean flag = false;
                if(Integer.parseInt(memoData.getDone()) == 1){
                    flag = true;
                }
                swInputDone.setChecked(flag);

            } catch (Exception ex) {
                Log.e("ERROR", ex.toString());
            } finally {
                db.close();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        if (_mode == ToDoListActivity.MODE_INSERT) {
            inflater.inflate(R.menu.to_do_edit2 , menu);
        }else {
            inflater.inflate(R.menu.to_do_edit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int itemid = item.getItemId();
        switch (itemid){
            case android.R.id.home:
                finish();
                return true;
            case R.id.btnDelete:
                Bundle extras = new Bundle();
                extras.putString("id" , String.valueOf(_idNo));
                FullDialogFragment dialog = new FullDialogFragment();
                FragmentManager manager = getSupportFragmentManager();
                dialog.show(manager,"FullDialogFragment");
                dialog.setArguments(extras);
                break;
            case R.id.btnSave:
                EditText etInputTitle = findViewById(R.id.etInputName);
                String inputTitle = etInputTitle.getText().toString();
                if(inputTitle.equals("")){
                    Toast.makeText(ToDoEditActivity.this , R.string.msg_input_title , Toast.LENGTH_SHORT).show();
                }
                else{
                    EditText etInputName = findViewById(R.id.etInputName);
                    String inputName = etInputName.getText().toString();
                    /**
                     * switchボタン
                     */
                    Switch swInputDone = findViewById(R.id.etInputDone);
                    String inputDone = "";
                    if(swInputDone.isChecked()){
                        //ON
                        inputDone = "1";
                    }else{
                        //OFF
                        inputDone = "0";
                    }

                    TextView etInputDeadLine = findViewById(R.id.etInputDeadLine);
                    String inputDeadLine = etInputDeadLine.getText().toString();
                    SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy年MM月dd日");
                    SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date sourceDate = null;
                    try {sourceDate = sourceFormat.parse(inputDeadLine);} catch (ParseException e) {e.printStackTrace();}
                    inputDeadLine = convertFormat.format(sourceDate);
                    EditText etInputContent = findViewById(R.id.etInputContent);
                    String inputContent = etInputContent.getText().toString();
                    DatabaseHelper helper = new DatabaseHelper(ToDoEditActivity.this);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    String msg = "";
                    try{
                        if(_mode == ToDoListActivity.MODE_INSERT){
                            DataAccess.insert(db , inputName , inputDeadLine , inputDone , inputContent);
                        }
                        else{
                            DataAccess.update(db , _idNo , inputName , inputDeadLine , inputDone , inputContent);
                        }
                    }
                    catch (Exception ex){
                        Log.e("ERROR" , ex.toString());
                    }
                    finally {
                        db.close();
                    }
                    if(_mode == ToDoListActivity.MODE_INSERT){
                        msg = "新規登録されました。";
                        finish();
                    }
                    else{
                        msg = "更新されました。";
                    }
                    finish();
                    Toast.makeText(ToDoEditActivity.this , msg , Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * 日付選択ダイアログ表示ボタンが押された時のイベント処理用メソッド。
     */
    public void showDatePickerDialog(View view) throws ParseException {

        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);
        int nowMonth = cal.get(Calendar.MONTH);
        int nowDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

        TextView etInputDeadLine = findViewById(R.id.etInputDeadLine);
        String inputDeadLine = etInputDeadLine.getText().toString();

        if(!"".equals(inputDeadLine)){
            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy年MM月dd日");
            SimpleDateFormat convertFormat = new SimpleDateFormat("yyyy-MM-dd");
            /**
             * deadlineの変換。
             */
            Date sourceDate = null;
            sourceDate = sourceFormat.parse(inputDeadLine);
            inputDeadLine = convertFormat.format(sourceDate);
            Log.e("変換結果2",inputDeadLine);
            DateFormat SimpleDate = new SimpleDateFormat("yyyy-mm-dd");
            DateFormat yyyy = new SimpleDateFormat("yyyy");
            DateFormat mm = new SimpleDateFormat("mm");
            DateFormat dd = new SimpleDateFormat("dd");
            try {
                Date date = SimpleDate.parse(inputDeadLine);

                String strYear = yyyy.format(date);
                nowYear = Integer.parseInt(strYear);

                String strMonth = mm.format(date);
                nowMonth = (Integer.parseInt(strMonth))-1;

                String strDate = dd.format(date);
                nowDayOfMonth = Integer.parseInt(strDate);

            } catch(ParseException e) {
                e.printStackTrace();
                Log.e("FormatERROR","Formatで問題発生！");

            }
        }

        DatePickerDialog dialog = new DatePickerDialog(ToDoEditActivity.this, new DatePickerDialogDateSetListener() , nowYear, nowMonth, nowDayOfMonth);
        dialog.show();
    }

    /**
     * 日付選択ダイアログの、完了ボタンが押された時の処理が記述されたメンバクラス。
     */
    private class DatePickerDialogDateSetListener implements DatePickerDialog.OnDateSetListener{
        @Override
        public void onDateSet(DatePicker view , int year , int monthOfYear , int dayOfMonth){
            monthOfYear = monthOfYear + 1;
            String strMonth = String.valueOf(monthOfYear);;
            String strDate = String.valueOf(dayOfMonth);
            if(monthOfYear < 10 ){
                strMonth = "0" + strMonth;
            }
            if(dayOfMonth < 10 ){
                strDate = "0" + strDate;
            }
            TextView etYear = findViewById(R.id.etInputDeadLine);
            etYear.setText(year + "年" + strMonth + "月" + strDate + "日");

        }
    }

}
