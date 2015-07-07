package com.app.rahul_zoldyck.MangaWolf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;



public class ChapterList extends ActionBarActivity {
    public String parsestring(String s){
        s=s.replaceAll(" ","_");
        for(int i=0;i<s.length();i++){
            String temp=s.substring(i,i+1);
            Character t=s.charAt(i);
            if(!Character.isLetterOrDigit(t))


                s=s.replace(temp,"_");
        }
        for(int i=0;i<3;i++)
            s=s.replaceAll("__","_");
        s=s.toLowerCase();
        return s;
    }
String name,url;
    Integer totchap;
    ImageView img;
    TextView nam;
    ListView list;
    Integer pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list);

        Bundle b=getIntent().getExtras();
        if(b!=null){
            name=b.getString("animename");
            totchap=b.getInt("totpages");
            url=b.getString("url");

            nam=(TextView)findViewById(R.id.listname);
            img=(ImageView)findViewById(R.id.listimg);
            nam.setText(name);
            img.setImageBitmap(BitmapFactory.decodeFile(url));
        }
     list=(ListView)findViewById(R.id.chapterlist);
        String[] chaps=new String[totchap];
        for(int i=0;i<totchap;i++){
            chaps[i]="Chapter "+String.valueOf(totchap-i);
        }

     list.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,chaps));
        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //TODO:PReferance SET
                        totpage h=new totpage();
                        pos=totchap-position;
                        h.execute(MainActivity.URL1+parsestring(name)+MainActivity.URL+String.valueOf(pos)+ File.separator+"1.html");
                    }
                }
        );
    }


    class totpage extends AsyncTask<String,Void,Integer>{
        Document doc;
        int temp=53;
        @Override
        protected Integer doInBackground(String... params) {
            try {
                doc= Jsoup.connect(params[0]).get();
                Log.i("qwerty",params[0]);
                Elements links=doc.getElementsByTag("option");
                temp=0;
                for(Element l : links){
                    if(temp<Integer.parseInt(l.attr("value")))
                        temp=Integer.parseInt(l.attr("value"));
                    Log.i("zold",String.valueOf(temp));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            return temp;
        }
  int raw;
        @Override
        protected void onPostExecute(Integer integer) {
            raw=integer;
            //
            new AlertDialog.Builder(ChapterList.this)
                    .setTitle("Reading Option")
                    .setMessage("Do you want to make this chapter available for offline")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            Intent j=new Intent(ChapterList.this,DownloadService.class);
                            j.putExtra("anime",name);
                            j.putExtra("totpages",raw);
                            j.putExtra("chapter", pos);
                           j.putExtra("download",true);
                            startService(j);
                            Log.i("zold","sent int is "+String.valueOf(raw));
                            Intent i= new Intent(ChapterList.this,MangaPages.class);
                            i.putExtra("name",name);
                            i.putExtra("chapter",pos);
                            i.putExtra("totpages",raw);
                            i.putExtra("temp",totchap);
                            SharedPreferences pref=getSharedPreferences(name+"_"+String.valueOf(pos),MODE_PRIVATE); //todo: sharepref
                            SharedPreferences.Editor editor=pref.edit();
                            editor.putString("pno",String.valueOf(raw));
                            editor.apply();

                            startActivity(i);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent j=new Intent(ChapterList.this,DownloadService.class);
                            j.putExtra("anime",name);
                            j.putExtra("totpages",raw);
                            j.putExtra("chapter", pos);
                            j.putExtra("download",false);
                            startService(j);
                            Log.i("zold","sent int is "+String.valueOf(raw));
                            Intent i= new Intent(ChapterList.this,MangaPages.class);
                            i.putExtra("name",name);
                            i.putExtra("chapter",pos);
                            i.putExtra("totpages",raw);
                            i.putExtra("temp",totchap);
                            SharedPreferences pref=getSharedPreferences(name+"_"+String.valueOf(pos),MODE_PRIVATE); //todo: sharepref
                            SharedPreferences.Editor editor=pref.edit();
                            editor.putString("pno",String.valueOf(raw));
                            editor.apply();

                            startActivity(i);
                        }
                    })
                    .setIcon(android.R.drawable.ic_menu_upload)
                    .show();
            //


            super.onPostExecute(integer);
        }
    }

}
