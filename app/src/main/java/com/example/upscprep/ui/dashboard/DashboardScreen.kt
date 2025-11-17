package com.example.upscprep.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    onLogout: () -> Unit
) {
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
            item {
                WelcomeCard(userName = userName, onLogout = onLogout)
            }

            // Stats Grid
            item {
                StatsGrid(stats = stats)
            }

            // Today's Summary
            item {
                TodaySummaryCard(stats = stats)
            }

            // Progress Section
            item {
                ProgressSection(subjects = subjects)
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
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

/**
 * Grid of stat cards showing key metrics
 */
@Composable
fun StatsGrid(stats: StudyStats) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Study Time",
                value = stats.studyTimeDisplay,
                icon = Icons.Default.DateRange,
                backgroundColor = StatGreen
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Streak",
                value = "${stats.currentStreak} days",
                icon = Icons.Default.KeyboardArrowUp,
                backgroundColor = StatOrange
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Topics Done",
                value = stats.completedTopics.toString(),
                icon = Icons.Default.CheckCircle,
                backgroundColor = StatBlue
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Subjects",
                value = stats.totalSubjects.toString(),
                icon = Icons.Default.Menu,
                backgroundColor = StatPurple
            )
        }
    }
}

/**
 * Individual stat card
 */
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    backgroundColor: Color
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background icon
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 10.dp, y = (-10).dp),
                tint = backgroundColor.copy(alpha = 0.3f)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = backgroundColor,
                    modifier = Modifier.size(28.dp)
                )

                Column {
                    Text(
                        text = value,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

/**
 * Today's summary card
 */
@Composable
fun TodaySummaryCard(stats: StudyStats) {
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
            Text(
                text = "Today's Activity",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TodayStat(
                    label = "Sessions",
                    value = stats.todayStudySessions.toString(),
                    icon = Icons.Default.PlayArrow
                )
                TodayStat(
                    label = "Topics",
                    value = stats.todayTopicsCovered.toString(),
                    icon = Icons.Default.Check
                )
                TodayStat(
                    label = "Questions",
                    value = stats.todayPracticeQuestions.toString(),
                    icon = Icons.Default.Star
                )
            }
        }
    }
}

@Composable
fun TodayStat(label: String, value: String, icon: ImageVector) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = GradientStart,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

/**
 * Progress section showing subject-wise completion
 */
@Composable
fun ProgressSection(subjects: List<Subject>) {
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
            Text(
                text = "Subject Progress",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Show top 5 subjects
            subjects.take(5).forEach { subject ->
                ProgressItem(subject = subject)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

/**
 * Individual progress item for a subject
 */
@Composable
fun ProgressItem(subject: Subject) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = subject.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = "${(subject.progressPercentage * 100).toInt()}%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = subject.color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { subject.progressPercentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = subject.color,
            trackColor = SurfaceDark,
        )
    }
}
