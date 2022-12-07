package edu.unh.cs.cs619.bulletzone.model;

public class FusionGenerator extends FieldResource {
    @Override
    public int getIntValue() {
        return 3121;
    }

    @Override
    public FieldResource copy() {
        return new FusionGenerator();
    }

    @Override
    public void hit(int damage){
        super.hit(damage);
        if(parent.getNeighbor(Direction.Up).isEntityPresent()){
            parent.getNeighbor(Direction.Up).getEntity().hit(50);
        }
        if(parent.getNeighbor(Direction.Left).isEntityPresent()){
            parent.getNeighbor(Direction.Left).getEntity().hit(50);
        }
        if(parent.getNeighbor(Direction.Right).isEntityPresent()){
            parent.getNeighbor(Direction.Right).getEntity().hit(50);
        }
        if(parent.getNeighbor(Direction.Down).isEntityPresent()){
            parent.getNeighbor(Direction.Down).getEntity().hit(50);
        }
    }

    @Override
    public boolean gather(Tank tank) {
        tank.enhance(new FusionGenerated());
        return true;
    }
}
