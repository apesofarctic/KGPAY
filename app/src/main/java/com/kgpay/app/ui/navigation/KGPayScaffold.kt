package com.kgpay.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.kgpay.app.ui.components.KGPayTopBar
import com.kgpay.app.ui.theme.Strings

/** Wraps a non-bottom-nav destination (Send money, Scan QR, Enter PIN, …) with a back-able top bar. */
@Composable
fun ScreenWithTopBar(title: String, onBack: () -> Unit, content: @Composable () -> Unit) {
    Scaffold(topBar = { KGPayTopBar(title, onBack) }) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) { content() }
    }
}

@Composable
fun KGPayBottomBar(navController: NavHostController, strings: Strings) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination

    NavigationBar {
        val items = listOf(
            Dest.Home to (Icons.Default.Home to strings.homeLabel),
            Dest.History to (Icons.Default.Receipt to strings.history),
            Dest.Insights to (Icons.Default.BarChart to strings.insights),
            Dest.Contacts to (Icons.Default.People to strings.contacts),
            Dest.Settings to (Icons.Default.Settings to strings.settings),
        )
        items.forEach { (dest, iconLabel) ->
            val (icon, label) = iconLabel
            NavigationBarItem(
                selected = currentRoute?.hierarchy?.any { it.route == dest.route } == true,
                onClick = {
                    navController.navigate(dest.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label) },
            )
        }
    }
}
