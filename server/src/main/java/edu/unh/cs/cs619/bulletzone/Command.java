package edu.unh.cs.cs619.bulletzone;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class Command {

    Tank tank;
    Direction direction;
    InMemoryGameRepository gameRepository;
    long delay;
    final String ConcreteCommandType = "Command";

    public Command(Tank tank, Direction direction, InMemoryGameRepository gameRepository, long delay){

        this.tank = tank;
        this.direction = direction;
        this.gameRepository = gameRepository;
        this.delay = delay;

    }

    public boolean execute() {
        return false;
    }

    public String getConcreteCommandType(){
        return ConcreteCommandType;
    }

}

