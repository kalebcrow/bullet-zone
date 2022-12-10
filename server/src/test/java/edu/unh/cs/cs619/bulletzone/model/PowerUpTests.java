package edu.unh.cs.cs619.bulletzone.model;

import static org.junit.Assert.assertEquals;

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
    public void GravityAssistedTank_PowerUpApplied_MakesTankTwiceAsFast() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        repo = new InMemoryGameRepository();
        tanks = repo.join(username,ip);
        long id = tanks[0].getId();
        repo.insertResource(new GravAssist(), 17);
        repo.insertResource(new Iron(), 19);
        repo.turn(id, Direction.Right);
        Thread.sleep(500);


        repo.move(id, Direction.Right);
        Thread.sleep(300);
        repo.move(id, Direction.Right);
        Thread.sleep(250);
        repo.move(id, Direction.Right);
        LinkedList<GridEvent> action = repo.getEvents(System.currentTimeMillis()-10000);
        for(int i=0;i<action.size();i++) System.out.println(action.get(i).getType());
        //assertEquals( 5, repo.getEvents(System.currentTimeMillis()-10000).size());
    }

    @Test
    public void GravityAssistedTank_PowerUpApplied_ReducesFiringRateFiftyPercent(){

    }

    @Test
    public void FusionGeneratorTank_PowerUpApplied_DoublesFiringRate(){

    }

    @Test
    public void FusionGeneratorTank_PowerUpApplied_IncreasesMaxNumberOfBulletsByTwo(){

    }

    @Test
    public void FusionGeneratorTank_PowerUpApplied_ReducesMovementSpeedByFiftyPercent(){

    }

}
