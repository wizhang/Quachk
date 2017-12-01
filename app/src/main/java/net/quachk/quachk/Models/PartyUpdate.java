package net.quachk.quachk.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PartyUpdate {

    @SerializedName("Player")
    @Expose
    private Player player;
    @SerializedName("Party")
    @Expose
    private Party party;
    @SerializedName("TaggedPlayers")
    @Expose
    private List<PublicPlayer> taggedPlayers;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public List<PublicPlayer> getTaggedPlayers() {
        return taggedPlayers;
    }

    public void setTaggedPlayers(List<PublicPlayer> taggedPlayers) {
        this.taggedPlayers = taggedPlayers;
    }

}