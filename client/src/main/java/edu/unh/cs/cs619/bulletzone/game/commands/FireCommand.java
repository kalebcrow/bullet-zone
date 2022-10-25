package edu.unh.cs.cs619.bulletzone.game.commands;

public class FireCommand implements Commands {
    public Integer tankID;
    public Integer direction;

    FireCommand(Integer tankID) {
        this.tankID = tankID;
    }

    @Override
    public void execute() {

    }
}
