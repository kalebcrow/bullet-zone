package edu.unh.cs.cs619.bulletzone.events;

import org.junit.Test;

import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Forest;
import edu.unh.cs.cs619.bulletzone.model.Meadow;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankController;
import edu.unh.cs.cs619.bulletzone.model.Water;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;
import static org.junit.Assert.*;

public class RestrictionEventTest {

    private Tank tank;
    private TankController tc;
    private InMemoryGameRepository IMGR;
    private final String ip = "test";

    @Test
    public void testRestrictions_TankAroundMeadow_NoRestrictions() throws InterruptedException, IllegalTransitionException, LimitExceededException, TankDoesNotExistException{

        EventManager eventManager = EventManager.getInstance();
        IMGR = new InMemoryGameRepository();
        IMGR.create();
        tc = new TankController();
        Tank[] tank = IMGR.join("i",ip);
        Thread.sleep(1000); //Letting server catch up
        Long tankId = tank[0].getId();


        int[][][] grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        tank[0].getParent().getNeighbor(Direction.Left).setFieldTerrain(new Meadow());
        tank[0].getParent().getNeighbor(Direction.Right).setFieldTerrain(new Meadow());

        IMGR.turn(tank[0].getId(), Direction.Left);

        System.out.println("\n\n\n");

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        Thread.sleep(1000); //Letting server catch up

        LinkedList<GridEvent> update = eventManager.getEvents(System.currentTimeMillis()-2000);

        GridEvent testEvent = new GridEvent();

        for(GridEvent e: update){

            if(e.getType() == "restriction"){

                System.out.println("Found it");
                System.out.println("Restrictions: " + e.getRestrictions()[0] + e.getRestrictions()[1] + e.getRestrictions()[2]);
                testEvent = e;
                break;

            }

        }

        assertEquals(1, testEvent.getRestrictions()[0]);
        assertEquals(1, testEvent.getRestrictions()[1]);
        assertEquals(1, testEvent.getRestrictions()[2]);

    }

    @Test
    public void testRestrictions_TankAroundForest_ForwardBackwardsMovementRestricted() throws InterruptedException, IllegalTransitionException, LimitExceededException, TankDoesNotExistException {

        EventManager eventManager = EventManager.getInstance();
        IMGR = new InMemoryGameRepository();
        IMGR.create();
        tc = new TankController();
        Tank[] tank = IMGR.join("i",ip);
        Thread.sleep(1000); //Letting server catch up
        Long tankId = tank[0].getId();


        int[][][] grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        tank[0].getParent().getNeighbor(Direction.Left).setFieldTerrain(new Forest());
        tank[0].getParent().getNeighbor(Direction.Right).setFieldTerrain(new Forest());

        IMGR.turn(tank[0].getId(), Direction.Left);

        System.out.println("\n\n\n");

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        Thread.sleep(1000); //Letting server catch up

        LinkedList<GridEvent> update = eventManager.getEvents(System.currentTimeMillis()-2000);

        GridEvent testEvent = new GridEvent();

        for(GridEvent e: update){

            if(e.getType() == "restriction"){

                System.out.println("Found it");
                System.out.println("Restrictions: " + e.getRestrictions()[0] + e.getRestrictions()[1] + e.getRestrictions()[2]);
                testEvent = e;
                break;

            }

        }

        assertEquals(0, testEvent.getRestrictions()[0]);
        assertEquals(0, testEvent.getRestrictions()[1]);
        assertEquals(1, testEvent.getRestrictions()[2]);

    }

    @Test
    public void testRestrictions_TankAroundWater_ForwardBackwardsMovementRestricted() throws InterruptedException, IllegalTransitionException, LimitExceededException, TankDoesNotExistException{

        EventManager eventManager = EventManager.getInstance();
        IMGR = new InMemoryGameRepository();
        IMGR.create();
        tc = new TankController();
        Tank[] tank = IMGR.join("i",ip);
        Thread.sleep(1000); //Letting server catch up
        Long tankId = tank[0].getId();


        int[][][] grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        tank[0].getParent().getNeighbor(Direction.Left).setFieldTerrain(new Water());
        tank[0].getParent().getNeighbor(Direction.Right).setFieldTerrain(new Water());

        IMGR.turn(tank[0].getId(), Direction.Left);

        System.out.println("\n\n\n");

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        Thread.sleep(1000); //Letting server catch up

        LinkedList<GridEvent> update = eventManager.getEvents(System.currentTimeMillis()-2000);

        GridEvent testEvent = new GridEvent();

        for(GridEvent e: update){

            if(e.getType() == "restriction"){

                System.out.println("Found it");
                System.out.println("Restrictions: " + e.getRestrictions()[0] + e.getRestrictions()[1] + e.getRestrictions()[2]);
                testEvent = e;
                break;

            }

        }

        assertEquals(0, testEvent.getRestrictions()[0]);
        assertEquals(0, testEvent.getRestrictions()[1]);
        assertEquals(1, testEvent.getRestrictions()[2]);

    }

    @Test
    public void testRestrictions_MinerAroundWater_ForwardBackwardsMovementRestricted() throws InterruptedException, IllegalTransitionException, LimitExceededException, TankDoesNotExistException{

        EventManager eventManager = EventManager.getInstance();
        IMGR = new InMemoryGameRepository();
        IMGR.create();
        tc = new TankController();
        Tank[] tank = IMGR.join("i",ip);
        Thread.sleep(1000); //Letting server catch up
        Long tankId = tank[1].getId();


        int[][][] grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        tank[1].getParent().getNeighbor(Direction.Left).setFieldTerrain(new Water());
        tank[1].getParent().getNeighbor(Direction.Right).setFieldTerrain(new Water());

        IMGR.turn(tank[1].getId(), Direction.Left);

        System.out.println("\n\n\n");

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][0] + "\t");
            }
            System.out.println();
        }

        Thread.sleep(1000); //Letting server catch up

        LinkedList<GridEvent> update = eventManager.getEvents(System.currentTimeMillis()-2000);

        GridEvent testEvent = new GridEvent();

        for(GridEvent e: update){

            if(e.getType() == "restriction" && e.getID() == tank[1].getId()){

                System.out.println("Event id " + e.getID() + "\t tankID: " + tank[1].getId());
                System.out.println("Restrictions: " + e.getRestrictions()[0] + e.getRestrictions()[1] + e.getRestrictions()[2]);
                testEvent = e;
                break;

            }

        }

        assertEquals(0, testEvent.getRestrictions()[0]);
        assertEquals(0, testEvent.getRestrictions()[1]);
        assertEquals(1, testEvent.getRestrictions()[2]);

    }

    @Test
    public void testRestrictions_MinerAroundForest_NoMovementRestrictions() throws InterruptedException, IllegalTransitionException, LimitExceededException, TankDoesNotExistException {

        EventManager eventManager = EventManager.getInstance();
        IMGR = new InMemoryGameRepository();
        IMGR.create();
        tc = new TankController();
        Tank[] tank = IMGR.join("i",ip);
        Thread.sleep(1000); //Letting server catch up
        Long tankId = tank[1].getId();


        int[][][] grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        tank[1].getParent().getNeighbor(Direction.Left).setFieldTerrain(new Forest());
        tank[1].getParent().getNeighbor(Direction.Right).setFieldTerrain(new Forest());

        IMGR.turn(tank[1].getId(), Direction.Left);

        System.out.println("\n\n\n");

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][0] + "\t");
            }
            System.out.println();
        }


        Thread.sleep(1000); //Letting server catch up

        LinkedList<GridEvent> update = eventManager.getEvents(System.currentTimeMillis()-2000);

        GridEvent testEvent = new GridEvent();

        for(GridEvent e: update){

            if(e.getType() == "restriction" && e.getID() == tank[1].getId()){

                System.out.println("Found it");
                System.out.println("Restrictions: " + e.getRestrictions()[0] + e.getRestrictions()[1] + e.getRestrictions()[2]);
                testEvent = e;
                break;

            }

        }

        assertEquals(1, testEvent.getRestrictions()[0]);
        assertEquals(1, testEvent.getRestrictions()[1]);
        assertEquals(1, testEvent.getRestrictions()[2]);

    }

    @Test
    public void testRestrictions_BuilderAroundForest_ForwardBackwardsMovementRestricted() throws InterruptedException, IllegalTransitionException, LimitExceededException, TankDoesNotExistException{

        EventManager eventManager = EventManager.getInstance();
        IMGR = new InMemoryGameRepository();
        IMGR.create();
        tc = new TankController();
        Tank[] tank = IMGR.join("i",ip);
        Thread.sleep(1000); //Letting server catch up
        Long tankId = tank[2].getId();


        int[][][] grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }



        tank[2].getParent().getNeighbor(Direction.Left).setFieldTerrain(new Forest());
        tank[2].getParent().getNeighbor(Direction.Right).setFieldTerrain(new Forest());

        IMGR.turn(tank[2].getId(), Direction.Left);

        System.out.println("\n\n\n");

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][0] + "\t");
            }
            System.out.println();
        }

        Thread.sleep(1000); //Letting server catch up

        LinkedList<GridEvent> update = eventManager.getEvents(System.currentTimeMillis()-2000);

        GridEvent testEvent = new GridEvent();

        for(GridEvent e: update){

            if(e.getType() == "restriction"){

                System.out.println("Found it");
                System.out.println("Restrictions: " + e.getRestrictions()[0] + e.getRestrictions()[1] + e.getRestrictions()[2]);
                testEvent = e;
                break;

            }

        }

        assertEquals(0, testEvent.getRestrictions()[0]);
        assertEquals(0, testEvent.getRestrictions()[1]);
        assertEquals(1, testEvent.getRestrictions()[2]);

    }

    @Test
    public void testRestriction_BuilderAroundWater_NoMovementRestrictions() throws InterruptedException, IllegalTransitionException, LimitExceededException, TankDoesNotExistException{

        EventManager eventManager = EventManager.getInstance();
        IMGR = new InMemoryGameRepository();
        IMGR.create();
        tc = new TankController();
        Tank[] tank = IMGR.join("i",ip);
        Thread.sleep(1000); //Letting server catch up
        Long tankId = tank[2].getId();


        int[][][] grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        tank[2].setNumberOfBullets(2);

        tank[2].getParent().getNeighbor(Direction.Left).setFieldTerrain(new Water());
        tank[2].getParent().getNeighbor(Direction.Right).setFieldTerrain(new Water());

        IMGR.turn(tank[2].getId(), Direction.Left);

        System.out.println("\n\n\n");

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        Thread.sleep(1000); //Letting server catch up

        LinkedList<GridEvent> update = eventManager.getEvents(System.currentTimeMillis()-2000);

        GridEvent testEvent = new GridEvent();

        for(GridEvent e: update){

            if(e.getType() == "restriction"){

                System.out.println("Found it");
                System.out.println("Restrictions: " + e.getRestrictions()[0] + e.getRestrictions()[1] + e.getRestrictions()[2]);
                testEvent = e;
                break;

            }

        }

        assertEquals(1, testEvent.getRestrictions()[0]);
        assertEquals(1, testEvent.getRestrictions()[1]);
        assertEquals(1, testEvent.getRestrictions()[2]);

    }

    @Test
    public void testRestrictions_TankHasTwoBullets_FireRestrictionGenerated() throws InterruptedException, IllegalTransitionException, LimitExceededException, TankDoesNotExistException{

        EventManager eventManager = EventManager.getInstance();
        IMGR = new InMemoryGameRepository();
        IMGR.create();
        tc = new TankController();
        Tank[] tank = IMGR.join("i",ip);
        Thread.sleep(1000); //Letting server catch up
        Long tankId = tank[0].getId();


        int[][][] grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        tank[0].setNumberOfBullets(2);

        tank[0].getParent().getNeighbor(Direction.Left).setFieldTerrain(new Meadow());
        tank[0].getParent().getNeighbor(Direction.Right).setFieldTerrain(new Meadow());

        IMGR.turn(tank[0].getId(), Direction.Left);

        System.out.println("\n\n\n");

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        Thread.sleep(1000); //Letting server catch up

        LinkedList<GridEvent> update = eventManager.getEvents(System.currentTimeMillis()-2000);

        GridEvent testEvent = new GridEvent();

        for(GridEvent e: update){

            if(e.getType() == "restriction"){

                System.out.println("Found it");
                System.out.println("Restrictions: " + e.getRestrictions()[0] + e.getRestrictions()[1] + e.getRestrictions()[2]);
                testEvent = e;
                break;

            }

        }

        assertEquals(1, testEvent.getRestrictions()[0]);
        assertEquals(1, testEvent.getRestrictions()[1]);
        assertEquals(0, testEvent.getRestrictions()[2]);

    }

    @Test
    public void testRestrictions_MinerHasFourBullets_FireRestrictionGenerated() throws InterruptedException, IllegalTransitionException, LimitExceededException, TankDoesNotExistException {

        EventManager eventManager = EventManager.getInstance();
        IMGR = new InMemoryGameRepository();
        IMGR.create();
        tc = new TankController();
        Tank[] tank = IMGR.join("i",ip);
        Thread.sleep(1000); //Letting server catch up
        Long tankId = tank[1].getId();


        int[][][] grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        tank[1].setNumberOfBullets(4);

        tank[1].getParent().getNeighbor(Direction.Left).setFieldTerrain(new Meadow());
        tank[1].getParent().getNeighbor(Direction.Right).setFieldTerrain(new Meadow());

        IMGR.turn(tank[1].getId(), Direction.Left);

        System.out.println("\n\n\n");

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        Thread.sleep(1000); //Letting server catch up

        LinkedList<GridEvent> update = eventManager.getEvents(System.currentTimeMillis()-2000);

        GridEvent testEvent = new GridEvent();

        for(GridEvent e: update){

            if(e.getType() == "restriction"){

                System.out.println("Found it");
                System.out.println("Restrictions: " + e.getRestrictions()[0] + e.getRestrictions()[1] + e.getRestrictions()[2]);
                testEvent = e;
                break;

            }

        }

        assertEquals(1, testEvent.getRestrictions()[0]);
        assertEquals(1, testEvent.getRestrictions()[1]);
        assertEquals(0, testEvent.getRestrictions()[2]);

    }

    @Test
    public void testRestrictions_BuilderHasSixBullets_FireRestrictionGenerated() throws InterruptedException, IllegalTransitionException, LimitExceededException, TankDoesNotExistException{

        EventManager eventManager = EventManager.getInstance();
        IMGR = new InMemoryGameRepository();
        IMGR.create();
        tc = new TankController();
        Tank[] tank = IMGR.join("i",ip);
        Thread.sleep(1000); //Letting server catch up
        Long tankId = tank[2].getId();


        int[][][] grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        tank[2].setNumberOfBullets(6);

        tank[2].getParent().getNeighbor(Direction.Left).setFieldTerrain(new Meadow());
        tank[2].getParent().getNeighbor(Direction.Right).setFieldTerrain(new Meadow());

        IMGR.turn(tank[2].getId(), Direction.Left);

        System.out.println("\n\n\n");

        grid2d = IMGR.getGrid();
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                System.out.print(grid2d[i][j][1] + "\t");
            }
            System.out.println();
        }

        Thread.sleep(1000); //Letting server catch up

        LinkedList<GridEvent> update = eventManager.getEvents(System.currentTimeMillis()-2000);

        GridEvent testEvent = new GridEvent();

        for(GridEvent e: update){

            if(e.getType() == "restriction"){

                System.out.println("Found it");
                System.out.println("Restrictions: " + e.getRestrictions()[0] + e.getRestrictions()[1] + e.getRestrictions()[2]);
                testEvent = e;
                break;

            }

        }

        assertEquals(1, testEvent.getRestrictions()[0]);
        assertEquals(1, testEvent.getRestrictions()[1]);
        assertEquals(0, testEvent.getRestrictions()[2]);

    }

}
