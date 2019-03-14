package edu.unh.cs.cs619.bulletzone.datalayer;

public class DriveType extends ItemType {
    DriveType(ItemTypeRecord rec)
    {
        super(rec);
    }

    public double getDrivePowerUsed() {
        return info.val1;
    }

    public double getMovementToWeightRatio() {
        return info.val2;
    }
}
