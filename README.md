# UPSC Prep - Authentication System

A complete Android application for UPSC civil services study management with Firebase Authentication and Firestore integration.

## Features Implemented

✅ **User Authentication**
- Login with email and password
- Sign up with name, email, and password
- Password validation (minimum 6 characters)
- Email format validation
- Logout functionality

✅ **MVVM Architecture**
- Clean separation of concerns
- Repository pattern for data layer
- ViewModel for business logic
- LiveData for reactive UI updates
- ViewBinding for type-safe view access

✅ **Firebase Integration**
- Firebase Authentication for user management
- Firestore for storing user data
- Real-time data synchronization

✅ **Material Design UI**
- Material 3 components
- Modern and intuitive interface
- Responsive layouts
- Progress indicators for async operations

## Project Structure

```
app/src/main/java/com/example/upscprep/
├── data/
│   ├── model/
│   │   └── User.kt                    # User data model
│   └── repository/
│       └── AuthRepository.kt          # Authentication repository
├── ui/
│   ├── auth/
│   │   ├── LoginActivity.kt           # Login screen
│   │   ├── LoginViewModel.kt          # Shared ViewModel for auth
│   │   └── SignupActivity.kt          # Sign up screen
│   └── main/
│       └── MainActivity.kt            # Home screen after login
└── MainActivity.kt (original)         # Moved to ui.main package

app/src/main/res/layout/
├── activity_login.xml                 # Login screen layout
├── activity_signup.xml                # Sign up screen layout
└── activity_main.xml                  # Main screen layout
```

## Setup Instructions

### 1. Firebase Setup

**IMPORTANT:** The app includes a placeholder `google-services.json` file. You MUST replace it with your actual Firebase configuration file.

#### Steps to get your google-services.json:

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select existing project "UPSC Prep"
3. Add an Android app:
   - Package name: `com.example.upscprep`
   - App nickname: UPSC Prep
   - SHA-1: (Optional for Authentication)
4. Download the `google-services.json` file
5. Replace the file at: `app/google-services.json`

#### Enable Firebase Services:

1. **Authentication:**
   - In Firebase Console, go to Authentication
   - Click "Get Started"
   - Enable "Email/Password" sign-in method

2. **Firestore:**
   - In Firebase Console, go to Firestore Database
   - Click "Create Database"
   - Start in production mode (or test mode for development)
   - Choose your location
   - The app will create a "users" collection automatically

#### Firestore Security Rules (Development):

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection - authenticated users can read their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

#### Firestore Security Rules (Production):

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read: if request.auth != null && request.auth.uid == userId;
      allow create: if request.auth != null && request.auth.uid == userId;
      allow update: if request.auth != null && request.auth.uid == userId;
      allow delete: if false; // Don't allow deletion
    }
  }
}
```

### 2. Build the Project

After setting up Firebase, sync and build the project:

```bash
# Windows PowerShell
cd C:\Users\ramin\AndroidStudioProjects\UPSCPrep
.\gradlew clean build
```

Or use Android Studio:
- File → Sync Project with Gradle Files
- Build → Rebuild Project

### 3. Run the Application

- Connect an Android device or start an emulator
- Click Run in Android Studio
- Or use: `.\gradlew installDebug`

## Dependencies Added

The following dependencies have been configured in `build.gradle.kts`:

```kotlin
// Firebase (using BOM for version management)
implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")

// ViewModel and LiveData
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
implementation("androidx.activity:activity-ktx:1.8.2")

// Material Design
implementation("com.google.android.material:material:1.11.0")
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("androidx.constraintlayout:constraintlayout:2.1.4")
```

## User Flow

1. **App Launch** → Login Screen
2. **New User** → Click "Sign Up" → Fill details → Account created → Return to Login
3. **Login** → Enter credentials → Main Screen
4. **Main Screen** → View welcome message → Logout button
5. **Logout** → Return to Login Screen

## Validation Rules

### Login:
- Email cannot be empty
- Password cannot be empty

### Sign Up:
- Name cannot be empty
- Email cannot be empty
- Password cannot be empty
- Password must be at least 6 characters
- Passwords must match
- Email must be unique (Firebase handles this)

## Data Model

### User Document in Firestore

Collection: `users`
Document ID: `{user_uid}`

```json
{
  "uid": "firebase_user_uid",
  "name": "User Full Name",
  "email": "user@example.com",
  "registrationDate": Timestamp
}
```

## Error Handling

The app includes comprehensive error handling:
- Network errors
- Invalid credentials
- Duplicate email
- Firebase errors
- Form validation errors

All errors are displayed to users via Toast messages.

## Testing the App

### Test User Creation:

1. Run the app
2. Click "Sign Up"
3. Enter:
   - Name: Test User
   - Email: test@example.com
   - Password: test123
   - Confirm Password: test123
4. Click "Sign Up"
5. Account created → Returns to Login
6. Login with test@example.com / test123
7. Should see Main Screen with "Hello Test User!"

### Test Validation:

1. Try logging in with empty fields
2. Try signing up with passwords that don't match
3. Try signing up with password < 6 characters
4. Try signing up with existing email

## Key Features

### State Management
- Uses sealed classes for type-safe state management
- Three states: Loading, Success, Error
- Reactive UI updates with LiveData observers

### Repository Pattern
- Single source of truth for authentication
- Suspend functions for async operations
- Result wrapper for success/failure handling

### ViewBinding
- No findViewById() calls
- Type-safe view access
- Null-safe by design

### Material Design 3
- Modern UI components
- Outlined text fields
- Material buttons
- Proper spacing and typography

## Troubleshooting

### Issue: "Unresolved reference" errors

**Solution:** Sync Gradle files
```bash
.\gradlew --stop
.\gradlew clean build
```

### Issue: "google-services.json missing" error

**Solution:** Replace the placeholder file with your actual Firebase configuration

### Issue: "FirebaseApp not initialized"

**Solution:** Ensure google-services.json is in the `app/` folder and sync Gradle

### Issue: Authentication fails

**Solution:** 
1. Check Firebase console for enabled Email/Password authentication
2. Verify internet connection
3. Check Firestore security rules

### Issue: ViewBinding not generated

**Solution:**
1. Verify `viewBinding = true` in build.gradle.kts
2. Sync Gradle files
3. Rebuild project

## Next Steps / Future Enhancements

- [ ] Add password reset functionality
- [ ] Add email verification
- [ ] Add profile picture upload
- [ ] Add study progress tracking
- [ ] Add syllabus management
- [ ] Add daily goals and reminders
- [ ] Add study statistics and analytics
- [ ] Add offline support
- [ ] Add dark mode
- [ ] Add multi-language support

## Security Considerations

1. **Password Storage:** Firebase handles password hashing and storage securely
2. **Authentication State:** Always check auth state before accessing protected resources
3. **Firestore Rules:** Implement proper security rules in production
4. **API Keys:** The google-services.json file contains API keys - don't commit to public repos
5. **Data Validation:** Always validate user input on both client and server side

## License

This is a private project for UPSC preparation.

## Support

For issues or questions, please check:
1. Firebase Console for backend issues
2. Android Logcat for runtime errors
3. Gradle build output for compilation errors

---

**Last Updated:** November 15, 2025
**Minimum SDK:** 24 (Android 7.0)
**Target SDK:** 36
**Kotlin Version:** 2.0.21
**Gradle Version:** 8.13.1

