package com.example.amancheck.data.model

enum class Language(val code: String, val displayName: String) {
    FRENCH("fr", "Français"),
    HAUSA("ha", "Hausa")
}

data class Translation(
    val appName: String,
    val tagline: String,
    val searchPlaceholder: String,
    val verify: String,
    val report: String,
    val analyze: String,
    val alerts: String,
    val education: String,
    val login: String,
    val logout: String,
    val searchResults: String,
    val noResults: String,
    val fraudDetected: String,
    val suspicious: String,
    val safe: String,
    val phoneNumber: String,
    val link: String,
    val app: String,
    val riskLevel: String,
    val description: String,
    val proof: String,
    val submitReport: String,
    val aiAnalyzer: String,
    val aiDesc: String,
    val analyzing: String,
    val aiResult: String
)

object Translations {
    val french = Translation(
        appName = "AmanCheck Secure",
        tagline = "Protégez-vous des arnaques au Niger et en Afrique de l'Ouest",
        searchPlaceholder = "Vérifier un numéro, un lien ou une application...",
        verify = "Vérifier",
        report = "Signaler",
        analyze = "Analyser",
        alerts = "Alertes",
        education = "Éducation",
        login = "Se connecter",
        logout = "Déconnexion",
        searchResults = "Résultats de recherche",
        noResults = "Aucun résultat suspect trouvé. Restez vigilant.",
        fraudDetected = "ALERTE : Fraude détectée !",
        suspicious = "Attention : Activité suspecte",
        safe = "Semble sûr",
        phoneNumber = "Numéro de téléphone",
        link = "Lien / URL",
        app = "Application",
        riskLevel = "Niveau de risque",
        description = "Description",
        proof = "Preuve (Capture d'écran)",
        submitReport = "Soumettre le signalement",
        aiAnalyzer = "Assistant IA Sécurité",
        aiDesc = "Téléchargez une capture d'écran d'un message ou d'une application pour analyse.",
        analyzing = "Analyse en cours...",
        aiResult = "Résultat de l'analyse IA"
    )

    val hausa = Translation(
        appName = "AmanCheck Secure",
        tagline = "Kare kanka daga zamba a Nijar da Afirka ta Yamma",
        searchPlaceholder = "Bincika lamba, hanyar yanar gizo ko manhaja...",
        verify = "Bincika",
        report = "Kai Kara",
        analyze = "Bincika Hoto",
        alerts = "Sanarwa",
        education = "Ilimi",
        login = "Shiga",
        logout = "Fita",
        searchResults = "Sakamakon Bincike",
        noResults = "Ba a sami wani abu mai hadari ba. Kasance cikin shiri.",
        fraudDetected = "GARGADI: An sami zamba!",
        suspicious = "Hattara: Akwai shakku",
        safe = "Kamar yana da kyau",
        phoneNumber = "Lambobin waya",
        link = "Hanyar yanar gizo",
        app = "Manhaja",
        riskLevel = "Matakin hadari",
        description = "Bayanai",
        proof = "Shaida (Hoton allo)",
        submitReport = "Aika da Rahoto",
        aiAnalyzer = "Mataimakin IA na Tsaro",
        aiDesc = "Sanya hoton saƙo ko manhaja don bincike.",
        analyzing = "Ana kan bincike...",
        aiResult = "Sakamakon binciken IA"
    )

    fun get(lang: Language): Translation = when (lang) {
        Language.FRENCH -> french
        Language.HAUSA -> hausa
    }
}
