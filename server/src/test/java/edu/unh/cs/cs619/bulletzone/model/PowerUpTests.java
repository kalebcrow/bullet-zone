package edu.unh.cs.cs619.bulletzone.model;

import org.junit.Test;

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
    public void GravityAssistedTank_PowerUpApplied_MakesTankTwiceAsFast() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
        repo = new InMemoryGameRepository();
        tanks = repo.join(username,ip);
        long id = tanks[0].getId();
        repo.turn(id, Direction.Right);
        Thread.sleep();
        repo.move(id,Direction.Right);
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
