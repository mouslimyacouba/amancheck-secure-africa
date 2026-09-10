import React, { useState } from 'react';
import { blink } from '@/blink/client';
import type { ReportsRow } from '@/lib/db-types';
import { useLanguage } from '@/hooks/useLanguage';
import { 
  AlertTriangle, 
  Send, 
  Type, 
  FileText, 
  Camera,
  CheckCircle2
} from 'lucide-react';
import { Card, Button, Input, Textarea, Select, SelectTrigger, SelectValue, SelectContent, SelectItem, toast } from '@blinkdotnew/ui';

export function ReportScam() {
  const { t } = useLanguage();
  const [loading, setLoading] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [formData, setFormData] = useState({
    type: 'phone',
    target: '',
    description: '',
  });
  const [file, setFile] = useState<File | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.target || !formData.description) {
      toast.error('Veuillez remplir tous les champs obligatoires');
      return;
    }

    setLoading(true);
    try {
      let proofUrl = '';
      if (file) {
        const { publicUrl } = await blink.storage.upload(file, `reports/${Date.now()}_${file.name}`);
        proofUrl = publicUrl;
      }

      const reportsTable = blink.db.table<ReportsRow>('reports');
      await reportsTable.create({
        type: formData.type,
        target: formData.target,
        description: formData.description,
        proofUrl,
        status: 'pending'
      });

      setSubmitted(true);
      toast.success('Signalement envoyé avec succès');
    } catch (error) {
      console.error('Reporting failed', error);
      toast.error('Erreur lors de l\'envoi du signalement');
    } finally {
      setLoading(false);
    }
  };

  if (submitted) {
    return (
      <div className="max-w-xl mx-auto p-12 text-center space-y-6">
        <div className="w-20 h-20 rounded-full bg-primary/10 flex items-center justify-center text-primary mx-auto">
          <CheckCircle2 size={48} />
        </div>
        <div className="space-y-2">
          <h2 className="text-2xl font-bold">Merci pour votre signalement !</h2>
          <p className="text-muted-foreground">
            Votre signalement a été reçu et sera vérifié par notre équipe. Merci de contribuer à la sécurité de la communauté.
          </p>
        </div>
        <Button onClick={() => setSubmitted(false)} variant="outline">Signaler un autre cas</Button>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto space-y-6 p-6">
      <div className="space-y-2">
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <AlertTriangle className="text-destructive" /> {t.report}
        </h1>
        <p className="text-muted-foreground">Aidez-nous à bloquer les fraudeurs en signalant les activités suspectes.</p>
      </div>

      <Card className="p-8 shadow-xl border-t-4 border-t-destructive">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="space-y-2">
            <label className="text-sm font-semibold">{t.phone_number} / {t.link} / {t.app}</label>
            <div className="flex gap-3">
              <Select 
                defaultValue="phone" 
                onValueChange={(val) => setFormData({...formData, type: val})}
              >
                <SelectTrigger className="w-[180px]">
                  <SelectValue placeholder="Type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="phone">{t.phone_number}</SelectItem>
                  <SelectItem value="url">{t.link}</SelectItem>
                  <SelectItem value="app">{t.app}</SelectItem>
                </SelectContent>
              </Select>
              <Input 
                placeholder="Ex: +227 00 00 00 00" 
                className="flex-1"
                value={formData.target}
                onChange={(e) => setFormData({...formData, target: e.target.value})}
              />
            </div>
          </div>

          <div className="space-y-2">
            <label className="text-sm font-semibold">{t.description}</label>
            <Textarea 
              placeholder="Expliquez brièvement ce qui s'est passé..."
              className="min-h-[120px]"
              value={formData.description}
              onChange={(e) => setFormData({...formData, description: e.target.value})}
            />
          </div>

          <div className="space-y-2">
            <label className="text-sm font-semibold">{t.proof} (Facultatif)</label>
            <div className="flex items-center gap-4">
              <label className="flex-1 cursor-pointer">
                <div className="border-2 border-dashed border-border rounded-xl p-4 flex items-center justify-center gap-2 hover:bg-secondary/50 transition-colors">
                  <Camera size={20} className="text-muted-foreground" />
                  <span className="text-sm text-muted-foreground">
                    {file ? file.name : "Ajouter une capture d'écran"}
                  </span>
                </div>
                <input 
                  type="file" 
                  className="hidden" 
                  onChange={(e) => setFile(e.target.files?.[0] || null)}
                  accept="image/*"
                />
              </label>
            </div>
          </div>

          <Button 
            type="submit" 
            className="w-full h-12 gap-2 text-lg font-bold"
            disabled={loading}
          >
            {loading ? <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-white" /> : <Send size={20} />}
            {t.submit_report}
          </Button>
        </form>
      </Card>
    </div>
  );
}
