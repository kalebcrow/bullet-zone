package edu.unh.cs.cs619.bulletzone;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class MoveToCommand {

    long tankID;
    Direction direction;
    InMemoryGameRepository gameRepository;

    public MoveToCommand(long tankID, Direction direction, InMemoryGameRepository gameRepository){

        this.tankID = tankID;
        this.direction = direction;
        this.gameRepository = gameRepository;

    }

    public boolean execute() {
        return false;
    }

}
