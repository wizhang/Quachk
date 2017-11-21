package net.quachk.quachk;

import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import net.quachk.quachk.Utility.GameScreenConstants;
import net.quachk.quachk.Utility.GameScreenUtility;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class GameScreenTest {
    @Test
    public void CheckForPointsTest() throws Exception {
        Random r = new Random();
        LatLng position = new LatLng(0, 0);

        List<LatLng> points = new ArrayList<>();
        for (int i = 0; i < r.nextInt(100); i++) {
            points.add(position);
        }

        // these points should not be counted
        List<LatLng> pointsOutOfBounds = new ArrayList<>();
        LatLng outOfBoundsPosition = new LatLng(position.latitude + GameScreenConstants.SCAN_RADIUS + 1,
                position.longitude + GameScreenConstants.SCAN_RADIUS + 1);
        for (int i = 0; i < r.nextInt(100); i++) {
            points.add(outOfBoundsPosition);
            pointsOutOfBounds.add(outOfBoundsPosition);
        }

        List<LatLng> pointsInBounds = GameScreenUtility.checkForPoints(position, points);
        int actual = pointsInBounds.size();
        int expected = points.size() - pointsOutOfBounds.size();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void GenerateBoundsTest() throws Exception {
        LatLng center = new LatLng(0, 0);
        int radius = 5;
        LatLngBounds expected = new LatLngBounds(new LatLng(-radius, -radius),
                new LatLng(radius, radius));

        LatLngBounds actual = GameScreenUtility.generateBounds(center, radius);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void GeneratePointsTest() throws Exception {
        int numberOfPoints = new Random().nextInt(100);
        List<LatLng> points = new ArrayList<>();
        LatLngBounds bounds = new LatLngBounds(
                new LatLng(-5, -5), new LatLng(5, 5));

        GameScreenUtility.generatePoints(numberOfPoints, points, bounds);
    }
}
