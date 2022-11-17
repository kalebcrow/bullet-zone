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
    private int tankOrientation;
    private static volatile TankController INSTANCE = null;
    private Vehicle currentVehicle = Vehicle.TANK;

    /**
     * TankController
     */
    public TankController() {
        tankID = new Long[3];
        tankOrientation = 0;
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
        return tankOrientation;
    }

    /**
     *
     * @param tankOrientation tankOriention
     */
    public void setTankOrientation(int tankOrientation) {
        this.tankOrientation = tankOrientation;
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
        int value = direction - tankOrientation;

        if (direction == tankOrientation) {
            restClient.move(currentTankID, direction);
        } else if (Math.abs(direction - tankOrientation) != 4) {
            restClient.turn(currentTankID,direction);
            tankOrientation = direction;
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
    }

    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }
}
