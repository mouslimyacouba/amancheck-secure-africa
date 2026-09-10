import React, { useState, useEffect } from 'react';
import { blink } from '@/blink/client';
import type { EducationContentRow } from '@/lib/db-types';
import { useLanguage } from '@/hooks/useLanguage';
import { 
  BookOpen, 
  ChevronRight,
  ShieldCheck,
  Smartphone,
  Zap
} from 'lucide-react';
import { Card, Button, Badge, Skeleton } from '@blinkdotnew/ui';

type Content = EducationContentRow;

export function EducationSection() {
  const { lang, t } = useLanguage();
  const [content, setContent] = useState<Content[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchContent = async () => {
      try {
        const contentTable = blink.db.table<EducationContentRow>('education_content');
        const data = await contentTable.list();
        setContent(data);
      } catch (error) {
        console.error('Failed to fetch education content', error);
      } finally {
        setLoading(false);
      }
    };
    fetchContent();
  }, []);

  return (
    <div className="max-w-5xl mx-auto space-y-8 p-6">
      <div className="space-y-2">
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <BookOpen className="text-primary" /> {t.education}
        </h1>
        <p className="text-muted-foreground">Apprenez à détecter les arnaques et protégez vos finances.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {loading ? (
          [1, 2].map(i => <Skeleton key={i} className="h-64 w-full rounded-2xl" />)
        ) : content.map(item => (
          <Card key={item.id} className="overflow-hidden flex flex-col group cursor-pointer border-2 hover:border-primary/50 transition-all">
            <div className="h-32 bg-primary/5 flex items-center justify-center text-primary group-hover:scale-105 transition-transform">
              {item.category === 'security' ? <ShieldCheck size={48} /> : <Zap size={48} />}
            </div>
            <div className="p-6 space-y-4 flex-1 flex flex-col">
              <div className="space-y-2 flex-1">
                <Badge variant="secondary" className="bg-primary/10 text-primary">{item.category}</Badge>
                <h3 className="text-xl font-bold">{lang === 'fr' ? item.titleFr : item.titleHa}</h3>
                <p className="text-muted-foreground line-clamp-2">
                  {lang === 'fr' ? item.excerptFr : item.excerptHa}
                </p>
              </div>
              <Button variant="ghost" className="w-full justify-between group-hover:bg-primary/10">
                Lire la suite <ChevronRight size={18} />
              </Button>
            </div>
          </Card>
        ))}
      </div>

      <Card className="p-8 bg-primary text-primary-foreground space-y-6">
        <div className="space-y-2">
          <h2 className="text-2xl font-bold">Conseil de sécurité du jour</h2>
          <p className="text-primary-foreground/90 text-lg italic">
            "Ne communiquez jamais votre code secret Mobile Money à qui que ce soit, même s'ils prétendent être de votre opérateur."
          </p>
        </div>
        <div className="flex gap-4">
          <Badge className="bg-white/20 hover:bg-white/30 text-white border-none">#SecuritéNiger</Badge>
          <Badge className="bg-white/20 hover:bg-white/30 text-white border-none">#AntiScam</Badge>
        </div>
      </Card>
    </div>
  );
}
