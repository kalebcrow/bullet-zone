package edu.unh.cs.cs619.bulletzone.game;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;

import java.util.Objects;

import edu.unh.cs.cs619.bulletzone.ShakeService;
import edu.unh.cs.cs619.bulletzone.rest.BZRestErrorhandler;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;

@EBean(scope = EBean.Scope.Singleton)
public class TankController {

    public enum Vehicle {
        TANK,
        MINER,
        BUILDER
    }

    @RestService
    BulletZoneRestClient restClient;

    @Bean
    BZRestErrorhandler bzRestErrorhandler;

    private Long tankID[];

    public Long getCurrentTankID() {
        return currentTankID;
    }

    public void setCurrentTankID(int index) {
        this.currentTankID = tankID[index];
    }

    private Long currentTankID;
    private int[] tankOrientation;
    private static volatile TankController INSTANCE = null;
    private Vehicle currentVehicle = Vehicle.TANK;

    /**
     * TankController
     */
    public TankController() {
        tankID = new Long[3];
        tankOrientation = new int[3];
        INSTANCE = this;
    }

    /**
     * Create Tank controller
     * @return tankController
     */
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

    /**
     *
     * @return returnClient
     */
    public BulletZoneRestClient getRestClient() {
        return restClient;
    }

    /**
     * set rest client
     * @param restClient restClient
     */
    public void setRestClient(BulletZoneRestClient restClient) {
        this.restClient = restClient;
    }

    /**
     *
     * @return tankID
     */
    public Long[] getTankID() {
        return tankID;
    }

    /**
     *
     * @param tankID set tankID
     */
    public void setTankID(int index, Long tankID) {
        this.tankID[index] = tankID;
    }

    /**
     *
     * @param tankID set tankID
     */
    public void setTankIDs(Long[] tankID) {
        this.tankID = tankID;
    }


    public boolean containsTankID(Long tankID) {
        for (int i = 0; i < 3; i++) {
            if (Objects.equals(this.tankID[i], tankID)) {
                return true;
            }
        }
        return  false;
    }



    public int getTankOrientation() {
        int othervalue =0;
        if (currentVehicle == Vehicle.BUILDER) {
            othervalue = 2;
        } else if (currentVehicle == Vehicle.MINER) {
            othervalue = 1;
        }

        return tankOrientation[othervalue];
    }

    /**
     *
     */
    public void setTankOrientation(int orientation, int tankmodulo) {
        this.tankOrientation[tankmodulo] = orientation;
    }

    public void passContext(Context context){
        ShakeService.setTankController(this);
        Intent intent = new Intent(context, ShakeService.class);
        context.startService(intent);
    }

    public void afterInject(){
        restClient.setRestErrorHandler(bzRestErrorhandler);
    }

    /**
     * moves the tank
     * @param direction direction
     */
    @Background
    public void move(byte direction) {
        int othervalue =0;
        if (currentVehicle == Vehicle.BUILDER) {
            othervalue = 2;
        } else if (currentVehicle == Vehicle.MINER) {
            othervalue = 1;
        }

        int value = direction - tankOrientation[othervalue];

        if (direction == tankOrientation[othervalue]) {
            restClient.move(currentTankID, direction);
        } else if (Math.abs(direction - tankOrientation[othervalue]) != 4) {
            restClient.turn(currentTankID,direction);
            tankOrientation[othervalue] = direction;
        } else {
            restClient.move(currentTankID, direction);
        }
    }

    @Background
    public void joinGame(){
        try {
            tankID = restClient.join().getResult();
            currentTankID = tankID[0];
        } catch (Exception e) {
            Log.d("Yeah", e.toString());
        }

    }

    @Background
    public void fire(){
        restClient.fire(currentTankID);
    }

    @Background
    public void leaveGame(){
        System.out.println("leaveGame() called, tank ID: " + tankID.toString());
        for (int i = 0; i < 3; i++) {
            restClient.leave(tankID[i]);
        }
    }

    public void setCurrentVehicle(Vehicle currentVehicle){
        Log.d("TankController", "Tank Changed to: " + currentVehicle);
        this.currentVehicle = currentVehicle;
        if (currentVehicle == Vehicle.BUILDER) {
            currentTankID = tankID[2];
        } else if (currentVehicle == Vehicle.MINER) {
            currentTankID = tankID[1];
        } else {
            currentTankID = tankID[0];
        }


    }

    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }
}
