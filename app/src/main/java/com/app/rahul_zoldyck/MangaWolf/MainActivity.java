package com.app.rahul_zoldyck.MangaWolf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {
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
        while(s.endsWith("_")){
            s=s.substring(0,s.length()-1);
            Log.i("namechange","updated->"+s);
        }
        return s;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    Mysqlhandler handle;
    ArrayList<String> all;
    boolean flag=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File folder = new File(Environment.getExternalStorageDirectory() +File.separator+ OpenerActivity.Appname);
        boolean success;
        if (!folder.exists()) {
            success=folder.mkdir();
            Log.i("result", "Folder created result->" + success);
        }
        else {
            Log.i("result","Folder already exist");
        }
        File folder1 = new File(Environment.getExternalStorageDirectory() +
                File.separator+ OpenerActivity.Appname+File.separator+"cover");
        boolean success1;
        if (!folder1.exists()) {
            success1=folder1.mkdir();
            File nomedia=new File(Environment.getExternalStorageDirectory() +
                    File.separator+ OpenerActivity.Appname+File.separator+"cover" +
                    File.separator+".nomedia");
            if((!nomedia.exists()) )
                try {
                    boolean re=nomedia.createNewFile();
                    if(!re)
                        Log.e("path", nomedia + "Not Created");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            Log.i("result","Folder created result->"+success1);
        }
        else {
            Log.i("result", "Folder already exist");
        }
        handle=new Mysqlhandler(this,null);

        all=new ArrayList<>();
        all=handle.getnames();
        if(all.size()==0){
            if(!isNetworkAvailable()){
                startActivity(new Intent(this,Interneterror.class));
            }
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
            if (flag)
            addtodb(strings);
            else{
                coverdownload c=new coverdownload();
                String[] s=new String[strings.size()];
                s=strings.toArray(s);
                c.execute(s);
            }

            super.onPostExecute(strings);
        }


    }

    public void addtodb( ArrayList<String> strings) {


       handle.getlistupdated(strings);

        handle.setEventListener(
                new Mysqlhandler.Blank() {

                    @Override
                    public void downloadfinished() {
                    }

                    @Override
                    public void downloadlistfinished() {


                        getimage();
                    }
                }
        );
    }

    public void getimage() {
        TextView t=(TextView)findViewById(R.id.maintext);
        t.setText("Downloading cover images");
        flag=false;
        firsttime f=new firsttime();
        f.execute();

    }
    class coverdownload extends AsyncTask<String[], Void, Void> {
        Document doc = null;
        Elements img = null;
        String url, imgurl;
        Bitmap myBitmap;

        @Override
        protected Void doInBackground(String[]... params) {
            for (String name : params[0]) {
                url = OpenerActivity.URL1 + parsestring(name);
                Log.i("final","url-->"+url);

                try {
                    doc = Jsoup.connect(url).get();
                    Log.i("result", url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (doc != null) {
                    img = doc.getElementsByTag("img");

                    for (Element i : img) {
                        if (i.attr("src").contains("cover.jpg")) {
                            imgurl = i.attr("src");
                            Log.i("final","imgurl-->"+imgurl);
                        }
                    }
                    try {
                        URL urls = new URL(imgurl);
                        HttpURLConnection connection = (HttpURLConnection) urls
                                .openConnection();
                        connection.setDoInput(true);
                        connection.setConnectTimeout(7000);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        myBitmap = BitmapFactory.decodeStream(input);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    FileOutputStream fos = null;
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    File file = new File(Environment.getExternalStorageDirectory()
                            + File.separator + OpenerActivity.Appname + File.separator + "cover" + File.separator + name + ".jpg");

                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        fos = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null) {
                            fos.write(bytes.toByteArray());
                            fos.close();
                            Log.i("result", Environment.getExternalStorageDirectory()
                                    + File.separator + OpenerActivity.Appname + File.separator + "cover"
                                    + File.separator + name + ".jpg" + " img saved ");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
           startActivity(new Intent(MainActivity.this,OpenerActivity.class));

            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }
}