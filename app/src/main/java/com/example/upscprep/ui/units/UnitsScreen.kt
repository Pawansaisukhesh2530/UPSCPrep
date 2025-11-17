package com.example.upscprep.ui.units

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.upscprep.data.model.SyllabusSubject
import com.example.upscprep.data.model.Unit as SyllabusUnit
import com.example.upscprep.ui.theme.*

/**
 * Units Screen - Displays all units for a selected subject
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitsScreen(
    syllabusSubject: SyllabusSubject,
    onUnitClick: (SyllabusUnit) -> kotlin.Unit,
    onNavigateBack: () -> kotlin.Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = syllabusSubject.subject,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = syllabusSubject.gs_paper,
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
            // Subject Summary Card
            item {
                SubjectSummaryCard(syllabusSubject)
            }

            // Units List
            items(syllabusSubject.units) { unit ->
                UnitCard(
                    unit = unit,
                    onClick = { onUnitClick(unit) }
                )
            }
        }
    }
}

/**
 * Summary card showing subject statistics
 */
@Composable
fun SubjectSummaryCard(syllabusSubject: SyllabusSubject) {
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
                text = "Subject Overview",
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
                    label = "Units",
                    value = syllabusSubject.getTotalUnits().toString(),
                    color = Color(0xFF4CAF50)
                )
                StatBox(
                    label = "Topics",
                    value = syllabusSubject.getTotalSubTopics().toString(),
                    color = Color(0xFF2196F3)
                )
                StatBox(
                    label = "Items",
                    value = syllabusSubject.getTotalTrackingItems().toString(),
                    color = Color(0xFFFF9800)
                )
            }
        }
    }
}

/**
 * Individual unit card
 */
@Composable
fun UnitCard(
    unit: SyllabusUnit,
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
                        brush = Brush.linearGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = unit.unit_name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "${unit.getTotalSubTopics()} Topics",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${unit.getTotalItems()} Items",
                        fontSize = 12.sp,
                        color = Color(0xFFFF9800)
                    )
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

