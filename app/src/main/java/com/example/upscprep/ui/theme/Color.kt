package com.example.upscprep.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core palette (UPSC inspired)
val DeepBlue = Color(0xFF1A237E)
val NavyBlue = Color(0xFF283593)
val MidnightBlue = Color(0xFF101B4D)
val SteelBlue = Color(0xFF3E4A7A)
val MistBlue = Color(0xFF8EA0D4)

val GoldAmber = Color(0xFFFFA000)
val AccentTeal = Color(0xFF00897B)
val AccentSky = Color(0xFF4DD0E1)

val BackgroundLight = Color(0xFFFAFAFA)
val BackgroundDark = Color(0xFF263238)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF37474F)
val ElevatedDark = Color(0xFF2E3A45)

val OutlineLight = Color(0xFFE0E0E0)
val OutlineDark = Color(0xFF455A64)

val TextPrimaryLight = Color(0xFF212121)
val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF757575)
val TextSecondaryDark = Color(0xFFB0BEC5)

val SuccessGreen = Color(0xFF2E7D32)
val WarningAmber = Color(0xFFFFA000)
val ErrorRed = Color(0xFFC62828)
val InfoBlue = Color(0xFF29B6F6)

// Legacy aliases to reduce churn while migrating UI code
val GradientStart = DeepBlue
val GradientMid = NavyBlue
val GradientEnd = MidnightBlue
val AccentCoral = GoldAmber
val CardBackground = ElevatedDark
val TextPrimary = TextPrimaryDark
val TextTertiary = TextSecondaryDark

// Subject palette (used for icons/chips)
val SubjectHistory = Color(0xFFB46914)
val SubjectPolity = Color(0xFF4E84C4)
val SubjectEconomy = Color(0xFF00897B)
val SubjectGeography = Color(0xFF5C6BC0)
val SubjectEnvironment = Color(0xFF2E7D32)
val SubjectScience = Color(0xFF26C6DA)
val SubjectCurrent = Color(0xFF8E24AA)
val SubjectEthics = Color(0xFFFFB300)
val SubjectArtCulture = Color(0xFFEC407A)

val SubjectRed = SubjectHistory
val SubjectOrange = SubjectEthics
val SubjectYellow = GoldAmber
val SubjectGreen = SubjectEnvironment
val SubjectBlue = SubjectPolity
val SubjectIndigo = SubjectGeography
val SubjectPurple = SubjectCurrent
val SubjectPink = SubjectArtCulture
val SubjectTeal = SubjectEconomy
val SubjectCyan = SubjectScience
val SubjectLime = Color(0xFFC5E1A5)
val SubjectAmber = GoldAmber

val StatGreen = SuccessGreen
val StatOrange = WarningAmber
val StatBlue = InfoBlue
val StatPurple = SubjectPurple

@Immutable
object UPSCGradients {
    val Hero = Brush.verticalGradient(listOf(DeepBlue, NavyBlue, MidnightBlue))
    val Progress = Brush.horizontalGradient(listOf(GoldAmber, AccentTeal))
    val CardGloss = Brush.linearGradient(listOf(Color.White.copy(alpha = 0.15f), Color.Transparent))
    val ElevatedDarkGradient = Brush.verticalGradient(listOf(ElevatedDark, SurfaceDark))
}