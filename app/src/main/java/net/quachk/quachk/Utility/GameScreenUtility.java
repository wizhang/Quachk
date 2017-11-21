package net.quachk.quachk.Utility;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import net.quachk.quachk.App;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by quachk on 11/20/17.
 */

public class GameScreenUtility {
    /** Check to see if there are any points in the user's scan area */
    public static List<LatLng> checkForPoints(LatLng currentLocation, List<LatLng> pointMarkers) {
        LatLngBounds scanBounds = generateBounds(currentLocation, GameScreenConstants.SCAN_RADIUS);
        List<LatLng> pointsFound = new ArrayList<>();
        for (int i = pointMarkers.size() - 1; i >= 0; i--) {
            LatLng point = pointMarkers.get(i);
            if (scanBounds.contains(point)) {
                pointsFound.add(point);
            }
        }
        return pointsFound;
    }

    /**
     * Generates bounds for the map based on the center (team leader's location)
     */
    public static LatLngBounds generateBounds(LatLng center, double radius) {
        return new LatLngBounds(
                new LatLng(center.latitude - radius,
                        center.longitude - radius),
                new LatLng(center.latitude + radius,
                        center.longitude + radius));
    }

    /**
     * Generates a set of random LatLng and stores them in the specified list.
     * Each point will be contained within bounds specified by the bounds parameter
     */
    public static void generatePoints(int numberOfPoints, List<LatLng> points, LatLngBounds bounds) {
        Random r = new Random();

        LatLng northeast = bounds.northeast;
        LatLng southwest = bounds.southwest;

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
}
