package edu.unh.cs.cs619.bulletzone.model.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class InvalidResourceTileType extends Exception {
    public InvalidResourceTileType() {
        super(String.format("Requested resource tile type does not exist"));
    }
}
