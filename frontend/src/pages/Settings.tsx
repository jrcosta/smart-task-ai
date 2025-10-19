import { useState } from 'react';
import { useMutation, useQuery } from '@tanstack/react-query';
import { toast } from 'react-hot-toast';
import { Eye, EyeOff, Key, MessageSquare, Save, CheckCircle, XCircle } from 'lucide-react';
import Layout from '@/components/Layout';
import { settingsAPI } from '@/lib/api';
import type { SettingsRequest } from '@/types';

/**
 * Página de configuração de chaves de API para integrações externas.
 * Permite configurar credenciais OpenAI e Twilio de forma segura.
 */
export default function Settings() {
  const [showOpenAI, setShowOpenAI] = useState(false);
  const [showTwilioSid, setShowTwilioSid] = useState(false);
  const [showTwilioToken, setShowTwilioToken] = useState(false);

  const [formData, setFormData] = useState<SettingsRequest>({
    openaiApiKey: '',
    twilioAccountSid: '',
    twilioAuthToken: '',
    twilioWhatsappNumber: '',
    userWhatsappNumber: '',
  });

  // Buscar configurações atuais
  const { data: settings, refetch } = useQuery({
    queryKey: ['settings'],
    queryFn: settingsAPI.get,
  });

  // Mutation para atualizar configurações
  const updateMutation = useMutation({
    mutationFn: settingsAPI.update,
    onSuccess: () => {
      toast.success('Configurações atualizadas com sucesso!');
      refetch();
      // Limpar campos de senha após salvar
      setFormData({
        openaiApiKey: '',
        twilioAccountSid: '',
        twilioAuthToken: '',
        twilioWhatsappNumber: formData.twilioWhatsappNumber,
        userWhatsappNumber: formData.userWhatsappNumber,
      });
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Erro ao atualizar configurações');
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    // Remove campos vazios para não enviar ao backend
    const dataToSend: SettingsRequest = {};
    if (formData.openaiApiKey?.trim()) dataToSend.openaiApiKey = formData.openaiApiKey.trim();
    if (formData.twilioAccountSid?.trim()) dataToSend.twilioAccountSid = formData.twilioAccountSid.trim();
    if (formData.twilioAuthToken?.trim()) dataToSend.twilioAuthToken = formData.twilioAuthToken.trim();
    if (formData.twilioWhatsappNumber?.trim()) dataToSend.twilioWhatsappNumber = formData.twilioWhatsappNumber.trim();
    if (formData.userWhatsappNumber?.trim()) dataToSend.userWhatsappNumber = formData.userWhatsappNumber.trim();

    updateMutation.mutate(dataToSend);
  };

  const handleChange = (field: keyof SettingsRequest, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  return (
    <Layout>
      <div className="max-w-4xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Configurações</h1>
          <p className="text-gray-600 mt-2">
            Configure suas chaves de API para habilitar as integrações com IA e WhatsApp
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* OpenAI Configuration */}
          <div className="bg-white rounded-lg shadow-sm border p-6">
            <div className="flex items-start justify-between mb-4">
              <div className="flex items-center space-x-3">
                <div className="p-2 bg-green-100 rounded-lg">
                  <Key className="w-6 h-6 text-green-600" />
                </div>
                <div>
                  <h2 className="text-xl font-semibold text-gray-900">OpenAI (ChatGPT)</h2>
                  <p className="text-sm text-gray-600">
                    Análise inteligente de tarefas com IA
                  </p>
                </div>
              </div>
              {settings?.openaiConfigured ? (
                <div className="flex items-center space-x-2 text-green-600">
                  <CheckCircle className="w-5 h-5" />
                  <span className="text-sm font-medium">Configurado</span>
                </div>
              ) : (
                <div className="flex items-center space-x-2 text-gray-400">
                  <XCircle className="w-5 h-5" />
                  <span className="text-sm font-medium">Não configurado</span>
                </div>
              )}
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  API Key
                </label>
                <div className="relative">
                  <input
                    type={showOpenAI ? 'text' : 'password'}
                    value={formData.openaiApiKey}
                    onChange={(e) => handleChange('openaiApiKey', e.target.value)}
                    placeholder={settings?.openaiConfigured ? '••••••••••••••••' : 'sk-...'}
                    className="w-full px-4 py-2 pr-10 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  />
                  <button
                    type="button"
                    onClick={() => setShowOpenAI(!showOpenAI)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                  >
                    {showOpenAI ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                  </button>
                </div>
                <p className="text-xs text-gray-500 mt-1">
                  Obtenha sua chave em{' '}
                  <a
                    href="https://platform.openai.com/api-keys"
                    target="_blank"
                    rel="noopener noreferrer"
                    className="text-primary-600 hover:underline"
                  >
                    platform.openai.com/api-keys
                  </a>
                </p>
              </div>
            </div>
          </div>

          {/* Twilio WhatsApp Configuration */}
          <div className="bg-white rounded-lg shadow-sm border p-6">
            <div className="flex items-start justify-between mb-4">
              <div className="flex items-center space-x-3">
                <div className="p-2 bg-blue-100 rounded-lg">
                  <MessageSquare className="w-6 h-6 text-blue-600" />
                </div>
                <div>
                  <h2 className="text-xl font-semibold text-gray-900">Twilio WhatsApp</h2>
                  <p className="text-sm text-gray-600">
                    Notificações e lembretes por WhatsApp
                  </p>
                </div>
              </div>
              {settings?.twilioConfigured ? (
                <div className="flex items-center space-x-2 text-green-600">
                  <CheckCircle className="w-5 h-5" />
                  <span className="text-sm font-medium">Configurado</span>
                </div>
              ) : (
                <div className="flex items-center space-x-2 text-gray-400">
                  <XCircle className="w-5 h-5" />
                  <span className="text-sm font-medium">Não configurado</span>
                </div>
              )}
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Account SID
                </label>
                <div className="relative">
                  <input
                    type={showTwilioSid ? 'text' : 'password'}
                    value={formData.twilioAccountSid}
                    onChange={(e) => handleChange('twilioAccountSid', e.target.value)}
                    placeholder={settings?.twilioConfigured ? '••••••••••••••••' : 'AC...'}
                    className="w-full px-4 py-2 pr-10 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  />
                  <button
                    type="button"
                    onClick={() => setShowTwilioSid(!showTwilioSid)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                  >
                    {showTwilioSid ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                  </button>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Auth Token
                </label>
                <div className="relative">
                  <input
                    type={showTwilioToken ? 'text' : 'password'}
                    value={formData.twilioAuthToken}
                    onChange={(e) => handleChange('twilioAuthToken', e.target.value)}
                    placeholder={settings?.twilioConfigured ? '••••••••••••••••' : 'Auth Token'}
                    className="w-full px-4 py-2 pr-10 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                  />
                  <button
                    type="button"
                    onClick={() => setShowTwilioToken(!showTwilioToken)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-700"
                  >
                    {showTwilioToken ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                  </button>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Número WhatsApp Twilio
                </label>
                <input
                  type="text"
                  value={formData.twilioWhatsappNumber}
                  onChange={(e) => handleChange('twilioWhatsappNumber', e.target.value)}
                  placeholder="whatsapp:+14155238886"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                />
                <p className="text-xs text-gray-500 mt-1">
                  Formato: whatsapp:+14155238886
                </p>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Seu Número WhatsApp
                </label>
                <input
                  type="text"
                  value={formData.userWhatsappNumber}
                  onChange={(e) => handleChange('userWhatsappNumber', e.target.value)}
                  placeholder="whatsapp:+5511999999999"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                />
                <p className="text-xs text-gray-500 mt-1">
                  Formato: whatsapp:+5511999999999 (número onde você receberá notificações)
                </p>
              </div>

              <p className="text-xs text-gray-500">
                Configure sua conta em{' '}
                <a
                  href="https://www.twilio.com/console"
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-primary-600 hover:underline"
                >
                  twilio.com/console
                </a>
              </p>
            </div>
          </div>

          {/* Save Button */}
          <div className="flex justify-end">
            <button
              type="submit"
              disabled={updateMutation.isPending}
              className="flex items-center space-x-2 px-6 py-3 bg-primary-600 text-white rounded-lg hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              <Save className="w-5 h-5" />
              <span>{updateMutation.isPending ? 'Salvando...' : 'Salvar Configurações'}</span>
            </button>
          </div>

          {/* Info Message */}
          {settings?.message && (
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
              <p className="text-sm text-blue-800">{settings.message}</p>
            </div>
          )}
        </form>
      </div>
    </Layout>
  );
}
