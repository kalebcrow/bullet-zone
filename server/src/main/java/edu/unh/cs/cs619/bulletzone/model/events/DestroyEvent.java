package edu.unh.cs.cs619.bulletzone.model.events;

public class DestroyEvent extends GridEvent{

    private int row;
    private int col;

    public DestroyEvent(int row, int col) {
        this.row = row;
        this.col = col;
        this.type = "destroy";
        this.time = System.currentTimeMillis();
    }
}
