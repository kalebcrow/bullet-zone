package com.example.bulletzone;


import android.content.Context;
import android.widget.GridView;
import android.widget.TextView;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.rest.BulletZoneRestClient;


@RunWith(MockitoJUnitRunner.class)
public class TankControllerTest {

    @Mock
    BulletZoneRestClient restClient;
    private static TankController testingUnit;

    @BeforeClass
    public static void init(){
        testingUnit = TankController.getTankController();
    }

    @Test public void TankControllerReturnsTankID() {
        long value = 12L;
        testingUnit.setTankID(0, value);
        assertEquals(java.util.Optional.of(value).get(), testingUnit.getTankID()[0]);
    }

    @Test public void TankControllerMoveCalls() {
        long value = 12L;
        testingUnit.setTankID(0, value);
        restClient = Mockito.mock(BulletZoneRestClient.class);
        testingUnit.setRestClient(restClient);
        //forward and backwards
        //Note testingUnit assumes tank faces up initially
        //Move up
        testingUnit.move((byte) 0);
        verify(restClient).move(value, (byte) 0);
        //Move Down
        testingUnit.move((byte) 4);
        verify(restClient).move(value, (byte) 4);
    }

    @Test public void TankControllerTurnCalls() {
        long value = 12L;
        testingUnit.setTankID(0, value);
        restClient = Mockito.mock(BulletZoneRestClient.class);
        testingUnit.setRestClient(restClient);

        //Note testingUnit assumes tank faces up initially
        //Turn right
        testingUnit.move((byte) 2);
        verify(restClient).turn(value, (byte) 2);
        //Turn down
        testingUnit.move((byte) 4);
        verify(restClient).turn(value, (byte) 4);
    }





}
