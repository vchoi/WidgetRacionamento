package org.vchoi.unyleya.tcc;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AskPermissionActivity extends AppCompatActivity {

    private int delay = 0;
    final private int delayStep = 500;
    final private int delayMax = 3000;

    PermissionChecker permissionChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_permission);

        permissionChecker = new PermissionChecker(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    private void checkPermissions() {
        int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

        if (permissionChecker.hasPermission()) {
            setResult(RESULT_OK);
            finish();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

}
