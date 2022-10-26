package edu.unh.cs.cs619.bulletzone.game.commands;

public class TurnCommand implements Commands {

    public Integer tankID;
    public Integer direction;

    TurnCommand(Integer tankID, Integer direction) {
        this.tankID = tankID;
        this.direction = direction;
    }

    @Override
    public void execute() {

    }

}
