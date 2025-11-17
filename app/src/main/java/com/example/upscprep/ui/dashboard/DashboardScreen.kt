package com.example.upscprep.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.upscprep.data.model.StudyStats
import com.example.upscprep.data.model.Subject
import com.example.upscprep.ui.theme.*

/**
 * Dashboard Screen - Main home screen showing study statistics and progress
 */
@Composable
fun DashboardScreen(
    userName: String = "Aspirant",
    stats: StudyStats,
    subjects: List<Subject>,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel
) {
    val liveStats by viewModel.dashboardStats.collectAsState(initial = DashboardViewModel.DashboardStats())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundDark, SurfaceDark, BackgroundDark)
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Welcome Card
            item { WelcomeCard(userName = userName, onLogout = onLogout) }

            // Live Overall Progress
            item { OverallProgressCard(progressPercent = liveStats.overallProgress) }

            // Streak and Time Today quick row
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SmallStatCard(title = "Streak", value = "${liveStats.studyStreak} days", icon = Icons.Default.KeyboardArrowUp, color = StatOrange, modifier = Modifier.weight(1f))
                    val mins = (liveStats.timeTodayMillis / 60000).toInt()
                    val hours = mins / 60
                    val rem = mins % 60
                    val timeStr = if (hours > 0) "${hours}h ${rem}m" else "${rem}m"
                    SmallStatCard(title = "Today", value = timeStr, icon = Icons.Default.DateRange, color = StatGreen, modifier = Modifier.weight(1f))
                }
            }

            // Live Test Statistics
            item { TestStatsRow(stats = liveStats.testStats) }

            // Recent tests
            item { RecentTestsList(items = liveStats.recentTests) }

            // Weak areas
            item { WeakAreasCard(items = liveStats.weakAreas) }

            // Subject-wise progress (live)
            item { SubjectProgressList(items = liveStats.subjectProgress) }

            // Recent activity
            item { RecentActivityCard(items = liveStats.recentActivity) }

            // Bottom spacing
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun OverallProgressCard(progressPercent: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Syllabus Coverage",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$progressPercent% of syllabus covered",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
            CircularProgressIndicator(
                progress = { (progressPercent / 100f).coerceIn(0f, 1f) },
                color = GradientStart,
                trackColor = SurfaceDark,
                strokeWidth = 8.dp,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

@Composable
private fun TestStatsRow(stats: com.example.upscprep.data.repository.TestStatistics) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SmallStatCard(
            title = "Total Tests",
            value = stats.totalTests.toString(),
            icon = Icons.Default.List,
            color = StatBlue,
            modifier = Modifier.weight(1f)
        )
        SmallStatCard(
            title = "Avg Score",
            value = "${String.format("%.1f", stats.avgScore)}%",
            icon = Icons.Default.Star,
            color = StatGreen,
            modifier = Modifier.weight(1f)
        )
        SmallStatCard(
            title = "Best",
            value = "${String.format("%.1f", stats.bestScore)}%",
            icon = Icons.Default.ThumbUp,
            color = StatOrange,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SmallStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(28.dp))
            Column {
                Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(title, fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun SubjectProgressList(items: List<com.example.upscprep.data.repository.SubjectProgress>) {
    if (items.isEmpty()) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Subject-wise Progress", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            items.forEach { item ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(item.subjectName, color = TextPrimary)
                        Text("${item.percentage}%", color = TextSecondary)
                    }
                    LinearProgressIndicator(
                        progress = { (item.percentage / 100f).coerceIn(0f, 1f) },
                        color = GradientStart,
                        trackColor = SurfaceDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun RecentActivityCard(items: List<com.example.upscprep.data.repository.ActivityItem>) {
    if (items.isEmpty()) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Recent Activity", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            items.take(10).forEach { act ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(act.description, color = TextPrimary)
                    Text(timeAgo(act.timeMillis), color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun RecentTestsList(items: List<com.example.upscprep.data.model.TestAttempt>) {
    if (items.isEmpty()) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Recent Tests", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(12.dp))
            items.forEach { t ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(t.subjectName ?: t.gsPaper ?: t.testType, color = TextPrimary)
                        Text("Score: ${String.format("%.1f", t.percentage)}%", color = TextSecondary, fontSize = 12.sp)
                    }
                    Text(timeAgo(t.attemptDate), color = TextSecondary, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun WeakAreasCard(items: List<com.example.upscprep.data.repository.WeakArea>) {
    if (items.isEmpty()) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Needs Attention", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AccentCoral)
            Spacer(modifier = Modifier.height(12.dp))
            items.forEach { w ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(w.subject, color = TextPrimary)
                    Text("${String.format("%.1f", w.avgScore)}%", color = TextSecondary)
                }
            }
        }
    }
}

private fun timeAgo(timeMillis: Long): String {
    val diff = System.currentTimeMillis() - timeMillis
    val minutes = diff / 60000
    val hours = minutes / 60
    val days = hours / 24
    return when {
        minutes < 1 -> "just now"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> "$hours h ago"
        else -> "$days d ago"
    }
}

/**
 * Welcome card showing user greeting
 */
@Composable
fun WelcomeCard(userName: String, onLogout: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hello, Aspirant!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = GradientStart
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Dream big, work smart",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                // Logout button
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceDark)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = AccentCoral
                    )
                }
            }
        }
    }
}
