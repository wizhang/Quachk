package net.quachk.quachk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.quachk.quachk.Models.Game;
import net.quachk.quachk.Models.PublicPlayer;
import net.quachk.quachk.Utility.LocationController;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GameScreenActivity extends LocationActivity implements OnMapReadyCallback {

    private Handler mHandler = new Handler();
    private TextView mTimeLimit;
    private TextView mPoints;
    private long endTime;
    private static final DateFormat formatter = new SimpleDateFormat("mm:ss");
    private static final int CIRCLE_SIZE = 15;
    private static final double MAP_RADIUS = 0.005;

    private static boolean initialized = false;
    private static GoogleMap mMap;
    private static LatLng mCenter;
    private static List<LatLng> mRunners = new ArrayList<>();
    private static List<LatLng> mTaggers = new ArrayList<>();
    private static List<LatLng> mPointMarkers = new ArrayList<>();
    private static LatLngBounds mBounds;

    private Runnable mUpdateTime = new Runnable() {
        @Override
        public void run() {
            mTimeLimit.setText(formatter.format(new Date(endTime - System.currentTimeMillis())));
            mHandler.postDelayed(mUpdateTime, 1000);
        }
    };

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

        mTimeLimit = findViewById(R.id.TimeLimitIndicator);
        endTime = System.currentTimeMillis() + 15 * 60 * 1000; // get/set this on the server
        mTimeLimit.setText(formatter.format(new Date(endTime - System.currentTimeMillis())));
        mHandler.post(mUpdateTime);

        mPoints = findViewById(R.id.Points);
        mPoints.setText("1000");

        findViewById(R.id.ScanButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
    }
    
    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        
        //When user has changed location, update it on webserver
        App.GAME.CURRENT_PLAYER.setLatitude(location.getLatitude());
        App.GAME.CURRENT_PLAYER.setLongitude(location.getLongitude());
        try{
            network().checkPartyStatus((String) App.GAME.CURRENT_PARTY.getPartyCode(), App.GAME.CURRENT_PLAYER).enqueue(new Callback<PartyStatus>() {
                @Override
                public void onResponse(Call<PartyStatus> call, Response<PartyStatus> response) {
                    PartyStatus partyStatus = null;
                    partyStatus = response.body();
 
                    if(partyStatus != null){
                        //Success! We have updated player information
                    }
                    else {
                        Log.d("GameScreenActivity", "Error updating current player location");
                    }
                }
 
                @Override
                public void onFailure(Call<PartyStatus> call, Throwable t) {
                    hideLoading();
                    // Give Some Kind Of Error Update (The response should have some kind of error if it was server side).
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        Log.w("Loc", "Location Updated:"); //Remove once we have verified that location is updating.
    }

    private void scan() {
        refreshMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        initializeMap(googleMap);
    }

    public void initializeMap(GoogleMap map) {
        mMap = map;

        mCenter = new LatLng(39.999475, -83.013086); // get party leader's position from server
        generateMapBounds(mCenter);

        refreshMap();

        drawMap();

        initialized = true;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * Refreshes the locations of the points stored in the map including
     * points, runners, and taggers
     *
     * Called when user clicks "Scan"
     */
    public void refreshMap() {

        try{
            network().fetchPlayersInParty((String) App.GAME.CURRENT_PARTY.getPartyCode(), App.GAME.CURRENT_PLAYER).enqueue(new Callback<List<PublicPlayer>>() {
                @Override
                public void onResponse(Call<List<PublicPlayer>> call, Response<List<PublicPlayer>> response) {
                    List<PublicPlayer> playerList = null;
                    playerList = response.body();

                    if(playerList != null){
                        //Success! We have retrieved other player information
                        mRunners.clear();
                        mTaggers.clear();
                        for (PublicPlayer player: playerList){
                            Double latitude = (Double) player.getLatitude();
                            Double longitude = (Double) player.getLongitude();
                            if (player.getIsTagged()){
                                mTaggers.add(new LatLng(latitude, longitude));
                            }
                            else{
                                mRunners.add(new LatLng(latitude, longitude));
                            }
                        }
                        App.GAME = new Game();
                    }
                    else {
                        Log.d("GameScreenActivity", "Error retrieving other player locations");
                    }
                }

                @Override
                public void onFailure(Call<List<PublicPlayer>> call, Throwable t) {
                    hideLoading();
                    // Give Some Kind Of Error Update (The response should have some kind of error if it was server side).
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        mPointMarkers.clear();
        generatePoints(100, mPointMarkers); // get from server

        drawMap();
    }

    /**
     * Draws the map with indicators for points, runners, and taggers
     */
    private static void drawMap() {
        mMap.clear();

        for (LatLng point : mPointMarkers) {
            mMap.addMarker(new MarkerOptions().position(point));
        }

        for (LatLng runner : mRunners) {
            mMap.addCircle(new CircleOptions().radius(CIRCLE_SIZE).fillColor(Color.BLUE).strokeWidth(0).center(runner));
        }

        for (LatLng tagger : mTaggers) {
            mMap.addCircle(new CircleOptions().radius(CIRCLE_SIZE).fillColor(Color.RED).strokeWidth(0).center(tagger));
        }

        mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(mCenter, mCenter));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 0));
    }

    /**
     * Generates a set of random LatLng and stores them in the specified list.
     * Each point will be contained within bounds specified by the mBounds field
     */
    private static void generatePoints(int numberOfPoints, List<LatLng> points) {
        Random r = new Random();

        LatLng northeast = mBounds.northeast;
        LatLng southwest = mBounds.southwest;

        double minLat = Math.min(northeast.latitude, southwest.latitude);
        double maxLat = Math.max(northeast.latitude, southwest.latitude);
        double minLng = Math.min(northeast.longitude, southwest.longitude);
        double maxLng = Math.max(northeast.longitude, southwest.longitude);

        while (--numberOfPoints >= 0) { // add points to server
            points.add(new LatLng(
                    minLat + r.nextDouble() * (maxLat - minLat),
                    minLng + r.nextDouble() * (maxLng - minLng)));
        }
    }

    /**
     * Generates bounds for the map based on the center (team leader's location)
     */
    private static void generateMapBounds(LatLng center) {
        mBounds = new LatLngBounds(
                new LatLng(center.latitude - MAP_RADIUS, center.longitude - MAP_RADIUS),
                new LatLng(center.latitude + MAP_RADIUS, center.longitude + MAP_RADIUS));
    }

}
