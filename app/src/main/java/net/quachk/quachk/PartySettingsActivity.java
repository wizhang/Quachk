package net.quachk.quachk;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
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
import net.quachk.quachk.Models.PartyUpdate;
import net.quachk.quachk.Models.PublicPlayer;
import net.quachk.quachk.Network.Network;
import net.quachk.quachk.Network.PlayerApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PartySettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_party_settings);

        getUpdatePartyButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateParty();
            }
        });

        ((TextView)findViewById(R.id.PartyCode)).setText(App.GAME.CURRENT_PARTY.getPartyCode().toString());
    }

    private View getUpdatePartyButton(){
        return findViewById(R.id.UpdatePartyButton);
    }

    private void updateParty(){
        showLoading();

        PartyUpdate update = new PartyUpdate();
        update.setPlayer(App.GAME.CURRENT_PLAYER);
        update.setParty(App.GAME.CURRENT_PARTY);

        network().updatePartySettings(update).enqueue(new Callback<Party>() {
            @Override
            public void onResponse(Call<Party> call, Response<Party> response) {
                onPartyUpdate(response.body());
            }

            @Override
            public void onFailure(Call<Party> call, Throwable t) {
                //TODO Do something with failure.
                hideLoading();
            }
        });
    }

    private void onPartyUpdate(Party p){
        hideLoading();
        if(p != null)
            App.GAME.CURRENT_PARTY = p;
        openPartyDetails();
    }

    private void openPartyDetails() {
        Intent i = new Intent(this, PartyDetailsActivity.class);
        startActivity(i);
    }
}
