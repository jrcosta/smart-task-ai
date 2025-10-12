import { create } from 'zustand';
import type { Task, TaskStatus } from '@/types';

/**
 * Estado compartilhado de tarefas incluindo filtros, busca e seleção atual.
 */
interface TaskState {
  tasks: Task[];
  selectedTask: Task | null;
  filterStatus: TaskStatus | 'ALL';
  searchQuery: string;
  setTasks: (tasks: Task[]) => void;
  addTask: (task: Task) => void;
  updateTask: (task: Task) => void;
  removeTask: (taskId: number) => void;
  setSelectedTask: (task: Task | null) => void;
  setFilterStatus: (status: TaskStatus | 'ALL') => void;
  setSearchQuery: (query: string) => void;
  getFilteredTasks: () => Task[];
}

/**
 * Hook Zustand que gerencia coleção de tarefas e fornece utilitários de filtragem.
 */
export const useTaskStore = create<TaskState>((set, get) => ({
  tasks: [],
  selectedTask: null,
  filterStatus: 'ALL',
  searchQuery: '',

  setTasks: (tasks) => set({ tasks }),

  addTask: (task) =>
    set((state) => ({
      tasks: [task, ...state.tasks],
    })),

  updateTask: (updatedTask) =>
    set((state) => ({
      tasks: state.tasks.map((task) =>
        task.id === updatedTask.id ? updatedTask : task
      ),
      selectedTask:
        state.selectedTask?.id === updatedTask.id
          ? updatedTask
          : state.selectedTask,
    })),

  removeTask: (taskId) =>
    set((state) => ({
      tasks: state.tasks.filter((task) => task.id !== taskId),
      selectedTask:
        state.selectedTask?.id === taskId ? null : state.selectedTask,
    })),

  setSelectedTask: (task) => set({ selectedTask: task }),

  setFilterStatus: (status) => set({ filterStatus: status }),

  setSearchQuery: (query) => set({ searchQuery: query }),

  getFilteredTasks: () => {
    const { tasks, filterStatus, searchQuery } = get();
    let filtered = tasks;

    // Filtrar por status
    if (filterStatus !== 'ALL') {
      filtered = filtered.filter((task) => task.status === filterStatus);
    }

    // Filtrar por busca
    if (searchQuery) {
      const query = searchQuery.toLowerCase();
      filtered = filtered.filter(
        (task) =>
          task.title.toLowerCase().includes(query) ||
          task.description?.toLowerCase().includes(query) ||
          task.tags.some((tag) => tag.toLowerCase().includes(query))
      );
    }

    return filtered;
  },
}));
