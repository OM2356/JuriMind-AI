package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_conversations")
data class ChatConversation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val conversationId: Int,
    val sender: String, // "user" or "ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_documents")
data class SavedDocument(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val docType: String, // "rental", "nda", "employment", "notice", "analysis"
    val content: String, // generated draft or report markdown
    val riskScore: Int = 0, // 0 for safe, 100 for maximum risk
    val timestamp: Long = System.currentTimeMillis()
)
