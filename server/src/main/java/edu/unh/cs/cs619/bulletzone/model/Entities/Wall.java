package edu.unh.cs.cs619.bulletzone.model.Entities;

import edu.unh.cs.cs619.bulletzone.events.DestroyWallEvent;
import edu.unh.cs.cs619.bulletzone.events.EventManager;

public class Wall extends FieldEntity {
    int destructValue, pos;
    public String name = "W";
    private EventManager eventManager = EventManager.getInstance();
    public Wall(){
        this.destructValue = 1000;
        this.name = "IW";
    }

    public Wall(int destructValue, int pos){
        this.destructValue = destructValue;
        this.pos = pos;
    }

    @Override
    public void hit(int damage){
        if (destructValue < 1000 && destructValue < 2000 ){
            destructValue -= damage;
            if(destructValue <= 1000){
                eventManager.addEvent(new DestroyWallEvent(pos));
                parent.clearField();
                parent = null;
            }
        }

    }

    public Wall(int destructValue)
    {
        this.destructValue = destructValue;
    }
    @Override
    public FieldEntity copy() {
        return new Wall();
    }

    @Override
    public int getIntValue() {
        return destructValue;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getPos(){
        return pos;
    }
    @Override
    public int getLife(){return this.destructValue - 1000;}
}
