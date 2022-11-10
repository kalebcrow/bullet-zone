package edu.unh.cs.cs619.bulletzone.model;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.unh.cs.cs619.bulletzone.model.Exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class TankControllerTest {
    Tank tank;
    TankController tc;
    InMemoryGameRepository IMGR;

    @Test
    public void turn_TurnsSideways_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, "ip", 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertTrue(tc.turn(tank, Direction.Left));
    }

    @Test
    public void turn_TurnsForwardsOrBackwards_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, "ip", 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertFalse(tc.turn(tank, Direction.Up));
        assertFalse(tc.turn(tank, Direction.Down));
    }

    @Test
    public void turn_TurnsOnlyOncePerAllowedMoveTime() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, "ip", 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        IMGR = new InMemoryGameRepository();
        IMGR.join("ip");
        IMGR.turn(tank.getId(), Direction.Left);
        IMGR.turn(tank.getId(), Direction.Down);
        IMGR.turn(tank.getId(), Direction.Right);
        Game game = IMGR.getGame();
        tank = game.getTanks("ip");
        assertEquals(Direction.Left, tank.getDirection());
    }

    @Test
    public void move_DoesNotMoveSideways_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, "ip", 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertFalse(tc.move(tank, Direction.Left));
    }

    @Test
    public void move_MovesForwards_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, "ip", 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertTrue(tc.move(tank, Direction.Up));
    }

    @Test
    public void move_MovesBackwards_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, "ip", 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertTrue(tc.move(tank, Direction.Down));
    }

    @Test
    public void move_MovesOnlyOncePerAllowedMoveTime() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, "ip", 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        IMGR = new InMemoryGameRepository();
        IMGR.join("ip");
        IMGR.move(tank.getId(), Direction.Up);
        long millis = System.currentTimeMillis() + 10000;
        IMGR.move(tank.getId(), Direction.Down);
        IMGR.move(tank.getId(), Direction.Right);
        Game game = IMGR.getGame();
        tank = game.getTank("ip");
        assertTrue(tank.getLastMoveTime() <= millis);
    }

    @Test
    public void fire_SuccessfullyFires_ReturnsTrue() throws LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, "ip", 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        tank.setNumberOfBullets(0);
        assertNotEquals(-1, tc.fire(tank, 1));
    }

    @Test
    public void fire_DoesNotFireMoreThanTwoBulletsAtATime_ReturnsTrue() throws LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, "ip", 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        tank.setNumberOfBullets(2);
        assertEquals(-1, tc.fire(tank, 1));
    }

    @Test
    public void fire_DoesNotFireWithin500MillisecondsAfterPreviousFire_ReturnsTrue() throws LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, "ip", 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        IMGR = new InMemoryGameRepository();
        tank.setNumberOfBullets(0);
        IMGR.join("ip");
        IMGR.fire(tank.getId(), 1);
        IMGR.fire(tank.getId(), 1);
        Game game = IMGR.getGame();
        tank = game.getTank("ip");
        assertEquals(1, tank.getNumberOfBullets());
    }

    // testing basic moving, turning, and firing constraints for miner and builder

    @Test
    public void move_MinerMovingAtAppropriateInterval_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        tank = new Tank(0,Direction.Up,"ip",2);
        IMGR = new InMemoryGameRepository();
        IMGR.join("ip");
        assertTrue(IMGR.move(tank.getId(), Direction.Up));
        Thread.sleep(450);
        assertTrue(IMGR.move(tank.getId(), Direction.Down));
    }
}