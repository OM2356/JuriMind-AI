package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
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
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.WarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyzerScreen(
    viewModel: JuriMindViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val analyzerText by viewModel.analyzerInput.collectAsState()
    val findings by viewModel.analyzerResult.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Risk Guard Analyzer", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.minimumInteractiveComponentSize().testTag("analyzer_back_button")
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
                    "Clause Risk Audit Scanner",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Paste arbitrary legal agreements, provisions, or terms of service below. Our rule engines will flag unlimited indemnities, unilateral terminations, and severe ownership imbalances.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Input Paragraph Field
            item {
                OutlinedTextField(
                    value = analyzerText,
                    onValueChange = { viewModel.updateAnalyzerInput(it) },
                    label = { Text("Contract text or clause provision") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    placeholder = { 
                        Text(
                            "e.g., 'The Client agrees to fully indemnify and hold harmless the Company against all third party claims...'",
                            color = Color.Gray
                        ) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .testTag("analyzer_input_area"),
                    maxLines = 10
                )
            }

            // Command Control Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.clearAnalyzer() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.LightGray),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("clear_analyzer_button")
                    ) {
                        Text("Reset Scanner")
                    }

                    Button(
                        onClick = { viewModel.runAnalyzerAssessment() },
                        enabled = analyzerText.isNotBlank() && !isAnalyzing,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                        modifier = Modifier
                            .weight(1.5f)
                            .minimumInteractiveComponentSize()
                            .testTag("trigger_audit_button")
                    ) {
                        if (isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp).padding(end = 8.dp),
                                color = Color.White
                            )
                            Text("Analyzing Engine...")
                        } else {
                            Icon(Icons.Default.Search, contentDescription = "Scan icon", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Run Assessment")
                        }
                    }
                }
            }

            // Findings Feed
            findings?.let { checkList ->
                item {
                    Text(
                        "Risk Audit Findings (${checkList.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                items(checkList) { finding ->
                    val isHigh = finding.contains("HIGH RISK")
                    val isMedium = finding.contains("MEDIUM RISK")
                    val badgeColor = when {
                        isHigh -> Color.Red
                        isMedium -> WarningOrange
                        else -> CyberBlue
                    }

                    CyberCard(borderColor = badgeColor.copy(alpha = 0.5f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "finding",
                                tint = badgeColor,
                                modifier = Modifier.padding(end = 12.dp).size(22.dp)
                            )
                            Text(
                                text = finding,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                lineHeight = 20.sp
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
