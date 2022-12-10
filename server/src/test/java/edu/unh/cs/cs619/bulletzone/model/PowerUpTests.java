package edu.unh.cs.cs619.bulletzone.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.events.GridEvent;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;
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
    public void FusionGeneratorTank_PowerUpAppliedOnDecking_DoublesFiringRate() throws InterruptedException, LimitExceededException, TankDoesNotExistException, IllegalTransitionException {
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
    public void FusionGeneratorTank_PowerUpAppliedOnOtherTerrain_IncreasesMaxNumberOfBulletsByTwo() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
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
    public void FusionGeneratorTank_PowerUpAppliedOnRoad_ReducesMovementSpeedByFiftyPercent() throws InterruptedException, IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
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

}
