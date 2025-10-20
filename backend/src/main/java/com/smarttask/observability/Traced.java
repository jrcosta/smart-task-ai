package com.smarttask.observability;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotacao para marcar metodos que devem ser instrumentados com traces do
 * OpenTelemetry. Quando aplicada, cria automaticamente um span com informacoes
 * sobre a execucao.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Traced {
    /**
     * Nome customizado do span. Se nao fornecido, usa o nome do metodo.
     *
     * @return nome do span a ser criado para o metodo anotado
     */
    String value() default "";

    /**
     * Indica se deve capturar parametros do metodo como atributos do span.
     *
     * @return {@code true} quando os parametros devem ser anexados ao span
     */
    boolean captureParameters() default false;

    /**
     * Indica se deve capturar o retorno do metodo como atributo do span.
     *
     * @return {@code true} quando o resultado do metodo deve ser registrado
     */
    boolean captureResult() default false;
}
