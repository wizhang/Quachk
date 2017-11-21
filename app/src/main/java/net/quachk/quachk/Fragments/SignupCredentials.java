package net.quachk.quachk.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import net.quachk.quachk.App;
import net.quachk.quachk.CredentialsActivity;
import net.quachk.quachk.Models.Game;
import net.quachk.quachk.Models.NewPlayer;
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

public class SignupCredentials extends BaseFragment {
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        hideLoading();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.signup_credentials, container, false);
        view.findViewById(R.id.SignupButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newUser();
            }
        });
        return view;
    }

    private void newUser(){
        EditText user = view.findViewById(R.id.Username);
        if(user == null)
            return;
        EditText pass = view.findViewById(R.id.Password);
        if(pass == null)
            return;

        EditText cpass = view.findViewById(R.id.ConfirmPassword);
        if(cpass == null)
            return;

        showLoading();

        NewPlayer np = new NewPlayer();
        np.setUsername(user.getText().toString());
        np.setPassword(pass.getText().toString());
        np.setConfirmPassword(cpass.getText().toString());

        network().fetchNewPlayer(np).enqueue(new Callback<Player>() {
            @Override
            public void onResponse(Call<Player> call, Response<Player> response) {
                updatePlayer(response.body());
            }

            @Override
            public void onFailure(Call<Player> call, Throwable t) {
                Toast error = Toast.makeText(getActivity().getApplicationContext(),
                        "Error: Please ensure that you are connected to the internet.", Toast.LENGTH_SHORT);
                error.setGravity(Gravity.TOP, 0, 0);
                error.show();

                hideLoading();
            }
        });
    }

    private void updatePlayer(Player p){
        hideLoading();
        if(p == null)
            return;
        App.GAME = new Game();
        App.GAME.CURRENT_PLAYER = p;
        openPartyOptions();
    }

    private void openPartyOptions(){
        Intent i = new Intent(getActivity(), PartyOptionsActivity.class);
        startActivity(i);
    }
}
