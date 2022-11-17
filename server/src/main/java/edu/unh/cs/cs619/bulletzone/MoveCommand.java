package edu.unh.cs.cs619.bulletzone;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class MoveCommand extends Command {

    public MoveCommand(long tankID, Direction direction, InMemoryGameRepository gameRepository, long delay){
        super(tankID, direction, gameRepository, delay);
    }

    public boolean execute(){

        boolean returnVariable = false;
        try {
            returnVariable = gameRepository.move(tankID, direction);
        } catch (TankDoesNotExistException e) {
            e.printStackTrace();
        } catch (IllegalTransitionException e) {
            e.printStackTrace();
        } catch (LimitExceededException e) {
            e.printStackTrace();
        }

        System.out.println("Move command");

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return returnVariable;
    }

}

