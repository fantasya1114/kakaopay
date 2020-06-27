package com.example.kakaopay.model;

import java.util.List;

public class EventStatus extends Result {

    private long amount;

    private long useAmount;

    private String startTime;

    private List<Winner> winners;

    public long getUseAmount() {
        return useAmount;
    }

    public void setUseAmount(long useAmount) {
        this.useAmount = useAmount;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public List<Winner> getWinners() {
        return winners;
    }

    public void setWinners(List<Winner> winners) {
        this.winners = winners;
    }
}
