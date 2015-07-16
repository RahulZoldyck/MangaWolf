package com.app.rahul_zoldyck.MangaWolf;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UpdateService extends IntentService{
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
            Log.i("namechange", "updated->" + s);
        }
        return s;
    }
    Mysqlhandler handle;
    NotificationCompat.Builder notify;
    ArrayList<String> all;
    int requestcode;
    ArrayList<Integer> pgnos;
    public UpdateService (){
        super("UpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        requestcode=(int)System.currentTimeMillis();
        Context c= getApplicationContext();
        notify=new NotificationCompat.Builder(c);
        notify.setAutoCancel(true);
        handle=new Mysqlhandler(c,null);
        all=new ArrayList<>();
        pgnos=new ArrayList<>();
        all=handle.getnames();
        pgnos=handle.gettotpg();
        for(String name : all){
            String urls;
            int pgno;
            pgno=pgnos.get(all.indexOf(name));
            urls=parsestring(name);
            String url=OpenerActivity.URL1+urls;
            Integer npgno=null;
            try {

                org.jsoup.Connection con= Jsoup.connect(url);
                con.timeout(10000);
                Document doc= con.get();
                Element f=doc.getElementsByClass("slide").first();

                float temp=Float .parseFloat(f.text().split(" ")[f.text().split(" ").length - 1]);
                 npgno=Math.round(temp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(npgno!=null) {
                if (npgno != pgno){
                    handle.modpg(name, npgno);
                notify.setSmallIcon(R.drawable.circle_wolf);
                notify.setLargeIcon(BitmapFactory.decodeFile(OpenerActivity.PATH + "cover" + File.separator + name + ".jpg"));
                notify.setTicker("New Chapter released in " + name);
                notify.setWhen(System.currentTimeMillis());
                notify.setContentTitle("New Chapter released in " + name);
                notify.setContentText("Check out the latest Chapter " + String.valueOf(npgno) + " of " + name);
                Intent i = new Intent(this, ChapterList.class);
                i.putExtra("animename", name);
                i.putExtra("totpages", npgno);
                i.putExtra("url", OpenerActivity.PATH + "cover" + File.separator + name + ".jpg");
                PendingIntent p = PendingIntent.getActivity(c, requestcode, i, PendingIntent.FLAG_UPDATE_CURRENT);
                notify.setContentIntent(p);
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.notify(requestcode, notify.build());
            }
            }
        }
    }


}
