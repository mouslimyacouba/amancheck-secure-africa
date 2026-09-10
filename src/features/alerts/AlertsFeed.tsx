import React, { useState, useEffect } from 'react';
import { blink } from '@/blink/client';
import { useLanguage } from '@/hooks/useLanguage';
import { 
  Bell, 
  AlertTriangle, 
  Info,
  Clock,
  ExternalLink
} from 'lucide-react';
import { Card, Badge, Skeleton } from '@blinkdotnew/ui';

interface Alert {
  id: string;
  title_fr: string;
  title_ha: string;
  content_fr: string;
  content_ha: string;
  type: string;
  created_at: string;
}

export function AlertsFeed() {
  const { lang, t } = useLanguage();
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAlerts = async () => {
      try {
        const { data } = await blink.db.alerts.list({
          orderBy: { created_at: 'desc' }
        });
        setAlerts(data as any);
      } catch (error) {
        console.error('Failed to fetch alerts', error);
      } finally {
        setLoading(false);
      }
    };
    fetchAlerts();
  }, []);

  return (
    <div className="max-w-3xl mx-auto space-y-6 p-6">
      <div className="flex items-center justify-between">
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <Bell className="text-primary" /> {t.alerts}
        </h1>
        <Badge variant="outline">{alerts.length} Nouveaux</Badge>
      </div>

      <div className="space-y-4">
        {loading ? (
          [1, 2, 3].map(i => <Skeleton key={i} className="h-32 w-full rounded-2xl" />)
        ) : alerts.length > 0 ? (
          alerts.map(alert => (
            <Card key={alert.id} className="p-6 hover:shadow-md transition-shadow">
              <div className="flex gap-4">
                <div className={`shrink-0 w-12 h-12 rounded-xl flex items-center justify-center ${
                  alert.type === 'urgent' ? 'bg-destructive/10 text-destructive' : 'bg-primary/10 text-primary'
                }`}>
                  {alert.type === 'urgent' ? <AlertTriangle /> : <Bell />}
                </div>
                <div className="flex-1 space-y-2">
                  <div className="flex items-center justify-between">
                    <h3 className="text-lg font-bold">{lang === 'fr' ? alert.title_fr : alert.title_ha}</h3>
                    <span className="text-xs text-muted-foreground flex items-center gap-1">
                      <Clock size={12} /> {new Date(alert.created_at).toLocaleDateString()}
                    </span>
                  </div>
                  <p className="text-muted-foreground leading-relaxed">
                    {lang === 'fr' ? alert.content_fr : alert.content_ha}
                  </p>
                </div>
              </div>
            </Card>
          ))
        ) : (
          <div className="p-12 text-center text-muted-foreground">
            Aucune alerte pour le moment.
          </div>
        )}
      </div>
    </div>
  );
}
