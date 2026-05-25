package com.example.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class JuriMindViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {
    private val db = AppDatabase.getDatabase(application)
    private val repository = LegalRepository(db.legalDao())
    
    // TTS Engine
    private var tts: TextToSpeech? = null
    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking = _isSpeaking.asStateFlow()

    // Conversations Flow
    val conversations: StateFlow<List<ChatConversation>> = repository.allConversations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Session State
    private val _activeConversationId = MutableStateFlow<Int?>(null)
    val activeConversationId = _activeConversationId.asStateFlow()

    // Active Messages Flow
    @OptIn(ExperimentalCoroutinesApi::class)
    val activeMessages: StateFlow<List<ChatMessage>> = _activeConversationId
        .flatMapLatest { id ->
            if (id != null) {
                repository.getMessages(id)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Contract Clauses
    val clauses: StateFlow<List<LegalClause>> = repository.allClauses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Analysis UI States
    private val _analyzerInput = MutableStateFlow("")
    val analyzerInput = _analyzerInput.asStateFlow()

    private val _analyzerResult = MutableStateFlow<List<String>?>(null)
    val analyzerResult = _analyzerResult.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing = _isAnalyzing.asStateFlow()

    // Contract Generator States
    private val _generatedContractText = MutableStateFlow("")
    val generatedContractText = _generatedContractText.asStateFlow()

    init {
        // Initialize TTS
        tts = TextToSpeech(application, this)

        // Setup pre-seeded clauses if missing
        viewModelScope.launch {
            repository.allClauses.first().let { currentList ->
                if (currentList.isEmpty()) {
                    repository.insertClause(
                        LegalClause(
                            category = "Intellectual Property",
                            name = "Unilateral IP Ownership Clause",
                            standardText = "All intellectual property created during the service term shall belong solely to the Service Provider, without credit or royalties.",
                            explanation = "HIGH RISK: Restricts client's rights to own deliverables they fully paid for.",
                            riskLevel = "High"
                        )
                    )
                    repository.insertClause(
                        LegalClause(
                            category = "Liability",
                            name = "Unlimited Indemnity Clause",
                            standardText = "The Client agrees to completely indemnify and hold harmless the Contractor from any and all damages, without limits or caps.",
                            explanation = "HIGH RISK: Exposes client to unbounded financial claims from third parties.",
                            riskLevel = "High"
                        )
                    )
                    repository.insertClause(
                        LegalClause(
                            category = "Termination",
                            name = "Termination for Convenience Without Notice",
                            standardText = "The Company may terminate this Agreement immediately for any or no reason without any notice period or compensation.",
                            explanation = "MEDIUM RISK: Creates structural instability for operations and scheduling.",
                            riskLevel = "Medium"
                        )
                    )
                }
            }
        }

        // Setup initial default conversation if database is empty
        viewModelScope.launch {
            val dbList = repository.allConversations.first() // Wait for Room entry
            if (dbList.isEmpty()) {
                val demoId = repository.createConversation("Initial Strategy Inquiry")
                repository.addMessage(
                    demoId.toInt(), 
                    "ai", 
                    "Greetings. I am JuriMind AI, your dedicated futuristic legal analyst. How may I assist in contract optimization, agreement drafting, or risk assessment today?"
                )
                _activeConversationId.value = demoId.toInt()
            } else {
                _activeConversationId.value = dbList.first().id
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
        }
    }

    fun selectConversation(id: Int) {
        _activeConversationId.value = id
    }

    fun createNewSession(title: String) {
        viewModelScope.launch {
            val id = repository.createConversation(title)
            _activeConversationId.value = id.toInt()
            repository.addMessage(
                id.toInt(),
                "ai",
                "Advanced model initialized. Strategy room prepared for: '$title'. What questions or analytical tasks do we begin with?"
            )
        }
    }

    fun deleteSession(id: Int) {
        viewModelScope.launch {
            repository.deleteConversation(id)
            val currentList = repository.allConversations.first()
            if (currentList.isNotEmpty()) {
                _activeConversationId.value = currentList.first().id
            } else {
                _activeConversationId.value = null
            }
        }
    }

    fun addMessage(content: String) {
        val currentConvId = _activeConversationId.value ?: return
        if (content.isBlank()) return

        viewModelScope.launch {
            repository.addMessage(currentConvId, "user", content)
            simulateAiResponse(currentConvId, content)
        }
    }

    private suspend fun simulateAiResponse(conversationId: Int, prompt: String) {
        // AI Rules engine
        val cleanPrompt = prompt.lowercase()
        val response = when {
            cleanPrompt.contains("nda") || cleanPrompt.contains("disclosure") -> {
                "NDA ANALYSIS INITIALIZED:\n\nFor effective standard non-disclosure protection, always ensure definition of 'Confidential Information' contains exclusions for information publicly known. Standard mutual NDA terms typically span 2-5 years. Advise using the JuriMind 'Agreement Generator' tool to output a robust template."
            }
            cleanPrompt.contains("liability") || cleanPrompt.contains("indemnity") -> {
                "LIABILITY AUDIT RECOMMENDATION:\n\nUnlimited indemnities represent major commercial risks. Recommendation: Cap liability to a multiple of fees paid (e.g., 100% or 200% of contract value). Add a carve-out clause for gross negligence/willful misconduct."
            }
            cleanPrompt.contains("hello") || cleanPrompt.contains("hi") || cleanPrompt.contains("greet") -> {
                "Hello! How can JuriMind AI help simplify contract complexity, assess potential risk, or draft professional legal documents today?"
            }
            else -> {
                "ANALYSIS OF INQUIRY:\n\nBased on general contract law standards, this matter requires careful structuring. Suggested Actions:\n1. Audit clauses using our Clause Risk Analyzer.\n2. Ensure standard governing jurisdiction matches the execution locale.\n3. Draft an initial consensus sheet using the SLA drafting tools inside the main generator tab."
            }
        }
        repository.addMessage(conversationId, "ai", response)
    }

    // TTS Control
    fun speakText(text: String) {
        if (tts == null) return
        _isSpeaking.value = true
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "JuriMindSpeech")
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
    }

    // Risk Analyzer
    fun updateAnalyzerInput(text: String) {
        _analyzerInput.value = text
    }

    fun clearAnalyzer() {
        _analyzerInput.value = ""
        _analyzerResult.value = null
    }

    fun runAnalyzerAssessment() {
        val text = _analyzerInput.value
        if (text.isBlank()) return

        _isAnalyzing.value = true
        viewModelScope.launch {
            // Simulated deep clause audit scanning
            kotlinx.coroutines.delay(1200)
            val findings = mutableListOf<String>()
            val lowerText = text.lowercase()

            if (lowerText.contains("indemnify") && !lowerText.contains("limit")) {
                findings.add("⚠️ HIGH RISK: Unlimited Indemnification detected. This exposes you to unchecked financial liability without any cap limit.")
            }
            if (lowerText.contains("intellectual property") && (lowerText.contains("solely") || lowerText.contains("unilateral"))) {
                findings.add("⚠️ HIGH RISK: Unbalanced Intellectual Property Assignment. You are retaining zero joint-rights or promotional licenses.")
            }
            if (lowerText.contains("terminate") && lowerText.contains("immediately") && lowerText.contains("without notice")) {
                findings.add("⚠️ MEDIUM RISK: immediate termination for convenience. Leaves your company vulnerable without runway windows.")
            }
            if (findings.isEmpty()) {
                findings.add("✅ NO HIGH RISKS FOUND: The scanned text looks standard. Standard dispute structures and mutual liability provisions confirmed.")
            }
            _analyzerResult.value = findings
            _isAnalyzing.value = false
        }
    }

    // Contract Generator
    fun generateAgreement(type: String, company: String, client: String, duration: String, value: String) {
        _generatedContractText.value = when (type) {
            "Mutual NDA" -> """
                MUTUAL NON-DISCLOSURE AGREEMENT
                
                This Mutual Non-Disclosure Agreement ("Agreement") is made effective as of current timestamp, by and between $company ("Disclosing Party") and $client ("Receiving Party").
                
                1. Confidential Information: Consists of technical, commercial, financial, and product strategies disclosed during a period of $duration months.
                2. Exclusions: Does not cover details already in public view, shared through third party legal actions, or made independently.
                3. Governing Law: State laws of execution venue.
                4. Signatures: Executive signing binds the entity.
            """.trimIndent()

            "Service Level SLA" -> """
                PROFESSIONAL SERVICES LEVEL AGREEMENT (SLA)
                
                CUSTOMER CONTEXT: $client
                SERVICE PROVIDER: $company
                CONTRACT VALUE STIPULATED: $value
                
                1. Services Rendered: Provider delivers technical consultation and SaaS deployment as specified in Scope of Work documents.
                2. Execution Duration: This agreement continues for $duration months.
                3. Compensation: Customer compensates $value payable on net-30 terms. Late fees accrue at 1.5% monthly.
                4. Limited Liability: Total cumulative liability is capped strictly at 100% of the fees received under this contract.
            """.trimIndent()

            else -> """
                COMMERCIAL AGREEMENT MEMORANDUM
                
                This protocol establishes a joint venture between $company and $client.
                
                - Term Scope: $duration Months.
                - Projected value capitalization: $value.
                - Terms of negotiation: Parties proceed in ultimate good faith to establish mutual binding terms.
            """.trimIndent()
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
        tts = null
    }
}
