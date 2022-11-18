package edu.unh.cs.cs619.bulletzone.model;

public class Rocky extends FieldTerrain {
    double speed = 1;
    int pos;

    public Rocky(){}

    public Rocky(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 1;
    }

    @Override
    public FieldTerrain copy() {
        return new Rocky();
    }

    @Override
    public String toString() {
        return "R";
    }

    public int getPos(){
        return pos;
    }
}
