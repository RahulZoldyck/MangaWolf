package com.app.rahul_zoldyck.MangaWolf;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import java.io.File;


public class CleanerService extends IntentService {

    private static final String Appname="MangaWolf";
    private static final String PATH= Environment.getExternalStorageDirectory() + File.separator+Appname;


    public CleanerService() {
        super("CleanerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        File[] mangas=new File(PATH).listFiles();
        for(File i : mangas){



            File[] f=i.listFiles();
            if(f!=null)
            for(File d : f){
                File[] g=d.listFiles();
                String path=d.getAbsolutePath()+File.separator+".nomedia";
                File test=new File(path);
                if((!test.exists()) && g!=null){
                    for(File e: g){
                        boolean eh;
                            eh=e.delete();
                        if(!eh)
                            Log.e("path",e+" Not Deleted");
                    }
                }


            }


        }

    }


}