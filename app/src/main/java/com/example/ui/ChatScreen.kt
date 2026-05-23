package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatConversation
import com.example.data.ChatMessage
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(viewModel: JuriMindViewModel) {
    val conversations by viewModel.conversations.collectAsState()
    val activeMessages by viewModel.activeMessages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val activeId by viewModel.activeConversationId.collectAsState()
    val speakActive by viewModel.chatSpeechState.collectAsState()

    val chatListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var keyboardInputStr by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Help auto Scroll to end of list when a message is added
    LaunchedEffect(activeMessages.size, isTyping) {
        if (activeMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(activeMessages.size - 1)
        }
    }

    val suggestPrompts = listOf(
        "Summarize IP clauses in standard NDAs.",
        "What governs Delaware Section 251 merger rules?",
        "Draft a short cure message for late tenancy payout.",
        "Highlight risks of 'termination for convenience' clause."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- 1. TOP BAR: Conversations Row ---
        Column(modifier = Modifier.padding(top = 10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GradientText(
                    text = "NEURAL CONSULTATION",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                
                IconButton(
                    onClick = {
                        val title = "Case #${conversations.size + 1}: General Inquiry"
                        viewModel.startNewConversation(title)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.AddComment,
                        contentDescription = "New chat",
                        tint = CyberCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Topics List Horizontal Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    count = conversations.size,
                    key = { idx -> conversations[idx].id }
                ) { idx ->
                    val conv = conversations[idx]
                    val isSelected = conv.id == activeId
                    val borderColor = if (isSelected) CyberCyan else GlassWhiteBorder
                    val backgroundBrush = if (isSelected) {
                        Brush.linearGradient(listOf(CyberCyan.copy(alpha = 0.25f), CosmicCard))
                    } else {
                        Brush.linearGradient(listOf(CosmicSurface, CosmicSurface))
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundBrush)
                            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                            .clickable { viewModel.selectConversation(conv.id) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = conv.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) CyberCyan else TextSecondary
                            )
                            if (isSelected && conversations.size > 1) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete",
                                    tint = CyberRed.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clickable { viewModel.deleteConversation(conv.id) }
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 2. CHAT MESSAGES BODY ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CosmicSurface)
                .border(1.dp, GlassWhite, RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            if (activeMessages.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Secure AI Shield",
                        tint = CyberCyan.copy(alpha = 0.15f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "JuriMind AI Secured Core Ready",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Encrypted conversation pipeline. State-level corporate analysis.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    state = chatListState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        count = activeMessages.size,
                        key = { idx -> activeMessages[idx].id }
                    ) { idx ->
                        val msg = activeMessages[idx]
                        val isUser = msg.sender == "user"

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            if (!isUser) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(CyberMagenta.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Memory,
                                        contentDescription = "AI Icon",
                                        tint = CyberMagenta,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            // Speech Bubble
                            Box(
                                modifier = Modifier
                                    .widthIn(max = 280.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 14.dp,
                                            topEnd = 14.dp,
                                            bottomStart = if (isUser) 14.dp else 2.dp,
                                            bottomEnd = if (isUser) 2.dp else 14.dp
                                        )
                                    )
                                    .background(
                                        if (isUser) CyberCyan.copy(alpha = 0.15f)
                                        else CosmicCard
                                    )
                                    .border(
                                        1.dp,
                                        if (isUser) CyberCyan.copy(alpha = 0.3f) else GlassWhiteRuleColor,
                                        RoundedCornerShape(
                                            topStart = 14.dp,
                                            topEnd = 14.dp,
                                            bottomStart = if (isUser) 14.dp else 2.dp,
                                            bottomEnd = if (isUser) 2.dp else 14.dp
                                        )
                                    )
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = msg.text,
                                        fontSize = 13.sp,
                                        color = if (isUser) TextPrimary else TextPrimary,
                                        lineHeight = 18.sp
                                    )
                                    
                                    // Custom visual feature: allow re-speaking message when clicked
                                    if (!isUser) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier
                                                .clickable { viewModel.speak(msg.text.replace(Regex("[*#`_]"), "")) }
                                                .padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.VolumeUp,
                                                contentDescription = "Speak Text",
                                                tint = CyberMagenta,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "SPEAK CLARIFICATION",
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = CyberMagenta,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }

                            if (isUser) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(CyberCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "User Icon",
                                        tint = CyberCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Loading / typing indicator
                    if (isTyping) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(CyberMagenta.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Memory,
                                        tint = CyberMagenta,
                                        contentDescription = "AI",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomEnd = 14.dp))
                                        .background(CosmicCard)
                                        .border(1.dp, GlassWhite, RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomEnd = 14.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            strokeWidth = 2.dp,
                                            color = CyberMagenta
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Scanning neural legal matrix...",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = CyberMagenta
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 3. BOTTOM: Suggesters & Keyboard input ---
        Column(modifier = Modifier.padding(bottom = 100.dp)) {
            // Quick suggested prompt rows
            if (activeMessages.size <= 1) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(suggestPrompts.size) { index ->
                        val text = suggestPrompts[index]
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GlassWhite)
                                .border(1.dp, GlassWhiteBorder, RoundedCornerShape(8.dp))
                                .clickable {
                                    keyboardInputStr = text
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = text,
                                fontSize = 10.sp,
                                color = TextPrimary,
                                maxLines = 1,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }
                }
            }

            // Text input container
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Speech TTS toggle modifier icon
                IconButton(
                    onClick = {
                        if (speakActive) viewModel.stopSpeaking() else viewModel.speak("Voice assistance audio feedback activated.")
                    },
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .background(
                            if (speakActive) CyberMagenta.copy(alpha = 0.2f) else TransparentColor,
                            CircleShape
                        )
                        .border(
                            1.dp,
                            if (speakActive) CyberMagenta else GlassWhiteBorder,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (speakActive) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Audio feedback",
                        tint = if (speakActive) CyberMagenta else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                TextField(
                    value = keyboardInputStr,
                    onValueChange = { keyboardInputStr = it },
                    placeholder = { Text("Query AI legal analysis...", fontSize = 13.sp, color = TextSecondary) },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (keyboardInputStr.isNotBlank()) {
                                viewModel.sendChatMessage(keyboardInputStr)
                                keyboardInputStr = ""
                                keyboardController?.hide()
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CosmicCard,
                        unfocusedContainerColor = CosmicCard,
                        focusedBorderColor = CyberCyan,
                        unfocusedBorderColor = GlassWhiteBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, GlassWhite, RoundedCornerShape(12.dp)),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (keyboardInputStr.isNotBlank()) {
                            viewModel.sendChatMessage(keyboardInputStr)
                            keyboardInputStr = ""
                            keyboardController?.hide()
                        }
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(CyberCyan, CyberMagenta)
                            )
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = CosmicDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// Inline constants to ensure absolute compilation protection
val GlassWhiteRuleColor = Color(0x22FFFFFF)
val TransparentColor = Color(0x00000000)
