package edu.unh.cs.cs619.bulletzone;

import org.graalvm.compiler.lir.sparc.SPARCMove;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class MoveCommand extends MoveToCommand{

    public MoveCommand(long tankID, Direction direction, InMemoryGameRepository gameRepository){
        super(tankID, direction, gameRepository);
    }

    public boolean execute(){

        try {
            return gameRepository.move(tankID, direction);
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
