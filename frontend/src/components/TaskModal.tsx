import { useState, useEffect } from 'react';
import { Task, TaskRequest, TaskStatus, TaskPriority, AIAnalysisResponse } from '@/types';
import { X, Sparkles } from 'lucide-react';
import { aiAPI } from '@/lib/api';
import { getErrorMessage } from '@/lib/utils';
import toast from 'react-hot-toast';

/**
 * Propriedades aceitas pelo modal de criação/edição de tarefas.
 */
interface TaskModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (data: TaskRequest, useAI: boolean) => void;
  task?: Task | null;
}

/**
 * Modal reutilizável para cadastrar ou editar tarefas, com suporte a sugestões de IA e tags.
 */
export default function TaskModal({ isOpen, onClose, onSubmit, task }: TaskModalProps) {
  const [formData, setFormData] = useState<TaskRequest>({
    title: '',
    description: '',
    status: TaskStatus.TODO,
    priority: TaskPriority.MEDIUM,
    tags: [],
  });
  const [tagInput, setTagInput] = useState('');
  const [useAI, setUseAI] = useState(false);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [aiResult, setAiResult] = useState<AIAnalysisResponse | null>(null);

  useEffect(() => {
    if (task) {
      setFormData({
        title: task.title,
        description: task.description,
        status: task.status,
        priority: task.priority,
        dueDate: task.dueDate,
        estimatedHours: task.estimatedHours,
        tags: task.tags,
      });
      setUseAI(false);
      setAiResult(null);
    } else {
      setFormData({
        title: '',
        description: '',
        status: TaskStatus.TODO,
        priority: TaskPriority.MEDIUM,
        tags: [],
      });
      setUseAI(false);
      setAiResult(null);
    }
  }, [task]);

  useEffect(() => {
    if (!isOpen) {
      setUseAI(false);
      setAiResult(null);
      setIsAnalyzing(false);
    }
  }, [isOpen]);

  const requestAISuggestions = async () => {
    if (!formData.title.trim()) {
      toast.error('Preencha o título antes de solicitar sugestões da IA.');
      setUseAI(false);
      return;
    }

    setIsAnalyzing(true);
    try {
      const analysis = await aiAPI.analyze({
        text: `${formData.title} ${formData.description ?? ''}`.trim(),
      });

      setAiResult(analysis);
      setFormData((prev) => ({
        ...prev,
        priority: analysis.suggestedPriority ?? prev.priority,
        estimatedHours: analysis.estimatedHours ?? prev.estimatedHours,
        tags: analysis.suggestedTags?.length ? analysis.suggestedTags : prev.tags,
      }));

      toast.success('Sugestões da IA aplicadas. Confira os campos destacados.');
    } catch (error) {
      toast.error(getErrorMessage(error, 'Não foi possível gerar sugestões com IA.'));
      setUseAI(false);
      setAiResult(null);
    } finally {
      setIsAnalyzing(false);
    }
  };

  const handleAIChange = async (checked: boolean) => {
    setUseAI(checked);
    if (checked) {
      await requestAISuggestions();
    } else {
      setAiResult(null);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSubmit(formData, useAI && !task);
    onClose();
  };

  const handleAddTag = () => {
    if (tagInput.trim() && !formData.tags?.includes(tagInput.trim())) {
      setFormData({
        ...formData,
        tags: [...(formData.tags || []), tagInput.trim()],
      });
      setTagInput('');
    }
  };

  const handleRemoveTag = (tag: string) => {
    setFormData({
      ...formData,
      tags: formData.tags?.filter((t) => t !== tag) || [],
    });
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between p-6 border-b">
          <h2 className="text-2xl font-bold text-gray-900">
            {task ? 'Editar Tarefa' : 'Nova Tarefa'}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Título *
            </label>
            <input
              type="text"
              required
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Descrição
            </label>
            <textarea
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
              rows={4}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Status
              </label>
              <select
                value={formData.status}
                onChange={(e) => setFormData({ ...formData, status: e.target.value as TaskStatus })}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value={TaskStatus.TODO}>A Fazer</option>
                <option value={TaskStatus.IN_PROGRESS}>Em Progresso</option>
                <option value={TaskStatus.COMPLETED}>Concluída</option>
                <option value={TaskStatus.CANCELLED}>Cancelada</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Prioridade
              </label>
              <select
                value={formData.priority}
                onChange={(e) => setFormData({ ...formData, priority: e.target.value as TaskPriority })}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value={TaskPriority.LOW}>Baixa</option>
                <option value={TaskPriority.MEDIUM}>Média</option>
                <option value={TaskPriority.HIGH}>Alta</option>
                <option value={TaskPriority.URGENT}>Urgente</option>
              </select>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Data de Vencimento
              </label>
              <input
                type="datetime-local"
                value={formData.dueDate?.slice(0, 16) || ''}
                onChange={(e) => setFormData({ ...formData, dueDate: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Horas Estimadas
              </label>
              <input
                type="number"
                min="0"
                value={formData.estimatedHours || ''}
                onChange={(e) => setFormData({ ...formData, estimatedHours: parseInt(e.target.value) || undefined })}
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Tags
            </label>
            <div className="flex gap-2 mb-2">
              <input
                type="text"
                value={tagInput}
                onChange={(e) => setTagInput(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), handleAddTag())}
                placeholder="Adicionar tag..."
                className="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
              <button
                type="button"
                onClick={handleAddTag}
                className="px-4 py-2 bg-gray-200 text-gray-700 rounded-md hover:bg-gray-300"
              >
                Adicionar
              </button>
            </div>
            <div className="flex flex-wrap gap-2">
              {formData.tags?.map((tag) => (
                <span
                  key={tag}
                  className="px-3 py-1 bg-primary-100 text-primary-700 rounded-full text-sm flex items-center gap-1"
                >
                  {tag}
                  <button
                    type="button"
                    onClick={() => handleRemoveTag(tag)}
                    className="hover:text-primary-900"
                  >
                    <X className="w-3 h-3" />
                  </button>
                </span>
              ))}
            </div>
          </div>

          {!task && (
            <div className="flex items-center gap-2 p-4 bg-purple-50 rounded-md">
              <input
                type="checkbox"
                id="useAI"
                checked={useAI}
                onChange={(e) => handleAIChange(e.target.checked)}
                className="w-4 h-4 text-purple-600"
              />
              <label htmlFor="useAI" className="flex items-center gap-2 text-sm text-gray-700 cursor-pointer">
                <Sparkles className="w-4 h-4 text-purple-600" />
                Usar IA para sugerir prioridade, tags e subtarefas
              </label>
              {useAI && (
                <button
                  type="button"
                  onClick={requestAISuggestions}
                  disabled={isAnalyzing}
                  className="ml-auto px-3 py-1 text-sm bg-purple-600 text-white rounded-md hover:bg-purple-700 disabled:opacity-60"
                >
                  {isAnalyzing ? 'Gerando...' : 'Atualizar sugestões'}
                </button>
              )}
            </div>
          )}

          {useAI && aiResult && (
            <div className="space-y-3 p-4 bg-white border border-purple-200 rounded-md">
              <div>
                <p className="text-sm font-semibold text-purple-700">Resumo da IA</p>
                <p className="text-sm text-gray-700 mt-1">{aiResult.summary}</p>
              </div>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-3 text-sm text-gray-700">
                <div>
                  <span className="font-semibold">Prioridade sugerida:</span>{' '}
                  <span>{aiResult.suggestedPriority}</span>
                </div>
                <div>
                  <span className="font-semibold">Horas estimadas:</span>{' '}
                  <span>{aiResult.estimatedHours ?? '—'}</span>
                </div>
                <div>
                  <span className="font-semibold">Tags sugeridas:</span>{' '}
                  <span>{aiResult.suggestedTags?.length ? aiResult.suggestedTags.join(', ') : '—'}</span>
                </div>
              </div>
              {aiResult.suggestedSubtasks?.length ? (
                <div>
                  <p className="text-sm font-semibold text-purple-700">Subtarefas sugeridas</p>
                  <ul className="list-disc list-inside text-sm text-gray-700 mt-1">
                    {aiResult.suggestedSubtasks.map((subtask) => (
                      <li key={subtask}>{subtask}</li>
                    ))}
                  </ul>
                </div>
              ) : null}
              {aiResult.analysis && (
                <div>
                  <p className="text-sm font-semibold text-purple-700">Análise detalhada</p>
                  <p className="text-sm text-gray-700 mt-1">{aiResult.analysis}</p>
                </div>
              )}
            </div>
          )}

          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 border border-gray-300 text-gray-700 rounded-md hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="flex-1 px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700"
            >
              {task ? 'Salvar' : 'Criar Tarefa'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
