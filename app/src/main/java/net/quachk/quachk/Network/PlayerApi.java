package net.quachk.quachk.Network;

import net.quachk.quachk.Models.LoginPlayer;
import net.quachk.quachk.Models.NewPlayer;
import net.quachk.quachk.Models.Party;
import net.quachk.quachk.Models.PartyStatus;
import net.quachk.quachk.Models.PartyUpdate;
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

    /**
     * Passes the login parameters to the server to log the player in and return the current
     * game player to the application.
     * @param player
     *      The login credential parameters (LoginPlayer object)
     * @return
     *      The current game player
     */
    @POST("api/Player/Login")
    Call<Player> fetchPlayer(@Body LoginPlayer player);

    /**
     * Creates a new player with the game. Once the new player is verified on the server,
     * the player is logged in and the current game player is returned.
     * @param player
     *      The player credentials to create a new player account (NewPlayer object)
     * @return
     *      The current game player, once successfully logged in.
     */
    @POST("api/Player/New")
    Call<Player> fetchNewPlayer(@Body NewPlayer player);

    /**
     * Starts a new party and sets the current game player as the leader.
     * @param player
     *      The player starting the party (the current game player)
     * @return
     *      The newly created party.
     */
    @POST("api/Party/New")
    Call<Party> startParty(@Body Player player);

    /**
     * Joins an existing party, given the party's code.
     * @param code
     *      The party's code
     * @param player
     *      The current game player, to join the party.
     * @return
     *      The newly joined party.
     */
    @POST("api/Party/Join/{code}")
    Call<Party> joinParty(@Path("code")String code, @Body Player player);


    /**
     * Leaves an existing party, given the party's code.
     * @param code
     *      The code of the party the user wishes to leave.
     *      Note: the server can identify which party the user is in, but this is needed
     *      to verify the correct party the user wishes to leave, in case a user is ever in a party
     *      at the same time.
     *      The code passed should be the current party's code.
     * @param player
     *      The player to evict from the party (the current game player).
     * @return
     *      The current game player with their updated status after leaving the party. If the player
     *      was unable to leave the party, this will be the same object as was passed in.
     */
    @POST("api/Party/Leave/{code}")
    Call<Player> leaveParty(@Path("code")String code, @Body Player player);

    /**
     * Disband the current party.
     * Note: this only works if the current game player {@player} is the party leader.
     * @param code
     *      The party's code of the party to disband.
     * @param player
     *      The current game player (must be the party leader)
     * @return
     *      True, if the party was successfully disbanded; false, otherwise.
     */
    @POST("api/Party/Disband/{code}")
    Call<Boolean> disbandParty(@Path("code")String code, @Body Player player);

    /**
     * Get's the players in the current player's party.
     * This returns the information on the other players, including their Location information.
     * @param code
     *      The Party's code
     * @param player
     *      The current player
     * @return
     *      A list of all other players in the party, along with their location information, and tag status.
     */
    @GET("api/Party/Code/{code}/Players")
    Call<List<PublicPlayer>> fetchPlayersInParty(@Path("code")String code, @Body Player player);

    /**
     * Updates the player's information on the server and sync's with the Party.
     * Returns the status of the player.
     * @param code
     *      The Party's code
     * @param player
     *      The player to update (This must be the current player in the game).
     * @return
     *      The Status of the party according to the server.
     */
    @POST("api/Party/Code/{code}/Status")
    Call<PartyStatus> checkPartyStatus(@Path("code")String code, @Body Player player);

    /**
     * Update the settings for the party.
     * Note: only the party leader can update the settings
     * @param update
     *      The PartyUpdate object, which includes the list of tagged players, the updated party,
     *      and the party leader.
     * @return
     *      The updated party.
     */
    @POST("api/Party/Update")
    Call<Party> updatePartySettings(@Body PartyUpdate update);
}
