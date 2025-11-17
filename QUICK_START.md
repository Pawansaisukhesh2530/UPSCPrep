# 🚀 QUICK START - UPSC Prep Authentication

## ⚡ Immediate Action Required

### Step 1: Get Firebase Configuration (5 minutes)

1. **Open Firebase Console**
   ```
   https://console.firebase.google.com/
   ```

2. **Create/Select Project**
   - Project name: **UPSC Prep**
   - Disable Google Analytics (optional)

3. **Add Android App**
   - Package name: `com.example.upscprep`
   - Download `google-services.json`

4. **Replace File**
   ```
   Replace: C:\Users\ramin\AndroidStudioProjects\UPSCPrep\app\google-services.json
   With: Your downloaded google-services.json
   ```

5. **Enable Authentication**
   - Go to: Authentication → Sign-in method
   - Enable: Email/Password

6. **Create Firestore Database**
   - Go to: Firestore Database
   - Create database in test mode
   - Select your region

### Step 2: Build Project (2 minutes)

Open PowerShell and run:
```powershell
cd C:\Users\ramin\AndroidStudioProjects\UPSCPrep
.\gradlew clean build
```

Wait for "BUILD SUCCESSFUL"

### Step 3: Run App (1 minute)

In Android Studio:
1. Click "Sync Project with Gradle Files" (elephant icon)
2. Select device/emulator
3. Click Run (green play button)

### Step 4: Test (2 minutes)

1. **Sign Up:**
   - Click "Sign Up"
   - Name: John Doe
   - Email: test@example.com
   - Password: test123
   - Confirm: test123
   - Click "Sign Up"

2. **Login:**
   - Email: test@example.com
   - Password: test123
   - Click "Login"

3. **Verify:**
   - You should see "Hello John Doe!"
   - Click "Logout" to return to login

## ✅ That's It!

Your authentication system is now working!

## 📚 Full Documentation

- **SETUP_GUIDE.md** - Detailed setup instructions
- **README.md** - Complete project documentation
- **IMPLEMENTATION_SUMMARY.md** - Technical overview

## 🆘 Quick Troubleshooting

**Problem:** Build fails
```powershell
.\gradlew clean
.\gradlew --stop
.\gradlew build
```

**Problem:** Firebase errors
- Check internet connection
- Verify google-services.json is correct
- Check Firebase Console for enabled services

**Problem:** App crashes
- Check Logcat in Android Studio
- Verify Firestore database is created
- Ensure Authentication is enabled

## 📞 Need Help?

See detailed troubleshooting in **SETUP_GUIDE.md**

---

**Time Required:** ~10 minutes total
**Difficulty:** Easy
**Prerequisites:** Android Studio installed

