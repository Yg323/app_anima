package com.example.app_anima;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "anima.db";
    private static final int DB_VERSION = 1;

    // 테이블 생성
    private static final String CREATE_TABLE_STEP = "CREATE TABLE if not exists steptable(id integer primary key autoincrement, " +
            "stepcnt integer NOT NULL, writedate DATETIME DEFAULT (datetime('now', 'localtime')));";
    private static final String CREATE_TABLE_TEMP = "CREATE TABLE if not exists temptable(id integer primary key autoincrement, " +
            "tempvalue integer NOT NULL, writedate DATETIME DEFAULT (datetime('now', 'localtime')));";

    // 생성자
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // DB가 처음 만들어 질 때 호출. 여기서 테이블 생성 및 초기 레코드 삽입
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_STEP);
        db.execSQL(CREATE_TABLE_TEMP);

    }

    // DB를 열 때 호출한다.
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    // DB를 업그레이드 할 때 호출. (DB 버전이 바뀌었을 때)
    // 기존 테이블 삭제 & 새로 생성 하거나 ALTER TABLE 로 Schema 수정
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE if exists steptable");
        db.execSQL("DROP TABLE if exists temptable");
        onCreate(db);
    }
}
