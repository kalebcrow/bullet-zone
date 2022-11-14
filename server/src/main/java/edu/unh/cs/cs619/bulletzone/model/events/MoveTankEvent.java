package edu.unh.cs.cs619.bulletzone.model.events;


import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.events.GridEvent;

public class MoveTankEvent extends GridEvent {

    public MoveTankEvent(Long tankID, byte direction, String terrain) {
        this.ID = Math.toIntExact(tankID);
        this.direction = direction;
        this.type = "moveTank";
        this.time = System.currentTimeMillis();
        this.terrain = terrain;
    }

}
