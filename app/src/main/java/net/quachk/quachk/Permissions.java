package net.quachk.quachk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Elijah on 10/2/2017.
 */

public class Permissions {
    public static boolean INITIALIZED = false;
    public static final int INTERNET = 1;
    public static final int LOCATION = 2;

    public static void initialize(){
        App.setInternetEnabled(hasPermission(INTERNET,true));
        App.setLocationEnabled(hasPermission(LOCATION,true));
        INITIALIZED = true;
    }

    public static String[] getPermissionString(int permission){
        String[] perm = null;
        if(permission == INTERNET)
            perm = new String[]{ Manifest.permission.INTERNET };
        else if(permission == LOCATION)
            perm = new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION };
        return perm;
    }
    public static boolean hasPermission(int permission){
        String[] perm = getPermissionString(permission);
        if(perm == null)
            return false;
        for(int i = 0; i < perm.length; i++)
            if(ContextCompat.checkSelfPermission(App.getContext(), perm[i]) != PackageManager.PERMISSION_GRANTED)
                return false;
        return true;
    }

    public static boolean hasPermission(int permission, boolean request){
        String[] perm = getPermissionString(permission);
        if(!hasPermission(permission) && request) {
            App.setPermissionDialogOpen(permission, true); //set dialog status to open
            ActivityCompat.requestPermissions(App.getActivity(), perm, INTERNET);
            return false;
        }else if(hasPermission(permission)) {
            return true;
        }
        return false;
    }

    public static void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        App.setPermissionDialogOpen(requestCode, false); //set the permission dialog status to closed
        switch (requestCode) {
            case INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    App.setInternetEnabled(true);
                } else {
                    App.setInternetEnabled(false);
                }
                return;
            }

            case LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    App.setLocationEnabled(true);

                } else {
                    App.setLocationEnabled(false);
                }
                return;
            }
        }
    }
}
