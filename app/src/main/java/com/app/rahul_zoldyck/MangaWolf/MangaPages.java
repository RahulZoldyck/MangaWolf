package com.app.rahul_zoldyck.MangaWolf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class MangaPages extends ActionBarActivity {
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

    String URL,name,Path,Url;
    Integer pno,maxpno,chap,totpage,totchap;
    Zoomable img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_pages);
        img=(Zoomable)findViewById(R.id.pages);
        img.setEventListener(new Zoomable.Zoldyck() {
            @Override
            public void swipednext() {
                if(pno<totpage){
                    ++pno;
                    maxpno=pno;
                }
                else
                    sendback();
                setImage(pno);
            }

            @Override
            public void swipedprevious() {
                if(pno>1)
                    --pno;
                else
                sendback();
                setImage(pno);
            }
        });

        Bundle b=getIntent().getExtras();
        if(b!=null){
           name= b.getString("name");
            chap=b.getInt("chapter");
            totpage=b.getInt("totpages");
            totchap=b.getInt("temp");
            Url=b.getString("url");
            SharedPreferences share=getSharedPreferences("pno", MODE_PRIVATE);
            SharedPreferences.Editor editor=share.edit();
            editor.putString("pno",String.valueOf(pno));
            editor.apply();

            Path= OpenerActivity.PATH+name+ File.separator;


        }

    }

    @Override
    protected void onPause() {
        SharedPreferences share=getSharedPreferences("pno", MODE_PRIVATE);
        SharedPreferences.Editor editor=share.edit();
        editor.putInt(name+"_"+"Chapter "+String.valueOf(chap)+"pno1", maxpno);
        editor.apply();
        super.onPause();
    }

    @Override
    protected void onResume() {
        SharedPreferences preferences=getSharedPreferences("pno",MODE_PRIVATE);
        Log.i("side","osj"+preferences.getInt(name+"_"+"Chapter "+String.valueOf(chap)+"pno1", 1));
        String temp=Integer.toString(preferences.getInt(name+"_"+"Chapter "+String.valueOf(chap)+"pno1", 1));
        pno=Integer.valueOf(temp);
        maxpno=pno;
        if(pno==0)
            pno=1;
        setImage(pno);

        super.onResume();
    }

    private void setImage(Integer pno) {
        if (pno>totpage || pno<1){

           sendback();
        }
        URL= OpenerActivity.URL1+parsestring(name)+ OpenerActivity.URL+String.valueOf(chap)+"/";
        Path= OpenerActivity.PATH+name+ File.separator+"Chapter" + String.valueOf(chap)+File.separator+"Chapter"+String.valueOf(chap)+" Page"+String.valueOf(pno)+".jpg";
        File folder = new File(Path);
        if (!folder.exists()) {
            String url=URL+String.valueOf(pno)+".html";
            Alternate s=new Alternate();
            s.execute(url);
        }
        else {
            img=(Zoomable)findViewById(R.id.pages);
            img.setImageBitmap(BitmapFactory.decodeFile(Path));
        }


    }

    private void sendback() {
        finish();
    }




    class Alternate extends AsyncTask<String,Void,Bitmap>{

        Bitmap myBitmap;
        Document doc,doc2;
        @Override
        protected Bitmap doInBackground(String... params) {

            try {

                doc= Jsoup.connect(params[0]).get();
                doc2=Jsoup.connect(params[0]).get();

            } catch (IOException e) {

                e.printStackTrace();
            }


            Log.i("higf","2");
            String url;
            Element link=doc.select("img").first();
            if(link==null){
               // onProgressUpdate();
            }
            else {
                url = link.attr("src");

                try {
                    Log.i("higf", "3");
                    java.net.URL urls = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) urls
                            .openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    myBitmap = BitmapFactory.decodeStream(input);

                } catch (Exception e) {
                    Log.i("higf", "dfs");
                }
            }
            Log.i("higf","4");
            return myBitmap;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            img.setImageBitmap(bitmap);
            super.onPostExecute(bitmap);
        }

    }

    @Override
    protected void onDestroy() {
        startService(new Intent(this,CleanerService.class));
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        Intent i=new Intent(this,ChapterList.class);
        i.putExtra("animename",name);
        i.putExtra("totpages",totchap);
        i.putExtra("url",Url);
       startActivity(i);
       finish();
    }
}
