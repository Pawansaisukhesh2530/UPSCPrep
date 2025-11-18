package com.example.upscprep.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.upscprep.ui.theme.*
import com.example.upscprep.utils.ThemeManager

/**
 * Settings Screen - Allows users to customize app theme and profile settings
 */
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val currentTheme by viewModel.currentTheme.collectAsState()
    val currentUsername by viewModel.currentUsername.collectAsState()
    val saveStatus by viewModel.saveStatus.collectAsState()

    var usernameInput by remember { mutableStateOf(currentUsername) }
    var showResetDialog by remember { mutableStateOf(false) }

    // Update input when username changes from ViewModel
    LaunchedEffect(currentUsername) {
        usernameInput = currentUsername
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Text(
                text = "Settings",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Theme Section
            SettingsSection(title = "Appearance") {
                ThemeSelector(
                    currentTheme = currentTheme,
                    onThemeSelected = { theme ->
                        viewModel.changeTheme(theme)
                    }
                )
            }

            // Profile Section
            SettingsSection(title = "Profile") {
                UsernameEditor(
                    username = usernameInput,
                    onUsernameChange = { usernameInput = it },
                    onSave = {
                        viewModel.updateUsername(usernameInput)
                    }
                )
            }

            // Additional Settings Section (Placeholder for future)
            SettingsSection(title = "Additional Settings") {
                SettingsPlaceholderItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    subtitle = "Coming soon"
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsPlaceholderItem(
                    icon = Icons.Default.Lock,
                    title = "Privacy",
                    subtitle = "Coming soon"
                )
                Spacer(modifier = Modifier.height(12.dp))
                SettingsPlaceholderItem(
                    icon = Icons.Default.Info,
                    title = "Feedback",
                    subtitle = "Coming soon"
                )
            }

            // Reset Button
            Button(
                onClick = { showResetDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentCoral.copy(alpha = 0.2f),
                    contentColor = AccentCoral
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset to Defaults", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Save Status Snackbar
        when (val status = saveStatus) {
            is SettingsViewModel.SaveStatus.Success -> {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    containerColor = StatGreen,
                    contentColor = Color.White
                ) {
                    Text(status.message)
                }
            }
            is SettingsViewModel.SaveStatus.Error -> {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    containerColor = AccentCoral,
                    contentColor = Color.White
                ) {
                    Text(status.message)
                }
            }
            else -> {}
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Settings") },
            text = { Text("Are you sure you want to reset all settings to defaults?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetToDefaults()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Settings Section Container
 */
@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * Theme Selector Component
 */
@Composable
fun ThemeSelector(
    currentTheme: ThemeManager.ThemeMode,
    onThemeSelected: (ThemeManager.ThemeMode) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ThemeOption(
            icon = Icons.Default.Star,
            label = "Light Mode",
            isSelected = currentTheme == ThemeManager.ThemeMode.LIGHT,
            onClick = { onThemeSelected(ThemeManager.ThemeMode.LIGHT) }
        )
        ThemeOption(
            icon = Icons.Default.Clear,
            label = "Dark Mode",
            isSelected = currentTheme == ThemeManager.ThemeMode.DARK,
            onClick = { onThemeSelected(ThemeManager.ThemeMode.DARK) }
        )
        ThemeOption(
            icon = Icons.Default.Phone,
            label = "System Default",
            isSelected = currentTheme == ThemeManager.ThemeMode.SYSTEM_DEFAULT,
            onClick = { onThemeSelected(ThemeManager.ThemeMode.SYSTEM_DEFAULT) }
        )
    }
}

/**
 * Theme Option Item
 */
@Composable
fun ThemeOption(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) GradientStart.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) GradientStart else TextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            color = if (isSelected) TextPrimary else TextSecondary,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = GradientStart,
                unselectedColor = TextSecondary
            )
        )
    }
}

/**
 * Username Editor Component
 */
@Composable
fun UsernameEditor(
    username: String,
    onUsernameChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Username",
                    tint = GradientStart
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GradientStart,
                unfocusedBorderColor = TextSecondary,
                focusedLabelColor = GradientStart,
                unfocusedLabelColor = TextSecondary,
                cursorColor = GradientStart
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GradientStart
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Save",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Username", fontSize = 16.sp)
        }
    }
}

/**
 * Placeholder Settings Item (for future features)
 */
@Composable
fun SettingsPlaceholderItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = TextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary.copy(alpha = 0.6f)
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Navigate",
            tint = TextSecondary.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
    }
}
