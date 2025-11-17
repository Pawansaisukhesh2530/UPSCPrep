package com.example.upscprep.ui.assignments.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.upscprep.ui.theme.*
import com.example.upscprep.utils.QuestionLoaderHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SubjectInfo(
    val name: String,
    val displayName: String,
    val icon: ImageVector,
    val color: Color,
    val questionCount: Int
)

class SubjectSelectionActivity : ComponentActivity() {

    private val mode by lazy { intent.getStringExtra("mode") ?: "subject" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UPSCPrepTheme() {
                SubjectSelectionScreen(
                    mode = mode,
                    onSubjectSelected = { subjectName ->
                        handleSubjectSelection(subjectName)
                    },
                    onBack = { finish() }
                )
            }
        }
    }

    private fun handleSubjectSelection(subjectName: String) {
        if (mode == "unit") {
            // Navigate to Unit Selection
            val intent = Intent(this, UnitSelectionActivity::class.java)
            intent.putExtra("subject", subjectName)
            startActivity(intent)
        } else {
            // Navigate directly to Quiz with all questions from subject
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("subject", subjectName)
            intent.putExtra("mode", "subject")
            startActivity(intent)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectSelectionScreen(
    mode: String,
    onSubjectSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var subjects by remember { mutableStateOf<List<SubjectInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            subjects = withContext(Dispatchers.IO) {
                loadSubjects(context)
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (mode == "unit") "Choose Subject for Topics" else "Choose Subject",
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GradientStart)
            }
        } else {
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(subjects) { subject ->
                    SubjectCard(
                        subject = subject,
                        onClick = { onSubjectSelected(subject.name) }
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectCard(
    subject: SubjectInfo,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    .size(56.dp)
                    .background(
                        color = subject.color.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = subject.icon,
                    contentDescription = subject.displayName,
                    tint = subject.color,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subject.displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${subject.questionCount} questions available",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            // Arrow
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Select",
                tint = TextSecondary
            )
        }
    }
}

private fun loadSubjects(context: android.content.Context): List<SubjectInfo> {
    val subjects = listOf(
        SubjectInfo(
            name = "history",
            displayName = "History",
            icon = Icons.Default.DateRange,
            color = Color(0xFF4CAF50),
            questionCount = QuestionLoaderHelper.getQuestionCountForSubject(context, "history")
        ),
        SubjectInfo(
            name = "polity",
            displayName = "Polity",
            icon = Icons.Default.CheckCircle,
            color = Color(0xFF2196F3),
            questionCount = QuestionLoaderHelper.getQuestionCountForSubject(context, "polity")
        ),
        SubjectInfo(
            name = "economy",
            displayName = "Economy",
            icon = Icons.Default.Star,
            color = Color(0xFFFF9800),
            questionCount = QuestionLoaderHelper.getQuestionCountForSubject(context, "economy")
        ),
        SubjectInfo(
            name = "geography",
            displayName = "Geography",
            icon = Icons.Default.Place,
            color = Color(0xFF9C27B0),
            questionCount = QuestionLoaderHelper.getQuestionCountForSubject(context, "geography")
        ),
        SubjectInfo(
            name = "ethics",
            displayName = "Ethics",
            icon = Icons.Default.Favorite,
            color = Color(0xFFE91E63),
            questionCount = QuestionLoaderHelper.getQuestionCountForSubject(context, "ethics")
        )
    )

    return subjects.filter { it.questionCount > 0 }
}
