package com.example.upscprep.ui.assignments

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.upscprep.data.database.AppDatabase
import com.example.upscprep.ui.assignments.activities.GSPaperSelectionActivity
import com.example.upscprep.ui.assignments.activities.SubjectSelectionActivity
import com.example.upscprep.ui.assignments.activities.TestHistoryActivity
import com.example.upscprep.ui.theme.microInteractionClickable
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var totalTests by remember { mutableStateOf(0) }
    var avgScore by remember { mutableStateOf(0.0) }
    var bestScore by remember { mutableStateOf(0.0) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            runCatching {
                val dao = AppDatabase.getDatabase(context).testAttemptDao()
                totalTests = dao.getTotalAttempts()
                avgScore = dao.getAverageScore() ?: 0.0
                bestScore = dao.getBestScore() ?: 0.0
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Practice Labs", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, TestHistoryActivity::class.java))
                    }) {
                        Icon(Icons.Default.List, contentDescription = "History")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    PracticeHeroCard(totalTests, avgScore, bestScore)
                }

                item {
                    Text("Choose a mode", style = MaterialTheme.typography.titleMedium)
                }

                item {
                    PracticeModeCard(
                        title = "Subject-wise Practice",
                        description = "Curated tests focused on one subject at a time.",
                        gradient = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary),
                        icon = Icons.Default.Info
                    ) {
                        context.startActivity(Intent(context, SubjectSelectionActivity::class.java).apply {
                            putExtra("mode", "subject")
                        })
                    }
                }

                item {
                    PracticeModeCard(
                        title = "GS Paper Simulator",
                        description = "Full mock tests across GS papers with timer & analysis.",
                        gradient = listOf(Color(0xFFFFA000), Color(0xFFFF7043)),
                        icon = Icons.Default.Star
                    ) {
                        context.startActivity(Intent(context, GSPaperSelectionActivity::class.java))
                    }
                }

                item {
                    PracticeModeCard(
                        title = "Topic Labs",
                        description = "Mix topics, difficulty, and duration for quick drills.",
                        gradient = listOf(Color(0xFF00897B), Color(0xFF26C6DA)),
                        icon = Icons.Default.DateRange
                    ) {
                        context.startActivity(Intent(context, SubjectSelectionActivity::class.java).apply {
                            putExtra("mode", "unit")
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun PracticeHeroCard(totalTests: Int, avgScore: Double, bestScore: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Consistency tracker", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                HeroStat(label = "Tests", value = totalTests.toString())
                HeroStat(label = "Best Score", value = "${bestScore.roundToInt()}%")
                HeroStat(label = "Avg Score", value = "${avgScore.roundToInt()}%")
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
private fun PracticeModeCard(
    title: String,
    description: String,
    gradient: List<Color>,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .microInteractionClickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(gradient))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = Color.White)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(description, color = Color.White.copy(alpha = 0.8f))
                }
                Icon(Icons.Default.List, contentDescription = null, tint = Color.White)
            }
        }
    }
}
