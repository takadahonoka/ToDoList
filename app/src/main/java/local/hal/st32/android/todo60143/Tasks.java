package local.hal.st32.android.todo60143;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by takadahonoka on 2018/04/22.
 * タスクのリスト用クラス。
 */

public class Tasks {

    private int _id;
    private String _name;
    private String _deadline;
    private String _done;
    private String _note;

    public int getId() { return _id; }

    public void setId(int id) {
        _id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String getDeadLine() {
        return _deadline;
    }

    public void setDeadLine(String deadline) {
        _deadline = deadline;
    }

    public String getDone() {
        return _done;
    }

    public void setDone(String done) {
        _done = done;
    }

    public String getNote() {
        return _note;
    }

    public void setNote(String note) {
        _note = note;
    }

    static DateFormat yyyy = new SimpleDateFormat("yyyy");

    public static String convertYyyy(Long date) {
        return yyyy.format(new Date(date));
    }

}
