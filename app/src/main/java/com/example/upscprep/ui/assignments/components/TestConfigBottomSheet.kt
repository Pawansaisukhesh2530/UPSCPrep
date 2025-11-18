package com.example.upscprep.ui.assignments.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.upscprep.ui.theme.*

/**
 * Configuration data for test customization
 */
data class TestConfiguration(
    val numQuestions: Int,
    val durationMinutes: Int
) {
    companion object {
        val QUESTION_OPTIONS = listOf(5, 10, 15, 20, 25)
        val DURATION_OPTIONS = listOf(10, 15, 20, 25)

        val DEFAULT = TestConfiguration(
            numQuestions = 15,
            durationMinutes = 20
        )
    }
}

/**
 * Test Configuration Bottom Sheet
 *
 * Allows users to customize:
 * - Number of questions (5, 10, 15, 20, 25)
 * - Test duration (10, 15, 20, 25 minutes)
 *
 * Usage:
 * ```
 * var showConfig by remember { mutableStateOf(true) }
 *
 * if (showConfig) {
 *     TestConfigBottomSheet(
 *         onConfigSelected = { config ->
 *             showConfig = false
 *             startQuiz(config.numQuestions, config.durationMinutes)
 *         },
 *         onDismiss = { showConfig = false }
 *     )
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestConfigBottomSheet(
    onConfigSelected: (TestConfiguration) -> Unit,
    onDismiss: () -> Unit,
    maxQuestionsAvailable: Int? = null // If provided, limits max questions to available
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedQuestions by remember { mutableStateOf<Int?>(null) }
    var selectedDuration by remember { mutableStateOf<Int?>(null) }

    // Filter question options based on availability
    val availableQuestionOptions = remember(maxQuestionsAvailable) {
        if (maxQuestionsAvailable != null) {
            TestConfiguration.QUESTION_OPTIONS.filter { it <= maxQuestionsAvailable }
        } else {
            TestConfiguration.QUESTION_OPTIONS
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        contentColor = TextPrimary,
        dragHandle = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(4.dp)
                        .background(TextSecondary.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Text(
                text = "Customize Your Assignment",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Text(
                text = "Select the number of questions and test duration",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Number of Questions Section
            SectionHeader(
                icon = Icons.Default.List,
                title = "Number of Questions"
            )

            Spacer(modifier = Modifier.height(16.dp))

            QuestionOptionsGrid(
                options = availableQuestionOptions,
                selectedOption = selectedQuestions,
                onOptionSelected = { selectedQuestions = it }
            )

            if (maxQuestionsAvailable != null && maxQuestionsAvailable < TestConfiguration.QUESTION_OPTIONS.last()) {
                Text(
                    text = "Only $maxQuestionsAvailable questions available in this category",
                    fontSize = 12.sp,
                    color = AccentCoral,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Test Duration Section
            SectionHeader(
                icon = Icons.Default.DateRange,
                title = "Test Duration"
            )

            Spacer(modifier = Modifier.height(16.dp))

            DurationOptionsGrid(
                options = TestConfiguration.DURATION_OPTIONS,
                selectedOption = selectedDuration,
                onOptionSelected = { selectedDuration = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Start Test Button
            Button(
                onClick = {
                    if (selectedQuestions != null && selectedDuration != null) {
                        onConfigSelected(
                            TestConfiguration(
                                numQuestions = selectedQuestions!!,
                                durationMinutes = selectedDuration!!
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedQuestions != null && selectedDuration != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GradientStart,
                    contentColor = Color.White,
                    disabledContainerColor = GradientStart.copy(alpha = 0.4f),
                    disabledContentColor = Color.White.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (selectedQuestions != null && selectedDuration != null) {
                        "Start Test ($selectedQuestions Q • $selectedDuration min)"
                    } else {
                        "Select Options to Start"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GradientStart,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
private fun QuestionOptionsGrid(
    options: List<Int>,
    selectedOption: Int?,
    onOptionSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.chunked(3).forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowOptions.forEach { option ->
                    OptionChip(
                        label = "$option",
                        sublabel = "Questions",
                        isSelected = option == selectedOption,
                        onSelected = { onOptionSelected(option) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if row not complete
                repeat(3 - rowOptions.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DurationOptionsGrid(
    options: List<Int>,
    selectedOption: Int?,
    onOptionSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        options.chunked(2).forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowOptions.forEach { option ->
                    OptionChip(
                        label = "$option",
                        sublabel = "Minutes",
                        isSelected = option == selectedOption,
                        onSelected = { onOptionSelected(option) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill remaining space if row not complete
                repeat(2 - rowOptions.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun OptionChip(
    label: String,
    sublabel: String,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .selectable(
                selected = isSelected,
                onClick = onSelected,
                role = Role.RadioButton
            ),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) GradientStart.copy(alpha = 0.2f) else CardBackground,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, GradientStart)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, TextSecondary.copy(alpha = 0.2f))
        },
        tonalElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) GradientStart else TextPrimary
            )
            Text(
                text = sublabel,
                fontSize = 12.sp,
                color = if (isSelected) GradientStart.copy(alpha = 0.8f) else TextSecondary
            )
        }
    }
}

