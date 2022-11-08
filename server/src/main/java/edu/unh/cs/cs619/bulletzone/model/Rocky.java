package edu.unh.cs.cs619.bulletzone.model;

public class Rocky extends FieldEntity {
    double speed = 1;
    int pos;

    public Rocky(){}

    public Rocky(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 20;
    }

    @Override
    public FieldEntity copy() {
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
