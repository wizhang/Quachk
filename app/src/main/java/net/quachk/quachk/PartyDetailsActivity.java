package net.quachk.quachk;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import net.quachk.quachk.Models.Party;
import net.quachk.quachk.Network.Network;
import net.quachk.quachk.Network.PlayerApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PartyDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_party_details);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ((TextView)findViewById(R.id.PartyCode)).setText(App.GAME.CURRENT_PARTY.getPartyCode().toString());
    }

    @Override
    protected void onResume(){
        super.onResume();
    }
}
