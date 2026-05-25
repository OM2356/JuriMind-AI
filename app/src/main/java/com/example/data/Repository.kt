package com.example.data

import kotlinx.coroutines.flow.Flow

class LegalRepository(private val legalDao: LegalDao) {
    val allConversations: Flow<List<ChatConversation>> = legalDao.getAllConversations()
    val allClauses: Flow<List<LegalClause>> = legalDao.getAllClauses()

    fun getMessages(conversationId: Int): Flow<List<ChatMessage>> {
        return legalDao.getMessagesForConversation(conversationId)
    }

    suspend fun createConversation(title: String): Long {
        return legalDao.insertConversation(ChatConversation(title = title))
    }

    suspend fun addMessage(conversationId: Int, sender: String, content: String): Long {
        return legalDao.insertMessage(
            ChatMessage(
                conversationId = conversationId,
                sender = sender,
                content = content
            )
        )
    }

    suspend fun deleteConversation(id: Int) {
        legalDao.deleteConversation(ChatConversation(id = id, title = ""))
    }

    suspend fun insertClause(clause: LegalClause) {
        legalDao.insertClause(clause)
    }
}
