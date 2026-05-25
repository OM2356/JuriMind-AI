package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
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
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.RoyalBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(
    viewModel: JuriMindViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resultText by viewModel.generatedContractText.collectAsState()

    var selectedType by remember { mutableStateOf("Mutual NDA") }
    var companyName by remember { mutableStateOf("") }
    var clientName by remember { mutableStateOf("") }
    var contractValue by remember { mutableStateOf("$25,000") }
    var durationMonths by remember { mutableStateOf(12f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SLA Agreement Auto-Gen", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.minimumInteractiveComponentSize().testTag("generator_back_button")
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
                    "Autogenerate Compliant SLA/NDA templates",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Provide contextual variables below. The generator injects mutual restrictions and liability protective ceilings dynamically.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Selector Tabs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Mutual NDA", "Service Level SLA").forEach { option ->
                        val isMatched = selectedType == option
                        Button(
                            onClick = { selectedType = option },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isMatched) RoyalBlue else DarkSurface,
                                contentColor = if (isMatched) Color.White else Color.LightGray
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("type_selector_${option.replace(" ", "_").lowercase()}")
                        ) {
                            Text(option, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Input Fields
            item {
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Your Disclosing Entity / Company Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gen_company_name_field"),
                    singleLine = true
                )
            }

            item {
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Client/Counterparty Entity Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gen_client_name_field"),
                    singleLine = true
                )
            }

            if (selectedType == "Service Level SLA") {
                item {
                    OutlinedTextField(
                        value = contractValue,
                        onValueChange = { contractValue = it },
                        label = { Text("Stipulated Service Value (USD)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gen_value_field"),
                        singleLine = true
                    )
                }
            }

            // Duration Slider
            item {
                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("Agreement Duration", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("${durationMonths.toInt()} Months", color = CyberBlue, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = durationMonths,
                        onValueChange = { durationMonths = it },
                        valueRange = 1f..60f,
                        steps = 59,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberBlue,
                            activeTrackColor = RoyalBlue
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("gen_duration_slider")
                    )
                }
            }

            // Trigger Generation Action
            item {
                Button(
                    onClick = {
                        viewModel.generateAgreement(
                            type = selectedType,
                            company = if (companyName.isNotBlank()) companyName else "[DISCLOSING PARTY]",
                            client = if (clientName.isNotBlank()) clientName else "[COUNTERPARTY]",
                            duration = durationMonths.toInt().toString(),
                            value = contractValue
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("trigger_document_generation")
                ) {
                    Icon(Icons.Default.Build, contentDescription = "Draft icon")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Draft Contract Agreement", color = Color.White)
                }
            }

            // Final Output Block
            if (resultText.isNotBlank()) {
                item {
                    Text(
                        "Generated Draft Memorandum",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CyberBlue.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = resultText,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
