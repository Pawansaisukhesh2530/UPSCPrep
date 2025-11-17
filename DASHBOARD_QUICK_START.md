# 🚀 Quick Start Guide - UPSC Study Manager Dashboard

## Running the App

### Prerequisites
- Android Studio (latest version recommended)
- JDK 11+
- Android SDK 24+
- Google services (Firebase already configured)

### Steps to Run

1. **Sync Gradle**
   ```bash
   ./gradlew build
   ```

2. **Run on Emulator/Device**
   - Open project in Android Studio
   - Select target device
   - Click Run ▶️

3. **Test Flow**
   - Login with your credentials
   - Dashboard opens automatically
   - Explore study statistics
   - Click "View All Subjects"
   - Click any subject (Topics coming soon)
   - Use back button to navigate
   - Logout from welcome card

## Navigation Structure

```
LoginActivity (XML-based)
    ↓ (After successful login)
MainActivity (Compose-based)
    ├── Dashboard Screen [/dashboard]
    │   └── View All Subjects Button
    │       ↓
    ├── Subjects Screen [/subjects]
    │   └── Click Subject Card
    │       ↓
    └── Topics Screen [/topics/{subjectName}]
```

## Key Files to Explore

### UI Screens
- `MainActivity.kt` - Navigation host
- `DashboardScreen.kt` - Home dashboard
- `SubjectsScreen.kt` - Subjects grid
- `TopicsScreen.kt` - Topics placeholder

### Data Layer
- `SubjectRepository.kt` - Data management
- `Subject.kt` - Subject model
- `StudyStats.kt` - Statistics model

### Theme
- `Color.kt` - Color palette
- `Theme.kt` - Material theme setup

## Customization

### Changing Colors
Edit `ui/theme/Color.kt`:
```kotlin
val GradientStart = Color(0xFF6B4CE6)  // Change purple
val GradientEnd = Color(0xFF4E9AF1)    // Change blue
```

### Adding New Subjects
Edit `SubjectRepository.kt`:
```kotlin
fun getAllSubjects(): List<Subject> {
    return listOf(
        Subject("Your Subject", 50, 25, SubjectRed),
        // Add more subjects
    )
}
```

### Updating Stats
Edit `SubjectRepository.kt`:
```kotlin
fun getStudyStats(): StudyStats {
    return StudyStats(
        weeklyStudyMinutes = 1245,  // Change values
        currentStreak = 12,
        // ...
    )
}
```

## Testing

### Manual Testing Checklist
- ✅ Login flow works
- ✅ Dashboard displays correctly
- ✅ All stat cards visible
- ✅ Progress bars render properly
- ✅ Navigation to subjects works
- ✅ Subject cards clickable
- ✅ Back navigation functional
- ✅ Logout works
- ✅ App returns to login screen

## Troubleshooting

### Common Issues

**1. Build Errors**
```bash
./gradlew clean build
```

**2. Compose Preview Not Working**
- Invalidate caches: File > Invalidate Caches
- Restart Android Studio

**3. Navigation Not Working**
- Check MainActivity route names match
- Verify NavHost startDestination

**4. Colors Not Applied**
- Ensure using UPSCPrepTheme wrapper
- Check darkTheme = true parameter

## Next Steps

### Immediate Tasks
1. Test on real device
2. Add Firebase data persistence
3. Implement Topics screen
4. Add loading states
5. Implement error handling

### Feature Development
1. Create topic management system
2. Add study timer functionality
3. Implement notes feature
4. Build quiz/practice module
5. Add analytics dashboard

## Code Style

Follow these conventions:
- Use Kotlin best practices
- Composables start with capital letter
- Use remember for state
- Prefer StateFlow over LiveData in Compose
- Keep composables small and focused
- Extract reusable components

## Resources

- **Compose Docs**: https://developer.android.com/jetpack/compose
- **Material 3**: https://m3.material.io/
- **Navigation**: https://developer.android.com/jetpack/compose/navigation

---

**Need Help?** Check DASHBOARD_IMPLEMENTATION.md for detailed documentation.

*Happy Coding! 💻*

