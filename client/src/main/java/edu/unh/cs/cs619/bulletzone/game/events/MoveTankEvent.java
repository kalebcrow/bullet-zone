package edu.unh.cs.cs619.bulletzone.game.events;

import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class MoveTankEvent extends GridEvent {

    public Integer tankID;
    public Boolean forward;

    /**
     *
     * @param tankID Tank being moved
     * @param forward Forward or back
     */
    MoveTankEvent(Integer tankID, Boolean forward) {
        this.tankID = tankID;
        this.forward = forward;
    }

    /**
     * Execution of event
     */
    @Override
    public void execute() {

        TankTile tile = TankList.getTankList().getLocation(tankID);
        Integer location = tile.getLocation();
        Integer prevlocation = location;


        if (tile.getOrientation() == 0) {
            if (forward) {
                location = goingUp(location);
            } else {
                location = goingDown(location);
            }
        } else if (tile.getOrientation() == 2) {
            if (forward) {
                location = goingRight(location);
            } else {
                location = goingLeft(location);
            }
        } else if (tile.getOrientation() == 4) {
            if (forward) {
                location = goingDown(location);
            } else {
                location = goingUp(location);
            }
        } else if (tile.getOrientation() == 6) {
            if (forward) {
                location = goingLeft(location);
            } else {
                location = goingRight(location);
            }
        }
        tile.setLocation(location);

        busProvider.getEventBus().post(new TileUpdateEvent(location, tile));
        busProvider.getEventBus().post(new TileUpdateEvent(prevlocation, new BlankTile(0, prevlocation)));
    }

    private Integer goingUp(Integer location) {
        if (location <= 15) {
            location = 240 + location;
        } else {
            location = location + 16;
        }
        return location;
    }

    private Integer goingDown(Integer location) {
        if (location >= 240) {
            location = location - 240;
        } else {
            location = location + 16;
        }
        return location;
    }

    private Integer goingRight(Integer location) {
        if (location % 15 == 0 && location != 0) {
            location = location - 15;
        } else {
            location = location + 1;
        }
        return location;
    }

    private Integer goingLeft(Integer location) {
        if (location % 16 == 0) {
            location = location + 15;
        } else {
            location = location - 1;
        }
        return location;
    }

}
