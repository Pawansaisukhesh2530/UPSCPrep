package com.example.upscprep.ui.assignments.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.upscprep.data.database.AppDatabase
import com.example.upscprep.data.model.TestAttempt
import com.example.upscprep.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TestHistoryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UPSCPrepTheme() {
                TestHistoryScreen(
                    onBack = { finish() },
                    onViewDetails = { /* no-op, Activity will handle details via other navigation */ }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestHistoryScreen(
    onBack: () -> Unit,
    onViewDetails: (TestAttempt) -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var attempts by remember { mutableStateOf<List<TestAttempt>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var sortBy by remember { mutableStateOf("recent") } // recent, score
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(sortBy) {
        scope.launch {
            try {
                val database = AppDatabase.getDatabase(context)
                attempts = database.testAttemptDao().getAllAttempts()
                if (sortBy == "score") {
                    attempts = attempts.sortedByDescending { it.percentage }
                }
                errorMessage = null
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Failed to load test history"
                attempts = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Test History",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    // Sort button
                    var showSortMenu by remember { mutableStateOf(false) }

                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Sort",
                            tint = TextPrimary
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Recent First") },
                            onClick = {
                                sortBy = "recent"
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Highest Score First") },
                            onClick = {
                                sortBy = "score"
                                showSortMenu = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GradientStart)
            }
        } else if (attempts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "No history",
                        tint = TextSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No test history yet",
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Take your first test to see it here",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(BackgroundDark, SurfaceDark, BackgroundDark)
                        )
                    )
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Summary card
                item {
                    HistorySummaryCard(
                        totalTests = attempts.size,
                        avgScore = attempts.map { it.percentage }.average(),
                        bestScore = attempts.maxOfOrNull { it.percentage } ?: 0.0
                    )
                }

                // History items
                items(attempts) { attempt ->
                    TestAttemptCard(
                        attempt = attempt,
                        onClick = { onViewDetails(attempt) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistorySummaryCard(
    totalTests: Int,
    avgScore: Double,
    bestScore: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Overall Performance",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryStatColumn(
                    label = "Tests",
                    value = totalTests.toString(),
                    color = StatBlue
                )
                SummaryStatColumn(
                    label = "Avg Score",
                    value = "${avgScore.toInt()}%",
                    color = StatOrange
                )
                SummaryStatColumn(
                    label = "Best",
                    value = "${bestScore.toInt()}%",
                    color = StatGreen
                )
            }
        }
    }
}

@Composable
fun SummaryStatColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun TestAttemptCard(
    attempt: TestAttempt,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon based on test type
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = getTestTypeColor(attempt.testType).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getTestTypeIcon(attempt.testType),
                    contentDescription = attempt.testType,
                    tint = getTestTypeColor(attempt.testType),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getTestTitle(attempt),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatDate(attempt.attemptDate),
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Score: ${attempt.score.toInt()}/30",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "Time: ${formatTime(attempt.timeTakenSeconds)}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            // Score badge
            Box(
                modifier = Modifier
                    .background(
                        color = getScoreColor(attempt.percentage).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${attempt.percentage.toInt()}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = getScoreColor(attempt.percentage)
                )
            }
        }
    }
}

private fun getTestTitle(attempt: TestAttempt): String {
    return when (attempt.testType) {
        "subject" -> attempt.subjectName?.replaceFirstChar { it.uppercase() } ?: "Subject Test"
        "unit" -> "${attempt.subjectName?.replaceFirstChar { it.uppercase() }} - ${attempt.unitName}"
        "gs_paper" -> attempt.gsPaper ?: "GS Paper Test"
        else -> "Practice Test"
    }
}

private fun getTestTypeIcon(testType: String) = when (testType) {
    "subject" -> Icons.Default.Menu
    "unit" -> Icons.Default.CheckCircle
    "gs_paper" -> Icons.Default.Star
    else -> Icons.Default.Info
}

private fun getTestTypeColor(testType: String) = when (testType) {
    "subject" -> StatBlue
    "unit" -> StatGreen
    "gs_paper" -> StatOrange
    else -> StatPurple
}

private fun getScoreColor(percentage: Double) = when {
    percentage >= 75 -> StatGreen
    percentage >= 50 -> StatOrange
    percentage >= 33 -> Color(0xFFFFEB3B)
    else -> Color.Red
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%dm %ds", minutes, secs)
}
