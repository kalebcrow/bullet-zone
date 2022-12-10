package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.model.PowerUp;

public abstract class Powered extends PowerUp {

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
        PowerUp p = this.subject;
        this.subject = null;
        return p;
    };

    @Override
    public int getHealth() {
        return subject.getHealth();
    }

    @Override
    public void setHealth(int health) {
        subject.setHealth(health);
    }

    @Override
    public int getMaxHealth(){return subject.getMaxHealth();}

    @Override
    public void setMaxHealth(int health){subject.setMaxHealth(health);}

    @Override
    public long getId(){return subject.getId();}

}
