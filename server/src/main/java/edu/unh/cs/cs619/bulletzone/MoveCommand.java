package edu.unh.cs.cs619.bulletzone;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class MoveCommand extends Command {

    final String ConcreteCommandType = "Move";

    public MoveCommand(Tank tank, Direction direction, InMemoryGameRepository gameRepository, long delay){
        super(tank, direction, gameRepository, delay);
    }

    @Override
    public boolean execute(){

        boolean returnVariable = false;
        try {
            returnVariable = gameRepository.move(tank.getId(), direction);
        } catch (TankDoesNotExistException e) {
            e.printStackTrace();
        } catch (IllegalTransitionException e) {
            e.printStackTrace();
        } catch (LimitExceededException e) {
            e.printStackTrace();
        }

        System.out.println("Move Command: Result = " + returnVariable);

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return returnVariable;
    }

    @Override
    public String getConcreteCommandType(){
        return ConcreteCommandType;
    }

}

