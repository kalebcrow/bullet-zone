package edu.unh.cs.cs619.bulletzone.model;

import java.util.Timer;
import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.events.DamageEvent;

public class DeflectorShielded extends Powered{

    private final Timer timer = new Timer();

    public DeflectorShielded(){fieldElement = new DeflectorShield();}

    @Override
    public PowerUp powerDown(){
        PowerUp p = this.subject;
        subject.setMaxHealth(subject.getMaxHealth()-100);
        this.subject = null;
        return p;
    };

    @Override
    public void setSubject(PowerUp p){
        subject = p;
        setMaxHealth(subject.getMaxHealth()+100);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int health = subject.getHealth();
                if(health < subject.getMaxHealth()){
                    subject.setHealth(health + 1);
                    eventManager.addEvent(new DamageEvent(Math.toIntExact(getId()), health+2));
                }
            }
        }, 0, 1000);
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
        return subject.getAllowedFireInterval()/2;
    }

    @Override
    public int getAllowedNumberOfBullets() {
        return subject.getAllowedNumberOfBullets();
    }





}
