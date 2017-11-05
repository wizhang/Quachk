package net.quachk.quachk;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;

import net.quachk.quachk.Adapters.PlayerListAdapter;
import net.quachk.quachk.Models.Party;
import net.quachk.quachk.Models.Player;
import net.quachk.quachk.Models.PublicPlayer;
import net.quachk.quachk.Network.Network;
import net.quachk.quachk.Network.PlayerApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PartyDetailsActivity extends AppCompatActivity {

    private List<PublicPlayer> LIST_ITEMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_party_details);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.LIST_ITEMS = new ArrayList<>();

        RecyclerView list = findViewById(R.id.ListItems);
        list.setLayoutManager(new LinearLayoutManager(this));
        initListItems();

        ((TextView)findViewById(R.id.PartyCode)).setText(App.GAME.CURRENT_PARTY.getPartyCode().toString());
    }

    @Override
    protected void onStart(){
        super.onStart();
        hideFragment(getPartyLeaderOptions());
        hideFragment(getPlayerPartyOptions());
        getStartGameButton().setVisibility(View.GONE);
        if(App.GAME.CURRENT_PARTY.getPartyLeaderId() == App.GAME.CURRENT_PLAYER.getPlayerId()) {
            showFragment(getPartyLeaderOptions());
            getStartGameButton().setVisibility(View.VISIBLE);
        } else
            showFragment(getPlayerPartyOptions());
        GetPlayersInParty();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void hideFragment(Fragment frag){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.hide(frag);
        ft.commit();
    }

    private void showFragment(Fragment frag){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.show(frag);
        ft.commit();
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

    private void GetPlayersInParty(){
        Retrofit restAdapter = new Retrofit.Builder().baseUrl(Network.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        PlayerApi task = restAdapter.create(PlayerApi.class);

        //Remove next 2 lines once turned into Async Task
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        List<PublicPlayer> players = null;

        try {
            players = task.fetchPlayersInParty(App.GAME.CURRENT_PARTY.getPartyCode().toString(), App.GAME.CURRENT_PLAYER).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.w("app", "Party Code:" + App.GAME.CURRENT_PARTY.getPartyCode().toString());
        Log.w("app", "Got List Items:" + new Gson().toJson(players).toString());

        this.LIST_ITEMS = players;

        if(this.LIST_ITEMS != null)
            initListItems();
    }

    public List<PublicPlayer> getListItems(){
        return this.LIST_ITEMS;
    }

    private void initListItems(){
        RecyclerView list = findViewById(R.id.ListItems);
        Log.w("app", "List Items:" + new Gson().toJson(getListItems()).toString());
        PlayerListAdapter adapter = new PlayerListAdapter(getListItems());
        if(list.getAdapter() == null)
            list.setAdapter(adapter);
        else
            list.swapAdapter(adapter, false);
    }
}
