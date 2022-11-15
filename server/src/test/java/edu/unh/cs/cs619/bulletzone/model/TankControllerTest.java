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
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join("ip");
        IMGR.turn(tanks[0].getId(), Direction.Left);
        IMGR.turn(tanks[0].getId(), Direction.Down);
        IMGR.turn(tanks[0].getId(), Direction.Right);
        Game game = IMGR.getGame();
        assertEquals(Direction.Left, game.getTank(tanks[0].getId()).getDirection());
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
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join("ip");
        IMGR.move(tanks[0].getId(), Direction.Up);
        long millis = System.currentTimeMillis() + 10000;
        IMGR.move(tanks[0].getId(), Direction.Down);
        IMGR.move(tanks[0].getId(), Direction.Right);
        Game game = IMGR.getGame();
        assertTrue(game.getTank(tanks[0].getId()).getLastMoveTime() <= millis);
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
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join("ip");
        IMGR.fire(tanks[0].getId(), 1);
        IMGR.fire(tanks[0].getId(), 1);
        Game game = IMGR.getGame();
        assertEquals(1, game.getTank(tanks[0].getId()).getNumberOfBullets());
    }

    // testing basic moving, turning, and firing constraints for miner and builder

    @Test
    public void move_MinerMovingAtAppropriateInterval_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {

    }
}