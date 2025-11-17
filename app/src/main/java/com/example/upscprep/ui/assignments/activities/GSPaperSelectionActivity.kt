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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.upscprep.ui.theme.*
import com.example.upscprep.utils.QuestionLoaderHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class GSPaperInfo(
    val paper: String,
    val title: String,
    val subjects: String,
    val color: Color,
    var questionCount: Int = 0
)

class GSPaperSelectionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UPSCPrepTheme(darkTheme = true) {
                GSPaperSelectionScreen(
                    onPaperSelected = { paper ->
                        val intent = Intent(this, QuizActivity::class.java)
                        intent.putExtra("gs_paper", paper)
                        intent.putExtra("mode", "gs_paper")
                        startActivity(intent)
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GSPaperSelectionScreen(
    onPaperSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var papers by remember { mutableStateOf<List<GSPaperInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch {
            papers = withContext(Dispatchers.IO) {
                loadGSPapers(context)
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select GS Paper",
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(papers) { paper ->
                    GSPaperCard(
                        paper = paper,
                        onClick = { onPaperSelected(paper.paper) }
                    )
                }
            }
        }
    }
}

@Composable
fun GSPaperCard(
    paper: GSPaperInfo,
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
                Text(
                    text = paper.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = paper.color
                )
                Box(
                    modifier = Modifier
                        .background(
                            color = paper.color.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = paper.paper,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = paper.color
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Subjects: ${paper.subjects}",
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${paper.questionCount} questions available",
                    fontSize = 14.sp,
                    color = TextPrimary
                )

                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = paper.color
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Start Test")
                }
            }
        }
    }
}

private fun loadGSPapers(context: android.content.Context): List<GSPaperInfo> {
    return listOf(
        GSPaperInfo(
            paper = "GS I",
            title = "General Studies Paper I",
            subjects = "History, Geography",
            color = StatGreen,
            questionCount = QuestionLoaderHelper.getQuestionsForGSPaper(context, "GS I").size
        ),
        GSPaperInfo(
            paper = "GS II",
            title = "General Studies Paper II",
            subjects = "Polity, Governance",
            color = StatBlue,
            questionCount = QuestionLoaderHelper.getQuestionsForGSPaper(context, "GS II").size
        ),
        GSPaperInfo(
            paper = "GS III",
            title = "General Studies Paper III",
            subjects = "Economy",
            color = StatOrange,
            questionCount = QuestionLoaderHelper.getQuestionsForGSPaper(context, "GS III").size
        ),
        GSPaperInfo(
            paper = "GS IV",
            title = "General Studies Paper IV",
            subjects = "Ethics, Integrity",
            color = StatPurple,
            questionCount = QuestionLoaderHelper.getQuestionsForGSPaper(context, "GS IV").size
        )
    )
}

