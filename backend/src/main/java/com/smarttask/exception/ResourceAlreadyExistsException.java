package com.smarttask.exception;

/**
 * Exceção utilizada quando uma entidade duplicada é identificada durante operações de criação.
 */
public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
