package edu.unh.cs.cs619.bulletzone.model;

public class DeflectorShield extends FieldResource{

    @Override
    public int getCredits(){
        return 300;
    }

    @Override
    public String toString(){
        return "DS";
    }

    @Override
    public int getIntValue() {
        return 3122;
    }

    @Override
    public FieldResource copy() {
        return new DeflectorShield();
    }

    @Override
    public boolean gather(Tank tank) {
        tank.enhance(new DeflectorShielded());
        return true;
    }
}
