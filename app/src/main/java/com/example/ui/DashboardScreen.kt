package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatConversation
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.RoyalBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: JuriMindViewModel,
    onNavigateToChat: () -> Unit,
    onNavigateToAnalyzer: () -> Unit,
    onNavigateToGenerator: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val conversations by viewModel.conversations.collectAsState()
    val activeId by viewModel.activeConversationId.collectAsState()
    var showNewSessionDialog by remember { mutableStateOf(false) }
    var newSessionTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "JuriMind icon",
                            tint = CyberBlue,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        GradientText("JuriMind AI")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToAdmin,
                        modifier = Modifier.testTag("admin_nav_button")
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings Icon", tint = Color.LightGray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewSessionDialog = true },
                containerColor = RoyalBlue,
                contentColor = Color.White,
                modifier = Modifier.testTag("create_new_session_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Strategy Session")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // High-Tech Hero Banner
            item {
                CyberCard {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "L.A.W. CORE INITIALIZED",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            GlowBadge(text = "Online", glowColor = NeonGreen)
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            "Autonomous Compliance & Risk Management",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Text(
                            "Futuristic SaaS assistant powered by local relational models to audit indemnification risks and autogenerate secure commercial SLAs.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray
                        )
                    }
                }
            }

            // Interactive Navigation Quick-Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp)
                            .clickable(onClick = onNavigateToChat)
                            .testTag("link_to_chat"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Chat logo", tint = CyberBlue)
                            Text("Legal AI Chat", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp)
                            .clickable(onClick = onNavigateToAnalyzer)
                            .testTag("link_to_analyzer"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(Icons.Default.Star, contentDescription = "Analyzer Logo", tint = NeonGreen)
                            Text("Risk Guard", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(110.dp)
                            .clickable(onClick = onNavigateToGenerator)
                            .testTag("link_to_generator"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Generator logo", tint = RoyalBlue)
                            Text("SLA Gen", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Strategy Session History Section
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text(
                        "Active Strategy Rooms",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "${conversations.size} cached",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            if (conversations.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No strategy sessions recorded. Click the '+' to begin.", color = Color.Gray)
                    }
                }
            } else {
                items(conversations) { conv ->
                    val isActive = conv.id == activeId
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectConversation(conv.id)
                                onNavigateToChat()
                            }
                            .testTag("session_item_${conv.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isActive) RoyalBlue.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isActive) BorderStroke(1.dp, RoyalBlue) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Folder",
                                tint = if (isActive) CyberBlue else Color.Gray,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    conv.title,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Text(
                                    "Session ID: #${conv.id}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }
                            IconButton(
                                onClick = { viewModel.deleteSession(conv.id) },
                                modifier = Modifier.testTag("delete_session_${conv.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showNewSessionDialog) {
        AlertDialog(
            onDismissRequest = { showNewSessionDialog = false },
            title = { Text("Initialize Strategy Room", color = Color.White) },
            text = {
                Column {
                    Text("Provide a focused title for this analytical legal environment.", color = Color.LightGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newSessionTitle,
                        onValueChange = { newSessionTitle = it },
                        label = { Text("Case Title") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("new_session_name_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSessionTitle.isNotBlank()) {
                            viewModel.createNewSession(newSessionTitle)
                            newSessionTitle = ""
                            showNewSessionDialog = false
                            onNavigateToChat()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    modifier = Modifier.testTag("confirm_create_session")
                ) {
                    Text("Initialize", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showNewSessionDialog = false },
                    modifier = Modifier.testTag("cancel_create_session")
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = CardBackground
        )
    }
}
