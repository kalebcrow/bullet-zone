package edu.unh.cs.cs619.bulletzone.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.repository.GameRepository;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

public class TankController {
    Game game;
    private int bulletDelay[]={500,1000,1500};

    /**
     *
     * @param tank current tank
     * @param direction direction of tank
     * @return If the turn is legal or not
     * @throws IllegalTransitionException not used
     * @throws LimitExceededException not used
     * @throws TankDoesNotExistException not used
     */
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

            return !isSameRelativeDirection(tank.getDirection(), direction); // TODO check
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

    /**
     *
     * @param tank the tank
     * @param bulletType The bullet type
     * @return The bullet type if true and -1 if false
     * @throws TankDoesNotExistException not used
     * @throws LimitExceededException not used
     */
    public int fire(Tank tank, int bulletType)
            throws TankDoesNotExistException, LimitExceededException {

        //Check for tank firing too many bullets
        if (tank.getNumberOfBullets() >= tank.getAllowedNumberOfBullets())
            return -1;

        //Check for bad last fire time
        long millis = System.currentTimeMillis();
        if (millis < tank.getLastFireTime()/*>tank.getAllowedFireInterval()*/){
            return -1;
        }

        if(!(bulletType>=1 && bulletType<=3)) {
            System.out.println("Bullet type must be 1, 2 or 3, set to 1 by default.");
            bulletType = 1;
        }

        tank.setLastFireTime(millis + bulletDelay[bulletType - 1]);

        return bulletType;
    }


}