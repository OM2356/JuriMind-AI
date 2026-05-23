package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LegalDao {

    // --- Conversations ---
    @Query("SELECT * FROM chat_conversations ORDER BY timestamp DESC")
    fun getAllConversations(): Flow<List<ChatConversation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ChatConversation): Long

    @Query("DELETE FROM chat_conversations WHERE id = :id")
    suspend fun deleteConversation(id: Int)

    // --- Messages ---
    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessagesForConversation(conversationId: Int): Flow<List<ChatMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage): Long

    // --- Documents & Notices & Contract Analyses ---
    @Query("SELECT * FROM saved_documents ORDER BY timestamp DESC")
    fun getAllDocuments(): Flow<List<SavedDocument>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: SavedDocument): Long

    @Query("DELETE FROM saved_documents WHERE id = :id")
    suspend fun deleteDocument(id: Int)
}
