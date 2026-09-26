package io.github.hiairman.monet.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.hiairman.monet.data.sampleCourses
import io.github.hiairman.monet.ui.theme.MonetTheme
import io.github.hiairman.monet.ui.timetable.TimetableScreen
import io.github.hiairman.monet.ui.today.TodayScreen
import java.time.LocalDateTime

/**
 * The destinations in the bottom bar.
 *
 * Routes are plain strings rather than type-safe `@Serializable` objects, which
 * would mean adding the kotlinx-serialization plugin for two destinations.
 *
 * The bar is text-only: Material3 no longer bundles the core icon set, and adding
 * the deprecated `material-icons-core` artifact for two glyphs isn't worth it.
 * Swapping in Material Symbols later is a local change to this file.
 */
private enum class MonetTab(val route: String, val label: String) {
    TODAY("today", "Today"),
    WEEK("week", "Week"),
}

/**
 * App shell: a bottom bar plus the nav host that swaps screens inside it.
 *
 * The [Scaffold] owns the window insets, so [NavHost] gets `innerPadding` and no
 * screen needs to know the bottom bar exists.
 */
@Composable
fun MonetApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                MonetTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentRoute == tab.route,
                        onClick = {
                            navController.navigate(tab.route) {
                                // Keep a single copy of each tab on the back stack and
                                // remember where the user was inside it. Without this,
                                // tapping tabs repeatedly would stack duplicates.
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(tab.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MonetTab.TODAY.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(MonetTab.TODAY.route) {
                // Read the clock once, not on every recomposition. A `LocalDateTime.now()`
                // default argument would be re-evaluated on each pass, handing the screen
                // a new "now" continuously and resetting its remembered state.
                val now = remember { LocalDateTime.now() }
                TodayScreen(now = now)
            }
            composable(MonetTab.WEEK.route) { TimetableScreen(courses = sampleCourses) }
        }
    }
}
