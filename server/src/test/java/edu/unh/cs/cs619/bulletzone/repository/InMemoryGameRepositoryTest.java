package edu.unh.cs.cs619.bulletzone.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.events.GridEvent;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class InMemoryGameRepositoryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @InjectMocks
    InMemoryGameRepository repo;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testJoin() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());
    }
    /*
    @Test
    public void testTurn() throws Exception {
        Tank tank = repo.join("");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(repo.turn(tank.getId(), Direction.Right));
        Assert.assertTrue(tank.getDirection() == Direction.Right);

        thrown.expect(TankDoesNotExistException.class);
        thrown.expectMessage("Tank '1000' does not exist");
        repo.turn(1000, Direction.Right);
    } */

    @Test
    public void testMove() throws Exception {

    }

    @Test
    public void testFire() throws Exception {

    }

    @Test
    public void testLeave() throws Exception {

    }
    @Test
    public void testGetEvents_noEvents_ReturnsEmptyList(){
        repo.create();
        assert(repo.getEvents(System.currentTimeMillis()).size() == 0);
    }

    @Test
    public void testGetEvents_tankJoined_ReturnsAddTankEvent(){
        repo.create();
        Tank tank = repo.join("");
        LinkedList<GridEvent> update = repo.getEvents(System.currentTimeMillis()-500);
        assert(update.size() == 1);
        assert(update.getFirst().getType() == "addTank");
    }

    @Test
    public void testGetEvents_tankJoinedMoved_ReturnsListOfAppropriateSize() throws IllegalTransitionException, LimitExceededException, TankDoesNotExistException, InterruptedException {
        repo.create();
        Tank tank = repo.join("");
        Long tankID = tank.getId();
        repo.turn(tankID, Direction.Left);
        Thread.sleep(60000);
        repo.turn(tankID, Direction.Up);
        LinkedList<GridEvent> update = repo.getEvents(System.currentTimeMillis()-70000);
        assert(update.size() == 2);
    }
}