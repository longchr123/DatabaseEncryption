package com.example.administrator.day0308;

import android.content.ContentValues;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase db,database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase.loadLibs(this);
        MySQLiteHelper dbHelper = new MySQLiteHelper(this, "0308.db", null, 1);
        db = dbHelper.getWritableDatabase("qwerdf");
        Button addData = (Button) findViewById(R.id.add_data);
        Button queryData = (Button) findViewById(R.id.query_data);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("name", "达芬奇密码");
                values.put("pages", 566);
                db.insert("Book", null, values);
            }
        });
        queryData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = db.query("Book", null, null, null, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        int pages = cursor.getInt(cursor.getColumnIndex("pages"));
                        Log.d("qqq", "book name is " + name);
                        Log.d("qqq", "book pages is " + pages);
                    }
                }
                cursor.close();
            }
        });

        ziDingYi();
    }
    public void ziDingYi(){
        database=SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory().getAbsolutePath()+"/0308.db","zhongyu2802",null);
        database.execSQL("create table if not exists Book(name text, pages integer)");
        ContentValues values = new ContentValues();
        values.put("name", "美人鱼");
        values.put("pages", 122);
        database.insert("Book", null, values);

        Cursor cursor = database.query("Book", null, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int pages = cursor.getInt(cursor.getColumnIndex("pages"));
                Log.d("qqq", "book name is " + name);
                Log.d("qqq", "book pages is " + pages);
            }
        }
    }
}
