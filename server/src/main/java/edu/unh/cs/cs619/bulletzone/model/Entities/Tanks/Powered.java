package edu.unh.cs.cs619.bulletzone.model.Entities.Tanks;

public abstract class Powered extends PowerUp{

    protected PowerUp subject;

    @Override
    public abstract long getAllowedMoveInterval();
    @Override
    public abstract long getAllowedTurnInterval();
    @Override
    public abstract long getAllowedFireInterval();
    @Override
    public abstract int getAllowedNumberOfBullets();

    public void setSubject(PowerUp p){
        this.subject = p;
    }

    @Override
    public PowerUp powerDown(){
        return this.subject;
    };

}
