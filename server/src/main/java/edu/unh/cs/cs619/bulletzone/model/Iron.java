package edu.unh.cs.cs619.bulletzone.model;

import edu.unh.cs.cs619.bulletzone.events.MineEvent;

public class Iron extends FieldResource {
    int credits = 78;
    int pos;

    public Iron(){

    }

    public Iron(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 503;
    }

    @Override
    public FieldResource copy() {
        return new Iron();
    }

    @Override
    public boolean gather(Tank tank) {
        // this is awful :(
        Tank miner = game.getTank(game.getTanks(tank.getIp()).get("miner"));
        miner.addBundleOfResources(1,1);
        System.out.println("Finished item pickup process, adding iron to stash");
        eventManager.addEvent(new MineEvent(tank.getId(), miner.getAllResources()));
        return true;
    }

    @Override
    public String toString() {
        return "IB";
    }

    public int getPos(){
        return pos;
    }

    public int getCredits(){
        return credits;
    }
}
