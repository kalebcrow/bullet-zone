package edu.unh.cs.cs619.bulletzone.model.events;

public class AddTankEvent extends GridEvent {

    private int row;
    private int col;
    private Long tankID;

    public AddTankEvent(int row, int col, Long tankID) {
        this.row = row;
        this.col = col;
        this.tankID = tankID;
        this.type = "addTank";
        this.time = System.currentTimeMillis();
    }
}
