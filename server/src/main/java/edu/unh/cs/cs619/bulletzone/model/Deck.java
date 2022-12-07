package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.events.EventManager;

public class Deck extends FieldEntity {
    int destructValue, pos;
    public String name = "D";
    public String ip;
    private EventManager eventManager = EventManager.getInstance();
    public Deck(){
        this.destructValue = 1000;
        this.name = "F";
        this.ip = "";
    }

    public Deck(int destructValue, int pos){
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

    public Deck(String ip)
    {
        this.ip = ip;
    }

    @Override
    public FieldEntity copy() {
        return new Factory();
    }

    @Override
    public int getIntValue() {
        return 4;
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