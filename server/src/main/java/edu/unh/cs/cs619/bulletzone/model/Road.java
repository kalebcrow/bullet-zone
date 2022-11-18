package edu.unh.cs.cs619.bulletzone.model;

public class Road extends FieldEntity {
    int pos;

    public Road(){}

    public Road(int pos){
        this.pos = pos;
    }

    @Override
    public FieldEntity copy() {
        return new Wall();
    }

    @Override
    public int getIntValue() {
        return 0;
    }

    @Override
    public String toString() {
        return "R";
    }

    public int getPos(){
        return pos;
    }
}
