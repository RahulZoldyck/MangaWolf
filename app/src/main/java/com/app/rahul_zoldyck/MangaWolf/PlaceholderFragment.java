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
import android.widget.ProgressBar;
import android.widget.Toast;

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
    MyAdapter alladapt,favadapt;

    View v;
   static ProgressBar spinner;
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

    Mysqlhandler handle;
    GridView grid;

    int id = 1;

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void addmanga(final OpenerActivity mainActivity, String s) {
        spinner.setVisibility(View.VISIBLE);
        Mysqlhandler hand = new Mysqlhandler(mainActivity, null);
        hand.getupdated(s);
        hand.setEventListener(new Mysqlhandler.Blank() {
            @Override
            public void downloadfinished() {
                spinner.setVisibility(View.GONE);
                mainActivity.startActivity(new Intent(mainActivity,OpenerActivity.class));
            }
        });
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

    ArrayList<String> all, fav;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        handle = new Mysqlhandler(getActivity(), null);

        if (handle.getnames().size() == 0 && !isNetworkAvailable())
            startActivity(new Intent(getActivity(), Interneterror.class));
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        spinner=(ProgressBar)rootView.findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        grid = (GridView) rootView.findViewById(R.id.grid);

        if (handle.getnames().size() == 0) {
            spinner.setVisibility(View.VISIBLE);

            handle.getupdated("Bleach");
            Toast.makeText(getActivity(),"1 completed",Toast.LENGTH_SHORT).show();

            Toast.makeText(getActivity(),"2 completed",Toast.LENGTH_SHORT).show();
            handle.getupdated("Naruto");
            Toast.makeText(getActivity(),"3 completed",Toast.LENGTH_SHORT).show();

            Toast.makeText(getActivity(),"4 completed",Toast.LENGTH_SHORT).show();
            handle.getupdated("Naruto Gaiden The Seventh Hokage");
            Toast.makeText(getActivity(),"5 completed",Toast.LENGTH_SHORT).show();

            Toast.makeText(getActivity(),"6 completed",Toast.LENGTH_SHORT).show();
            handle.getupdated("Fairy Tail");
            Toast.makeText(getActivity(),"7 completed",Toast.LENGTH_SHORT).show();

            handle.getupdated("Hunter X Hunter");
            spinner.setVisibility(View.GONE);
            handle.setEventListener(
                    new Mysqlhandler.Blank() {
                        @Override
                        public void downloadfinished() {
                            Log.i("update","updated");
                            updatepicfirsttime();  //TODO:Update all in sqlite
                        }
                    }
            );
            //getActivity().startActivity(new Intent(getActivity(),MainActivity.class));
        }


        Log.i("test", "5");
        all = new ArrayList<>();


        fav = new ArrayList<>();
        fav = handle.getfavname();
        all = handle.getnames();
        alladapt=new MyAdapter(getActivity(),all);
        favadapt=new MyAdapter(getActivity(),fav);
        List<String> temp = new ArrayList<>();
        for (String i : all) {
            File folder = new File(Environment.getExternalStorageDirectory()
                    + File.separator + OpenerActivity.Appname + File.separator + "cover" + File.separator + i + ".jpg");
            if (!folder.exists()) {
                temp.add(i);
            } else {
                Log.i("result", "Folder already exist");
            }
        }
        v=rootView;
        if (temp.size() != 0) {
            String[]s=new String[temp.size()];
            s=temp.toArray(s);
            spinner.setVisibility(View.VISIBLE);
            coverdownload cd = new coverdownload();
            cd.execute(s);


               try {
                   Thread.sleep(5000);
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }

           return v;

        } else {
            spinner.setVisibility(View.GONE);

            if (id == 1)
                grid.setAdapter(alladapt);
            if (id == 2)
                grid.setAdapter(favadapt);
            grid.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String names = (String) parent.getItemAtPosition(position);
                            Intent i = new Intent(getActivity(), MangaInfo.class);
                            i.putExtra("manganame", names);
                            startActivity(i);
                        }
                    }
            );
          return rootView;

        }



    }

    private void updatepicfirsttime() {
        spinner.setVisibility(View.VISIBLE);

        all = new ArrayList<>();
        all = handle.getnames();
        List<String> temp = new ArrayList<>();
        for (String i : all) {
            File folder = new File(Environment.getExternalStorageDirectory()
                    + File.separator + OpenerActivity.Appname + File.separator + "cover" + File.separator + i + ".jpg");
            if (!folder.exists()) {
                temp.add(i);
            } else {
                Log.i("result", "Folder already exist");
            }
        }

        if (temp.size() != 0) {
            String[] s = new String[temp.size()];
            s = temp.toArray(s);
            coverdownload cd = new coverdownload();
            cd.execute(s);

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((OpenerActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        id = getArguments().getInt(ARG_SECTION_NUMBER);
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

                try {
                    doc = Jsoup.connect(url).get();
                    Log.i("result", url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (doc != null) {
                    img = doc.getElementsByTag("img");

                    for (Element i : img) {
                        if (i.attr("src").contains("manga"))
                            imgurl = i.attr("src");
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
            spinner.setVisibility(View.GONE);
//            Toast.makeText(getActivity(),"PostExecute",Toast.LENGTH_SHORT).show();
            Log.i("update","onPostExecute");
                grid=(GridView)v.findViewById(R.id.grid);
            if (id == 1)
                grid.setAdapter(alladapt);
            if (id == 2 && fav!=null)
                grid.setAdapter(favadapt);
            grid.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String names = (String) parent.getItemAtPosition(position);
                            Intent i = new Intent(getActivity(), MangaInfo.class);
                            i.putExtra("manganame", names);
                            startActivity(i);
                        }
                    }
            );
            super.onPostExecute(aVoid);
        }
    }

}