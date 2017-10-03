package net.quachk.quachk.Network;

import net.quachk.quachk.Models.LoginPlayer;
import net.quachk.quachk.Models.NewPlayer;
import net.quachk.quachk.Models.Player;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Elijah on 10/3/2017.
 */

public interface PlayerApi {
    @POST("api/Player/Login")
    Call<Player> fetchPlayer(@Body LoginPlayer player);

    @POST("api/Player/New")
    Call<Player> fetchNewPlayer(@Body NewPlayer player);
}
