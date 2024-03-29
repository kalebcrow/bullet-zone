package edu.unh.cs.cs619.bulletzone.model;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.graalvm.compiler.replacements.Log.print;

import edu.unh.cs.cs619.bulletzone.model.Exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;


public class TankController {

    private static final String TAG = "TankController";

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

            if (tank.getLife() == 0) {
                return false;
            }

            //Check for bad getLastMoveTime
            long millis = System.currentTimeMillis();
            if (millis < tank.getLastMoveTime())
                return false;

            //Causes tank to wait to move
            tank.setLastMoveTime(millis + tank.getAllowedTurnInterval());

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

        if (tank.getLife() == 0) {
            return false;
        }
        if (!tank.allowMovement)
        {
            return false;
        }

        FieldHolder nextField = tank.getParent().getNeighbor(direction);

        if(nextField.getTerrain().toString().equals("W") && tank.getTypeIndex() != 2)
        {
            if(!nextField.getImprovement().toString().equals("D"))
            {
                return false;
            }
        }

        if(nextField.getTerrain().toString().equals("F") && tank.getTypeIndex() != 1) {
            return false;
        }

        double speed = 0;
        // adding a check for field type (water, forest, meadow, hilly, or rocky) for speed purposes
        if (nextField.getTerrain().toString().equals("W") && tank.getTypeIndex() == 2) {
            // water (only applies to builders = 2)
            speed = tank.getAllowedMoveInterval() * 2;
        } else if (nextField.getTerrain().toString().equals("F") && tank.getTypeIndex() == 1) {
            // forest (only applies to miners = 1)
            speed = tank.getAllowedMoveInterval() * 1.5;
        } else if (nextField.getTerrain().toString().equals("R")) {
            // rocky
            speed = tank.getAllowedMoveInterval() * 2;
        } else if (nextField.getTerrain().toString().equals("H")) {
            // hilly
            speed = tank.getAllowedMoveInterval() * 1.5;
        } else {
            // meadow
            speed = 0;
        }

        if(nextField.isImprovementPresent()) {
            if (nextField.getImprovement().toString() == "R") {
                speed = speed/2;
            } else if (nextField.getImprovement().toString() == "Decking") {
                speed = 0; // treats the block like a meadow square
            }
        }


        //Check for bad getLastMoveTime
        long millis = System.currentTimeMillis();
        if (millis < tank.getLastMoveTime())
            return false;

        //Causes tank to wait to move
        long terrainSpeed = (long) (speed + tank.getAllowedMoveInterval());
        tank.setLastMoveTime(millis + terrainSpeed);

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
    public boolean fire(Tank tank, int bulletType)
            throws TankDoesNotExistException, LimitExceededException {

        if (tank.getLife() == 0) {
            return false;
        }

        //Check for tank firing too many bullets
        if (tank.getNumberOfBullets() >= tank.getAllowedNumberOfBullets())
            return false;

        //Check for bad last fire time
        long millis = System.currentTimeMillis();
        if (millis < tank.getLastFireTime()/*>tank.getAllowedFireInterval()*/){
            return false;
        }

        if(!(bulletType>=1 && bulletType<=3)) {
            System.out.println("Bullet type must be 1, 2 or 3, set to 1 by default.");
            bulletType = 1;
        }

        tank.setLastFireTime(millis + tank.getAllowedFireInterval());

        return true;
    }

    public boolean mine(Tank tank)
            throws LimitExceededException, TankDoesNotExistException {
        checkNotNull(tank);

        return tank.getLife() != 0;
    }

    public int build(Tank tank, int building)
    {
        if(tank.getLife() == 0){
            return -1;
        }

        return 1;
    }

    public int dismantle(Tank tank)
    {
        if(tank.getLife() == 0){
            return -1;
        }

        return 1;
    }
}
