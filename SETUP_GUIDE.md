# UPSC Prep App - Complete Setup Guide

This guide will walk you through setting up the UPSC Prep authentication system step by step.

## 📋 Prerequisites

- Android Studio (latest version recommended)
- Android SDK 24 or higher
- Internet connection
- Google/Firebase account

## 🚀 Step-by-Step Setup

### Step 1: Verify Project Files

Ensure all the following files have been created:

**Data Layer:**
- ✅ `app/src/main/java/com/example/upscprep/data/model/User.kt`
- ✅ `app/src/main/java/com/example/upscprep/data/repository/AuthRepository.kt`

**UI Layer:**
- ✅ `app/src/main/java/com/example/upscprep/ui/auth/LoginActivity.kt`
- ✅ `app/src/main/java/com/example/upscprep/ui/auth/SignupActivity.kt`
- ✅ `app/src/main/java/com/example/upscprep/ui/auth/LoginViewModel.kt`
- ✅ `app/src/main/java/com/example/upscprep/ui/main/MainActivity.kt`

**Layouts:**
- ✅ `app/src/main/res/layout/activity_login.xml`
- ✅ `app/src/main/res/layout/activity_signup.xml`
- ✅ `app/src/main/res/layout/activity_main.xml`

**Configuration:**
- ✅ `app/build.gradle.kts` (updated)
- ✅ `build.gradle.kts` (updated)
- ✅ `gradle/libs.versions.toml` (updated)
- ✅ `app/src/main/AndroidManifest.xml` (updated)
- ⚠️ `app/google-services.json` (needs replacement)

### Step 2: Firebase Project Setup

#### 2.1 Create Firebase Project

1. Open your browser and go to: https://console.firebase.google.com/
2. Click **"Add project"** or **"Create a project"**
3. Enter project name: **"UPSC Prep"**
4. Click **Continue**
5. Disable Google Analytics (or enable if you want analytics)
6. Click **Create project**
7. Wait for project creation to complete
8. Click **Continue**

#### 2.2 Add Android App to Firebase

1. In Firebase Console, click the **Android icon** or **"Add app"**
2. Enter the following details:
   - **Android package name:** `com.example.upscprep`
   - **App nickname (optional):** UPSC Prep
   - **Debug signing certificate SHA-1 (optional):** Leave blank for now
3. Click **Register app**

#### 2.3 Download google-services.json

1. Click **Download google-services.json**
2. Save the file to your computer
3. **IMPORTANT:** Replace the placeholder file at:
   ```
   C:\Users\ramin\AndroidStudioProjects\UPSCPrep\app\google-services.json
   ```
4. Click **Next**
5. Click **Next** (Firebase SDK is already added)
6. Click **Continue to console**

#### 2.4 Enable Email/Password Authentication

1. In Firebase Console, click **Authentication** in the left sidebar
2. Click **Get started**
3. Click on the **Sign-in method** tab
4. Find **Email/Password** in the list
5. Click on it to expand
6. Toggle the **Enable** switch to ON
7. Click **Save**

#### 2.5 Create Firestore Database

1. In Firebase Console, click **Firestore Database** in the left sidebar
2. Click **Create database**
3. Select **Start in test mode** (for development)
   - Test mode allows read/write access for 30 days
   - We'll update rules later
4. Choose your **Cloud Firestore location** (select closest to you)
5. Click **Enable**
6. Wait for database creation

#### 2.6 Set Up Firestore Security Rules

1. In Firestore Database, click on the **Rules** tab
2. Replace the existing rules with:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow users to read and write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

3. Click **Publish**

### Step 3: Sync Gradle and Build Project

#### 3.1 In Android Studio:

1. Open the project in Android Studio
2. Wait for initial indexing to complete
3. Click **File → Sync Project with Gradle Files**
4. Wait for sync to complete (this may take a few minutes)

#### 3.2 Using Terminal:

Open PowerShell in the project directory and run:

```powershell
cd C:\Users\ramin\AndroidStudioProjects\UPSCPrep
.\gradlew --stop
.\gradlew clean build
```

Wait for the build to complete. You should see **BUILD SUCCESSFUL**.

### Step 4: Verify Setup

#### 4.1 Check for Build Errors

1. Open **Build** menu → **Rebuild Project**
2. Check the **Build** output panel at the bottom
3. All files should compile without errors

#### 4.2 Verify Dependencies

Check that all Firebase dependencies are downloaded:
- Firebase BOM 32.7.0
- Firebase Authentication
- Firebase Firestore

### Step 5: Run the Application

#### 5.1 Start Emulator or Connect Device

**Option A - Using Emulator:**
1. Open **Tools → Device Manager**
2. Create or start an Android emulator
3. Recommended: Pixel 5, Android 11 or higher

**Option B - Using Physical Device:**
1. Enable Developer Options on your device
2. Enable USB Debugging
3. Connect device via USB
4. Accept debugging permission on device

#### 5.2 Run the App

1. Select your device/emulator in the device dropdown
2. Click the **Run** button (green play icon)
3. Or press **Shift + F10**

The app should launch and show the **Login Screen**.

### Step 6: Test the Application

#### Test 1: Sign Up Flow

1. Click **"Don't have an account? Sign Up"**
2. Fill in the form:
   - **Full Name:** John Doe
   - **Email:** john.doe@example.com
   - **Password:** test123
   - **Confirm Password:** test123
3. Click **Sign Up**
4. You should see: **"Account created successfully!"**
5. You should return to the Login screen

#### Test 2: Login Flow

1. Enter credentials:
   - **Email:** john.doe@example.com
   - **Password:** test123
2. Click **Login**
3. You should see: **"Welcome, John Doe!"**
4. Main screen should appear showing: **"Hello John Doe!"**

#### Test 3: Logout

1. On the Main screen, click **Logout**
2. You should return to the Login screen

#### Test 4: Validation

**Test Empty Fields:**
- Try logging in without entering email/password
- You should see error messages

**Test Password Mismatch:**
- Go to Sign Up
- Enter different passwords in Password and Confirm Password
- Click Sign Up
- You should see: **"Passwords do not match"**

**Test Short Password:**
- Try password: "test"
- You should see: **"Password must be at least 6 characters"**

**Test Duplicate Email:**
- Try signing up with john.doe@example.com again
- You should see: **"Email already registered"**

### Step 7: Verify Firebase Data

#### 7.1 Check Authentication

1. Go to Firebase Console
2. Click **Authentication** → **Users** tab
3. You should see the user you created:
   - UID
   - Email: john.doe@example.com
   - Created date

#### 7.2 Check Firestore Data

1. Go to Firebase Console
2. Click **Firestore Database** → **Data** tab
3. You should see:
   - Collection: **users**
   - Document: **{user_uid}**
   - Fields: uid, name, email, registrationDate

## 🔧 Troubleshooting

### Problem: Build fails with "Unresolved reference: firebase"

**Solution:**
1. Ensure `google-services.json` is in the `app/` folder
2. Verify the file is not empty or corrupted
3. Run: `.\gradlew clean build`
4. Sync Gradle files in Android Studio

### Problem: "FirebaseApp is not initialized"

**Solution:**
1. Check that `google-services.json` is the correct file from Firebase
2. Verify package name in the file matches: `com.example.upscprep`
3. Rebuild the project
4. Uninstall and reinstall the app

### Problem: "Network error" when logging in

**Solution:**
1. Check internet connection
2. Ensure emulator/device has internet access
3. Check Firebase Console → Authentication is enabled
4. Verify Firestore rules allow access

### Problem: ViewBinding errors

**Solution:**
1. Verify `buildFeatures { viewBinding = true }` in `app/build.gradle.kts`
2. Sync Gradle files
3. Clean and rebuild project
4. Restart Android Studio

### Problem: "Failed INSTALL_FAILED_UPDATE_INCOMPATIBLE"

**Solution:**
1. Uninstall the app from device/emulator
2. Run the app again

### Problem: Gradle sync fails

**Solution:**
1. Check internet connection
2. Clear Gradle cache:
   ```powershell
   .\gradlew clean
   .\gradlew --stop
   ```
3. Delete `.gradle` folder in project root
4. Sync again

## 📱 Expected App Behavior

### Login Screen
- App title: "UPSC Prep"
- Subtitle: "Welcome back, Aspirant!"
- Email input field
- Password input field (with show/hide toggle)
- Login button
- "Don't have an account? Sign Up" link

### Signup Screen
- Title: "Create Account"
- Name input field
- Email input field
- Password input field (with show/hide toggle)
- Confirm Password input field (with show/hide toggle)
- Sign Up button
- "Already have an account? Login" link

### Main Screen
- Welcome text: "Hello Aspirant!"
- User's name displayed
- Message: "Welcome to your UPSC preparation journey!"
- Logout button

## 🎯 What's Next?

Now that authentication is working, you can:

1. **Add more features:**
   - Syllabus tracking
   - Study progress
   - Daily goals
   - Notes management

2. **Enhance security:**
   - Add email verification
   - Add password reset
   - Update Firestore rules

3. **Improve UI:**
   - Add splash screen
   - Add profile screen
   - Add animations

4. **Add functionality:**
   - Remember me option
   - Biometric authentication
   - Social login (Google, Facebook)

## 📞 Need Help?

If you encounter any issues:

1. **Check Firebase Console:**
   - Verify Authentication is enabled
   - Check Firestore rules
   - Look at usage logs

2. **Check Android Studio:**
   - View Logcat for runtime errors
   - Check Build output for compilation errors
   - Use debugger to trace issues

3. **Common Commands:**
   ```powershell
   # Clean build
   .\gradlew clean
   
   # Build project
   .\gradlew build
   
   # Install on device
   .\gradlew installDebug
   
   # View logs
   adb logcat | Select-String "upscprep"
   ```

## ✅ Verification Checklist

Before considering setup complete, verify:

- [ ] All files created successfully
- [ ] Firebase project created
- [ ] google-services.json replaced with actual file
- [ ] Email/Password authentication enabled in Firebase
- [ ] Firestore database created
- [ ] Firestore rules configured
- [ ] Gradle sync successful
- [ ] Project builds without errors
- [ ] App runs on device/emulator
- [ ] Can sign up new user
- [ ] Can login with created user
- [ ] User data appears in Firestore
- [ ] Can logout successfully
- [ ] Validation works correctly

## 🎉 Success!

If all tests pass, your UPSC Prep authentication system is fully functional!

You now have:
- ✅ Complete authentication system
- ✅ MVVM architecture
- ✅ Firebase integration
- ✅ Material Design UI
- ✅ Proper error handling
- ✅ Input validation

**You're ready to start adding more features to your UPSC Prep app!**

---

**Setup Guide Version:** 1.0
**Last Updated:** November 15, 2025
**For Project:** UPSC Prep Authentication System

