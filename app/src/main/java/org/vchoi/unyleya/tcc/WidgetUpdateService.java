package org.vchoi.unyleya.tcc;

import java.util.Random;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class WidgetUpdateService extends Service {

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private Intent lastIntent;
    private RacionamentoUtil racionamentoUtil;
    private int sequencia;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.lastIntent = intent;
        racionamentoUtil = new RacionamentoUtil();
        sequencia = 0;

        // Todos os widgets são atualizados com os mesmos dados de localização.
        mFusedLocationClient = LocationServices.
                getFusedLocationProviderClient(getApplicationContext());

        // Atualiza os widgets quando o serviço é iniciado
        updateWidgetsWithLastLocation();

        // Configura atualizações regulares de localização, passando um callback que atualiza
        // os widgets a cada nova posição.
        // Tempo de atualização: produção: 60s/10s, debug: 1s/1s
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Update with location data
                    updateAllWidgets(location);
                }
            }
        };

        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null /* Looper */);

        return START_NOT_STICKY;
    }

    public void updateWidgetsWithLastLocation() {
        // Atualiza os widgets quando o serviço é iniciado
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        updateAllWidgets(location);
                    }
                });
    }

    public void updateAllWidgets(Location location) {

        int[] allWidgetIds = lastIntent
                .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());

        // Dados para atualizar o widget
        final String codArea = racionamentoUtil.getCodigoRacionamento(location);
        final String estadoAbastecimento = racionamentoUtil.getEstadoAbastecimento(location);
        int corTexto;

        sequencia++;
        if (sequencia > 99)
            sequencia = 0;

        switch (estadoAbastecimento) {
            case RacionamentoUtil.ESTADO_EM_ESTABILIZACAO:
                corTexto = Color.parseColor("#ff8c00");
                break;

            case RacionamentoUtil.ESTADO_ESTABILIZADO:
                corTexto = Color.parseColor("#4bf442");
                break;

            case RacionamentoUtil.ESTADO_INTERROMPIDO:
                corTexto = Color.parseColor("#f44242");
                break;

            case RacionamentoUtil.ESTADO_DESCONHECIDO:
            default:
                corTexto = Color.parseColor("#444444");
                break;
        }

        // Set the text
        StringBuilder texto = new StringBuilder();

//        texto.append("(");
//        texto.append(String.valueOf(sequencia));
//        texto.append(")  ");
        texto.append("Área: ");
        texto.append(codArea);
        texto.append("\n");
        texto.append("Abastecimento ");
        texto.append(estadoAbastecimento.toLowerCase());

        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(this
                    .getApplicationContext().getPackageName(),
                    R.layout.widget_layout);

            remoteViews.setTextViewText(R.id.tvAppWidget, texto.toString());
            remoteViews.setTextColor(R.id.tvAppWidget, corTexto);

            // Configura o widget para iniciar DebugActivity quando for clicado
            Intent intent = new Intent(this, DebugActivity.class);
            PendingIntent pendingIntent = PendingIntent.
                    getActivity(this, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.tvAppWidget, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    // don't provide binding, so return null
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "WidgetUpdateService stopped", Toast.LENGTH_SHORT).show();
    }
}