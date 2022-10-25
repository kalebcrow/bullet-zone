package edu.unh.cs.cs619.bulletzone.game.commands;

public class MoveCommand implements Commands {

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
