package edu.unh.cs.cs619.bulletzone.game.events;

import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.BulletTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class FireEvent extends GridEvent {
    private Long tankID;
    private Long bulletID;

    /**
     *
     * @param tankID Tank doing the shooting
     * @param bulletID Bullet being shot
     */
    public FireEvent(Long tankID, Long bulletID) {
        this.tankID = tankID;
        this.bulletID = bulletID;
        this.type = "fire";
        this.time = System.currentTimeMillis();
    }

    /**
     * runs the command updating the board
     */
    @Override
    public void execute() {
        TankTile tile = TankList.getTankList().getLocation(Math.toIntExact(tankID));
        Integer orientation = tile.getOrientation();

        Integer location = tile.getLocation();

        if (orientation == 0) {
            location = goingUp(location);
        } else if (orientation == 2) {
            location = goingRight(location);
        } else if (orientation == 4) {
            location = goingDown(location);
        } else if (orientation == 6) {
            location = goingLeft(location);
        }

        busProvider.getEventBus().post(new TileUpdateEvent(location, new BulletTile(bulletID, Math.toIntExact(tankID), location)));
    }

    /**
     *
     * @param location starting location
     * @return
     */
    private Integer goingUp(Integer location) {
        if (location <= 15) {
            location = 240 + location;
        } else {
            location = location + 16;
        }
        return location;
    }

    /**
     *
     * @param location starting location
     * @return
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
     * @return
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
     * @return
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
