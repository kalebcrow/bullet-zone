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

public class ConcreteGameBoardBuilder implements GameBoardBuilder {

    private static final Logger log = LoggerFactory.getLogger(ConcreteGameBoardBuilder.class);

    private final Object monitor = new Object();
    private static final int FIELD_DIM = 16;

    private static final int FIELD_DIM_ROW = FIELD_DIM * 3;
    private static final int FIELD_DIM_COL = FIELD_DIM;

    private Game game = null;

    private String[][] terrainGrid = {
            {"M", "M", "M", "M", "M", "R", "R", "R", "M", "M", "M", "M", "F", "F", "F", "F"}, // 0
            {"M", "M", "W", "W", "R", "R", "R", "R", "M", "M", "M", "M", "M", "F", "F", "F"}, // 1
            {"M", "M", "W", "W", "R", "R", "R", "M", "M", "M", "H", "H", "M", "M", "F", "F"}, // 2
            {"M", "M", "M", "R", "R", "R", "M", "M", "M", "M", "H", "H", "M", "M", "F", "M"}, // 3
            {"M", "M", "M", "R", "R", "R", "M", "F", "M", "M", "H", "H", "M", "M", "M", "M"}, // 4
            {"M", "M", "M", "R", "R", "M", "M", "F", "F", "M", "H", "H", "M", "M", "M", "H"}, // 5
            {"R", "R", "R", "R", "R", "M", "F", "F", "F", "M", "H", "H", "M", "M", "M", "H"}, // 6
            {"M", "R", "R", "R", "M", "M", "M", "M", "F", "H", "H", "H", "M", "H", "M", "H"}, // 7
            {"M", "R", "F", "R", "R", "M", "M", "M", "M", "H", "H", "H", "H", "H", "H", "H"}, // 8
            {"M", "M", "F", "F", "M", "M", "M", "M", "M", "H", "H", "H", "M", "H", "H", "H"}, // 9
            {"H", "M", "F", "F", "M", "M", "W", "W", "W", "H", "M", "H", "M", "H", "H", "H"}, // 10
            {"H", "H", "M", "M", "M", "M", "W", "W", "W", "H", "M", "M", "M", "M", "M", "H"}, // 11
            {"H", "H", "M", "M", "M", "M", "W", "W", "W", "H", "M", "R", "M", "M", "H", "H"}, // 12
            {"W", "H", "H", "H", "M", "M", "M", "M", "W", "R", "R", "M", "M", "M", "M", "M"}, // 13
            {"W", "W", "H", "H", "M", "M", "M", "M", "R", "R", "R", "M", "M", "M", "M", "W"}, // 14
            {"W", "W", "H", "M", "M", "M", "M", "R", "R", "R", "R", "M", "M", "M", "M", "W"}, // 15

            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 0
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 1
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 2
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 3
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 4
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 5
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 6
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 7
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 8
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 9
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 10
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 11
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 12
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 13
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 14
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 15

            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 0
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 1
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 2
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 3
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 4
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 5
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 6
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 7
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 8
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 9
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 10
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 11
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 12
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 13
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 14
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 15
    };

    private String[][] improvementGrid = {
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 0
            {" ", "I", " ", " ", " ", " ", " ", " ", " ", "D", " ", " ", " ", " ", " ", " "}, // 1
            {" ", "I", " ", " ", " ", " ", " ", " ", " ", "D", " ", " ", " ", "I", " ", " "}, // 2
            {" ", "I", " ", " ", " ", " ", " ", " ", " ", "D", " ", " ", " ", "I", " ", " "}, // 3
            {" ", "I", " ", " ", " ", " ", " ", " ", " ", "D", " ", " ", " ", "I", " ", " "}, // 4
            {" ", "I", " ", " ", " ", " ", " ", " ", " ", "D", " ", " ", " ", "I", " ", " "}, // 5
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 6
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 7
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 8
            {" ", "I", " ", " ", " ", "D", " ", " ", " ", "I", " ", " ", " ", " ", " ", " "}, // 9
            {" ", "I", " ", " ", " ", "D", " ", " ", " ", "I", " ", " ", " ", " ", " ", " "}, // 10
            {" ", "I", " ", " ", " ", "D", " ", " ", " ", "I", " ", " ", " ", " ", " ", " "}, // 11
            {" ", "I", " ", " ", " ", "D", " ", " ", " ", "I", " ", "D", "D", "D", "D", " "}, // 12
            {" ", "I", " ", " ", " ", "D", " ", " ", " ", "I", " ", " ", " ", " ", " ", " "}, // 13
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 14
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 15
    };

    private String[][] testTerrainGrid = {
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 0
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 1
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 2
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 3
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 4
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 5
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 6
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 7
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 8
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 9
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 10
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 11
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 12
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 13
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 14
            {"M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M", "M"}, // 15
    };

    private String[][] testTerrainGrid2 = {
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 0
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 1
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 2
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 3
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 4
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 5
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 6
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 7
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 8
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 9
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 10
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 11
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 12
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 13
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 14
            {"R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R", "R"}, // 15
    };

    private String[][] testImprovementGrid = {
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 0
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 1
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 2
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 3
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 4
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 5
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 6
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 7
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 8
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 9
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 10
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 11
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 12
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 13
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 14
            {" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "}, // 15
    };

    ConcreteGameBoardBuilder(Game game)
    {
        this.game = game;
    }

    /**
     * Creates a new board from specified game returns if no current game
     */
    public void testCreate(){
        if(game == null) {
            return;
        }
        createFieldHolderGrid(game);
        testCreateInitialGrid(game);
    }

    public void create() {
        if(game == null) {
            return;
        }
        createFieldHolderGrid(game);

        createInitialGrid(game);
    }

    public void createInitialGrid(Game game) {
        // add each level of the square - terrain --> improvements/tanks --> bundles
        int count = 0;
        // row
        for (int i = 0; i < FIELD_DIM_ROW; i++) {
            // col
            for (int j = 0; j < FIELD_DIM_COL; j++) {
                if (terrainGrid[i][j].equals("M")) {
                    game.getHolderGrid().get(count).setFieldTerrain(new Meadow());
                } else if (terrainGrid[i][j].equals("H")) {
                    game.getHolderGrid().get(count).setFieldTerrain(new Hilly());
                } else if (terrainGrid[i][j].equals("R")) {
                    game.getHolderGrid().get(count).setFieldTerrain(new Rocky());
                } else if (terrainGrid[i][j].equals("F")) {
                    game.getHolderGrid().get(count).setFieldTerrain(new Forest());
                } else if (terrainGrid[i][j].equals("W")) {
                    game.getHolderGrid().get(count).setFieldTerrain(new Water());
                }

                // SARA improvements on grid
                /*
                if (improvementGrid[i][j].equals("I")) {
                    game.getHolderGrid().get(count).setFieldEntity(new Wall());
                } else if (improvementGrid[i][j].equals("D")) {
                    game.getHolderGrid().get(count).setFieldEntity(new Wall(1500, count));
                }
                 */

                // second layer would be roads and now decking [2]
                // third layer is walls + vehicles + resources [1]

                count++;
            }
        }
    }

    public void testCreateInitialGrid(Game game) {
        // add each level of the square - terrain --> improvements --> vehicle
        // for now just doing terrain
        int count = 0;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (testTerrainGrid[i][j].equals("M")) {
                    game.getHolderGrid().get(count).setFieldTerrain(new Meadow());
                } else if (testTerrainGrid[i][j].equals("H")) {
                    game.getHolderGrid().get(count).setFieldTerrain(new Hilly());
                } else if (testTerrainGrid[i][j].equals("R")) {
                    game.getHolderGrid().get(count).setFieldTerrain(new Rocky());
                }

                if (testImprovementGrid[i][j].equals("I")) {
                    game.getHolderGrid().get(count).setFieldEntity(new Wall());
                } else if (testImprovementGrid[i][j].equals("D")) {
                    game.getHolderGrid().get(count).setFieldEntity(new Wall(1500, count));
                }

                count++;
            }
        }
    }

    /** Creates a 16*16 grid.
     * @param game specified game to create board for
     */
    public void createFieldHolderGrid(Game game) {
        synchronized (this.monitor) {
            game.getHolderGrid().clear();

            for (int i = 0; i < FIELD_DIM_ROW * FIELD_DIM_COL; i++) {
                game.getHolderGrid().add(new FieldHolder(i));
            }

            FieldHolder targetHolder;
            FieldHolder rightHolder;
            FieldHolder downHolder;

            // Build connections
            // row
            int k = 0;
            // SARA fix so can have correct neighbors for bottom row
            for (int i = 0; i < FIELD_DIM_ROW; i++) {
                for (int j = 0; j < FIELD_DIM_COL; j++) {
                    int val = (i * FIELD_DIM_COL + j);
                    int val2 = (i * FIELD_DIM_COL
                            + ((j + 1) % FIELD_DIM_COL));
                    int val3 = k + (((i + 1) % FIELD_DIM_COL)
                            * FIELD_DIM_COL + j);

                    targetHolder = game.getHolderGrid().get(val);//i * FIELD_DIM_COL + j);
                    rightHolder = game.getHolderGrid().get(val2);//i * FIELD_DIM_COL
                            //+ ((j + 1) % FIELD_DIM_COL));
                    downHolder = game.getHolderGrid().get(val3);//((i + 1) % FIELD_DIM_COL)
                            //* FIELD_DIM_COL + j);

                    targetHolder.addNeighbor(Direction.Right, rightHolder);
                    rightHolder.addNeighbor(Direction.Left, targetHolder);

                    targetHolder.addNeighbor(Direction.Down, downHolder);
                    downHolder.addNeighbor(Direction.Up, targetHolder);

                    log.debug(k + ": (" + i + ", " + j + "): " + val + ", " + val2 + ", " + val3);
                    log.debug("right: " + targetHolder.getNeighbor(Direction.Right));
                    log.debug("left: " + targetHolder.getNeighbor(Direction.Left));
                    log.debug("up: " + targetHolder.getNeighbor(Direction.Up));
                    log.debug("bottom: " + targetHolder.getNeighbor(Direction.Down));
                }
                // update this number to get the bottom of each grid to hold the top of each grid, instead of one big grid
                if (i != 0 && (i + 1) % 16 == 0) {
                    k += 256;
                }
            }
        }
    }


    /**
     * Creates a 16*16 grid.
     * @param game specified game to create board for
     */
    /*
    public void createFieldHolderGrid(Game game) {
        synchronized (this.monitor) {
            game.getHolderGrid().clear();
            for (int i = 0; i < FIELD_DIM * FIELD_DIM; i++) {
                game.getHolderGrid().add(new FieldHolder(i));
            }

            FieldHolder targetHolder;
            FieldHolder rightHolder;
            FieldHolder downHolder;

            // Build connections
            // row
            for (int i = 0; i < FIELD_DIM; i++) {
                // col
                for (int j = 0; j < FIELD_DIM; j++) {
                    targetHolder = game.getHolderGrid().get(i * FIELD_DIM + j);
                    rightHolder = game.getHolderGrid().get(i * FIELD_DIM
                            + ((j + 1) % FIELD_DIM));
                    downHolder = game.getHolderGrid().get(((i + 1) % FIELD_DIM)
                            * FIELD_DIM + j);

                    targetHolder.addNeighbor(Direction.Right, rightHolder);
                    rightHolder.addNeighbor(Direction.Left, targetHolder);

                    targetHolder.addNeighbor(Direction.Down, downHolder);
                    downHolder.addNeighbor(Direction.Up, targetHolder);
                }
            }
        }
    }

     */

}
