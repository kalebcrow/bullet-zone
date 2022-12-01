package edu.unh.cs.cs619.bulletzone.model.Entities.Terrain;

public class Meadow extends FieldTerrain {
    double speed = 0;
    int pos;


    public Meadow(){

    }

    public Meadow(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 0;
    }

    @Override
    public FieldTerrain copy() {
        return new Meadow();
    }

    @Override
    public String toString() {
        return "M";
    }

    public int getPos(){
        return pos;
    }
}
