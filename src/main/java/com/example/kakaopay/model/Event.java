package com.example.kakaopay.model;

public class Event {

    private String token;

    private String userId;

    private String roomId;

    private long amount;

    private long reward;

    private long winner;

    private long startTime;

    private int id;

    private long updateTime;

    private int checkUserId;

    private boolean checkInterval;

    public long getReward() {
        return reward;
    }

    public void setReward(long reward) {
        this.reward = reward;
    }

    public boolean isCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(boolean checkInterval) {
        this.checkInterval = checkInterval;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getWinner() {
        return winner;
    }

    public void setWinner(long winner) {
        this.winner = winner;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getCheckUserId() {
        return checkUserId;
    }

    public void setCheckUserId(int checkUserId) {
        this.checkUserId = checkUserId;
    }
}
