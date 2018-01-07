package org.vchoi.unyleya.tcc;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Classe de suporte para fazer as verificações de permissões de uso do GPS.
 */

public class PermissionChecker {

    Activity a;

    public PermissionChecker(Activity activity) {

        a = activity;
    }

    public boolean hasPermission() {
        return ContextCompat.checkSelfPermission(a.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void askForPermission() {

        if (hasPermission()) {
            return;
        } else {
            Intent intent = new Intent(a, AskPermissionActivity.class);
            a.startActivity(intent);
        }
    }
}
