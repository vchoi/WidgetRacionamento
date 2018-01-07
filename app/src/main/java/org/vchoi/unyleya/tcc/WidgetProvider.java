package org.vchoi.unyleya.tcc;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {

            // Inicia o serviço do widget
            // Get all ids
            ComponentName thisWidget = new ComponentName(context,
                    WidgetProvider.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            Intent serviceIntent = new Intent(context.getApplicationContext(),
                    WidgetUpdateService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
            context.startService(serviceIntent);
        }
    }

    @Override
    public void onDisabled(Context context) {
        // Desliga o serviço quando o último widget for removido

        Intent serviceIntent = new Intent(context.getApplicationContext(),
                WidgetUpdateService.class);

        context.getApplicationContext().stopService(serviceIntent);
    }
}



