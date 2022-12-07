package edu.unh.cs.cs619.bulletzone.model.Entities.Tanks;

 public abstract class PowerUp {

     public abstract long getAllowedMoveInterval();
     public abstract long getAllowedTurnInterval();
     public abstract long getAllowedFireInterval();
     public abstract int getAllowedNumberOfBullets();

     public abstract PowerUp powerDown();
}
