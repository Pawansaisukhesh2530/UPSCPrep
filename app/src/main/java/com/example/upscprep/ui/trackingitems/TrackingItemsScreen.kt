package com.example.upscprep.ui.trackingitems

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.upscprep.data.model.SubTopic
import com.example.upscprep.data.model.TrackingItem
import com.example.upscprep.ui.theme.*

/**
 * Tracking Items Screen - Displays all tracking items for a selected sub-topic
 * This is the leaf level with checkboxes for completion tracking
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingItemsScreen(
    subTopic: SubTopic,
    unitName: String,
    onNavigateBack: () -> Unit
) {
    // Track items state locally (in production, save to database/SharedPreferences)
    var items by remember { mutableStateOf(subTopic.tracking_items) }
    val completedCount = items.count { it.isCompleted }
    val totalCount = items.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = subTopic.sub_topic_name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = unitName,
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
            // Progress Card
            item {
                ProgressCard(
                    progress = progress,
                    completedCount = completedCount,
                    totalCount = totalCount,
                    totalFlashcards = subTopic.getTotalFlashcards()
                )
            }

            // Tracking Items List
            items(items) { item ->
                TrackingItemCard(
                    item = item,
                    onCheckedChange = { checked ->
                        items = items.map {
                            if (it.item_id == item.item_id) {
                                it.copy(isCompleted = checked)
                            } else {
                                it
                            }
                        }
                    }
                )
            }
        }
    }
}

/**
 * Progress card showing completion statistics
 */
@Composable
fun ProgressCard(
    progress: Float,
    completedCount: Int,
    totalCount: Int,
    totalFlashcards: Int
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
                text = "Progress Tracking",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$completedCount / $totalCount completed",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFF263238),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Flashcard Count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFF9C27B0).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Suggested Flashcards",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "$totalFlashcards cards",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9C27B0)
                    )
                }
            }
        }
    }
}

/**
 * Individual tracking item card with checkbox
 */
@Composable
fun TrackingItemCard(
    item: TrackingItem,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCompleted)
                Color(0xFF4CAF50).copy(alpha = 0.1f)
            else
                CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF4CAF50),
                    uncheckedColor = TextSecondary
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.item_name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (item.isCompleted) TextSecondary else TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ID: ${item.item_id}",
                    fontSize = 11.sp,
                    color = TextSecondary.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Flashcard Badge
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFF9C27B0).copy(alpha = 0.2f),
                        shape = CircleShape
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFF9C27B0),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${item.suggested_flashcard_qty}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9C27B0)
                    )
                }
            }
        }
    }
}

