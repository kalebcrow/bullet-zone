package edu.unh.cs.cs619.bulletzone.model;

 public abstract class PowerUp {

     protected FieldResource fieldElement;

     public abstract long getAllowedMoveInterval();
     public abstract long getAllowedTurnInterval();
     public abstract long getAllowedFireInterval();
     public abstract int getAllowedNumberOfBullets();


     public abstract PowerUp powerDown();

     public FieldResource getFieldElement() {
         return fieldElement;
     }

 }
