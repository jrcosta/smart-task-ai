import { useQuery } from '@tanstack/react-query';
import { tasksAPI } from '@/lib/api';
import Layout from '@/components/Layout';
import { TaskStatus } from '@/types';
import { ListTodo, Clock, CheckCircle2, AlertCircle } from 'lucide-react';
import { Link } from 'react-router-dom';

/**
 * Página inicial com visão geral de métricas, tarefas recentes e ações rápidas.
 */
export default function Dashboard() {
  const { data: tasks = [] } = useQuery({
    queryKey: ['tasks'],
    queryFn: tasksAPI.getAll,
  });

  const todoTasks = tasks.filter((t) => t.status === TaskStatus.TODO);
  const inProgressTasks = tasks.filter((t) => t.status === TaskStatus.IN_PROGRESS);
  const completedTasks = tasks.filter((t) => t.status === TaskStatus.COMPLETED);
  const overdueTasks = tasks.filter(
    (t) => t.dueDate && new Date(t.dueDate) < new Date() && t.status !== TaskStatus.COMPLETED
  );

  const stats = [
    {
      title: 'A Fazer',
      value: todoTasks.length,
      icon: ListTodo,
      color: 'bg-gray-100 text-gray-600',
      link: '/tasks?status=TODO',
    },
    {
      title: 'Em Progresso',
      value: inProgressTasks.length,
      icon: Clock,
      color: 'bg-blue-100 text-blue-600',
      link: '/tasks?status=IN_PROGRESS',
    },
    {
      title: 'Concluídas',
      value: completedTasks.length,
      icon: CheckCircle2,
      color: 'bg-green-100 text-green-600',
      link: '/tasks?status=COMPLETED',
    },
    {
      title: 'Atrasadas',
      value: overdueTasks.length,
      icon: AlertCircle,
      color: 'bg-red-100 text-red-600',
      link: '/tasks?overdue=true',
    },
  ];

  const recentTasks = tasks
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(0, 5);

  return (
    <Layout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-600 mt-1">Visão geral das suas tarefas</p>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {stats.map((stat) => {
            const Icon = stat.icon;
            return (
              <Link
                key={stat.title}
                to={stat.link}
                className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 hover:shadow-md transition-shadow"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm font-medium text-gray-600">{stat.title}</p>
                    <p className="text-3xl font-bold text-gray-900 mt-2">{stat.value}</p>
                  </div>
                  <div className={`p-3 rounded-lg ${stat.color}`}>
                    <Icon className="w-6 h-6" />
                  </div>
                </div>
              </Link>
            );
          })}
        </div>

        {/* Recent Tasks */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200">
          <div className="p-6 border-b">
            <h2 className="text-xl font-semibold text-gray-900">Tarefas Recentes</h2>
          </div>
          <div className="divide-y">
            {recentTasks.length === 0 ? (
              <div className="p-6 text-center text-gray-500">
                Nenhuma tarefa encontrada. Crie sua primeira tarefa!
              </div>
            ) : (
              recentTasks.map((task) => (
                <Link
                  key={task.id}
                  to={`/tasks`}
                  className="p-4 hover:bg-gray-50 flex items-center justify-between"
                >
                  <div className="flex-1">
                    <h3 className="font-medium text-gray-900">{task.title}</h3>
                    <p className="text-sm text-gray-500 mt-1">
                      {task.description?.substring(0, 100)}
                      {task.description && task.description.length > 100 ? '...' : ''}
                    </p>
                  </div>
                  <div className="ml-4">
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-medium ${
                        task.status === TaskStatus.COMPLETED
                          ? 'bg-green-100 text-green-700'
                          : task.status === TaskStatus.IN_PROGRESS
                          ? 'bg-blue-100 text-blue-700'
                          : 'bg-gray-100 text-gray-700'
                      }`}
                    >
                      {task.status === TaskStatus.TODO
                        ? 'A Fazer'
                        : task.status === TaskStatus.IN_PROGRESS
                        ? 'Em Progresso'
                        : task.status === TaskStatus.COMPLETED
                        ? 'Concluída'
                        : task.status}
                    </span>
                  </div>
                </Link>
              ))
            )}
          </div>
        </div>

        {/* Quick Actions */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Ações Rápidas</h2>
          <div className="flex flex-wrap gap-3">
            <Link
              to="/tasks?new=true"
              className="px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700"
            >
              Nova Tarefa
            </Link>
            <Link
              to="/tasks?new=true&ai=true"
              className="px-4 py-2 bg-purple-600 text-white rounded-md hover:bg-purple-700 flex items-center gap-2"
            >
              <span>✨</span>
              Nova Tarefa com IA
            </Link>
          </div>
        </div>
      </div>
    </Layout>
  );
}
