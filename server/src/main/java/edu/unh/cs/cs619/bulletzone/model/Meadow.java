package edu.unh.cs.cs619.bulletzone.model;

public class Hilly extends FieldEntity {
    double speed = 0.5;
    int pos;

    public Hilly(){

    }

    public Hilly(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 10;
    }

    @Override
    public FieldEntity copy() {
        return new Hilly();
    }

    @Override
    public String toString() {
        return "H";
    }

    public int getPos(){
        return pos;
    }
}
