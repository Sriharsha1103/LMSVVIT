package com.vvit.aammu.lmsvvit.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.vvit.aammu.lmsvvit.R;

public class LMSWidget extends AppWidgetProvider {

    public static final String WIDGET_UPDATE_ACTION = "com.vvit.aammu.lmsvvit.widget.WIDGET_UPDATE_ACTION";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        final int N = appWidgetIds.length;
        for (int appWidgetId : appWidgetIds) {
            Intent intentUpdate = new Intent(context, LMSWidget.class);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lmswidget);
            intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] idArray = new int[]{appWidgetId};
            intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
            PendingIntent pendingUpdate = PendingIntent.getBroadcast(
                    context, appWidgetId, intentUpdate,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.id_widget,pendingUpdate);
            Intent serviceIntent = new Intent(context, WidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


    private static RemoteViews updateWidgetListView(Context context, int appWidgetId, String title, String result) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.lmswidget);

        Intent svcIntent = new Intent(context, WidgetService.class);
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        remoteViews.setTextViewText(R.id.id_widget_name, title);
        remoteViews.setTextViewText(R.id.id_widget_leave_balance_casual, result);

        return remoteViews;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName widget = new ComponentName(context,LMSWidget.class);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.lmswidget);
        AppWidgetManager.getInstance(context).updateAppWidget(widget,remoteViews);
        super.onReceive(context, intent);
        if (intent.getAction().equals(WIDGET_UPDATE_ACTION)) {
            int appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            String name = intent.getStringExtra(context.getString(R.string.name));
            String result = intent.getStringExtra(context.getString(R.string.result));
            AppWidgetManager appWidgetManager = AppWidgetManager
                    .getInstance(context);
            remoteViews = updateWidgetListView(context, appWidgetId, name, result);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }

    }
    public static void updateAppWidget(Context context,Intent intent){
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        String name = intent.getStringExtra(context.getString(R.string.name));
        String result = intent.getStringExtra(context.getString(R.string.result));
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        RemoteViews remoteViews = updateWidgetListView(context, appWidgetId, name, result);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

}
