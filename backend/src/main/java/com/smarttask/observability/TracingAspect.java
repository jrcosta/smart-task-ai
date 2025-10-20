package com.smarttask.observability;

import java.lang.reflect.Method;

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

/**
 * Aspecto que intercepta metodos anotados com {@link Traced} e cria spans do
 * OpenTelemetry automaticamente, registrando atributos relevantes da execucao.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class TracingAspect {

    /** Cliente OpenTelemetry responsavel pela criacao de spans. */
    private final Tracer tracer;

    /**
     * Executa o metodo anotado dentro de um span OpenTelemetry, capturando
     * atributos configurados pela anotacao {@link Traced}.
     *
     * @param joinPoint ponto de juncao fornecido pelo AspectJ
     * @return resultado do metodo original
     * @throws Throwable propagado caso o metodo interceptado lance excecao
     */
    @Around("@annotation(com.smarttask.observability.Traced)")
    public Object traceMethod(final ProceedingJoinPoint joinPoint)
            throws Throwable {
        final MethodSignature signature =
                (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        final Traced traced = method.getAnnotation(Traced.class);

        final String spanName = traced.value().isEmpty()
                ? signature.getDeclaringTypeName() + "."
                + signature.getName()
                : traced.value();

        final Span span = tracer.spanBuilder(spanName).startSpan();

        try (Scope scope = span.makeCurrent()) {
            span.setAttribute("component", signature.getDeclaringTypeName());
            span.setAttribute("method", signature.getName());

            if (traced.captureParameters()) {
                captureParameters(joinPoint, signature, span);
            }

            final Object result = joinPoint.proceed();

            if (traced.captureResult() && result != null) {
                span.setAttribute("result", result.toString());
            }

            span.setStatus(StatusCode.OK);
            return result;

        } catch (Throwable throwable) {
            span.setStatus(StatusCode.ERROR, throwable.getMessage());
            span.recordException(throwable);
            log.error(
                    "Erro durante execucao de metodo rastreado: {}",
                    spanName,
                    throwable);
            throw throwable;
        } finally {
            span.end();
        }
    }

    private void captureParameters(
            final ProceedingJoinPoint joinPoint,
            final MethodSignature signature,
            final Span span) {
        final String[] parameterNames = signature.getParameterNames();
        final Object[] args = joinPoint.getArgs();
    for (int index = 0;
        index < parameterNames.length && index < args.length;
        index++) {
            if (args[index] != null) {
                span.setAttribute(
                        "param." + parameterNames[index],
                        args[index].toString());
            }
        }
    }
}
