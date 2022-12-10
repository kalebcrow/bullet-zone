package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.events.MineEvent;

public class Clay extends FieldResource {
    int credits = 16;
    int pos;

    public Clay(){

    }

    public Clay(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 501;
    }

    @Override
    public FieldResource copy() {
        return new Clay();
    }

    @Override
    public boolean gather(Tank tank) {
        Tank miner = game.getTank(game.getTanks(tank.getIp()).get("miner"));
        miner.addBundleOfResources(2,1);
        System.out.println("Finished item pickup process, adding iron to stash");
        eventManager.addEvent(new MineEvent(tank.getId(), miner.getAllResources()));
        return true;
    }

    @Override
    public String toString() {
        return "CB";
    }

    public int getPos(){
        return pos;
    }

    public int getCredits(){
        return credits;
    }
}
