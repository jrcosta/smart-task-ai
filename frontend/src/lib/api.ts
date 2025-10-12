/**
 * Cliente HTTP configurado com interceptadores para lidar com autenticação JWT e chamadas REST.
 */
import axios from 'axios';
import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  Task,
  TaskRequest,
  AIAnalysisRequest,
  AIAnalysisResponse,
} from '@/types';

/**
 * Instância Axios base para todas as requisições à API do Smart Task Manager.
 */
const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para adicionar token JWT
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para tratar erros de autenticação
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
/**
 * Serviços de autenticação (login e registro) consumidos pelo frontend.
 */
export const authAPI = {
  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', data);
    return response.data;
  },

  register: async (data: RegisterRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/register', data);
    return response.data;
  },
};

// Tasks API
/**
 * Serviços relacionados às operações CRUD de tarefas.
 */
export const tasksAPI = {
  getAll: async (): Promise<Task[]> => {
    const response = await api.get<Task[]>('/tasks');
    return response.data;
  },

  getById: async (id: number): Promise<Task> => {
    const response = await api.get<Task>(`/tasks/${id}`);
    return response.data;
  },

  getByStatus: async (status: string): Promise<Task[]> => {
    const response = await api.get<Task[]>(`/tasks/status/${status}`);
    return response.data;
  },

  getOverdue: async (): Promise<Task[]> => {
    const response = await api.get<Task[]>('/tasks/overdue');
    return response.data;
  },

  create: async (data: TaskRequest): Promise<Task> => {
    const response = await api.post<Task>('/tasks', data);
    return response.data;
  },

  createWithAI: async (data: TaskRequest): Promise<Task> => {
    const response = await api.post<Task>('/tasks/ai', data);
    return response.data;
  },

  update: async (id: number, data: TaskRequest): Promise<Task> => {
    const response = await api.put<Task>(`/tasks/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/tasks/${id}`);
  },
};

// AI API
/**
 * Serviço de integração com a API de análise de tarefas alimentada por IA.
 */
export const aiAPI = {
  analyze: async (data: AIAnalysisRequest): Promise<AIAnalysisResponse> => {
    const response = await api.post<AIAnalysisResponse>('/ai/analyze', data);
    return response.data;
  },
};

export default api;
