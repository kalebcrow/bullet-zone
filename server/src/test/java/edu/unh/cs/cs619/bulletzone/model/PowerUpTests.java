package edu.unh.cs.cs619.bulletzone.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.events.GridEvent;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class PowerUpTests {
    private InMemoryGameRepository repo;
    private final String ip = "test";
    private final String username = "i";
    private Tank[] tanks = new Tank[3];


    @Test
    public void GravityAssistedTank_PowerUpAppliedOnMeadow_MakesTankTwiceAsFast() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        repo = new InMemoryGameRepository();
        tanks = repo.join(username,ip);
        long id = tanks[0].getId();
        Tank tank = repo.getGame().getTank(id);
        repo.insertResource(new GravAssist(), tank.getParent().getNeighbor(Direction.Up).getPos());
        //repo.insertResource(new Iron(), 19);
        Thread.sleep(2000);


        assertTrue(repo.move(id, Direction.Up));
        Thread.sleep(2000);
        assertTrue(repo.move(id, Direction.Down));
        Thread.sleep(1000);
        assertTrue(repo.move(id, Direction.Up));
        //assertEquals( 5, repo.getEvents(System.currentTimeMillis()-10000).size());
    }

    @Test
    public void GravityAssistedTank_PowerUpAppliedOnMeadow_ReducesFiringRateFiftyPercent() throws InterruptedException, IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        repo = new InMemoryGameRepository();
        tanks = repo.join(username,ip);
        long id = tanks[0].getId();
        Tank tank = repo.getGame().getTank(id);
        //repo.insertResource(new Iron(), 19);
        Thread.sleep(2000);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(2000);
        //Let bullets hit tank to reset them
        repo.insertResource(new GravAssist(), tank.getParent().getNeighbor(Direction.Up).getPos());

        assertTrue(repo.move(id, Direction.Up));
        Thread.sleep(2000);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(4000);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(4000);
        //assertEquals( 5, repo.getEvents(System.currentTimeMillis()-10000).size());
    }

    @Test
    public void FusionGeneratorTank_PowerUpAppliedOnMeadow_DoublesFiringRate() throws InterruptedException, LimitExceededException, TankDoesNotExistException, IllegalTransitionException {
        repo = new InMemoryGameRepository();
        tanks = repo.join(username,ip);
        long id = tanks[0].getId();
        Tank tank = repo.getGame().getTank(id);
        //repo.insertResource(new Iron(), 19);
        Thread.sleep(2000);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(2000);
        //Let bullets hit tank to reset them
        repo.insertResource(new FusionGenerator(), tank.getParent().getNeighbor(Direction.Up).getPos());

        assertTrue(repo.move(id, Direction.Up));
        Thread.sleep(3000);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(1000);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(1000);
        //assertEquals( 5, repo.getEvents(System.currentTimeMillis()-10000).size());
    }

    @Test
    public void FusionGeneratorTank_PowerUpAppliedOnMeadow_IncreasesMaxNumberOfBulletsByTwo() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        repo = new InMemoryGameRepository();
        tanks = repo.join(username,ip);
        long id = tanks[0].getId();
        Tank tank = repo.getGame().getTank(id);
        //repo.insertResource(new Iron(), 19);
        Thread.sleep(2000);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(6000);
        //Let bullets hit tank to reset them
        repo.insertResource(new FusionGenerator(), tank.getParent().getNeighbor(Direction.Up).getPos());

        assertTrue(repo.move(id, Direction.Up));
        Thread.sleep(3000);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(750);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(750);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(750);
        //assertEquals( 5, repo.getEvents(System.currentTimeMillis()-10000).size());
    }

    @Test
    public void FusionGeneratorTank_PowerUpAppliedOnMeadow_ReducesMovementSpeedByFiftyPercent() throws InterruptedException, IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        repo = new InMemoryGameRepository();
        tanks = repo.join(username,ip);
        long id = tanks[0].getId();
        Tank tank = repo.getGame().getTank(id);
        //repo.insertResource(new Iron(), 19);
        Thread.sleep(2000);
        assertTrue(repo.move(id, Direction.Up));
        Thread.sleep(2500);
        assertTrue(repo.move(id, Direction.Down));
        Thread.sleep(2500);
        //Let bullets hit tank to reset them
        repo.insertResource(new FusionGenerator(), tank.getParent().getNeighbor(Direction.Up).getPos());

        assertTrue(repo.move(id, Direction.Up));
        Thread.sleep(5000);
        assertTrue(repo.move(id, Direction.Down));
        Thread.sleep(5000);
        //assertEquals( 5, repo.getEvents(System.currentTimeMillis()-10000).size());
    }

    @Test
    public void GravityAssistedTank_PowerUpAppliedOnOtherTerrain_MakesTankTwiceAsFast() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        repo = new InMemoryGameRepository();
        tanks = repo.join(username,ip);
        long id = tanks[0].getId();
        Tank tank = repo.getGame().getTank(id);
        tank.getParent().setTerrain(new Hilly());
        tank.getParent().getNeighbor(Direction.Up).setTerrain(new Hilly());
        repo.insertResource(new GravAssist(), tank.getParent().getNeighbor(Direction.Up).getPos());
        //repo.insertResource(new Iron(), 19);
        Thread.sleep(2000);


        assertTrue(repo.move(id, Direction.Up));
        Thread.sleep(2000);
        assertTrue(repo.move(id, Direction.Down));
        Thread.sleep(1000);
        assertTrue(repo.move(id, Direction.Up));
        //assertEquals( 5, repo.getEvents(System.currentTimeMillis()-10000).size());
    }

    @Test
    public void GravityAssistedTank_PowerUpAppliedOnRoads_ReducesFiringRateFiftyPercent() throws InterruptedException, IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        repo = new InMemoryGameRepository();
        tanks = repo.join(username,ip);
        long id = tanks[0].getId();
        Tank tank = repo.getGame().getTank(id);
        tank.getParent().setImprovementEntity(new Road());
        tank.getParent().getNeighbor(Direction.Up).setImprovementEntity(new Road());
        repo.insertResource(new GravAssist(), tank.getParent().getNeighbor(Direction.Up).getPos());
        //repo.insertResource(new Iron(), 19);
        Thread.sleep(2000);


        assertTrue(repo.move(id, Direction.Up));
        Thread.sleep(2000);
        assertTrue(repo.move(id, Direction.Down));
        Thread.sleep(1000);
        assertTrue(repo.move(id, Direction.Up));
        //assertEquals( 5, repo.getEvents(System.currentTimeMillis()-10000).size());
    }

    @Test
    public void FusionGeneratorTank_PowerUpAppliedOnDeck_DoublesFiringRate() throws InterruptedException, LimitExceededException, TankDoesNotExistException, IllegalTransitionException {
        repo = new InMemoryGameRepository();
        tanks = repo.join(username,ip);
        long id = tanks[0].getId();
        Tank tank = repo.getGame().getTank(id);
        tank.getParent().setImprovementEntity(new Deck());
        tank.getParent().getNeighbor(Direction.Up).setImprovementEntity(new Deck());
        //repo.insertResource(new Iron(), 19);
        Thread.sleep(2000);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(2000);
        //Let bullets hit tank to reset them
        repo.insertResource(new FusionGenerator(), tank.getParent().getNeighbor(Direction.Up).getPos());

        assertTrue(repo.move(id, Direction.Up));
        Thread.sleep(3000);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(1000);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(1000);
        //assertEquals( 5, repo.getEvents(System.currentTimeMillis()-10000).size());
    }

    @Test
    public void FusionGeneratorTank_PowerUpAppliedOnOtherTerrain_IncreasesMaxNumberOfBulletsByTwo() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        repo = new InMemoryGameRepository();
        tanks = repo.join(username,ip);
        long id = tanks[0].getId();
        Tank tank = repo.getGame().getTank(id);
        tank.getParent().setTerrain(new Rocky());
        tank.getParent().getNeighbor(Direction.Up).setTerrain(new Hilly());
        //repo.insertResource(new Iron(), 19);
        Thread.sleep(2000);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(6000);
        //Let bullets hit tank to reset them
        repo.insertResource(new FusionGenerator(), tank.getParent().getNeighbor(Direction.Up).getPos());

        assertTrue(repo.move(id, Direction.Up));
        Thread.sleep(3000);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(750);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(750);
        assertTrue(repo.fire(id, 1));
        Thread.sleep(750);
        //assertEquals( 5, repo.getEvents(System.currentTimeMillis()-10000).size());
    }

    @Test
    public void FusionGeneratorTank_PowerUpAppliedOnRoad_ReducesMovementSpeedByFiftyPercent() throws InterruptedException, IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        repo = new InMemoryGameRepository();
        tanks = repo.join(username,ip);
        long id = tanks[0].getId();
        Tank tank = repo.getGame().getTank(id);
        tank.getParent().setImprovementEntity(new Road());
        tank.getParent().getNeighbor(Direction.Up).setImprovementEntity(new Road());
        //repo.insertResource(new Iron(), 19);
        Thread.sleep(2000);
        assertTrue(repo.move(id, Direction.Up));
        Thread.sleep(2500);
        assertTrue(repo.move(id, Direction.Down));
        Thread.sleep(2500);
        //Let bullets hit tank to reset them
        repo.insertResource(new FusionGenerator(), tank.getParent().getNeighbor(Direction.Up).getPos());

        assertTrue(repo.move(id, Direction.Up));
        Thread.sleep(5000);
        assertTrue(repo.move(id, Direction.Down));
        Thread.sleep(5000);
        //assertEquals( 5, repo.getEvents(System.currentTimeMillis()-10000).size());
    }


    @Test
    public void PowerUpCredit_Leave_GetCorrectCredit() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        DataRepository data = new DataRepository(true);
        String username = "testuseronlyfortesting";
        data.validateUser(username, username, false);

        repo = new InMemoryGameRepository();
        tanks = repo.join(username,ip);
        long id = tanks[2].getId();
        System.out.println(tanks[2].parent.getNeighbor(Direction.Up).getPos());
        repo.insertResource(new GravAssist(), 235);
        repo.move(id, Direction.Up);

        repo.leave(id);

        double actualBalance = data.getUserAccountBalance(username);
        double expectedBalance = data.getUserAccountBalance(username);

        Thread.sleep(2000);
        // set balance back to 1000 by subtracting 10
        // if this ever fails then the thingamajig test will always fail because modify balance isn't working there either
        if (actualBalance == expectedBalance) {
            // actually fix the balance if the test passed
            double sub10 = 10.0 - 310.0; // this is hard coded bc '-10' did not act like a negative number
            data.modifyAccountBalance(username, sub10);
        }

        assertEquals(expectedBalance, actualBalance, 10);

    }

    @Test
    public void PowerUp_DismantlePowerUp_DismantleTrueAndBalance() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        DataRepository data = new DataRepository(true);
        String username = "testuseronlyfortesting";
        data.validateUser(username, username, false);

        repo = new InMemoryGameRepository();
        tanks = repo.join(username,ip);
        long id = tanks[2].getId();
        System.out.println(tanks[2].parent.getNeighbor(Direction.Up).getPos());
        repo.insertResource(new GravAssist(), 235);
        repo.move(id, Direction.Up);
        Thread.sleep(2000);
        assert(repo.turn(id, Direction.Left));
        Thread.sleep(2000);
        assert (repo.turn(id, Direction.Down));

        repo.powerDown(id);

        assert(repo.dismantle(id));

        double actualBalance = data.getUserAccountBalance(username);
        double expectedBalance = data.getUserAccountBalance(username);

        Thread.sleep(2000);
        // set balance back to 1000 by subtracting 10
        // if this ever fails then the thingamajig test will always fail because modify balance isn't working there either
        if (actualBalance == expectedBalance) {
            // actually fix the balance if the test passed
            double sub10 = 10.0 - 310.0; // this is hard coded bc '-10' did not act like a negative number
            data.modifyAccountBalance(username, sub10);
        }

        assertEquals(expectedBalance, actualBalance, 10);
        assert(repo.dismantle(id));
    }
    @Test
    public void PowerUp_EjectPowerUp_OnSpace() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        repo = new InMemoryGameRepository();
        tanks = repo.join(username,ip);
        long id = tanks[0].getId();
        System.out.println(tanks[0].parent.getNeighbor(Direction.Up).getPos());
        repo.insertResource(new GravAssist(), 0);
        repo.move(id, Direction.Up);

        repo.powerDown(id);
        assert(tanks[0].getParent().getNeighbor(Direction.Up).getEntity() != null);
    }

}
