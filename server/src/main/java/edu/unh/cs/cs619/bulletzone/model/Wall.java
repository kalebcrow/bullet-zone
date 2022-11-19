package edu.unh.cs.cs619.bulletzone.model;

public class Wall extends FieldEntity {
    int destructValue, pos;
    public String name = "W";
    public Wall(){
        this.destructValue = 1000;
        this.name = "IW";
    }

    public Wall(int destructValue, int pos){
        this.destructValue = destructValue;
        this.pos = pos;
    }
    public Wall(int destructValue)
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
        return name;
    }

    public int getPos(){
        return pos;
    }
}
