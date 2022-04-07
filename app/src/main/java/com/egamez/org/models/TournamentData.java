//For all upcoming tournament data
package com.egamez.org.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TournamentData {

    @SerializedName("allplay_match")
    @Expose
    private List<AllplayMatch> allplayMatch = null;

    public List<AllplayMatch> getAllplayMatch() {
        return allplayMatch;
    }

    public void setAllplayMatch(List<AllplayMatch> allplayMatch) {
        this.allplayMatch = allplayMatch;
    }

    public class AllplayMatch {

        @SerializedName("pin_match")
        @Expose
        private String pinMatch;
        @SerializedName("m_id")
        @Expose
        private Integer mId;
        @SerializedName("match_name")
        @Expose
        private String matchName;
        @SerializedName("match_url")
        @Expose
        private String matchUrl;
        @SerializedName("room_id")
        @Expose
        private String roomId;
        @SerializedName("room_password")
        @Expose
        private String roomPassword;
        @SerializedName("unique_series_match_code")
        @Expose
        private String uniqueSeriesMatchCode;
        @SerializedName("m_time")
        @Expose
        private String mTime;
        @SerializedName("match_time")
        @Expose
        private String matchTime;
        @SerializedName("win_prize")
        @Expose
        private Integer winPrize;
        @SerializedName("prize_description")
        @Expose
        private String prizeDescription;
        @SerializedName("per_kill")
        @Expose
        private Integer perKill;
        @SerializedName("entry_fee")
        @Expose
        private Integer entryFee;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("version")
        @Expose
        private String version;
        @SerializedName("MAP")
        @Expose
        private String map;
        @SerializedName("match_type")
        @Expose
        private String matchType;
        @SerializedName("match_desc")
        @Expose
        private String matchDesc;
        @SerializedName("no_of_player")
        @Expose
        private Integer noOfPlayer;
        @SerializedName("number_of_position")
        @Expose
        private Integer numberOfPosition;
        @SerializedName("match_banner")
        @Expose
        private String matchBanner;
        @SerializedName("match_sponsor")
        @Expose
        private String matchSponsor;
        @SerializedName("unique_code_matches")
        @Expose
        private List<UniqueCodeMatch> uniqueCodeMatches = null;

        private String gameName;

        public String getGameName() {
            return gameName;
        }

        public void setGameName(String gameName) {
            this.gameName = gameName;
        }

        public String getPinMatch() {
            return pinMatch;
        }

        public void setPinMatch(String pinMatch) {
            this.pinMatch = pinMatch;
        }

        public Integer getmId() {
            return mId;
        }

        public void setmId(Integer mId) {
            this.mId = mId;
        }

        public String getMatchName() {
            return matchName;
        }

        public void setMatchName(String matchName) {
            this.matchName = matchName;
        }

        public String getMatchUrl() {
            return matchUrl;
        }

        public void setMatchUrl(String matchUrl) {
            this.matchUrl = matchUrl;
        }

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getRoomPassword() {
            return roomPassword;
        }

        public void setRoomPassword(String roomPassword) {
            this.roomPassword = roomPassword;
        }

        public String getUniqueSeriesMatchCode() {
            return uniqueSeriesMatchCode;
        }

        public void setUniqueSeriesMatchCode(String uniqueSeriesMatchCode) {
            this.uniqueSeriesMatchCode = uniqueSeriesMatchCode;
        }

        public String getmTime() {
            return mTime;
        }

        public void setmTime(String mTime) {
            this.mTime = mTime;
        }

        public String getMatchTime() {
            return matchTime;
        }

        public void setMatchTime(String matchTime) {
            this.matchTime = matchTime;
        }

        public Integer getWinPrize() {
            return winPrize;
        }

        public void setWinPrize(Integer winPrize) {
            this.winPrize = winPrize;
        }

        public String getPrizeDescription() {
            return prizeDescription;
        }

        public void setPrizeDescription(String prizeDescription) {
            this.prizeDescription = prizeDescription;
        }

        public Integer getPerKill() {
            return perKill;
        }

        public void setPerKill(Integer perKill) {
            this.perKill = perKill;
        }

        public Integer getEntryFee() {
            return entryFee;
        }

        public void setEntryFee(Integer entryFee) {
            this.entryFee = entryFee;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getMap() {
            return map;
        }

        public void setMap(String map) {
            this.map = map;
        }

        public String getMatchType() {
            return matchType;
        }

        public void setMatchType(String matchType) {
            this.matchType = matchType;
        }

        public String getMatchDesc() {
            return matchDesc;
        }

        public void setMatchDesc(String matchDesc) {
            this.matchDesc = matchDesc;
        }

        public Integer getNoOfPlayer() {
            return noOfPlayer;
        }

        public void setNoOfPlayer(Integer noOfPlayer) {
            this.noOfPlayer = noOfPlayer;
        }

        public Integer getNumberOfPosition() {
            return numberOfPosition;
        }

        public void setNumberOfPosition(Integer numberOfPosition) {
            this.numberOfPosition = numberOfPosition;
        }

        public String getMatchBanner() {
            return matchBanner;
        }

        public void setMatchBanner(String matchBanner) {
            this.matchBanner = matchBanner;
        }

        public String getMatchSponsor() {
            return matchSponsor;
        }

        public void setMatchSponsor(String matchSponsor) {
            this.matchSponsor = matchSponsor;
        }

        public List<UniqueCodeMatch> getUniqueCodeMatches() {
            return uniqueCodeMatches;
        }

        public void setUniqueCodeMatches(List<UniqueCodeMatch> uniqueCodeMatches) {
            this.uniqueCodeMatches = uniqueCodeMatches;
        }

        public class UniqueCodeMatch {

            @SerializedName("pin_match")
            @Expose
            private String pinMatch;
            @SerializedName("m_id")
            @Expose
            private Integer mId;
            @SerializedName("match_name")
            @Expose
            private String matchName;
            @SerializedName("match_url")
            @Expose
            private String matchUrl;
            @SerializedName("room_id")
            @Expose
            private String roomId;
            @SerializedName("room_password")
            @Expose
            private String roomPassword;
            @SerializedName("unique_series_match_code")
            @Expose
            private String uniqueSeriesMatchCode;
            @SerializedName("m_time")
            @Expose
            private String mTime;
            @SerializedName("match_time")
            @Expose
            private String matchTime;
            @SerializedName("win_prize")
            @Expose
            private Integer winPrize;
            @SerializedName("per_kill")
            @Expose
            private Integer perKill;
            @SerializedName("entry_fee")
            @Expose
            private Integer entryFee;
            @SerializedName("type")
            @Expose
            private String type;
            @SerializedName("version")
            @Expose
            private String version;
            @SerializedName("MAP")
            @Expose
            private String map;
            @SerializedName("match_type")
            @Expose
            private String matchType;
            @SerializedName("no_of_player")
            @Expose
            private Integer noOfPlayer;
            @SerializedName("number_of_position")
            @Expose
            private Integer numberOfPosition;
            @SerializedName("match_banner")
            @Expose
            private String matchBanner;
            @SerializedName("match_sponsor")
            @Expose
            private String matchSponsor;
            @SerializedName("member_id")
            @Expose
            private String memberId;
            @SerializedName("join_status")
            @Expose
            private Boolean joinStatus;
            private Boolean isSelected;
            private String gameName;

            public String getPinMatch() {
                return pinMatch;
            }

            public void setPinMatch(String pinMatch) {
                this.pinMatch = pinMatch;
            }

            public Integer getmId() {
                return mId;
            }

            public void setmId(Integer mId) {
                this.mId = mId;
            }

            public String getMatchName() {
                return matchName;
            }

            public void setMatchName(String matchName) {
                this.matchName = matchName;
            }

            public String getMatchUrl() {
                return matchUrl;
            }

            public void setMatchUrl(String matchUrl) {
                this.matchUrl = matchUrl;
            }

            public String getRoomId() {
                return roomId;
            }

            public void setRoomId(String roomId) {
                this.roomId = roomId;
            }

            public String getRoomPassword() {
                return roomPassword;
            }

            public void setRoomPassword(String roomPassword) {
                this.roomPassword = roomPassword;
            }

            public String getUniqueSeriesMatchCode() {
                return uniqueSeriesMatchCode;
            }

            public void setUniqueSeriesMatchCode(String uniqueSeriesMatchCode) {
                this.uniqueSeriesMatchCode = uniqueSeriesMatchCode;
            }

            public String getmTime() {
                return mTime;
            }

            public void setmTime(String mTime) {
                this.mTime = mTime;
            }

            public String getMatchTime() {
                return matchTime;
            }

            public void setMatchTime(String matchTime) {
                this.matchTime = matchTime;
            }

            public Integer getWinPrize() {
                return winPrize;
            }

            public void setWinPrize(Integer winPrize) {
                this.winPrize = winPrize;
            }

            public Integer getPerKill() {
                return perKill;
            }

            public void setPerKill(Integer perKill) {
                this.perKill = perKill;
            }

            public Integer getEntryFee() {
                return entryFee;
            }

            public void setEntryFee(Integer entryFee) {
                this.entryFee = entryFee;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getVersion() {
                return version;
            }

            public void setVersion(String version) {
                this.version = version;
            }

            public String getMap() {
                return map;
            }

            public void setMap(String map) {
                this.map = map;
            }

            public String getMatchType() {
                return matchType;
            }

            public void setMatchType(String matchType) {
                this.matchType = matchType;
            }

            public Integer getNoOfPlayer() {
                return noOfPlayer;
            }

            public void setNoOfPlayer(Integer noOfPlayer) {
                this.noOfPlayer = noOfPlayer;
            }

            public Integer getNumberOfPosition() {
                return numberOfPosition;
            }

            public void setNumberOfPosition(Integer numberOfPosition) {
                this.numberOfPosition = numberOfPosition;
            }

            public String getMatchBanner() {
                return matchBanner;
            }

            public void setMatchBanner(String matchBanner) {
                this.matchBanner = matchBanner;
            }

            public String getMatchSponsor() {
                return matchSponsor;
            }

            public void setMatchSponsor(String matchSponsor) {
                this.matchSponsor = matchSponsor;
            }

            public String getMemberId() {
                return memberId;
            }

            public void setMemberId(String memberId) {
                this.memberId = memberId;
            }

            public Boolean getJoinStatus() {
                return joinStatus;
            }

            public void setJoinStatus(Boolean joinStatus) {
                this.joinStatus = joinStatus;
            }

            public Boolean getSelected() {
                return isSelected;
            }

            public void setSelected(Boolean selected) {
                isSelected = selected;
            }

            public String getGameName() {
                return gameName;
            }

            public void setGameName(String gameName) {
                this.gameName = gameName;
            }
        }
    }
}