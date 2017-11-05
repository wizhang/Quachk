package net.quachk.quachk.Network;

import net.quachk.quachk.Models.LoginPlayer;
import net.quachk.quachk.Models.NewPlayer;
import net.quachk.quachk.Models.Party;
import net.quachk.quachk.Models.Player;
import net.quachk.quachk.Models.PublicPlayer;

import java.util.List;

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

    @POST("api/Party/New")
    Call<Party> startParty(@Body Player player);

    @POST("api/Party/Join/{code}")
    Call<Party> joinParty(@Path("code")String code, @Body Player player);

    @POST("api/Party/Join/{code}")
    Call<Player> leaveParty(@Path("code")String code, @Body Player player);

    @POST("api/Party/Disband/{code}")
    Call<Boolean> disbandParty(@Path("code")String code, @Body Player player);

    @GET("api/Party/Code/{code}/Players")
    Call<List<PublicPlayer>> fetchPlayersInParty(@Path("code")String code);
}
