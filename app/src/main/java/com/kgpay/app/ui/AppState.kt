package com.kgpay.app.ui

import androidx.compose.runtime.compositionLocalOf
import com.kgpay.app.KGPayApp
import com.kgpay.app.data.repository.AppSettings
import com.kgpay.app.ui.theme.EnglishStrings
import com.kgpay.app.ui.theme.Strings

/** App-wide dependencies and current settings, provided once at the NavHost root. */
val LocalAppContainer = compositionLocalOf<KGPayApp> { error("KGPayApp not provided") }
val LocalStrings = compositionLocalOf<Strings> { EnglishStrings }
val LocalAppSettings = compositionLocalOf { AppSettings() }
