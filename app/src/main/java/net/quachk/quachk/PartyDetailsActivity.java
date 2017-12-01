package net.quachk.quachk;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Debug;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import net.quachk.quachk.Adapters.PlayerListAdapter;
import net.quachk.quachk.Models.Party;
import net.quachk.quachk.Models.PartyStatus;
import net.quachk.quachk.Models.PartyUpdate;
import net.quachk.quachk.Models.Player;
import net.quachk.quachk.Models.PublicPlayer;
import net.quachk.quachk.Network.Network;
import net.quachk.quachk.Network.PlayerApi;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PartyDetailsActivity extends LocationActivity {

    private List<PublicPlayer> LIST_ITEMS;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private Runnable updatePlayersTask = new Runnable() {
        @Override
        public void run() {
            try {
                getPlayersInParty();
                Log.d("PartyDetailsActivity", "Updating party members");
                getPartyStatus();
            }
            catch (Exception e){
                Log.d("PartyDetailsActivity", "Failed updating party members");
            }
        }
    };

    private void getPartyStatus() {
        network().checkPartyStatus((String)App.GAME.CURRENT_PARTY.getPartyCode(), App.GAME.CURRENT_PLAYER)
            .enqueue(new Callback<PartyStatus>() {
                @Override
                public void onResponse(Call<PartyStatus> call, Response<PartyStatus> response) {
                    PartyStatus partyStatus = response.body();
                    if(partyStatus.getParty().getStartTime() != null) {
                        Log.d("PartyStatus", "game has started");
                        setPlayerInitialValues();
                        showGame();
                    } else {
                        Log.d("PartyStatus", "game has not started yet");
                    }
                }

                @Override
                public void onFailure(Call<PartyStatus> call, Throwable t) {
                    Log.d("PartyStatus", "failed to get party status");
                }
            });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_details);
        this.LIST_ITEMS = new ArrayList<>();

        RecyclerView list = findViewById(R.id.ListItems);
        list.setLayoutManager(new LinearLayoutManager(this));
        initListItems();
        ((TextView)findViewById(R.id.PartyCode)).setText(App.GAME.CURRENT_PARTY.getPartyCode().toString());


        if(App.GAME.CURRENT_PARTY.getPartyLeaderId().equals(App.GAME.CURRENT_PLAYER.getPlayerId()))
            getStartGameButton().setVisibility(View.GONE);

    }

    @Override
    protected void onPause(){
        super.onPause();
        executor.shutdownNow();
        Log.d("PartyDetails", "shut down executor");
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        App.GAME.CURRENT_PLAYER.setLatitude(location.getLatitude());
        App.GAME.CURRENT_PLAYER.setLongitude(location.getLongitude());
        try{
            network().checkPartyStatus((String) App.GAME.CURRENT_PARTY.getPartyCode(), App.GAME.CURRENT_PLAYER).enqueue(new Callback<PartyStatus>() {
                @Override
                public void onResponse(Call<PartyStatus> call, Response<PartyStatus> response) {
                    PartyStatus partyStatus = null;
                    partyStatus = response.body();

                    if(partyStatus != null){
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



    @Override
    protected void onStart(){
        super.onStart();
        hideFragment(getPartyLeaderOptions());
        hideFragment(getPlayerPartyOptions());
        getStartGameButton().setVisibility(View.GONE);
        if(App.GAME.CURRENT_PARTY.getPartyLeaderId() == App.GAME.CURRENT_PLAYER.getPlayerId()) {
            showFragment(getPartyLeaderOptions());
        } else {
            showFragment(getPlayerPartyOptions());
        }
        getStartGameButton().setVisibility(View.VISIBLE);
        findViewById(R.id.StartGameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });
        Log.d("PartyDetailsActivity", "Executing update players task");
        executor.scheduleAtFixedRate(updatePlayersTask, 0, 3, TimeUnit.SECONDS);
    }

    private void startGame() {
        setPlayerInitialValues();
        Location l = getLocationController().getLastBestLocation();
        App.GAME.CURRENT_PLAYER.setLatitude(l.getLatitude());
        App.GAME.CURRENT_PLAYER.setLongitude(l.getLongitude());
        PartyUpdate partyUpdate = new PartyUpdate();
        partyUpdate.setPlayer(App.GAME.CURRENT_PLAYER);
        partyUpdate.setParty(App.GAME.CURRENT_PARTY);

        final Context tCtxt = this;
        network().startGame(partyUpdate).enqueue(new Callback<Party>() {
            @Override
            public void onResponse(Call<Party> call, Response<Party> response) {
                    Party p = response.body();
                    App.GAME.CURRENT_PARTY = p;
            }

            @Override
            public void onFailure(Call<Party> call, Throwable t) {
                Toast message= Toast.makeText(tCtxt, R.string.start_party_failure, Toast.LENGTH_LONG);
                message.setGravity(Gravity.TOP, 0, 0);
                message.show();
            }
        });
        showGame();
    }

    private void showGame(){
        Intent i = new Intent(this, GameScreenActivity.class);
        startActivity(i);
        finish(); // do not allow the player to return to the party screen after a game has started
    }

    private void setPlayerInitialValues() {
        Location currentLocation = getLocationController().getLastBestLocation();
        App.GAME.CURRENT_PLAYER.setLatitude(currentLocation.getLatitude());
        App.GAME.CURRENT_PLAYER.setLongitude(currentLocation.getLongitude());
        Log.d("PartyDetailsActivity", "Set player's latitude to " + App.GAME.CURRENT_PLAYER.getLatitude());
        Log.d("PartyDetailsActivity", "Set player's longitude to " + App.GAME.CURRENT_PLAYER.getLongitude());

        if (App.GAME.CURRENT_PLAYER.getIsTagged() == null) {
            App.GAME.CURRENT_PLAYER.setIsTagged(false);
        }

        network().checkPartyStatus((String) App.GAME.CURRENT_PARTY.getPartyCode(), App.GAME.CURRENT_PLAYER).enqueue(new Callback<PartyStatus>() {
            @Override
            public void onResponse(Call<PartyStatus> call, Response<PartyStatus> response) {
                Log.d("update location", "success");
            }

            @Override
            public void onFailure(Call<PartyStatus> call, Throwable t) {
                Log.d("update location", "fail");
            }
        });
    }


    private View getStartGameButton(){
        return findViewById(R.id.StartGameButton);
    }

    private Fragment getPartyLeaderOptions(){
        return getFragmentManager().findFragmentById(R.id.PartyLeaderOptions);
    }

    private Fragment getPlayerPartyOptions(){
        return getFragmentManager().findFragmentById(R.id.PlayerPartyOptions);
    }

    private void getPlayersInParty(){
        showLoading();
        network().fetchPlayersInParty(App.GAME.CURRENT_PARTY.getPartyCode().toString(), App.GAME.CURRENT_PLAYER).enqueue(new Callback<List<PublicPlayer>>() {
            @Override
            public void onResponse(Call<List<PublicPlayer>> call, Response<List<PublicPlayer>> response) {
                updatePlayersInParty(response.body());
            }

            @Override
            public void onFailure(Call<List<PublicPlayer>> call, Throwable t) {
                //TODO Do something with the error.
                hideLoading();
            }
        });
    }

    private void updatePlayersInParty(List<PublicPlayer> players){
        hideLoading();
        this.LIST_ITEMS = players;
        if(this.LIST_ITEMS != null)
            initListItems();
    }

    public List<PublicPlayer> getListItems(){
        return this.LIST_ITEMS;
    }

    private void initListItems(){
        RecyclerView list = findViewById(R.id.ListItems);
        PlayerListAdapter adapter = new PlayerListAdapter(getListItems());
        if(list.getAdapter() == null)
            list.setAdapter(adapter);
        else
            list.swapAdapter(adapter, false);
    }
}
