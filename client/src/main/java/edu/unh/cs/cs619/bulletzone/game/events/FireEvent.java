package edu.unh.cs.cs619.bulletzone.game.events;

import android.util.Log;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.BulletTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class FireEvent extends ExecutableEvent {

    public FireEvent(GridEvent event) {
        super(event);
    }

    /**
     * runs the command updating the board
     */
    public void execute(Bus bus) {
        tileUpdateEvent = new TileUpdateEvent(this.pos + 1, new BulletTile(ID, this.pos + 1, true));
        bus.post(tileUpdateEvent);
    }

    /**
     *
     * @param location starting location
     * @return ending location
     */
    private Integer goingUp(Integer location) {
        if (location <= 15) {
            location = 240 + location;
        } else {
            location = location - 16;
        }
        return location;
    }

    /**
     *
     * @param location starting location
     * @return ending location
     */
    private Integer goingDown(Integer location) {
        if (location >= 240) {
            location = location - 240;
        } else {
            location = location + 16;
        }
        return location;
    }

    /**
     *
     * @param location starting location
     * @return ending location
     */
    private Integer goingRight(Integer location) {
        if (location % 15 == 0 && location != 0) {
            location = location - 15;
        } else {
            location = location + 1;
        }
        return location;
    }

    /**
     *
     * @param location starting location
     * @return ending location
     */
    private Integer goingLeft(Integer location) {
        if (location % 16 == 0) {
            location = location + 15;
        } else {
            location = location - 1;
        }
        return location;
    }

}
