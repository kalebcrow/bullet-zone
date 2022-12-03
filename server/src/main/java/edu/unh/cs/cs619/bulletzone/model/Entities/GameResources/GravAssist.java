package edu.unh.cs.cs619.bulletzone.model.Entities.GameResources;

public class GravAssist extends FieldResource{
    @Override
    public int getIntValue() {
        return 3111;
    }

    @Override
    public FieldResource copy() {
        return new GravAssist();
    }
}
