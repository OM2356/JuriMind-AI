package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LegalDao {
    @Query("SELECT * FROM conversations ORDER BY createdAt DESC")
    fun getAllConversations(): Flow<List<ChatConversation>>

    @Query("SELECT * FROM messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun getMessagesForConversation(convId: Int): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ChatConversation): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    @Delete
    suspend fun deleteConversation(conversation: ChatConversation)

    @Query("DELETE FROM messages WHERE conversationId = :convId")
    suspend fun deleteMessagesForConversation(convId: Int)

    @Query("SELECT * FROM clauses")
    fun getAllClauses(): Flow<List<LegalClause>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClause(clause: LegalClause)
}
