package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.RoyalBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: JuriMindViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.activeMessages.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val activeId by viewModel.activeConversationId.collectAsState()
    var inputMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Autonomous Strategy Room",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            if (activeId != null) "Session Connected: #$activeId" else "Disconnect Mode",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberBlue
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.minimumInteractiveComponentSize().testTag("chat_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Return back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (isSpeaking) {
                        IconButton(
                            onClick = { viewModel.stopSpeaking() },
                            modifier = Modifier.minimumInteractiveComponentSize().testTag("stop_speech_action")
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Mute audio readout", tint = Color.Red)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (activeId == null) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Select or create a strategy session from the dashboard.", color = Color.Gray)
                }
            } else {
                // Conversational Streams
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                    
                    items(messages) { msg ->
                        val isUser = msg.sender == "user"
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                        ) {
                            val rowArrangement: Arrangement.Horizontal = if (isUser) Arrangement.End else Arrangement.Start
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = rowArrangement,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (!isUser) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = CyberBlue.copy(alpha = 0.1f),
                                        modifier = Modifier.padding(end = 8.dp).size(30.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("AI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CyberBlue)
                                        }
                                    }
                                }
                                Text(
                                    if (isUser) "CONSULTANT" else "JURIMIND AGENT",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUser) RoyalBlue else CyberBlue
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isUser) 16.dp else 2.dp,
                                            bottomEnd = if (isUser) 2.dp else 16.dp
                                        )
                                    )
                                    .background(if (isUser) RoyalBlue else DarkSurface)
                                    .padding(14.dp)
                                    .widthIn(max = 280.dp)
                            ) {
                                Column {
                                    Text(
                                        msg.content,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        lineHeight = 20.sp
                                    )
                                    
                                    if (!isUser) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        IconButton(
                                            onClick = { viewModel.speakText(msg.content) },
                                            modifier = Modifier
                                                .size(36.dp)
                                                .minimumInteractiveComponentSize()
                                                .testTag("tts_speak_msg_${msg.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Read out loud",
                                                tint = CyberBlue,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(10.dp)) }
                }

                // Cyber Message Input Area
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputMessage,
                            onValueChange = { inputMessage = it },
                            placeholder = { Text("Consult JuriMind models...", color = Color.Gray) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("chat_input_text_field"),
                            maxLines = 4
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                if (inputMessage.isNotBlank()) {
                                    viewModel.addMessage(inputMessage)
                                    inputMessage = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RoyalBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(24.dp),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .size(48.dp)
                                .minimumInteractiveComponentSize()
                                .testTag("send_chat_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Submit analysis request",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
