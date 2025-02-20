package br.com.ordermanager.service.exceptions;

public class DuplicateOrderIdException extends RuntimeException {

    public DuplicateOrderIdException(String message) {
        super(message);
    }
}
