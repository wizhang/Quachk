package net.quachk.quachk.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import net.quachk.quachk.Models.LoginPlayer;
import net.quachk.quachk.Models.NewPlayer;
import net.quachk.quachk.Models.Player;
import net.quachk.quachk.Network.PlayerApi;
import net.quachk.quachk.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Elijah on 10/3/2017.
 */

public class LoginCredentials extends Fragment{
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.login_credentials, container, false);
        view.findViewById(R.id.LoginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetUser();
            }
        });
        return view;
    }

    private void GetUser(){
        //TODO: Turn this into Async Task

        EditText user = view.findViewById(R.id.Username);
        if(user == null)
            return;
        EditText pass = view.findViewById(R.id.Password);
        if(pass == null)
            return;

        LoginPlayer np = new LoginPlayer();
        np.setUsername(user.getText().toString());
        np.setPassword(pass.getText().toString());

        Retrofit restAdapter = new Retrofit.Builder().baseUrl("http://192.168.1.68/Quachk/")
                .addConverterFactory(GsonConverterFactory.create()).build();
        PlayerApi task = restAdapter.create(PlayerApi.class);

        //Remove next 2 lines once turned into Async Task
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Player p = null;
        try {
            p = task.fetchPlayer(np).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(p != null){
            //TODO: Do something with the player once we get it.
        }

    }
}
