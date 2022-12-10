package edu.unh.cs.cs619.bulletzone.game;


import android.util.Log;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.events.AddObstacleEvent;
import edu.unh.cs.cs619.bulletzone.game.events.AddResourceEvent;
import edu.unh.cs.cs619.bulletzone.game.events.AddTankEvent;
import edu.unh.cs.cs619.bulletzone.game.events.BalenceEvent;
import edu.unh.cs.cs619.bulletzone.game.events.DamageTankEvent;
import edu.unh.cs.cs619.bulletzone.game.events.DamageWallEvent;
import edu.unh.cs.cs619.bulletzone.game.events.DestroyBulletEvent;
import edu.unh.cs.cs619.bulletzone.game.events.DestroyResourceEvent;
import edu.unh.cs.cs619.bulletzone.game.events.DestroyTankEvent;
import edu.unh.cs.cs619.bulletzone.game.events.DestroyWallEvent;
import edu.unh.cs.cs619.bulletzone.game.events.DismantleEvent;
import edu.unh.cs.cs619.bulletzone.game.events.ExecutableEvent;
import edu.unh.cs.cs619.bulletzone.game.events.FireEvent;
import edu.unh.cs.cs619.bulletzone.game.events.GridEvent;
import edu.unh.cs.cs619.bulletzone.game.events.MineEvent;
import edu.unh.cs.cs619.bulletzone.game.events.MoveBulletEvent;
import edu.unh.cs.cs619.bulletzone.game.events.MoveTankEvent;
import edu.unh.cs.cs619.bulletzone.game.events.RestrictionEvent;
import edu.unh.cs.cs619.bulletzone.game.events.PortalEvent;
import edu.unh.cs.cs619.bulletzone.game.events.TurnEvent;
import edu.unh.cs.cs619.bulletzone.rest.HistoryUpdateEvent;
import edu.unh.cs.cs619.bulletzone.util.EventWrapper;

@EBean(scope = EBean.Scope.Singleton)
public class CommandInterpreter {

    private static volatile CommandInterpreter INSTANCE = null;

    // Injected object
    @Bean
    BusProvider busProvider;

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    boolean paused = false;

    public LinkedList<GridEvent> getEventHistory() {
        return eventHistory;
    }

    public void setEventHistory(LinkedList<GridEvent> eventHistory) {
        this.eventHistory = eventHistory;
    }

    LinkedList<GridEvent> eventHistory;

    public EventWrapper ew;

    /**
     * Command interpreter
     *
     */
    public CommandInterpreter() {
        eventHistory = new LinkedList<>();

    }

    @AfterInject
    public void setBusProvider(){
        busProvider.getEventBus().register(CommandHistoryUpdateHandler);
    }

    public void clear() {
        eventHistory.clear();
    }

    private Object CommandHistoryUpdateHandler = new Object()
    {
        @Subscribe
        public void onCommandHistoryUpdateEvent(HistoryUpdateEvent event) {
            updateBoard(event);
        }
    };

    /**
     *
     * @param event event
     */
    private void updateBoard(HistoryUpdateEvent event) {
        this.ew = event.getHw();
        LinkedList<GridEvent> history = ew.getUpdate();
        for (int i = 0; i < history.size(); i++) {
            GridEvent currEvent = history.get(i);
            if (!paused) {
                interpret(currEvent).execute(busProvider.getEventBus());
            }
            eventHistory.add(history.get(i));

        }
    }

    /**
     *
     * @param currEvent restEvent
     * @return executable event
     */
     private ExecutableEvent interpret(GridEvent currEvent) {
         ExecutableEvent event;
         switch (currEvent.getType()) {
             case "moveTank":
                 event = new MoveTankEvent(currEvent);
                 break;
             case "moveBullet":
                 event = new MoveBulletEvent(currEvent);
                 break;
             case "destroyTank":
                 event = new DestroyTankEvent(currEvent);
                 break;
             case "destroyWall":
                 event = new DestroyWallEvent(currEvent);
                 break;
             case "fire":
                 event = new FireEvent(currEvent);
                 break;
             case "turn":
                 event = new TurnEvent(currEvent);
                 break;
             case "addTank":
                 event = new AddTankEvent(currEvent);
                 break;
             case "destroyBullet":
                 event = new DestroyBulletEvent(currEvent);
                 break;
             case "build":
                 event = new AddObstacleEvent(currEvent);
                 break;
             case "damageWallEvent":
                 event = new DamageWallEvent(currEvent);
                 break;
             case "damage":
                 event = new DamageTankEvent(currEvent);
                 break;
             case "mine":
                 event = new MineEvent(currEvent);
                 break;
             case "dismantle":
                 event = new DismantleEvent(currEvent);
                 break;
             case "destroyResource":
                 event = new DestroyResourceEvent(currEvent);
                 break;
             case "addResource":
                 event = new AddResourceEvent(currEvent);
                 break;
             case "balance":
                 event = new BalenceEvent(currEvent);
                 break;
             case "restriction":
                 event = new RestrictionEvent(currEvent);
                 break;
             case "portal":
                 event =  new PortalEvent(currEvent);
                 break;
             default:
                 event = new ExecutableEvent(currEvent);
         }

         return event;
     }

     public void pause() {
         paused = true;
     }

}
