package com.example.administrator.addmima2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import net.sqlcipher.Cursor;
//import net.sqlcipher.database.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_down, bt_zhuanhuan, bt_read, bt_readData;
    private net.sqlcipher.database.SQLiteDatabase db, dbtest;//创建加密数据库，打开加密数据库
    private static SQLiteDatabase database;//原始数据库
    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DBFile/";
    private String DBName = "zl.db";
    private String jiaMiDBName = "jiamizl.db";
    private List<String> englishNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        net.sqlcipher.database.SQLiteDatabase.loadLibs(this);
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        initView();
        initListener();
    }

    private void initListener() {
        bt_down.setOnClickListener(this);
        bt_zhuanhuan.setOnClickListener(this);
        bt_read.setOnClickListener(this);
        bt_readData.setOnClickListener(this);
    }

    private void initView() {
        bt_down = (Button) findViewById(R.id.bt_down);
        bt_zhuanhuan = (Button) findViewById(R.id.bt_zhuanhuan);
        bt_read = (Button) findViewById(R.id.bt_read);
        bt_readData = (Button) findViewById(R.id.bt_readData);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_down:
                InputStream is = getResources().openRawResource(R.raw.zl);//需要修改
                try {
                    OutputStream os = new FileOutputStream(path + DBName);
                    byte[] bytes = new byte[200];
                    int len;
                    while ((len = is.read(bytes)) != -1) {
                        os.write(bytes, 0, len);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "下载完成", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_zhuanhuan:
                englishNames = getTableEnglishName(path + DBName);
//创建加密的数据库及其表格
                db = net.sqlcipher.database.SQLiteDatabase.openOrCreateDatabase(path + jiaMiDBName, "WCXLYHGYQLWWYLSWP2016", null);
                db.execSQL("create table if not exists tableNames" + "(EnglishName varchar,ChineseName varchar)");
                for (int i = 0; i < englishNames.size(); i++) {
                    db.execSQL("create table if not exists " + englishNames.get(i) + "(题干 varchar,A varchar,B varchar,C varchar,D varchar,E varchar,答案 varchar,解析 varchar,id integer)");
                    Log.i("tableName", englishNames.get(i));
                }
//将未加密表格tableNames数据填充到加密表格tableNames中
                database = SQLiteDatabase.openDatabase(path + DBName, null, 1);
                Cursor cursor = database.query("tableNames", new String[]{"EnglishName", "ChineseName"}, null, null, null, null, null, null);
//利用游标遍历所有数据对象添加到空数据库中
                while (cursor.moveToNext()) {
                    String chineseName = cursor.getString(cursor.getColumnIndex("ChineseName"));
                    String englishName = cursor.getString(cursor.getColumnIndex("EnglishName"));

                    ContentValues values = new ContentValues();
                    values.put("ChineseName", chineseName);
                    values.put("EnglishName", englishName);
                    db.insert("tableNames", null, values);
                }
//利用游标遍历所有数据对象添加到空数据库中new String[]{"题干", "A", "B", "C", "D", "E", "答案", "解析", "id"}
                for (int i = 0; i < englishNames.size(); i++) {
                    Cursor cursor1 = database.query(englishNames.get(i), null, null, null, null, null, null, null);
//                    String[] strings = cursor1.getColumnNames();
                    while (cursor1.moveToNext()) {
//                        ContentValues values2 = new ContentValues();
//                        for (int j = 0; j < strings.length; j++) {
//                            values2.put(strings[j], cursor1.getString(cursor1.getColumnIndex(strings[j])));
//                        }
//                        db.insert(englishNames.get(i), null, values2);

                        String content = cursor1.getString(cursor1.getColumnIndex("subject"));
                        String answerA = cursor1.getString(cursor1.getColumnIndex("A"));
                        String answerB = cursor1.getString(cursor1.getColumnIndex("B"));
                        String answerC = cursor1.getString(cursor1.getColumnIndex("C"));
                        String answerD = cursor1.getString(cursor1.getColumnIndex("D"));
                        String answerE = cursor1.getString(cursor1.getColumnIndex("E"));
                        String answer = cursor1.getString(cursor1.getColumnIndex("answer"));
                        String jieXi = cursor1.getString(cursor1.getColumnIndex("analysis"));
                        int id = cursor1.getInt(cursor1.getColumnIndex("id"));

                        ContentValues values = new ContentValues();
                        values.put("题干", content);
                        values.put("A", answerA);
                        values.put("B", answerB);
                        values.put("C", answerC);
                        values.put("D", answerD);
                        values.put("E", answerE);
                        values.put("答案", answer);
                        values.put("解析", jieXi);
                        values.put("id", id);
                        db.insert(englishNames.get(i), null, values);
                    }
                    cursor1.close();
                }
                cursor.close();
                Toast.makeText(this, "转换完成", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_read:
                dbtest = net.sqlcipher.database.SQLiteDatabase.openOrCreateDatabase(path + jiaMiDBName, "WCXLYHGYQLWWYLSWP2016", null);
                Cursor cursor2 = dbtest.query("tableNames", new String[]{"EnglishName", "ChineseName"}, null, null, null, null, null, null);
                while (cursor2.moveToNext()) {
                    String chineseName = cursor2.getString(cursor2.getColumnIndex("ChineseName"));
                    String englishName = cursor2.getString(cursor2.getColumnIndex("EnglishName"));
                    Log.i("table", chineseName + englishName);
                }
                dbtest.close();
                cursor2.close();
                break;
            case R.id.bt_readData:
                dbtest = net.sqlcipher.database.SQLiteDatabase.openOrCreateDatabase(path + jiaMiDBName, "WCXLYHGYQLWWYLSWP2016", null);
                Cursor cursor3 = dbtest.query(englishNames.get(0), null, null, null, null, null, null, null);
//                String[] strings = cursor3.getColumnNames();
                while (cursor3.moveToNext()) {
//                    for (int j = 0; j < strings.length; j++) {
//                        Log.i("content", cursor3.getString(cursor3.getColumnIndex(strings[j])) + "");
//                    }
                    String content = cursor3.getString(cursor3.getColumnIndex("题干"));
                    String answerA = cursor3.getString(cursor3.getColumnIndex("A"));
                    String answerB = cursor3.getString(cursor3.getColumnIndex("B"));
                    String answerC = cursor3.getString(cursor3.getColumnIndex("C"));
                    String answerD = cursor3.getString(cursor3.getColumnIndex("D"));
                    String answerE = cursor3.getString(cursor3.getColumnIndex("E"));
                    String answer = cursor3.getString(cursor3.getColumnIndex("答案"));
                    String jieXi = cursor3.getString(cursor3.getColumnIndex("解析"));
                    int id = cursor3.getInt(cursor3.getColumnIndex("id"));
                    Log.i("content", id + content);
                    Log.i("answerA", id + answerA);
                    Log.i("answerB", id + answerB);
                    Log.i("answerC", id + answerC);
                    Log.i("answerD", id + answerD);
                    Log.i("answerE", id + answerE);
                    Log.i("answer", id + answer);
                    Log.i("jieXi", id + jieXi);
                }
                dbtest.close();
                cursor3.close();
                break;
        }
    }

    //获取未加密数据库英文表名
    public static List<String> getTableEnglishName(String path) {
        List<String> list = new ArrayList<>();
        database = SQLiteDatabase.openOrCreateDatabase(path, null);
        Cursor cursor = database.query("tableNames", new String[]{"EnglishName", "ChineseName"}, null, null, null, null, null, null);
        //利用游标遍历所有数据对象
        while (cursor.moveToNext()) {
            String englishName = cursor.getString(cursor.getColumnIndex("EnglishName"));
            list.add(englishName);
        }
        return list;
    }
}
