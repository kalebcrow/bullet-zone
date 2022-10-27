package edu.unh.cs.cs619.bulletzone.game.events;

import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.BulletTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class FireEvent extends GridEvent {
    private Long tankID;
    private Long bulletID;

    public FireEvent(Long tankID, Long bulletID) {
        this.tankID = tankID;
        this.bulletID = bulletID;
        this.type = "fire";
        this.time = System.currentTimeMillis();
    }

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
