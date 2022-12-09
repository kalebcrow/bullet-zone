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
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
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

    private Long[] tankID;

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
    String username;

    private int[] boardTankOn = { 0, 0, 0 };

    /**
     * TankController
     */
    public TankController() {
        tankID = new Long[3];
        tankOrientation = new int[3];
        for (int i = 0; i < 3; i++) {
            tankOrientation[i] = 0;
        }
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

    public int getCurrentVehicleNum() {
        int othervalue = 0;
        if (currentVehicle == Vehicle.BUILDER) {
            othervalue = 2;
        } else if (currentVehicle == Vehicle.MINER) {
            othervalue = 1;
        }
        return othervalue;
    }

    public int getBoardTankOn() {
        int othervalue = 0;
        if (currentVehicle == Vehicle.BUILDER) {
            othervalue = 2;
        } else if (currentVehicle == Vehicle.MINER) {
            othervalue = 1;
        }

        return boardTankOn[othervalue];
    }

    /**
     *
     */
    public void setBoardTankOn(int board) {
        this.boardTankOn[getCurrentVehicleNum()] = board;
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
    public void move(int gridnum, byte direction) {
        int othervalue = 0;
        if (currentVehicle == Vehicle.BUILDER) {
            othervalue = 2;
        } else if (currentVehicle == Vehicle.MINER) {
            othervalue = 1;
        }
        if (getBoardTankOn() != gridnum) {
            // don't move the tank if its on a different grid
            return;
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
    public void joinGame(String username){
        try {
            this.username = username;
            tankID = restClient.join(username).getResult();
            currentTankID = tankID[0];
        } catch (Exception e) {
            Log.d("Yeah", e.toString());
        }

    }

    @Background
    public void fire(){
        restClient.fire(currentTankID, (int) (currentTankID % 3));
    }

    @Background
    public void leaveGame(){
        System.out.println("leaveGame() called, tank ID: " + tankID.toString());
        restClient.leave(tankID[0].longValue());

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

    @Background
    public void mine(){

        if(currentVehicle == Vehicle.MINER){
            restClient.mine(tankID[1]);
        }
        else{
            Log.d("TankController", "Error: Mine called when currentVehicle is not Miner");
        }

    }

    @Background
    public void builderActions(int desiredAction){

        if(currentVehicle == Vehicle.BUILDER){

            //0 == dismantle
            if(desiredAction == 0){
                restClient.dismantle(tankID[2]);
            }
            //10 == indestructible wall
            else if(desiredAction == 10){
                //serverside, indestructible wall is 3
                restClient.build(tankID[2], 3);
            }
            //11 == road
            else if(desiredAction == 11){
                //serverside, road is 1
                restClient.build(tankID[2], 1);
            }
            //12 == wall
            else if(desiredAction == 12){
                //serverside, wall is 3
                restClient.build(tankID[2], 2);
            }
            else if(desiredAction == 13){
                //decking
                restClient.build(tankID[2], 4);
            }
            else if(desiredAction == 14){
                //factory
                restClient.build(tankID[2], 5);
            }
            else{

                Log.d("TankController", "Error: Invalid value received in builderAction");

            }

        }
        else{

            Log.d("TankController", "Error: Non-builder trying to call build/dismantle");

        }

    }

    @Background
    public void moveTo(int desiredLocation){
        restClient.moveTo(currentTankID, desiredLocation);
    }

    @Background
    public void requestTestResources(){
        restClient.test(tankID[1]);
    }

    @Background
    public void turnLeft(){
        int othervalue =0;
        if (currentVehicle == Vehicle.BUILDER) {
            othervalue = 2;
        } else if (currentVehicle == Vehicle.MINER) {
            othervalue = 1;
        }

        int value =  - tankOrientation[othervalue];

        int left = tankOrientation[othervalue] + 6;
        left = left % 8;
        restClient.turn(currentTankID, (byte) left);
        tankOrientation[othervalue] = left;
    }

    @Background
    public void turnRight(){
        int othervalue =0;
        if (currentVehicle == Vehicle.BUILDER) {
            othervalue = 2;
        } else if (currentVehicle == Vehicle.MINER) {
            othervalue = 1;
        }

        int value =  - tankOrientation[othervalue];

        int right = tankOrientation[othervalue] + 2;
        right = right % 8;
        restClient.turn(currentTankID, (byte) right);
        tankOrientation[othervalue] = right;
    }

    @Background
    public void respawn() {
        TankTile tile = TankList.getTankList().getLocation(Math.toIntExact(tankID[0]));
        TankTile tile1 = TankList.getTankList().getLocation(Math.toIntExact(tankID[2]));
        TankTile tile2 = TankList.getTankList().getLocation(Math.toIntExact(tankID[1]));
        if (tile == null && tile1 == null && tile2 == null) {
            joinGame(username);
        }
        restClient.rebuild(currentTankID);
    }

    @Background
    public void destroyTank(){
        restClient.destroy(getCurrentTankID());
    }

    @Background
    public void eject() {
        restClient.powerDown(getCurrentTankID());
    }
}
