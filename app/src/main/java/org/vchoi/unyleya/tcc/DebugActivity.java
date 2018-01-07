package org.vchoi.unyleya.tcc;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;


public class DebugActivity extends AppCompatActivity {

    static final String CONSOLE_TEXT = "consoleText";

    private TextView tvDebug;
    private boolean initComplete, locationEnabled;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    PermissionChecker permissionChecker;
    RacionamentoUtil racionamentoUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        tvDebug = (TextView) findViewById(R.id.tvDebug);
        initComplete = false;

        permissionChecker = new PermissionChecker(this);
        racionamentoUtil = new RacionamentoUtil();

        tvDebug.setMovementMethod(new ScrollingMovementMethod());

        if (savedInstanceState == null) {
            // no saved state, new activity
            tvDebug.setText("");
        }
        else {
            tvDebug.setText(savedInstanceState.getString(CONSOLE_TEXT));
        }

        log("Verificando permissões");
        if (permissionChecker.hasPermission() == false) {
            locationEnabled = false;
            permissionChecker.askForPermission();
            return;
        } else {
            locationEnabled = true;
        }

        initLocationServices();

        initComplete = true;
    }

    private void log(String s) {
        tvDebug.append("\n" + s);
    }


    private void initLocationServices () {
        log("initLocationServices()");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    printLocation(location);
                }
            }

        };

        startLocationUpdates();

        // get initial position
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        printLocation(location);
                    }
                });

    }

    private void startLocationUpdates() {
        log("startLocationUpdates();");
        if (permissionChecker.hasPermission() == false) {
            return;
        }
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
    }

    private void stopLocationUpdates() {
        log("stopLocationUpdates();");
        if (permissionChecker.hasPermission() == false) {
            return;
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public void printLocation(Location location) {
        // Got last known location. In some rare situations this can be null.
        if (location != null) {
            // Logic to handle location object
            LatLng marker = new LatLng(location.getLatitude(),
                    location.getLongitude());
            Double lat = marker.latitude;
            Double lon = marker.longitude;
            Float accuracy = location.getAccuracy();
            log("\nlocation: " + lat.toString() + ", " + lon.toString() +
                    " ("+ accuracy.toString() +"m)");
            String codRacionamento = racionamentoUtil.getCodigoRacionamento(location);
            if (codRacionamento != null)
                log("codRacionamento: " + codRacionamento);
            log("Abastecimento " + racionamentoUtil.getEstadoAbastecimento(codRacionamento));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        savedInstanceState.putCharSequence(CONSOLE_TEXT, tvDebug.getText());

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}
