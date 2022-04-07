//For all game data
package com.egamez.org.models;

public class GameData {

    String gameId;
    String gameName;
    String gameImage;
    String gameStatus;
    String totalUpcoming;

    public GameData(String gameId, String gameName, String gameImage, String gameStatus, String totalUpcoming) {
        this.gameId = gameId;
        this.gameName = gameName;
        this.gameImage = gameImage;
        this.gameStatus = gameStatus;
        this.totalUpcoming = totalUpcoming;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getGameImage() {
        return gameImage;
    }

    public void setGameImage(String gameImage) {
        this.gameImage = gameImage;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String getTotalUpcoming() {
        return totalUpcoming;
    }

    public void setTotalUpcoming(String totalUpcoming) {
        this.totalUpcoming = totalUpcoming;
    }
}
