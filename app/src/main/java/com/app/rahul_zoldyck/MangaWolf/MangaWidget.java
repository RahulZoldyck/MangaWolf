package com.app.rahul_zoldyck.MangaWolf;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.File;

public class MangaWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            MangaWidgetConfigureActivity.deleteTitlePref(context, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = MangaWidgetConfigureActivity.loadTitlePref(context, appWidgetId);


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.manga_widget);
        views.setTextViewText(R.id.widetname, widgetText);
        if(widgetText!=null){

        Intent i=new Intent(context,MangaInfo.class);
        i.putExtra("manganame",MangaWidgetConfigureActivity.loadTitlePref(context, appWidgetId));
            Log.i("wid","widget->"+MangaWidgetConfigureActivity.loadTitlePref(context, appWidgetId));
        PendingIntent p = PendingIntent.getActivity(context, appWidgetId, i, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setImageViewBitmap(R.id.widgetimage, BitmapFactory.decodeFile(OpenerActivity.PATH + "cover" + File.separator + widgetText.toString() + ".jpg"));
        views.setOnClickPendingIntent(R.id.widgetimage,p);}
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }
}


