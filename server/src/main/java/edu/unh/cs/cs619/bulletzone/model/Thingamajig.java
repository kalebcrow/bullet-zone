package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.events.balanceEvent;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;

public class Thingamajig extends FieldResource {
    private int credits;
    private int pos;
    private DataRepository data = new DataRepository();

    public Thingamajig(){
        credits = (int) (Math.random() * (200)) + 1;; // TODO randomize [1,10000] with average 100?
    }

    public Thingamajig(int credits, boolean test) {
        this.credits = credits;
    }

    public Thingamajig(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 7;
    }

    @Override
    public FieldResource copy() {
        return new Thingamajig();
    }

    @Override
    public boolean gather(Tank tank) {
        String username = tank.getUsername();
        data.modifyAccountBalance(username, credits);
        eventManager.addEvent(new balanceEvent(data.getUserAccountBalance(username), tank.getId()));
        return true;
    }

    @Override
    public String toString() {
        return "TB";
    }

    public int getPos(){
        return pos;
    }

    public int getCredits(){
        return credits;
    }
}
