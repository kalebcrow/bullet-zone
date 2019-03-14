package edu.unh.cs.cs619.bulletzone.datalayer;

public class EngineType extends ItemType {
    EngineType(ItemTypeRecord rec)
    {
        super(rec);
    }

    public double getDrivePower() {
        return info.val1;
    }

    public double getElectricPowerUsed() {
        return info.val2;
    }
}
