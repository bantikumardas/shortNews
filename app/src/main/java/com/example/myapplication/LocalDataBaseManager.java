package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LocalDataBaseManager extends SQLiteOpenHelper {
    public static final String dbName="data.db";
    public  static  final int version=1;
    Context context;
    public LocalDataBaseManager(Context context){
        super(context, dbName, null, version);
        this.context=context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery="CREATE TABLE newsData( id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, link TEXT, keywords TEXT," +
                " author TEXT,description TEXT, content TEXT, date TEXT, imageUrl TEXT, catagory TEXT, " +
                "country TEXT, language TEXT);";
        db.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS newsData");
        onCreate(db);
    }
    public void deleteDataBase(){
        SQLiteDatabase database=this.getWritableDatabase();
        database.delete("newsData", null, null);
        database.close();
    }
    public int addResponse(String title, String link, String keywords, String author, String description, String content,
                           String date, String imageUrl, String catagory, String country, String language){
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("title", title);
        cv.put("link", link);
        cv.put("keywords", keywords);
        cv.put("author", author);
        cv.put("description", description);
        cv.put("content", content);
        cv.put("date", date);
        cv.put("imageUrl", imageUrl);
        cv.put("catagory", catagory);
        cv.put("country", country);
        cv.put("language", language);
        long res=database.insert("newsData", null, cv);
        database.close();
        if(res==-1) return 0;
        else return 1;
    }
    public ArrayList<newsClass> getDatabase(){
        String query = "SELECT  * FROM newsData";
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cs=null;
        ArrayList<newsClass> array=new ArrayList<>();
        try {
             cs=db.rawQuery(query,null);
             while (cs.moveToNext()){
                  String title=cs.getString(1);
                  String link=cs.getString(2);
                  String keywords=cs.getString(3);
                  String author=cs.getString(4);
                  String description=cs.getString(5);
                  String content=cs.getString(6);
                  String date=cs.getString(7);
                  String imageUrl=cs.getString(8);
                  String catagory=cs.getString(9);
                  String country=cs.getString(10);
                  String language=cs.getString(11);
                  newsClass news=new newsClass(title, link, keywords, author, description, content, date, imageUrl, catagory,
                          country, language);
                  array.add((news));
             }
        }catch (Exception e){
            Log.e("dataBaseError", e+"");
        }
        return array;
    }
}
