package edu.unh.cs.cs619.bulletzone.model;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUser;
import edu.unh.cs.cs619.bulletzone.datalayer.user.GameUserRepository;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.BuildingDoesNotExistException;
import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.datalayer.terrain.TerrainType;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.InvalidResourceTileType;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class TankControllerTest {
    private Tank tank;
    private TankController tc;
    private InMemoryGameRepository IMGR;
    private final String ip = "test";


    @Test
    public void turn_TurnsSideways_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, 0, Direction.Up, ip, 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertTrue(tc.turn(tank, Direction.Left));
    }

    @Test
    public void turn_TurnsForwardsOrBackwards_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, 0, Direction.Up, ip, 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertFalse(tc.turn(tank, Direction.Up));
        assertFalse(tc.turn(tank, Direction.Down));
    }

    @Test
    public void turn_TurnsOnlyOncePerAllowedMoveTime() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        IMGR.turn(tanks[0].getId(), Direction.Left);
        IMGR.turn(tanks[0].getId(), Direction.Down);
        IMGR.turn(tanks[0].getId(), Direction.Right);
        Game game = IMGR.getGame();
        assertEquals(Direction.Left, game.getTank(tanks[0].getId()).getDirection());
    }

    @Test
    public void move_DoesNotMoveSideways_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, 0, Direction.Up, ip, 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertFalse(tc.move(tank, Direction.Left, 0));
    }

    @Test
    public void move_MovesForwards_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, 0, Direction.Up, ip, 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertTrue(tc.move(tank, Direction.Up, 0));
    }

    @Test
    public void move_MovesBackwards_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, 0, Direction.Up, ip, 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        assertTrue(tc.move(tank, Direction.Down, 0));
    }

    @Test
    public void move_MovesOnlyOncePerAllowedMoveTime() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        IMGR.move(tanks[0].getId(), Direction.Up);
        long millis = System.currentTimeMillis() + 10000;
        IMGR.move(tanks[0].getId(), Direction.Down);
        IMGR.move(tanks[0].getId(), Direction.Right);
        Game game = IMGR.getGame();
        assertTrue(game.getTank(tanks[0].getId()).getLastMoveTime() <= millis);
    }

    @Test
    public void fire_SuccessfullyFires_ReturnsTrue() throws LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, 0, Direction.Up, ip, 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        tank.setNumberOfBullets(0);
        assertNotEquals(-1, tc.fire(tank, 1));
    }

    @Test
    public void fire_DoesNotFireMoreThanTwoBulletsAtATime_ReturnsTrue() throws LimitExceededException, TankDoesNotExistException {
        tank = new Tank(0, 0, Direction.Up, ip, 0);
        tank.setLastMoveTime(System.currentTimeMillis());
        tc = new TankController();
        tank.setNumberOfBullets(2);
        assertEquals(-1, tc.fire(tank, 1));
    }

    @Test
    public void fire_DoesNotFireWithin500MillisecondsAfterPreviousFire_ReturnsTrue() throws LimitExceededException, TankDoesNotExistException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        IMGR.fire(tanks[0].getId(), 1);
        IMGR.fire(tanks[0].getId(), 1);
        Game game = IMGR.getGame();
        assertEquals(1, game.getTank(tanks[0].getId()).getNumberOfBullets());
    }

    // testing basic moving, turning, and firing constraints for miner and builder

    @Test
    public void move_MinerMovingAtAppropriateInterval_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        Thread.sleep(1000); //Letting server catch up
        IMGR.move(tanks[1].getId(), Direction.Down);
        Thread.sleep(800);
        IMGR.move(tanks[1].getId(), Direction.Up);
        Game game = IMGR.getGame();
        assertEquals(5, game.getEvents(System.currentTimeMillis() - 10000).size());
    }

    @Test
    public void move_MinerMovingAtInappropriateInterval_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        Thread.sleep(1000); //Letting server catch up
        IMGR.move(tanks[1].getId(), Direction.Down);
        Thread.sleep(700);
        IMGR.move(tanks[1].getId(), Direction.Up);
        Game game = IMGR.getGame();
        assertEquals(4, game.getEvents(System.currentTimeMillis() - 10000).size());
    }

    @Test
    public void move_BuilderMovingAtAppropriateInterval_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        Thread.sleep(1000); //Letting server catch up
        IMGR.move(tanks[2].getId(), Direction.Down);
        Thread.sleep(1000);
        IMGR.move(tanks[2].getId(), Direction.Up);
        Game game = IMGR.getGame();
        assertEquals(5, game.getEvents(System.currentTimeMillis() - 10000).size());
    }

    @Test
    public void move_BuilderMovingAtInappropriateInterval_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        Thread.sleep(1000); //Letting server catch up
        IMGR.move(tanks[2].getId(), Direction.Down);
        Thread.sleep(900);
        IMGR.move(tanks[2].getId(), Direction.Up);
        Game game = IMGR.getGame();
        assertEquals(4, game.getEvents(System.currentTimeMillis() - 10000).size());
    }

    @Test
    public void turn_MinerTurningAtAppropriateInterval_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        Thread.sleep(1000); //Letting server catch up
        IMGR.turn(tanks[1].getId(), Direction.Left);
        Thread.sleep(800);
        IMGR.turn(tanks[1].getId(), Direction.Up);
        Game game = IMGR.getGame();
        assertEquals(5, game.getEvents(System.currentTimeMillis() - 10000).size());
    }

    @Test
    public void turn_BuilderTurningAtInappropriateInterval_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        Thread.sleep(1000); //Letting server catch up
        IMGR.move(tanks[2].getId(), Direction.Down);
        Thread.sleep(250);
        IMGR.move(tanks[2].getId(), Direction.Up);
        Game game = IMGR.getGame();
        assertEquals(4, game.getEvents(System.currentTimeMillis() - 10000).size());
    }

    @Test
    public void fire_BuilderFiringAtAppropriateInterval_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        Thread.sleep(1000); //Letting server catch up
        IMGR.fire(tanks[2].getId(), 1);
        Thread.sleep(1000);
        IMGR.fire(tanks[2].getId(), 1);
        Game game = IMGR.getGame();
        assertEquals(2, game.getTank(tanks[2].getId()).getNumberOfBullets());
    }

    @Test
    public void fire_MinerFiringAtInappropriateInterval_ReturnsFalse() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        Thread.sleep(1000); //Letting server catch up
        IMGR.fire(tanks[1].getId(), 1);
        Thread.sleep(150);
        IMGR.fire(tanks[1].getId(), 1);
        Game game = IMGR.getGame();
        assertEquals(1, game.getTank(tanks[1].getId()).getNumberOfBullets());
    }

    @Test
    public void fire_MinerFiringTooManyBullets_LeavesMaxNumberOfBullets() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        Thread.sleep(1000); //Letting server catch up
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
        Tank[] tanks = IMGR.join(0, ip);
        Thread.sleep(1000); //Letting server catch up
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
        Tank[] tanks = IMGR.join(0, ip);
        Thread.sleep(1000); //Letting server catch up
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
        Tank[] tanks = IMGR.join(0, ip);
        Thread.sleep(1000); //Letting server catch up
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
    public void move_MovePicksUpRock_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException, InvalidResourceTileType {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        Tank miner = tanks[1];
        FieldHolder currTerrain = miner.getParent();
        currTerrain = currTerrain.getNeighbor(Direction.Up);
        currTerrain.setFieldEntity(new Rock());
        Integer val = 1;
        //assertEquals(val, currTerrain.getTerrain().getIntValue());
        IMGR.move(tanks[1].getId(), Direction.Up);
        Thread.sleep(2000);
        assertEquals(val, tanks[1].getResourcesByResource(0));
    }

    @Test
    public void move_MovePicksUpThingamajig_DoesNotAddToTankBalance() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException, InvalidResourceTileType {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        Tank miner = tanks[1];
        FieldHolder currTerrain = miner.getParent();
        currTerrain = currTerrain.getNeighbor(Direction.Up);
        currTerrain.setFieldEntity(new Thingamajig());
        Integer val = 1;
        //assertEquals(val, currTerrain.getTerrain().getIntValue());
        IMGR.move(tanks[1].getId(), Direction.Up);
        Thread.sleep(2000);
        assertNotEquals(val, tanks[1].getResourcesByResource(0));
    }

    @Test
    public void move_MovePicksUpThingamajig_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException, InvalidResourceTileType {
        DataRepository data = new DataRepository(true);
        String username = "testuseronlyfortesting";
        data.validateUser(username, username, false);

        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        Tank tank = tanks[0];
        FieldHolder currTerrain = tank.getParent();
        currTerrain = currTerrain.getNeighbor(Direction.Up);

        // expect 10 credits added
        currTerrain.setFieldEntity(new Thingamajig(10, true));
        IMGR.move(tanks[1].getId(), Direction.Up);
        Thread.sleep(2000);

        double actualBalance = data.getUserAccountBalance(username);
        double expectedBalance = 1010.0;

        // set balance back to 1000 by subtracting 10
        // if this ever fails then the thingamajig test will always fail because modify balance isn't working there either
        if (actualBalance == expectedBalance) {
            // actually fix the balance if the test passed
            double sub10 = 10.0 - 20.0; // this is hard coded bc '-10' did not act like a negative number
            data.modifyAccountBalance(username, sub10);
        }

        assertEquals(expectedBalance, actualBalance, 0);
    }

    @Test
    public void mine_MoveCancelsMineAction_ReturnsTrue() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException, InvalidResourceTileType {
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0, ip);
        Thread.sleep(1000); //Letting server catch up
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
    public void moveTo_TankStartsInRandomLocation_NavigatesTo3LocationsCorrectly(){
        IMGR = new InMemoryGameRepository();
        Tank[] tanks = IMGR.join(0,ip);

        System.out.println("Controlling tank with id " + tanks[0].getId());

        Game g = IMGR.getGame();
        HashMap<String,Long> map = g.getTanks(tanks[0].getIp());

        Tank miner = g.getTank(map.get("miner"));
        Tank builder = g.getTank(map.get("builder"));

        miner.getParent().clearField();
        builder.getParent().clearField();

        int[][][] grid2d = IMGR.getGrid();

        System.out.println("Controlling tank with id " + tanks[0].getId());

        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                System.out.print(grid2d[i][j][1] + "\t");

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

                System.out.print(grid2d[i][j][1] + "\t");

            }
            System.out.println();
        }

        assertEquals(tanks[0].getParent().getPos(), 0);

        try {
            IMGR.moveTo(tanks[0].getId(), 240);
        } catch (TankDoesNotExistException e) {
            e.printStackTrace();
        }

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                System.out.print(grid2d[i][j][1] + "\t");

            }
            System.out.println();
        }

        assertEquals(tanks[0].getParent().getPos(), 240);

        try {
            IMGR.moveTo(tanks[0].getId(), 255);
        } catch (TankDoesNotExistException e) {
            e.printStackTrace();
        }

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){

                System.out.print(grid2d[i][j][1] + "\t");

            }
            System.out.println();
        }

        assertEquals(tanks[0].getParent().getPos(), 255);

    }

    @Test
    public void testMoveTank_moveIntoHilly_timingCorrect() throws BuildingDoesNotExistException, TankDoesNotExistException, LimitExceededException, IllegalTransitionException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        IMGR.create();
        Tank[] tank = IMGR.join(0,"test");
        Thread.sleep(1000); //Letting server catch up
        Long tankId = tank[0].getId();
        tank[0].getParent().getNeighbor(Direction.Up).clearField();


        IMGR.move(tankId,Direction.Up);
        tank[0] = IMGR.getGame().getTank(tankId);
        FieldHolder nexttile = tank[0].getParent().getNeighbor(Direction.Up);
        tank[0].getParent().getNeighbor(Direction.Up).clearField();

        nexttile.setTerrain(new Hilly());
        int movetoposition = tank[0].getParent().getNeighbor(Direction.Up).getPos();
        int position = tank[0].getParent().getPos();

        Thread.sleep(tank[0].getAllowedMoveInterval() - 100);
        assert(IMGR.move(tankId,Direction.Up) == false);
        Thread.sleep(2000);
        assert(IMGR.move(tankId,Direction.Up) == true);
        tank[0] = IMGR.getGame().getTank(tankId);
        position = tank[0].getParent().getPos();
        assert(position == movetoposition);
    }

    @Test
    public void testMoveBuilder_moveIntoMeadow_timingCorrect() throws BuildingDoesNotExistException, TankDoesNotExistException, LimitExceededException, IllegalTransitionException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        IMGR.create();
        Tank[] tank = IMGR.join(0,"test");
        Thread.sleep(1000); //Letting server catch up
        Long tankId = tank[2].getId();
        tank[2].getParent().getNeighbor(Direction.Up).clearField();

        IMGR.move(tankId,Direction.Up);
        tank[2] = IMGR.getGame().getTank(tankId);
        FieldHolder nexttile = tank[2].getParent().getNeighbor(Direction.Up);
        nexttile.clearField();
        nexttile.setTerrain(new Meadow());
        int movetoposition = tank[2].getParent().getNeighbor(Direction.Up).getPos();
        int position = tank[0].getParent().getPos();

        Thread.sleep(tank[2].getAllowedMoveInterval() - 100);
        assert(IMGR.move(tankId,Direction.Up) == false);
        Thread.sleep(2000);
        assert(IMGR.move(tankId,Direction.Up) == true);
        tank[2] = IMGR.getGame().getTank(tankId);
        position = tank[2].getParent().getPos();
        assert(position == movetoposition);
    }

    @Test
    public void testMoveMiner_moveIntoRocky_timingCorrect() throws BuildingDoesNotExistException, TankDoesNotExistException, LimitExceededException, IllegalTransitionException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        IMGR.create();
        Tank[] tank = IMGR.join(0,"test");
        Thread.sleep(1000); //Letting server catch up
        Long tankId = tank[1].getId();
        tank[2].getParent().getNeighbor(Direction.Up).clearField();


        IMGR.move(tankId,Direction.Up);
        tank[1] = IMGR.getGame().getTank(tankId);
        FieldHolder nexttile = tank[1].getParent().getNeighbor(Direction.Up);
        nexttile.clearField();
        nexttile.setTerrain(new Rocky());
        int movetoposition = tank[1].getParent().getNeighbor(Direction.Up).getPos();
        int position = tank[0].getParent().getPos();

        Thread.sleep(tank[1].getAllowedMoveInterval() - 100);
        assert(IMGR.move(tankId,Direction.Up) == false);
        Thread.sleep(2000);
        assert(IMGR.move(tankId,Direction.Up) == true);
        tank[1] = IMGR.getGame().getTank(tankId);
        position = tank[1].getParent().getPos();
        assert(position == movetoposition);
    }

    @Test
    public void testMoveMiner_moveIntoRoad_timingCorrect() throws BuildingDoesNotExistException, TankDoesNotExistException, LimitExceededException, IllegalTransitionException, InterruptedException {
        IMGR = new InMemoryGameRepository();
        IMGR.create();
        Tank[] tank = IMGR.join(0,"test");
        Thread.sleep(1000); //Letting server catch up
        Long tankId = tank[0].getId();
        tank[0].getParent().getNeighbor(Direction.Up).clearField();


        IMGR.move(tankId,Direction.Up);
        tank[0] = IMGR.getGame().getTank(tankId);
        FieldHolder nexttile = tank[0].getParent().getNeighbor(Direction.Up);
        nexttile.clearField();

        nexttile.setTerrain(new Meadow());
        nexttile.setImprovementEntity(new Road());
        int movetoposition = tank[0].getParent().getNeighbor(Direction.Up).getPos();
        int position = tank[0].getParent().getPos();

        Thread.sleep(tank[0].getAllowedMoveInterval() - 100);
        assert(IMGR.move(tankId,Direction.Up) == false);
        Thread.sleep(2000);
        assert(IMGR.move(tankId,Direction.Up) == true);
        tank[0] = IMGR.getGame().getTank(tankId);
        position = tank[0].getParent().getPos();
        assert(position == movetoposition);
    }

}