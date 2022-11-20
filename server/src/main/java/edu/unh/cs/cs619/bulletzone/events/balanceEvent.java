package edu.unh.cs.cs619.bulletzone.events;

public class balanceEvent extends GridEvent {

    public balanceEvent(double balance, long tankID) {
        this.ID = Math.toIntExact(tankID);
        this.type = "balance";
        this.pos = (int) balance;
        this.time = System.currentTimeMillis();
    }
}
