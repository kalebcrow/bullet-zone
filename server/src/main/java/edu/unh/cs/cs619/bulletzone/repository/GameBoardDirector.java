package edu.unh.cs.cs619.bulletzone.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.unh.cs.cs619.bulletzone.model.Game;

public class GameBoardDirector {

    public void GameBoardBuilder(GameBoardBuilder builder) {
        builder.create();
    }

    public void ConstructGameBoard(GameBoardBuilder builder) {
        builder.create();
    }
}
