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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.upscprep.ui.assignments.components.TestConfigBottomSheet
import com.example.upscprep.ui.assignments.components.TestConfiguration
import com.example.upscprep.ui.theme.*
import com.example.upscprep.utils.QuestionLoaderHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UnitInfo(
    val name: String,
    val questionCount: Int
)

class UnitSelectionActivity : ComponentActivity() {

    private val subject by lazy { intent.getStringExtra("subject") ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Respect saved app theme; don't force darkTheme here
            UPSCPrepTheme() {
                var selectedUnit by remember { mutableStateOf<String?>(null) }
                var showConfigDialog by remember { mutableStateOf(false) }

                UnitSelectionScreen(
                    subject = subject,
                    onUnitSelected = { unitName ->
                        selectedUnit = unitName
                        showConfigDialog = true
                    },
                    onBack = { finish() }
                )

                // Test Configuration Dialog
                if (showConfigDialog && selectedUnit != null) {
                    TestConfigBottomSheet(
                        onConfigSelected = { config ->
                            showConfigDialog = false
                            startQuizWithConfig(selectedUnit!!, config)
                        },
                        onDismiss = {
                            showConfigDialog = false
                            selectedUnit = null
                        }
                    )
                }
            }
        }
    }

    private fun startQuizWithConfig(unitName: String, config: TestConfiguration) {
        val intent = Intent(this, QuizActivity::class.java)
        intent.putExtra("subject", subject)
        intent.putExtra("unit", unitName)
        intent.putExtra("mode", "unit")
        intent.putExtra("num_questions", config.numQuestions)
        intent.putExtra("duration_minutes", config.durationMinutes)
        startActivity(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitSelectionScreen(
    subject: String,
    onUnitSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

    var units by remember { mutableStateOf<List<UnitInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(subject) {
        scope.launch {
            units = withContext(Dispatchers.IO) {
                loadUnits(context, subject)
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Select Topic",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 18.sp
                        )
                        Text(
                            text = subject.replaceFirstChar { it.uppercase() },
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
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
        } else if (units.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "No units",
                        tint = TextSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No topics available",
                        fontSize = 16.sp,
                        color = TextSecondary
                    )
                }
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
                items(units) { unit ->
                    UnitCard(
                        unit = unit,
                        onClick = { onUnitSelected(unit.name) }
                    )
                }
            }
        }
    }
}

@Composable
fun UnitCard(
    unit: UnitInfo,
    onClick: () -> Unit
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
            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = unit.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${unit.questionCount} questions",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            // Start button
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GradientStart
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Start", fontSize = 14.sp)
            }
        }
    }
}

private fun loadUnits(context: android.content.Context, subject: String): List<UnitInfo> {
    val questions = QuestionLoaderHelper.loadQuestionsFromFolder(context, subject)
    val uniqueUnits = QuestionLoaderHelper.getUniqueUnits(questions)

    return uniqueUnits.map { unitName ->
        val filtered = QuestionLoaderHelper.filterQuestionsByUnit(questions, unitName)
        UnitInfo(
            name = unitName,
            questionCount = filtered.size
        )
    }.filter { it.questionCount > 0 }
}
