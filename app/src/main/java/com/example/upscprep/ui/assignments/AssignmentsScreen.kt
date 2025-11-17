package com.example.upscprep.ui.assignments

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.upscprep.data.database.AppDatabase
import com.example.upscprep.ui.assignments.activities.GSPaperSelectionActivity
import com.example.upscprep.ui.assignments.activities.SubjectSelectionActivity
import com.example.upscprep.ui.assignments.activities.TestHistoryActivity
import com.example.upscprep.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentsScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = remember { AppDatabase.getDatabase(context) }

    var totalTests by remember { mutableStateOf(0) }
    var avgScore by remember { mutableStateOf(0.0) }
    var bestScore by remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        scope.launch {
            totalTests = database.testAttemptDao().getTotalAttempts()
            avgScore = database.testAttemptDao().getAverageScore() ?: 0.0
            bestScore = database.testAttemptDao().getBestScore() ?: 0.0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Practice Tests",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                actions = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, TestHistoryActivity::class.java))
                    }) {
                        Icon(
                            imageVector = Icons.Default.List,
                            contentDescription = "Test History",
                            tint = TextPrimary
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Statistics Card
            item {
                StatisticsCard(
                    totalTests = totalTests,
                    avgScore = avgScore,
                    bestScore = bestScore
                )
            }

            // Test Mode Cards
            item {
                Text(
                    text = "Choose Test Mode",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                TestModeCard(
                    title = "Subject-wise Practice",
                    description = "Test yourself on individual subjects",
                    icon = Icons.Default.Menu,
                    color = StatBlue,
                    onClick = {
                        val intent = Intent(context, SubjectSelectionActivity::class.java)
                        intent.putExtra("mode", "subject")
                        context.startActivity(intent)
                    }
                )
            }

            item {
                TestModeCard(
                    title = "GS Paper Mock Test",
                    description = "Complete paper with mixed subjects",
                    icon = Icons.Default.Star,
                    color = StatOrange,
                    onClick = {
                        context.startActivity(Intent(context, GSPaperSelectionActivity::class.java))
                    }
                )
            }

            item {
                TestModeCard(
                    title = "Topic-wise Practice",
                    description = "Practice specific units/topics",
                    icon = Icons.Default.CheckCircle,
                    color = StatGreen,
                    onClick = {
                        val intent = Intent(context, SubjectSelectionActivity::class.java)
                        intent.putExtra("mode", "unit")
                        context.startActivity(intent)
                    }
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun StatisticsCard(
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
                text = "Your Statistics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Tests Taken", value = totalTests.toString(), color = StatBlue)
                StatItem(label = "Avg Score", value = "${avgScore.toInt()}%", color = StatOrange)
                StatItem(label = "Best Score", value = "${bestScore.toInt()}%", color = StatGreen)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
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
fun TestModeCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            // Arrow
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Start",
                tint = TextSecondary
            )
        }
    }
}
