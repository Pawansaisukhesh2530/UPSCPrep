package com.example.upscprep.ui.assignments.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.upscprep.data.model.Question
import com.example.upscprep.data.model.UserAnswer
import com.example.upscprep.ui.theme.*
import com.example.upscprep.utils.QuestionLoaderHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class QuizActivity : ComponentActivity() {

    private val QUIZ_DURATION_MS = 30 * 60 * 1000L // 30 minutes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mode = intent.getStringExtra("mode") ?: ""
        val subject = intent.getStringExtra("subject")
        val unit = intent.getStringExtra("unit")
        val gsPaper = intent.getStringExtra("gs_paper")

        setContent {
            UPSCPrepTheme() {
                QuizScreen(
                    mode = mode,
                    subject = subject,
                    unit = unit,
                    gsPaper = gsPaper,
                    quizDuration = QUIZ_DURATION_MS,
                    onSubmit = { questions, answers, timeTaken ->
                        navigateToResult(questions, answers, timeTaken, mode, subject, unit, gsPaper)
                    },
                    onExit = { finish() }
                )
            }
        }
    }

    private fun navigateToResult(
        questions: List<Question>,
        answers: Map<String, UserAnswer>,
        timeTaken: Long,
        mode: String,
        subject: String?,
        unit: String?,
        gsPaper: String?
    ) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putParcelableArrayListExtra("questions", ArrayList(questions))
        intent.putExtra("answers_json", com.google.gson.Gson().toJson(answers))
        intent.putExtra("time_taken", timeTaken)
        intent.putExtra("mode", mode)
        intent.putExtra("subject", subject)
        intent.putExtra("unit", unit)
        intent.putExtra("gs_paper", gsPaper)
        startActivity(intent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    mode: String,
    subject: String?,
    unit: String?,
    gsPaper: String?,
    quizDuration: Long,
    onSubmit: (List<Question>, Map<String, UserAnswer>, Long) -> Unit,
    onExit: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var userAnswers by remember { mutableStateOf<Map<String, UserAnswer>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var showQuestionGrid by remember { mutableStateOf(false) }
    var showSubmitDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    var timeRemaining by remember { mutableStateOf(quizDuration) }
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }

    val pagerState = rememberPagerState(pageCount = { questions.size })

    // Load questions
    LaunchedEffect(Unit) {
        scope.launch {
            questions = withContext(Dispatchers.IO) {
                loadQuestions(context, mode, subject, unit, gsPaper)
            }
            isLoading = false
            startTime = System.currentTimeMillis()
        }
    }

    // Timer
    LaunchedEffect(isLoading) {
        if (!isLoading && questions.isNotEmpty()) {
            while (timeRemaining > 0) {
                kotlinx.coroutines.delay(1000)
                timeRemaining -= 1000
            }
            // Auto-submit when time expires
            val timeTaken = System.currentTimeMillis() - startTime
            onSubmit(questions, userAnswers, timeTaken)
        }
    }

    // Back handler
    BackHandler {
        showExitDialog = true
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = GradientStart)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading questions...", color = TextPrimary)
            }
        }
    } else if (questions.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "No questions",
                    tint = TextSecondary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No questions available for this test",
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onExit) {
                    Text("Go Back")
                }
            }
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Timer
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Timer",
                                    tint = if (timeRemaining < 5 * 60 * 1000) Color.Red else TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = formatTime(timeRemaining),
                                    color = if (timeRemaining < 5 * 60 * 1000) Color.Red else TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Question counter
                            Text(
                                text = "${pagerState.currentPage + 1}/${questions.size}",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { showExitDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit",
                                tint = TextPrimary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showQuestionGrid = true }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Question Grid",
                                tint = TextPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SurfaceDark
                    )
                )
            },
            containerColor = BackgroundDark,
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { showSubmitDialog = true },
                    containerColor = GradientStart,
                    contentColor = TextPrimary
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Submit")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit Test")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Question Pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    QuestionPage(
                        question = questions[page],
                        questionNumber = page + 1,
                        userAnswer = userAnswers[questions[page].q_id],
                        onAnswerSelected = { selectedOption ->
                            userAnswers = userAnswers.toMutableMap().apply {
                                put(
                                    questions[page].q_id,
                                    UserAnswer(questions[page].q_id, selectedOption, get(questions[page].q_id)?.isMarkedForReview ?: false)
                                )
                            }
                        },
                        onClearResponse = {
                            userAnswers = userAnswers.toMutableMap().apply {
                                remove(questions[page].q_id)
                            }
                        },
                        onMarkForReview = { marked ->
                            userAnswers = userAnswers.toMutableMap().apply {
                                val current = get(questions[page].q_id)
                                put(
                                    questions[page].q_id,
                                    UserAnswer(questions[page].q_id, current?.selectedOption ?: "", marked)
                                )
                            }
                        }
                    )
                }

                // Navigation buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceDark)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                if (pagerState.currentPage > 0) {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        },
                        enabled = pagerState.currentPage > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CardBackground
                        )
                    ) {
                        Text("Previous")
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                if (pagerState.currentPage < questions.size - 1) {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        enabled = pagerState.currentPage < questions.size - 1,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GradientStart
                        )
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }

    // Question Grid Dialog
    if (showQuestionGrid) {
        QuestionGridDialog(
            questions = questions,
            userAnswers = userAnswers,
            currentQuestion = pagerState.currentPage,
            onQuestionSelected = { index ->
                scope.launch {
                    pagerState.animateScrollToPage(index)
                }
                showQuestionGrid = false
            },
            onDismiss = { showQuestionGrid = false }
        )
    }

    // Submit Confirmation Dialog
    if (showSubmitDialog) {
        SubmitConfirmationDialog(
            answeredCount = userAnswers.count { it.value.selectedOption.isNotEmpty() },
            totalCount = questions.size,
            onConfirm = {
                val timeTaken = System.currentTimeMillis() - startTime
                onSubmit(questions, userAnswers, timeTaken)
            },
            onDismiss = { showSubmitDialog = false }
        )
    }

    // Exit Confirmation Dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit Quiz?", color = TextPrimary) },
            text = { Text("Your progress will be lost if you exit now.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = onExit) {
                    Text("Exit", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Continue", color = GradientStart)
                }
            },
            containerColor = CardBackground
        )
    }
}

@Composable
fun QuestionPage(
    question: Question,
    questionNumber: Int,
    userAnswer: UserAnswer?,
    onAnswerSelected: (String) -> Unit,
    onClearResponse: () -> Unit,
    onMarkForReview: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        // Question number badge
        Box(
            modifier = Modifier
                .background(
                    color = GradientStart.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Question $questionNumber",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GradientStart
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Question text
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = question.question_text,
                fontSize = 16.sp,
                color = TextPrimary,
                modifier = Modifier.padding(16.dp),
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Options
        question.options.forEach { option ->
            OptionCard(
                option = option.option_text,
                optionId = option.option_id,
                isSelected = userAnswer?.selectedOption == option.option_id,
                onSelected = { onAnswerSelected(option.option_id) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Clear button
            OutlinedButton(
                onClick = onClearResponse,
                enabled = userAnswer?.selectedOption?.isNotEmpty() == true
            ) {
                Text("Clear Response")
            }

            // Mark for review checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    onMarkForReview(!(userAnswer?.isMarkedForReview ?: false))
                }
            ) {
                Checkbox(
                    checked = userAnswer?.isMarkedForReview ?: false,
                    onCheckedChange = onMarkForReview,
                    colors = CheckboxDefaults.colors(
                        checkedColor = StatOrange,
                        uncheckedColor = TextSecondary
                    )
                )
                Text(
                    text = "Mark for Review",
                    color = TextPrimary,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun OptionCard(
    option: String,
    optionId: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelected)
            .border(
                width = 2.dp,
                color = if (isSelected) GradientStart else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) GradientStart.copy(alpha = 0.1f) else CardBackground
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Option ID badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        color = if (isSelected) GradientStart else SurfaceDark,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = optionId,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Option text
            Text(
                text = option,
                fontSize = 15.sp,
                color = TextPrimary,
                lineHeight = 22.sp,
                modifier = Modifier.weight(1f)
            )

            // Selection indicator
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = GradientStart,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun QuestionGridDialog(
    questions: List<Question>,
    userAnswers: Map<String, UserAnswer>,
    currentQuestion: Int,
    onQuestionSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Question Navigator",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LegendItem(color = StatGreen, label = "Answered")
                    LegendItem(color = StatOrange, label = "Marked")
                    LegendItem(color = SurfaceDark, label = "Not Attempted")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Question grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(questions.size) { index ->
                        QuestionGridItem(
                            questionNumber = index + 1,
                            isAnswered = userAnswers[questions[index].q_id]?.selectedOption?.isNotEmpty() == true,
                            isMarked = userAnswers[questions[index].q_id]?.isMarkedForReview == true,
                            isCurrent = index == currentQuestion,
                            onClick = { onQuestionSelected(index) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GradientStart)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun QuestionGridItem(
    questionNumber: Int,
    isAnswered: Boolean,
    isMarked: Boolean,
    isCurrent: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .background(
                color = when {
                    isAnswered -> StatGreen
                    isMarked -> StatOrange
                    else -> SurfaceDark
                },
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isCurrent) 2.dp else 0.dp,
                color = if (isCurrent) TextPrimary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = questionNumber.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
fun SubmitConfirmationDialog(
    answeredCount: Int,
    totalCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Submit Test?", color = TextPrimary, fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                Text(
                    "You have answered $answeredCount out of $totalCount questions.",
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (answeredCount < totalCount) {
                    Text(
                        "${totalCount - answeredCount} questions are unanswered.",
                        color = StatOrange
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    "Are you sure you want to submit?",
                    color = TextSecondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = GradientStart)
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Continue Test", color = TextPrimary)
            }
        },
        containerColor = CardBackground
    )
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%02d:%02d", minutes, seconds)
}

private fun loadQuestions(
    context: android.content.Context,
    mode: String,
    subject: String?,
    unit: String?,
    gsPaper: String?
): List<Question> {
    val allQuestions = when (mode) {
        "subject" -> {
            subject?.let { QuestionLoaderHelper.loadQuestionsFromFolder(context, it) } ?: emptyList()
        }
        "unit" -> {
            subject?.let {
                val questions = QuestionLoaderHelper.loadQuestionsFromFolder(context, it)
                unit?.let { u -> QuestionLoaderHelper.filterQuestionsByUnit(questions, u) } ?: questions
            } ?: emptyList()
        }
        "gs_paper" -> {
            gsPaper?.let { QuestionLoaderHelper.getQuestionsForGSPaper(context, it) } ?: emptyList()
        }
        else -> emptyList()
    }

    // Return exactly 15 random questions
    return QuestionLoaderHelper.getRandomQuestions(allQuestions, 15)
}
