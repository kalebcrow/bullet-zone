package edu.unh.cs.cs619.bulletzone.events;



public class AddTankEvent extends GridEvent {

    public AddTankEvent(int row, int col, Long tankID) {
        this.pos = 16*row + col;
        this.ID = Math.toIntExact(tankID);
        this.type = "addTank";
        this.time = System.currentTimeMillis();
    }
}
