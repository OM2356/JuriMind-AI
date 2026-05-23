package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GeneratorScreen(viewModel: JuriMindViewModel) {
    val generatorDocType by viewModel.generatorDocType.collectAsState()
    val generatorTitle by viewModel.generatorTitle.collectAsState()
    val generatorParties by viewModel.generatorParties.collectAsState()
    val generatorCustomDetails by viewModel.generatorCustomDetails.collectAsState()
    val generatedDraftText by viewModel.generatedDraftText.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()

    val templateTypes = listOf(
        TemplateItem("nda", "Confidential NDA", Icons.Default.Gavel),
        TemplateItem("rental", "Lease Contract", Icons.Default.Home),
        TemplateItem("employment", "Employment agreement", Icons.Default.AssignmentInd),
        TemplateItem("notice", "Cease formal notice", Icons.Default.Warning)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Title ---
        item {
            Column {
                GradientText(
                    text = "DRAFTING & SYNTHESIS LAB",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Compile legally binding documents via automated neural pipelines.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        if (generatedDraftText == null) {
            // --- GENERATION INPUT CONTROLS ---

            // Horizontal Template Slider
            item {
                Text(
                    text = "SELECT REQUISITE DECREE TEMPLATE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    fontFamily = FontFamily.Monospace
                )
            }

            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(templateTypes.size) { index ->
                        val item = templateTypes[index]
                        val isSelected = item.id == generatorDocType
                        val colorAccent = when (item.id) {
                            "nda" -> CyberCyan
                            "rental" -> CyberGreen
                            "employment" -> CyberYellow
                            else -> CyberMagenta
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CosmicCard)
                                .border(
                                    1.dp,
                                    if (isSelected) colorAccent else GlassWhiteBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    viewModel.updateGeneratorDocType(item.id)
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = if (isSelected) colorAccent else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) colorAccent else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Input Fields Configuration form
            item {
                GlassmorphicCardFixed(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "DOCUMENT PREAMBLE SPECIFICATIONS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Title fields
                    Text("Agreement Title:", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = generatorTitle,
                        onValueChange = { viewModel.updateGeneratorTitle(it) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = GlassWhiteBorder,
                            focusedContainerColor = CosmicCard,
                            unfocusedContainerColor = CosmicDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Involved Parties fields
                    Text("Participating Entities / Parties:", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = generatorParties,
                        onValueChange = { viewModel.updateGeneratorParties(it) },
                        placeholder = { Text("E.g. Lessor Smith & Lessee Johnson LLC", fontSize = 12.sp, color = TextSecondary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = CyberCyan,
                            unfocusedBorderColor = GlassWhiteBorder,
                            focusedContainerColor = CosmicCard,
                            unfocusedContainerColor = CosmicDark
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Special custom clauses field
                    Text("Special Conditions / Directives:", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = generatorCustomDetails,
                        onValueChange = { viewModel.updateGeneratorDetails(it) },
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
                            .height(100.dp),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isGenerating) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(color = CyberMagenta)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Compiling statutory definitions...",
                                fontSize = 12.sp,
                                color = CyberMagenta,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    } else {
                        Button(
                            onClick = { viewModel.generateLegalDocument() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.HistoryEdu, contentDescription = "Draft Contract", tint = CosmicDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SYNTHESIZE CONTRACT DECREE", fontWeight = FontWeight.Black, color = CosmicDark, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            // --- COMPILED AGREEMENT DISPLAY SCREEN ---
            item {
                Button(
                    onClick = { viewModel.clearGenerator() },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicCard),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, GlassWhite, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WRITE ANOTHER AGREEMENT", fontSize = 11.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
                }
            }

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
                                text = "NEURAL SYNTHESIS RESOLVED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberCyan,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = generatorTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(CyberGreen.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "COMPILED SECURE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberGreen,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = GlassWhite)
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = generatedDraftText ?: "",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

data class TemplateItem(
    val id: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
