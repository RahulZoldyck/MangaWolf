package com.app.rahul_zoldyck.MangaWolf;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;


public class Mylistadapter extends ArrayAdapter<String> {
    Context c;
    String anime;
    String[] chapter;
    public Mylistadapter(Context context, String animename, String[] chapter) {

        super(context, R.layout.listadapterxml, chapter);
        c=context;
        this.chapter=new String[chapter.length];
        this.chapter=chapter;
        anime=animename;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater r = LayoutInflater.from(getContext());
        View v = r.inflate(R.layout.listadapterxml, parent, false);
        TextView chapname=(TextView)v.findViewById(R.id.listtextchapter);

        Log.i("asfds",String.valueOf(position));
        chapname.setText(chapter[position]);
        TextView page=(TextView)v.findViewById(R.id.listtextpg);
        ProgressBar pb=(ProgressBar)v.findViewById(R.id.listtextprogress);
        SharedPreferences pref=c.getSharedPreferences(anime+"_"+chapter[position],Context.MODE_PRIVATE);//TODO:log it all
        String s=String.valueOf(pref.getInt("pno01",-1));
        int totpage=Integer.parseInt(s);
        SharedPreferences prefi=c.getSharedPreferences("pno",Context.MODE_PRIVATE);
        String s1=String.valueOf(prefi.getInt(anime+"_"+chapter[position]+"pno1",-1));
        Log.i("sjf",anime+"_"+chapter[position]+"pno01");
        int pages=Integer.parseInt(s1);
        if(totpage!=-1 && pages!=-1) {
            pb.setProgress((int) ((float) pages *100/ totpage));
            page.setText(pages + "/" + totpage);
            page.setTextColor(Color.parseColor("#3F85F4"));
        }
        else{
            pb.setVisibility(View.INVISIBLE);
            page.setText("UNREAD");
            page.setTextColor(Color.parseColor("#3F85F4"));
        }
        return v;
    }
}
