package edu.unh.cs.cs619.bulletzone.model;

import java.util.Optional;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.swing.text.html.Option;

public class FieldHolder {

    private final Map<Direction, FieldHolder> neighbors = new HashMap<Direction, FieldHolder>();

    // should hold a terrain and also an item
    private Optional<FieldEntity> entityHolder = Optional.empty();
    private Optional<FieldTerrain> terrainHolder = Optional.empty();
    private Optional<FieldEntity> improvementHolder = Optional.empty();
    private int pos;

    public FieldHolder(int i){
        this.pos = i;
    }

    public FieldHolder(){}
    public int getPos(){ return this.pos;}

    public void addNeighbor(Direction direction, FieldHolder fieldHolder) {
        neighbors.put(checkNotNull(direction), checkNotNull(fieldHolder));
    }

    public FieldHolder getNeighbor(Direction direction) {
        return neighbors.get(checkNotNull(direction,
                "Direction cannot be null."));
    }

    public boolean isEntityPresent() {
        return entityHolder.isPresent();
    }

    public FieldTerrain getTerrain() {
        return terrainHolder.get();
    }

    public void setFieldTerrain(FieldTerrain terrain) {
        // set current entity
        terrainHolder = Optional.of(checkNotNull(terrain,
                "FieldTerrain cannot be null."));
    }

    public FieldEntity getEntity() {
        return entityHolder.get();
    }

    public void setFieldEntity(FieldEntity entity) {
        // set current entity
        entityHolder = Optional.of(checkNotNull(entity,
                "FieldEntity cannot be null."));
    }

    public boolean isImprovementPresent(){
        return improvementHolder.isPresent();
    }

    public FieldEntity getImprovement() {
        return improvementHolder.get();
    }

    public void setImprovementEntity(FieldEntity entity) {
        // set current entity
        improvementHolder = Optional.of(checkNotNull(entity,
                "FieldEntity cannot be null."));
    }

    public void setTerrain(FieldTerrain terrain) {
        terrainHolder = Optional.of(checkNotNull(terrain,
                "FieldEntity cannot be null."));
    }

    public void clearField() {
        entityHolder = Optional.empty();
    }

    public void clearImprovement() {
        improvementHolder = Optional.empty();
    }

}
