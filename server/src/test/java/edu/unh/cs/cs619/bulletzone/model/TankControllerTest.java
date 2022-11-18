package edu.unh.cs.cs619.bulletzone.model;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class TankControllerTest {
    Tank tank;
    TankController tc;
    InMemoryGameRepository IMGR;

    @Test
    public void turn_TurnsSideways_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0,0, Direction.Up, "ip");
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertTrue(tc.turn(tank, Direction.Left));
    }

    @Test
    public void turn_TurnsForwardsOrBackwards_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0,0, Direction.Up, "ip");
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertFalse(tc.turn(tank, Direction.Up));
        assertFalse(tc.turn(tank, Direction.Down));
    }

    @Test
    public void turn_TurnsOnlyOncePerAllowedMoveTime() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0,0, Direction.Up, "ip");
        tank.setLastMoveTime(System.currentTimeMillis());
        IMGR = new InMemoryGameRepository();
        tank.setAllowedMoveInterval(10000);
        IMGR.join(0,"ip");
        IMGR.turn(tank.getId(), Direction.Left);
        IMGR.turn(tank.getId(), Direction.Down);
        IMGR.turn(tank.getId(), Direction.Right);
        Game game = IMGR.getGame();
        tank = game.getTank("ip");
        assertEquals(Direction.Left, tank.getDirection());
    }

    @Test
    public void move_DoesNotMoveSideways_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0,0, Direction.Up, "ip");
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertFalse(tc.move(tank, Direction.Left, 0));
    }

    @Test
    public void move_MovesForwards_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0,0, Direction.Up, "ip");
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertTrue(tc.move(tank, Direction.Up, 0));
    }

    @Test
    public void move_MovesBackwards_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0,0, Direction.Up, "ip");
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertTrue(tc.move(tank, Direction.Down, 0));
    }

    @Test
    public void move_MovesOnlyOncePerAllowedMoveTime() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0,0, Direction.Up, "ip");
        tank.setLastMoveTime(System.currentTimeMillis());
        IMGR = new InMemoryGameRepository();
        tank.setAllowedMoveInterval(10000);
        IMGR.join(0,"ip");
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
        tank = new Tank(0,0, Direction.Up, "ip");
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        tank.setNumberOfBullets(0);
        assertNotEquals(-1, tc.fire(tank, 1));
    }

    @Test
    public void fire_DoesNotFireMoreThanTwoBulletsAtATime_ReturnsTrue() throws LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0,0, Direction.Up, "ip");
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        tank.setNumberOfBullets(2);
        assertEquals(-1, tc.fire(tank, 1));
    }

    @Test
    public void fire_DoesNotFireWithin500MillisecondsAfterPreviousFire_ReturnsTrue() throws LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0,0, Direction.Up, "ip");
        tank.setLastMoveTime(System.currentTimeMillis());
        IMGR = new InMemoryGameRepository();
        tank.setAllowedFireInterval(10000);
        tank.setNumberOfBullets(0);
        IMGR.join(0,"ip");
        IMGR.fire(tank.getId(), 1);
        IMGR.fire(tank.getId(), 1);
        Game game = IMGR.getGame();
        tank = game.getTank("ip");
        assertEquals(1, tank.getNumberOfBullets());
    }
}