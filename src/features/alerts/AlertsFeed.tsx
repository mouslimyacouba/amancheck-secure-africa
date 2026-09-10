import React, { useState, useEffect } from 'react';
import { blink } from '@/blink/client';
import type { AlertsRow } from '@/lib/db-types';
import { useLanguage } from '@/hooks/useLanguage';
import { 
  Bell, 
  AlertTriangle, 
  Info,
  Clock,
  ExternalLink
} from 'lucide-react';
import { Card, Badge, Skeleton } from '@blinkdotnew/ui';

type Alert = AlertsRow;

export function AlertsFeed() {
  const { lang, t } = useLanguage();
  const [alerts, setAlerts] = useState<Alert[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAlerts = async () => {
      try {
        const alertsTable = blink.db.table<AlertsRow>('alerts');
        const data = await alertsTable.list({
          orderBy: { createdAt: 'desc' }
        });
        setAlerts(data);
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
                    <h3 className="text-lg font-bold">{lang === 'fr' ? alert.titleFr : alert.titleHa}</h3>
                    <span className="text-xs text-muted-foreground flex items-center gap-1">
                      <Clock size={12} /> {alert.createdAt ? new Date(alert.createdAt).toLocaleDateString() : ''}
                    </span>
                  </div>
                  <p className="text-muted-foreground leading-relaxed">
                    {lang === 'fr' ? alert.contentFr : alert.contentHa}
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
