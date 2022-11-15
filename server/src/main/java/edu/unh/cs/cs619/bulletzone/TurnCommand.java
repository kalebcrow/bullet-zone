package edu.unh.cs.cs619.bulletzone;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class TurnCommand extends MoveToCommand{

    public TurnCommand(long tankID, Direction direction, InMemoryGameRepository gameRepository){
        super(tankID, direction, gameRepository);
    }

    @Override
    public boolean execute() {

        try {
            return gameRepository.turn(tankID, direction);
        } catch (TankDoesNotExistException e) {
            e.printStackTrace();
        } catch (IllegalTransitionException e) {
            e.printStackTrace();
        } catch (LimitExceededException e) {
            e.printStackTrace();
        }

        return false;

    }
}
