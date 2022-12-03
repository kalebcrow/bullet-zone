package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.events.EventManager;

public class Factory extends FieldEntity {
    int destructValue, pos;
    public String ip;
    public String name = "F";
    private EventManager eventManager = EventManager.getInstance();
    public Factory(){
        this.destructValue = 1000;
        this.name = "F";
        this.ip = "";
    }

    public Factory(int destructValue, int pos){
        this.destructValue = destructValue;
        this.pos = pos;
    }

    @Override
    public void hit(int damage){
        if (destructValue < 1000 && destructValue < 2000 ){
            destructValue -= damage;
            if(destructValue <= 1000){
                //eventManager.addEvent(new DestroyWallEvent(pos));
                parent.clearField();
                parent = null;
            }
        }

    }

    public Factory(String ip)
    {
        this.ip = ip;
    }

    @Override
    public FieldEntity copy() {
        return new Factory();
    }

    @Override
    public int getIntValue() {
        return 4000;
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