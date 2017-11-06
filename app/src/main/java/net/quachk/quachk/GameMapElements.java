package net.quachk.quachk;

import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by quachk on 11/6/17.
 */

public class GameMapElements {
    private static boolean initialized = false;
    private static GoogleMap mMap;
    private static LatLng mCenter;
    private static List<LatLng> mRunners = new ArrayList<>();
    private static List<LatLng> mTaggers = new ArrayList<>();
    private static List<LatLng> mPoints = new ArrayList<>();
    private static LatLngBounds mBounds;


    public static void initializeMap(GoogleMap map) {
        mMap = map;

        mCenter = new LatLng(39.999475, -83.013086);
        generateMapBounds(mCenter);

        generatePoints(100, mPoints);

        drawMap();

        initialized = true;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void refreshMap() {
        Log.d(GameMapElements.class.toString(), "refreshMap called");
        if (initialized) {
            mPoints.clear();
            generatePoints(100, mPoints);
            drawMap();
        }
    }

    private static void drawMap() {
        mMap.clear();

        for (LatLng point : mPoints) {
            mMap.addMarker(new MarkerOptions().position(point));
        }

        mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(mCenter, mCenter));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 0));
    }

    private static void generatePoints(int numberOfPoints, List<LatLng> points) {
        Random r = new Random();

        LatLng northeast = mBounds.northeast;
        LatLng southwest = mBounds.southwest;

        double minLat = Math.min(northeast.latitude, southwest.latitude);
        double maxLat = Math.max(northeast.latitude, southwest.latitude);
        double minLng = Math.min(northeast.longitude, southwest.longitude);
        double maxLng = Math.max(northeast.longitude, southwest.longitude);

        while (--numberOfPoints >= 0) {
            points.add(new LatLng(
                    minLat + r.nextDouble() * (maxLat - minLat),
                    minLng + r.nextDouble() * (maxLng - minLng)));
        }
    }

    private static void generateMapBounds(LatLng center) {
        double mapRadius = 0.005;
        mBounds = new LatLngBounds(
                new LatLng(center.latitude - mapRadius, center.longitude - mapRadius),
                new LatLng(center.latitude + mapRadius, center.longitude + mapRadius));
    }
}
