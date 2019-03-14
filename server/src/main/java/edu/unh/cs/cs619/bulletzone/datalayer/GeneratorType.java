package edu.unh.cs.cs619.bulletzone.datalayer;

public class GeneratorType extends ItemType {
    GeneratorType(ItemTypeRecord rec)
    {
        super(rec);
    }

    public double getElectricPower() {
        return info.val1;
    }
}
