# 🎨 UPSC Prep - Quick Reference Card

## 🚀 QUICK START

### Run the App
```powershell
# In Android Studio
1. File → Sync Project with Gradle Files
2. Select device/emulator
3. Click Run (▶️) or press Shift + F10
```

---

## 🎨 COLOR QUICK REFERENCE

```
Dark Backgrounds:
  #0F0F23  ███  background_dark
  #1A1A2E  ███  surface_dark
  #16213E  ███  card_background
  #1E2139  ███  card_background_light

Gradients:
  #6B4CE6  ███  gradient_purple_start
  #5B7CE6  ███  gradient_purple_mid
  #4E9AF1  ███  gradient_blue_end

Accents:
  #E94560  ███  accent_coral
  #00D9A3  ███  accent_green

Text:
  #FFFFFF  ███  text_primary
  #B8B8D2  ███  text_secondary
```

---

## 📐 SPACING QUICK REFERENCE

```
@dimen/spacing_xs   →  4dp
@dimen/spacing_sm   →  8dp
@dimen/spacing_md   → 16dp
@dimen/spacing_lg   → 24dp
@dimen/spacing_xl   → 32dp
@dimen/spacing_xxl  → 40dp
```

---

## 📝 TEXT SIZE QUICK REFERENCE

```
@dimen/text_size_xs      → 12sp  (tiny labels)
@dimen/text_size_sm      → 14sp  (captions)
@dimen/text_size_md      → 16sp  (body)
@dimen/text_size_lg      → 18sp  (small headers)
@dimen/text_size_xl      → 20sp  (subheadings)
@dimen/text_size_xxl     → 24sp  (headlines)
@dimen/text_size_title   → 28sp  (section titles)
@dimen/text_size_display → 32sp  (page titles)
@dimen/text_size_hero    → 36sp  (hero text)
```

---

## 🎨 DRAWABLE QUICK REFERENCE

### Gradients
```
@drawable/gradient_purple_blue     → Purple to blue diagonal
@drawable/gradient_button          → Button gradient
@drawable/gradient_card_background → Card gradient
@drawable/gradient_background      → Screen background
```

### Shapes
```
@drawable/rounded_card_background  → Card with border
@drawable/input_field_background   → Input background
@drawable/button_outlined          → Outlined button
```

### Icons
```
@drawable/ic_person  → 👤 Person
@drawable/ic_email   → 📧 Email
@drawable/ic_lock    → 🔒 Lock
@drawable/ic_stats   → 📊 Stats
```

---

## 🎯 TEXT STYLE QUICK REFERENCE

```
@style/TextAppearance.UPSCPrep.Display        → 36sp bold white
@style/TextAppearance.UPSCPrep.Title          → 32sp bold white
@style/TextAppearance.UPSCPrep.TitleMedium    → 28sp bold white
@style/TextAppearance.UPSCPrep.Headline       → 24sp bold white
@style/TextAppearance.UPSCPrep.HeadlineSmall  → 18sp bold white
@style/TextAppearance.UPSCPrep.Body           → 16sp reg white
@style/TextAppearance.UPSCPrep.BodyMedium     → 14sp reg gray
@style/TextAppearance.UPSCPrep.Caption        → 14sp reg gray
@style/TextAppearance.UPSCPrep.CaptionSmall   → 12sp reg gray
```

---

## 🔧 CORNER RADIUS QUICK REFERENCE

```
@dimen/corner_radius_sm   →  8dp  (small elements)
@dimen/corner_radius_md   → 12dp  (inputs, buttons)
@dimen/corner_radius_lg   → 16dp  (cards)
@dimen/corner_radius_xl   → 20dp  (large cards)
@dimen/corner_radius_pill → 24dp  (pill buttons)
```

---

## 📏 COMPONENT SIZE QUICK REFERENCE

```
@dimen/input_height      → 56dp
@dimen/button_height     → 56dp
@dimen/icon_size         → 24dp
@dimen/avatar_size       → 60dp
@dimen/logo_size         → 80dp
@dimen/card_height_sm    → 100dp
@dimen/card_height_md    → 160dp
@dimen/card_height_lg    → 200dp
@dimen/card_width_sm     → 160dp
```

---

## 🎨 COMMON USAGE PATTERNS

### Purple Gradient Button
```xml
<com.google.android.material.button.MaterialButton
    android:layout_width="match_parent"
    android:layout_height="@dimen/button_height"
    android:background="@drawable/gradient_button"
    android:textAppearance="@style/TextAppearance.UPSCPrep.Body"
    app:backgroundTint="@null"
    app:cornerRadius="@dimen/corner_radius_md" />
```

### Input Field with Icon
```xml
<com.google.android.material.textfield.TextInputLayout
    style="@style/Widget.Material3.TextInputLayout.FilledBox"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:hint="Email"
    app:boxBackgroundColor="@color/card_background_light"
    app:boxStrokeColor="@color/border_purple"
    app:startIconDrawable="@drawable/ic_email"
    app:startIconTint="@color/gradient_purple_start">
    
    <com.google.android.material.textfield.TextInputEditText
        android:layout_width="match_parent"
        android:layout_height="@dimen/input_height"
        android:textColor="@color/text_primary" />
</com.google.android.material.textfield.TextInputLayout>
```

### Gradient Card
```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardBackgroundColor="@color/transparent"
    app:cardCornerRadius="@dimen/corner_radius_lg"
    app:cardElevation="@dimen/elevation_md"
    app:strokeWidth="0dp">
    
    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@drawable/gradient_card_background"
        android:padding="@dimen/spacing_lg">
        
        <!-- Card content -->
        
    </androidx.constraintlayout.widget.ConstraintLayout>
</com.google.android.material.card.MaterialCardView>
```

### Outlined Pill Button
```xml
<com.google.android.material.button.MaterialButton
    style="@style/Widget.Material3.Button.OutlinedButton"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Logout"
    android:textColor="@color/text_primary"
    app:cornerRadius="@dimen/corner_radius_pill"
    app:strokeColor="@color/border_purple"
    app:strokeWidth="@dimen/stroke_width_thick" />
```

---

## 📱 SCREEN IDS REFERENCE

### Login Screen
```
btnLogin       → Login button
etEmail        → Email input
etPassword     → Password input
progressBar    → Loading indicator
tvSignup       → Sign up link
```

### Signup Screen
```
btnSignup         → Signup button
etName            → Name input
etEmail           → Email input
etPassword        → Password input
etConfirmPassword → Confirm password
progressBar       → Loading indicator
tvLogin           → Login link
```

### Home Screen
```
tvUsername  → Username display
btnLogout   → Logout button
tvWelcome   → Welcome text
```

---

## 🔄 BUILD COMMANDS

```powershell
# Clean
.\gradlew clean

# Build
.\gradlew build

# Install
.\gradlew installDebug

# Run with logs
.\gradlew installDebug; adb logcat | Select-String "upscprep"
```

---

## ✅ TESTING CHECKLIST

- [ ] Login screen shows gradient
- [ ] Input fields have icons
- [ ] Buttons have gradient
- [ ] Progress bar is purple
- [ ] Signup screen scrolls
- [ ] Home shows stat cards
- [ ] Cards scroll horizontally
- [ ] Logout works
- [ ] Navigation works
- [ ] Colors are consistent

---

## 🎯 KEY FILES

```
Resources:
  res/values/colors.xml
  res/values/dimens.xml
  res/values/styles.xml
  res/values/themes.xml

Drawables:
  res/drawable/gradient_*.xml
  res/drawable/ic_*.xml

Layouts:
  res/layout/activity_login.xml
  res/layout/activity_signup.xml
  res/layout/activity_main.xml

Docs:
  UI_REDESIGN_SUMMARY.md
  VISUAL_DESIGN_GUIDE.md
  IMPLEMENTATION_REPORT.md
```

---

## 💡 QUICK TIPS

1. **Always use** @dimen/ for sizes
2. **Always use** @color/ for colors
3. **Always use** @style/ for text
4. **Set backgroundTint="@null"** for gradient buttons
5. **Use elevation** for depth
6. **Test on dark theme**
7. **Check touch targets** (48dp min)
8. **Keep spacing consistent**

---

## 🆘 TROUBLESHOOTING

**Colors not showing?**
→ Sync Gradle, clean build

**Layout broken?**
→ Check all @dimen/ and @color/ exist

**Icons missing?**
→ Verify drawable files created

**Gradient not visible?**
→ Check backgroundTint="@null" on buttons

**Build fails?**
→ .\gradlew clean; .\gradlew build

---

## 📚 DOCUMENTATION

- Full guide: `VISUAL_DESIGN_GUIDE.md`
- Summary: `UI_REDESIGN_SUMMARY.md`
- Report: `IMPLEMENTATION_REPORT.md`
- This card: `QUICK_REFERENCE.md`

---

**Status:** ✅ Build Successful  
**Theme:** Dark with Purple-Blue Gradients  
**Ready:** Yes! Run the app now! 🚀

