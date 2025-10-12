import { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useSearchParams } from 'react-router-dom';
import { tasksAPI } from '@/lib/api';
import { useTaskStore } from '@/store/taskStore';
import Layout from '@/components/Layout';
import TaskCard from '@/components/TaskCard';
import TaskModal from '@/components/TaskModal';
import { TaskStatus, TaskRequest, Task } from '@/types';
import { Plus, Search, Filter } from 'lucide-react';
import toast from 'react-hot-toast';
import { getErrorMessage } from '@/lib/utils';

/**
 * Página que lista tarefas do usuário, permitindo criação, edição e filtragem avançada.
 */
export default function Tasks() {
  const queryClient = useQueryClient();
  const [searchParams, setSearchParams] = useSearchParams();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);
  const { filterStatus, setFilterStatus, searchQuery, setSearchQuery, getFilteredTasks } = useTaskStore();

  const { data: tasks = [], isLoading } = useQuery({
    queryKey: ['tasks'],
    queryFn: tasksAPI.getAll,
  });

  useEffect(() => {
    useTaskStore.getState().setTasks(tasks);
  }, [tasks]);

  useEffect(() => {
    if (searchParams.get('new') === 'true') {
      setIsModalOpen(true);
      searchParams.delete('new');
      setSearchParams(searchParams);
    }
  }, [searchParams, setSearchParams]);

  const createTaskMutation = useMutation({
    mutationFn: ({ data, useAI }: { data: TaskRequest; useAI: boolean }) =>
      useAI ? tasksAPI.createWithAI(data) : tasksAPI.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
      toast.success('Tarefa criada com sucesso!');
    },
    onError: (error) => {
      toast.error(getErrorMessage(error, 'Não foi possível criar a tarefa.'));
    },
  });

  const updateTaskMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: TaskRequest }) =>
      tasksAPI.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['tasks'] });
      toast.success('Tarefa atualizada com sucesso!');
    },
    onError: (error) => {
      toast.error(getErrorMessage(error, 'Não foi possível atualizar a tarefa.'));
    },
  });

  const handleSubmit = (data: TaskRequest, useAI: boolean) => {
    if (selectedTask) {
      updateTaskMutation.mutate({ id: selectedTask.id, data });
    } else {
      createTaskMutation.mutate({ data, useAI });
    }
    setSelectedTask(null);
  };

  const handleTaskClick = (task: Task) => {
    setSelectedTask(task);
    setIsModalOpen(true);
  };

  const handleNewTask = () => {
    setSelectedTask(null);
    setIsModalOpen(true);
  };

  const filteredTasks = getFilteredTasks();

  return (
    <Layout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Minhas Tarefas</h1>
            <p className="text-gray-600 mt-1">
              {filteredTasks.length} {filteredTasks.length === 1 ? 'tarefa' : 'tarefas'}
            </p>
          </div>
          <button
            onClick={handleNewTask}
            className="flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700"
          >
            <Plus className="w-5 h-5" />
            Nova Tarefa
          </button>
        </div>

        {/* Filters */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-4">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                <input
                  type="text"
                  placeholder="Buscar tarefas..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
                />
              </div>
            </div>
            <div className="flex items-center gap-2">
              <Filter className="w-5 h-5 text-gray-400" />
              <select
                value={filterStatus}
                onChange={(e) => setFilterStatus(e.target.value as TaskStatus | 'ALL')}
                className="px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value="ALL">Todos os Status</option>
                <option value={TaskStatus.TODO}>A Fazer</option>
                <option value={TaskStatus.IN_PROGRESS}>Em Progresso</option>
                <option value={TaskStatus.COMPLETED}>Concluídas</option>
                <option value={TaskStatus.CANCELLED}>Canceladas</option>
              </select>
            </div>
          </div>
        </div>

        {/* Tasks Grid */}
        {isLoading ? (
          <div className="text-center py-12">
            <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
            <p className="text-gray-600 mt-4">Carregando tarefas...</p>
          </div>
        ) : filteredTasks.length === 0 ? (
          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
            <p className="text-gray-600 mb-4">
              {searchQuery || filterStatus !== 'ALL'
                ? 'Nenhuma tarefa encontrada com os filtros aplicados.'
                : 'Você ainda não tem tarefas. Crie sua primeira tarefa!'}
            </p>
            <button
              onClick={handleNewTask}
              className="inline-flex items-center gap-2 px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700"
            >
              <Plus className="w-5 h-5" />
              Criar Primeira Tarefa
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {filteredTasks.map((task) => (
              <TaskCard key={task.id} task={task} onClick={() => handleTaskClick(task)} />
            ))}
          </div>
        )}
      </div>

      <TaskModal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setSelectedTask(null);
        }}
        onSubmit={handleSubmit}
        task={selectedTask}
      />
    </Layout>
  );
}
