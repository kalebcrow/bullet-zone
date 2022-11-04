package edu.unh.cs.cs619.bulletzone.game;


import android.util.Log;

import com.squareup.otto.Subscribe;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

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

    /**
     *
     * @return Gets command interpreter
     */
    public static CommandInterpreter getCommandInterpreter() {
        if(INSTANCE == null) {
            synchronized (CommandInterpreter.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CommandInterpreter();
                }
            }
        }
        return INSTANCE;
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

        return event;
     }

     public void pause() {
         paused = true;
     }

}
