import { useState, useEffect } from 'react';

type Language = 'fr' | 'ha';

export const translations = {
  fr: {
    app_name: 'AmanCheck Secure',
    tagline: 'Protégez-vous des arnaques au Niger et en Afrique de l\'Ouest',
    search_placeholder: 'Vérifier un numéro, un lien ou une application...',
    verify: 'Vérifier',
    report: 'Signaler',
    analyze: 'Analyser',
    alerts: 'Alertes',
    education: 'Éducation',
    login: 'Se connecter',
    logout: 'Déconnexion',
    search_results: 'Résultats de recherche',
    no_results: 'Aucun résultat suspect trouvé. Restez vigilant.',
    fraud_detected: 'ALERTE : Fraude détectée !',
    suspicious: 'Attention : Activité suspecte',
    safe: 'Semble sûr',
    phone_number: 'Numéro de téléphone',
    link: 'Lien / URL',
    app: 'Application',
    risk_level: 'Niveau de risque',
    description: 'Description',
    proof: 'Preuve (Capture d\'écran)',
    submit_report: 'Soumettre le signalement',
    ai_analyzer: 'Assistant IA Sécurité',
    ai_desc: 'Téléchargez une capture d\'écran d\'un message ou d\'une application pour analyse.',
    analyzing: 'Analyse en cours...',
    ai_result: 'Résultat de l\'analyse IA',
  },
  ha: {
    app_name: 'AmanCheck Secure',
    tagline: 'Kare kanka daga zamba a Nijar da Afirka ta Yamma',
    search_placeholder: 'Bincika lamba, hanyar yanar gizo ko manhaja...',
    verify: 'Bincika',
    report: 'Kai Kara',
    analyze: 'Bincika Hoto',
    alerts: 'Sanarwa',
    education: 'Ilimi',
    login: 'Shiga',
    logout: 'Fita',
    search_results: 'Sakamakon Bincike',
    no_results: 'Ba a sami wani abu mai hadari ba. Kasance cikin shiri.',
    fraud_detected: 'GARGADI: An sami zamba!',
    suspicious: 'Hattara: Akwai shakku',
    safe: 'Kamar yana da kyau',
    phone_number: 'Lambobin waya',
    link: 'Hanyar yanar gizo',
    app: 'Manhaja',
    risk_level: 'Matakin hadari',
    description: 'Bayanai',
    proof: 'Shaida (Hoton allo)',
    submit_report: 'Aika da Rahoto',
    ai_analyzer: 'Mataimakin IA na Tsaro',
    ai_desc: 'Sanya hoton saƙo ko manhaja don bincike.',
    analyzing: 'Ana kan bincike...',
    ai_result: 'Sakamakon binciken IA',
  }
};

export function useLanguage() {
  const [lang, setLang] = useState<Language>(() => {
    return (localStorage.getItem('amancheck_lang') as Language) || 'fr';
  });

  const t = translations[lang];

  const changeLanguage = (newLang: Language) => {
    setLang(newLang);
    localStorage.setItem('amancheck_lang', newLang);
  };

  return { lang, t, changeLanguage };
}
