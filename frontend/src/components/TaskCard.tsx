import { Task } from '@/types';
import { formatDate, getPriorityColor, getStatusColor, getPriorityLabel, getStatusLabel, isOverdue } from '@/lib/utils';
import { Calendar, Tag, Clock, AlertCircle, Sparkles } from 'lucide-react';
import { cn } from '@/lib/utils';

/**
 * Propriedades esperadas pelo componente {@link TaskCard}.
 */
interface TaskCardProps {
  task: Task;
  onClick: () => void;
}

/**
 * Componente responsável por exibir informações resumidas de uma tarefa em formato de cartão.
 */
export default function TaskCard({ task, onClick }: TaskCardProps) {
  const overdue = isOverdue(task.dueDate);

  return (
    <div
      onClick={onClick}
      className="bg-white rounded-lg shadow-sm border border-gray-200 p-4 hover:shadow-md transition-shadow cursor-pointer"
    >
      <div className="flex items-start justify-between mb-2">
        <h3 className="text-lg font-semibold text-gray-900 flex-1">
          {task.title}
        </h3>
        {task.aiSuggestedPriority && (
          <span className="ml-2 inline-flex" title="Prioridade sugerida por IA">
            <Sparkles className="w-5 h-5 text-purple-500" aria-hidden="true" />
          </span>
        )}
      </div>

      {task.description && (
        <p className="text-sm text-gray-600 mb-3 line-clamp-2">
          {task.description}
        </p>
      )}

      <div className="flex flex-wrap gap-2 mb-3">
        <span className={cn('px-2 py-1 rounded-full text-xs font-medium', getStatusColor(task.status))}>
          {getStatusLabel(task.status)}
        </span>
        <span className={cn('px-2 py-1 rounded-full text-xs font-medium', getPriorityColor(task.priority))}>
          {getPriorityLabel(task.priority)}
        </span>
      </div>

      <div className="flex items-center gap-4 text-xs text-gray-500">
        {task.dueDate && (
          <div className={cn('flex items-center gap-1', overdue && 'text-red-600')}>
            {overdue && <AlertCircle className="w-3 h-3" />}
            <Calendar className="w-3 h-3" />
            <span>{formatDate(task.dueDate)}</span>
          </div>
        )}

        {task.estimatedHours && (
          <div className="flex items-center gap-1">
            <Clock className="w-3 h-3" />
            <span>{task.estimatedHours}h</span>
          </div>
        )}

        {task.subtaskCount > 0 && (
          <div className="flex items-center gap-1">
            <span>{task.subtaskCount} subtarefas</span>
          </div>
        )}
      </div>

      {task.tags.length > 0 && (
        <div className="flex items-center gap-2 mt-3 flex-wrap">
          <Tag className="w-3 h-3 text-gray-400" />
          {task.tags.map((tag) => (
            <span
              key={tag}
              className="px-2 py-0.5 bg-gray-100 text-gray-600 rounded text-xs"
            >
              {tag}
            </span>
          ))}
        </div>
      )}
    </div>
  );
}
