/**
 * Funções utilitárias compartilhadas em todo o frontend para formatação, estilos e mensagens de erro.
 */
import axios, { type AxiosError } from 'axios';
import { type ClassValue, clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatDate(date: string | undefined): string {
  if (!date) return '';
  return new Date(date).toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  });
}

export function formatDateTime(date: string | undefined): string {
  if (!date) return '';
  return new Date(date).toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export function isOverdue(dueDate: string | undefined): boolean {
  if (!dueDate) return false;
  return new Date(dueDate) < new Date();
}

export function getPriorityColor(priority: string): string {
  const colors: Record<string, string> = {
    LOW: 'text-green-600 bg-green-50',
    MEDIUM: 'text-yellow-600 bg-yellow-50',
    HIGH: 'text-orange-600 bg-orange-50',
    URGENT: 'text-red-600 bg-red-50',
  };
  return colors[priority] || colors.MEDIUM;
}

export function getStatusColor(status: string): string {
  const colors: Record<string, string> = {
    TODO: 'text-gray-600 bg-gray-50',
    IN_PROGRESS: 'text-blue-600 bg-blue-50',
    COMPLETED: 'text-green-600 bg-green-50',
    CANCELLED: 'text-red-600 bg-red-50',
  };
  return colors[status] || colors.TODO;
}

export function getStatusLabel(status: string): string {
  const labels: Record<string, string> = {
    TODO: 'A Fazer',
    IN_PROGRESS: 'Em Progresso',
    COMPLETED: 'Concluída',
    CANCELLED: 'Cancelada',
  };
  return labels[status] || status;
}

export function getPriorityLabel(priority: string): string {
  const labels: Record<string, string> = {
    LOW: 'Baixa',
    MEDIUM: 'Média',
    HIGH: 'Alta',
    URGENT: 'Urgente',
  };
  return labels[priority] || priority;
}

/**
 * Extrai a melhor mensagem de erro de respostas HTTP ou objetos de exceção genéricos.
 */
export function getErrorMessage(error: unknown, fallback = 'Ocorreu um erro inesperado.'): string {
  if (axios.isAxiosError(error)) {
    const err = error as AxiosError<any>;
    const data = err.response?.data;

    if (typeof data === 'string' && data.trim().length > 0) {
      return data;
    }

    if (data?.mensagem) {
      return String(data.mensagem);
    }

    if (data?.message) {
      return String(data.message);
    }

    if (data?.erros && typeof data.erros === 'object') {
      const firstError = Object.values(data.erros)[0];
      if (Array.isArray(firstError)) {
        const value = firstError.find((item) => typeof item === 'string');
        if (value) {
          return value;
        }
      }
      if (typeof firstError === 'string') {
        return firstError;
      }
    }
  }

  if (error instanceof Error && error.message) {
    return error.message;
  }

  return fallback;
}
