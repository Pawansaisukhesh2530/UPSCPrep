package com.example.upscprep.ui.assignments.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.upscprep.data.database.AppDatabase
import com.example.upscprep.data.model.Question
import com.example.upscprep.data.model.TestAttempt
import com.example.upscprep.data.model.UserAnswer
import com.example.upscprep.ui.theme.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val questions = intent.getParcelableArrayListExtra<Question>("questions") ?: emptyList()
        val answersJson = intent.getStringExtra("answers_json") ?: "{}"
        val answers: Map<String, UserAnswer> = Gson().fromJson(
            answersJson,
            object : TypeToken<Map<String, UserAnswer>>() {}.type
        )
        val timeTaken = intent.getLongExtra("time_taken", 0L)
        val mode = intent.getStringExtra("mode") ?: ""
        val subject = intent.getStringExtra("subject")
        val unit = intent.getStringExtra("unit")
        val gsPaper = intent.getStringExtra("gs_paper")

        setContent {
            UPSCPrepTheme(darkTheme = true) {
                ResultScreen(
                    questions = questions,
                    answers = answers,
                    timeTaken = timeTaken,
                    mode = mode,
                    subject = subject,
                    unit = unit,
                    gsPaper = gsPaper,
                    onViewSolutions = {
                        val intent = Intent(this, SolutionReviewActivity::class.java)
                        intent.putParcelableArrayListExtra("questions", ArrayList(questions))
                        intent.putExtra("answers_json", answersJson)
                        startActivity(intent)
                    },
                    onRetakeTest = {
                        finish()
                        // Navigate back to quiz with same parameters
                        val intent = Intent(this, QuizActivity::class.java)
                        intent.putExtra("mode", mode)
                        intent.putExtra("subject", subject)
                        intent.putExtra("unit", unit)
                        intent.putExtra("gs_paper", gsPaper)
                        startActivity(intent)
                    },
                    onHome = {
                        finishAffinity()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    questions: List<Question>,
    answers: Map<String, UserAnswer>,
    timeTaken: Long,
    mode: String,
    subject: String?,
    unit: String?,
    gsPaper: String?,
    onViewSolutions: () -> Unit,
    onRetakeTest: () -> Unit,
    onHome: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    // Calculate results
    val result = remember {
        calculateResult(questions, answers)
    }

    // Save to database
    LaunchedEffect(Unit) {
        scope.launch {
            withContext(Dispatchers.IO) {
                saveTestAttempt(
                    context = context,
                    questions = questions,
                    answers = answers,
                    result = result,
                    timeTaken = timeTaken,
                    mode = mode,
                    subject = subject,
                    unit = unit,
                    gsPaper = gsPaper
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Test Result",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
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
            // Score Card
            item {
                ScoreCard(result = result, timeTaken = timeTaken)
            }

            // Statistics Card
            item {
                StatisticsCard(result = result, totalQuestions = questions.size)
            }

            // Performance Chart
            item {
                PerformanceChart(result = result)
            }

            // Action Buttons
            item {
                ActionButtons(
                    onViewSolutions = onViewSolutions,
                    onRetakeTest = onRetakeTest,
                    onHome = onHome
                )
            }
        }
    }
}

@Composable
fun ScoreCard(result: ResultData, timeTaken: Long) {
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Score circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        ),
                        shape = RoundedCornerShape(60.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${result.score}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "/ 30",
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${result.percentage.toInt()}%",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    result.percentage >= 75 -> StatGreen
                    result.percentage >= 50 -> StatOrange
                    else -> Color.Red
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Time Taken: ${formatTime(timeTaken)}",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun StatisticsCard(result: ResultData, totalQuestions: Int) {
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
                text = "Statistics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn(
                    label = "Correct",
                    value = result.correctAnswers.toString(),
                    color = StatGreen
                )
                StatColumn(
                    label = "Wrong",
                    value = result.wrongAnswers.toString(),
                    color = Color.Red
                )
                StatColumn(
                    label = "Skipped",
                    value = result.skippedAnswers.toString(),
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bars
            ResultProgressBar(
                label = "Correct",
                count = result.correctAnswers,
                total = totalQuestions,
                color = StatGreen
            )
            Spacer(modifier = Modifier.height(8.dp))
            ResultProgressBar(
                label = "Wrong",
                count = result.wrongAnswers,
                total = totalQuestions,
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(8.dp))
            ResultProgressBar(
                label = "Skipped",
                count = result.skippedAnswers,
                total = totalQuestions,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun StatColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun ResultProgressBar(label: String, count: Int, total: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = TextPrimary
            )
            Text(
                text = "$count/$total",
                fontSize = 14.sp,
                color = TextSecondary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { count.toFloat() / total.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = color,
            trackColor = SurfaceDark,
        )
    }
}

@Composable
fun PerformanceChart(result: ResultData) {
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
                text = "Performance Summary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Simple bar chart representation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                BarColumn(
                    label = "Correct",
                    height = result.correctAnswers * 10f,
                    color = StatGreen
                )
                BarColumn(
                    label = "Wrong",
                    height = result.wrongAnswers * 10f,
                    color = Color.Red
                )
                BarColumn(
                    label = "Skipped",
                    height = result.skippedAnswers * 10f,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun BarColumn(label: String, height: Float, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(height.dp.coerceAtLeast(20.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(color, color.copy(alpha = 0.5f))
                    ),
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary
        )
    }
}

@Composable
fun ActionButtons(
    onViewSolutions: () -> Unit,
    onRetakeTest: () -> Unit,
    onHome: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onViewSolutions,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = GradientStart),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.List, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("View Solutions", fontSize = 16.sp)
        }

        OutlinedButton(
            onClick = onRetakeTest,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = TextPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retake Test", fontSize = 16.sp, color = TextPrimary)
        }

        OutlinedButton(
            onClick = onHome,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Home, contentDescription = null, tint = TextPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Home", fontSize = 16.sp, color = TextPrimary)
        }
    }
}

data class ResultData(
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val skippedAnswers: Int,
    val score: Double,
    val percentage: Double
)

private fun calculateResult(questions: List<Question>, answers: Map<String, UserAnswer>): ResultData {
    var correct = 0
    var wrong = 0
    var skipped = 0

    questions.forEach { question ->
        val userAnswer = answers[question.q_id]
        when {
            userAnswer == null || userAnswer.selectedOption.isEmpty() -> skipped++
            userAnswer.selectedOption == question.correct_answer -> correct++
            else -> wrong++
        }
    }

    val score = (correct * 2.0) - (wrong * 0.66)
    val percentage = (score / 30.0) * 100.0

    return ResultData(
        correctAnswers = correct,
        wrongAnswers = wrong,
        skippedAnswers = skipped,
        score = score.coerceAtLeast(0.0),
        percentage = percentage.coerceAtLeast(0.0)
    )
}

private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%dm %ds", minutes, seconds)
}

private suspend fun saveTestAttempt(
    context: android.content.Context,
    questions: List<Question>,
    answers: Map<String, UserAnswer>,
    result: ResultData,
    timeTaken: Long,
    mode: String,
    subject: String?,
    unit: String?,
    gsPaper: String?
) {
    val database = AppDatabase.getDatabase(context)
    val attempt = TestAttempt(
        testType = mode,
        subjectName = subject,
        unitName = unit,
        gsPaper = gsPaper,
        attemptDate = System.currentTimeMillis(),
        questionsJson = Gson().toJson(questions),
        answersJson = Gson().toJson(answers),
        correctCount = result.correctAnswers,
        wrongCount = result.wrongAnswers,
        skippedCount = result.skippedAnswers,
        score = result.score,
        percentage = result.percentage,
        timeTakenSeconds = (timeTaken / 1000).toInt()
    )
    database.testAttemptDao().insertAttempt(attempt)
}

