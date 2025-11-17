package com.example.upscprep

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.content.SharedPreferences
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
import kotlinx.coroutines.delay

/**
 * Main Activity - Entry point for Compose-based screens
 * Hosts a BottomNavigation with Dashboard, Subjects, and Assignments tabs
 *
 * Fixed Issues:
 * - Proper back button handling with exit confirmation
 * - Bottom navigation always responsive
 * - Proper state management for deep navigation
 * - Gesture handling throughout the app
 */
class MainActivity : ComponentActivity() {

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val authRepository = AuthRepository()
    private var backPressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply saved theme before setting content
        com.example.upscprep.utils.ThemeHelper.applySavedTheme(this)

        // Read username from SharedPreferences and observe changes so Settings updates reflect immediately
        val prefs = getSharedPreferences("upsc_settings", MODE_PRIVATE)
        val initialUserName = prefs.getString("username", null)
            ?: com.example.upscprep.utils.SecurePreferences.getSavedEmail(this)?.substringBefore("@")
            ?: intent.getStringExtra("USER_NAME") ?: "Aspirant"

        // Backing state observed by Compose
        var userNameState by mutableStateOf(initialUserName)

        // Listen for preference changes to update username in UI immediately
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "username") {
                userNameState = sharedPreferences.getString(key, "Aspirant") ?: "Aspirant"
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)

        setContent {
            // Use UPSCPrepTheme without forcing darkTheme so ThemeHelper controls appearance
            UPSCPrepTheme() {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation(
                        userName = userNameState,
                        viewModel = dashboardViewModel,
                        onLogout = { handleLogout() },
                        onExit = { handleBackPressed() }
                    )
                }
            }
        }

        // Ensure listener is unregistered when activity is destroyed
        lifecycle.addObserver(object : androidx.lifecycle.DefaultLifecycleObserver {
            override fun onDestroy(owner: androidx.lifecycle.LifecycleOwner) {
                try { prefs.unregisterOnSharedPreferenceChangeListener(listener) } catch (e: Exception) { /* ignore */ }
            }
        })
    }

    private fun handleBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finish()
        } else {
            android.widget.Toast.makeText(
                this,
                "Press back again to exit",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            backPressedTime = System.currentTimeMillis()
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
private enum class BottomTab(val label: String) {
    DASHBOARD("Dashboard"),
    SUBJECTS("Subjects"),
    ASSIGNMENTS("Assignments"),
    SETTINGS("Settings")
}

/**
 * Navigation state to track deep navigation screens
 */
private sealed class NavigationState {
    object MainTabs : NavigationState()
    data class SubjectDetails(val subject: SyllabusSubject) : NavigationState()
    data class UnitDetails(val subject: SyllabusSubject, val unit: SyllabusUnit) : NavigationState()
    data class SubTopicDetails(
        val subject: SyllabusSubject,
        val unit: SyllabusUnit,
        val subTopic: SubTopic
    ) : NavigationState()
}

/**
 * Main navigation composable with proper back handling and state management
 */
@Composable
fun MainNavigation(
    userName: String,
    viewModel: DashboardViewModel,
    onLogout: () -> Unit,
    onExit: () -> Unit
) {
    val subjects by viewModel.subjects.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val repository = viewModel.getRepository()

    // Bottom navigation tab state
    var selectedTab by remember { mutableStateOf(BottomTab.DASHBOARD) }

    // Deep navigation state
    var navigationState by remember { mutableStateOf<NavigationState>(NavigationState.MainTabs) }

    // Handle back button based on navigation state
    BackHandler(enabled = true) {
        when (navigationState) {
            is NavigationState.MainTabs -> {
                // On main tabs, handle based on selected tab
                when (selectedTab) {
                    BottomTab.DASHBOARD -> onExit()
                    else -> selectedTab = BottomTab.DASHBOARD
                }
            }
            is NavigationState.SubTopicDetails -> {
                // Go back to Unit details
                val state = navigationState as NavigationState.SubTopicDetails
                navigationState = NavigationState.UnitDetails(state.subject, state.unit)
            }
            is NavigationState.UnitDetails -> {
                // Go back to Subject details
                val state = navigationState as NavigationState.UnitDetails
                navigationState = NavigationState.SubjectDetails(state.subject)
            }
            is NavigationState.SubjectDetails -> {
                // Go back to main tabs
                navigationState = NavigationState.MainTabs
                selectedTab = BottomTab.SUBJECTS
            }
        }
    }

    Scaffold(
        bottomBar = {
            // Only show bottom navigation on main tabs
            if (navigationState is NavigationState.MainTabs) {
                NavigationBar(
                    tonalElevation = 8.dp,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        selected = selectedTab == BottomTab.DASHBOARD,
                        onClick = {
                            if (selectedTab != BottomTab.DASHBOARD) {
                                selectedTab = BottomTab.DASHBOARD
                                navigationState = NavigationState.MainTabs
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                        label = { Text(BottomTab.DASHBOARD.label) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == BottomTab.SUBJECTS,
                        onClick = {
                            if (selectedTab != BottomTab.SUBJECTS) {
                                selectedTab = BottomTab.SUBJECTS
                                navigationState = NavigationState.MainTabs
                            }
                        },
                        icon = { Icon(Icons.Default.Menu, contentDescription = "Subjects") },
                        label = { Text(BottomTab.SUBJECTS.label) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == BottomTab.ASSIGNMENTS,
                        onClick = {
                            if (selectedTab != BottomTab.ASSIGNMENTS) {
                                selectedTab = BottomTab.ASSIGNMENTS
                                navigationState = NavigationState.MainTabs
                            }
                        },
                        icon = { Icon(Icons.Default.Star, contentDescription = "Assignments") },
                        label = { Text(BottomTab.ASSIGNMENTS.label) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == BottomTab.SETTINGS,
                        onClick = {
                            if (selectedTab != BottomTab.SETTINGS) {
                                selectedTab = BottomTab.SETTINGS
                                navigationState = NavigationState.MainTabs
                            }
                        },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text(BottomTab.SETTINGS.label) }
                    )
                }
            }
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (navigationState) {
                is NavigationState.MainTabs -> {
                    // Show main tab content
                    when (selectedTab) {
                        BottomTab.DASHBOARD -> {
                            DashboardScreen(
                                userName = userName,
                                stats = stats,
                                subjects = subjects,
                                onLogout = onLogout,
                                viewModel = viewModel
                            )
                        }

                        BottomTab.SUBJECTS -> {
                            SubjectsScreen(
                                subjects = subjects,
                                onSubjectClick = { subjectName ->
                                    repository.getSyllabusSubjectByName(subjectName)?.let { subject ->
                                        navigationState = NavigationState.SubjectDetails(subject)
                                    }
                                },
                                onNavigateBack = {
                                    selectedTab = BottomTab.DASHBOARD
                                }
                            )
                        }

                        BottomTab.ASSIGNMENTS -> {
                            AssignmentsScreen()
                        }

                        BottomTab.SETTINGS -> {
                            com.example.upscprep.ui.settings.SettingsScreen()
                        }
                    }
                }

                is NavigationState.SubjectDetails -> {
                    val state = navigationState as NavigationState.SubjectDetails
                    UnitsScreen(
                        syllabusSubject = state.subject,
                        onUnitClick = { unit ->
                            navigationState = NavigationState.UnitDetails(state.subject, unit)
                        },
                        onNavigateBack = {
                            navigationState = NavigationState.MainTabs
                            selectedTab = BottomTab.SUBJECTS
                        }
                    )
                }

                is NavigationState.UnitDetails -> {
                    val state = navigationState as NavigationState.UnitDetails
                    SubTopicsScreen(
                        unit = state.unit,
                        subjectName = state.subject.subject,
                        onSubTopicClick = { subTopic ->
                            navigationState = NavigationState.SubTopicDetails(
                                state.subject,
                                state.unit,
                                subTopic
                            )
                        },
                        onNavigateBack = {
                            navigationState = NavigationState.SubjectDetails(state.subject)
                        }
                    )
                }

                is NavigationState.SubTopicDetails -> {
                    val state = navigationState as NavigationState.SubTopicDetails
                    TrackingItemsScreen(
                        subTopic = state.subTopic,
                        unitName = state.unit.unit_name,
                        onNavigateBack = {
                            navigationState = NavigationState.UnitDetails(state.subject, state.unit)
                        }
                    )
                }
            }
        }
    }
}
