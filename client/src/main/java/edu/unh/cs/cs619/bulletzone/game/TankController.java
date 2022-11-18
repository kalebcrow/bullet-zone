package edu.unh.cs.cs619.bulletzone.game;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.rest.spring.annotations.RestService;

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

    private Long tankID;
    //private Long minerID
    //private Long BuilderID
    private int tankOrientation;
    private static volatile TankController INSTANCE = null;
    private Vehicle currentVehicle = Vehicle.TANK;

    /**
     * TankController
     */
    public TankController() {
        tankID = 0L;
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
    public Long getTankID() {
        return tankID;
    }

    /**
     *
     * @param tankID set tankID
     */
    public void setTankID(Long tankID) {
        this.tankID = tankID;
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
            restClient.move(tankID, direction);
        } else if (Math.abs(direction - tankOrientation) != 4) {
            restClient.turn(tankID,direction);
            tankOrientation = direction;
        } else {
            restClient.move(tankID, direction);
        }
    }

    @Background
    public void joinGame(long userID){
        try {
            Long[] s = restClient.join(userID).getResult();
            tankID = s[0];
            //tankID = restClient.join().getResult();
        } catch (Exception e) {

        }

    }

    @Background
    public void fire(){
        restClient.fire(tankID);
    }

    @Background
    public void leaveGame(){
        System.out.println("leaveGame() called, tank ID: " + tankID);
        restClient.leave(tankID);
    }

    public void setCurrentVehicle(Vehicle currentVehicle){
        Log.d("TankController", "Tank Changed to: " + currentVehicle);
        this.currentVehicle = currentVehicle;
    }

    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }
}
