package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class LegalRepository(private val legalDao: LegalDao) {

    // --- Local Room DB Access ---
    val allConversations: Flow<List<ChatConversation>> = legalDao.getAllConversations()
    val allDocuments: Flow<List<SavedDocument>> = legalDao.getAllDocuments()

    suspend fun createConversation(title: String): Long = withContext(Dispatchers.IO) {
        legalDao.insertConversation(ChatConversation(title = title))
    }

    suspend fun deleteConversation(conversationId: Int) = withContext(Dispatchers.IO) {
        legalDao.deleteConversation(conversationId)
    }

    fun getMessagesForConversation(conversationId: Int): Flow<List<ChatMessage>> {
        return legalDao.getMessagesForConversation(conversationId)
    }

    suspend fun addMessage(conversationId: Int, sender: String, text: String): Long = withContext(Dispatchers.IO) {
        legalDao.insertMessage(ChatMessage(conversationId = conversationId, sender = sender, text = text))
    }

    suspend fun saveDocument(title: String, docType: String, content: String, riskScore: Int): Long = withContext(Dispatchers.IO) {
        legalDao.insertDocument(SavedDocument(title = title, docType = docType, content = content, riskScore = riskScore))
    }

    suspend fun deleteDocument(documentId: Int) = withContext(Dispatchers.IO) {
        legalDao.deleteDocument(documentId)
    }

    // --- Gemini API Native REST Client with OkHttp and org.json ---
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Executes a prompt against Gemini-3.5-flash with custom system instructions.
     */
    suspend fun callGemini(
        systemInstruction: String,
        userPrompt: String,
        historyMessages: List<ChatMessage> = emptyList(),
        jsonResponseOnly: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e("JuriMindAI", "Gemini API key is not configured.")
            return@withContext getMockResponseOrError(systemInstruction, userPrompt)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            // Construct Gemini Payload with JSON
            val requestJson = JSONObject()

            // 1. Contents Array
            val contentsArray = JSONArray()

            // Add history if present
            historyMessages.forEach { msg ->
                val contentObj = JSONObject()
                val partsArray = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", msg.text)
                partsArray.put(partObj)
                contentObj.put("parts", partsArray)
                contentObj.put("role", if (msg.sender == "user") "user" else "model")
                contentsArray.put(contentObj)
            }

            // Current user turn
            val currentTurn = JSONObject()
            val currentParts = JSONArray()
            val currentPart = JSONObject()
            currentPart.put("text", userPrompt)
            currentParts.put(currentPart)
            currentTurn.put("parts", currentParts)
            currentTurn.put("role", "user")
            contentsArray.put(currentTurn)

            requestJson.put("contents", contentsArray)

            // 2. System Instruction
            if (systemInstruction.isNotEmpty()) {
                val systemInstructionObj = JSONObject()
                val sysPartsArray = JSONArray()
                val sysPart = JSONObject()
                sysPart.put("text", systemInstruction)
                sysPartsArray.put(sysPart)
                systemInstructionObj.put("parts", sysPartsArray)
                requestJson.put("systemInstruction", systemInstructionObj)
            }

            // 3. Optional JSON Schema Response format
            val configObj = JSONObject()
            if (jsonResponseOnly) {
                val responseFormatObj = JSONObject()
                responseFormatObj.put("responseMimeType", "application/json")
                configObj.put("responseFormat", responseFormatObj)
            }
            configObj.put("temperature", 0.7)
            requestJson.put("generationConfig", configObj)

            val body = requestJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorMsg = response.body?.string() ?: "Internal Error"
                    Log.e("JuriMindAI", "API returned non-success code $response: $errorMsg")
                    return@withContext "API Error: ${response.code}\nFailed to get a real-time response. Please verify your internet connection or check your Gemini API key inside AI Studio Secrets Panel."
                }

                val responseBodyStr = response.body?.string() ?: return@withContext "No response from JuriMind Core Service."
                val responseJson = JSONObject(responseBodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    if (content != null) {
                        val parts = content.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return@withContext parts.getJSONObject(0).optString("text")
                        }
                    }
                }
                return@withContext "No text response could be gathered from the legal engine. Let me re-try shortly."
            }
        } catch (e: Exception) {
            Log.e("JuriMindAI", "Network Exception inside Repository", e)
            return@withContext "Network timeout or connection error.\nDetails: ${e.localizedMessage}\n\nFalling back to Secure Offline Mode. Here is a simulated response:\n\n" +
                    getFallbackOfflineResponse(systemInstruction, userPrompt)
        }
    }

    private fun getMockResponseOrError(systemInstruction: String, userPrompt: String): String {
        return "⚠️ **DEMO MODE ACTIVED**: No valid GEMINI_API_KEY loaded in AI Studio Secrets.\n\n" +
                getFallbackOfflineResponse(systemInstruction, userPrompt)
    }

    private fun getFallbackOfflineResponse(systemInstruction: String, userPrompt: String): String {
        val lowerPrompt = userPrompt.lowercase()
        return when {
            lowerPrompt.contains("nda") || lowerPrompt.contains("non-disclosure") -> {
                "### MUTUAL NON-DISCLOSURE AGREEMENT (NDA)\n\n" +
                        "This Mutual Non-Disclosure Agreement (\"Agreement\") is entered into by and between the active Parties to facilitate confidential business discussions.\n\n" +
                        "1. **Purpose**: The disclosing party intends to share intellectual property, trade secrets, software architecture, or algorithms solely to analyze partnership opportunities.\n\n" +
                        "2. **Confidential Information**: Includes any technical data, formula, process, design, prototype, code repositories, or customer details labeled Confidentially or reasonably understood as private.\n\n" +
                        "3. **Obligations**: The receiving party shall hold Confidential Information in strict trust, utilizing at minimum the same protective care they safeguard their proprietary information with (not less than a reasonable standard), and shall not disclose or commercially exploit said data.\n\n" +
                        "4. **Term of Protection**: 5 years from the execution of the notice, after which details entered into public domain automatically expire.\n\n" +
                        "**GOVERNING LAW**: Delaware, USA.\n\n" +
                        "[Drafted successfully in offline mock container mode.]"
            }
            lowerPrompt.contains("rental") || lowerPrompt.contains("tenancy") || lowerPrompt.contains("lease") -> {
                "### RESIDENTIAL LEASE AGREEMENT (DRAFT)\n\n" +
                        "**LANDLORD**: JuriMind Legal Assets Group LLC\n" +
                        "**TENANT**: Authorized Premium App User\n\n" +
                        "1. **PREMISES**: Standard apartment unit under lease terms.\n\n" +
                        "2. **RENTAL TERM**: 12 Months, starting next business cycle.\n\n" +
                        "3. **MONTHLY RENT**: $2,500 payable on the first day of each standard calendar month.\n\n" +
                        "4. **SECURITY DEPOSIT**: Equal to one month's rent ($2,500) held in escrow account against physical damage.\n\n" +
                        "5. **MAINTENANCE**: Tenant agrees to maintain neat, sanitary conditions and immediately report leaks or structural defects.\n\n" +
                        "[Offline template synthesis complete]"
            }
            lowerPrompt.contains("employment") || lowerPrompt.contains("hiring") -> {
                "### STANDARD EMPLOYMENT AGREEMENT PRINCIPLES\n\n" +
                        "**POSITION**: Legal AI Architect\n" +
                        "**DUTIES**: Overseeing legal text analysis pipelines, fine-tuning risk scores, and designing beautiful user-centered interfaces.\n\n" +
                        "1. **COMPENSATION**: $145,000 per annum, paid in monthly installments.\n\n" +
                        "2. **INTELLECTUAL PROPERTY**: Any software, tool, pattern, or document generated during the hours of employment resides exclusively as the property of the Company.\n\n" +
                        "3. **TERMINATION**: Employment is designated \"At-Will\", meaning both operator and provider may terminate operations with 14 days standard notifications.\n\n" +
                        "[Offline template synthesis complete]"
            }
            lowerPrompt.contains("notice") || lowerPrompt.contains("cease") -> {
                "### FORMAL CEASE AND DESIST LEGAL NOTICE\n\n" +
                        "**TO**: Infringing Party Org\n" +
                        "**RE**: Unauthorized Intellectual Property Exploitation\n\n" +
                        "This notice acts as formal notification that you are operating in direct violation of our registered trademark and intellectual properties.\n\n" +
                        "We demand you immediately cease any unauthorized display or modification of JuriMind AI software assets, logos, and layouts.\n\n" +
                        "Failure to respond or cease activities within 7 business days will result in immediate legal retaliation before Delaware courts without further communications.\n\n" +
                        "Sincerest Regards,\n" +
                        "Legal Council for JuriMind Users"
            }
            else -> {
                "### JuriMind AI Assistant Response\n\n" +
                        "Under secure offline model, here is some general advice:\n\n" +
                        "1. **Document Analysis**: JuriMind can evaluate risk scores using heuristic rules. Ensure key parameters like liability limits, governing jurisdiction, and liquidated damages are tightly bounded.\n\n" +
                        "2. **Risk Indexing**: Generally, 95% of software agreements have unvetted indemnification clauses. Focus on restricting indemnity bounds only to proven direct intellectual property infringements.\n\n" +
                        "Please verify your Gemini API Key in the AI Studio Settings / Secrets Panel to chat dynamically on any custom question!"
            }
        }
    }
}
