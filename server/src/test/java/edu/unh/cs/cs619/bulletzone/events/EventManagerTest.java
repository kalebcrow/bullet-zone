package edu.unh.cs.cs619.bulletzone.events;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Tank;

public class EventManagerTest {

    private EventManager eventManager = EventManager.getInstance();
    private Tank tank = new Tank();
    private Long id = tank.getId();

    @Test
    public void addEvent_GivenNewEvent_StoresEventInHistory(){
        eventManager.addEvent(new AddTankEvent(0,0,id));
        LinkedList<GridEvent> update = eventManager.getEvents(System.currentTimeMillis() - 10000);
        assertEquals(update.size(), 1);
        assertEquals(update.getFirst().getType(), "addTank");
    }

    @Test
    public void getEvents_MinutesOfAddingEvents_ReturnsEventsAtLeastOneMinuteOld() throws InterruptedException {
        eventManager.addEvent(new AddTankEvent(0,0,id));
        for(int i=0;i<60;i++){
            eventManager.addEvent(new MoveTankEvent(id,Direction.toByte(Direction.Up), 0));
            Thread.sleep(1000);
        }
        LinkedList<GridEvent> update = eventManager.getEvents(System.currentTimeMillis() - 100000);
        assertEquals(update.size(),61);
        assertEquals(update.get(15).getType(),"moveTank");
        assertEquals(update.getFirst().getType(), "addTank");

    }

    @Test
    public void getEvents_MinutesOfAddingEvents_ReturnsEventsNoMoreThanThreeMinutesOld() throws InterruptedException {
        eventManager.addEvent(new AddTankEvent(0,0,id));
        for(int i=0;i<180;i++){
            eventManager.addEvent(new MoveTankEvent(id,Direction.toByte(Direction.Up), 0));
            Thread.sleep(1000);
        }
        LinkedList<GridEvent> update = eventManager.getEvents(System.currentTimeMillis() - 100000);
        assert(update.size() < 181);
        assertEquals(update.get(15).getType(),"moveTank");
        assertEquals(update.getFirst().getType(), "moveTank");
    }

    @Test
    public void getEvents_GetsCertainOrderOfEvents_ReturnsSameOrder(){
        eventManager.addEvent(new AddTankEvent(0,0, id));
        eventManager.addEvent(new MoveTankEvent(id, Direction.toByte(Direction.Up), 0));
        eventManager.addEvent(new TurnEvent(id, Direction.toByte(Direction.Left)));
        LinkedList<GridEvent> update = eventManager.getEvents(System.currentTimeMillis()-10000);
        assertEquals(update.size(), 3);
        assertEquals(update.get(0).getType(),"addTank");
        assertEquals(update.get(1).getType(),"moveTank");
        assertEquals(update.get(2).getType(),"turn");
    }
}
