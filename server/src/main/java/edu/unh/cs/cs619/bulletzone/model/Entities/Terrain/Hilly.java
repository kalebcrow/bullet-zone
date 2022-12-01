package edu.unh.cs.cs619.bulletzone.model.Entities.Terrain;

public class Hilly extends FieldTerrain {
    double speed = 0.5;
    int pos;

    public Hilly(){

    }

    public Hilly(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 2;
    }

    @Override
    public FieldTerrain copy() {
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
