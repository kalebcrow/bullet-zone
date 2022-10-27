package edu.unh.cs.cs619.bulletzone.game.events;

import edu.unh.cs.cs619.bulletzone.game.TileFactory;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class DestroyEvent extends  GridEvent {
    private int row;
    private int col;

    public DestroyEvent(int row, int col) {
        this.row = row;
        this.col = col;
        this.type = "destroy";
        this.time = System.currentTimeMillis();
    }

    @Override
    public TileUpdateEvent execute() {
        Integer location = (row - 1) * 16 + col - 1;

        return new TileUpdateEvent(location, TileFactory.getFactory().makeTile(0, 0));
    }
}
