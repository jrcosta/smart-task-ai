package com.smarttask.exception;

/**
 * Exceção utilizada quando uma entidade duplicada é identificada durante
 * operações de criação.
 */
public final class ResourceAlreadyExistsException extends RuntimeException {

    /**
     * Cria a exceção com a mensagem amigável que será retornada ao cliente.
     *
     * @param message texto explicativo sobre o conflito encontrado
     */
    public ResourceAlreadyExistsException(final String message) {
        super(message);
    }
}
