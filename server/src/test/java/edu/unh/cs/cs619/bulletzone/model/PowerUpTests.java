package edu.unh.cs.cs619.bulletzone.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.unh.cs.cs619.bulletzone.model.Entities.GameResources.GravAssist;
import edu.unh.cs.cs619.bulletzone.model.Entities.Tanks.Tank;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Miscellaneous.Direction;
import edu.unh.cs.cs619.bulletzone.model.Miscellaneous.TankController;
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
        repo.insertResource(new GravAssist(), 18);
        repo.turn(id, Direction.Right);
        Thread.sleep(500);
        repo.move(id, Direction.Right);
        Thread.sleep(500);
        repo.move(id,Direction.Right);
        Thread.sleep(250);
        repo.move(id,Direction.Right);
        assertEquals( 7, repo.getEvents(System.currentTimeMillis()-10000).size());
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
