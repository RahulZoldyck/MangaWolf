package com.app.rahul_zoldyck.MangaWolf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

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

    String URL,name,Path;
    Integer pno,chap,totpage,temp;//todo: remove temp
    Zoomable img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_pages);
        img=(Zoomable)findViewById(R.id.pages);
        img.setEventListener(new Zoomable.Zoldyck() {
            @Override
            public void swipednext() {

                ++pno;
                setImage(pno);
            }

            @Override
            public void swipedprevious() {

                --pno;
                setImage(pno);
            }
        });

        Bundle b=getIntent().getExtras();
        if(b!=null){
           name= b.getString("name");
            chap=b.getInt("chapter");
            totpage=b.getInt("totpages");
            temp=b.getInt("temp");
            pno=1;
            SharedPreferences share=getSharedPreferences("pno", MODE_PRIVATE);
            SharedPreferences.Editor editor=share.edit();
            editor.putString("pno",String.valueOf(pno));
            editor.apply();
            URL= OpenerActivity.URL1+parsestring(name)+ OpenerActivity.URL+String.valueOf(chap)+"/";
            Path= OpenerActivity.PATH+name+ File.separator;


        }

    }
    private float x1,x2;
    static final int MIN_DISTANCE = 150;
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 > x1)
                    {
                        //Toast.makeText(this, "Left to Right swipe [Next]", Toast.LENGTH_SHORT).show ();
                    }

                    // Right to left swipe action
                    else
                    {
                       // Toast.makeText(this, "Right to Left swipe [Previous]", Toast.LENGTH_SHORT).show ();
                    }

                }
                else
                {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        SharedPreferences share=getSharedPreferences("pno", MODE_PRIVATE);
        SharedPreferences.Editor editor=share.edit();
        editor.putString("pno",String.valueOf(pno));
        editor.apply();
        super.onPause();
    }

    @Override
    protected void onResume() {
        SharedPreferences preferences=getSharedPreferences("pno",MODE_PRIVATE);
        pno=Integer.parseInt(preferences.getString("pno", "1"));
        setImage(pno);

        super.onResume();
    }

    private void setImage(Integer pno) {
        if (pno>totpage || pno<1){
            //TODO: fill this up
           sendback();
        }
        Path= OpenerActivity.PATH+name+ File.separator+"Chapter" + String.valueOf(chap)+File.separator+"Chapter"+String.valueOf(chap)+" Page"+String.valueOf(pno)+".jpg";
        File folder = new File(Path);
       // Log.i("test1",Path+"-->"+folder.exists());
       // Toast.makeText(MangaPages.this,Path,Toast.LENGTH_SHORT).show();
        if (!folder.exists()) {
            String url=URL+String.valueOf(pno)+".html";
            Alternate s=new Alternate();
            s.execute(url);
            //Toast.makeText(MangaPages.this,"Internet",Toast.LENGTH_SHORT).show();
        }
        else {
            img=(Zoomable)findViewById(R.id.pages);
            img.setImageBitmap(BitmapFactory.decodeFile(Path));
          //  Toast.makeText(MangaPages.this,"device",Toast.LENGTH_SHORT).show();
        }


    }

    private void sendback() {
        String path= OpenerActivity.PATH+"cover"+ File.separator+name+".jpg";
        Intent i=new Intent(MangaPages.this,ChapterList.class);
        i.putExtra("animename",name);
        i.putExtra("totpages",temp);
        i.putExtra("url",path);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_manga_pages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }



        return super.onOptionsItemSelected(item);
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

        @Override
        protected void onProgressUpdate(Void... values) {
           // sendback();
            super.onProgressUpdate(values);
        }
    }
}
