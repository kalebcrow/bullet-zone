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
        ListIterator<GridEvent> it = events.listIterator(0);
        GridEvent item;
        while(it.hasNext()){
            item = it.next();
            if(item.getTime() < time) break;
            update.addLast(item);
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
