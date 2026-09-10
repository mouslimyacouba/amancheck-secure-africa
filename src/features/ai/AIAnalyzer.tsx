import React, { useState } from 'react';
import { blink } from '@/blink/client';
import { useLanguage } from '@/hooks/useLanguage';
import { 
  Upload, 
  ShieldCheck, 
  Image as ImageIcon,
  X,
  Sparkles,
  ShieldAlert
} from 'lucide-react';
import { Card, Button, Badge, LoadingOverlay } from '@blinkdotnew/ui';

export function AIAnalyzer() {
  const { t } = useLanguage();
  const [file, setFile] = useState<File | null>(null);
  const [preview, setPreview] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [analysis, setAnalysis] = useState<string | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selected = e.target.files?.[0];
    if (selected) {
      setFile(selected);
      setPreview(URL.createObjectURL(selected));
      setAnalysis(null);
    }
  };

  const handleAnalyze = async () => {
    if (!file) return;
    setLoading(true);

    try {
      // 1. Upload the image
      const filename = `analysis/${Date.now()}.${file.name.split('.').pop()}`;
      const { publicUrl } = await blink.storage.upload(file, filename);

      // 2. Run Vision Analysis
      const systemPrompt = `Tu es un expert en cybersécurité spécialisé dans la détection d'arnaques en Afrique de l'Ouest (Niger, Sénégal, Mali, etc.). 
      Analyse cette capture d'écran (message SMS, WhatsApp, ou application financière).
      Détecte les signes de fraude : fautes d'orthographe, urgence artificielle, demandes de code OTP, promesses de gains irréalistes, usurpation de marques comme Orange Money, Moov Money ou les banques locales.
      Donne un verdict clair : SÛR, SUSPECT ou DANGEREUX, avec une explication concise.`;

      const { text } = await blink.ai.generateText({
        messages: [
          { role: 'system', content: systemPrompt },
          { role: 'user', content: [
            { type: 'text', text: 'Analyse cette image pour détecter d\'éventuelles arnaques.' },
            { type: 'image', image: publicUrl }
          ]}
        ]
      });

      setAnalysis(text);
    } catch (error) {
      console.error('Analysis failed', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-3xl mx-auto space-y-6 p-6">
      <div className="space-y-2">
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <Sparkles className="text-primary" /> {t.ai_analyzer}
        </h1>
        <p className="text-muted-foreground">{t.ai_desc}</p>
      </div>

      <Card className="p-8 border-2 border-dashed border-primary/20 bg-primary/5">
        {!preview ? (
          <label className="flex flex-col items-center justify-center cursor-pointer space-y-4">
            <div className="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center text-primary">
              <Upload size={32} />
            </div>
            <div className="text-center">
              <p className="text-lg font-semibold">Cliquez pour télécharger une capture d'écran</p>
              <p className="text-sm text-muted-foreground">PNG, JPG ou WEBP (Max 5MB)</p>
            </div>
            <input type="file" className="hidden" onChange={handleFileChange} accept="image/*" />
          </label>
        ) : (
          <div className="space-y-6">
            <div className="relative group max-w-sm mx-auto">
              <img 
                src={preview} 
                alt="Preview" 
                className="rounded-xl shadow-xl border-4 border-white max-h-[400px] object-contain mx-auto" 
              />
              <button 
                onClick={() => { setFile(null); setPreview(null); setAnalysis(null); }}
                className="absolute -top-3 -right-3 p-1 bg-destructive text-destructive-foreground rounded-full shadow-lg"
              >
                <X size={20} />
              </button>
            </div>
            <div className="flex justify-center">
              <Button 
                onClick={handleAnalyze} 
                disabled={loading} 
                size="lg" 
                className="gap-2 rounded-xl px-8"
              >
                {loading ? (
                  <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white" />
                ) : (
                  <ShieldCheck size={20} />
                )}
                {t.analyze}
              </Button>
            </div>
          </div>
        )}
      </Card>

      {analysis && (
        <Card className="p-6 space-y-4 border-l-8 border-l-primary animate-fade-in">
          <div className="flex items-center justify-between">
            <h3 className="text-xl font-bold flex items-center gap-2">
              <ShieldAlert className="text-primary" /> {t.ai_result}
            </h3>
            <Badge variant="outline" className="bg-primary/5 text-primary">IA EXPERT</Badge>
          </div>
          <div className="prose prose-sm max-w-none text-foreground leading-relaxed whitespace-pre-wrap">
            {analysis}
          </div>
        </Card>
      )}
    </div>
  );
}
