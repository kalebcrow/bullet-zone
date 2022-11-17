package edu.unh.cs.cs619.bulletzone.model.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public final class BuildingDoesNotExistException extends Exception {
    public BuildingDoesNotExistException() {
        super(String.format("Requested Building does not exist"));
    }
}
