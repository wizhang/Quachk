package net.quachk.quachk.Utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import net.quachk.quachk.LocationActivity;

/**
 * Created by Elijah on 11/6/2017.
 */

public class LocationController {
    private LocationManager locationManager;

    private LocationListener _locationListener;

    private boolean _isEnabled = false;

    private Context mCtxt;
    private LocationActivity mActivity;

    public LocationController(Context ctxt, LocationActivity activity){

        this.mCtxt = ctxt;
        this.mActivity = activity;

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, 1);
            return;
        }


        locationManager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);

        _locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                getActivity().onLocationChanged(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                getActivity().onStatusChanged(provider, status, extras);
            }

            @Override
            public void onProviderEnabled(String provider) {
                getActivity().onProviderEnabled(provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                getActivity().onProviderDisabled(provider);
            }
        };

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, (float) (0.00001), _locationListener);
            Toast toast = Toast.makeText(getContext(), "Location Updates Active", Toast.LENGTH_LONG);
            toast.show();
        }

        _isEnabled = true;
    }

    public void destroy(){
        locationManager.removeUpdates(_locationListener);
        locationManager = null;
        _isEnabled = false;
    }

    public boolean isEnabled(){
        return _isEnabled;
    }

    public Location getLastBestLocation(){
        Location locationGPS = null;
        Location locationNet = null;
        try {
            locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }catch (SecurityException e){
        }

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

    private Context getContext(){
        return this.mCtxt;
    }

    private LocationActivity getActivity(){
        return this.mActivity;
    }
}
