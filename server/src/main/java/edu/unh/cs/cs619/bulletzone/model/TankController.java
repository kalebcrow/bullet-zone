package edu.unh.cs.cs619.bulletzone.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.repository.GameRepository;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class TankController {
    Game game;

    public boolean turn(Tank tank, Direction direction)
            throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
            checkNotNull(direction);
            checkNotNull(tank);

            //Check for bad getLastMoveTime
            long millis = System.currentTimeMillis();
            if (millis < tank.getLastMoveTime())
                return false;

            //Causes tank to wait to move
            tank.setLastMoveTime(millis + tank.getAllowedMoveInterval());

            return true; // TODO check
    }

    /**
     * Currently restricts tank movement to forward or backward, if sideways returns false.
     * @param tank tank
     * @param direction direction of tank
     * @return true - inMemoryGameRepository.move gets invoked, false - return false
     * @throws IllegalTransitionException not used
     * @throws LimitExceededException not used
     * @throws TankDoesNotExistException not used
     */
    public boolean move(Tank tank, Direction direction)
            throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException {
            checkNotNull(direction);
            checkNotNull(tank);

            //Check for bad getLastMoveTime
            long millis = System.currentTimeMillis();
            if (millis < tank.getLastMoveTime())
                return false;

            //Causes tank to wait to move
            tank.setLastMoveTime(millis + tank.getAllowedMoveInterval());

            //Check for tank moving forwards or backwards
        return isSameRelativeDirection(tank.getDirection(), direction);
    }

    /**
     * Subfunction of move to check that the tank and requested move direction are same or inverse...
     * ... ie: forward and backward, left and right
     * @param tankDirection the tanks current direction
     * @param requestedDirection requested direction for movement
     * @return true or false based on if the move is possible or restricted
     */
    public boolean isSameRelativeDirection(Direction tankDirection, Direction requestedDirection) {
        byte direction1Byte = Direction.toByte(tankDirection);
        byte direction2Byte = Direction.toByte(requestedDirection);
        if (tankDirection == requestedDirection) {
            return true;
        } else if (Direction.fromByte((byte)(direction1Byte + 4)) == requestedDirection) {
            return true;
        } else if (Direction.fromByte((byte)(direction1Byte - 4)) == requestedDirection) {
            return true;
        } else {
            return false;
        }
    }

    public boolean fire(Tank tank, Direction direction)
            throws TankDoesNotExistException, LimitExceededException {
            return true;
    }


}
