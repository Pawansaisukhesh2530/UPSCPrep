package com.example.upscprep

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.upscprep.data.model.SubTopic
import com.example.upscprep.data.model.SyllabusSubject
import com.example.upscprep.data.model.Unit as SyllabusUnit
import com.example.upscprep.data.repository.AuthRepository
import com.example.upscprep.ui.auth.LoginActivity
import com.example.upscprep.ui.assignments.AssignmentsScreen
import com.example.upscprep.ui.dashboard.DashboardScreen
import com.example.upscprep.ui.dashboard.DashboardViewModel
import com.example.upscprep.ui.subjects.SubjectsScreen
import com.example.upscprep.ui.subtopics.SubTopicsScreen
import com.example.upscprep.ui.theme.UPSCPrepTheme
import com.example.upscprep.ui.trackingitems.TrackingItemsScreen
import com.example.upscprep.ui.units.UnitsScreen
import com.example.upscprep.utils.SecurePreferences

/**
 * Main Activity - Entry point for Compose-based screens
 * Hosts a BottomNavigation with Dashboard, Subjects, and Assignments tabs
 */
class MainActivity : ComponentActivity() {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get user name from intent
        val userName = intent.getStringExtra("USER_NAME") ?: "Aspirant"

        setContent {
            UPSCPrepTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation(
                        userName = userName,
                        viewModel = dashboardViewModel,
                        onLogout = { handleLogout() }
                    )
                }
            }
        }
    }

    /**
     * Handle user logout
     */
    private fun handleLogout() {
        // Clear saved credentials (Remember Me data)
        SecurePreferences.clearCredentials(this)

        // Logout from Firebase
        authRepository.logout()

        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}

/**
 * Bottom navigation tabs
 */
private enum class BottomTab(val route: String, val label: String) {
    DASHBOARD("dashboard", "Dashboard"),
    SUBJECTS("subjects", "Subjects"),
    ASSIGNMENTS("assignments", "Assignments")
}

/**
 * Main navigation composable using a BottomNavigation bar and simple tab state
 */
@Composable
fun MainNavigation(
    userName: String,
    viewModel: DashboardViewModel,
    onLogout: () -> Unit
) {
    val subjects by viewModel.subjects.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val repository = viewModel.getRepository()

    // Keep selection state
    var selectedTab by remember { mutableStateOf(BottomTab.DASHBOARD) }

    // Temporary holders for navigation to deep screens (Units/SubTopics/TrackingItems)
    var selectedSyllabusSubject by remember { mutableStateOf<SyllabusSubject?>(null) }
    var selectedUnit by remember { mutableStateOf<SyllabusUnit?>(null) }
    var selectedSubTopic by remember { mutableStateOf<SubTopic?>(null) }
    var showDeepScreen by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = selectedTab == BottomTab.DASHBOARD,
                    onClick = { selectedTab = BottomTab.DASHBOARD },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text(BottomTab.DASHBOARD.label) }
                )
                NavigationBarItem(
                    selected = selectedTab == BottomTab.SUBJECTS,
                    onClick = { selectedTab = BottomTab.SUBJECTS },
                    icon = { Icon(Icons.Default.Menu, contentDescription = "Subjects") },
                    label = { Text(BottomTab.SUBJECTS.label) }
                )
                NavigationBarItem(
                    selected = selectedTab == BottomTab.ASSIGNMENTS,
                    onClick = { selectedTab = BottomTab.ASSIGNMENTS },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Assignments") },
                    label = { Text(BottomTab.ASSIGNMENTS.label) }
                )
            }
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main content area — switch on selected tab
            when (selectedTab) {
                BottomTab.DASHBOARD -> {
                    DashboardScreen(
                        userName = userName,
                        stats = stats,
                        subjects = subjects,
                        onLogout = onLogout
                    )
                }

                BottomTab.SUBJECTS -> {
                    SubjectsScreen(
                        subjects = subjects,
                        onSubjectClick = { subjectName ->
                            selectedSyllabusSubject = repository.getSyllabusSubjectByName(subjectName)
                            // Navigate to Units using the deep-screen state
                            selectedUnit = null
                            selectedSubTopic = null
                            showDeepScreen = true
                        },
                        onNavigateBack = {
                            // No-op for tab back — deselect tab and go to dashboard
                            selectedTab = BottomTab.DASHBOARD
                        }
                    )
                }

                BottomTab.ASSIGNMENTS -> {
                    // Assignments placeholder implemented as a Compose-only screen
                    AssignmentsScreen()
                }
            }

            // If a deep screen is requested (from Subjects -> Units -> SubTopics -> TrackingItems)
            if (showDeepScreen && selectedSyllabusSubject != null) {
                UnitsScreen(
                    syllabusSubject = selectedSyllabusSubject!!,
                    onUnitClick = { unit ->
                        selectedUnit = unit
                        selectedSubTopic = null
                    },
                    onNavigateBack = {
                        // Close deep screens and stay on Subjects tab
                        selectedSyllabusSubject = null
                        selectedUnit = null
                        selectedSubTopic = null
                        showDeepScreen = false
                    }
                )

                // If a unit was selected, show SubTopics
                if (selectedUnit != null) {
                    SubTopicsScreen(
                        unit = selectedUnit!!,
                        subjectName = selectedSyllabusSubject!!.subject,
                        onSubTopicClick = { subTopic ->
                            selectedSubTopic = subTopic
                        },
                        onNavigateBack = {
                            selectedUnit = null
                        }
                    )
                }

                // If subtopic selected, show TrackingItems
                if (selectedSubTopic != null && selectedUnit != null) {
                    TrackingItemsScreen(
                        subTopic = selectedSubTopic!!,
                        unitName = selectedUnit!!.unit_name,
                        onNavigateBack = {
                            selectedSubTopic = null
                        }
                    )
                }
            }
        }
    }
}

