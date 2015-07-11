package com.app.rahul_zoldyck.MangaWolf;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;


 public class MyAdapter extends ArrayAdapter<String> {

    public MyAdapter(Context context, List<String> objects) {
        super(context, R.layout.layoutres, objects);
    }

     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
         LayoutInflater r = LayoutInflater.from(getContext());
         View v = r.inflate(R.layout.layoutres, parent, false);
         TextView name=(TextView)v.findViewById(R.id.aname);
         ImageView img=(ImageView)v.findViewById(R.id.aimg);
         String temp=getItem(position);
         if(temp.length()>20){


                 temp=temp.split(" ")[0]+" "+temp.split(" ")[1];

         }
         name.setText(temp);
         img.setImageBitmap(BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()
                 + File.separator+ OpenerActivity.Appname+File.separator+"cover"
                 +File.separator+getItem(position)+".jpg"));
         return v;
     }
 }
