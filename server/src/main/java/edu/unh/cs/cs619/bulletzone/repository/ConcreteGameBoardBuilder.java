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
    private Game game = null;

    private String[][] terrainGrid = {
            {"M", "M", "M", "M", "M", "R", "R", "R", "M", "M", "M", "M", "M", "M", "M", "M"}, // 0
            {"M", "M", "M", "R", "R", "R", "R", "R", "M", "M", "M", "M", "M", "M", "M", "M"}, // 1
            {"M", "M", "M", "R", "R", "R", "R", "M", "M", "M", "H", "H", "M", "M", "M", "M"}, // 2
            {"M", "M", "M", "R", "R", "R", "M", "M", "M", "M", "H", "H", "M", "M", "M", "M"}, // 3
            {"M", "M", "M", "R", "R", "R", "M", "M", "M", "M", "H", "H", "M", "M", "M", "M"}, // 4
            {"M", "M", "M", "R", "R", "M", "M", "M", "M", "M", "H", "H", "M", "M", "M", "H"}, // 5
            {"R", "R", "R", "R", "R", "M", "M", "M", "M", "M", "H", "H", "M", "M", "M", "H"}, // 6
            {"M", "R", "R", "R", "M", "M", "M", "H", "H", "H", "H", "H", "M", "H", "M", "H"}, // 7
            {"M", "R", "R", "R", "R", "M", "M", "M", "M", "M", "H", "H", "H", "H", "H", "H"}, // 8
            {"M", "M", "M", "R", "M", "M", "M", "M", "M", "H", "H", "H", "M", "H", "H", "H"}, // 9
            {"H", "M", "M", "M", "M", "M", "M", "M", "M", "H", "M", "H", "M", "H", "H", "H"}, // 10
            {"H", "H", "M", "M", "M", "M", "M", "M", "M", "H", "M", "M", "M", "M", "M", "H"}, // 11
            {"H", "H", "M", "M", "M", "M", "M", "M", "M", "H", "M", "R", "M", "M", "H", "H"}, // 12
            {"F", "H", "H", "H", "M", "M", "M", "M", "M", "R", "R", "M", "M", "M", "M", "M"}, // 13
            {"W", "H", "H", "H", "M", "M", "M", "M", "R", "R", "R", "M", "M", "M", "M", "M"}, // 14
            {"H", "H", "H", "M", "M", "M", "M", "R", "R", "R", "R", "M", "M", "M", "M", "M"}, // 15
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
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
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

                if (improvementGrid[i][j].equals("I")) {
                    game.getHolderGrid().get(count).setFieldEntity(new Wall());
                } else if (improvementGrid[i][j].equals("D")) {
                    game.getHolderGrid().get(count).setFieldEntity(new Wall(1500, count));
                }

                // second layer would be roads and now decking [2]
                // third layer is walls + vehicles + resources [1]

                count++;
            }
        }

        /*
                else if (improvementGrid[i][j].equals("CB")) {
                    game.getHolderGrid().get(count).setFieldEntity(new Clay()); // temp
                } else if (improvementGrid[i][j].equals("IB")) {
                    game.getHolderGrid().get(count).setFieldEntity(new Iron()); // temp
                } else if (improvementGrid[i][j].equals("RB")) {
                    game.getHolderGrid().get(count).setFieldEntity(new Rock()); // temp
                } else if (improvementGrid[i][j].equals("TB")) {
                    game.getHolderGrid().get(count).setFieldEntity(new Thingamajig()); // temp
                }
                */

        /*
        game.getHolderGrid().get(6).setFieldEntity(new Hilly());
        //game.getHolderGrid().get(6).setFieldEntity(new Hilly());

        game.getHolderGrid().get(17).setFieldEntity(new Wall());
        game.getHolderGrid().get(2).setFieldEntity(new Wall());
        game.getHolderGrid().get(3).setFieldEntity(new Wall());

        game.getHolderGrid().get(17).setFieldEntity(new Wall());
        game.getHolderGrid().get(33).setFieldEntity(new Wall(1500, 33));
        game.getHolderGrid().get(49).setFieldEntity(new Wall(1500, 49));
        game.getHolderGrid().get(65).setFieldEntity(new Wall(1500, 65));

        game.getHolderGrid().get(34).setFieldEntity(new Wall());
        game.getHolderGrid().get(66).setFieldEntity(new Wall(1500, 66));

        game.getHolderGrid().get(35).setFieldEntity(new Wall());
        game.getHolderGrid().get(51).setFieldEntity(new Wall());
        game.getHolderGrid().get(67).setFieldEntity(new Wall(1500, 67));

        game.getHolderGrid().get(5).setFieldEntity(new Wall());
        game.getHolderGrid().get(21).setFieldEntity(new Wall());
        game.getHolderGrid().get(37).setFieldEntity(new Wall());
        game.getHolderGrid().get(53).setFieldEntity(new Wall());
        game.getHolderGrid().get(69).setFieldEntity(new Wall(1500, 69));

        game.getHolderGrid().get(7).setFieldEntity(new Wall());
        game.getHolderGrid().get(23).setFieldEntity(new Wall());
        game.getHolderGrid().get(39).setFieldEntity(new Wall());
        game.getHolderGrid().get(71).setFieldEntity(new Wall(1500, 71));

        game.getHolderGrid().get(8).setFieldEntity(new Wall());
        game.getHolderGrid().get(40).setFieldEntity(new Wall());
        game.getHolderGrid().get(72).setFieldEntity(new Wall(1500, 72));

        game.getHolderGrid().get(9).setFieldEntity(new Wall());
        game.getHolderGrid().get(25).setFieldEntity(new Wall());
        game.getHolderGrid().get(41).setFieldEntity(new Wall());
        game.getHolderGrid().get(57).setFieldEntity(new Wall());
        game.getHolderGrid().get(73).setFieldEntity(new Wall());

         */



        // options are
        /*
        for (int i = 0; i < 256; i++) {
            // determine random option
            double option = (Math.random() * (256));

            // options for the grid are 1. wall 2. indestructible wall 3. blank 4. hilly 5. rocky
            if (option >= 0 && option < 20) {
                // 20% indestructible wall
                game.getHolderGrid().get(i).setFieldEntity(new Wall());
            } else if (option >= 20 && option < 30) {
                // 10% destructible wall
                game.getHolderGrid().get(i).setFieldEntity(new Wall(1500, i));
            } else if (option >= 30 && option < 50) {
                // 20% hilly
                game.getHolderGrid().get(i).setFieldEntity(new Hilly());
                //log.info("hilly entity at " + i + ": " + game.getHolderGrid().get(i).getEntity().toString() + " with previous: " + game.getHolderGrid().get(i).getPreviousEntity().toString());
            } else if (option >= 50 && option < 70) {
                // 20% rocky
                game.getHolderGrid().get(i).setFieldEntity(new Rocky());
                //log.info("rocky entity at " + i + ": " + game.getHolderGrid().get(i).getEntity().toString() + " with previous: " + game.getHolderGrid().get(i).getPreviousEntity().toString());
            } else {
                // 30% meadow
                //game.getHolderGrid().get(i).setFieldEntity(new Meadow());
                //log.info("meadow entity at " + i + ": " + game.getHolderGrid().get(i).getEntity().toString() + " with previous: " + game.getHolderGrid().get(i).getPreviousEntity().toString());
            }
        }

         */
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

    /**
     * Creates a 16*16 grid.
     * @param game specified game to create board for
     */
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
            for (int i = 0; i < FIELD_DIM; i++) {
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

}
