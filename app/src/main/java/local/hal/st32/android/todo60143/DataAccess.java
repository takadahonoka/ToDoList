package local.hal.st32.android.todo60143;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import java.sql.Timestamp;

import local.hal.st32.android.todo60143.Tasks;

/**
 * Created by takadahonoka on 2018/03/04.
 */

public class DataAccess {

    /**
     * 全データ検索メソッド
     * @param db SQLiteDatabaseオブジェクト
     * @return 検索結果のCursorオブジェクト
     * */
    public static Cursor findAll(SQLiteDatabase db , int no , String sqlwhere){
        String sql = "";
        switch (no){
            case 0:
                sql = "SELECT _id , name , deadline , done , note , strftime('%Y年%m月%d日', deadline) AS deadline2 FROM tasks "+ sqlwhere +" ORDER BY deadline DESC ;";
                break;
            case 1:
                sql = "SELECT _id , name , deadline , done , note , strftime('%Y年%m月%d日', deadline) AS deadline2  FROM tasks "+ sqlwhere +" ORDER BY deadline DESC ;";
                break;
            case 2:
                sql = "SELECT _id , name , deadline , done , note , strftime('%Y年%m月%d日', deadline) AS deadline2  FROM tasks WHERE done = 1 ORDER BY deadline DESC ";
                break;
            case 3:
                sql = "SELECT _id , name , deadline , done , note , strftime('%Y年%m月%d日', deadline) AS deadline2  FROM tasks WHERE done = 0 ORDER BY deadline ASC ;";
                break;
        }
        Cursor cursor = db.rawQuery(sql , null);
        return cursor;
    }

    /**
     * 日付毎の全データ検索メソッド
     * @param db SQLiteDatabaseオブジェクト
     * @return 検索結果のCursorオブジェクト
     * */
    public static Cursor findDateAll(SQLiteDatabase db , String date){
        String sql = "";
        sql = "SELECT _id , name , deadline , done , note , strftime('%Y年%m月%d日', deadline) AS deadline2 FROM tasks WHERE deadline = '"+ date +"' ORDER BY deadline DESC ;";
        Log.e("日付毎のSQL文",sql);
        Cursor cursor = db.rawQuery(sql , null);
        return cursor;
    }

    /**
     * 主キーによる検索
     * @param db SQLiteDatabaseオブジェクト
     * @param id 主キー値
     * @return 主キーに対応するデータを格納したShopオブジェクト。対応するデータが存在しない場合はnull。
     */
    public static Tasks findByPK(SQLiteDatabase db , int id){
        String sql = "SELECT _id , name , deadline , done , note FROM tasks WHERE _id =" + id ;
        Cursor cursor = db.rawQuery(sql , null);
        Tasks result = null;
        if(cursor.moveToFirst()){
            int idxName = cursor.getColumnIndex("name");
            int idxDeadLine = cursor.getColumnIndex("deadline");
            int idxDone = cursor.getColumnIndex("done");
            int idxNote = cursor.getColumnIndex("note");

            String name = cursor.getString(idxName);
            String deadline = cursor.getString(idxDeadLine);
            String done = cursor.getString(idxDone);
            String note = cursor.getString(idxNote);

            result = new Tasks();
            result.setId(id);
            result.setName(name);
            result.setDeadLine(deadline);
            result.setDone(done);
            result.setNote(note);
        }
        return result;
    }

    /**
     *メモ情報を更新するメソッド
     * @param db SQLiteDatabaseオブジェクト
     * @param id 主キー値
     * @return 更新件数
     * */
    public static int update(SQLiteDatabase db , int id , String name , String deadline , String done , String note){
        String sql = "UPDATE tasks SET name = ?,deadline = ?,done = ?,note = ? WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1 , name);
        stmt.bindString(2 , deadline);
        stmt.bindString(3 , done);
        stmt.bindString(4 , note);
        stmt.bindLong(5 , id);
        int result = stmt.executeUpdateDelete();
        return result;
    }

    /**
     *メモ情報を更新するメソッド
     * @param db SQLiteDatabaseオブジェクト
     * @param id 主キー値
     * @return 更新件数
     * */
    public static int update2(SQLiteDatabase db , int id , String done){
        String sql = "UPDATE tasks SET done = ? WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindLong(1 , Long.parseLong(done));
        stmt.bindLong(2 , id);
        int result = stmt.executeUpdateDelete();
        return result;
    }

    /**
     *メモ情報を新規登録するメソッド
     * @param db SQLiteDatabaseオブジェクト
     * @return 登録したレコードの主キーの値
     * */
    public static long insert(SQLiteDatabase db, String name , String deadline , String done , String note){
        String sql = "INSERT INTO tasks (name,deadline,done,note) VALUES (? , ? , ? , ?)";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1 , name);
        stmt.bindString(2 , deadline);
        stmt.bindString(3 , done);
        stmt.bindString(4 , note);
        long id = stmt.executeInsert();
        return id;
    }

    /**
     *メモ情報を削除するメソッド
     * @param db SQLiteDatabaseオブジェクト
     * @return 登録したレコードの主キーの値
     * */
    public static int delete(SQLiteDatabase db, int id){
        String sql = "DELETE FROM tasks WHERE _id = ? ";
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindLong(1 , id);
        int result = stmt.executeUpdateDelete();
        return result;
    }


    /**
     * 完了未完了のチェックが変更された時。
     *
     *  @param db SQLiteDatabaseオブジェクト
     *  @param id 変更対象レコードの主キー値。
     *  @param isChecked trueの場合フラグをon(値1)に、falseの場合フラグをoff(値0)に変更する。
     */
    public static void changeFinishChecked(SQLiteDatabase db,long id,boolean isChecked){
        String sql = "UPDATE tasks SET done = ? WHERE _id = ?";

        SQLiteStatement stmt = db.compileStatement(sql);
        if(isChecked){
            stmt.bindLong(1,1);
        }else{
            stmt.bindLong(1,0);
        }
        stmt.bindLong(2,id);
        stmt.executeInsert();
    }

}
