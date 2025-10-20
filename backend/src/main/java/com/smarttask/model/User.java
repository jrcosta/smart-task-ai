package com.smarttask.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entidade JPA que representa um usuário do sistema Smart Task Manager.
 *
 * <p>Armazena informacoes de cadastro, autenticacao e perfis (roles) de
 * usuarios, incluindo:</p>
 * <ul>
 *   <li>Credenciais (username, email, senha)</li>
 *   <li>Dados pessoais (nome completo, avatar)</li>
 *   <li>Status da conta (ativo/inativo)</li>
 *   <li>Papéis/permissões (roles) no sistema</li>
 *   <li>Tarefas associadas</li>
 *   <li>Datas de auditoria (criação e modificação)</li>
 * </ul>
 *
 * <p>Usa {@link AuditingEntityListener} para rastreamento automático de datas
 * de criação e modificação.</p>
 *
 * @author Smart Task AI Team
 * @version 1.0
 * @since 2025-10
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    /** Tamanho mínimo permitido para o username. */
    public static final int USERNAME_MIN_LENGTH = 3;

    /** Tamanho máximo permitido para o username. */
    public static final int USERNAME_MAX_LENGTH = 50;

    /**
     * Identificador único do usuário.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nome único do usuário (username).
     * Usado para login.
     * Deve ter entre 3 e 50 caracteres.
     */
    @NotBlank
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH)
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Email único do usuário.
     * Usado para contato e recuperação de senha.
     * Deve ser um email válido.
     */
    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Senha criptografada do usuário.
     * Nunca deve ser armazenada em texto plano.
     */
    @NotBlank
    @Column(nullable = false)
    private String password;

    /**
     * Nome completo do usuário.
     * Campo opcional.
     */
    @Column(name = "full_name")
    private String fullName;

    /**
     * URL do avatar/foto de perfil do usuário.
     * Campo opcional.
     */
    @Column(name = "avatar_url")
    private String avatarUrl;

    /**
     * Indica se a conta do usuário está ativa.
     * Padrão: true
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    /**
     * Conjunto de papéis (roles) atribuídos ao usuário.
     * Determina permissões no sistema (ex: ROLE_USER, ROLE_ADMIN).
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default
    private Set<String> roles = new HashSet<>();

    /**
     * Conjunto de tarefas criadas por este usuário.
     * Deletadas em cascata quando o usuário é removido.
     */
    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    @Builder.Default
    private Set<Task> tasks = new HashSet<>();

    /**
     * Data e hora de criação do usuário.
     * Preenchida automaticamente pelo sistema.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data e hora da última modificação do usuário.
     * Atualizada automaticamente a cada mudança.
     */
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
