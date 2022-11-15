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

@RunWith(MockitoJUnitRunner.class)
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
        Tank tank = repo.join(0,"");
        Tank tank2 = repo.join(0,"10");
        Assert.assertEquals(tank.getId(), 0);
        Assert.assertEquals(tank2.getId(), 1);
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
        //tank joins and is first player
        Tank tank = repo.join(0,"10");
        Assert.assertNotNull(tank);
        Assert.assertEquals(tank.getId(), 0);

        //tank leaves and is no longer present in the FieldHolder
        Assert.assertEquals(true, tank.getParent().isPresent());
        repo.leave(tank.getId());
        Assert.assertEquals(false, tank.getParent().isPresent());

        thrown.expect(TankDoesNotExistException.class);
        repo.turn(tank.getId(), Direction.Right);
    }

    @Test
    public void testGetEvents_noEvents_ReturnsEmptyList() {
        repo.create();
        assert (repo.getEvents(System.currentTimeMillis()).size() == 0);
    }

    @Test
    public void testGetEvents_tankJoined_ReturnsAddTankEvent() {
        repo.create();
        Tank tank = repo.join(0,"");
        LinkedList<GridEvent> update = repo.getEvents(System.currentTimeMillis() - 500);
        assert (update.size() == 1);
        assert (update.getFirst().getType() == "addTank");
    }

    @Test
    public void testGetEvents_tankJoinedMoved_ReturnsListOfAppropriateSizeAndContents() throws LimitExceededException, TankDoesNotExistException, IllegalTransitionException, InterruptedException {
        repo.create();
        Tank tank = repo.join(0,"");
        Long tankID = tank.getId();
        for (int i = 0; i < 10; i++) {
            repo.move(tankID, Direction.Up);
            Thread.sleep(550);
            repo.move(tankID, Direction.Down);
            Thread.sleep(550);
        }
        assert (repo.getEvents(System.currentTimeMillis() - 100000).size() == 21);
        assert (repo.getEvents(System.currentTimeMillis() - 100000).get(0).getType() == "addTank");
        assert (repo.getEvents(System.currentTimeMillis() - 100000).get(1).getType() == "moveTank");
        assert (repo.getEvents(System.currentTimeMillis() - 100000).get(20).getType() == "moveTank");
    }

    @Test
    public void testGetEvents_EventsAccrueOverMinute_ReturnsAtLeastEventsFromLastMinute() throws LimitExceededException, TankDoesNotExistException, IllegalTransitionException, InterruptedException {
        repo.create();
        Tank tank = repo.join(0,"");
        Long tankID = tank.getId();
        for (int i = 0; i < 60; i++) {
            repo.move(tankID, Direction.Up);
            Thread.sleep(500);
            repo.move(tankID, Direction.Down);
            Thread.sleep(500);
        }
        assert (repo.getEvents(System.currentTimeMillis() - 100000).size() >= 120);
        assert (repo.getEvents(System.currentTimeMillis() - 100000).get(1).getType() == "moveTank");
        assert (repo.getEvents(System.currentTimeMillis() - 100000).get(120).getType() == "moveTank");
    }

    @Test
    public void testGetEvents_EventsAccrueOverThreeMinutes_ReturnsEventsFromNoMoreThanThreeMinutesAgo() throws LimitExceededException, TankDoesNotExistException, IllegalTransitionException, InterruptedException {
        repo.create();
        Tank tank = repo.join(0,"");
        Long tankID = tank.getId();

        for (int i = 0; i < 180; i++) {
            repo.move(tankID, Direction.Up);
            Thread.sleep(500);
            repo.move(tankID, Direction.Down);
            Thread.sleep(500);
        }
        repo.getEvents(System.currentTimeMillis());
        assert (repo.getEvents(System.currentTimeMillis() - 180000).size() <= 360);
    }
}

