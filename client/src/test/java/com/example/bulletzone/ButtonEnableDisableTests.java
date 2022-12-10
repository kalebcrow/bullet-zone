package com.example.bulletzone;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.widget.Button;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.otto.Bus;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import edu.unh.cs.cs619.bulletzone.game.BoardView;
import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.events.GridEvent;
import edu.unh.cs.cs619.bulletzone.game.events.RestrictionEvent;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.ui.ButtonState;
import edu.unh.cs.cs619.bulletzone.ui.DownButtonState;
import edu.unh.cs.cs619.bulletzone.ui.FireButtonState;
import edu.unh.cs.cs619.bulletzone.ui.LeftButtonState;
import edu.unh.cs.cs619.bulletzone.ui.RightButtonState;
import edu.unh.cs.cs619.bulletzone.ui.UpButtonState;

public class ButtonEnableDisableTests {

    /*
    To Marielle:
    Because of how our implementation handles restrictions, our restrictions are generated
    on the server side and sent to the client side. This is the client side tests. The server
    side tests are called "RestrictionEventTests.java"
     */

    BoardView testBoardView;
    @Mock
    Bus mBus;

    @Mock
    Button mockUpButton;
    @Mock
    Button mockRightButton;
    @Mock
    Button mockDownButton;
    @Mock
    Button mockLeftButton;
    @Mock
    Button mockFireButton;

    @Test
    public void testButtonStates_FacingRightCannotLeftOrRight_DisablesRightAndLeftButtons(){

        testBoardView = new BoardView();

        int[][][] mimicJSONArray = new int[16][16][3];
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                mimicJSONArray[i][j][0] = 0;
                mimicJSONArray[i][j][1] = 0;
                mimicJSONArray[i][j][2] = 0;

            }
        }

        //tank of id 222, 100 health, facing right
        mimicJSONArray[0][0][1] = 12221002;
        testBoardView.setUsingJSON(mimicJSONArray);
        TankTile testTankTile = TankList.getTankList().getLocation(222);

        //create mock buttons
        mockUpButton = mock(Button.class);
        mockRightButton = mock(Button.class);
        mockDownButton = mock(Button.class);
        mockLeftButton = mock(Button.class);
        mockFireButton = mock(Button.class);

        //create buttonStates from Mocked buttons
        UpButtonState mockUpButtonState = new UpButtonState(mockUpButton);
        RightButtonState mockRightButtonState = new RightButtonState(mockRightButton);
        DownButtonState mockDownButtonState = new DownButtonState(mockDownButton);
        LeftButtonState mockLeftButtonState = new LeftButtonState(mockLeftButton);
        FireButtonState mockFireButtonState = new FireButtonState(mockFireButton);

        ButtonState[] buttonStates = new ButtonState[5];
        buttonStates[0] = mockUpButtonState;
        buttonStates[1] = mockRightButtonState;
        buttonStates[2] = mockDownButtonState;
        buttonStates[3] = mockLeftButtonState;
        buttonStates[4] = mockFireButtonState;

        TankController.getTankController().buttonStateSetup(buttonStates);

        mBus = Mockito.mock(Bus.class);

        GridEvent newGridEvent = new GridEvent();
        newGridEvent.setID(testTankTile.getID());
        //System.out.println("Test: " + testTankTile.getID() * 10);
        newGridEvent.setDirection((byte) 2);
        newGridEvent.setPos(0);
        newGridEvent.setType("restriction");
        newGridEvent.setTime(System.currentTimeMillis());
        newGridEvent.setRestrictions(new int[]{0,0,1});

        RestrictionEvent newRestrictionEvent = new RestrictionEvent(newGridEvent);

        Long[] tankIDs = new Long[3];
        tankIDs[0] = Long.valueOf(222);
        tankIDs[1] = Long.valueOf(1);
        tankIDs[2] = Long.valueOf(2);
        TankController.getTankController().setTankIDs(tankIDs);
        TankController.getTankController().setCurrentVehicle(TankController.Vehicle.TANK);
        TankController.getTankController().setCurrentTankID(0);
        TankController.getTankController().setTankOrientation(2, 0);

        newRestrictionEvent.execute(mBus);

        verify(mockRightButton).setEnabled(false);
        verify(mockLeftButton).setEnabled(false);

        verify(mockUpButton).setEnabled(true);
        verify(mockDownButton).setEnabled(true);
        verify(mockFireButton).setEnabled(true);

    }

    @Test
    public void testButtonState_FacingDownCannotMoveUpOrDown_DisablesUpAndDown(){

        testBoardView = new BoardView();

        int[][][] mimicJSONArray = new int[16][16][3];
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                mimicJSONArray[i][j][0] = 0;
                mimicJSONArray[i][j][1] = 0;
                mimicJSONArray[i][j][2] = 0;

            }
        }

        //tank of id 222, 100 health, facing right
        mimicJSONArray[0][0][1] = 12221002;
        testBoardView.setUsingJSON(mimicJSONArray);
        TankTile testTankTile = TankList.getTankList().getLocation(222);

        //create mock buttons
        mockUpButton = mock(Button.class);
        mockRightButton = mock(Button.class);
        mockDownButton = mock(Button.class);
        mockLeftButton = mock(Button.class);
        mockFireButton = mock(Button.class);

        //create buttonStates from Mocked buttons
        UpButtonState mockUpButtonState = new UpButtonState(mockUpButton);
        RightButtonState mockRightButtonState = new RightButtonState(mockRightButton);
        DownButtonState mockDownButtonState = new DownButtonState(mockDownButton);
        LeftButtonState mockLeftButtonState = new LeftButtonState(mockLeftButton);
        FireButtonState mockFireButtonState = new FireButtonState(mockFireButton);

        ButtonState[] buttonStates = new ButtonState[5];
        buttonStates[0] = mockUpButtonState;
        buttonStates[1] = mockRightButtonState;
        buttonStates[2] = mockDownButtonState;
        buttonStates[3] = mockLeftButtonState;
        buttonStates[4] = mockFireButtonState;

        TankController.getTankController().buttonStateSetup(buttonStates);

        mBus = Mockito.mock(Bus.class);

        GridEvent newGridEvent = new GridEvent();
        newGridEvent.setID(testTankTile.getID());
        //System.out.println("Test: " + testTankTile.getID() * 10);
        newGridEvent.setDirection((byte) 4);
        newGridEvent.setPos(0);
        newGridEvent.setType("restriction");
        newGridEvent.setTime(System.currentTimeMillis());
        newGridEvent.setRestrictions(new int[]{0,0,1});

        RestrictionEvent newRestrictionEvent = new RestrictionEvent(newGridEvent);

        Long[] tankIDs = new Long[3];
        tankIDs[0] = Long.valueOf(222);
        tankIDs[1] = Long.valueOf(1);
        tankIDs[2] = Long.valueOf(2);
        TankController.getTankController().setTankIDs(tankIDs);
        TankController.getTankController().setCurrentVehicle(TankController.Vehicle.TANK);
        TankController.getTankController().setCurrentTankID(0);
        TankController.getTankController().setTankOrientation(4, 0);

        newRestrictionEvent.execute(mBus);

        verify(mockRightButton).setEnabled(true);
        verify(mockLeftButton).setEnabled(true);

        verify(mockUpButton).setEnabled(false);
        verify(mockDownButton).setEnabled(false);
        verify(mockFireButton).setEnabled(true);

    }

    @Test
    public void testButtonState_MaxBulletsRestrictionsEnabled_DisablesFireButton(){

        testBoardView = new BoardView();

        int[][][] mimicJSONArray = new int[16][16][3];
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                mimicJSONArray[i][j][0] = 0;
                mimicJSONArray[i][j][1] = 0;
                mimicJSONArray[i][j][2] = 0;

            }
        }

        //tank of id 222, 100 health, facing right
        mimicJSONArray[0][0][1] = 12221002;
        testBoardView.setUsingJSON(mimicJSONArray);
        TankTile testTankTile = TankList.getTankList().getLocation(222);

        //create mock buttons
        mockUpButton = mock(Button.class);
        mockRightButton = mock(Button.class);
        mockDownButton = mock(Button.class);
        mockLeftButton = mock(Button.class);
        mockFireButton = mock(Button.class);

        //create buttonStates from Mocked buttons
        UpButtonState mockUpButtonState = new UpButtonState(mockUpButton);
        RightButtonState mockRightButtonState = new RightButtonState(mockRightButton);
        DownButtonState mockDownButtonState = new DownButtonState(mockDownButton);
        LeftButtonState mockLeftButtonState = new LeftButtonState(mockLeftButton);
        FireButtonState mockFireButtonState = new FireButtonState(mockFireButton);

        ButtonState[] buttonStates = new ButtonState[5];
        buttonStates[0] = mockUpButtonState;
        buttonStates[1] = mockRightButtonState;
        buttonStates[2] = mockDownButtonState;
        buttonStates[3] = mockLeftButtonState;
        buttonStates[4] = mockFireButtonState;

        TankController.getTankController().buttonStateSetup(buttonStates);

        mBus = Mockito.mock(Bus.class);

        GridEvent newGridEvent = new GridEvent();
        newGridEvent.setID(testTankTile.getID());
        //System.out.println("Test: " + testTankTile.getID() * 10);
        newGridEvent.setDirection((byte) 2);
        newGridEvent.setPos(0);
        newGridEvent.setType("restriction");
        newGridEvent.setTime(System.currentTimeMillis());
        newGridEvent.setRestrictions(new int[]{1,1,0});

        RestrictionEvent newRestrictionEvent = new RestrictionEvent(newGridEvent);

        Long[] tankIDs = new Long[3];
        tankIDs[0] = Long.valueOf(222);
        tankIDs[1] = Long.valueOf(1);
        tankIDs[2] = Long.valueOf(2);
        TankController.getTankController().setTankIDs(tankIDs);
        TankController.getTankController().setCurrentVehicle(TankController.Vehicle.TANK);
        TankController.getTankController().setCurrentTankID(0);
        TankController.getTankController().setTankOrientation(2, 0);

        newRestrictionEvent.execute(mBus);

        verify(mockRightButton).setEnabled(true);
        verify(mockLeftButton).setEnabled(true);
        verify(mockUpButton).setEnabled(true);
        verify(mockDownButton).setEnabled(true);
        verify(mockFireButton).setEnabled(false);

    }

    @Test
    public void testButtonState_NoRestrictionsReceived_AllButtonsEnabled(){

        testBoardView = new BoardView();

        int[][][] mimicJSONArray = new int[16][16][3];
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                mimicJSONArray[i][j][0] = 0;
                mimicJSONArray[i][j][1] = 0;
                mimicJSONArray[i][j][2] = 0;

            }
        }

        //tank of id 222, 100 health, facing right
        mimicJSONArray[0][0][1] = 12221002;
        testBoardView.setUsingJSON(mimicJSONArray);
        TankTile testTankTile = TankList.getTankList().getLocation(222);

        //create mock buttons
        mockUpButton = mock(Button.class);
        mockRightButton = mock(Button.class);
        mockDownButton = mock(Button.class);
        mockLeftButton = mock(Button.class);
        mockFireButton = mock(Button.class);

        //create buttonStates from Mocked buttons
        UpButtonState mockUpButtonState = new UpButtonState(mockUpButton);
        RightButtonState mockRightButtonState = new RightButtonState(mockRightButton);
        DownButtonState mockDownButtonState = new DownButtonState(mockDownButton);
        LeftButtonState mockLeftButtonState = new LeftButtonState(mockLeftButton);
        FireButtonState mockFireButtonState = new FireButtonState(mockFireButton);

        ButtonState[] buttonStates = new ButtonState[5];
        buttonStates[0] = mockUpButtonState;
        buttonStates[1] = mockRightButtonState;
        buttonStates[2] = mockDownButtonState;
        buttonStates[3] = mockLeftButtonState;
        buttonStates[4] = mockFireButtonState;

        TankController.getTankController().buttonStateSetup(buttonStates);

        mBus = Mockito.mock(Bus.class);

        GridEvent newGridEvent = new GridEvent();
        newGridEvent.setID(testTankTile.getID());
        //System.out.println("Test: " + testTankTile.getID() * 10);
        newGridEvent.setDirection((byte) 2);
        newGridEvent.setPos(0);
        newGridEvent.setType("restriction");
        newGridEvent.setTime(System.currentTimeMillis());
        newGridEvent.setRestrictions(new int[]{1,1,1});

        RestrictionEvent newRestrictionEvent = new RestrictionEvent(newGridEvent);

        Long[] tankIDs = new Long[3];
        tankIDs[0] = Long.valueOf(222);
        tankIDs[1] = Long.valueOf(1);
        tankIDs[2] = Long.valueOf(2);
        TankController.getTankController().setTankIDs(tankIDs);
        TankController.getTankController().setCurrentVehicle(TankController.Vehicle.TANK);
        TankController.getTankController().setCurrentTankID(0);
        TankController.getTankController().setTankOrientation(2, 0);

        newRestrictionEvent.execute(mBus);

        verify(mockRightButton).setEnabled(true);
        verify(mockLeftButton).setEnabled(true);
        verify(mockUpButton).setEnabled(true);
        verify(mockDownButton).setEnabled(true);
        verify(mockFireButton).setEnabled(true);

    }


}
