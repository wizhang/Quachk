package net.quachk.quachk;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import net.quachk.quachk.Models.Party;
import net.quachk.quachk.Models.PartyStatus;
import net.quachk.quachk.Models.PartyUpdate;
import net.quachk.quachk.Models.PublicPlayer;
import net.quachk.quachk.Utility.GameScreenConstants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GameScreenActivity extends LocationActivity implements OnMapReadyCallback {

    private static ScheduledExecutorService mExecutorService = Executors.newScheduledThreadPool(1);
    private static TextView mTimeLimit;
    private static TextView mPointsTextView;
    private static long endTime;
    private static final DateFormat formatter = new SimpleDateFormat("mm:ss");
    private static int mPoints;

    private static GoogleMap mMap;
    private static LatLng mCenter;
    private static List<PublicPlayer> mRunners = new ArrayList<>();
    private static List<LatLng> mTaggers = new ArrayList<>();
    private static List<LatLng> mPointMarkers = new ArrayList<>();
    private static LatLngBounds mBounds;

    private static boolean isTagged;

    private Runnable update = new Runnable() {
        @Override
        public void run() {
            Log.d("update", "Runnable called");
            if (App.GAME.CURRENT_PARTY.getPointSecond() != null) {
                mPoints += App.GAME.CURRENT_PARTY.getPointSecond();
            } else {
                mPoints += GameScreenConstants.DEFAULT_POINTS_PER_SECOND;
            }

            if (App.GAME.CURRENT_PLAYER.getIsTagged() != null &&
                    isTagged != App.GAME.CURRENT_PLAYER.getIsTagged()) {
                // player got tagged
                Toast message = Toast.makeText(GameScreenActivity.this,
                        "You got tagged!", Toast.LENGTH_SHORT);
                message.setGravity(Gravity.TOP, 0, 0);
                message.show();
                isTagged = true;
            }
            updateTextViews();
            Log.d("update", "Runnable finished");
        }
    };

    /** Update TextViews on the main thread */
    private void updateTextViews() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (endTime - System.currentTimeMillis() < 0) {
                    endGame();
                }
                mTimeLimit.setText(formatter.format(new Date(endTime - System.currentTimeMillis())));
                updatePoints();
            }
        });
    }

    /** end the game and go to scores screen */
    private void endGame() {
        /** END GAME ACTIVITY INTENT HERE */
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_screen);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.MapFragment);
        mapFragment.getMapAsync(this);

        findViewById(R.id.ScanButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });
        if (App.GAME.CURRENT_PLAYER.getIsTagged() != null) {
            isTagged = App.GAME.CURRENT_PLAYER.getIsTagged();
        } else {
            isTagged = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mTimeLimit = findViewById(R.id.TimeLimitIndicator);
        if (App.GAME.CURRENT_PARTY.getEndTime() == null) {
            Log.d("start game", "setting the end time");
            App.GAME.CURRENT_PARTY.setEndTime(System.currentTimeMillis() + 30 * 1000); // the party leader will set this
            updateParty(); // push endtime to the server
        }
        endTime = (long) App.GAME.CURRENT_PARTY.getEndTime();

        mTimeLimit.setText(formatter.format(new Date(endTime - System.currentTimeMillis())));

        if (App.GAME.CURRENT_PLAYER.getScore() == null) {
            mPoints = App.GAME.CURRENT_PLAYER.getScore();
        } else {
            mPoints = GameScreenConstants.DEFAULT_START_POINTS;
        }
        mPointsTextView = findViewById(R.id.Points);
        updatePoints();
        mExecutorService.scheduleAtFixedRate(update, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mExecutorService.shutdownNow();
        finish();
    }

    private void updateParty() {
        PartyUpdate update = new PartyUpdate();
        update.setPlayer(App.GAME.CURRENT_PLAYER);
        update.setParty(App.GAME.CURRENT_PARTY);
        network().updatePartySettings(update).enqueue(new Callback<Party>() {
            @Override
            public void onResponse(Call<Party> call, Response<Party> response) {
                Log.d("GameScreenActivity", "updated party successfully");
            }

            @Override
            public void onFailure(Call<Party> call, Throwable t) {
                Log.d("GameScreenActivity", "failed to update party");
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        App.GAME.CURRENT_PLAYER.setLatitude(location.getLatitude());
        App.GAME.CURRENT_PLAYER.setLongitude(location.getLongitude());
        try{
            network().checkPartyStatus((String) App.GAME.CURRENT_PARTY.getPartyCode(),
                    App.GAME.CURRENT_PLAYER).enqueue(new Callback<PartyStatus>() {
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
        LatLng currentLocation = new LatLng(
                (double)App.GAME.CURRENT_PLAYER.getLatitude(),
                (double)App.GAME.CURRENT_PLAYER.getLongitude());
        LatLngBounds scanBounds = generateBounds(currentLocation, GameScreenConstants.SCAN_RADIUS);

        // check to see if the player has enough points
        if (mPoints >= GameScreenConstants.SCAN_COST) {
            checkForPoints(scanBounds);
            if (isTagged) {
                refreshMap(); // update runners/taggers lists
                checkForRunners(scanBounds);
            }
            refreshMap();
            drawMap();
            mPoints -= GameScreenConstants.SCAN_COST;
            updatePoints();
        } else {
            Toast message = Toast.makeText(GameScreenActivity.this,
                    "You do not have enough points!", Toast.LENGTH_SHORT);
            message.setGravity(Gravity.TOP, 0, 0);
            message.show();
        }
    }

    private void checkForRunners(LatLngBounds scanBounds) {
        for (int i = mRunners.size() - 1; i >= 0; i--) {
            PublicPlayer runner = mRunners.get(i);
            LatLng runnerPosition = new LatLng((double)runner.getLatitude(),
                    (double)runner.getLongitude());
            if (scanBounds.contains(runnerPosition)) {
                // tag the runner and update the server
            }
        }
    }

    /** Check to see if there are any points in the user's scan area */
    private void checkForPoints(LatLngBounds scanBounds) {
        for (int i = mPointMarkers.size() - 1; i >= 0; i--) {
            LatLng point = mPointMarkers.get(i);
            if (scanBounds.contains(point)) {
                mPoints += GameScreenConstants.POINT_REWARD;
                mPointMarkers.remove(i);
            }
        }
    }

    /** Update the points text view */
    private void updatePoints() {
        mPointsTextView.setText(Integer.toString(mPoints));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        initializeMap(googleMap);
    }

    public void initializeMap(GoogleMap map) {
        mMap = map;

        mCenter = new LatLng(39.999475, -83.013086); // get party leader's position from server
        mBounds = generateBounds(mCenter, GameScreenConstants.MAP_RADIUS);
        generatePoints(100, mPointMarkers); // The bonus points scattered around the map

        refreshMap();
        drawMap();
    }

    /**
     * Refreshes the locations of the runners and taggers
     *
     * Called when user clicks "Scan"
     */
    public void refreshMap() {
        try{
            network().fetchPlayersInParty((String) App.GAME.CURRENT_PARTY.getPartyCode(),
                    App.GAME.CURRENT_PLAYER).enqueue(new Callback<List<PublicPlayer>>() {
                @Override
                public void onResponse(Call<List<PublicPlayer>> call, Response<List<PublicPlayer>> response) {
                    List<PublicPlayer> playerList = null;
                    playerList = response.body();

                    if(playerList != null){
                        //Success! We have retrieved other player information
                        mRunners.clear();
                        mTaggers.clear();
                        Log.d("Map refresh", "Number of players: " + playerList.size());
                        for (PublicPlayer player: playerList){
                            Log.d("Map refresh", "Player Name: " + player.getUsername());
                            Double latitude = (Double) player.getLatitude();
                            Double longitude = (Double) player.getLongitude();
                            Log.d("Map refresh", "Latitude: " + latitude + " Longitude: " + longitude);
                            if (latitude == null || longitude == null){
                                Log.d("Map refresh", player.getUsername() + " does not have a location saved.");
                            }
                            else {
                                if (player.getIsTagged()) {
                                    mTaggers.add(new LatLng(latitude, longitude));
                                    Log.d("Add Players", player.getUsername() + " was added to tagged players");
                                } else {
                                    mRunners.add(player);
                                    Log.d("Add Players", player.getUsername() + " was added to not tagged players");
                                }
                            }
                        }
                        Log.d("Add Players", "finished updating players");
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
    }

    /**
     * Draws the map with indicators for points, runners, and taggers
     */
    private static void drawMap() {
        mMap.clear();

        for (LatLng point : mPointMarkers) {
            mMap.addMarker(new MarkerOptions().position(point));
        }

        for (PublicPlayer runner : mRunners) {
            LatLng runnerPostion = new LatLng((double)runner.getLatitude(),
                    (double)runner.getLongitude());
            mMap.addCircle(new CircleOptions().radius(GameScreenConstants.CIRCLE_SIZE)
                    .fillColor(Color.BLUE).strokeWidth(0).center(runnerPostion));
        }

        for (LatLng tagger : mTaggers) {
            mMap.addCircle(new CircleOptions().radius(GameScreenConstants.CIRCLE_SIZE)
                    .fillColor(Color.RED).strokeWidth(0).center(tagger));
        }

        // draw current location
        LatLng currentLocation = new LatLng((double)App.GAME.CURRENT_PLAYER.getLatitude(),
                (double)App.GAME.CURRENT_PLAYER.getLongitude());
        mMap.addCircle(new CircleOptions().center(currentLocation).fillColor(Color.BLACK)
                .strokeColor(Color.BLUE).radius(30.0f).strokeWidth(10.0f));

        mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(currentLocation, currentLocation));
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
    private static LatLngBounds generateBounds(LatLng center, double radius) {
        return new LatLngBounds(
                new LatLng(center.latitude - radius,
                        center.longitude - radius),
                new LatLng(center.latitude + radius,
                        center.longitude + radius));
    }

}
