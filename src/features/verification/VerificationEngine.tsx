import React, { useState } from 'react';
import { blink } from '@/blink/client';
import { useLanguage } from '@/hooks/useLanguage';
import { 
  ShieldCheck, 
  AlertTriangle, 
  ShieldAlert, 
  CheckCircle2, 
  Info,
  ExternalLink,
  Phone
} from 'lucide-react';
import { Card, Badge, Button, LoadingOverlay } from '@blinkdotnew/ui';

interface VerificationResult {
  id: string;
  type: 'phone' | 'url' | 'app';
  value: string;
  risk_level: 'low' | 'high' | 'suspicious';
  source: string;
  created_at: string;
}

export function VerificationEngine() {
  const { t } = useLanguage();
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<VerificationResult | null>(null);
  const [aiRisk, setAiRisk] = useState<string | null>(null);
  const [hasSearched, setHasSearched] = useState(false);

  const handleVerify = async () => {
    if (!query.trim()) return;
    setLoading(true);
    setHasSearched(true);
    setResult(null);
    setAiRisk(null);

    try {
      // 1. Check database for known reports
      const { data } = await blink.db.verified_items.list({
        where: { value: query.trim() }
      });

      if (data && data.length > 0) {
        setResult(data[0] as any);
      } else {
        // 2. If not found, use AI for risk assessment
        const prompt = `Analyse la fiabilité de cet élément au Niger/Afrique de l'Ouest : "${query}". 
        S'il s'agit d'un numéro de téléphone, d'un lien ou d'une application, évalue les risques d'arnaque courants dans cette région (ex: faux gains, usurpation d'identité mobile money).
        Donne un score de risque de 0 à 100 et une explication courte en français.`;

        const { text } = await blink.ai.generateText({ prompt });
        setAiRisk(text);
      }
    } catch (error) {
      console.error('Verification failed', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="relative">
        <div className="absolute inset-y-0 left-4 flex items-center pointer-events-none text-muted-foreground">
          {query.includes('http') ? <ExternalLink size={24} /> : <Phone size={24} />}
        </div>
        <input 
          type="text" 
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleVerify()}
          placeholder={t.search_placeholder}
          className="w-full h-16 pl-14 pr-32 rounded-2xl border-2 border-primary/20 bg-card text-lg focus:outline-none focus:border-primary transition-all shadow-lg"
        />
        <Button 
          className="absolute right-3 top-3 bottom-3 px-8 rounded-xl font-bold"
          onClick={handleVerify}
          disabled={loading}
        >
          {loading ? '...' : t.verify}
        </Button>
      </div>

      {loading && (
        <div className="flex flex-col items-center justify-center p-12 space-y-4">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary" />
          <p className="text-muted-foreground font-medium">{t.analyzing}</p>
        </div>
      )}

      {!loading && hasSearched && (
        <div className="animate-fade-in space-y-4">
          <h2 className="text-xl font-bold px-1">{t.search_results}</h2>
          
          {result ? (
            <Card className={`p-6 border-l-8 ${result.risk_level === 'high' ? 'border-l-destructive' : 'border-l-yellow-500'}`}>
              <div className="flex items-start justify-between">
                <div className="space-y-2">
                  <div className="flex items-center gap-2">
                    {result.risk_level === 'high' ? (
                      <ShieldAlert className="text-destructive" size={24} />
                    ) : (
                      <AlertTriangle className="text-yellow-500" size={24} />
                    )}
                    <h3 className="text-2xl font-bold">
                      {result.risk_level === 'high' ? t.fraud_detected : t.suspicious}
                    </h3>
                  </div>
                  <p className="text-lg font-mono bg-secondary/50 px-3 py-1 rounded inline-block">
                    {result.value}
                  </p>
                  <p className="text-muted-foreground">
                    Source: <span className="font-semibold text-foreground">{result.source}</span>
                  </p>
                </div>
                <Badge variant={result.risk_level === 'high' ? 'destructive' : 'secondary'} className="text-lg py-1 px-4">
                  {result.risk_level.toUpperCase()}
                </Badge>
              </div>
            </Card>
          ) : aiRisk ? (
            <Card className="p-6 border-l-8 border-l-primary/50">
              <div className="space-y-4">
                <div className="flex items-center gap-2 text-primary">
                  <ShieldCheck size={24} />
                  <h3 className="text-xl font-bold">{t.ai_result}</h3>
                </div>
                <div className="prose prose-sm max-w-none text-foreground leading-relaxed whitespace-pre-wrap">
                  {aiRisk}
                </div>
                <div className="bg-blue-500/5 p-4 rounded-xl border border-blue-500/10 flex gap-3">
                  <Info className="text-blue-500 shrink-0" size={20} />
                  <p className="text-sm text-blue-700 italic">
                    Cette analyse est générée par IA. Restez vigilant et vérifiez toujours auprès des sources officielles.
                  </p>
                </div>
              </div>
            </Card>
          ) : (
            <Card className="p-8 text-center space-y-4 border-dashed border-2">
              <CheckCircle2 className="mx-auto text-primary" size={48} />
              <div className="space-y-1">
                <h3 className="text-xl font-bold">{t.no_results}</h3>
                <p className="text-muted-foreground">
                  Cet élément n'est pas répertorié comme frauduleux dans notre base de données.
                </p>
              </div>
              <Button variant="outline" className="rounded-xl">
                En savoir plus sur la sécurité
              </Button>
            </Card>
          )}
        </div>
      )}
    </div>
  );
}
