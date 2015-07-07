package com.app.rahul_zoldyck.MangaWolf;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Mysqlhandler extends SQLiteOpenHelper {
    private static final int VERSION =2;
    private static final String DATABASE_NAME="manga.db";
    private static final String TABLENAME="MANGAINFO";
    private static final String ANIMENAME="name";
    private static final String ANIMEDESC="description";
    private static final String TOTALCHAP="totalchapter";
    public Mysqlhandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query="CREATE TABLE "+TABLENAME+"(" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                ANIMENAME +" TEXT," +
                ANIMEDESC+" TEXT," +
                TOTALCHAP+" INT," +
                "fav INT);";
        db.execSQL(query);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLENAME);
        onCreate(db);
        db.close();
    }
    public void deleteall(){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+TABLENAME);
        onCreate(db);
    }
    public void addentry(String animename,String Description,int totchap){
        ContentValues v=new ContentValues();
        v.put(ANIMENAME,animename);
        v.put(ANIMEDESC,Description);
        v.put(TOTALCHAP,totchap);
        v.put("fav",0);
        SQLiteDatabase db=getWritableDatabase();
        db.insert(TABLENAME,null,v);
        db.close();
    }
    public String getdesc(String name){
        SQLiteDatabase db5=getWritableDatabase();
        String desc="";
        String query="SELECT "+ANIMEDESC+" FROM "+TABLENAME+" WHERE ( "+ANIMENAME+"= \""+name+"\" );";
        Cursor c1=db5.rawQuery(query,null);
        c1.moveToFirst();
        while(!c1.isAfterLast()){
            if(c1.getString(c1.getColumnIndex(ANIMEDESC))!=null){
                desc=c1.getString(c1.getColumnIndex(ANIMEDESC));
                Log.i("db",desc);

            }
            c1.moveToNext();

        }
        c1.close();
        db5.close();
        return desc;
    }
    public int gettot(String name){
        SQLiteDatabase db5=getWritableDatabase();
        int tot=0;
        String query="SELECT "+TOTALCHAP+" FROM "+TABLENAME+" WHERE ( "+ANIMENAME+"= \""+name+"\" );";
        Cursor c1=db5.rawQuery(query,null);
        c1.moveToFirst();
        while(!c1.isAfterLast()){
            if(c1.getString(c1.getColumnIndex(TOTALCHAP))!=null){
                tot=c1.getInt(c1.getColumnIndex(TOTALCHAP));
                Log.i("db",String.valueOf(tot));

            }
            c1.moveToNext();

        }
        c1.close();
        db5.close();
        return tot;
    }
    public ArrayList<String> getnames(){
        ArrayList<String> names=new ArrayList<>();
        SQLiteDatabase db5=getWritableDatabase();
        String query="SELECT "+ANIMENAME+" FROM "+TABLENAME+" WHERE 1;";
        Cursor c1=db5.rawQuery(query,null);
        c1.moveToFirst();
        while(!c1.isAfterLast()){
            if(c1.getString(c1.getColumnIndex(ANIMENAME))!=null){
                names.add(c1.getString(c1.getColumnIndex(ANIMENAME)));

            }
            c1.moveToNext();

        }
        c1.close();
        db5.close();
        return names;
    }
    public ArrayList<String> getfavname(){
        ArrayList<String> names=new ArrayList<>();
        SQLiteDatabase db5=getWritableDatabase();
        String query="SELECT "+ANIMENAME+" FROM "+TABLENAME+" WHERE ( fav = 1 );";
        Cursor c1=db5.rawQuery(query,null);
        c1.moveToFirst();
        while(!c1.isAfterLast()){
            if(c1.getString(c1.getColumnIndex(ANIMENAME))!=null){
                names.add(c1.getString(c1.getColumnIndex(ANIMENAME)));
            }
            c1.moveToNext();

        }
        c1.close();
        db5.close();
        return names;
    }
    public void setfav(String name,Integer fav){
        SQLiteDatabase db=getWritableDatabase();
        String query="UPDATE "+TABLENAME+" " +
                "SET fav="+fav+
                " WHERE ("+ANIMENAME+"=\""+name+"\");";
        db.execSQL(query);
        db.close();

    }
    public int getfav(String name){
        int fav=0;
        SQLiteDatabase db5=getWritableDatabase();
        String query="SELECT fav FROM "+TABLENAME+ " WHERE ( "+ANIMENAME+"= \""+name+"\" );";
        Cursor c1=db5.rawQuery(query,null);
        c1.moveToFirst();
        while(!c1.isAfterLast()){
            if(c1.getString(c1.getColumnIndex("fav"))!=null){
                fav=c1.getInt(c1.getColumnIndex("fav"));
            }
            c1.moveToNext();

        }
        c1.close();
        db5.close();
        return fav;

    }
    public void getupdated(String name){
        getupdates s=new getupdates();
        File folder = new File(MainActivity.PATH+name);
        boolean success;
        if (!folder.exists()) {
            success=folder.mkdir();
            Log.i("result", "Folder created result->" + success);
        }
        else {
            Log.i("result","Folder already exist");
        }
        s.execute(name);

    }

    class getupdates extends AsyncTask<String,Void,String[]> {
        String name,url,desc;
        Document doc;
        @Override
        protected String[] doInBackground(String... params) {
            name=params[0].replaceAll(" ","_").toLowerCase();
            url=MainActivity.URL1+name;
            try {
                doc= Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
                onProgressUpdate();
                Log.e("error",e.toString());
            }
            Elements e=doc.getElementsByClass("summary");
            for(Element i : e){
                desc=i.text();
            }
            Element f=doc.getElementsByClass("slide").first();

            String ta=f.text();
            String sa=f.text().split(" ")[f.text().split(" ").length-1];
            String[] sh={params[0],desc,sa};
            Log.i("aka",sa);
            return sh;
        }

        @Override
        protected void onPostExecute(String[] s) {

            String tot=s[2];
            String des=s[1];
            String nam=s[0];
           Float r= Float.parseFloat(tot);
            int total=Math.round(r);
            addentry(nam,des,total);
            super.onPostExecute(s);
        }
    }

}
