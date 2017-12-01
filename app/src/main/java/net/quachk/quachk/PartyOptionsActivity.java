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
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import net.quachk.quachk.Models.Game;
import net.quachk.quachk.Models.Party;
import net.quachk.quachk.Network.Network;
import net.quachk.quachk.Network.PlayerApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PartyOptionsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_options);

        findViewById(R.id.StartPartyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startParty();
            }
        });

        findViewById(R.id.JoinPartyButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt = ((EditText)findViewById(R.id.JoinPartyCode)).getText().toString();
                if(txt != null)
                    joinParty(txt);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onStart(){
        super.onStart();
        UpdatePlayerName(App.GAME.CURRENT_PLAYER.getUsername());
    }


    public void UpdatePlayerName(String player){
        ((TextView)findViewById(R.id.CurrentPlayerName)).setText(player);
    }

    public void startParty(){
        showLoading();
        network().startParty(App.GAME.CURRENT_PLAYER).enqueue(new Callback<Party>() {
            @Override
            public void onResponse(Call<Party> call, Response<Party> response) {
                onPartyStart(response.body());
            }

            @Override
            public void onFailure(Call<Party> call, Throwable t) {
                //TODO Do something with failure.
                hideLoading();
            }
        });
    }

    private void onPartyStart(Party p){
        hideLoading();
        App.GAME.CURRENT_PARTY = p;
        if(App.GAME.CURRENT_PARTY != null)
            openPartyDetails();
    }

    public void joinParty(String code){
        showLoading();
        network().joinParty(code, App.GAME.CURRENT_PLAYER).enqueue(new Callback<Party>() {
            @Override
            public void onResponse(Call<Party> call, Response<Party> response) {
                onPartyStart(response.body());
            }

            @Override
            public void onFailure(Call<Party> call, Throwable t) {
                //TODO Do something with failure.
                hideLoading();
            }
        });
    }

    private void openPartyDetails() {
        Intent i = new Intent(this, PartyDetailsActivity.class);
        startActivity(i);
    }
}
