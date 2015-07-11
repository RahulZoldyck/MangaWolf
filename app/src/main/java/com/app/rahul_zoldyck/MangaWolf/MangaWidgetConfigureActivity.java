package com.app.rahul_zoldyck.MangaWolf;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

/**
 * The configuration screen for the {@link MangaWidget MangaWidget} AppWidget.
 */
public class MangaWidgetConfigureActivity extends Activity {
    ArrayList<String> manganames;
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String PREFS_NAME = "com.app.rahul_zoldyck.MangaWolf.MangaWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    GridView gd;

    public MangaWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setResult(RESULT_CANCELED);
        manganames=new ArrayList<>();
        Mysqlhandler handle=new Mysqlhandler(this,null);
        manganames=handle.getnames();
        setContentView(R.layout.manga_widget_configure);
        gd=(GridView)findViewById(R.id.widgetgrid);
        gd.setAdapter(new MyAdapter(this,manganames));
        gd.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        saveTitlePref(MangaWidgetConfigureActivity.this, mAppWidgetId, manganames.get(position));
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MangaWidgetConfigureActivity.this);

                            MangaWidget.updateAppWidget(MangaWidgetConfigureActivity.this, appWidgetManager, mAppWidgetId);


                        Intent resultValue = new Intent();
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                        setResult(RESULT_OK, resultValue);
                        finish();
                    }
                }
        );


        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }


    }


    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.commit();
    }

    static String loadTitlePref(Context context, int appWidgetId) {

        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        Log.i("wid", "loadTitlePref->" + titleValue);

            return titleValue;

    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.commit();
    }
}



