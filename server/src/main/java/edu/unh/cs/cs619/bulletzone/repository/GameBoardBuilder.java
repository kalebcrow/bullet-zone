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

public interface GameBoardBuilder {

    void testCreate();

    void create();

    void createMiddle();

    void createTop();

    void createBottom();

    public void createInitialGrid(Game game);

    void testCreateInitialGrid(Game game);

    void createFieldHolderGrid(Game game);
}
