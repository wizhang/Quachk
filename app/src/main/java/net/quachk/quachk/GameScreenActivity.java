package net.quachk.quachk;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

import net.quachk.quachk.Utility.LocationController;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class GameScreenActivity extends LocationActivity implements OnMapReadyCallback {

    private TextView mTimeLimit;
    private TextView mPoints;

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
        mTimeLimit.setText("15:00");

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
