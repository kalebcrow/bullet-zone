package edu.unh.cs.cs619.bulletzone.rest;

import edu.unh.cs.cs619.bulletzone.util.IntArayWrapper;

public class ResourceEvent {
    public int[] resources;

    public ResourceEvent(IntArayWrapper resources) {
        this.resources = resources.getResult();
    }
}
