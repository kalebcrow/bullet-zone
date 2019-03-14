package edu.unh.cs.cs619.bulletzone.datalayer;

public class WeaponType extends ItemType {
    WeaponType(ItemTypeRecord rec)
    {
        super(rec);
    }

    public double getDamage() {
        return info.val1;
    }

    public double getElectricPowerUsed() {
        return info.val2;
    }

    public double getInstanceLimit() {
        return info.val3;
    }

}
