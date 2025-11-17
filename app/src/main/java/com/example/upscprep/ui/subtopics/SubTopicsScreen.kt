package com.example.upscprep.ui.subtopics

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.upscprep.data.model.SubTopic
import com.example.upscprep.data.model.Unit as SyllabusUnit
import com.example.upscprep.ui.theme.*

/**
 * SubTopics Screen - Displays all sub-topics for a selected unit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubTopicsScreen(
    unit: SyllabusUnit,
    subjectName: String,
    onSubTopicClick: (SubTopic) -> kotlin.Unit,
    onNavigateBack: () -> kotlin.Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = unit.unit_name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = subjectName,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CardBackground
                )
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Unit Summary Card
            item {
                UnitSummaryCard(unit)
            }

            // Sub-topics List
            items(unit.sub_topics) { subTopic ->
                SubTopicCard(
                    subTopic = subTopic,
                    onClick = { onSubTopicClick(subTopic) }
                )
            }
        }
    }
}

/**
 * Summary card showing unit statistics
 */
@Composable
fun UnitSummaryCard(unit: SyllabusUnit) {
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
                text = "Unit Overview",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatBox(
                    label = "Sub-Topics",
                    value = unit.getTotalSubTopics().toString(),
                    color = Color(0xFF2196F3)
                )
                StatBox(
                    label = "Items",
                    value = unit.getTotalItems().toString(),
                    color = Color(0xFFFF9800)
                )
                StatBox(
                    label = "Flashcards",
                    value = unit.getTotalFlashcards().toString(),
                    color = Color(0xFF9C27B0)
                )
            }
        }
    }
}

/**
 * Individual sub-topic card
 */
@Composable
fun SubTopicCard(
    subTopic: SubTopic,
    onClick: () -> kotlin.Unit
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
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = Color(0xFF2196F3).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = subTopic.sub_topic_name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${subTopic.getTotalItems()} items",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF9C27B0),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${subTopic.getTotalFlashcards()} cards",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Arrow
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Open",
                tint = TextSecondary
            )
        }
    }
}

/**
 * Stat box component
 */
@Composable
fun StatBox(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

