package com.smarttask.observability;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para marcar métodos que devem ser instrumentados com traces do OpenTelemetry.
 * Quando aplicada a um método, cria automaticamente um span com informações sobre a execução.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Traced {
    /**
     * Nome customizado do span. Se não fornecido, usa o nome do método.
     */
    String value() default "";
    
    /**
     * Indica se deve capturar parâmetros do método como atributos do span.
     */
    boolean captureParameters() default false;
    
    /**
     * Indica se deve capturar o retorno do método como atributo do span.
     */
    boolean captureResult() default false;
}
