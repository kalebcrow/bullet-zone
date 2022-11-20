package edu.unh.cs.cs619.bulletzone.events;

public class AddResourceEvent extends GridEvent {

    public AddResourceEvent(int pos, String resource) {
        this.pos = pos;
        this.type = "addResource";
        this.time = System.currentTimeMillis();
        this.resource = resource;
    }
}
