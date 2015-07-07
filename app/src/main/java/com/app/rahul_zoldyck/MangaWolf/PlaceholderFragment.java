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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
import java.util.List;

public  class PlaceholderFragment extends Fragment {
    Mysqlhandler handle;
    GridView grid;

    int id=1;
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void addmanga(MainActivity mainActivity, String s) {
        Mysqlhandler hand=new Mysqlhandler(mainActivity,null);
        hand.getupdated(s);
    }

    private static final String ARG_SECTION_NUMBER = "section_number";


    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public PlaceholderFragment() {
    }
    ArrayList<String> all,fav;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        handle=new Mysqlhandler(getActivity(),null);
        if(handle.getnames().size()==0 && !isNetworkAvailable())
            startActivity(new Intent(getActivity(),Interneterror.class));
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        if(handle.getnames().size()==0){

        handle.getupdated("Bleach");
            Log.i("test","1");
        handle.getupdated("Naruto");
            Log.i("test","2");
        handle.getupdated("Naruto Gaiden The Seventh Hokage");
            Log.i("test","3");
        handle.getupdated("Fairy Tail");
            Log.i("test","4");
        handle.getupdated("Hunter X Hunter");
        }

        Log.i("test","5");
        grid=(GridView)rootView.findViewById(R.id.grid);
        all=new ArrayList<>();


        fav=new ArrayList<>();
        fav=handle.getfavname();
        all=handle.getnames();
        List<String> temp=new ArrayList<>();
        for(String i : all){
            File folder = new File(Environment.getExternalStorageDirectory()
                    +File.separator+MainActivity.Appname+File.separator+"cover"+File.separator+i+".jpg");
            if (!folder.exists()) {
                temp.add(i);
            }
            else {
                Log.i("result","Folder already exist");
            }
        }
        if(temp.size()!=0){
            coverdownload cd=new coverdownload();
            cd.execute(temp);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(id==1)
        grid.setAdapter(new MyAdapter(getActivity(),all));
        if(id==2)
            grid.setAdapter(new MyAdapter(getActivity(),fav));

        grid.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String names=(String)parent.getItemAtPosition(position);
                       // Toast.makeText(getActivity(), names, Toast.LENGTH_SHORT).show();
                       Intent i=new Intent(getActivity(),MangaInfo.class);
                        i.putExtra("manganame",names);
                        startActivity(i);
                    }
                }
        );
        return rootView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        id=getArguments().getInt(ARG_SECTION_NUMBER);
    }




    class coverdownload extends AsyncTask<List<String>,Void,Void>{
        Document doc=null;
        Elements img=null;
        String url,imgurl;
        Bitmap myBitmap;
        @Override
        protected Void doInBackground(List<String>... params) {
            for(String name : params[0]){
                url=MainActivity.URL1+name.replaceAll(" ","_").toLowerCase();

                try {
                    doc= Jsoup.connect(url).get();
                    Log.i("result",url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(doc!=null) {
                    img = doc.getElementsByTag("img");

                    for (Element i : img) {
                        if(i.attr("src").contains("manga"))
                            imgurl=i.attr("src");
                    }
                    try{
                        URL urls = new URL(imgurl);
                        HttpURLConnection connection = (HttpURLConnection) urls
                                .openConnection();
                        connection.setDoInput(true);
                        connection.setConnectTimeout(7000);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        myBitmap = BitmapFactory.decodeStream(input);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    FileOutputStream fos=null;
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    File file = new File(Environment.getExternalStorageDirectory()
                            + File.separator +MainActivity.Appname+File.separator+"cover"+File.separator+name+".jpg");

                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
    /*--- create a new FileOutputStream and write bytes to file ---*/
                    try {
                        fos = new FileOutputStream(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos!=null){
                            fos.write(bytes.toByteArray());
                            fos.close();
                            Log.i("result", Environment.getExternalStorageDirectory()
                                    + File.separator + MainActivity.Appname + File.separator + "cover"
                                    + File.separator + name + ".jpg" + " img saved ");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                }

                 }
            return null;
        }
    }
}

