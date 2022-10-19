package edu.unh.cs.cs619.bulletzone.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.repository.GameRepository;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class TankController {
    private final Object monitor = new Object();
    GameRepository mGameRepository;
    Game game;
    public TankController(GameRepository gameRepository) {
        mGameRepository = gameRepository;
    }

    public boolean turn(Tank tank, Direction direction) {
        synchronized (this.monitor) {
            checkNotNull(direction);
            checkNotNull(tank);

            long millis = System.currentTimeMillis();
            if (millis < tank.getLastMoveTime())
                return false;


            return true; // TODO check
        }
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
        synchronized (this.monitor) {
            checkNotNull(direction);
            checkNotNull(tank);

            //out of sync??? Someone explain this to me
            long millis = System.currentTimeMillis();
            if (millis < tank.getLastMoveTime())
                return false;

            //What does this do and is this the thing that causes it to wait.
            tank.setLastMoveTime(millis + tank.getAllowedMoveInterval());

            //Check for tank moving forwards or backwards
            if (isSameRelativeDirection(tank.getDirection(), direction)) {
                return mGameRepository.move(tank.getId(), direction);
            } else {
                return false;
            }
        }
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
        synchronized (this.monitor) {
            return true;
        }
    }


}
