package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.theme.*

enum class ScreenState {
    Dashboard, Chat, Analyzer, Generator, Admin
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable true Edge-to-Edge bleed viewports
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                // Initialize the AI Core ViewModel self-contained with Room and TTS
                val mainViewModel: JuriMindViewModel = viewModel()
                var currentNavState by remember { mutableStateOf(ScreenState.Dashboard) }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(CosmicDark),
                    containerColor = CosmicDark,
                    // Bottom Navigation Bar with gorgeous glass design floating over content
                    bottomBar = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .windowInsetsPadding(WindowInsets.navigationBars) // Safeguards Android Gestures / pills
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(68.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(CosmicSurface.copy(alpha = 0.92f))
                                    .border(1.6.dp, GlassWhiteBorder, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Item 1: Dashboard
                                    BottomNavItem(
                                        label = "Dashboard",
                                        icon = Icons.Default.Dashboard,
                                        active = currentNavState == ScreenState.Dashboard,
                                        onClick = { currentNavState = ScreenState.Dashboard }
                                    )

                                    // Item 2: AI Chat
                                    BottomNavItem(
                                        label = "AI Chat",
                                        icon = Icons.Default.Chat,
                                        active = currentNavState == ScreenState.Chat,
                                        onClick = { currentNavState = ScreenState.Chat }
                                    )

                                    // Item 3: Assessor
                                    BottomNavItem(
                                        label = "Assessor",
                                        icon = Icons.Default.Analytics,
                                        active = currentNavState == ScreenState.Analyzer,
                                        onClick = { currentNavState = ScreenState.Analyzer }
                                    )

                                    // Item 4: Notice Synthesis
                                    BottomNavItem(
                                        label = "Synthesis",
                                        icon = Icons.Default.HistoryEdu,
                                        active = currentNavState == ScreenState.Generator,
                                        onClick = { currentNavState = ScreenState.Generator }
                                    )

                                    // Item 5: SaaS Control FAQs
                                    BottomNavItem(
                                        label = "SaaS Info",
                                        icon = Icons.Default.Settings,
                                        active = currentNavState == ScreenState.Admin,
                                        onClick = { currentNavState = ScreenState.Admin }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    // Top safe container protecting camera status notches
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = innerPadding.calculateTopPadding())
                            .background(CosmicDark)
                    ) {
                        Crossfade(
                            targetState = currentNavState,
                            label = "screenTransition"
                        ) { screen ->
                            when (screen) {
                                ScreenState.Dashboard -> DashboardScreen(
                                    viewModel = mainViewModel,
                                    onNavigateToChat = { currentNavState = ScreenState.Chat },
                                    onNavigateToAnalyzer = { currentNavState = ScreenState.Analyzer },
                                    onNavigateToGenerator = { currentNavState = ScreenState.Generator }
                                )
                                ScreenState.Chat -> ChatScreen(viewModel = mainViewModel)
                                ScreenState.Analyzer -> AnalyzerScreen(viewModel = mainViewModel)
                                ScreenState.Generator -> GeneratorScreen(viewModel = mainViewModel)
                                ScreenState.Admin -> AdminScreen(viewModel = mainViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    val activeColor = if (active) CyberCyan else TextSecondary
    val scale = if (active) 1.1f else 1.0f

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = activeColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
            color = activeColor,
            fontFamily = FontFamily.SansSerif
        )
    }
}

