package net.quachk.quachk.Utility;

import android.util.Log;

import net.quachk.quachk.App;
import net.quachk.quachk.LocationActivity;
import net.quachk.quachk.Models.PartyStatus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by quachk on 11/10/17.
 */

public class PlayerUpdater extends LocationActivity
{
    public PlayerUpdater() { }

    public void updatePlayer() {
        try {
            network().checkPartyStatus(App.GAME.CURRENT_PARTY.getPartyCode().toString(), App.GAME.CURRENT_PLAYER)
                    .enqueue(new Callback<PartyStatus>() {
                        @Override
                        public void onResponse(Call<PartyStatus> call, Response<PartyStatus> response) {
                            // success
                            Log.d("PlayerUpdater", "successfully updated player information");
                            if (response.body() != null) {
                                PartyStatus status = response.body();
                                Log.d("PartyUpdater", "Current player score: " + App.GAME.CURRENT_PLAYER.getScore().toString());
                                Log.d("PartyUpdater", "Current player score on the server: " + status.getPlayer().getScore().toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<PartyStatus> call, Throwable t) {
                            Log.d("PlayerUpdater", "failed to update player information");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
