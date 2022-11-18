package edu.unh.cs.cs619.bulletzone.model;

public class Thingamajig extends FieldResource {
    int credits;
    int pos;

    public Thingamajig(){
        credits = (int) (Math.random() * (200)) + 1;; // TODO randomize 1,10000 with average 100?
    }

    public Thingamajig(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 7;
    }

    @Override
    public FieldResource copy() {
        return new Thingamajig();
    }

    @Override
    public String toString() {
        return "TB";
    }

    public int getPos(){
        return pos;
    }

    public int getCredits(){
        return credits;
    }
}
