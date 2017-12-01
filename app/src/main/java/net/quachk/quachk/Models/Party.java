package net.quachk.quachk.Models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Elijah on 10/18/2017.
 */

public class Party {

    @SerializedName("PartyId")
    @Expose
    private Integer partyId;
    @SerializedName("StartTime")
    @Expose
    private String startTime;
    @SerializedName("GameDuration")
    @Expose
    private Integer gameDuration;
    @SerializedName("EndTime")
    @Expose
    private String endTime;
    @SerializedName("TotalPoints")
    @Expose
    private Integer totalPoints;
    @SerializedName("PointSecond")
    @Expose
    private Integer pointSecond;
    @SerializedName("PartyCode")
    @Expose
    private Object partyCode;
    @SerializedName("PartyLeaderId")
    @Expose
    private Integer partyLeaderId;

    public Integer getPartyId() {
        return partyId;
    }

    public void setPartyId(Integer partyId) {
        this.partyId = partyId;
    }

    public Date getStartTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            return df.parse(startTime);
        }catch (Exception e){
            return null;
        }
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getGameDuration() {
        return gameDuration;
    }

    public void setGameDuration(Integer gameDuration) {
        this.gameDuration = gameDuration;
    }

    public Date getEndTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            return df.parse(endTime);
        }catch (Exception e){
            return null;
        }
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Integer getPointSecond() {
        return pointSecond;
    }

    public void setPointSecond(Integer pointSecond) {
        this.pointSecond = pointSecond;
    }

    public Object getPartyCode() {
        return partyCode;
    }

    public void setPartyCode(Object partyCode) {
        this.partyCode = partyCode;
    }

    public Integer getPartyLeaderId() {
        return partyLeaderId;
    }

    public void setPartyLeaderId(Integer partyLeaderId) {
        this.partyLeaderId = partyLeaderId;
    }

}
