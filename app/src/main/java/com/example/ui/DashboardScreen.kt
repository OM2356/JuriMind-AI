package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: JuriMindViewModel,
    onNavigateToChat: () -> Unit,
    onNavigateToAnalyzer: () -> Unit,
    onNavigateToGenerator: () -> Unit
) {
    val quotaUsed by viewModel.apiQuotaUsed.collectAsState()
    val quotaLimit by viewModel.apiQuotaLimit.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val userPlan by viewModel.userPlan.collectAsState()
    val savedDocs by viewModel.savedDocuments.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Standard Compose layout
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                CyberMagenta.copy(alpha = 0.2f),
                                CyberCyan.copy(alpha = 0.05f)
                            )
                        )
                    )
                    .border(1.dp, GlassWhiteBorder, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "SYSTEM ACCESS AUTHORIZED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = CyberCyan,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            GradientText(
                                text = "JuriMind AI Core v3.5",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CyberCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Security Core",
                                tint = CyberCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Global SaaS status online. Decentralized legal intelligence active over neural encryption networks. Ask real-time questions, evaluate high-stakes indemnities, or draft automated NDAs.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // --- Grid Analytics Cards ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Queries
                GlassmorphicCardFixed(
                    modifier = Modifier.weight(1f),
                    borderColor = GlassWhiteBorder
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.QueryStats,
                            contentDescription = "Queries",
                            tint = CyberCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("QUERIES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("$quotaUsed", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text("API Quota consumption", fontSize = 10.sp, color = TextSecondary)
                }

                // Protected index / Saved Docs
                GlassmorphicCardFixed(
                    modifier = Modifier.weight(1f),
                    borderColor = GlassWhiteBorder
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.VerifiedUser,
                            contentDescription = "Safe",
                            tint = CyberGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("DOSSIERS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${savedDocs.size}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text("Contracts analyzed", fontSize = 10.sp, color = TextSecondary)
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Subscription level
                GlassmorphicCardFixed(
                    modifier = Modifier.weight(1f),
                    borderColor = GlassWhiteBorder
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.OfflineBolt,
                            contentDescription = "Active Plan",
                            tint = CyberYellow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("ACTIVE PLAN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(userRole, fontSize = 20.sp, fontWeight = FontWeight.Black, color = CyberYellow)
                    Text(userPlan, fontSize = 9.sp, color = TextSecondary)
                }

                // AI Engine score
                GlassmorphicCardFixed(
                    modifier = Modifier.weight(1f),
                    borderColor = GlassWhiteBorder
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Memory,
                            contentDescription = "Active engine",
                            tint = CyberMagenta,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI ENGINE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("FLASH 3.5", fontSize = 20.sp, fontWeight = FontWeight.Black, color = CyberMagenta)
                    Text("Latency: 480ms ~ Secure", fontSize = 9.sp, color = TextSecondary)
                }
            }
        }

        // --- Live API Quota Chart ---
        item {
            GlassmorphicCardFixed(
                modifier = Modifier.fillMaxWidth(),
                glowActive = true
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NEURAL NETWORK TRANSACTIONS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberCyan,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "7-Day Quota Analytics Logs",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "ACTIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberGreen,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .background(CyberGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                UsageBarChart(
                    data = listOf(35f, 48f, 72f, 96f, 60f, 85f, 110f),
                    days = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total Quota remaining: ${quotaLimit - quotaUsed} / $quotaLimit Credits",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Auto-recharges in 4 days",
                        fontSize = 11.sp,
                        color = CyberCyan,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // --- Quick Actions Grid ---
        item {
            Text(
                text = "NEURAL CORE MODULES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyan,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                maxItemsInEachRow = 3
            ) {
                // Quick Chat
                Button(
                    onClick = onNavigateToChat,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyan.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(54.dp)
                        .weight(1f)
                        .border(1.dp, CyberCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "Consult AI", tint = CyberCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Chat", fontSize = 12.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                }

                // Quick Analyzer
                Button(
                    onClick = onNavigateToAnalyzer,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberMagenta.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(54.dp)
                        .weight(1f)
                        .border(1.dp, CyberMagenta.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.Analytics, contentDescription = "Analyzer", tint = CyberMagenta, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Analyzer", fontSize = 12.sp, color = CyberMagenta, fontWeight = FontWeight.Bold)
                }

                // Quick Notice Gen
                Button(
                    onClick = onNavigateToGenerator,
                    colors = ButtonDefaults.buttonColors(containerColor = CyberYellow.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(54.dp)
                        .weight(1f)
                        .border(1.dp, CyberYellow.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Default.HistoryEdu, contentDescription = "Synthesize", tint = CyberYellow, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Gen Doc", fontSize = 12.sp, color = CyberYellow, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- Saved Active History ---
        item {
            Text(
                text = "SAVED DOSSIERS & HISTORIES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyan,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
            )
        }

        if (savedDocs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = "No Saved Cases",
                            tint = TextSecondary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No cases or compiled structures. Open Generator or Analyzer to launch operations.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(
                count = savedDocs.size,
                key = { idx -> savedDocs[idx].id }
            ) { idx ->
                val doc = savedDocs[idx]
                val typeColor = when (doc.docType) {
                    "nda" -> CyberCyan
                    "rental" -> CyberGreen
                    "employment" -> CyberYellow
                    "analysis" -> CyberMagenta
                    else -> TextSecondary
                }

                val typeIcon = when (doc.docType) {
                    "nda" -> Icons.Default.Gavel
                    "rental" -> Icons.Default.Home
                    "employment" -> Icons.Default.AssignmentInd
                    "analysis" -> Icons.Default.QueryStats
                    else -> Icons.Default.Description
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CosmicSurface)
                        .border(1.dp, GlassWhite, RoundedCornerShape(12.dp))
                        .clickable { /* Detail View or download */ }
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(typeColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = typeIcon, contentDescription = doc.docType, tint = typeColor, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = doc.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = doc.docType.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = typeColor,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Risk index: ${doc.riskScore}%",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        IconButton(
                            onClick = { viewModel.deleteDocument(doc.id) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = CyberRed.copy(alpha = 0.6f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
