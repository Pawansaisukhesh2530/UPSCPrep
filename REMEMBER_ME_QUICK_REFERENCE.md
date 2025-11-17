# 🔐 Remember Me - Quick Code Reference

## 📋 COPY-PASTE SNIPPETS

### **1. Using SecurePreferences in Your Code**

#### Save Credentials (After Login Success)
```kotlin
val email = binding.etEmail.text.toString().trim()
val password = binding.etPassword.text.toString()
val rememberMe = cbRememberMe.isChecked

SecurePreferences.saveCredentials(this, email, password, rememberMe)
```

#### Load Credentials (On Activity Start)
```kotlin
private fun loadSavedCredentials() {
    // Get saved email
    val savedEmail = SecurePreferences.getSavedEmail(this)
    if (!savedEmail.isNullOrEmpty()) {
        binding.etEmail.setText(savedEmail)
    }

    // Get password if Remember Me was enabled
    val rememberMe = SecurePreferences.isRememberMeEnabled(this)
    if (rememberMe) {
        val savedPassword = SecurePreferences.getSavedPassword(this)
        if (!savedPassword.isNullOrEmpty()) {
            binding.etPassword.setText(savedPassword)
            cbRememberMe.isChecked = true
        }
    }
}
```

#### Clear Credentials (On Logout)
```kotlin
private fun handleLogout() {
    // Clear saved credentials
    SecurePreferences.clearCredentials(this)
    
    // Continue with Firebase logout
    firebaseAuth.signOut()
}
```

#### Check if Auto-Login Possible
```kotlin
if (SecurePreferences.hasValidCredentials(this)) {
    // Credentials exist and Remember Me enabled
    // Proceed with auto-login
} else {
    // Show login screen
}
```

---

## 🎨 UI ADDITIONS

### **Remember Me Checkbox (activity_login.xml)**
```xml
<!-- Add this after password TextInputLayout -->
<CheckBox
    android:id="@+id/cbRememberMe"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginTop="8dp"
    android:text="Remember Me"
    android:textColor="@color/text_secondary"
    android:textSize="@dimen/text_size_sm"
    android:buttonTint="@color/gradient_purple_start"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toBottomOf="@id/tilPassword" />
```

### **Initialize Checkbox (LoginActivity.kt)**
```kotlin
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var cbRememberMe: CheckBox  // Add this
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        cbRememberMe = binding.cbRememberMe  // Initialize
        
        loadSavedCredentials()  // Load saved data
    }
}
```

---

## 🔧 GRADLE DEPENDENCY

### **app/build.gradle.kts**
```kotlin
dependencies {
    // ... existing dependencies
    
    // Security Crypto for encrypted SharedPreferences
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
}
```

**After adding:** File → Sync Project with Gradle Files

---

## 📱 MANIFEST UPDATES

### **AndroidManifest.xml (Optional - for auto-login)**
```xml
<!-- Make SplashActivity the launcher -->
<activity
    android:name=".ui.auth.SplashActivity"
    android:exported="true"
    android:theme="@style/Theme.UPSCPrep"
    android:noHistory="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>

<!-- Update LoginActivity (remove launcher intent) -->
<activity
    android:name=".ui.auth.LoginActivity"
    android:exported="false"
    android:theme="@style/Theme.UPSCPrep" />
```

---

## 🧪 TESTING COMMANDS

### **Test 1: Remember Me Checked**
```
1. Enter email: test@example.com
2. Enter password: password123
3. Check "Remember Me"
4. Click Login
5. Close app (swipe away)
6. Reopen app
Expected: Email and password auto-filled, checkbox checked
```

### **Test 2: Remember Me Unchecked**
```
1. Enter credentials
2. Leave "Remember Me" unchecked
3. Login successfully
4. Close and reopen app
Expected: Only email auto-filled, password empty
```

### **Test 3: Logout Clears Data**
```
1. Login with Remember Me checked
2. Click Logout button
3. Close and reopen app
Expected: Both fields empty
```

---

## 🐛 DEBUGGING SNIPPETS

### **Add Logs to SecurePreferences**
```kotlin
fun saveCredentials(context: Context, email: String, password: String, rememberMe: Boolean) {
    Log.d("SecurePrefs", "Saving credentials: email=$email, rememberMe=$rememberMe")
    try {
        val prefs = getEncryptedPreferences(context)
        // ... rest of code
        Log.d("SecurePrefs", "Credentials saved successfully")
    } catch (e: Exception) {
        Log.e("SecurePrefs", "Error saving credentials", e)
    }
}
```

### **Check Saved Data**
```kotlin
// In LoginActivity.onCreate()
Log.d("Login", "Saved email: ${SecurePreferences.getSavedEmail(this)}")
Log.d("Login", "Remember Me: ${SecurePreferences.isRememberMeEnabled(this)}")
Log.d("Login", "Has password: ${SecurePreferences.getSavedPassword(this) != null}")
```

### **View EncryptedSharedPreferences File**
```bash
# Using ADB
adb shell
run-as com.example.upscprep
cd shared_prefs
cat upsc_prep_secure_prefs.xml

# Output will be encrypted (good!)
```

---

## 🔒 SECURITY CHECKLIST

### **Before Production:**
- [ ] Test on physical device (not just emulator)
- [ ] Verify encryption works (file should be unreadable)
- [ ] Test logout clears credentials
- [ ] Test app uninstall removes data
- [ ] Test with screen lock enabled
- [ ] Test with different Android versions
- [ ] Consider adding biometric authentication
- [ ] Add session timeout for auto-login

### **Optional Enhancements:**
```kotlin
// Add expiry time for saved credentials (e.g., 30 days)
private const val KEY_SAVED_TIMESTAMP = "saved_timestamp"

fun saveCredentials(...) {
    // ... existing code
    editor.putLong(KEY_SAVED_TIMESTAMP, System.currentTimeMillis())
    editor.apply()
}

fun isCredentialsExpired(context: Context): Boolean {
    val savedTime = prefs.getLong(KEY_SAVED_TIMESTAMP, 0)
    val currentTime = System.currentTimeMillis()
    val thirtyDays = 30L * 24 * 60 * 60 * 1000
    return (currentTime - savedTime) > thirtyDays
}
```

---

## 📊 FILE STRUCTURE

```
app/src/main/
├── java/com/example/upscprep/
│   ├── ui/auth/
│   │   ├── LoginActivity.kt          [MODIFIED]
│   │   ├── SignupActivity.kt         [UNCHANGED]
│   │   └── SplashActivity.kt         [NEW]
│   ├── utils/
│   │   ├── JsonHelper.kt             [UNCHANGED]
│   │   └── SecurePreferences.kt      [NEW]
│   └── MainActivity.kt               [MODIFIED]
│
├── res/layout/
│   ├── activity_login.xml            [MODIFIED]
│   └── activity_splash.xml           [NEW]
│
└── AndroidManifest.xml               [MODIFIED]

app/build.gradle.kts                  [MODIFIED]
```

---

## 🎯 COMMON MISTAKES TO AVOID

### **❌ DON'T:**
```kotlin
// Save password without user consent
SecurePreferences.saveCredentials(this, email, password, true) // BAD!

// Use regular SharedPreferences
val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
prefs.edit().putString("password", password) // INSECURE!

// Forget to clear on logout
fun logout() {
    firebaseAuth.signOut()
    // Missing: SecurePreferences.clearCredentials(this)
}
```

### **✅ DO:**
```kotlin
// Save only if checkbox checked
val rememberMe = cbRememberMe.isChecked
SecurePreferences.saveCredentials(this, email, password, rememberMe)

// Use encrypted storage
SecurePreferences.saveCredentials(this, email, password, true)

// Always clear on logout
fun logout() {
    SecurePreferences.clearCredentials(this)
    firebaseAuth.signOut()
}
```

---

## 📞 QUICK HELP

### **"Credentials not saving?"**
Check: 
1. Checkbox initialized? `cbRememberMe = binding.cbRememberMe`
2. Save called on success? Check `LoginState.Success` block
3. Gradle synced? File → Sync Project

### **"Auto-login not working?"**
Check:
1. SplashActivity set as launcher in manifest?
2. Firebase session valid? Check `firebaseAuth.currentUser`
3. Remember Me was checked during login?

### **"Build errors?"**
1. Sync Gradle: File → Sync Project with Gradle Files
2. Clean build: Build → Clean Project
3. Rebuild: Build → Rebuild Project
4. Invalidate caches: File → Invalidate Caches / Restart

---

## 🚀 READY TO USE!

All code is implemented and ready. Just:
1. ✅ Sync Gradle
2. ✅ Build project
3. ✅ Run on device
4. ✅ Test Remember Me feature

**Need more help?** Check `REMEMBER_ME_IMPLEMENTATION.md` for detailed explanation.

