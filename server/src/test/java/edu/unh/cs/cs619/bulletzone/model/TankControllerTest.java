package edu.unh.cs.cs619.bulletzone.model;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.unh.cs.cs619.bulletzone.datalayer.terrain.TerrainType;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.InvalidResourceTileType;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class TankControllerTest {
    private Tank tank;
    private TankController tc;
    private InMemoryGameRepository IMGR;
    private final String ip = "test";


    @Test
    public void turn_TurnsSideways_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, ip, 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertTrue(tc.turn(tank, Direction.Left));
    }

    @Test
    public void turn_TurnsForwardsOrBackwards_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, ip, 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertFalse(tc.turn(tank, Direction.Up));
        assertFalse(tc.turn(tank, Direction.Down));
    }

    @Test
    public void turn_TurnsOnlyOncePerAllowedMoveTime() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        IMGR.turn(tanks[0].getId(), Direction.Left);
        IMGR.turn(tanks[0].getId(), Direction.Down);
        IMGR.turn(tanks[0].getId(), Direction.Right);
        Game game = IMGR.getGame();
        assertEquals(Direction.Left, game.getTank(tanks[0].getId()).getDirection());
    }

    @Test
    public void move_DoesNotMoveSideways_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, ip, 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertFalse(tc.move(tank, Direction.Left, 0));
    }

    @Test
    public void move_MovesForwards_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, ip, 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertTrue(tc.move(tank, Direction.Up, 0));
    }

    @Test
    public void move_MovesBackwards_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, ip, 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertTrue(tc.move(tank, Direction.Down, 0));
    }

    @Test
    public void move_MovesOnlyOncePerAllowedMoveTime() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        IMGR.move(tanks[0].getId(), Direction.Up);
        long millis = System.currentTimeMillis() + 10000;
        IMGR.move(tanks[0].getId(), Direction.Down);
        IMGR.move(tanks[0].getId(), Direction.Right);
        Game game = IMGR.getGame();
        assertTrue(game.getTank(tanks[0].getId()).getLastMoveTime() <= millis);
    }

    @Test
    public void fire_SuccessfullyFires_ReturnsTrue() throws LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, ip, 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        tank.setNumberOfBullets(0);
        assertNotEquals(-1, tc.fire(tank, 1));
    }

    @Test
    public void fire_DoesNotFireMoreThanTwoBulletsAtATime_ReturnsTrue() throws LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, Direction.Up, ip, 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        tank.setNumberOfBullets(2);
        assertEquals(-1, tc.fire(tank, 1));
    }

    @Test
    public void fire_DoesNotFireWithin500MillisecondsAfterPreviousFire_ReturnsTrue() throws LimitExceededException, TankDoesNotExistException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        IMGR.fire(tanks[0].getId(), 1);
        IMGR.fire(tanks[0].getId(), 1);
        Game game = IMGR.getGame();
        assertEquals(1, game.getTank(tanks[0].getId()).getNumberOfBullets());
    }

    // testing basic moving, turning, and firing constraints for miner and builder

    @Test
    public void move_MinerMovingAtAppropriateInterval_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        IMGR.move(tanks[1].getId(), Direction.Down);
        Thread.sleep(800);
        IMGR.move(tanks[1].getId(), Direction.Up);
        Game game = IMGR.getGame();
        assertEquals(5, game.getEvents(System.currentTimeMillis() - 10000).size());
    }

    @Test
    public void move_MinerMovingAtInappropriateInterval_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        IMGR.move(tanks[1].getId(), Direction.Down);
        Thread.sleep(700);
        IMGR.move(tanks[1].getId(), Direction.Up);
        Game game = IMGR.getGame();
        assertEquals(4, game.getEvents(System.currentTimeMillis() - 10000).size());
    }

    @Test
    public void move_BuilderMovingAtAppropriateInterval_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        IMGR.move(tanks[2].getId(), Direction.Down);
        Thread.sleep(1000);
        IMGR.move(tanks[2].getId(), Direction.Up);
        Game game = IMGR.getGame();
        assertEquals(5, game.getEvents(System.currentTimeMillis() - 10000).size());
    }

    @Test
    public void move_BuilderMovingAtInappropriateInterval_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        IMGR.move(tanks[2].getId(), Direction.Down);
        Thread.sleep(900);
        IMGR.move(tanks[2].getId(), Direction.Up);
        Game game = IMGR.getGame();
        assertEquals(4, game.getEvents(System.currentTimeMillis() - 10000).size());
    }

    @Test
    public void turn_MinerTurningAtAppropriateInterval_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        IMGR.turn(tanks[1].getId(), Direction.Left);
        Thread.sleep(800);
        IMGR.turn(tanks[1].getId(), Direction.Up);
        Game game = IMGR.getGame();
        assertEquals(5, game.getEvents(System.currentTimeMillis() - 10000).size());
    }

    @Test
    public void turn_BuilderTurningAtInappropriateInterval_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        IMGR.move(tanks[2].getId(), Direction.Down);
        Thread.sleep(250);
        IMGR.move(tanks[2].getId(), Direction.Up);
        Game game = IMGR.getGame();
        assertEquals(4, game.getEvents(System.currentTimeMillis() - 10000).size());
    }

    @Test
    public void fire_BuilderFiringAtAppropriateInterval_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        IMGR.fire(tanks[2].getId(), 1);
        Thread.sleep(1000);
        IMGR.fire(tanks[2].getId(), 1);
        Game game = IMGR.getGame();
        assertEquals(2, game.getTank(tanks[2].getId()).getNumberOfBullets());
    }

    @Test
    public void fire_MinerFiringAtInappropriateInterval_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        IMGR.fire(tanks[1].getId(), 1);
        Thread.sleep(150);
        IMGR.fire(tanks[1].getId(), 1);
        Game game = IMGR.getGame();
        assertEquals(1, game.getTank(tanks[1].getId()).getNumberOfBullets());
    }

    @Test
    public void fire_MinerFiringTooManyBullets_LeavesMaxNumberOfBullets() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        //#1
        IMGR.fire(tanks[1].getId(), 1);
        IMGR.turn(tanks[1].getId(), Direction.Right);
        Thread.sleep(800);
        IMGR.move(tanks[1].getId(), Direction.Right);
        Thread.sleep(1000);
        IMGR.turn(tanks[1].getId(), Direction.Up);
        Thread.sleep(800);
        //#2
        IMGR.fire(tanks[1].getId(), 1);
        IMGR.turn(tanks[1].getId(), Direction.Right);
        Thread.sleep(800);
        IMGR.move(tanks[1].getId(), Direction.Right);
        Thread.sleep(1000);
        IMGR.turn(tanks[1].getId(), Direction.Up);
        Thread.sleep(800);
        //#3
        IMGR.fire(tanks[1].getId(), 1);
        IMGR.turn(tanks[1].getId(), Direction.Right);
        Thread.sleep(800);
        IMGR.move(tanks[1].getId(), Direction.Right);
        Thread.sleep(1000);
        IMGR.turn(tanks[1].getId(), Direction.Up);
        Thread.sleep(800);
        //#4
        IMGR.fire(tanks[1].getId(), 1);
        IMGR.turn(tanks[1].getId(), Direction.Right);
        Thread.sleep(800);
        IMGR.move(tanks[1].getId(), Direction.Right);
        Thread.sleep(1000);
        IMGR.turn(tanks[1].getId(), Direction.Up);
        Thread.sleep(800);
        //#5
        IMGR.fire(tanks[1].getId(), 1);

        Game game = IMGR.getGame();
        assertEquals(4, game.getTank(tanks[1].getId()).getNumberOfBullets());
    }

    @Test
    public void mine_MinerMiningResourceAddsIronResource_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException, InvalidResourceTileType {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        Tank miner = tanks[1];
        FieldHolder currTerrain = miner.getParent();
        currTerrain.setTerrain(new Hilly());
        Integer val = 2;
        //assertEquals(val, currTerrain.getTerrain().getIntValue());
        IMGR.mine(tanks[1].getId());
        Thread.sleep(2000);
        IMGR.turn(tanks[1].getId(), Direction.Right);
        assertEquals(val, tanks[1].getResourcesByResource(1));
    }

    @Test
    public void mine_MinerMiningResourceAddsClayResource_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException, InvalidResourceTileType {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        Tank miner = tanks[1];
        FieldHolder currTerrain = miner.getParent();
        currTerrain.setTerrain(new Meadow());
        Integer val = 2;
        //assertEquals(val, currTerrain.getTerrain().getIntValue());
        IMGR.mine(tanks[1].getId());
        Thread.sleep(2000);
        IMGR.turn(tanks[1].getId(), Direction.Right);
        assertEquals(val, tanks[1].getResourcesByResource(2));
    }

    @Test
    public void mine_MinerMiningResourceAddsRockResource_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException, InvalidResourceTileType {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        Tank miner = tanks[1];
        FieldHolder currTerrain = miner.getParent();
        currTerrain.setTerrain(new Rocky());
        Integer val = 2;
        //assertEquals(val, currTerrain.getTerrain().getIntValue());
        IMGR.mine(tanks[1].getId());
        Thread.sleep(2000);
        IMGR.turn(tanks[1].getId(), Direction.Right);
        assertEquals(val, tanks[1].getResourcesByResource(0));
    }

    @Test
    public void mine_MoveCancelsMineAction_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException, InvalidResourceTileType {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(ip);
        Tank miner = tanks[1];
        FieldHolder currTerrain = miner.getParent();
        currTerrain.setTerrain(new Meadow());
        Integer val = 1;
        //assertEquals(val, currTerrain.getTerrain().getIntValue());
        IMGR.mine(tanks[1].getId());
        IMGR.turn(tanks[1].getId(), Direction.Right);
        Thread.sleep(2000);
        assertEquals(val, tanks[1].getResourcesByResource(2));
    }

    @Test
    public void moveToTest(){
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join("");
        int[][] grid2d = IMGR.getGrid();

        System.out.println("Controlling tank with id " + tanks[0].getId());

        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                System.out.print(grid2d[i][j] + "\t");

            }
            System.out.println();
        }

        //test command
        try {
            IMGR.moveTo(tanks[0].getId(), 0);
        } catch (TankDoesNotExistException e) {
            e.printStackTrace();
        }


        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                System.out.print(grid2d[i][j] + "\t");

            }
            System.out.println();
        }

        /*

        //strictly horizontal movement
        try {
            IMGR.moveTo(tanks[0].getId(), 15);
        } catch (TankDoesNotExistException e) {
            e.printStackTrace();
        }

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                System.out.print(grid2d[i][j] + "\t");

            }
            System.out.println();
        }

        //try strictly vertical movement
        try {
            IMGR.moveTo(tanks[0].getId(), 95);
        } catch (TankDoesNotExistException e) {
            e.printStackTrace();
        }

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                System.out.print(grid2d[i][j] + "\t");

            }
            System.out.println();
        }

        try {
            IMGR.moveTo(tanks[1].getId(), 154);
        } catch (TankDoesNotExistException e) {
            e.printStackTrace();
        }

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                System.out.print(grid2d[i][j] + "\t");

            }
            System.out.println();
        }

        try {
            IMGR.moveTo(tanks[1].getId(), 43);
        } catch (TankDoesNotExistException e) {
            e.printStackTrace();
        }

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                System.out.print(grid2d[i][j] + "\t");

            }
            System.out.println();
        }

         */

    }

}