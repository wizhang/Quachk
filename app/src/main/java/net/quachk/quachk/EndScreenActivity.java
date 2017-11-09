package net.quachk.quachk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import net.quachk.quachk.Adapters.PlayerListAdapter;
import net.quachk.quachk.Adapters.ScoreListAdapter;
import net.quachk.quachk.Models.Party;
import net.quachk.quachk.Models.Player;
import net.quachk.quachk.Models.PublicPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EndScreenActivity extends LocationActivity {
    private List<PublicPlayer> LIST_ITEMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);
        this.LIST_ITEMS = new ArrayList<>();

        RecyclerView list = findViewById(R.id.ListItems);
        list.setLayoutManager(new LinearLayoutManager(this));

        updateScores();
        findViewById(R.id.ContinueButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPartyOptions();
            }
        });
    }

    private void openPartyOptions(){
        Intent i = new Intent(this, PartyOptionsActivity.class);
        startActivity(i);
    }

    @Override
    protected void onResume(){
        super.onResume();
        updateScores();
    }


    public void updateScores(){
        try{
            network().fetchPlayersInParty((String) App.GAME.CURRENT_PARTY.getPartyCode(), App.GAME.CURRENT_PLAYER).enqueue(new Callback<List<PublicPlayer>>() {
                @Override
                public void onResponse(Call<List<PublicPlayer>> call, Response<List<PublicPlayer>> response) {
                    List<PublicPlayer> orderedPlayers = new ArrayList<>();
                    List<PublicPlayer> playerList = response.body();
                    if(playerList != null){
                        //Success! We have retrieved other player information
                        Log.d("Scores", "Number of players: " + playerList.size());
                        while (!playerList.isEmpty()) {
                            int maxValue = -1;
                            PublicPlayer maxPlayer = null;
                            for (PublicPlayer player : playerList) {
                                Log.d("Player Score", player.getScore().toString());
                                if (maxPlayer == null){
                                    maxPlayer = player;
                                }
                                if (player.getScore() > maxValue){
                                    maxValue = player.getScore();
                                    maxPlayer = player;
                                }
                            }
                            playerList.remove(maxPlayer);
                            orderedPlayers.add(maxPlayer);
                        }
                        for (PublicPlayer player: orderedPlayers){
                            Log.d("Top score Usernames", player.getUsername());
                        }
                        updatePlayersInParty(orderedPlayers);
                    }
                    else {
                        Log.d("EndScreenActivity", "Error retrieving other player scores");
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
        ScoreListAdapter adapter = new ScoreListAdapter(getListItems());
        if(list.getAdapter() == null)
            list.setAdapter(adapter);
        else
            list.swapAdapter(adapter, false);
    }
}