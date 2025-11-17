package com.example.upscprep

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.upscprep.data.model.SubTopic
import com.example.upscprep.data.model.SyllabusSubject
import com.example.upscprep.data.model.Unit as SyllabusUnit
import com.example.upscprep.data.repository.AuthRepository
import com.example.upscprep.ui.auth.LoginActivity
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
 * Handles navigation between Dashboard, Subjects, Units, SubTopics, and TrackingItems screens
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
 * Main navigation composable with complete navigation flow
 */
@Composable
fun MainNavigation(
    userName: String,
    viewModel: DashboardViewModel,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val subjects by viewModel.subjects.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val repository = viewModel.getRepository()

    // Store navigation data
    var selectedSyllabusSubject: SyllabusSubject? = null
    var selectedUnit: SyllabusUnit? = null
    var selectedSubTopic: SubTopic? = null

    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        // Dashboard Screen
        composable("dashboard") {
            DashboardScreen(
                userName = userName,
                stats = stats,
                subjects = subjects,
                onNavigateToSubjects = {
                    navController.navigate("subjects")
                },
                onLogout = onLogout
            )
        }

        // Subjects Screen
        composable("subjects") {
            SubjectsScreen(
                subjects = subjects,
                onSubjectClick = { subjectName ->
                    // Load full syllabus subject data
                    selectedSyllabusSubject = repository.getSyllabusSubjectByName(subjectName)
                    navController.navigate("units/$subjectName")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Units Screen (NEW)
        composable(
            route = "units/{subjectName}",
            arguments = listOf(
                navArgument("subjectName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val subjectName = backStackEntry.arguments?.getString("subjectName") ?: ""
            val syllabusSubject = selectedSyllabusSubject
                ?: repository.getSyllabusSubjectByName(subjectName)

            if (syllabusSubject != null) {
                UnitsScreen(
                    syllabusSubject = syllabusSubject,
                    onUnitClick = { unit ->
                        selectedUnit = unit
                        navController.navigate("subtopics/${subjectName}")
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // SubTopics Screen (NEW)
        composable(
            route = "subtopics/{subjectName}",
            arguments = listOf(
                navArgument("subjectName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val subjectName = backStackEntry.arguments?.getString("subjectName") ?: ""
            val unit = selectedUnit

            if (unit != null) {
                SubTopicsScreen(
                    unit = unit,
                    subjectName = subjectName,
                    onSubTopicClick = { subTopic ->
                        selectedSubTopic = subTopic
                        navController.navigate("trackingitems/${subjectName}")
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Tracking Items Screen (NEW - Leaf Level)
        composable(
            route = "trackingitems/{subjectName}",
            arguments = listOf(
                navArgument("subjectName") { type = NavType.StringType }
            )
        ) {
            val subTopic = selectedSubTopic
            val unit = selectedUnit

            if (subTopic != null && unit != null) {
                TrackingItemsScreen(
                    subTopic = subTopic,
                    unitName = unit.unit_name,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

