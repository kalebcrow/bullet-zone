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
    private static volatile TankController INSTANCE = null;


    public TankController() {
        tankID = 0L;
        tankOrientation = 0;
        INSTANCE = this;
    }

    public static TankController getTankController() {
        if(INSTANCE == null) {
            synchronized (TankController.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TankController();
                }
            }
        }
        return INSTANCE;
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
