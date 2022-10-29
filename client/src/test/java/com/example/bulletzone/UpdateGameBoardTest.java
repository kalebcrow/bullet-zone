package com.example.bulletzone;

import org.androidannotations.annotations.AfterInject;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.BoardView;
import edu.unh.cs.cs619.bulletzone.game.events.FireEvent;
import edu.unh.cs.cs619.bulletzone.game.events.GridEvent;
import edu.unh.cs.cs619.bulletzone.game.events.MoveTankEvent;
import edu.unh.cs.cs619.bulletzone.game.events.TurnEvent;
import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.BulletTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

import com.squareup.otto.Bus;

public class UpdateGameBoardTest {

    BoardView testBoardView;
    @Mock
    Bus mBus;

    @Test
    public void fireTest(){

        testBoardView = new BoardView();

        int[][] mimicJSONArray = new int[16][16];
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                mimicJSONArray[i][j] = 0;

            }
        }

        //tank of id 222, 100 health, facing right
        mimicJSONArray[0][0] = 12221002;
        testBoardView.setUsingJSON(mimicJSONArray);
        TankTile testTankTile = (TankTile) testBoardView.getTile(0);

        GridEvent newGridEvent = new GridEvent();
        newGridEvent.setID(testTankTile.getID() * 10);
        newGridEvent.setDirection((byte) 2);
        newGridEvent.setPos(0);
        newGridEvent.setType("fire");
        newGridEvent.setTime(System.currentTimeMillis());

        //BusProvider testBusProvider = new BusProvider();
        mBus = Mockito.mock(Bus.class);
        FireEvent newFireEvent = new FireEvent(newGridEvent);
        newFireEvent.execute(mBus);

        TileUpdateEvent tileUpdateEvent = newFireEvent.tileUpdateEvent;

        Integer testResourceID = tileUpdateEvent.movedTile.getResourceID();
        Integer testLocation = tileUpdateEvent.location;

        if(testResourceID != null && testLocation != null){
            assertTrue(testResourceID == R.drawable.bullet);
            assertTrue(testLocation == 1);
        }
        else{
            assertTrue(false);
        }

    }

    @Test
    public void moveTest(){

        testBoardView = new BoardView();

        int[][] mimicJSONArray = new int[16][16];
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                mimicJSONArray[i][j] = 0;

            }
        }

        //tank of id 222, 100 health, facing right
        mimicJSONArray[0][0] = 12221002;
        testBoardView.setUsingJSON(mimicJSONArray);
        TankTile testTankTile = (TankTile) testBoardView.getTile(0);

        GridEvent newGridEvent = new GridEvent();
        newGridEvent.setID(testTankTile.getID());
        newGridEvent.setDirection((byte) 2);
        newGridEvent.setPos(0);
        newGridEvent.setType("move");
        newGridEvent.setTime(System.currentTimeMillis());

        //BusProvider testBusProvider = new BusProvider();
        mBus = Mockito.mock(Bus.class);
        MoveTankEvent newMoveEvent = new MoveTankEvent(newGridEvent);
        newMoveEvent.execute(mBus);

        TileUpdateEvent tileUpdateEvent = newMoveEvent.tileUpdateEvent;

        Integer testResourceID = tileUpdateEvent.movedTile.getResourceID();
        Integer testLocation = tileUpdateEvent.location;

        if(testResourceID != null && testLocation != null){
            assertTrue(testResourceID == R.drawable.redtank);
            assertTrue(testLocation == 1);
        }
        else{
            assertTrue(false);
        }

    }

    @Test
    public void testTurn(){

        testBoardView = new BoardView();

        int[][] mimicJSONArray = new int[16][16];
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                mimicJSONArray[i][j] = 0;

            }
        }

        //tank of id 222, 100 health, facing right
        mimicJSONArray[0][0] = 12221002;
        testBoardView.setUsingJSON(mimicJSONArray);
        TankTile testTankTile = (TankTile) testBoardView.getTile(0);

        GridEvent newGridEvent = new GridEvent();
        newGridEvent.setID(testTankTile.getID());
        newGridEvent.setDirection((byte) 0);
        newGridEvent.setPos(0);
        newGridEvent.setType("turn");
        newGridEvent.setTime(System.currentTimeMillis());

        //BusProvider testBusProvider = new BusProvider();
        mBus = Mockito.mock(Bus.class);
        TurnEvent newTurnEvent = new TurnEvent(newGridEvent);
        newTurnEvent.execute(mBus);

        TileUpdateEvent tileUpdateEvent = newTurnEvent.tileUpdateEvent;

        Integer testOrientation = tileUpdateEvent.movedTile.orientation;

        if(testOrientation != null){
            assertTrue(testOrientation == 0);
        }
        else{
            assertTrue(false);
        }

    }

}
