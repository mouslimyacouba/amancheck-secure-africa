package com.example.amancheck.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.amancheck.data.model.Language
import com.example.amancheck.ui.components.RiskBadge
import com.example.amancheck.ui.theme.DangerRed
import com.example.amancheck.ui.theme.DangerRedContainer
import com.example.amancheck.ui.theme.PrimaryGreen
import com.example.amancheck.ui.theme.PrimaryGreenContainer
import com.example.amancheck.ui.theme.WarningAmber
import com.example.amancheck.ui.theme.WarningAmberContainer
import com.example.amancheck.ui.viewmodel.AnalyzeUiState
import com.example.amancheck.ui.viewmodel.MainViewModel

@Composable
fun AnalyzeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLang by viewModel.currentLanguage.collectAsState()
    val t by viewModel.translation.collectAsState()
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val analysisNotes by viewModel.analysisNotes.collectAsState()
    val analyzeState by viewModel.analyzeState.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            viewModel.selectAnalysisImage(uri)
        }
    )

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Screen Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column {
                Text(
                    text = t.aiAnalyzer,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = t.aiDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Upload & Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Image Box
                if (selectedImageUri != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant,
                                RoundedCornerShape(14.dp)
                            )
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = selectedImageUri),
                            contentDescription = "Selected screenshot",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { viewModel.selectAnalysisImage(null) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove screenshot",
                                tint = Color.White
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                RoundedCornerShape(14.dp)
                            )
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .testTag("upload_screenshot_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = if (currentLang == Language.FRENCH)
                                    "Cliquez pour choisir une capture d'écran"
                                else
                                    "Danna don zaban hoton saƙo",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (currentLang == Language.FRENCH)
                                    "SMS, WhatsApp, ou application financière"
                                else
                                    "SMS, WhatsApp, ko manhajar kudi",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Text Notes / Paste Field
                OutlinedTextField(
                    value = analysisNotes,
                    onValueChange = { viewModel.setAnalysisNotes(it) },
                    placeholder = {
                        Text(
                            text = if (currentLang == Language.FRENCH)
                                "Collez le message texte ou décrivez l'offre reçue (ex: gain de 500.000F, demande de code PIN...)"
                            else
                                "Sanya sakon da aka turo ko bayyana abin da ake nema..."
                        )
                    },
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("analysis_notes_input")
                )

                // Quick preset buttons to easily test
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.setAnalysisNotes(
                                if (currentLang == Language.FRENCH)
                                    "SMS Orange Money : 'Félicitations! Vous avez gagné 500 000 FCFA. Envoyez votre code secret par SMS pour activer le virement.'"
                                else
                                    "Sakon waya: 'An taya ka murna! Ka ci 500,000 FCFA. Turo lambar sirrinka don karbar kudin.'"
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Test: Faux gain", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.setAnalysisNotes(
                                if (currentLang == Language.FRENCH)
                                    "Urgent: Votre compte Moov Money sera résilié sous 2h. Cliquez sur http://moov-securite-bonus.xyz pour confirmer votre mot de passe."
                                else
                                    "Gaggawa: Za a toshe asusunka na Moov. Shiga hanyar yanar gizo ka bayar da sirrinka."
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Test: Urgence SMS", fontSize = 12.sp)
                    }
                }

                // Analyze Action Button
                Button(
                    onClick = { viewModel.runAnalysis(context) },
                    shape = RoundedCornerShape(14.dp),
                    enabled = (selectedImageUri != null || analysisNotes.isNotBlank()) && analyzeState !is AnalyzeUiState.Analyzing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_analysis_button")
                ) {
                    if (analyzeState is AnalyzeUiState.Analyzing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(t.analyzing)
                    } else {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = t.analyze,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Analysis Results Card
        when (val state = analyzeState) {
            is AnalyzeUiState.Analyzing -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(t.analyzing, fontWeight = FontWeight.Medium)
                    }
                }
            }
            is AnalyzeUiState.Success -> {
                val res = state.result
                val isDangerous = res.verdict.equals("DANGEREUX", ignoreCase = true)
                val isSuspicious = res.verdict.equals("SUSPECT", ignoreCase = true)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("analysis_result_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isDangerous -> DangerRedContainer.copy(alpha = 0.45f)
                            isSuspicious -> WarningAmberContainer.copy(alpha = 0.45f)
                            else -> PrimaryGreenContainer.copy(alpha = 0.45f)
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = when {
                                        isDangerous -> Icons.Default.ReportProblem
                                        isSuspicious -> Icons.Default.Warning
                                        else -> Icons.Default.CheckCircle
                                    },
                                    contentDescription = null,
                                    tint = when {
                                        isDangerous -> DangerRed
                                        isSuspicious -> WarningAmber
                                        else -> PrimaryGreen
                                    },
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = res.verdict,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = when {
                                        isDangerous -> DangerRed
                                        isSuspicious -> WarningAmber
                                        else -> PrimaryGreen
                                    }
                                )
                            }
                            RiskBadge(riskLevel = res.verdict)
                        }

                        // Risk Score Gauge
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (currentLang == Language.FRENCH) "Score de risque estimé :" else "Kimanin hadari:",
                                    style = MaterialTheme.typography.labelMedium
                                )
                                Text(
                                    text = "${res.riskScore}/100",
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isDangerous -> DangerRed
                                        isSuspicious -> WarningAmber
                                        else -> PrimaryGreen
                                    }
                                )
                            }
                            LinearProgressIndicator(
                                progress = { res.riskScore / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = when {
                                    isDangerous -> DangerRed
                                    isSuspicious -> WarningAmber
                                    else -> PrimaryGreen
                                },
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }

                        // Summary
                        Text(
                            text = res.summary,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp
                        )

                        // Key Indicators
                        if (res.keyIndicators.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = if (currentLang == Language.FRENCH) "Indicateurs détectés :" else "Alamomin da aka gano:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                res.keyIndicators.forEach { indicator ->
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ReportProblem,
                                            contentDescription = null,
                                            tint = DangerRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = indicator,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }

                        // Recommendation Box
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = WarningAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = res.recommendation,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Share Analysis Report
                        Button(
                            onClick = {
                                val shareText = "🛡️ Rapport de Sécurité AmanCheck\n" +
                                        "Verdict: ${res.verdict} (Score de risque: ${res.riskScore}/100)\n\n" +
                                        "Résumé:\n${res.summary}\n\n" +
                                        "Indicateurs clés:\n" + res.keyIndicators.joinToString("\n") { "• $it" } + "\n\n" +
                                        "Recommandation:\n${res.recommendation}\n\n" +
                                        "AmanCheck Secure - Protégeons notre communauté contre les arnaques."
                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, null))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("share_analysis_report_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = when (res.verdict) {
                                    "DANGEREUX" -> DangerRed
                                    "SUSPECT" -> WarningAmber
                                    else -> PrimaryGreen
                                }
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentLang == Language.FRENCH) "Partager ce rapport" else "Tura sakamakon binciken nan",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            is AnalyzeUiState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DangerRedContainer)
                ) {
                    Text(
                        text = state.message,
                        color = DangerRed,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            AnalyzeUiState.Idle -> {}
        }
    }
}
