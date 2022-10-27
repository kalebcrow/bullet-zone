package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.UiThread;

import edu.unh.cs.cs619.bulletzone.game.TileFactory;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class DestroyEvent extends  GridEvent {
    private int row;
    private int col;

    /**
     *
     * @param row the row
     * @param col the colomn
     */
    public DestroyEvent(int row, int col) {
        this.row = row;
        this.col = col;
        this.type = "destroy";
        this.time = System.currentTimeMillis();
    }

    /**
     * runs the command updating the board
     */
    @Override
    public void execute() {
        Integer location = row * 16 + col;

         busProvider.getEventBus().post(new TileUpdateEvent(location, TileFactory.getFactory().makeTile(0, 0)));
    }
}
