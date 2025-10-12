import { useState, useEffect } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import Layout from '@/components/Layout';
import { Bell, Send, Clock, AlertCircle, CheckCircle } from 'lucide-react';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import { getErrorMessage } from '@/lib/utils';

interface NotificationPreference {
  id: number;
  whatsappNumber: string;
  enabled: boolean;
  dailyReminderTime: string;
  timezone: string;
  sendOverdueAlerts: boolean;
  sendCompletionSummary: boolean;
}

/**
 * Tela para configuração das preferências de notificações via WhatsApp.
 */
export default function Notifications() {
  const queryClient = useQueryClient();
  const [formData, setFormData] = useState({
    whatsappNumber: '',
    enabled: false,
    dailyReminderTime: '08:30',
    timezone: 'America/Sao_Paulo',
    sendOverdueAlerts: true,
    sendCompletionSummary: true,
  });

  const { data: preferences, isLoading } = useQuery({
    queryKey: ['notification-preferences'],
    queryFn: async () => {
      const response = await api.get<NotificationPreference>('/notifications/preferences');
      return response.data;
    },
    retry: false,
  });

  useEffect(() => {
    if (preferences) {
      setFormData({
        whatsappNumber: preferences.whatsappNumber || '',
        enabled: preferences.enabled,
        dailyReminderTime: preferences.dailyReminderTime || '08:30',
        timezone: preferences.timezone || 'America/Sao_Paulo',
        sendOverdueAlerts: preferences.sendOverdueAlerts,
        sendCompletionSummary: preferences.sendCompletionSummary,
      });
    }
  }, [preferences]);

  const savePreferencesMutation = useMutation({
    mutationFn: async (data: typeof formData) => {
      const response = await api.post('/notifications/preferences', data);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notification-preferences'] });
      toast.success('Preferências salvas com sucesso!');
    },
    onError: (error) => {
      toast.error(getErrorMessage(error, 'Não foi possível salvar as preferências.'));
    },
  });

  const sendTestMutation = useMutation({
    mutationFn: async () => {
      const response = await api.post('/notifications/test');
      return response.data;
    },
    onSuccess: () => {
      toast.success('Mensagem de teste enviada! Verifique seu WhatsApp.');
    },
    onError: (error) => {
      toast.error(getErrorMessage(error, 'Não foi possível enviar a mensagem de teste.'));
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    savePreferencesMutation.mutate(formData);
  };

  const handleTestNotification = () => {
    if (!formData.whatsappNumber) {
      toast.error('Informe um número do WhatsApp antes de enviar o teste.');
      return;
    }
    sendTestMutation.mutate();
  };

  if (isLoading) {
    return (
      <Layout>
        <div className="text-center py-12">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
          <p className="text-gray-600 mt-4">Carregando...</p>
        </div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="max-w-3xl mx-auto space-y-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Notificações WhatsApp</h1>
          <p className="text-gray-600 mt-1">
            Configure lembretes diários e alertas via WhatsApp
          </p>
        </div>

        {/* Info Card */}
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
          <div className="flex items-start gap-3">
            <Bell className="w-5 h-5 text-blue-600 mt-0.5" />
            <div>
              <h3 className="font-semibold text-blue-900">Como funciona?</h3>
              <p className="text-sm text-blue-800 mt-1">
                Configure seu número do WhatsApp e o horário que deseja receber lembretes.
                Você receberá uma mensagem todos os dias com suas tarefas pendentes.
              </p>
            </div>
          </div>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 space-y-6">
          {/* WhatsApp Number */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Número do WhatsApp *
            </label>
            <input
              type="tel"
              required
              value={formData.whatsappNumber}
              onChange={(e) => setFormData({ ...formData, whatsappNumber: e.target.value })}
              placeholder="+5511999999999"
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
            <p className="text-xs text-gray-500 mt-1">
              Use o formato internacional com código do país (ex: +5511999999999)
            </p>
          </div>

          {/* Enable Notifications */}
          <div className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
            <div className="flex items-center gap-3">
              <Bell className="w-5 h-5 text-gray-600" />
              <div>
                <p className="font-medium text-gray-900">Ativar Notificações</p>
                <p className="text-sm text-gray-600">Receber lembretes diários</p>
              </div>
            </div>
            <label className="relative inline-flex items-center cursor-pointer">
              <input
                type="checkbox"
                checked={formData.enabled}
                onChange={(e) => setFormData({ ...formData, enabled: e.target.checked })}
                className="sr-only peer"
              />
              <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-primary-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary-600"></div>
            </label>
          </div>

          {/* Daily Reminder Time */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              <Clock className="w-4 h-4 inline mr-1" />
              Horário do Lembrete Diário
            </label>
            <input
              type="time"
              value={formData.dailyReminderTime}
              onChange={(e) => setFormData({ ...formData, dailyReminderTime: e.target.value })}
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-primary-500"
            />
            <p className="text-xs text-gray-500 mt-1">
              Você receberá uma mensagem todos os dias neste horário
            </p>
          </div>

          {/* Additional Options */}
          <div className="space-y-3">
            <h3 className="font-medium text-gray-900">Opções Adicionais</h3>
            
            <div className="flex items-center gap-3 p-3 border border-gray-200 rounded-lg">
              <input
                type="checkbox"
                id="overdueAlerts"
                checked={formData.sendOverdueAlerts}
                onChange={(e) => setFormData({ ...formData, sendOverdueAlerts: e.target.checked })}
                className="w-4 h-4 text-primary-600"
              />
              <label htmlFor="overdueAlerts" className="flex-1 cursor-pointer">
                <div className="flex items-center gap-2">
                  <AlertCircle className="w-4 h-4 text-orange-600" />
                  <span className="font-medium text-gray-900">Alertas de Tarefas Atrasadas</span>
                </div>
                <p className="text-sm text-gray-600 mt-1">
                  Receber notificações sobre tarefas com prazo vencido
                </p>
              </label>
            </div>

            <div className="flex items-center gap-3 p-3 border border-gray-200 rounded-lg">
              <input
                type="checkbox"
                id="completionSummary"
                checked={formData.sendCompletionSummary}
                onChange={(e) => setFormData({ ...formData, sendCompletionSummary: e.target.checked })}
                className="w-4 h-4 text-primary-600"
              />
              <label htmlFor="completionSummary" className="flex-1 cursor-pointer">
                <div className="flex items-center gap-2">
                  <CheckCircle className="w-4 h-4 text-green-600" />
                  <span className="font-medium text-gray-900">Resumo de Conclusões</span>
                </div>
                <p className="text-sm text-gray-600 mt-1">
                  Receber resumo das tarefas concluídas no dia
                </p>
              </label>
            </div>
          </div>

          {/* Buttons */}
          <div className="flex gap-3 pt-4">
            <button
              type="button"
              onClick={handleTestNotification}
              disabled={sendTestMutation.isPending || !formData.whatsappNumber}
              className="flex-1 flex items-center justify-center gap-2 px-4 py-2 border border-primary-600 text-primary-600 rounded-md hover:bg-primary-50 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <Send className="w-4 h-4" />
              {sendTestMutation.isPending ? 'Enviando...' : 'Enviar Teste'}
            </button>
            <button
              type="submit"
              disabled={savePreferencesMutation.isPending}
              className="flex-1 px-4 py-2 bg-primary-600 text-white rounded-md hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {savePreferencesMutation.isPending ? 'Salvando...' : 'Salvar Configurações'}
            </button>
          </div>
        </form>

        {/* Setup Instructions */}
        <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
          <h3 className="font-semibold text-gray-900 mb-4">📱 Como Configurar o WhatsApp</h3>
          <div className="space-y-3 text-sm text-gray-700">
            <div className="flex gap-3">
              <span className="font-semibold text-primary-600">1.</span>
              <div>
                <p className="font-medium">Crie uma conta no Twilio</p>
                <p className="text-gray-600">Acesse <a href="https://www.twilio.com/try-twilio" target="_blank" rel="noopener noreferrer" className="text-primary-600 hover:underline">twilio.com/try-twilio</a> e crie uma conta gratuita</p>
              </div>
            </div>
            <div className="flex gap-3">
              <span className="font-semibold text-primary-600">2.</span>
              <div>
                <p className="font-medium">Configure o WhatsApp Sandbox</p>
                <p className="text-gray-600">No console do Twilio, vá em Messaging → Try it out → Send a WhatsApp message</p>
              </div>
            </div>
            <div className="flex gap-3">
              <span className="font-semibold text-primary-600">3.</span>
              <div>
                <p className="font-medium">Conecte seu WhatsApp</p>
                <p className="text-gray-600">Envie a mensagem de ativação do sandbox para o número fornecido pelo Twilio</p>
              </div>
            </div>
            <div className="flex gap-3">
              <span className="font-semibold text-primary-600">4.</span>
              <div>
                <p className="font-medium">Configure as credenciais no backend</p>
                <p className="text-gray-600">Adicione suas credenciais do Twilio no arquivo .env do backend</p>
              </div>
            </div>
          </div>
          
          <div className="mt-4 p-3 bg-yellow-50 border border-yellow-200 rounded-md">
            <p className="text-sm text-yellow-800">
              <strong>Nota:</strong> O Twilio oferece créditos gratuitos para teste. Para uso em produção, 
              você precisará de uma conta paga e um número WhatsApp Business aprovado.
            </p>
          </div>
        </div>
      </div>
    </Layout>
  );
}
