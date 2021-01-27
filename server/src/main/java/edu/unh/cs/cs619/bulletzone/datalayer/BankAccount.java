package edu.unh.cs.cs619.bulletzone.datalayer;

public class BankAccount extends OwnableEntity {
    protected double credits;

    public double getCredits() { return credits; }

    //----------------------------------END OF PUBLIC METHODS--------------------------------------
    BankAccount(BankAccountRecord rec) {
        super(rec);
        credits = rec.credits;
    }
}
