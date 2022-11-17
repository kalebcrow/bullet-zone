package edu.unh.cs.cs619.bulletzone.events;

public class DamageEvent extends GridEvent{
     public DamageEvent(Integer ID, int damage){
         this.ID = ID;
         this.pos = damage;
         this.type = "damage";
         this.time = System.currentTimeMillis();
     }
}
