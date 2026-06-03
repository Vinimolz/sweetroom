package com.vinicius.sweetRoom.exceptions;

public class DuplicatedResourceException extends RuntimeException{
    public DuplicatedResourceException(String message){
        super(message);
    }
}
