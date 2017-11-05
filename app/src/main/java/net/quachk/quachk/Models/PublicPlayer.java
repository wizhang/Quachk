package net.quachk.quachk.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PublicPlayer {

    @SerializedName("PlayerId")
    @Expose
    private Integer playerId;
    @SerializedName("Username")
    @Expose
    private String username;
    @SerializedName("Highscore")
    @Expose
    private Integer highscore;
    @SerializedName("IsTagged")
    @Expose
    private Boolean isTagged;
    @SerializedName("TaggedTime")
    @Expose
    private Object taggedTime;
    @SerializedName("Score")
    @Expose
    private Integer score;
    @SerializedName("PartyId")
    @Expose
    private Integer partyId;
    @SerializedName("Latitude")
    @Expose
    private Object latitude;
    @SerializedName("Longitude")
    @Expose
    private Object longitude;

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getHighscore() {
        return highscore;
    }

    public void setHighscore(Integer highscore) {
        this.highscore = highscore;
    }

    public Boolean getIsTagged() {
        return isTagged;
    }

    public void setIsTagged(Boolean isTagged) {
        this.isTagged = isTagged;
    }

    public Object getTaggedTime() {
        return taggedTime;
    }

    public void setTaggedTime(Object taggedTime) {
        this.taggedTime = taggedTime;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getPartyId() {
        return partyId;
    }

    public void setPartyId(Integer partyId) {
        this.partyId = partyId;
    }

    public Object getLatitude() {
        return latitude;
    }

    public void setLatitude(Object latitude) {
        this.latitude = latitude;
    }

    public Object getLongitude() {
        return longitude;
    }

    public void setLongitude(Object longitude) {
        this.longitude = longitude;
    }

}