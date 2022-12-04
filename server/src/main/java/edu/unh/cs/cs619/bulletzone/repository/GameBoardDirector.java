package edu.unh.cs.cs619.bulletzone.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Forest;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Hilly;
import edu.unh.cs.cs619.bulletzone.model.Meadow;
import edu.unh.cs.cs619.bulletzone.model.Rocky;
import edu.unh.cs.cs619.bulletzone.model.Wall;
import edu.unh.cs.cs619.bulletzone.model.Water;

public class GameBoardDirector {

    private static final Logger log = LoggerFactory.getLogger(GameBoardDirector.class);

    private final Object monitor = new Object();
    private static final int FIELD_DIM = 16;
    private Game game = null;

    public void ConstructGameBoard(GameBoardBuilder builder) {
        builder.create();
    }

}
