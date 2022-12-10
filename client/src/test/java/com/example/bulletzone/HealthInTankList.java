package com.example.bulletzone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.squareup.otto.Bus;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.game.BoardView;
import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.events.DamageTankEvent;
import edu.unh.cs.cs619.bulletzone.game.events.FireEvent;
import edu.unh.cs.cs619.bulletzone.game.events.GridEvent;
import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class HealthInTankList {
    BoardView testBoardView;
    @Mock
    Bus mBus;

    @Test
    public void damageTest(){

        testBoardView = new BoardView();

        int[][][] mimicJSONArray = new int[48][16][3];
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                mimicJSONArray[i][j][0] = 0;

            }
        }

        TankController.getTankController().setTankID(0, 222L);

        //tank of id 222, 100 health, facing right
        mimicJSONArray[0][0][0] = 12221002;
        testBoardView.setUsingJSON(mimicJSONArray);
        TankTile testTankTile =  TankList.getTankList().getLocation(222);

        GridEvent newGridEvent = new GridEvent();
        newGridEvent.setID(testTankTile.getID());
        newGridEvent.setPos(51);
        newGridEvent.setType("damage");
        newGridEvent.setTime(System.currentTimeMillis());

        //BusProvider testBusProvider = new BusProvider();
        mBus = Mockito.mock(Bus.class);
        DamageTankEvent newMoveEvent = new DamageTankEvent(newGridEvent);
        newMoveEvent.execute(mBus);

        TileUpdateEvent tileUpdateEvent = newMoveEvent.tileUpdateEvent;
        TankList tankList =TankList.getTankList();
        testTankTile = tankList.getLocation(testTankTile.getID());


        if(testTankTile != null){
            assertEquals(50, (int) testTankTile.health);
        }
        else{
            assertTrue(false);
        }

    }
}
