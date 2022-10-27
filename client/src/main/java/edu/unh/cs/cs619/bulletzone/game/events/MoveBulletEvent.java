package edu.unh.cs.cs619.bulletzone.game.events;

import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class MoveBulletEvent extends GridEvent {

    private Long tankID;
    private int bulletID;
    private int direction;

    public MoveBulletEvent(Long tankID, int bulletID, int direction) {
        this.tankID = tankID;
        this.bulletID = bulletID;
        this.direction = direction;
        this.type = "moveBullet";
        this.time = System.currentTimeMillis();
    }

    @Override
    public void execute() {

        TankTile tile = TankList.getTankList().getLocation(Math.toIntExact(tankID));
        Integer location = tile.getLocation();
        Integer prevlocation = location;


        if (tile.getOrientation() == 0) {
            location = goingUp(location);
        } else if (tile.getOrientation() == 2) {
            location = goingRight(location);
        } else if (tile.getOrientation() == 4) {
            location = goingDown(location);
        } else if (tile.getOrientation() == 6) {
            location = goingLeft(location);
        }

        busProvider.getEventBus().post(new TileUpdateEvent(prevlocation, new BlankTile(0, prevlocation)));
        busProvider.getEventBus().post(new TileUpdateEvent(location, new TankTile()));
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
