package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.events.MineEvent;

public class Wood extends FieldResource {
    int credits = 7;
    int pos;

    public Wood(){

    }

    public Wood(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 504;
    }

    @Override
    public FieldResource copy() {
        return new Wood();
    }

    @Override
    public boolean gather(Tank tank) {
        Tank miner = game.getTank(game.getTanks(tank.getIp()).get("miner"));
        miner.addBundleOfResources(3,1);
        System.out.println("Finished item pickup process, adding wood to stash");
        eventManager.addEvent(new MineEvent(tank.getId(), miner.getAllResources()));
        return true;
    }

    @Override
    public String toString() {
        return "WB";
    }

    public int getPos(){
        return pos;
    }

    public int getCredits(){
        return credits;
    }
}
