package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.events.EventManager;

public abstract class PowerUp {

     protected FieldResource fieldElement;
     protected EventManager eventManager = EventManager.getInstance();

     public abstract long getAllowedMoveInterval();
     public abstract long getAllowedTurnInterval();
     public abstract long getAllowedFireInterval();
     public abstract int getAllowedNumberOfBullets();
     public abstract int getMaxHealth();
     public abstract void setMaxHealth(int health);
     public abstract int getHealth();
     public abstract void setHealth(int health);
     public abstract long getId();


     public abstract PowerUp powerDown();

     public FieldResource getFieldElement() {
         return fieldElement;
     }

 }
