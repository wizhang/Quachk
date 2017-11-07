package net.quachk.quachk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.quachk.quachk.Utility.LocationController;


public class GameScreenActivity extends LocationActivity implements OnMapReadyCallback {

    private Handler mHandler = new Handler();
    private TextView mTimeLimit;
    private TextView mPoints;
    private long endTime;
    private static final DateFormat formatter = new SimpleDateFormat("mm:ss");

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
        Log.w("Loc", "Location Updated:"); //Remove once we have verified that location is updating.
    }

    private void scan() {
        GameMapElements.refreshMap();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GameMapElements.initializeMap(googleMap);
    }

}
