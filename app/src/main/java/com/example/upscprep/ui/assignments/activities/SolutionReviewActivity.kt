package com.example.upscprep.ui.assignments.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.upscprep.data.model.Question
import com.example.upscprep.data.model.UserAnswer
import com.example.upscprep.ui.theme.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SolutionReviewActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val questions = intent.getParcelableArrayListExtra<Question>("questions") ?: emptyList()
        val answersJson = intent.getStringExtra("answers_json") ?: "{}"
        val answers: Map<String, UserAnswer> = Gson().fromJson(
            answersJson,
            object : TypeToken<Map<String, UserAnswer>>() {}.type
        )

        setContent {
            UPSCPrepTheme(darkTheme = true) {
                SolutionReviewScreen(
                    questions = questions,
                    answers = answers,
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolutionReviewScreen(
    questions: List<Question>,
    answers: Map<String, UserAnswer>,
    onBack: () -> Unit
) {
    var filterType by remember { mutableStateOf("all") }

    val filteredQuestions = remember(filterType, questions, answers) {
        when (filterType) {
            "correct" -> questions.filter { q ->
                answers[q.q_id]?.selectedOption == q.correct_answer
            }
            "wrong" -> questions.filter { q ->
                val answer = answers[q.q_id]
                answer != null && answer.selectedOption.isNotEmpty() && answer.selectedOption != q.correct_answer
            }
            "skipped" -> questions.filter { q ->
                answers[q.q_id]?.selectedOption?.isEmpty() != false
            }
            else -> questions
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Solutions",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(BackgroundDark, SurfaceDark, BackgroundDark)
                    )
                )
                .padding(paddingValues)
        ) {
            // Filter Tabs
            ScrollableTabRow(
                selectedTabIndex = when (filterType) {
                    "all" -> 0
                    "correct" -> 1
                    "wrong" -> 2
                    "skipped" -> 3
                    else -> 0
                },
                containerColor = SurfaceDark,
                contentColor = TextPrimary,
                edgePadding = 0.dp
            ) {
                Tab(
                    selected = filterType == "all",
                    onClick = { filterType = "all" },
                    text = { Text("All (${questions.size})") }
                )
                Tab(
                    selected = filterType == "correct",
                    onClick = { filterType = "correct" },
                    text = {
                        val count = questions.count { q ->
                            answers[q.q_id]?.selectedOption == q.correct_answer
                        }
                        Text("Correct ($count)")
                    }
                )
                Tab(
                    selected = filterType == "wrong",
                    onClick = { filterType = "wrong" },
                    text = {
                        val count = questions.count { q ->
                            val answer = answers[q.q_id]
                            answer != null && answer.selectedOption.isNotEmpty() && answer.selectedOption != q.correct_answer
                        }
                        Text("Wrong ($count)")
                    }
                )
                Tab(
                    selected = filterType == "skipped",
                    onClick = { filterType = "skipped" },
                    text = {
                        val count = questions.count { q ->
                            answers[q.q_id]?.selectedOption?.isEmpty() != false
                        }
                        Text("Skipped ($count)")
                    }
                )
            }

            // Solutions List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredQuestions) { question ->
                    val questionIndex = questions.indexOf(question)
                    SolutionCard(
                        question = question,
                        questionNumber = questionIndex + 1,
                        userAnswer = answers[question.q_id]
                    )
                }

                if (filteredQuestions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No questions in this category",
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SolutionCard(
    question: Question,
    questionNumber: Int,
    userAnswer: UserAnswer?
) {
    val isCorrect = userAnswer?.selectedOption == question.correct_answer
    val isAnswered = userAnswer?.selectedOption?.isNotEmpty() == true

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Question number
                Box(
                    modifier = Modifier
                        .background(
                            color = when {
                                !isAnswered -> TextSecondary.copy(alpha = 0.2f)
                                isCorrect -> StatGreen.copy(alpha = 0.2f)
                                else -> Color.Red.copy(alpha = 0.2f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Q$questionNumber",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            !isAnswered -> TextSecondary
                            isCorrect -> StatGreen
                            else -> Color.Red
                        }
                    )
                }

                // Status icon
                if (isAnswered) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = if (isCorrect) "Correct" else "Wrong",
                        tint = if (isCorrect) StatGreen else Color.Red,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Question text
            Text(
                text = question.question_text,
                fontSize = 16.sp,
                color = TextPrimary,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Options
            question.options.forEach { option ->
                val isUserAnswer = userAnswer?.selectedOption == option.option_id
                val isCorrectAnswer = question.correct_answer == option.option_id

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(
                            color = when {
                                isCorrectAnswer -> StatGreen.copy(alpha = 0.15f)
                                isUserAnswer && !isCorrectAnswer -> Color.Red.copy(alpha = 0.15f)
                                else -> Color.Transparent
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = when {
                                isCorrectAnswer -> StatGreen
                                isUserAnswer && !isCorrectAnswer -> Color.Red
                                else -> SurfaceDark
                            },
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Option ID
                        Text(
                            text = "${option.option_id}.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isCorrectAnswer -> StatGreen
                                isUserAnswer && !isCorrectAnswer -> Color.Red
                                else -> TextPrimary
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Option text
                        Text(
                            text = option.option_text,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )

                        // Icons
                        when {
                            isCorrectAnswer -> Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Correct",
                                tint = StatGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            isUserAnswer && !isCorrectAnswer -> Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Your wrong answer",
                                tint = Color.Red,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Marks info
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when {
                        !isAnswered -> "Not Attempted: 0 marks"
                        isCorrect -> "Correct: +${question.marks} marks"
                        else -> "Wrong: -${question.marks / 3} marks"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = when {
                        !isAnswered -> TextSecondary
                        isCorrect -> StatGreen
                        else -> Color.Red
                    }
                )

                Text(
                    text = "Difficulty: ${question.difficulty}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // Explanation
            if (question.explanation.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = SurfaceDark)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Explanation",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GradientStart
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = question.explanation,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

