package edu.unh.cs.cs619.bulletzone.model;

public class Thingamajig extends FieldResource {
    int credits;
    int pos;

    public Thingamajig(){
        credits = 1; // TODO randomize
    }

    public Thingamajig(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 502;
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
