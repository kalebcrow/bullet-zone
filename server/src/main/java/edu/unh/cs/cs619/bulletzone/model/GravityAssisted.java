package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.model.Powered;

public class GravityAssisted extends Powered {

    public GravityAssisted(){
        fieldElement = new GravAssist();
    }

    @Override
    public long getAllowedMoveInterval() {
        return subject.getAllowedMoveInterval()/2;
    }

    @Override
    public long getAllowedTurnInterval() {
        return subject.getAllowedTurnInterval()/2;
    }

    @Override
    public long getAllowedFireInterval() {
        return subject.getAllowedFireInterval()*3/2;
    }

    @Override
    public int getAllowedNumberOfBullets() {
        return subject.getAllowedNumberOfBullets();
    }
}
