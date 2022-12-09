package edu.unh.cs.cs619.bulletzone.model;

public class DeflectorShielded extends Powered{
    @Override
    public long getAllowedMoveInterval() {
        return 0;
    }

    @Override
    public long getAllowedTurnInterval() {
        return 0;
    }

    @Override
    public long getAllowedFireInterval() {
        return 0;
    }

    @Override
    public int getAllowedNumberOfBullets() {
        return 0;
    }

}
