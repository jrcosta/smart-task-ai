/**
 * Enumeração de estados possíveis para uma tarefa.
 */
export enum TaskStatus {
  TODO = 'TODO',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
}

/**
 * Níveis de prioridade atribuídos às tarefas.
 */
export enum TaskPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  URGENT = 'URGENT',
}

/**
 * Representa dados essenciais do usuário autenticado.
 */
export interface User {
  id: number;
  username: string;
  email: string;
  fullName?: string;
  avatarUrl?: string;
  roles: string[];
}

/**
 * Dados retornados pelo backend após login ou registro com sucesso.
 */
export interface AuthResponse {
  token: string;
  type: string;
  id: number;
  username: string;
  email: string;
  roles: string[];
}

/**
 * Payload enviado ao backend para autenticação de credenciais.
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * Estrutura de criação de novos usuários pela interface.
 */
export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  fullName?: string;
}

/**
 * Modelo utilizado pelo frontend para representar uma tarefa completa.
 */
export interface Task {
  id: number;
  title: string;
  description?: string;
  status: TaskStatus;
  priority: TaskPriority;
  dueDate?: string;
  completedAt?: string;
  estimatedHours?: number;
  actualHours?: number;
  tags: string[];
  parentTaskId?: number;
  subtaskCount: number;
  aiSuggestedPriority?: boolean;
  aiAnalysis?: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * Dados mínimos necessários para criar ou atualizar uma tarefa.
 */
export interface TaskRequest {
  title: string;
  description?: string;
  status?: TaskStatus;
  priority?: TaskPriority;
  dueDate?: string;
  estimatedHours?: number;
  tags?: string[];
  parentTaskId?: number;
}

/**
 * Entrada utilizada na chamada de análise por IA.
 */
export interface AIAnalysisRequest {
  text: string;
  context?: string;
}

/**
 * Resultado estruturado retornado pela IA para auxiliar no gerenciamento de tarefas.
 */
export interface AIAnalysisResponse {
  summary: string;
  suggestedPriority: TaskPriority;
  estimatedHours: number;
  suggestedTags: string[];
  suggestedSubtasks: string[];
  analysis: string;
}
