package com.app.rahul_zoldyck.MangaWolf;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;


public class MangaInfo extends ActionBarActivity     {
String name,desc,path;
    int totchap;
    Mysqlhandler handle;
    ImageView img;
    TextView des,nam,totc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_info);
        handle=new Mysqlhandler(this,null);
        Bundle b=getIntent().getExtras();
        if(b!=null){
            name=b.getString("manganame");
            desc=handle.getdesc(name);
            totchap=handle.gettot(name);
            path= OpenerActivity.PATH+"cover"+ File.separator+name+".jpg";
            Log.i("enak",path);
            img=(ImageView)findViewById(R.id.infoimg);
            nam=(TextView)findViewById(R.id.infoname);
            des=(TextView)findViewById(R.id.desc);
            totc=(TextView)findViewById(R.id.totchap);
            img.setImageBitmap(BitmapFactory.decodeFile(path));
            nam.setText(name);
            des.setText(desc);
            totc.setText(String.valueOf(totchap));

        }


    }
     public void readmanga(View v){
         Intent i=new Intent(this,ChapterList.class);
         i.putExtra("animename",name);
         i.putExtra("totpages",totchap);
         i.putExtra("url",path);
         startActivity(i);
     }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_manga_info, menu);


        int f=handle.getfav(name);
        if(f==1)
            menu.findItem(R.id.action_fav).setChecked(true);
        if(f==0)
            menu.findItem(R.id.action_fav).setChecked(false);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            startActivity(new Intent(this,Settings.class));
            return true;
        }
        if (id == R.id.action_fav){

            if(item.isChecked()){
                item.setChecked(false);
                handle.setfav(name,0);
                Toast.makeText(this,name+" removed from Favorites",Toast.LENGTH_SHORT).show();
            }
            else{

                item.setChecked(true);
                handle.setfav(name,1);
                Toast.makeText(this,name+" added to Favorites",Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
