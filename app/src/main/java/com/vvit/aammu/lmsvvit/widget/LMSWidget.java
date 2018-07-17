package com.vvit.aammu.lmsvvit.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import com.vvit.aammu.lmsvvit.R;

public class LMSWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String widgetText = LMSWidgetConfigureActivity.loadLeavePref(context, appWidgetId);
        String widgetTitle = LMSWidgetConfigureActivity.loadTitlePref(context,appWidgetId);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lmswidget);
        views.setTextViewText(R.id.id_widget_leave_balance_casual, widgetText);
        views.setTextViewText(R.id.id_widget_name,widgetTitle);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            LMSWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }


}

