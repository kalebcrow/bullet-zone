package edu.unh.cs.cs619.bulletzone.model;

public class Iron extends FieldResource {
    int credits = 78;
    int pos;

    public Iron(){

    }

    public Iron(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 503;
    }

    @Override
    public FieldResource copy() {
        return new Iron();
    }

    @Override
    public String toString() {
        return "IB";
    }

    public int getPos(){
        return pos;
    }

    public int getCredits(){
        return credits;
    }
}
