package edu.unh.cs.cs619.bulletzone.model;

public class AutomatedRepair extends FieldResource{

    @Override
    public String toString(){
        return "RK";
    }

    @Override
    public int getIntValue() {
        return 3123;
    }

    @Override
    public FieldResource copy() {
        return new AutomatedRepair();
    }

    @Override
    public boolean gather(Tank tank) {
        return false;
    }


}
