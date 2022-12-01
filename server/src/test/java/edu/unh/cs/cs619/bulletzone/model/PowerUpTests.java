package edu.unh.cs.cs619.bulletzone.model;

import org.junit.Test;

import edu.unh.cs.cs619.bulletzone.model.Miscellaneous.TankController;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class PowerUpTests {
    private TankController tc;
    private final String ip = "test";


    @Test
    public void GravityAssistedTank_PowerUpApplied_MakesTankTwiceAsFast(){
        InMemoryGameRepository repo = new InMemoryGameRepository();

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
