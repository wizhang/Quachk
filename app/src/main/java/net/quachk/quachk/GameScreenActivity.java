package net.quachk.quachk;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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
import net.quachk.quachk.App;
import net.quachk.quachk.Models.PartyStatus;
import net.quachk.quachk.Models.PartyUpdate;
import net.quachk.quachk.Models.PublicPlayer;
import net.quachk.quachk.Utility.GameScreenConstants;
import net.quachk.quachk.Utility.GameScreenUtility;
import net.quachk.quachk.Utility.PlayerUpdater;

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

    private ScheduledExecutorService mExecutorService = Executors.newScheduledThreadPool(1);

    final Handler mExecutorHandler = new Handler();

    private static TextView mTimeLimit;
    private static TextView mPointsTextView;
    private static long endTime;
    private static final DateFormat formatter = new SimpleDateFormat("mm:ss");

    private static GoogleMap mMap;
    private static float mMapZoom;
    private static LatLng mCenter;
    private static List<PublicPlayer> mPlayerList = new ArrayList<>();
    private static List<PublicPlayer> mRunners = new ArrayList<>();
    private static List<LatLng> mTaggers = new ArrayList<>();
    private static List<LatLng> mPointMarkers = new ArrayList<>();
    private static LatLngBounds mBounds;

    private static boolean isTagged;

    private static PlayerUpdater mPlayerUpdater;

    private Runnable update = new Runnable() {
        @Override
        public void run() {
            mPlayerUpdater.updatePlayer();

            if (App.GAME.CURRENT_PLAYER.getIsTagged() != null &&
                    isTagged != App.GAME.CURRENT_PLAYER.getIsTagged()) {
                // player got tagged
                Toast message = Toast.makeText(GameScreenActivity.this,
                        "You got tagged!", Toast.LENGTH_SHORT);
                message.setGravity(Gravity.TOP, 0, 0);
                message.show();
                isTagged = true;
            }

            //mPlayerUpdater.updatePlayer(); //send the player's information to the server

            //Update Location, Even if Location Changed wasn't fired.
            Location current = getLocationController().getLastBestLocation();
            App.GAME.CURRENT_PLAYER.setLatitude(current.getLatitude());
            App.GAME.CURRENT_PLAYER.setLongitude(current.getLongitude());

            drawMap(); //Update player position on the map.

            Log.d("Game Activity", "Runnable is being called!");

            updateTextViews();

            mExecutorHandler.postDelayed(update, 1000);
        }
    };

    /** Update TextViews on the main thread */
    private void updateTextViews() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(App.GAME.CURRENT_PARTY.getEndTime() != null) {
                    if ((long) App.GAME.CURRENT_PARTY.getEndTime() - System.currentTimeMillis() <= 0) {
                        endGame();
                    }
                    mTimeLimit.setText(formatter.format(new Date(endTime - System.currentTimeMillis())));
                }
                Log.d("Game Activity", "Update Text Views is being called!");
                updatePoints();
            }
        });
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

        mPlayerUpdater = new PlayerUpdater();

        /*if (App.GAME.CURRENT_PLAYER.getScore() == 0) {
            App.GAME.CURRENT_PLAYER.setScore(GameScreenConstants.DEFAULT_START_POINTS);
        }*/
        mPointsTextView = findViewById(R.id.Points);
        //mExecutorService.scheduleAtFixedRate(update, 0, 1, TimeUnit.SECONDS);
        mExecutorHandler.postDelayed(update, 1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapZoom = 0.0f;

        mTimeLimit = findViewById(R.id.TimeLimitIndicator);
        if(App.GAME.CURRENT_PARTY != null && App.GAME.CURRENT_PARTY.getEndTime() != null) {
            endTime = (long) App.GAME.CURRENT_PARTY.getEndTime();
            mTimeLimit.setText(formatter.format(new Date(endTime - System.currentTimeMillis())));
        }else{
            Log.d("start game", "game is infinite (or until all players are tagged");
            mTimeLimit.setText("");
        }

        updatePoints();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mExecutorService.shutdownNow();
    }

    /** end the game and go to scores screen */
    private void endGame() {
        Intent endGame = new Intent(this, EndScreenActivity.class);
        startActivity(endGame);
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
                if (response.body() != null) {
                    Log.d("response body", "not null");
                } else {
                    Log.d("response body", "null");
                }
            }

            @Override
            public void onFailure(Call<Party> call, Throwable t) {
                Log.d("GameScreenActivity", "failed to update party");
            };
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        App.GAME.CURRENT_PLAYER.setLatitude(location.getLatitude());
        App.GAME.CURRENT_PLAYER.setLongitude(location.getLongitude());
        try{
            mPlayerUpdater.updatePlayer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        drawMap();
        Log.w("onLocationChanged", "Location Updated:"); //Remove once we have verified that location is updating.
    }

    private void scan() {
        LatLng currentLocation = new LatLng(
                (double)App.GAME.CURRENT_PLAYER.getLatitude(),
                (double)App.GAME.CURRENT_PLAYER.getLongitude());
        LatLngBounds scanBounds = GameScreenUtility.generateBounds(currentLocation, GameScreenConstants.SCAN_RADIUS);

        // Check if the player has enough points
        if (App.GAME.CURRENT_PLAYER.getScore() >= GameScreenConstants.SCAN_COST) {
            List<LatLng> points = GameScreenUtility.checkForPoints(currentLocation, mPointMarkers);
            for (LatLng point : points) {
                mPointMarkers.remove(point);
                //App.GAME.CURRENT_PLAYER.setScore(App.GAME.CURRENT_PLAYER.getScore() + GameScreenConstants.POINT_REWARD);
            }

            PartyUpdate pUpdate = new PartyUpdate();
            pUpdate.setPlayer(App.GAME.CURRENT_PLAYER);
            pUpdate.setParty(App.GAME.CURRENT_PARTY);
            final LatLngBounds llb = scanBounds;
            network().scanParty(pUpdate).enqueue(new Callback<PartyStatus>() {
                @Override
                public void onResponse(Call<PartyStatus> call, Response<PartyStatus> response) {
                    Log.d("Game Activity", "Scan Response: " + response.body().getPlayer().getUsername());
                    App.GAME.CURRENT_PLAYER = response.body().getPlayer();
                    if(App.GAME.CURRENT_PLAYER.getIsTagged()) {
                        refreshMap(); // update runners/taggers lists
                        checkForRunners(llb);
                    }
                }

                @Override
                public void onFailure(Call<PartyStatus> call, Throwable t) {

                }
            });

            /*if (isTagged) {
                refreshMap(); // update runners/taggers lists
                checkForRunners(scanBounds);
            }*/
            refreshMap();
            drawMap();
            //App.GAME.CURRENT_PLAYER.setScore(App.GAME.CURRENT_PLAYER.getScore() - GameScreenConstants.SCAN_COST);
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

    /** Update the points text view */
    private void updatePoints() {
        Log.d("Game Activity", "Update Points is being called!");
        mPointsTextView.setText(Integer.toString(
                App.GAME.CURRENT_PLAYER.getScore()));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        initializeMap(googleMap);
    }

    public void initializeMap(GoogleMap map) {
        mMap = map;

        //mCenter = new LatLng(39.999475, -83.013086); // get party leader's position from server
        if(mPlayerList == null)
            return;
        PublicPlayer leader = null;
        for(PublicPlayer p : mPlayerList){
            if(p.getPlayerId() == App.GAME.CURRENT_PARTY.getPartyLeaderId()) {
                leader = p;
                break;
            }
        }
        if(leader == null && App.GAME.CURRENT_PLAYER.getPlayerId() == App.GAME.CURRENT_PARTY.getPartyLeaderId()){
            mCenter = new LatLng((double)(App.GAME.CURRENT_PLAYER.getLatitude()), (double)(App.GAME.CURRENT_PLAYER
                    .getLongitude()));
        }else {
            mCenter = new LatLng((double) leader.getLatitude(), (double) leader.getLongitude());
        }

        mBounds = GameScreenUtility.generateBounds(mCenter, GameScreenConstants.MAP_RADIUS);
        GameScreenUtility.generatePoints(100, mPointMarkers, mBounds); // Generates the bonus points scattered around the map

        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int i) {
                mMapZoom = mMap.getCameraPosition().zoom;
            }
        });

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
                        mPlayerList = playerList;
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
     * Draws player's current position
     */
    private static void drawMap() {
        if (mMap != null) {
            mMap.clear();

            for (LatLng point : mPointMarkers) {
                mMap.addMarker(new MarkerOptions().position(point));
            }

            for (PublicPlayer runner : mRunners) {
                if(runner.getPlayerId().equals(App.GAME.CURRENT_PLAYER.getPlayerId()))
                    continue;
                LatLng runnerPostion = new LatLng((double) runner.getLatitude(),
                        (double) runner.getLongitude());
                mMap.addCircle(new CircleOptions().radius(GameScreenConstants.CIRCLE_SIZE)
                        .fillColor(Color.BLUE).strokeWidth(0).center(runnerPostion));
            }

            for (LatLng tagger : mTaggers) {
                mMap.addCircle(new CircleOptions().radius(GameScreenConstants.CIRCLE_SIZE)
                        .fillColor(Color.RED).strokeWidth(0).center(tagger));
            }

            // draw current location
            LatLng currentLocation = new LatLng((double) App.GAME.CURRENT_PLAYER.getLatitude(),
                    (double) App.GAME.CURRENT_PLAYER.getLongitude());
            mMap.addCircle(new CircleOptions().center(currentLocation).fillColor(Color.BLACK)
                    .strokeColor(Color.BLUE).radius(2.5f).strokeWidth(0));

            mMap.setLatLngBoundsForCameraTarget(new LatLngBounds(mCenter, mCenter));
            if(mMapZoom == 0.0f)
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 0));
            else
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        }
    }
}
