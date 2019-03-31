package local.hal.st32.android.todo60143;

import android.app.Activity;
import android.app.Dialog;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * 通常ダイアログクラス。
 */

public class FullDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("確認");
        builder.setMessage("このTaskの情報をDeleteしてもよろしいですか?");
        builder.setPositiveButton("削除" , new DialogButtonClickListener());
        builder.setNeutralButton("HELP??" , new DialogButtonClickListener());
        builder.setNegativeButton("キャンセル" , new DialogButtonClickListener());
        AlertDialog dialog = builder.create();
        return dialog;
    }

    /**
     * ダイアログのボタンが押された時の処理が記述されたメンバクラス。
     */
    private class DialogButtonClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog , int which){
            Activity parent = getActivity();
            String msg = "";
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Bundle bundle = getArguments();
                    String strId = bundle.getString("id");
                    int _idNo = Integer.parseInt(strId);
                    DatabaseHelper helper = new DatabaseHelper(parent);
                    SQLiteDatabase db = helper.getWritableDatabase();
                    try{
                        local.hal.st32.android.todo60143.DataAccess.delete(db , _idNo);
                    }
                    catch (Exception ex){
                        Log.e("ERROR" , ex.toString());
                    }
                    finally {
                        db.close();
                    }
                    msg = "削除されました。Bye(T.T)";
                    parent.finish();
                    break;
                case DialogInterface.BUTTON_NEUTRAL:
                    msg = "「削除」を押せばこのメモ消えるぞ?";
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    msg = "消さねぇんなら削除ボタン押すんじゃねえよ";
                    break;
            }
            Toast.makeText(parent , msg , Toast.LENGTH_SHORT).show();
        }
    }

}
