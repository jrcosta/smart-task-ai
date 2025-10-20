package com.smarttask.exception;

/**
 * Excecao lancada quando um recurso solicitado nao e encontrado no sistema.
 */
public final class ResourceNotFoundException extends RuntimeException {

    /**
    * Constroi a excecao com a mensagem exposta para o consumidor da API.
     *
    * @param message texto explicativo informando qual recurso nao foi
    *                encontrado
     */
    public ResourceNotFoundException(final String message) {
        super(message);
    }
}
