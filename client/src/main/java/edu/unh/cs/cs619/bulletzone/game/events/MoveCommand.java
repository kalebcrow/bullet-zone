package edu.unh.cs.cs619.bulletzone.game.events;

public class MoveCommand extends GridEvent {

    public Integer tankID;
    public Boolean forward;

    MoveCommand(Integer tankID, Boolean forward) {
        this.tankID = tankID;
        this.forward = forward;
    }

    @Override
    public void execute() {

    }

}
