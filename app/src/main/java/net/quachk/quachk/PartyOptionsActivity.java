package net.quachk.quachk;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import net.quachk.quachk.Models.Game;
import net.quachk.quachk.Models.Party;
import net.quachk.quachk.Network.Network;
import net.quachk.quachk.Network.PlayerApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PartyOptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_party_options);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.StartPartyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startParty();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        UpdatePlayerName(App.GAME.CURRENT_PLAYER.getUsername());
    }


    public void UpdatePlayerName(String player){
        ((TextView)findViewById(R.id.CurrentPlayerName)).setText(player);
    }

    public void startParty(){
        Retrofit restAdapter = new Retrofit.Builder().baseUrl(Network.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        PlayerApi task = restAdapter.create(PlayerApi.class);

        //Remove next 2 lines once turned into Async Task
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Party p = null;

        try {
            p = task.startParty(App.GAME.CURRENT_PLAYER).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        App.GAME.CURRENT_PARTY = p;
        Log.w("app", "App Player Name:" + App.GAME.CURRENT_PLAYER.getUsername() + " App Player Hash: " + App.GAME.CURRENT_PLAYER.getSessionHash());
        //Log.w("app", "App Game:" + (App.GAME == null) + " App Player: " + (App.GAME.CURRENT_PLAYER == null));
        if(App.GAME.CURRENT_PARTY != null)
            openPartyDetails();
    }

    private void openPartyDetails(){
        Intent i = new Intent(this, PartyDetailsActivity.class);
        startActivity(i);
    }
}
