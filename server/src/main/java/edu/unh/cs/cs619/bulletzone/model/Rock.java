package edu.unh.cs.cs619.bulletzone.model;

public class Rock extends FieldResource {
    int credits = 25;
    int pos;

    public Rock(){

    }

    public Rock(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 502;
    }

    @Override
    public FieldResource copy() {
        return new Rock();
    }

    @Override
    public String toString() {
        return "RB";
    }

    public int getPos(){
        return pos;
    }

    public int getCredits(){
        return credits;
    }
}
