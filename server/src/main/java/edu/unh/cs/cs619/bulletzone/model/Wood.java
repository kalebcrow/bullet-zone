package edu.unh.cs.cs619.bulletzone.model;

public class Wood extends FieldResource {
    int credits = 7;
    int pos;

    public Wood(){

    }

    public Wood(int pos){
        this.pos = pos;
    }

    @Override
    public int getIntValue() {
        return 504;
    }

    @Override
    public FieldResource copy() {
        return new Wood();
    }

    @Override
    public String toString() {
        return "WB";
    }

    public int getPos(){
        return pos;
    }

    public int getCredits(){
        return credits;
    }
}
