package edu.unh.cs.cs619.bulletzone.model.events;


import org.json.JSONException;
import org.json.JSONObject;

public abstract class GridEvent {

    //Time of event which is needed for getting the right events for the client
    protected Long time;
    //type of event
    protected String type;


    public Long getTime(){return this.time;}

}
