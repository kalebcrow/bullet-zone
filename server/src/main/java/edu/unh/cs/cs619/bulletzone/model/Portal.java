package edu.unh.cs.cs619.bulletzone.model;

public class Portal extends FieldEntity {
    int pos;
    Direction direction;
    Portal exit;

    public Portal() {
    }

    public Portal(Direction d) {
        this.direction = d;
    }

    public void setExit(Portal exit) {
        this.exit = exit;
    }

    @Override
    public FieldEntity copy() {
        return new Portal();
    }

    @Override
    public int getIntValue() {
        return 2000 + Direction.toByte(direction);
    }

    @Override
    public String toString() {
        return "P";
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
         this.pos = pos;
    }
}
