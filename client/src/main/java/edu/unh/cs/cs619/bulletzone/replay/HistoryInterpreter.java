package edu.unh.cs.cs619.bulletzone.replay;

import android.os.SystemClock;
import android.util.Log;

import org.androidannotations.annotations.Background;
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
import edu.unh.cs.cs619.bulletzone.game.events.TurnEvent;
import edu.unh.cs.cs619.bulletzone.util.EventWrapper;

@EBean
public class HistoryInterpreter extends Thread {

    // Injected object
    @Bean
    public BusProvider busProvider;

    private final Object lock = new Object();

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    boolean paused = true;

    public LinkedList<GridEvent> getEventHistory() {
        return eventHistory;
    }

    public void setEventHistory(LinkedList<GridEvent> eventHistory) {
        this.eventHistory = eventHistory;
    }

    LinkedList<GridEvent> eventHistory;
    long speed;

    public EventWrapper ew;

    /**
     * Command interpreter
     *
     */
    public HistoryInterpreter() {
        this.eventHistory = new LinkedList();
        speed = 1;
        pause();
    }

    public void clear() {
        eventHistory.clear();
    }

    @Background
    public void run() {
        if (eventHistory.size() != 0) {
            GridEvent prevEvent = eventHistory.get(0);
            interpret(prevEvent);
            for (int i = 1; i < eventHistory.size(); i++) {
                synchronized(lock) {
                    try {
                        if (paused) {
                            lock.wait();
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Thread Interrupted");
                    }
                }
                GridEvent currEvent = eventHistory.get(i);
                long timeDiff = currEvent.getTime() - prevEvent.getTime();
                try {
                    Thread.sleep(timeDiff/speed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                interpret(currEvent);
                prevEvent = currEvent;
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
            default:
                event = new ExecutableEvent(currEvent);
        }

        event.execute(busProvider.getEventBus());
    }

    public void pause() {
        if (!paused) {
            paused = true;
        }
    }

    public void Resume() {
        if (paused) {
            paused = false;
            synchronized (lock) {
                lock.notify();
            }
        }
    }

    public void speedUp(){
        if (speed < 4) {
            speed += 1;
        }
    }

    public void speedDown() {
        if (speed != 0) {
            speed -= 1;
        }
    }

}
