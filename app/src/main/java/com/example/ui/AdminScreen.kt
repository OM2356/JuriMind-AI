package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBlue
import com.example.ui.theme.DangerRed
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: JuriMindViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val preseededClauses by viewModel.clauses.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clause Reference Library", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.minimumInteractiveComponentSize().testTag("admin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Navigate backward",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
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
            item {
                Text(
                    "Standard Preseeded Clauses",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "This library stores standard contract clause templates from public statutes, alongside precomputed advisory risk values used as heuristics by JuriMind.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (preseededClauses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyberBlue)
                    }
                }
            } else {
                items(preseededClauses) { clause ->
                    val colorAccent = when (clause.riskLevel) {
                        "High" -> DangerRed
                        "Medium" -> WarningOrange
                        else -> CyberBlue
                    }
                    
                    CyberCard(borderColor = colorAccent.copy(alpha = 0.4f)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    clause.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                GlowBadge(text = "${clause.riskLevel} Risk", glowColor = colorAccent)
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                "Category: ${clause.category}",
                                style = MaterialTheme.typography.labelSmall,
                                color = colorAccent
                            )

                            Divider(
                                modifier = Modifier.padding(vertical = 12.dp),
                                thickness = 1.dp,
                                color = Color.Gray.copy(alpha = 0.2f)
                            )

                            Text(
                                "PROVISION TEXT:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                clause.standardText,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                "ADVISORY FINDING:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                clause.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
