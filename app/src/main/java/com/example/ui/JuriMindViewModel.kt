package com.example.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class JuriMindViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val db = AppDatabase.getDatabase(application)
    private val repository = LegalRepository(db.legalDao())

    // --- State Streams ---
    val conversations: StateFlow<List<ChatConversation>> = repository.allConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedDocuments: StateFlow<List<SavedDocument>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeConversationId = MutableStateFlow<Int?>(null)
    val activeConversationId: StateFlow<Int?> = _activeConversationId.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    // Observe active messages reactively
    val activeMessages: StateFlow<List<ChatMessage>> = _activeConversationId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else repository.getMessagesForConversation(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Contract Analyzer UI States ---
    private val _analyzerInputText = MutableStateFlow("")
    val analyzerInputText = _analyzerInputText.asStateFlow()

    private val _analyzerResult = MutableStateFlow<String?>(null)
    val analyzerResult = _analyzerResult.asStateFlow()

    private val _analyzedRiskScore = MutableStateFlow(0)
    val analyzedRiskScore = _analyzedRiskScore.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing = _isAnalyzing.asStateFlow()

    private val _detectedRiskyClauses = MutableStateFlow<List<RiskyClause>>(emptyList())
    val detectedRiskyClauses = _detectedRiskyClauses.asStateFlow()

    // --- Legal Notice Generator UI States ---
    private val _generatorDocType = MutableStateFlow("nda") // nda, rental, employment, notice
    val generatorDocType = _generatorDocType.asStateFlow()

    private val _generatorTitle = MutableStateFlow("Mutual NDA - Confidential")
    val generatorTitle = _generatorTitle.asStateFlow()

    private val _generatorParties = MutableStateFlow("JuriMind Tech & Delta Corp")
    val generatorParties = _generatorParties.asStateFlow()

    private val _generatorCustomDetails = MutableStateFlow("Include strict 3-year term, IP assignment, and Delaware jurisdiction.")
    val generatorCustomDetails = _generatorCustomDetails.asStateFlow()

    private val _generatedDraftText = MutableStateFlow<String?>(null)
    val generatedDraftText = _generatedDraftText.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    // --- SaaS Settings & Simulation ---
    private val _userRole = MutableStateFlow("Admin") // "User", "Admin", "Enterprise VIP"
    val userRole = _userRole.asStateFlow()

    private val _userPlan = MutableStateFlow("Enterprise Professional") 
    val userPlan = _userPlan.asStateFlow()

    private val _apiQuotaUsed = MutableStateFlow(328)
    val apiQuotaUsed = _apiQuotaUsed.asStateFlow()

    private val _apiQuotaLimit = MutableStateFlow(1000)
    val apiQuotaLimit = _apiQuotaLimit.asStateFlow()

    private val _chatSpeechState = MutableStateFlow(false) // Whether Voice assist is speaking output
    val chatSpeechState = _chatSpeechState.asStateFlow()

    // --- Android Text To Speech Engine ---
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    init {
        tts = TextToSpeech(application, this)
        
        // Setup initial default conversation if database is empty
        viewModelScope.launch {
            conversations.first { true } // Wait for flow
            if (conversations.value.isEmpty()) {
                val demoId = repository.createConversation("Initial Case Strategy Inquiry")
                repository.addMessage(demoId.toInt(), "ai", "Greetings. I am JuriMind AI, your dedicated futuristic legal analyst. How may I assist in contract optimization, agreement drafting, or risk assessment today?")
                _activeConversationId.value = demoId.toInt()
            } else if (_activeConversationId.value == null) {
                _activeConversationId.value = conversations.value.first().id
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isTtsInitialized = true
            }
        }
    }

    fun speak(text: String) {
        if (isTtsInitialized && tts != null) {
            _chatSpeechState.value = true
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "JuriSpeechID")
        }
    }

    fun stopSpeaking() {
        if (isTtsInitialized && tts != null) {
            tts?.stop()
            _chatSpeechState.value = false
        }
    }

    override fun onCleared() {
        tts?.stop()
        tts?.shutdown()
        super.onCleared()
    }

    // --- Chat Actions ---
    fun selectConversation(id: Int) {
        _activeConversationId.value = id
        stopSpeaking()
    }

    fun startNewConversation(title: String) {
        viewModelScope.launch {
            val freshId = repository.createConversation(title)
            _activeConversationId.value = freshId.toInt()
            repository.addMessage(freshId.toInt(), "ai", "Acknowledged. Initiating a new legal dossier: \"$title\". Ask any inquiry, upload sample clause text, or query agreement structures.")
        }
    }

    fun deleteConversation(id: Int) {
        viewModelScope.launch {
            repository.deleteConversation(id)
            if (_activeConversationId.value == id) {
                _activeConversationId.value = conversations.value.firstOrNull()?.id
            }
        }
    }

    fun sendChatMessage(text: String) {
        val convId = _activeConversationId.value ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            // 1. Save user msg
            repository.addMessage(convId, "user", text)
            _isTyping.value = true
            _apiQuotaUsed.value += 1

            // Gather active messages for history context
            val messagesList = activeMessages.value.takeLast(10) // feed up to 10 context messages

            // Construct System Instruction ensuring solid legal persona or response styling
            val sysInstruction = "You are JuriMind AI, a world-class premier automated legal analytics engineer. " +
                    "Analyze queries professionally. Ground opinions in international business statutes, Delaware corporate rules, and standard common law parameters. " +
                    "Use precise, elite, and highly advisory language. Always organize descriptions beautifully."

            val aiResponse = repository.callGemini(sysInstruction, text, messagesList)
            _isTyping.value = false
            repository.addMessage(convId, "ai", aiResponse)

            // Auto voice-speaking of short responses or summaries
            if (aiResponse.length < 500) {
                // Remove Markdown markers for cleaner speech synthesis
                val cleanSpeech = aiResponse.replace(Regex("[*#`_]"), "")
                speak(cleanSpeech)
            }
        }
    }

    // --- Contract Analyzer Actions ---
    fun setAnalyzerInput(text: String) {
        _analyzerInputText.value = text
    }

    fun runContractAnalyzer(text: String) {
        if (text.isBlank()) return
        _analyzerInputText.value = text
        _isAnalyzing.value = true

        viewModelScope.launch {
            _apiQuotaUsed.value += 2
            val prompt = "Perform a thorough high-fidelity risk analysis of the following contract text. " +
                    "Detect risky clauses, highlight tricky terms, explain legal meaning, suggest safer mitigation sentences. " +
                    "Your response MUST contain two parts: " +
                    "1. A structured JSON array under label '!!!CLAUSES!!!' where each clause item is a JSON object modeling: " +
                    "{\"clauseName\": \"...\", \"threatLevel\": \"High\" or \"Medium\" or \"Low\", \"explanation\": \"...\", \"replacement\": \"...\"}. " +
                    "2. A full comprehensive report detailing liability, indemnity, termination, governing law risks.\n\n" +
                    "Contract text:\n$text"

            val sysInstruction = "You are JuriMind AI Contract risk assessor. Identify tricky indemnification, unilateral termination, unlimited consequential damages, and non-compete locks."
            val fullResult = repository.callGemini(sysInstruction, prompt)

            _isAnalyzing.value = false
            _analyzerResult.value = fullResult

            // Parse visual score model
            var calculatedScore = 15
            val extractedClauses = mutableListOf<RiskyClause>()

            try {
                if (fullResult.contains("!!!CLAUSES!!!")) {
                    val splitIdx = fullResult.indexOf("!!!CLAUSES!!!")
                    val rawJson = fullResult.substring(splitIdx + "!!!CLAUSES!!!".length).trim()
                    
                    // Attempt clean json parsing of array boundaries
                    val arrayStart = rawJson.indexOf('[')
                    val arrayEnd = rawJson.lastIndexOf(']')
                    if (arrayStart != -1 && arrayEnd != -1) {
                        val jsonArrStr = rawJson.substring(arrayStart, arrayEnd + 1)
                        val arr = JSONArray(jsonArrStr)
                        for (i in 0 until arr.length()) {
                            val obj = arr.getJSONObject(i)
                            val name = obj.getString("clauseName")
                            val level = obj.getString("threatLevel")
                            val explanation = obj.getString("explanation")
                            val replacement = obj.getString("replacement")
                            
                            val clauseWeight = when (level.lowercase()) {
                                "high" -> 35
                                "medium" -> 15
                                else -> 5
                            }
                            calculatedScore += clauseWeight
                            
                            extractedClauses.add(
                                RiskyClause(
                                    title = name,
                                    riskLevel = level,
                                    explanation = explanation,
                                    suggestedAlternative = replacement
                                )
                            )
                        }
                    }
                }
            } catch (ne: Exception) {
                Log.e("JuriMindAI", "Clause list parsing failed. Resorting to smart defaults", ne)
            }

            // Fallback heuristics if API failed to construct valid JSON but returned rich text
            if (extractedClauses.isEmpty()) {
                if (text.contains("terminate", ignoreCase = true)) {
                    extractedClauses.add(RiskyClause("Termination for Convenience", "Medium", "Allows abrupt contract termination without penalty, inducing unstable business forecasts.", "Require 30 days mandatory default notice and payment for accrued works."))
                    calculatedScore += 20
                }
                if (text.contains("indemn", ignoreCase = true)) {
                    extractedClauses.add(RiskyClause("Unbounded Indemnification", "High", "Holds user fully responsible for unlimited consequential legal costs or third party claims.", "Cap total indemnity at direct damages or fees paid in the last 12 months."))
                    calculatedScore += 45
                }
                if (text.contains("delaware", ignoreCase = true).not() && (text.contains("law", ignoreCase = true) || text.contains("jurisdiction", ignoreCase = true))) {
                    extractedClauses.add(RiskyClause("Vague Governing Jurisdiction", "Low", "Fails to lock in predictable corporate laws, increasing dispute costs.", "Specify Delaware state law context as exclusive court jurisdiction."))
                    calculatedScore += 10
                }
            }

            val finalScore = calculatedScore.coerceIn(10, 95)
            _analyzedRiskScore.value = finalScore
            _detectedRiskyClauses.value = extractedClauses

            // Store analyzed report permanently in local database history
            val previewTxt = if (text.length > 30) text.substring(0, 30) + "..." else text
            repository.saveDocument(
                title = "Contract Analysis: $previewTxt",
                docType = "analysis",
                content = fullResult,
                riskScore = finalScore
            )
        }
    }

    fun clearAnalyzer() {
        _analyzerResult.value = null
        _analyzerInputText.value = ""
        _analyzedRiskScore.value = 0
        _detectedRiskyClauses.value = emptyList()
    }

    // --- Legal Document Generator Actions ---
    fun updateGeneratorDocType(type: String) {
        _generatorDocType.value = type
        _generatorTitle.value = when (type) {
            "nda" -> "Mutual Non-Disclosure Agreement"
            "rental" -> "Residential Lease Contract"
            "employment" -> "Professional Employment Offer"
            else -> "Formal Cease & Desist Demand"
        }
    }

    fun updateGeneratorTitle(title: String) {
        _generatorTitle.value = title
    }

    fun updateGeneratorParties(parties: String) {
        _generatorParties.value = parties
    }

    fun updateGeneratorDetails(details: String) {
        _generatorCustomDetails.value = details
    }

    fun generateLegalDocument() {
        _isGenerating.value = true
        viewModelScope.launch {
            _apiQuotaUsed.value += 3
            val docTypeStr = _generatorDocType.value
            val titleStr = _generatorTitle.value
            val partiesStr = _generatorParties.value
            val detailsStr = _generatorCustomDetails.value

            val prompt = "Draft an elite, legally robust, ready-to-use contract / legal tool. " +
                    "Document Type: $docTypeStr\n" +
                    "Display Title: $titleStr\n" +
                    "Parties: $partiesStr\n" +
                    "Additional Custom requirements: $detailsStr\n\n" +
                    "Synthesize the document text. Present with standard numbered Articles, precise definitions, standard statutory bounds (e.g., Delaware corporate code, common law tenancy rules), clear witness execution blocks."

            val sysInstruction = "You are JuriMind AI's Master Agreement Synthesis algorithm. Generate completely written contracts starting with realistic legal preambles."
            val generatedDraft = repository.callGemini(sysInstruction, prompt)

            _isGenerating.value = false
            _generatedDraftText.value = generatedDraft

            // Store inside Local Database so it exhibits cleanly on SaaS dashboard history
            repository.saveDocument(
                title = titleStr,
                docType = docTypeStr,
                content = generatedDraft,
                riskScore = 0
            )
        }
    }

    fun clearGenerator() {
        _generatedDraftText.value = null
    }

    // --- SaaS Settings Controls ---
    fun updateUserRole(role: String) {
        _userRole.value = role
        _userPlan.value = when (role) {
            "Admin" -> "Global System Controller"
            "Enterprise VIP" -> "Enterprise VIP Unlimited"
            else -> "Premium Lite Sandbox"
        }
    }

    fun deleteDocument(id: Int) {
        viewModelScope.launch {
            repository.deleteDocument(id)
        }
    }
}

// Clause item structure representation for contract analyzer
data class RiskyClause(
    val title: String,
    val riskLevel: String, // "High", "Medium", "Low"
    val explanation: String,
    val suggestedAlternative: String
)
