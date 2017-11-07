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
        Retrofit restAdapter = new Retrofit.Builder().baseUrl(Network.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        PlayerApi task = restAdapter.create(PlayerApi.class);

        //Remove next 2 lines once turned into Async Task
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        PartyUpdate update = new PartyUpdate();
        update.setPlayer(App.GAME.CURRENT_PLAYER);
        update.setParty(App.GAME.CURRENT_PARTY);

        Party party = null;

        try {
            party = task.updatePartySettings(update).execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(party != null)
            App.GAME.CURRENT_PARTY = party;

        openPartyDetails();
    }

    private void openPartyDetails() {
        Intent i = new Intent(this, PartyDetailsActivity.class);
        startActivity(i);
    }
}
