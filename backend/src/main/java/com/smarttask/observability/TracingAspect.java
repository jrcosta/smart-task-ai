package com.smarttask.observability;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Aspecto que intercepta métodos anotados com @Traced e cria spans do OpenTelemetry
 * automaticamente, capturando informações sobre a execução.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class TracingAspect {

    private final Tracer tracer;

    @Around("@annotation(com.smarttask.observability.Traced)")
    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Traced traced = method.getAnnotation(Traced.class);

        String spanName = traced.value().isEmpty() 
                ? signature.getDeclaringTypeName() + "." + signature.getName()
                : traced.value();

        Span span = tracer.spanBuilder(spanName).startSpan();

        try (Scope scope = span.makeCurrent()) {
            // Adiciona atributos do método
            span.setAttribute("component", signature.getDeclaringTypeName());
            span.setAttribute("method", signature.getName());

            // Captura parâmetros se solicitado
            if (traced.captureParameters()) {
                String[] parameterNames = signature.getParameterNames();
                Object[] args = joinPoint.getArgs();
                for (int i = 0; i < parameterNames.length && i < args.length; i++) {
                    if (args[i] != null) {
                        span.setAttribute("param." + parameterNames[i], args[i].toString());
                    }
                }
            }

            // Executa o método
            Object result = joinPoint.proceed();

            // Captura resultado se solicitado
            if (traced.captureResult() && result != null) {
                span.setAttribute("result", result.toString());
            }

            span.setStatus(StatusCode.OK);
            return result;

        } catch (Throwable t) {
            span.setStatus(StatusCode.ERROR, t.getMessage());
            span.recordException(t);
            log.error("Erro durante execução de método rastreado: {}", spanName, t);
            throw t;
        } finally {
            span.end();
        }
    }
}
