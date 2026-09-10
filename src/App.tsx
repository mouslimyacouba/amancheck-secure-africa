import React, { useState, useEffect } from 'react';
import { 
  createRouter, 
  createRoute, 
  createRootRoute, 
  RouterProvider, 
  Outlet,
  Link,
  useNavigate,
  useParams
} from '@tanstack/react-router';
import { 
  BlinkUIProvider, 
  Toaster, 
  AppShell, 
  AppShellSidebar, 
  AppShellMain, 
  MobileSidebarTrigger,
  SidebarItem,
  Button,
  Avatar,
  Badge,
  Card
} from '@blinkdotnew/ui';
import { 
  Search, 
  AlertTriangle, 
  LayoutDashboard, 
  BookOpen, 
  ShieldCheck, 
  LogOut, 
  User,
  Globe,
  Bell
} from 'lucide-react';
import { blink } from '@/blink/client';
import { useLanguage } from '@/hooks/useLanguage';
import { VerificationEngine } from '@/features/verification/VerificationEngine';
import { AIAnalyzer } from '@/features/ai/AIAnalyzer';
import { ReportScam } from '@/features/reporting/ReportScam';
import { AlertsFeed } from '@/features/alerts/AlertsFeed';
import { EducationSection } from '@/features/education/EducationSection';

// Pages
const HomePage = () => {
  const { t } = useLanguage();
  return (
    <div className="p-6">
      <div className="max-w-4xl mx-auto space-y-8">
        <div className="text-center space-y-4">
          <h1 className="text-5xl font-bold tracking-tight bg-gradient-to-r from-primary to-emerald-600 bg-clip-text text-transparent">
            {t.app_name}
          </h1>
          <p className="text-xl text-muted-foreground">{t.tagline}</p>
        </div>

        <VerificationEngine />

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 pt-8">
          <Link to="/report" className="group">
            <Card className="p-6 h-full hover:border-primary/50 transition-all cursor-pointer space-y-3">
              <div className="w-12 h-12 rounded-xl bg-destructive/10 flex items-center justify-center text-destructive group-hover:scale-110 transition-transform">
                <AlertTriangle size={24} />
              </div>
              <h3 className="font-bold text-lg">{t.report}</h3>
              <p className="text-sm text-muted-foreground">Signalez les tentatives de fraude pour protéger la communauté.</p>
            </Card>
          </Link>
          <Link to="/analyze" className="group">
            <Card className="p-6 h-full hover:border-primary/50 transition-all cursor-pointer space-y-3">
              <div className="w-12 h-12 rounded-xl bg-blue-500/10 flex items-center justify-center text-blue-500 group-hover:scale-110 transition-transform">
                <ShieldCheck size={24} />
              </div>
              <h3 className="font-bold text-lg">{t.analyze}</h3>
              <p className="text-sm text-muted-foreground">Utilisez l'IA pour analyser vos messages et captures d'écran.</p>
            </Card>
          </Link>
          <Link to="/education" className="group">
            <Card className="p-6 h-full hover:border-primary/50 transition-all cursor-pointer space-y-3">
              <div className="w-12 h-12 rounded-xl bg-primary/10 flex items-center justify-center text-primary group-hover:scale-110 transition-transform">
                <BookOpen size={24} />
              </div>
              <h3 className="font-bold text-lg">{t.education}</h3>
              <p className="text-sm text-muted-foreground">Apprenez à identifier les signes d'une arnaque financière.</p>
            </Card>
          </Link>
        </div>
      </div>
    </div>
  );
};

// Layout
const RootLayout = () => {
  const { t, lang, changeLanguage } = useLanguage();
  const [user, setUser] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    return blink.auth.onAuthStateChanged((state) => {
      setUser(state.user);
      if (!state.isLoading) setLoading(false);
    });
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary" />
      </div>
    );
  }

  return (
    <AppShell>
      <AppShellSidebar className="shrink-0">
        <div className="flex flex-col h-full w-[16rem] bg-card border-r border-border overflow-hidden">
          <div className="shrink-0 border-b border-border px-6 h-16 flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg bg-primary flex items-center justify-center text-primary-foreground">
              <ShieldCheck size={20} />
            </div>
            <span className="font-bold text-lg tracking-tight">AmanCheck</span>
          </div>
          
          <div className="flex-1 min-h-0 overflow-y-auto px-2 py-4 space-y-1">
            <SidebarItem icon={<LayoutDashboard size={20} />} label={t.verify} href="/" active />
            <SidebarItem icon={<AlertTriangle size={20} />} label={t.report} href="/report" />
            <SidebarItem icon={<ShieldCheck size={20} />} label={t.analyze} href="/analyze" />
            <SidebarItem icon={<Bell size={20} />} label={t.alerts} href="/alerts" />
            <SidebarItem icon={<BookOpen size={20} />} label={t.education} href="/education" />
          </div>

          <div className="shrink-0 border-t border-border p-4 space-y-4">
            <div className="flex items-center justify-between px-2">
              <Button 
                variant="ghost" 
                size="sm" 
                className="gap-2 text-xs"
                onClick={() => changeLanguage(lang === 'fr' ? 'ha' : 'fr')}
              >
                <Globe size={14} />
                {lang === 'fr' ? 'Hausa' : 'Français'}
              </Button>
            </div>
            
            {user ? (
              <div className="flex items-center gap-3 px-2 py-2 rounded-xl bg-secondary/50">
                <Avatar className="h-8 w-8">
                  <User size={16} />
                </Avatar>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium truncate">{user.displayName || 'Utilisateur'}</p>
                  <button 
                    onClick={() => blink.auth.signOut()}
                    className="text-[10px] text-muted-foreground hover:text-destructive flex items-center gap-1"
                  >
                    <LogOut size={10} /> {t.logout}
                  </button>
                </div>
              </div>
            ) : (
              <Button className="w-full gap-2 rounded-xl" onClick={() => blink.auth.login()}>
                <User size={18} /> {t.login}
              </Button>
            )}
          </div>
        </div>
      </AppShellSidebar>

      <AppShellMain>
        <div className="md:hidden flex items-center justify-between px-4 h-16 border-b border-border bg-card">
          <div className="flex items-center gap-3">
            <MobileSidebarTrigger />
            <span className="font-bold text-primary">AmanCheck</span>
          </div>
          <Button variant="ghost" size="icon" onClick={() => changeLanguage(lang === 'fr' ? 'ha' : 'fr')}>
            <Globe size={18} />
          </Button>
        </div>
        <div className="min-h-screen pb-20">
          <Outlet />
        </div>
      </AppShellMain>
      <Toaster />
    </AppShell>
  );
};

// Routes
const rootRoute = createRootRoute({ component: RootLayout });
const indexRoute = createRoute({ getParentRoute: () => rootRoute, path: '/', component: HomePage });
const reportRoute = createRoute({ getParentRoute: () => rootRoute, path: '/report', component: ReportScam });
const analyzeRoute = createRoute({ getParentRoute: () => rootRoute, path: '/analyze', component: AIAnalyzer });
const alertsRoute = createRoute({ getParentRoute: () => rootRoute, path: '/alerts', component: AlertsFeed });
const educationRoute = createRoute({ getParentRoute: () => rootRoute, path: '/education', component: EducationSection });

const routeTree = rootRoute.addChildren([indexRoute, reportRoute, analyzeRoute, alertsRoute, educationRoute]);
const router = createRouter({ routeTree });

declare module '@tanstack/react-router' {
  interface Register { router: typeof router }
}

export default function App() {
  return (
    <BlinkUIProvider theme="linear">
      <RouterProvider router={router} />
    </BlinkUIProvider>
  );
}