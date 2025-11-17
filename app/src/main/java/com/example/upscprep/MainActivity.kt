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
import com.example.upscprep.data.repository.AuthRepository
import com.example.upscprep.ui.auth.LoginActivity
import com.example.upscprep.ui.dashboard.DashboardScreen
import com.example.upscprep.ui.dashboard.DashboardViewModel
import com.example.upscprep.ui.subjects.SubjectsScreen
import com.example.upscprep.ui.theme.UPSCPrepTheme
import com.example.upscprep.ui.topics.TopicsScreen

/**
 * Main Activity - Entry point for Compose-based screens
 * Handles navigation between Dashboard, Subjects, and Topics screens
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
        authRepository.logout()

        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}

/**
 * Main navigation composable
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
                    navController.navigate("topics/$subjectName")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "topics/{subjectName}",
            arguments = listOf(
                navArgument("subjectName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val subjectName = backStackEntry.arguments?.getString("subjectName") ?: ""
            TopicsScreen(
                subjectName = subjectName,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

