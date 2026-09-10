package com.example.amancheck

import com.example.amancheck.data.local.AlertEntity
import com.example.amancheck.data.local.AmanCheckDao
import com.example.amancheck.data.local.EducationEntity
import com.example.amancheck.data.local.ScamReportEntity
import com.example.amancheck.data.local.VerifiedItemEntity
import com.example.amancheck.data.model.Language
import com.example.amancheck.data.model.Translations
import com.example.amancheck.data.repository.AmanCheckRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AmanCheckSecurityTest {

    private lateinit var fakeDao: FakeAmanCheckDao
    private lateinit var repository: AmanCheckRepository

    @Before
    fun setUp() {
        fakeDao = FakeAmanCheckDao()
        repository = AmanCheckRepository(fakeDao)
    }

    @Test
    fun testScamLotteryAndPinDetectionInFrench() = runBlocking {
        val scamMessage = "Félicitations! Vous avez gagné 500 000 FCFA Orange Money. Envoyez votre code secret PIN pour valider."
        val result = repository.runLocalSecurityAnalysis(scamMessage, Language.FRENCH)

        assertEquals("DANGEREUX", result.verdict)
        assertTrue(result.riskScore >= 70)
        assertTrue(result.keyIndicators.isNotEmpty())
        assertTrue(result.summary.contains("Alerte rouge") || result.summary.contains("escroquerie"))
    }

    @Test
    fun testUrgentScamInHausa() = runBlocking {
        val scamMessage = "Gaggawa: Za a toshe asusunka na kudi. Turo lambar sirri ko PIN."
        val result = repository.runLocalSecurityAnalysis(scamMessage, Language.HAUSA)

        assertEquals("DANGEREUX", result.verdict)
        assertTrue(result.riskScore >= 70)
        assertTrue(result.recommendation.contains("Dokar Tsaro") || result.recommendation.contains("sirri"))
    }

    @Test
    fun testBenignMessageSafeVerdict() = runBlocking {
        val safeMessage = "Bonjour maman, j'espère que tu te portes bien à Niamey."
        val result = repository.runLocalSecurityAnalysis(safeMessage, Language.FRENCH)

        assertEquals("SÛR", result.verdict)
        assertTrue(result.riskScore < 40)
    }

    @Test
    fun testSuspiciousLinkDetection() = runBlocking {
        val suspiciousMessage = "Cliquez sur http://moov-promo-niger.xyz pour activer votre recharge."
        val result = repository.runLocalSecurityAnalysis(suspiciousMessage, Language.FRENCH)

        assertTrue(result.verdict == "DANGEREUX" || result.verdict == "SUSPECT")
        assertTrue(result.riskScore >= 40)
    }

    @Test
    fun testFrenchAndHausaTranslations() {
        val fr = Translations.get(Language.FRENCH)
        val ha = Translations.get(Language.HAUSA)

        assertNotNull(fr)
        assertNotNull(ha)
        assertEquals("Vérifier", fr.verify)
        assertEquals("Bincika", ha.verify)
        assertEquals("Signaler", fr.report)
        assertEquals("Kai Kara", ha.report)
        assertEquals("AmanCheck Secure", fr.appName)
        assertEquals("AmanCheck Secure", ha.appName)
    }

    @Test
    fun testVerificationDatabaseHit() = runBlocking {
        fakeDao.items.add(
            VerifiedItemEntity(
                id = 1,
                value = "+22799887766",
                type = "phone",
                riskLevel = "high",
                source = "Signalements Niger",
                description = "Faux agent Orange Money"
            )
        )

        val result = repository.verifyQuery("+22799887766", Language.FRENCH)
        assertTrue(result.isKnownScam)
        assertEquals("high", result.riskLevel)
        assertEquals(95, result.score)
        assertTrue(result.details.contains("Faux agent Orange Money"))
    }
}

class FakeAmanCheckDao : AmanCheckDao {
    val items = mutableListOf<VerifiedItemEntity>()
    val reports = mutableListOf<ScamReportEntity>()
    val alerts = mutableListOf<AlertEntity>()
    val education = mutableListOf<EducationEntity>()

    override fun getAllVerifiedItems(): Flow<List<VerifiedItemEntity>> = flowOf(items)

    override suspend fun findVerifiedItem(value: String): VerifiedItemEntity? {
        return items.firstOrNull { it.value.equals(value, ignoreCase = true) }
    }

    override fun searchVerifiedItems(search: String): Flow<List<VerifiedItemEntity>> {
        return flowOf(items.filter { it.value.contains(search, ignoreCase = true) })
    }

    override suspend fun insertVerifiedItem(item: VerifiedItemEntity): Long {
        items.add(item)
        return items.size.toLong()
    }

    override suspend fun insertAllVerifiedItems(items: List<VerifiedItemEntity>): List<Long> {
        this.items.addAll(items)
        return items.mapIndexed { index, _ -> (index + 1).toLong() }
    }

    override fun getAllReports(): Flow<List<ScamReportEntity>> = flowOf(reports)

    override suspend fun insertReport(report: ScamReportEntity): Long {
        reports.add(report)
        return reports.size.toLong()
    }

    override fun getAllAlerts(): Flow<List<AlertEntity>> = flowOf(alerts)

    override suspend fun insertAllAlerts(alerts: List<AlertEntity>): List<Long> {
        this.alerts.addAll(alerts)
        return alerts.mapIndexed { index, _ -> (index + 1).toLong() }
    }

    override fun getAllEducationContent(): Flow<List<EducationEntity>> = flowOf(education)

    override suspend fun insertAllEducation(content: List<EducationEntity>): List<Long> {
        this.education.addAll(content)
        return content.mapIndexed { index, _ -> (index + 1).toLong() }
    }

    override suspend fun getVerifiedItemsCount(): Int = items.size
}
