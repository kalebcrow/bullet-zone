package edu.unh.cs.cs619.bulletzone.game;

import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;

import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;

@EBean
public class TankController {
    BulletZoneRestClient restClient;
    private Long tankID;
    private int tankOrientation;

    public TankController() {
        tankID = 0L;
        tankOrientation = 0;
    }

    public BulletZoneRestClient getRestClient() {
        return restClient;
    }

    public void setRestClient(BulletZoneRestClient restClient) {
        this.restClient = restClient;
    }

    public Long getTankID() {
        return tankID;
    }

    public void setTankID(Long tankID) {
        this.tankID = tankID;
    }

    public int getTankOrientation() {
        return tankOrientation;
    }

    public void setTankOrientation(int tankOrientation) {
        this.tankOrientation = tankOrientation;
    }

    @Background
    public void move(byte direction) {
        int value = direction - tankOrientation;

        if (direction == tankOrientation) {
            restClient.move(tankID, direction);
        } else if (Math.abs(direction - tankOrientation) != 4) {
            restClient.turn(tankID,direction);
            tankOrientation = direction;
        } else {
            restClient.move(tankID, direction);
        }
    }
}
