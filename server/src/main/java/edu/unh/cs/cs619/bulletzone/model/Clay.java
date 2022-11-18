package edu.unh.cs.cs619.bulletzone.model;

public class Clay extends FieldResource {
    int credits = 16;
    int pos;

    public Clay(){

    }

    public Clay(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 501;
    }

    @Override
    public FieldResource copy() {
        return new Clay();
    }

    @Override
    public String toString() {
        return "CB";
    }

    public int getPos(){
        return pos;
    }

    public int getCredits(){
        return credits;
    }
}
