package edu.unh.cs.cs619.bulletzone.model;

import java.util.Timer;
import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.events.DamageEvent;

public class AutomatedRepaired extends Powered{

    private final Timer timer = new Timer();

    public AutomatedRepaired(){fieldElement = new AutomatedRepair();}

    @Override
    public void setSubject(PowerUp p){
        subject = p;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int health = subject.getHealth();
                if(health < subject.getMaxHealth()){
                    subject.setHealth(health + 1);
                    eventManager.addEvent(new DamageEvent(Math.toIntExact(getId()), health+2));
                }
            }
        }, 0, 500);
    }

    @Override
    public long getAllowedMoveInterval() {
        return subject.getAllowedMoveInterval();
    }

    @Override
    public long getAllowedTurnInterval() {
        return subject.getAllowedTurnInterval();
    }

    @Override
    public long getAllowedFireInterval() {
        return subject.getAllowedFireInterval();
    }

    @Override
    public int getAllowedNumberOfBullets() {
        return subject.getAllowedNumberOfBullets();
    }
}
