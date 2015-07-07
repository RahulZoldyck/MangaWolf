package com.app.rahul_zoldyck.MangaWolf;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadService extends IntentService {
    Integer pno, chap, totpage;
    String anime, Path;
    Document doc;
    Bitmap myBitmap;


    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Bundle b = intent.getExtras();
        chap = b.getInt("chapter");
        totpage = b.getInt("totpages");
        anime = b.getString("anime");
        boolean download=b.getBoolean("download");
        pno = 1;
        boolean re=true;
        File test=new File(MainActivity.PATH + anime + File.separator+"Chapter" + String.valueOf(chap) );
        if(!test.exists())
            re=test.mkdir();
        if(!re)
            Log.e("path",test+"Not Created");
        File nomedia=new File(test.getAbsolutePath()+File.separator+".nomedia");
        if((!nomedia.exists()) && download)
            try {
                re=nomedia.createNewFile();
                if(!re)
                    Log.e("path",nomedia+"Not Created");

            } catch (IOException e) {
                e.printStackTrace();
            }


        Path = MainActivity.PATH + anime + File.separator +"Chapter" + String.valueOf(chap)+File.separator+
                "Chapter" + String.valueOf(chap) + " Page";//+String.valueOf(pno)+".jpg";
        downloadchapt(String.valueOf(chap));
    }

    private void downloadchapt(String chapt) {
        int temp=totpage;
        Log.d("impt",String.valueOf(temp));

        for (int i = 1; i <= totpage; i++) {

            File file = new File(Path + String.valueOf(i) + ".jpg");
            if(!file.exists())

            downloadpage(MainActivity.URL1 + anime.replaceAll(" ","_").toLowerCase() + MainActivity.URL + chapt + "/" + String.valueOf(i) + ".html", i);
        }
        Log.i("finish","service finished");
    }

    private void downloadpage(String urld, int pno) {
        try {
            doc = Jsoup.connect(urld).get();
        } catch (IOException e) {
            Log.i("error", "e-2->" + e.toString());
            e.printStackTrace();
        }
        Element link = doc.select("img").first();
        String url = link.attr("src");

        try {
            Log.i("higf", "3");
            URL urls = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urls
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);
            Log.i("result", "img downloaded");
        } catch (Exception e) {
            Log.i("error", "e-2->" + e.toString());
        }
        savetoSD(myBitmap, pno);
    }

    private void savetoSD(Bitmap myBitmap, int pno) {
        FileOutputStream fos = null;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File file = new File(Path + String.valueOf(pno) + ".jpg");
        Log.i("errors",Path+ String.valueOf(pno) + ".jpg");
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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}

