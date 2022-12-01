package edu.unh.cs.cs619.bulletzone.model;

public class Water extends FieldTerrain {
    double speed = 1;
    String delayType = "builders";
    int pos;

    public Water(){

    }

    public Water(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 50;
    }

    @Override
    public FieldTerrain copy() {
        return new Water();
    }

    @Override
    public String toString() {
        return "W";
    }

    public int getPos(){
        return pos;
    }
}
