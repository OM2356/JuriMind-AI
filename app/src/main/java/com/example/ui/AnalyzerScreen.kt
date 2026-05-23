package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AnalyzerScreen(viewModel: JuriMindViewModel) {
    val analyzerInputText by viewModel.analyzerInputText.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val analyzerResult by viewModel.analyzerResult.collectAsState()
    val riskScore by viewModel.analyzedRiskScore.collectAsState()
    val riskyClauses by viewModel.detectedRiskyClauses.collectAsState()

    var activeExpandedClauseIdx by remember { mutableStateOf(-1) }

    val sampleNda = "MUTUAL CONFIDENTIALITY INDEMNITY CONTRACT\n\n" +
            "The Receiving Party shall indemnify, defend, and hold completely harmless the Disclosing Party and its officers from and against any and all claims, lawsuits, liabilities, unlimited losses, or consequential damages arising directly or indirectly from any hypothetical breach of contract information by the Receiving Party, including reasonable attorney litigation fees.\n\n" +
            "Either party may terminate this agreement at any time for convenience immediately without prior written notifications or penalty, and all intellectual rights shall automatically transfer solely to the Disclosing Party."

    val sampleLease = "RESIDENTIAL TENANCY LEASE RULES\n\n" +
            "In case of any minor property water leak, the Tenant shall bear complete 100% cost of repairs. The Landlord reserves the absolute unilateral right to modify rent rates under 24 hours notice or inspect unit rooms at any time of night without notifying. " +
            "Under no circumstances shall the Security Deposit escrow yield any return or be refundable if unit lease is canceled in the initial half calendar cycle."

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header Banner ---
        item {
            Column {
                GradientText(
                    text = "CONTRACT ASSESSOR ENGINE",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Scan intellectual property clauses & detect exposure.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Output Display vs Inputs Display
        if (analyzerResult == null) {
            // --- INPUT SCREEN ---
            item {
                GlassmorphicCardFixed(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "PASTE CLAUSE OR CONTRACT DRAFT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    OutlinedTextField(
                        value = analyzerInputText,
                        onValueChange = { viewModel.setAnalyzerInput(it) },
                        placeholder = {
                            Text(
                                "Paste any paragraph or lease agreement sentences here to launch legal diagnostics...",
                                fontSize = 13.sp,
                                color = TextSecondary
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = GlassWhiteBorder,
                            focusedContainerColor = CosmicCard,
                            unfocusedContainerColor = CosmicDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        maxLines = 15
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isAnalyzing) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = CyberCyan)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Evaluating liabilities in real-time...",
                                fontSize = 12.sp,
                                color = CyberCyan,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    } else {
                        Button(
                            onClick = { viewModel.runContractAnalyzer(analyzerInputText) },
                            enabled = analyzerInputText.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberCyan,
                                disabledContainerColor = GlassWhite
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = "Run Assessment", tint = CosmicDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("RUN SAAS LEGAL ASSESSMENT", fontWeight = FontWeight.Black, color = CosmicDark, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Quick Presets Selector
            item {
                Text(
                    text = "NEURAL SIMULATED TEMPLATES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberMagenta,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.setAnalyzerInput(sampleNda) },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, CyberMagenta.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.Gavel, contentDescription = "NDA Sample", tint = CyberMagenta, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tricky NDA clause", fontSize = 10.sp, color = CyberMagenta, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.setAnalyzerInput(sampleLease) },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicCard),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, CyberYellow.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Default.Home, contentDescription = "Lease Sample", tint = CyberYellow, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tricky Rent lease", fontSize = 10.sp, color = CyberYellow, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // --- DIAGNOSTICS & RESULTS SCREEN ---
            item {
                Button(
                    onClick = { viewModel.clearAnalyzer() },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicCard),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, GlassWhite, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Reset", tint = CyberCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ANALYZE ANOTHER CLAUSE", fontSize = 11.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                }
            }

            // Dial Gauge & Risk Gauge Display
            item {
                GlassmorphicCardFixed(
                    modifier = Modifier.fillMaxWidth(),
                    glowActive = riskScore >= 50
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FuturisticDialGauge(
                            score = riskScore,
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 10.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1.2f)) {
                            Text(
                                text = "RISK INDEX ANALYSIS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (riskScore >= 70) "SEVERE DETECTOR LOCKS" else if (riskScore >= 40) "MEDIUM RISK DETECTED" else "LOW RISK PROFILE",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (riskScore >= 70) CyberRed else if (riskScore >= 40) CyberYellow else CyberGreen
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "JuriMind AI analyzed the provisions. Exposure is mapped below according to standard indemnification weights.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            // Identified Clauses Header List
            item {
                Text(
                    text = "IDENTIFIED CONTROVERSIAL PROVISIONS (${riskyClauses.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberMagenta,
                    fontFamily = FontFamily.Monospace
                )
            }

            items(
                count = riskyClauses.size,
                key = { idx -> riskyClauses[idx].title }
            ) { idx ->
                val clause = riskyClauses[idx]
                val levelColor = when (clause.riskLevel.lowercase()) {
                    "high" -> CyberRed
                    "medium" -> CyberYellow
                    else -> CyberCyan
                }
                val isExpanded = activeExpandedClauseIdx == idx

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CosmicCard)
                        .border(
                            1.dp,
                            if (isExpanded) levelColor.copy(alpha = 0.6f) else GlassWhite,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            activeExpandedClauseIdx = if (isExpanded) -1 else idx
                        }
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(levelColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = clause.riskLevel.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = levelColor,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = clause.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle",
                                tint = TextSecondary
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                Text(
                                    text = "EXPLANATION OF EXPOSURE:",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = clause.explanation,
                                    fontSize = 12.sp,
                                    color = TextPrimary,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "SUGGESTED MITIGATION TEXT:",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberGreen,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CosmicSurface)
                                        .border(1.dp, GlassWhite, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = clause.suggestedAlternative,
                                        fontSize = 11.sp,
                                        color = CyberGreen,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // General Report MD View
            item {
                Text(
                    text = "EXPANDED NEURAL ANALYSIS DRAFT REPORT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    fontFamily = FontFamily.Monospace
                )
            }

            item {
                GlassmorphicCardFixed(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "SYNTHESIZED LOGS:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = analyzerResult ?: "",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
