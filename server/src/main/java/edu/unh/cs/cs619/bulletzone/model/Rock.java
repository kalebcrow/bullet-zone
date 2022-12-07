package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.events.MineEvent;

public class Rock extends FieldResource {
    int credits = 25;
    int pos;

    public Rock(){

    }

    public Rock(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 502;
    }

    @Override
    public FieldResource copy() {
        return new Rock();
    }

    @Override
    public boolean gather(Tank tank) {
        Tank miner = game.getTank(game.getTanks(tank.getIp()).get("miner"));
        miner.addBundleOfResources(0,1);
        System.out.println("Finished item pickup process, adding iron to stash");
        eventManager.addEvent(new MineEvent(tank.getId(), miner.getAllResources()));
        return true;
    }

    @Override
    public String toString() {
        return "RB";
    }

    public int getPos(){
        return pos;
    }

    public int getCredits(){
        return credits;
    }
}
