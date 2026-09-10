package com.example.amancheck.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.amancheck.data.local.AlertEntity
import com.example.amancheck.data.local.EducationEntity
import com.example.amancheck.data.local.ScamReportEntity
import com.example.amancheck.data.local.VerifiedItemEntity
import com.example.amancheck.data.model.Language
import com.example.amancheck.data.model.Translation
import com.example.amancheck.data.model.Translations
import com.example.amancheck.data.repository.AmanCheckRepository
import com.example.amancheck.data.repository.AnalysisResult
import com.example.amancheck.data.repository.VerificationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface VerifyUiState {
    data object Idle : VerifyUiState
    data object Loading : VerifyUiState
    data class Success(val result: VerificationResult) : VerifyUiState
    data class Error(val message: String) : VerifyUiState
}

sealed interface AnalyzeUiState {
    data object Idle : AnalyzeUiState
    data object Analyzing : AnalyzeUiState
    data class Success(val result: AnalysisResult) : AnalyzeUiState
    data class Error(val message: String) : AnalyzeUiState
}

data class ReportFormState(
    val type: String = "phone", // "phone", "url", "app"
    val target: String = "",
    val description: String = "",
    val proofUri: Uri? = null,
    val isSubmitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val errorMessage: String? = null
)

class MainViewModel(private val repository: AmanCheckRepository) : ViewModel() {

    private val _currentLanguage = MutableStateFlow(Language.FRENCH)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    private val _translation = MutableStateFlow(Translations.get(Language.FRENCH))
    val translation: StateFlow<Translation> = _translation.asStateFlow()

    // Verification
    var searchQuery = MutableStateFlow("")
        private set
    private val _verifyState = MutableStateFlow<VerifyUiState>(VerifyUiState.Idle)
    val verifyState: StateFlow<VerifyUiState> = _verifyState.asStateFlow()

    // Analysis
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _analysisNotes = MutableStateFlow("")
    val analysisNotes: StateFlow<String> = _analysisNotes.asStateFlow()

    private val _analyzeState = MutableStateFlow<AnalyzeUiState>(AnalyzeUiState.Idle)
    val analyzeState: StateFlow<AnalyzeUiState> = _analyzeState.asStateFlow()

    // Report
    private val _reportFormState = MutableStateFlow(ReportFormState())
    val reportFormState: StateFlow<ReportFormState> = _reportFormState.asStateFlow()

    // Alerts & Education
    val alerts: StateFlow<List<AlertEntity>> = repository.allAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val educationContent: StateFlow<List<EducationEntity>> = repository.allEducation
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val verifiedItems: StateFlow<List<VerifiedItemEntity>> = repository.allVerifiedItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleLanguage() {
        val next = if (_currentLanguage.value == Language.FRENCH) Language.HAUSA else Language.FRENCH
        _currentLanguage.value = next
        _translation.value = Translations.get(next)
    }

    fun setLanguage(language: Language) {
        _currentLanguage.value = language
        _translation.value = Translations.get(language)
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun performVerification() {
        val query = searchQuery.value.trim()
        if (query.isEmpty()) return

        viewModelScope.launch {
            _verifyState.value = VerifyUiState.Loading
            try {
                val result = repository.verifyQuery(query, _currentLanguage.value)
                _verifyState.value = VerifyUiState.Success(result)
            } catch (e: Exception) {
                _verifyState.value = VerifyUiState.Error(e.localizedMessage ?: "Erreur de vérification")
            }
        }
    }

    fun clearVerification() {
        searchQuery.value = ""
        _verifyState.value = VerifyUiState.Idle
    }

    fun selectAnalysisImage(uri: Uri?) {
        _selectedImageUri.value = uri
        _analyzeState.value = AnalyzeUiState.Idle
    }

    fun setAnalysisNotes(notes: String) {
        _analysisNotes.value = notes
    }

    fun runAnalysis(context: Context? = null) {
        val notes = _analysisNotes.value.trim().ifEmpty {
            if (_selectedImageUri.value != null) {
                "Capture d'écran de message SMS/WhatsApp ou application mobile suspecte demandant des actions financières immédiates."
            } else {
                return
            }
        }

        viewModelScope.launch {
            _analyzeState.value = AnalyzeUiState.Analyzing
            try {
                var imageBase64: String? = null
                var imageMimeType: String? = null
                val uri = _selectedImageUri.value
                if (uri != null && context != null) {
                    try {
                        context.contentResolver.openInputStream(uri)?.use { stream ->
                            val bytes = stream.readBytes()
                            imageBase64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                            imageMimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                        }
                    } catch (e: Exception) {
                        // ignore image conversion failure and continue with text
                    }
                }

                val result = repository.analyzeTextOrScreenshot(
                    userNotes = notes,
                    language = _currentLanguage.value,
                    imageBase64 = imageBase64,
                    imageMimeType = imageMimeType
                )
                _analyzeState.value = AnalyzeUiState.Success(result)
            } catch (e: Exception) {
                _analyzeState.value = AnalyzeUiState.Error(e.localizedMessage ?: "Erreur d'analyse")
            }
        }
    }

    fun updateReportType(type: String) {
        _reportFormState.value = _reportFormState.value.copy(type = type)
    }

    fun updateReportTarget(target: String) {
        _reportFormState.value = _reportFormState.value.copy(target = target)
    }

    fun updateReportDescription(desc: String) {
        _reportFormState.value = _reportFormState.value.copy(description = desc)
    }

    fun updateReportProof(uri: Uri?) {
        _reportFormState.value = _reportFormState.value.copy(proofUri = uri)
    }

    fun submitReport() {
        val state = _reportFormState.value
        if (state.target.isBlank() || state.description.isBlank()) {
            _reportFormState.value = state.copy(
                errorMessage = if (_currentLanguage.value == Language.FRENCH)
                    "Veuillez remplir tous les champs obligatoires"
                else
                    "Da fatan a cika dukkan wuraren da ake bukata"
            )
            return
        }

        viewModelScope.launch {
            _reportFormState.value = state.copy(isSubmitting = true, errorMessage = null)
            try {
                repository.submitReport(
                    type = state.type,
                    target = state.target,
                    description = state.description,
                    proofUri = state.proofUri?.toString()
                )
                _reportFormState.value = state.copy(
                    isSubmitting = false,
                    isSubmitted = true,
                    target = "",
                    description = "",
                    proofUri = null,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _reportFormState.value = state.copy(
                    isSubmitting = false,
                    errorMessage = e.localizedMessage ?: "Erreur lors de l'envoi"
                )
            }
        }
    }

    fun resetReportForm() {
        _reportFormState.value = ReportFormState()
    }
}

class MainViewModelFactory(private val repository: AmanCheckRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
