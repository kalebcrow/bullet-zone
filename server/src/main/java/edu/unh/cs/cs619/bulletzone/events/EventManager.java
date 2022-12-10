package edu.unh.cs.cs619.bulletzone.events;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EventManager {
    // singleton EventManager
    private static EventManager _instance;
    private EventManager(){}
    public static EventManager getInstance(){
        if(_instance == null){
            synchronized(EventManager.class){
                if(_instance == null){
                    _instance = new EventManager();
                }
            }
        }
        return _instance;
    }

    // event history
    private LinkedList<GridEvent> events = new LinkedList<>();

    public void addEvent(GridEvent gridEvent){ events.addFirst(gridEvent); }

    public LinkedList<GridEvent> getEvents(Long time){

        LinkedList<GridEvent> update = new LinkedList<>();
        GridEvent gridEvent;

        // check that the list has the next node
        for (int i = 0; i < events.size(); i++) {
            gridEvent = events.get(i);
            if(gridEvent.getTime() <= time) break; // break if event's time is less recent than time given
            update.addFirst(gridEvent); // otherwise put it at the front of the list
        }
        return update;
    }


    // garbage collection
    private ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);
    Runnable cleanUp = new Runnable() {
        @Override
        public void run() {
            synchronized (EventManager.class){
                ListIterator<GridEvent> it = events.listIterator(events.size());
                GridEvent item;
                Long cutOff = System.currentTimeMillis() - 120000;
                while(it.hasPrevious()){
                    item = it.previous();
                    if(item.getTime() > cutOff) break;
                    events.removeLast();
                }
            }
        }
    };
    ScheduledFuture<?> periodicFuture = sch.scheduleAtFixedRate(cleanUp, 0, 90000, TimeUnit.MILLISECONDS);
}
