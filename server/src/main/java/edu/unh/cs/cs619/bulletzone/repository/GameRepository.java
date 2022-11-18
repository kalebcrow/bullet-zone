package edu.unh.cs.cs619.bulletzone.repository;

import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.BuildingDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.InvalidResourceTileType;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.events.GridEvent;

public interface GameRepository {

    Tank[] join(long userID, String ip);

    int[][][] getGrid();

    LinkedList<GridEvent> getEvents(Long time);

    boolean turn(long tankId, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException;

    boolean move(long tankId, Direction direction)
            throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException;

    boolean fire(long tankId, int strength)
            throws TankDoesNotExistException, LimitExceededException;

    boolean mine(long tankId)
            throws TankDoesNotExistException, LimitExceededException, InvalidResourceTileType;

    public void leave(long[] tankId)
            throws TankDoesNotExistException;

    boolean build(long tankId, int type)
            throws TankDoesNotExistException, BuildingDoesNotExistException;

    boolean dismantle(long tankId)
            throws TankDoesNotExistException;

    Game getGame()
            throws GameDoesNotExistException;
}
