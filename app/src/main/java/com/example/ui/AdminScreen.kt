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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun AdminScreen(viewModel: JuriMindViewModel) {
    val userRole by viewModel.userRole.collectAsState()
    val userPlan by viewModel.userPlan.collectAsState()
    val quotaUsed by viewModel.apiQuotaUsed.collectAsState()

    var activeFaqIdx by remember { mutableStateOf(-1) }

    val faqItems = listOf(
        FaqData(
            "How does JuriMind AI secure my draft covenants?",
            "JuriMind operates utilizing advanced end-to-end sandbox guidelines. In local mode, data resides entirely within your localized SQLite/Room enclave system. In cloud mode, queries are routed through secure, non-retained server pipelines using state-of-the-art enterprise protection tunnels."
        ),
        FaqData(
            "Can I use generated agreements directly inside court jurisdictions?",
            "While JuriMind provides elite legal draft synthesis based on standard commercial statutes (e.g., Delaware state code, common tenancy rules), it compiles drafts solely for consultation purposes. We recommend vetting files with qualified attorneys before filing direct court actions."
        ),
        FaqData(
            "What criteria dictates the identified risk scores?",
            "The contract risk dial is evaluated by scanning liabilities, indemnity clauses, unilateral terminations, and unlimited warranty exposures. High index ratings (70%+) signify severe unilateral obligations that generally bind the user to unfavorable damages."
        ),
        FaqData(
            "How is the SaaS monthly API quota calculated?",
            "Each network chat query consumes 1 credit, contract scans consume 2 credits, and agreement syntheses consume 3 credits. Recharging schedules reset credit counters naturally at each recurrent subscription cycle."
        )
    )

    val testimonialItems = listOf(
        TestimonialData("Devon Reed", "Senior Corporate Counsel", "JuriMind's clause-risk index dial helped us identify and strike three highly unfavorable indemnity terms in less than two minutes.", 5),
        TestimonialData("Kira Chen", "SaaS Startup Founder", "Drafting our seed-stage NDAs through the JuriMind synthesis lab saved us thousands in premature attorney consulting hours.", 5)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Title Banner ---
        item {
            Column {
                GradientText(
                    text = "CONTROLLER & DIRECTORIES",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Modify credentials, inspect clusters, examine SaaS directories.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // --- 1. USER PROFILE & ROLE SELECTOR ---
        item {
            GlassmorphicCardFixed(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(CyberCyan, CyberMagenta)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (userRole == "Admin") "SYS" else "USR",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = CosmicDark,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "omkarsathe3103",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(CyberCyan.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = userRole.uppercase(),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = userPlan,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = GlassWhite)
                Spacer(modifier = Modifier.height(14.dp))

                // Role Toggler selector
                Text(
                    text = "TOGGLE SIMULATED SaaS ACCOUNT ROLE:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberMagenta,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val roles = listOf("User", "Admin", "Enterprise VIP")
                    roles.forEach { r ->
                        val active = r == userRole
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) CyberMagenta.copy(alpha = 0.2f) else CosmicDark)
                                .border(
                                    1.dp,
                                    if (active) CyberMagenta else GlassWhiteBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.updateUserRole(r) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = r,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (active) CyberMagenta else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // --- 2. ADMIN ONLY MODULE: CLOUD CLUSTERS ---
        if (userRole == "Admin") {
            item {
                Text(
                    text = "SYSTEM OPERATIONS CLUSTER PANEL",
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "JuriServer Eastern Grid:",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CyberGreen))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ACTIVE", fontSize = 11.sp, color = CyberGreen, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Token Context cache:",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("94% RATIO", fontSize = 11.sp, color = CyberCyan, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Enclave Security Tunnels:",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("SSL/AES-GCM", fontSize = 11.sp, color = CyberYellow, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SaaS Analytics database:",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("SQLITE ACTIVE", fontSize = 11.sp, color = CyberMagenta, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // --- 3. PRICING WIDGET ---
        item {
            Text(
                text = "AVAILABLE SaaS PLANS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyan,
                fontFamily = FontFamily.Monospace
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Silver (Basic)
                GlassmorphicCardFixed(
                    modifier = Modifier.weight(1f)
                ) {
                    Text("SILVER LITE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextSecondary, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$29/mo", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("100 scans, basic chat consultation, standard models.", fontSize = 9.sp, color = TextSecondary)
                }

                // Gold (Enterprise)
                GlassmorphicCardFixed(
                    modifier = Modifier.weight(1f),
                    borderColor = CyberCyan,
                    glowActive = true
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("GOLD PRO", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberCyan, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(imageVector = Icons.Default.Star, contentDescription = "Recommended", tint = CyberCyan, modifier = Modifier.size(10.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$149/mo", fontSize = 18.sp, fontWeight = FontWeight.Black, color = CyberCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Unlimited scans, real-time Gemini, custom clauses, full legal notices.", fontSize = 9.sp, color = TextSecondary)
                }
            }
        }

        // --- 4. TESTIMONIALS ---
        item {
            Text(
                text = "SaaS USER COMMENDATIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CyberMagenta,
                fontFamily = FontFamily.Monospace
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicCard.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    val test = testimonialItems[0]
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(test.stars) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "Star", tint = CyberYellow, modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"${test.comment}\"",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "- ${test.name}, ${test.title}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                }
            }
        }

        // --- 5. COMPREHENSIVE FAQs COLLAPSIBLE LIST ---
        item {
            Text(
                text = "JuriMind EXPLANATORY FAQS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyan,
                fontFamily = FontFamily.Monospace
            )
        }

        items(faqItems.size) { index ->
            val faq = faqItems[index]
            val isExpanded = activeFaqIdx == index

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CosmicCard)
                    .border(
                        1.dp,
                        if (isExpanded) CyberCyan.copy(alpha = 0.6f) else GlassWhite,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        activeFaqIdx = if (isExpanded) -1 else index
                    }
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = faq.question,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand FAQ",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(
                                text = faq.answer,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

data class FaqData(
    val question: String,
    val answer: String
)

data class TestimonialData(
    val name: String,
    val title: String,
    val comment: String,
    val stars: Int
)
