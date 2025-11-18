package com.example.upscprep.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.upscprep.data.model.StudyStats
import com.example.upscprep.data.model.Subject
import com.example.upscprep.ui.theme.SuccessGreen
import com.example.upscprep.ui.theme.microInteractionClickable
import kotlin.math.roundToInt

@Composable
fun DashboardScreen(
    userName: String = "Aspirant",
    stats: StudyStats,
    subjects: List<Subject>,
    onLogout: () -> Unit,
    viewModel: DashboardViewModel,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    val liveStats by viewModel.dashboardStats.collectAsState(initial = DashboardViewModel.DashboardStats())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                HeroHeader(
                    userName = userName,
                    stats = stats,
                    liveStats = liveStats,
                    onLogout = onLogout
                )
            }

            item {
                OverallProgressCard(
                    progressPercent = liveStats.overallProgress,
                    streak = liveStats.studyStreak,
                    timeTodayMillis = liveStats.timeTodayMillis
                )
            }

            item {
                MetricsGrid(
                    metrics = listOf(
                        DashboardMetric("Subjects", stats.totalSubjects.toString(), Icons.AutoMirrored.Filled.List),
                        DashboardMetric("Topics", stats.completedTopics.toString(), Icons.Default.KeyboardArrowUp),
                        DashboardMetric("Practice Qs", "${stats.todayPracticeQuestions}", Icons.Default.CheckCircle),
                        DashboardMetric("Sessions Today", stats.todayStudySessions.toString(), Icons.Default.DateRange)
                    )
                )
            }

            item {
                QuickActionRow()
            }

            item {
                SubjectCarousel(
                    subjects = subjects,
                    progress = liveStats.subjectProgress
                )
            }

            item {
                RecentTestsSection(liveStats.recentTests)
            }

            item {
                ActivityTimeline(liveStats.recentActivity)
            }

            item {
                AnimatedVisibility(visible = liveStats.weakAreas.isNotEmpty()) {
                    WeakAreasCard(liveStats.weakAreas)
                }
            }

            item {
                MotivationalCard()
            }
        }
    }
}

@Composable
private fun HeroHeader(
    userName: String,
    stats: StudyStats,
    liveStats: DashboardViewModel.DashboardStats,
    onLogout: () -> Unit
) {
    val gradient = Brush.linearGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 6.dp,
        shadowElevation = 16.dp,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hello, $userName",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                        Text(
                            text = "Let's make today count for UPSC!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatHighlight(
                        title = "Weekly Study",
                        value = formatWeeklyMinutes(stats.weeklyStudyMinutes)
                    )
                    StatHighlight(
                        title = "Current Streak",
                        value = "${liveStats.studyStreak} days"
                    )
                }
            }
        }
    }
}

@Composable
private fun OverallProgressCard(
    progressPercent: Int,
    streak: Int,
    timeTodayMillis: Long
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
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
                    text = "Overall Progress",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$progressPercent% syllabus complete",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    MiniChip(label = "Streak ${streak}d")
                    MiniChip(label = "Today ${formatDuration(timeTodayMillis)}")
                }
            }
            CircularProgressIndicator(
                progress = { (progressPercent / 100f).coerceIn(0f, 1f) },
                color = MaterialTheme.colorScheme.secondary,
                strokeWidth = 8.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Composable
private fun MiniChip(label: String) {
    Surface(
        shape = RoundedCornerShape(50),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatHighlight(title: String, value: String) {
    Column {
        Text(text = title, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
        Text(text = value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MetricsGrid(metrics: List<DashboardMetric>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            metrics.forEach { metric ->
                MetricCard(metric)
            }
        }
    }
}

@Composable
private fun MetricCard(metric: DashboardMetric) {
    Surface(
        modifier = Modifier
            .width(150.dp)
            .height(90.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = metric.icon,
                        contentDescription = metric.title,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Column {
                Text(text = metric.value, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = metric.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionRow() {
    val actions = listOf(
        QuickAction("Assignments", Icons.AutoMirrored.Filled.List),
        QuickAction("Subjects", Icons.Default.Star),
        QuickAction("Progress", Icons.Default.KeyboardArrowUp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        actions.forEach { action ->
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .microInteractionClickable { },
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = action.title, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = "View details",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun SubjectCarousel(
    subjects: List<Subject>,
    progress: List<com.example.upscprep.data.repository.SubjectProgress>
) {
    if (subjects.isEmpty()) return

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Subject Progress",
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(subjects) { subject ->
                SubjectCard(
                    subject = subject,
                    percentage = progress.find { it.subjectName == subject.name }?.percentage ?: subject.progressPercentage.times(
                        100
                    ).roundToInt()
                )
            }
        }
    }
}

@Composable
private fun SubjectCard(subject: Subject, percentage: Int) {
    Card(
        modifier = Modifier
            .size(width = 220.dp, height = 150.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = subject.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            LinearProgressIndicator(
                progress = { (percentage / 100f).coerceIn(0f, 1f) },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
            )
            Text(
                text = "$percentage% complete",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun RecentTestsSection(items: List<com.example.upscprep.data.model.TestAttempt>) {
    if (items.isEmpty()) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Recent Tests", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            items.take(3).forEach { test ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(test.subjectName ?: test.gsPaper ?: test.testType, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = timeAgo(test.attemptDate),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (test.percentage >= 60) SuccessGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "${test.percentage.roundToInt()}%",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = if (test.percentage >= 60) SuccessGreen else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityTimeline(items: List<com.example.upscprep.data.repository.ActivityItem>) {
    if (items.isEmpty()) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Recent Activity", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            items.take(5).forEach { activity ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(activity.description, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = timeAgo(activity.timeMillis),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Composable
private fun WeakAreasCard(items: List<com.example.upscprep.data.repository.WeakArea>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("Needs Attention", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            items.forEach { weakArea ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(weakArea.subject, fontWeight = FontWeight.Medium)
                    Text("${weakArea.avgScore.roundToInt()}%", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun MotivationalCard() {
    val quotes = listOf(
        "Discipline is the bridge between goals and accomplishment.",
        "UPSC success starts with consistent smart work.",
        "Every hour of focused study moves you closer to LBSNAA."
    )
    var quote by remember { mutableStateOf(quotes.random()) }

    LaunchedEffect(Unit) {
        quote = quotes.random()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(text = quote, style = MaterialTheme.typography.titleMedium)
            Text("Keep going, aspirant!", color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
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

private fun formatWeeklyMinutes(minutes: Int): String {
    val hours = minutes / 60
    val remaining = minutes % 60
    return "${hours}h ${remaining}m"
}

private fun formatDuration(ms: Long): String {
    val minutes = (ms / 60000).toInt()
    val hours = minutes / 60
    val remaining = minutes % 60
    return if (hours > 0) "${hours}h ${remaining}m" else "${remaining}m"
}

private data class DashboardMetric(val title: String, val value: String, val icon: ImageVector)

private data class QuickAction(val title: String, val icon: ImageVector)
