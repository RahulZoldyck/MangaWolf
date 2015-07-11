package com.app.rahul_zoldyck.MangaWolf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.io.File;
import java.io.IOException;


public class OpenerActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {





    public static final String URL="/vTBD/c";
    public static final String URL1="http://mangafox.me/manga/";
    public static final String Appname="MangaWolf";
    public static final String PATH=Environment.getExternalStorageDirectory() +File.separator+Appname+File.separator;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this,CleanerService.class));

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

        setContentView(R.layout.activity_opener);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();


        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));





    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        if(id==R.id.action_add_manga){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add New Manga");
            builder.setMessage("Type the name of the Manga correctly with spaces");

            final AutoCompleteTextView input = new AutoCompleteTextView(this);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.anime_list));

            input.setInputType(InputType.TYPE_CLASS_TEXT );
            input.setAdapter(adapter);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String s = input.getText().toString();
                    PlaceholderFragment.addmanga(OpenerActivity.this,s);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        }
        return super.onOptionsItemSelected(item);
    }
}


