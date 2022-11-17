package edu.unh.cs.cs619.bulletzone;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class Command {

    long tankID;
    Direction direction;
    InMemoryGameRepository gameRepository;
    long delay;

    public Command(long tankID, Direction direction, InMemoryGameRepository gameRepository, long delay){

        this.tankID = tankID;
        this.direction = direction;
        this.gameRepository = gameRepository;
        this.delay = delay;

    }

    public boolean execute() {
        return false;
    }

}

