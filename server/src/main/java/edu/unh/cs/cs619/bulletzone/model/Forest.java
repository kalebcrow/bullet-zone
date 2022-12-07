package edu.unh.cs.cs619.bulletzone.model;

public class Forest extends FieldTerrain {
    double speed = 1;
    String delayType = "miners";
    int pos;

    public Forest(){

    }

    public Forest(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 3;
    }

    @Override
    public FieldTerrain copy() {
        return new Forest();
    }

    @Override
    public String toString() {
        return "F";
    }

    public int getPos(){
        return pos;
    }
}
