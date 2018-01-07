package org.vchoi.unyleya.tcc;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WidgetConfigurationActivity extends AppCompatActivity {

    int mAppWidgetId;
    AppWidgetManager appWidgetManager;

    private PermissionChecker permissionChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_configuration);

        permissionChecker = new PermissionChecker(this);

        // Pega o ID do app widget que está sendo configurado
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // Pega o app widget manager
        appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (permissionChecker.hasPermission() == true) {
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);

            // Inicia o serviço do widget
            // Get all ids
            ComponentName thisWidget = new ComponentName(this,
                    WidgetProvider.class);
            int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
            Intent serviceIntent = new Intent(this.getApplicationContext(),
                    WidgetUpdateService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);
            this.startService(serviceIntent);

            setResult(RESULT_OK, resultValue);
            finish();
        }
        else {
            permissionChecker.askForPermission();
        }
    }
}
