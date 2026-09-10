package com.example.amancheck.data.repository

import com.example.amancheck.BuildConfig
import com.example.amancheck.data.local.AlertEntity
import com.example.amancheck.data.local.AmanCheckDao
import com.example.amancheck.data.local.EducationEntity
import com.example.amancheck.data.local.ScamReportEntity
import com.example.amancheck.data.local.VerifiedItemEntity
import com.example.amancheck.data.model.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

data class VerificationResult(
    val query: String,
    val isKnownScam: Boolean,
    val riskLevel: String, // "high", "suspicious", "low"
    val score: Int, // 0 - 100
    val title: String,
    val details: String,
    val source: String? = null
)

data class AnalysisResult(
    val verdict: String, // "DANGEREUX", "SUSPECT", "SÛR"
    val riskScore: Int,
    val summary: String,
    val keyIndicators: List<String>,
    val recommendation: String
)

class AmanCheckRepository(private val dao: AmanCheckDao) {

    val allVerifiedItems: Flow<List<VerifiedItemEntity>> = dao.getAllVerifiedItems()
    val allReports: Flow<List<ScamReportEntity>> = dao.getAllReports()
    val allAlerts: Flow<List<AlertEntity>> = dao.getAllAlerts()
    val allEducation: Flow<List<EducationEntity>> = dao.getAllEducationContent()

    suspend fun submitReport(
        type: String,
        target: String,
        description: String,
        proofUri: String? = null
    ): Long {
        val report = ScamReportEntity(
            type = type,
            target = target.trim(),
            description = description.trim(),
            proofUri = proofUri
        )
        val id = dao.insertReport(report)
        // Also register into verified items as suspicious report
        dao.insertVerifiedItem(
            VerifiedItemEntity(
                type = type,
                value = target.trim(),
                riskLevel = "suspicious",
                source = "Signalement Communautaire Récent (#$id)",
                description = description.trim()
            )
        )
        return id
    }

    suspend fun verifyQuery(query: String, language: Language): VerificationResult = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim()
        val found = dao.findVerifiedItem(cleanQuery)
        if (found != null) {
            val score = if (found.riskLevel == "high") 95 else 70
            val title = if (language == Language.FRENCH) {
                if (found.riskLevel == "high") "ALERTE : Fraude confirmée !" else "Attention : Activité suspecte"
            } else {
                if (found.riskLevel == "high") "GARGADI: An sami zamba!" else "Hattara: Akwai shakku"
            }
            return@withContext VerificationResult(
                query = cleanQuery,
                isKnownScam = true,
                riskLevel = found.riskLevel,
                score = score,
                title = title,
                details = found.description,
                source = found.source
            )
        }

        // Evaluate using regional heuristic AI engine
        evaluateHeuristicRisk(cleanQuery, language)
    }

    private fun evaluateHeuristicRisk(query: String, language: Language): VerificationResult {
        val lower = query.lowercase(Locale.ROOT)
        var score = 10
        val warnings = mutableListOf<String>()

        val suspiciousKeywords = listOf(
            "bonus", "free", "gratuit", "promo", "gagner", "tombola", "orange",
            "moov", "airtel", "wave", "loan", "pret", "argent", "urgence",
            "kyauta", "kudi", "rabo"
        )

        for (kw in suspiciousKeywords) {
            if (lower.contains(kw)) {
                score += 25
                warnings.add(kw)
            }
        }

        val isUrl = lower.startsWith("http") || lower.contains(".xyz") || lower.contains(".tk") || lower.contains(".top") || lower.contains(".cf")
        val isApp = lower.endsWith(".apk") || lower.contains("apk")

        if (isUrl) {
            if (lower.contains(".xyz") || lower.contains(".top") || lower.contains(".tk")) {
                score += 40
            }
            if (lower.contains("orange") || lower.contains("moov") || lower.contains("wave")) {
                score += 35
            }
        }

        if (isApp) {
            score += 35
        }

        if (lower.startsWith("+") && lower.length in 8..15) {
            // Check for international prefixes known for ping-call / fraud
            if (lower.startsWith("+227") || lower.startsWith("+229") || lower.startsWith("+225") || lower.startsWith("+221")) {
                // Regular local number, unless reported
            } else {
                score += 20
            }
        }

        score = score.coerceIn(5, 95)
        val level = when {
            score >= 70 -> "high"
            score >= 40 -> "suspicious"
            else -> "low"
        }

        val (title, details) = when (level) {
            "high" -> {
                if (language == Language.FRENCH) {
                    "ALERTE : Risque d'arnaque élevé (Score: $score/100)" to
                            "Cet élément présente plusieurs caractéristiques typiques de fraudes financières courantes au Niger et en Afrique de l'Ouest (liens non officiels, mots-clés de gain fictif ou usurpation d'opérateur). Ne partagez aucune information personnelle ni code secret."
                } else {
                    "GARGADI: Hadarin zamba yana da yawa (Score: $score/100)" to
                            "Wannan yana da alamomin zamba na kudi a Nijar da Afirka ta Yamma. Kada ka bada lambar sirrinka ko wani bayani na sirri."
                }
            }
            "suspicious" -> {
                if (language == Language.FRENCH) {
                    "Attention : Risque potentiel modéré (Score: $score/100)" to
                            "Cet élément n'est pas certifié. Soyez extrêmement vigilant s'il vous réclame un paiement Mobile Money anticipé ou des informations confidentielles."
                } else {
                    "Hattara: Akwai shakku (Score: $score/100)" to
                            "Ba a tabbatar da sahihancin wannan ba. Yi hankali idan ana neman kudi ko lambar sirri."
                }
            }
            else -> {
                if (language == Language.FRENCH) {
                    "Semble sûr / Aucun signalement (Score: $score/100)" to
                            "Aucun signalement frauduleux trouvé dans notre base communautaire. Continuez toujours à respecter les règles de prudence de base (ne jamais donner son code secret PIN)."
                } else {
                    "Kamar yana da kyau / Babu kara (Score: $score/100)" to
                            "Ba a sami wani abu mai hadari ba. Kasance mai lura da kuma kare lambar sirrinka a kowane lokaci."
                }
            }
        }

        return VerificationResult(
            query = query,
            isKnownScam = false,
            riskLevel = level,
            score = score,
            title = title,
            details = details,
            source = "Analyse Heuristique Régionale AmanCheck"
        )
    }

    suspend fun analyzeTextOrScreenshot(
        userNotes: String,
        language: Language,
        imageBase64: String? = null,
        imageMimeType: String? = null
    ): AnalysisResult = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (!apiKey.isNullOrBlank() && apiKey != "placeholder" && apiKey != "YOUR_GEMINI_API_KEY") {
            try {
                return@withContext callGeminiAnalysis(userNotes, apiKey, language, imageBase64, imageMimeType)
            } catch (e: Exception) {
                // Fallback to local security analyzer
            }
        }
        return@withContext runLocalSecurityAnalysis(userNotes, language)
    }

    private fun callGeminiAnalysis(
        notes: String,
        apiKey: String,
        language: Language,
        imageBase64: String? = null,
        imageMimeType: String? = null
    ): AnalysisResult {
        val prompt = if (language == Language.FRENCH) {
            """
            Tu es un expert en cybersécurité spécialisé dans la détection d'arnaques financières et fraudes Mobile Money en Afrique de l'Ouest (Niger, Mali, Sénégal, Bénin, Côte d'Ivoire, Burkina Faso).
            Analyse le texte, la capture d'écran de message ou la situation suivante : "$notes".
            Vérifie la présence d'usurpation d'opérateurs (Orange, Moov, Airtel, Wave), faux gains/loteries, faux dépôts/reçus de transfert, et demandes de code PIN ou OTP.
            Réponds EXCLUSIVEMENT au format JSON valide avec les clés suivantes :
            {
              "verdict": "DANGEREUX" | "SUSPECT" | "SÛR",
              "riskScore": nombre entre 0 et 100,
              "summary": "résumé en français clair et concis",
              "keyIndicators": ["indicateur 1", "indicateur 2"],
              "recommendation": "conseil pratique"
            }
            """.trimIndent()
        } else {
            """
            Kai kwararre ne kan tsaron intanet da magance zambar kudi da zambar katin kudi ko wayar hannu a Nijar da Afirka ta Yamma.
            Bincika wannan bayani, hoton saƙo ko yanayi: "$notes".
            Bada amsa a tsarin JSON kadai:
            {
              "verdict": "DANGEREUX" | "SUSPECT" | "SÛR",
              "riskScore": lambobi tsakanin 0 da 100,
              "summary": "takaitaccen bayani da harshen Hausa",
              "keyIndicators": ["alamar 1", "alamar 2"],
              "recommendation": "shawara da Hausa"
            }
            """.trimIndent()
        }

        val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true

        val partsArray = JSONArray().apply {
            put(JSONObject().apply {
                put("text", prompt)
            })
            if (!imageBase64.isNullOrBlank() && !imageMimeType.isNullOrBlank()) {
                put(JSONObject().apply {
                    put("inline_data", JSONObject().apply {
                        put("mime_type", imageMimeType)
                        put("data", imageBase64)
                    })
                })
            }
        }

        val requestBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", partsArray)
                })
            })
        }

        OutputStreamWriter(conn.outputStream).use { writer ->
            writer.write(requestBody.toString())
            writer.flush()
        }

        val responseCode = conn.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val response = BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
            val root = JSONObject(response)
            val candidates = root.getJSONArray("candidates")
            val first = candidates.getJSONObject(0)
            val content = first.getJSONObject("content")
            val parts = content.getJSONArray("parts")
            var text = parts.getJSONObject(0).getString("text")

            // Clean markdown json ticks if present
            if (text.startsWith("```json")) {
                text = text.removePrefix("```json").substringBeforeLast("```").trim()
            } else if (text.startsWith("```")) {
                text = text.removePrefix("```").substringBeforeLast("```").trim()
            }

            val json = JSONObject(text)
            val verdict = json.optString("verdict", "SUSPECT")
            val riskScore = json.optInt("riskScore", 60)
            val summary = json.optString("summary", "")
            val indicatorsJson = json.optJSONArray("keyIndicators")
            val indicators = mutableListOf<String>()
            if (indicatorsJson != null) {
                for (i in 0 until indicatorsJson.length()) {
                    indicators.add(indicatorsJson.getString(i))
                }
            }
            val recommendation = json.optString("recommendation", "")

            return AnalysisResult(
                verdict = verdict,
                riskScore = riskScore,
                summary = summary,
                keyIndicators = indicators,
                recommendation = recommendation
            )
        } else {
            throw Exception("HTTP error $responseCode")
        }
    }

    internal fun runLocalSecurityAnalysis(notes: String, language: Language): AnalysisResult {
        val lower = notes.lowercase(Locale.ROOT)
        val indicators = mutableListOf<String>()
        var score = 15

        if (lower.contains("code") || lower.contains("pin") || lower.contains("otp") || lower.contains("mot de passe")) {
            indicators.add(if (language == Language.FRENCH) "Demande de code secret / PIN / OTP" else "Neman lambar sirri ta PIN")
            score += 45
        }
        if (lower.contains("bloqu") || lower.contains("urgent") || lower.contains("vite") || lower.contains("toshe") || lower.contains("gaggawa")) {
            indicators.add(if (language == Language.FRENCH) "Sentiment d'urgence artificielle ou menace de blocage" else "Tsoratarwa ko barazanar toshe asusu")
            score += 25
        }
        if (lower.contains("gagn") || lower.contains("bonus") || lower.contains("gratuit") || lower.contains("tombola") || lower.contains("kyauta")) {
            indicators.add(if (language == Language.FRENCH) "Promesse de gain financier imprévu ou bonus alléchant" else "Alkawarin kyautar kudi da ba ka san da ita ba")
            score += 30
        }
        if (lower.contains("orange") || lower.contains("moov") || lower.contains("airtel") || lower.contains("wave")) {
            indicators.add(if (language == Language.FRENCH) "Mention d'un opérateur de télécommunication ou Mobile Money" else "Amfani da sunan kamfanin sadarwa")
            score += 15
        }
        if (lower.contains("http") || lower.contains(".xyz") || lower.contains("lien") || lower.contains("cliquez")) {
            indicators.add(if (language == Language.FRENCH) "Présence d'un lien web externe non vérifié" else "Akwai hanyar yanar gizo da ba a sani ba")
            score += 20
        }

        score = score.coerceIn(10, 95)
        val verdict = when {
            score >= 70 -> "DANGEREUX"
            score >= 40 -> "SUSPECT"
            else -> "SÛR"
        }

        val summary = if (language == Language.FRENCH) {
            when (verdict) {
                "DANGEREUX" -> "Alerte rouge : Ce contenu rassemble plusieurs techniques d'escroquerie courantes (vol de code PIN, faux gains ou pression psychologique). Ne répondez pas et supprimez le message."
                "SUSPECT" -> "Activité suspecte : Des éléments douteux ont été détectés. Ne cliquez sur aucun lien et ne transférez aucun fonds sans avoir appelé le numéro officiel de votre service client."
                else -> "Analyse satisfaisante : Les éléments fournis ne présentent pas les marqueurs d'arnaque fréquents. Restez toutefois attentif."
            }
        } else {
            when (verdict) {
                "DANGEREUX" -> "Hadari Mai Girma: Wannan saƙo yana da dukkan alamomin zamba ta satar lambar sirri ko neman kudi. Kada ka amsa, goge shi nan take."
                "SUSPECT" -> "Akwai Shakku: An gano wasu alamomi marasa kyau. Kada ka danna wata hanyar yanar gizo kuma kada ka tura kudi."
                else -> "Babu Hadari: Ba a sami alamun zamba a cikin bayanin ba. Sai dai kuma a koda yaushe a kiyaye."
            }
        }

        val recommendation = if (language == Language.FRENCH) {
            "Règle de sécurité : Un véritable opérateur ne vous demandera JAMAIS votre code secret par SMS, appel ou WhatsApp."
        } else {
            "Dokar Tsaro: Babu wani kamfani da zai tambaye ka lambar sirrinka ta waya ko WhatsApp."
        }

        return AnalysisResult(
            verdict = verdict,
            riskScore = score,
            summary = summary,
            keyIndicators = if (indicators.isEmpty()) listOf(if (language == Language.FRENCH) "Aucune anomalie critique détectée" else "Babu wata matsala da aka gani") else indicators,
            recommendation = recommendation
        )
    }
}
