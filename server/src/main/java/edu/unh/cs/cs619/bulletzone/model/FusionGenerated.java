package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.model.Powered;

public class FusionGenerated extends Powered {

    @Override
    public long getAllowedMoveInterval() {
        return subject.getAllowedMoveInterval()*3/2;
    }

    @Override
    public long getAllowedTurnInterval() {
        return subject.getAllowedTurnInterval()*3/2;
    }

    @Override
    public long getAllowedFireInterval() {
        return subject.getAllowedFireInterval()/2;
    }

    @Override
    public int getAllowedNumberOfBullets() {
        return subject.getAllowedNumberOfBullets()+2;
    }
}
