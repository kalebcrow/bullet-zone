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

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Long getTankID() {
        return tankID;
    }

    public void setTankID(Long tankID) {
        this.tankID = tankID;
    }
}
