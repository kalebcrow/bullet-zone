package edu.unh.cs.cs619.bulletzone.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unh.cs.cs619.bulletzone.model.Game;

public class GameBoardDirector {

    private static final Logger log = LoggerFactory.getLogger(GameBoardDirector.class);

    private final Object monitor = new Object();
    private static final int FIELD_DIM = 16;
    private Game game = null;

    public void GameBoardBuilder(GameBoardBuilder builder) {
        builder.create();
    }

    public void ConstructGameBoard(GameBoardBuilder builder) {
        builder.createMiddle();
    }

    public void ConstructTopGameBoard(GameBoardBuilder builder) {
        builder.createTop();
    }

    public void ConstructBottomGameBoard(GameBoardBuilder builder) {
        builder.createBottom();
    }

}
