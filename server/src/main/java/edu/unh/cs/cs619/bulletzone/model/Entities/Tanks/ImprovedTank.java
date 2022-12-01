package edu.unh.cs.cs619.bulletzone.model.Entities.Tanks;

import edu.unh.cs.cs619.bulletzone.model.Entities.FieldEntity;

public abstract class ImprovedTank extends Tank{

    protected Tank subject;

    @Override
    public FieldEntity copy() {
        return subject.copy();
    }

    @Override
    public abstract long getAllowedMoveInterval();

    @Override
    public abstract long getAllowedTurnInterval();

    @Override
    public abstract long getAllowedFireInterval();

    @Override
    public abstract int getAllowedNumberOfBullets();

    @Override
    public Tank strip(){
        return this.subject;
    }


}
