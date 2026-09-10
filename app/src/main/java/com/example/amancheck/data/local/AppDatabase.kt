package com.example.amancheck.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        VerifiedItemEntity::class,
        ScamReportEntity::class,
        AlertEntity::class,
        EducationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): AmanCheckDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "amancheck_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.dao())
                    }
                }
            }

            suspend fun populateDatabase(dao: AmanCheckDao) {
                // Pre-populate with known fraud items in West Africa
                val initialVerifiedItems = listOf(
                    VerifiedItemEntity(
                        type = "phone",
                        value = "+227 90 88 12 34",
                        riskLevel = "high",
                        source = "Police Nationale / Signalements Communautaires",
                        description = "Faux agent Orange Money demandant le code secret pour déblocage de compte."
                    ),
                    VerifiedItemEntity(
                        type = "phone",
                        value = "+227 89 44 55 66",
                        riskLevel = "high",
                        source = "Orange Money Niger",
                        description = "Arnaque aux faux gains de tombola Moov/Orange."
                    ),
                    VerifiedItemEntity(
                        type = "phone",
                        value = "+229 61 22 33 44",
                        riskLevel = "suspicious",
                        source = "Signalements Utilisateurs",
                        description = "Appel frauduleux prétendant un virement Mobile Money erroné avec demande de renvoi."
                    ),
                    VerifiedItemEntity(
                        type = "url",
                        value = "http://orange-bonus-promo2025.xyz",
                        riskLevel = "high",
                        source = "CERT-Niger / CyberSécurité",
                        description = "Site de phishing volant les identifiants et numéros de cartes."
                    ),
                    VerifiedItemEntity(
                        type = "url",
                        value = "https://moov-recharge-gratuite.net",
                        riskLevel = "high",
                        source = "Moov Africa Niger",
                        description = "Faux site promettant du crédit gratuit en échange du mot de passe."
                    ),
                    VerifiedItemEntity(
                        type = "app",
                        value = "WaveMoneyBonus.apk",
                        riskLevel = "high",
                        source = "Sécurité Mobile Afrique",
                        description = "Application malveillante contenant un cheval de Troie bancaire (Trojan-Banker)."
                    ),
                    VerifiedItemEntity(
                        type = "app",
                        value = "QuickLoan Niger VIP",
                        riskLevel = "suspicious",
                        source = "Signalements Utilisateurs",
                        description = "Application de prêt illégal avec taux usuraires et chantage aux contacts."
                    )
                )
                dao.insertAllVerifiedItems(initialVerifiedItems)

                // Pre-populate alerts
                val initialAlerts = listOf(
                    AlertEntity(
                        titleFr = "Alerte Fraude : Faux agents Mobile Money",
                        titleHa = "Gargadin Zamba: Masu yin sojan gona na Mobile Money",
                        contentFr = "Des individus se font passer pour des employés d'Orange Money ou Moov Money pour demander votre code secret PIN sous prétexte de mise à jour. Ne communiquez jamais votre code secret !",
                        contentHa = "Wasu mutane na kiran mutane suna cewa su ma'aikatan Orange Money ne ko Moov Money suna neman lambar sirri (PIN). Kada ka taba ba kowa lambar sirrinka!",
                        type = "urgent"
                    ),
                    AlertEntity(
                        titleFr = "Tentatives de phishing par SMS (Faux colis & tombolas)",
                        titleHa = "Sakonni na karya na wayar salula (Kyautar kudi)",
                        contentFr = "Circulation massive de SMS prétendant que vous avez gagné 500 000 FCFA à une tombola. Les liens joints mènent à des formulaires frauduleux.",
                        contentHa = "Akwai sakonnin karya da ke yawo cewa ka ci kyautar 500,000 FCFA. Hanyoyin yanar gizon da ke jiki na zamba ne don sace bayananku.",
                        type = "urgent"
                    ),
                    AlertEntity(
                        titleFr = "Attention aux faux recrutements d'ONG et forces armées",
                        titleHa = "Hattara da daukar aikin karya na kungiyoyin agaji",
                        contentFr = "Des arnaqueurs publient de fausses offres d'emploi pour des postes à Niamey, Maradi et Zinder en exigeant des 'frais de dossier' par mobile money.",
                        contentHa = "Masu zamba suna yada sanarwar aikin karya a Niamey, Maradi da Zinder suna neman a tura kudin 'fom' ta waya. Kada ku tura!",
                        type = "warning"
                    )
                )
                dao.insertAllAlerts(initialAlerts)

                // Pre-populate education content
                val initialEducation = listOf(
                    EducationEntity(
                        titleFr = "Comment protéger son compte Mobile Money",
                        titleHa = "Yadda zaka kare asusunka na Mobile Money",
                        excerptFr = "Les 5 règles d'or pour ne jamais perdre votre argent sur Orange Money, Moov ou Wave.",
                        excerptHa = "Dokoki 5 masu muhimmanci don kare kudaden ka a waya.",
                        contentFr = "1. Votre code secret est STRICTEMENT PERSONNEL : aucun agent officiel ne vous le demandera.\n2. Si vous recevez de l'argent par erreur, ne renvoyez jamais directement la somme. Dites à la personne de contacter le service client officiel.\n3. Ne cliquez jamais sur les liens reçus par SMS promettant des recharges ou bonus gratuits.\n4. Vérifiez toujours le nom du destinataire avant de valider tout transfert.\n5. En cas de doute ou de perte de carte SIM, faites bloquer immédiatement votre compte auprès de votre agence.",
                        contentHa = "1. Lambar sirrinka (PIN) na ka ne kai kadai: babu wani ma'aikaci da zai tambaye ka.\n2. Idan wani ya turo maka kudi bisa kuskure, kada ka mayar da kanka. Ka ce ya tuntubi ofis.\n3. Kada ka shiga hanyar yanar gizo da aka turo ta SMS da sunan kyauta.\n4. Bincika sunan wanda kake turawa kudi kafin ka danna 'Send'.\n5. Idan layinka ya bace, je ofis a toshe asusunka nan take.",
                        category = "mobile_money"
                    ),
                    EducationEntity(
                        titleFr = "Reconnaître un message d'hameçonnage (Phishing)",
                        titleHa = "Gane sakon yaudara (Phishing)",
                        excerptFr = "Apprenez à repérer les fautes, les fausses adresses et l'urgence artificielle.",
                        excerptHa = "Koyi yadda zaka gane kura-kurai da hanyoyin yanar gizo na karya.",
                        contentFr = "Les fraudeurs utilisent souvent la peur ou l'empressement pour vous faire agir sans réfléchir ('Votre compte sera bloqué sous 2h', 'Confirmez vos coordonnées immédiatement').\n\nObservez attentivement l'adresse du site web : des lettres inversées (ex: ornage-money au lieu de orange) sont fréquentes. Les entreprises réelles n'utilisent pas d'adresses gratuites (@gmail.com ou .xyz) pour des communications bancaires officielles.",
                        contentHa = "Masu zamba suna amfani da tsoratarwa kamar 'Za a toshe asusunka cikin awa biyu'.\n\nKalli rubutun sosai: sukan canza haruffa (misali 'ornage' maimakon 'orange'). Kamfanoni na gaskiya ba sa amfani da shafukan karya.",
                        category = "security"
                    ),
                    EducationEntity(
                        titleFr = "Arnaques aux faux prêts d'argent",
                        titleHa = "Zambar ba da bashi na karya",
                        excerptFr = "Méfiez-vous des applications de prêt facile qui demandent l'accès à vos contacts et photos.",
                        excerptHa = "Kada ku amince da manhajar da ke ba da bashi cikin sauki amma tana neman hotuna da lambobinku.",
                        contentFr = "Certaines applications mobiles promettent des prêts instantanés sans garantie. Une fois installées, elles aspirent votre répertoire de contacts et vos photos privées. Si vous avez un jour de retard, elles harcèlent vos proches et menacent de publier vos photos. Ne téléchargez jamais d'applications financières en dehors du Google Play Store officiel.",
                        contentHa = "Wasu manhajoji suna alkawarin bashi cikin sauri. Suna kwashe lambobin wayar danginka da hotunanka don yi maka barazana idan baka biya da wuri ba. Kada ka sauke manhajar da ba a Google Play Store take ba.",
                        category = "phishing"
                    )
                )
                dao.insertAllEducation(initialEducation)
            }
        }
    }
}
