package edu.unh.cs.cs619.bulletzone.model.events;



public abstract class GridEvent {

    //Time of event which is needed for getting the right events for the client
    protected Long time;
    //type of event
    protected String type;


    public Long getTime(){
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
