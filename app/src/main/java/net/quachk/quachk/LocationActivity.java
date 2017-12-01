package net.quachk.quachk;

import android.location.Location;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import net.quachk.quachk.Utility.LocationController;

/**
 * Created by Elijah on 11/6/2017.
 */

public class LocationActivity extends BaseActivity {

    private LocationController mLocationController;

    public LocationActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationController = new LocationController(this, this);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        getLocationController().destroy();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mLocationController.requestLocationUpdates();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    public LocationController getLocationController(){
        return this.mLocationController;
    }

    public void onLocationChanged(Location location) {

    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void onProviderEnabled(String provider) {

    }

    public void onProviderDisabled(String provider) {

    }

}
