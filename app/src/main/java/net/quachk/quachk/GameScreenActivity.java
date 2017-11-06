package net.quachk.quachk;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameScreenActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng mCenter;
    private List<LatLng> mPoints;
    private List<LatLng> mRunners;
    private List<LatLng> mTaggers;
    private LatLngBounds mBounds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_screen);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.MapFragment);
        mapFragment.getMapAsync(this);

        /** Location of Team Leader */
        mCenter = new LatLng(39.999475, -83.013086);
        generateMapBounds(mCenter);

        mPoints = new ArrayList<>();
        generatePoints(100, mPoints);

        // obtain from db
        mRunners = new ArrayList<>();
        generatePoints(20, mRunners);

        // obtain from db
        mTaggers = new ArrayList<>();
        generatePoints(20, mTaggers);


    }

    private void generatePoints(int numberOfPoints, List<LatLng> points) {
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

    private void generateMapBounds(LatLng center) {
        double mapRadius = 0.005;
        mBounds = new LatLngBounds(
                new LatLng(center.latitude - mapRadius, center.longitude - mapRadius),
                new LatLng(center.latitude + mapRadius, center.longitude + mapRadius));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (LatLng runner : mRunners) {
            mMap.addCircle(new CircleOptions()
                    .center(runner)
                    .radius(10)
                    .fillColor(Color.BLUE)
                    .strokeWidth(0));
        }

        for (LatLng tagger : mTaggers) {
            mMap.addCircle(new CircleOptions()
                    .center(tagger)
                    .radius(10)
                    .fillColor(Color.RED)
                    .strokeWidth(0));
        }

        for (LatLng point : mPoints) {
            mMap.addMarker(new MarkerOptions().position(point));
        }

        mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(mCenter, mCenter));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 0));
    }
}
