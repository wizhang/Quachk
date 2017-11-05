package net.quachk.quachk.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.quachk.quachk.App;
import net.quachk.quachk.Models.Player;
import net.quachk.quachk.Network.Network;
import net.quachk.quachk.Network.PlayerApi;
import net.quachk.quachk.PartyOptionsActivity;
import net.quachk.quachk.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Elijah on 10/3/2017.
 */

public class PlayerPartyOptions extends Fragment{

    View view;
    public PlayerPartyOptions(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_player_party_options, container, false);
        view.findViewById(R.id.LeavePartyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leaveParty();
            }
        });

        view.findViewById(R.id.InvitePartyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invitePlayers();
            }
        });

        return view;
    }

    private void leaveParty(){
        Retrofit restAdapter = new Retrofit.Builder().baseUrl(Network.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        PlayerApi task = restAdapter.create(PlayerApi.class);

        //Remove next 2 lines once turned into Async Task
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Player p = null;

        try {
            p = task.leaveParty(App.GAME.CURRENT_PARTY.getPartyCode().toString(), App.GAME.CURRENT_PLAYER).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(p != null){
            App.GAME.CURRENT_PLAYER = p;
            openPartyOptions();
        }
    }

    private void openPartyOptions(){
        Intent i = new Intent(getActivity(), PartyOptionsActivity.class);
        startActivity(i);
    }

    private void invitePlayers(){

    }
}
