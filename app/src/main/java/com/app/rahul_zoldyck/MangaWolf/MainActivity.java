package com.app.rahul_zoldyck.MangaWolf;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {
    Mysqlhandler handle;
    ArrayList<String> all;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handle=new Mysqlhandler(this,null);
        all=new ArrayList<>();
        all=handle.getnames();
        if(all.size()==0){
           firsttime f=new firsttime();
            f.execute();
        }
        else
            startActivity(new Intent(this,OpenerActivity.class));
    }



    class firsttime extends AsyncTask<Void,Void,ArrayList<String>>{
        Document doc;
        ArrayList<String> pop;
        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            pop=new ArrayList<>();
            try {
                doc= Jsoup.connect("http://mangafox.me/").get();
                Element popular=doc.getElementById("popular");
                Elements e=popular.select("a");
                for(Element name : e){
                    if(name.hasClass("series_preview")) {
                        pop.add(name.text());
                        Log.i("Main",name.text());
                    }


                }


            } catch (IOException e) {
                startActivity(new Intent(MainActivity.this,Interneterror.class));
                e.printStackTrace();

            }

            return pop;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            addtodb(strings);
            super.onPostExecute(strings);
        }


    }

    private void addtodb(ArrayList<String> strings) {
        for(String s : strings) {
            handle.getupdated(s);
        }
        handle.setEventListener(
                new Mysqlhandler.Blank() {
                    @Override
                    public void downloadfinished() {
                        startActivity(new Intent(MainActivity.this,OpenerActivity.class));
                    }
                }
        );
    }
}