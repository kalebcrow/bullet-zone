package edu.unh.cs.cs619.bulletzone.game;


import android.util.Log;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import java.util.Iterator;
import java.util.LinkedList;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.events.AddTankEvent;
import edu.unh.cs.cs619.bulletzone.game.events.DestroyBulletEvent;
import edu.unh.cs.cs619.bulletzone.game.events.DestroyTankEvent;
import edu.unh.cs.cs619.bulletzone.game.events.DestroyWallEvent;
import edu.unh.cs.cs619.bulletzone.game.events.ExecutableEvent;
import edu.unh.cs.cs619.bulletzone.game.events.FireEvent;
import edu.unh.cs.cs619.bulletzone.game.events.GridEvent;
import edu.unh.cs.cs619.bulletzone.game.events.MoveBulletEvent;
import edu.unh.cs.cs619.bulletzone.game.events.MoveTankEvent;
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

    private final Object monitor = new Object();

    private boolean waiting;

    /**
     * Command interpreter
     *
     */
    public CommandInterpreter() {
        eventHistory = new LinkedList<>();
        waiting = false;
        updateBoard();
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
            addToHistory(event);
        }
    };

    /**
     *
     */
    @Background
    public void updateBoard() {
        synchronized (monitor) {
            if (eventHistory.isEmpty()) {
                try {
                        waiting = true;
                        monitor.wait();
                        Log.d("Yeah", "we cool");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Thread Interrupted");
                }
            }
            GridEvent currEvent = eventHistory.get(0);
            int i = 0;
            while (true) {
                if (!paused) {
                    interpret(currEvent);
                }
                i++;
                if (eventHistory.size() != i - 1) {
                    try {
                        waiting = true;
                        monitor.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Thread Interrupted");
                    }
                }
                currEvent = eventHistory.get(i);
            }
        }
    }

    private void addToHistory(HistoryUpdateEvent event) {
        this.ew = event.getHw();
        eventHistory.addAll(ew.getUpdate());

        synchronized (monitor) {
            if (waiting && ew.getUpdate().size() != 0) {
                waiting = false;
                monitor.notify();
            }
        }

    }


    /**
     *
     * @param currEvent restEvent
     * @return executable event
     */
    @UiThread
    public void interpret(GridEvent currEvent) {
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
                Log.d("Yeah", "we cool");
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
            default:
                event = new ExecutableEvent(currEvent);
        }

        event.execute(busProvider.getEventBus());
    }

     public void pause() {
         paused = true;
     }

}
