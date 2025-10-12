import { create } from 'zustand';
import type { User, AuthResponse } from '@/types';

/**
 * Estado e ações disponíveis na store de autenticação utilizando Zustand.
 */
interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (authResponse: AuthResponse) => void;
  logout: () => void;
  loadFromStorage: () => void;
}

/**
 * Hook Zustand responsável por persistir e recuperar credenciais do usuário autenticado.
 */
export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  token: null,
  isAuthenticated: false,

  login: (authResponse: AuthResponse) => {
    const user: User = {
      id: authResponse.id,
      username: authResponse.username,
      email: authResponse.email,
      roles: authResponse.roles,
    };

    localStorage.setItem('token', authResponse.token);
    localStorage.setItem('user', JSON.stringify(user));

    set({
      user,
      token: authResponse.token,
      isAuthenticated: true,
    });
  },

  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');

    set({
      user: null,
      token: null,
      isAuthenticated: false,
    });
  },

  loadFromStorage: () => {
    const token = localStorage.getItem('token');
    const userStr = localStorage.getItem('user');

    if (token && userStr) {
      try {
        const user = JSON.parse(userStr);
        set({
          user,
          token,
          isAuthenticated: true,
        });
      } catch (error) {
        console.error('Error loading auth from storage:', error);
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      }
    }
  },
}));
