package edu.unh.cs.cs619.bulletzone.model;

import static org.junit.Assert.assertNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.BuildingDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.Exceptions.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.events.GridEvent;
import edu.unh.cs.cs619.bulletzone.repository.DataRepository;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;
import edu.unh.cs.cs619.bulletzone.web.GamesControllerTest;

@RunWith(MockitoJUnitRunner.class)

public class BuilderTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @InjectMocks
    InMemoryGameRepository repo;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testBuildFunction_tankJoinedAndBuildRoad_createdRoad() throws LimitExceededException, TankDoesNotExistException, IllegalTransitionException, InterruptedException, BuildingDoesNotExistException {
        repo.create();
        Tank[] tank = repo.join("i","");
        long tankId = tank[2].getId();
        tank[1].addBundleOfResources(0,10);
        tank[1].addBundleOfResources(1,10);
        tank[1].addBundleOfResources(2,10);
        repo.build(tankId,1);
        FieldEntity ent = tank[2].getParent().getNeighbor(Direction.Down).getImprovement();
        assert(Objects.equals(ent.toString(), "R"));
    }

    @Test
    public void testBuildFunction_tankJoinedAndBuildWall_createdWall() throws LimitExceededException, TankDoesNotExistException, IllegalTransitionException, InterruptedException, BuildingDoesNotExistException {
        repo.create();
        Tank[] tank = repo.join("i","");
        long tankId = tank[2].getId();
        tank[1].addBundleOfResources(0,10);
        tank[1].addBundleOfResources(1,10);
        tank[1].addBundleOfResources(2,10);
        repo.build(tankId,2);
        FieldEntity ent = tank[2].getParent().getNeighbor(Direction.Down).getEntity();
        assert(Objects.equals(ent.toString(), "W"));
    }

    @Test
    public void testBuildFunction_tankJoinedAndBuildIndesWall_createdIndesWall() throws LimitExceededException, TankDoesNotExistException, IllegalTransitionException, InterruptedException, BuildingDoesNotExistException {
        repo.create();
        Tank[] tank = repo.join("i","");
        long tankId = tank[2].getId();
        tank[1].addBundleOfResources(0,10);
        tank[1].addBundleOfResources(1,10);
        tank[1].addBundleOfResources(2,10);
        repo.build(tankId,3);
        FieldEntity ent = tank[2].getParent().getNeighbor(Direction.Down).getEntity();
        assert(Objects.equals(ent.toString(), "IW"));
    }

    @Test
    public void testBuildFunction_tankJoinedWithNoResources_wallNotCreated() throws LimitExceededException, TankDoesNotExistException, IllegalTransitionException, InterruptedException, BuildingDoesNotExistException {
        repo.create();
        Tank[] tank = repo.join("i","");
        long tankId = tank[2].getId();
        assert(!repo.build(tankId, 3));
    }

    @Test(expected = NoSuchElementException.class)
    public void testDestroyFunction_tankJoinedAndDestroyWall_removedWall() throws LimitExceededException, TankDoesNotExistException, IllegalTransitionException, InterruptedException, BuildingDoesNotExistException {
        repo.create();
        Tank[] tank = repo.join("i","");
        long tankId = tank[2].getId();
        tank[1].addBundleOfResources(0,10);
        tank[1].addBundleOfResources(1,10);
        tank[1].addBundleOfResources(2,10);
        repo.build(tankId,2);
        FieldEntity ent = tank[2].getParent().getNeighbor(Direction.Down).getEntity();
        assert(Objects.equals(ent.toString(), "W"));
        assert(repo.dismantle(tankId));
        ent = tank[2].getParent().getNeighbor(Direction.Down).getEntity(); //exception thrown here
    }

    @Test
    public void testMoveBuild_moveIntoCreatedRoad_returnsTrue() throws BuildingDoesNotExistException, TankDoesNotExistException, LimitExceededException, IllegalTransitionException {
        repo.create();
        Tank[] tank = repo.join("i","");
        Long tankId = tank[2].getId();
        tank[1].addBundleOfResources(0,10);
        tank[1].addBundleOfResources(1,10);
        tank[1].addBundleOfResources(2,10);
        repo.build(tankId,1);
        assert(repo.move(tankId, Direction.Down));
    }

    @Test
    public void testMoveBuild_moveIntoCreatedWall_returnsFalse() throws BuildingDoesNotExistException, TankDoesNotExistException, LimitExceededException, IllegalTransitionException {
        repo.create();
        Tank[] tank = repo.join("i","");
        Long tankId = tank[2].getId();
        tank[1].addBundleOfResources(0,10);
        tank[1].addBundleOfResources(1,10);
        tank[1].addBundleOfResources(2,10);
        repo.build(tankId,2);
        assert(!repo.move(tankId, Direction.Down));
    }

    @Test
    public void testMoveDismantle_moveIntoDismantledWall_returnsTrue() throws BuildingDoesNotExistException, TankDoesNotExistException, LimitExceededException, IllegalTransitionException {
        repo.create();
        Tank[] tank = repo.join("i","");
        long tankId = tank[2].getId();
        tank[1].addBundleOfResources(0,10);
        tank[1].addBundleOfResources(1,10);
        tank[1].addBundleOfResources(2,10);
        repo.build(tankId,2);
        FieldEntity ent = tank[2].getParent().getNeighbor(Direction.Down).getEntity();
        assert(Objects.equals(ent.toString(), "W"));
        assert(repo.dismantle(tankId));
        assert(repo.move(tankId,Direction.Down));
    }

    @Test
    public void testDismantle_dismantleEmptySpace_returnsFalse() throws TankDoesNotExistException {
        repo.create();
        Tank[] tank = repo.join("i","");
        long tankId = tank[2].getId();
        assert(!repo.dismantle(tankId));
    }
    @Test
    public void testMoveBuild_moveWhileBuilding_returnsFalse() throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException, BuildingDoesNotExistException {
        repo.create();
        Tank[] tank = repo.join("i","");
        long tankId = tank[2].getId();
        tank[1].addBundleOfResources(0,10);
        tank[1].addBundleOfResources(1,10);
        tank[1].addBundleOfResources(2,10);
        new Thread(() -> {
            try {
                repo.build(tankId,3);
            } catch (TankDoesNotExistException e) {
                e.printStackTrace();
            } catch (BuildingDoesNotExistException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                Thread.sleep(100);
                assert(!repo.move(tankId,Direction.Up));
            } catch (TankDoesNotExistException e) {
                e.printStackTrace();
            } catch (IllegalTransitionException e) {
                e.printStackTrace();
            } catch (LimitExceededException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Test
    public void testFireBuild_fireWhileBuilding_returnsFalse() throws TankDoesNotExistException, IllegalTransitionException, LimitExceededException, BuildingDoesNotExistException {
        repo.create();
        Tank[] tank = repo.join("i","");
        long tankId = tank[2].getId();
        tank[1].addBundleOfResources(0,10);
        tank[1].addBundleOfResources(1,10);
        tank[1].addBundleOfResources(2,10);
        new Thread(() -> {
            try {
                repo.build(tankId,3);
            } catch (TankDoesNotExistException e) {
                e.printStackTrace();
            } catch (BuildingDoesNotExistException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                Thread.sleep(100);
                assert(repo.fire(tankId,1));
            } catch (TankDoesNotExistException e) {
                e.printStackTrace();
            } catch (LimitExceededException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Test
    public void testBuild_buildFactory_returnsTrue() throws BuildingDoesNotExistException, TankDoesNotExistException, LimitExceededException, IllegalTransitionException {
        repo.create();
        Tank[] tank = repo.join("i","");
        Long tankId = tank[2].getId();
        tank[1].addBundleOfResources(0,10);
        tank[1].addBundleOfResources(1,10);
        tank[1].addBundleOfResources(2,10);
        tank[1].addBundleOfResources(3,10);
        assert(repo.build(tankId,5));
    }

    @Test
    public void testFactory_respawnFactory_returnsTrue() throws BuildingDoesNotExistException, TankDoesNotExistException, LimitExceededException, IllegalTransitionException {
        repo.create();
        DataRepository data = new DataRepository(true);
        String username = "testuseronlyfortesting";
        data.validateUser(username, username, false);
        Tank[] tank = repo.join(username, "");

        double actualBalance = data.getUserAccountBalance(username);

        System.out.println(actualBalance);

        data.modifyAccountBalance(username,200000);

        actualBalance = data.getUserAccountBalance(username);

        Long tankId = tank[2].getId();
        tank[1].addBundleOfResources(0,10);
        tank[1].addBundleOfResources(1,10);
        tank[1].addBundleOfResources(2,10);
        tank[1].addBundleOfResources(3,10);
        assert(repo.build(tankId,5));
        tank[1].hit(2000);
        assert(repo.rebuildTank(tank[1].getId()));
    }

    @Test
    public void testFactory_respawnTankFactory_returnsTrue() throws BuildingDoesNotExistException, TankDoesNotExistException, LimitExceededException, IllegalTransitionException {
        repo.create();
        DataRepository data = new DataRepository(true);
        String username = "testuseronlyfortesting";
        data.validateUser(username, username, false);
        Tank[] tank = repo.join(username, "");

        double actualBalance = data.getUserAccountBalance(username);

        System.out.println(actualBalance);

        data.modifyAccountBalance(username,200000);

        actualBalance = data.getUserAccountBalance(username);

        Long tankId = tank[2].getId();
        tank[1].addBundleOfResources(0,10);
        tank[1].addBundleOfResources(1,10);
        tank[1].addBundleOfResources(2,10);
        tank[1].addBundleOfResources(3,10);
        assert(repo.build(tankId,5));
        tank[0].hit(2000);
        assert(repo.rebuildTank(tank[0].getId()));
    }

    @Test
    public void testFactory_respawnBuilderFactory_returnsTrue() throws BuildingDoesNotExistException, TankDoesNotExistException, LimitExceededException, IllegalTransitionException {
        repo.create();
        DataRepository data = new DataRepository(true);
        String username = "testuseronlyfortesting";
        data.validateUser(username, username, false);
        Tank[] tank = repo.join(username, "");

        double actualBalance = data.getUserAccountBalance(username);

        System.out.println(actualBalance);

        data.modifyAccountBalance(username,200000);

        actualBalance = data.getUserAccountBalance(username);

        Long tankId = tank[2].getId();
        tank[1].addBundleOfResources(0,10);
        tank[1].addBundleOfResources(1,10);
        tank[1].addBundleOfResources(2,10);
        tank[1].addBundleOfResources(3,10);
        assert(repo.build(tankId,5));
        tank[2].hit(2000);
        assert(repo.rebuildTank(tank[2].getId()));
    }

    @Test
    public void testDismantle_dismantleFactory_returnsTrue() throws BuildingDoesNotExistException, TankDoesNotExistException, LimitExceededException, IllegalTransitionException {
        repo.create();
        Tank[] tank = repo.join("i","");
        Long tankId = tank[2].getId();
        tank[1].addBundleOfResources(0,10);
        tank[1].addBundleOfResources(1,10);
        tank[1].addBundleOfResources(2,10);
        tank[1].addBundleOfResources(3,10);
        assert(repo.build(tankId,5));
        assert(repo.dismantle(tankId));
    }

    @Test
    public void testDismantle_dismantleDecking_returnsTrue() throws BuildingDoesNotExistException, TankDoesNotExistException, LimitExceededException, IllegalTransitionException {
        repo.create();
        Tank[] tank = repo.join("i","");
        Long tankId = tank[2].getId();
        tank[1].addBundleOfResources(0,10);
        tank[1].addBundleOfResources(1,10);
        tank[1].addBundleOfResources(2,10);
        tank[1].addBundleOfResources(3,10);
        assert(repo.build(tankId,4));
        assert(repo.dismantle(tankId));
    }

    @Test
    public void testshit_portals()
    {
        repo.create();
        Tank[] tank = repo.join("i","test");
        Portal p = new Portal();
        Portal p1 = new Portal();
        p.direction = Direction.Up;
        p1.direction = Direction.Right;
        p.setExit(p1);
        p.setExit(p);
        p.setParent(tank[0].getParent().getNeighbor(Direction.Up));
        p.setParent(tank[0].getParent().getNeighbor(Direction.Up).getNeighbor(Direction.Up));
        tank[0].getParent().getNeighbor(Direction.Up).setImprovementEntity(p);
        tank[0].getParent().getNeighbor(Direction.Up).getNeighbor(Direction.Up).setImprovementEntity(p1);
        tank[0].moveTank(Direction.Up);
    }


}
