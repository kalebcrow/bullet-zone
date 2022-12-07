package edu.unh.cs.cs619.bulletzone.model;

public class GravAssist extends FieldResource {
    @Override
    public int getIntValue() {
        return 3111;
    }

    @Override
    public FieldResource copy() {
        return new GravAssist();
    }

    @Override
    public boolean gather(Tank tank) {
        tank.enhance(new GravityAssisted());
        return true;
    }
}
