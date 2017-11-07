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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Elijah on 10/3/2017.
 */

public class PlayerPartyOptions extends BaseFragment{

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
        showLoading();
        network().leaveParty(App.GAME.CURRENT_PARTY.getPartyCode().toString(), App.GAME.CURRENT_PLAYER).enqueue(new Callback<Player>() {
            @Override
            public void onResponse(Call<Player> call, Response<Player> response) {
                onPartyLeave(response.body());
            }

            @Override
            public void onFailure(Call<Player> call, Throwable t) {
                //TODO Do something with failure.
                hideLoading();
            }
        });
    }

    private void onPartyLeave(Player p){
        hideLoading();
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
