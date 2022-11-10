package edu.unh.cs.cs619.bulletzone.model;

public class Road extends FieldEntity {
    int destructValue, pos;

    public Road(){
        this.destructValue = 1000;
    }

    public Road(int destructValue, int pos){
        this.destructValue = destructValue;
        this.pos = pos;
    }

    public Road(int destructValue)
    {
        this.destructValue = destructValue;
    }

    @Override
    public FieldEntity copy() {
        return new Wall();
    }

    @Override
    public int getIntValue() {
        return destructValue;
    }

    @Override
    public String toString() {
        return "R";
    }

    public int getPos(){
        return pos;
    }
}
