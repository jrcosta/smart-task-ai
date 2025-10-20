package com.smarttask.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Trata exceções comuns lançadas pela aplicação, traduzindo-as para respostas
 * HTTP amigáveis e registrando informações relevantes em log.
 */
@RestControllerAdvice
@Slf4j
public final class GlobalExceptionHandler {

    /** Mensagem padrão retornada quando o acesso é negado. */
    private static final String PERMISSION_MESSAGE =
        "Você não tem permissão para acessar este recurso.";

    /** Mensagem padrão para credenciais inválidas. */
    private static final String INVALID_CREDENTIALS_MESSAGE =
        "Usuário ou senha inválidos.";

    /** Mensagem genérica utilizada em falhas inesperadas. */
    private static final String GENERIC_ERROR_MESSAGE =
        "Ocorreu um erro inesperado. Tente novamente mais tarde.";

    /** Mensagem de validação utilizada para erros de campos. */
    private static final String VALIDATION_MESSAGE =
        "Alguns campos precisam de atenção.";

    /**
     * Converte {@link ResourceNotFoundException} em resposta HTTP 404.
     *
     * @param ex exceção lançada pela camada de serviço
     * @return resposta contendo detalhes do erro e carimbo de tempo
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
        final ResourceNotFoundException ex) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    /**
     * Converte {@link ResourceAlreadyExistsException} em resposta HTTP 409.
     *
     * @param ex exceção lançada ao detectar duplicidade
     * @return resposta com código HTTP de conflito
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(
        final ResourceAlreadyExistsException ex) {
        log.warn("Recurso duplicado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage()));
    }

    /**
     * Mapeia {@link AccessDeniedException} para resposta HTTP 403.
     *
     * @param ex exceção gerada pela camada de segurança
     * @return resposta com mensagem orientando o usuário
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
        final AccessDeniedException ex) {
        log.warn("Acesso negado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(buildErrorResponse(HttpStatus.FORBIDDEN, PERMISSION_MESSAGE));
    }

    /**
     * Traduz {@link BadCredentialsException} para resposta HTTP 401.
     *
     * @param ex exceção de autenticação gerada pelo Spring Security
     * @return resposta com mensagem indicando credenciais inválidas
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(
        final BadCredentialsException ex) {
        log.warn("Credenciais inválidas: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(buildErrorResponse(HttpStatus.UNAUTHORIZED,
                INVALID_CREDENTIALS_MESSAGE));
    }

    /**
     * Converte {@link IllegalStateException} em resposta HTTP 400.
     *
     * @param ex exceção provocada por regra de negócio inválida
     * @return resposta padronizada com motivo do erro
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
        final IllegalStateException ex) {
        log.warn("Estado inválido detectado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    /**
     * Trata erros de validação de bean, retornando detalhes campo a campo.
     *
     * @param ex exceção com os erros de validação coletados
     * @return mapa contendo mensagens por campo invalido
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
        final MethodArgumentNotValidException ex) {
        final Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            final String fieldName = ((FieldError) error).getField();
            final String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        final Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("mensagem", VALIDATION_MESSAGE);
        response.put("erros", errors);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Captura excecoes nao mapeadas e retorna resposta HTTP 500.
     *
     * @param ex excecao inesperada em tempo de execucao
     * @return resposta generica de erro interno
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
        final Exception ex) {
        log.error("Erro inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        GENERIC_ERROR_MESSAGE));
    }

    /**
     * Cria a estrutura padronizada de resposta de erro.
     *
     * @param status status HTTP a ser retornado
     * @param message mensagem amigavel exibida ao cliente
     * @return estrutura imutável com dados de erro
     */
    private ErrorResponse buildErrorResponse(
            final HttpStatus status,
            final String message) {
        return new ErrorResponse(
                status.value(),
                message,
                LocalDateTime.now());
    }

    /**
     * Estrutura da resposta padrao de erro retornada pela API.
     *
     * @param status codigo HTTP numerico associado ao erro
     * @param message mensagem amigavel exibida ao cliente
     * @param timestamp instante em que a falha foi registrada
     */
    public record ErrorResponse(
            int status,
            String message,
            LocalDateTime timestamp) {
    }
}
