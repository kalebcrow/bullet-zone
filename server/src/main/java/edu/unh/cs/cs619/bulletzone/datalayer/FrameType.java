package edu.unh.cs.cs619.bulletzone.datalayer;

public class FrameType extends ItemType {
    FrameType(ItemTypeRecord rec)
    {
        super(rec);
    }

    @Override
    public boolean isContainer() {
        return true;
    }

    public double getCapacity() {
        return info.val1;
    }

    public double getArmor() {
        return info.val2;
    }
}
