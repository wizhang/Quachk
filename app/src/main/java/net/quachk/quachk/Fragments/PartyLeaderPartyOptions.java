package net.quachk.quachk.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.gson.Gson;

import net.quachk.quachk.App;
import net.quachk.quachk.Models.Game;
import net.quachk.quachk.Models.LoginPlayer;
import net.quachk.quachk.Models.Player;
import net.quachk.quachk.Models.PublicPlayer;
import net.quachk.quachk.Network.Network;
import net.quachk.quachk.Network.PlayerApi;
import net.quachk.quachk.PartyOptionsActivity;
import net.quachk.quachk.R;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Elijah on 10/3/2017.
 */

public class PartyLeaderPartyOptions extends Fragment{

    View view;
    public PartyLeaderPartyOptions(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_party_leader_party_options, container, false);
        view.findViewById(R.id.DisbandPartyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disbandParty();
            }
        });
        view.findViewById(R.id.SettingsPartyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPartySettings();
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

    private void disbandParty(){
        Retrofit restAdapter = new Retrofit.Builder().baseUrl(Network.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        PlayerApi task = restAdapter.create(PlayerApi.class);

        //Remove next 2 lines once turned into Async Task
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Boolean success = false;

        try {
            success = task.disbandParty(App.GAME.CURRENT_PARTY.getPartyCode().toString(), App.GAME.CURRENT_PLAYER).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(success != null && success) {
            App.GAME.CURRENT_PARTY = null;
            App.GAME.CURRENT_PLAYER.setPartyId(null);

            openPartyOptions();
        }
    }

    private void openPartyOptions(){
        Intent i = new Intent(getActivity(), PartyOptionsActivity.class);
        startActivity(i);
    }

    private void openPartySettings(){

    }

    private void invitePlayers(){

    }
}
