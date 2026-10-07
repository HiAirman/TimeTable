package io.github.hiairman.monet.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import io.github.hiairman.monet.data.sampleCourses
import io.github.hiairman.monet.data.sampleNotes
import io.github.hiairman.monet.ui.dialog.AddCourseDialog
import io.github.hiairman.monet.ui.main.MainBottomBar
import io.github.hiairman.monet.ui.main.MainTopBar
import io.github.hiairman.monet.ui.main.NotesEditor
import io.github.hiairman.monet.ui.settings.SettingsDrawer
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * The whole app: one main screen, a settings drawer, and the add dialog.
 *
 * There is no navigation graph. The spec's three surfaces are not destinations — the
 * drawer and the dialog are overlays on a single screen — so a bottom bar and a
 * NavHost would have been scaffolding around nothing. If a second real screen ever
 * appears (a course editor, say), that is the point to bring navigation back.
 *
 * All state here is transient: nothing is read from or written to storage.
 */
@Composable
fun MonetApp() {
    // Read the clock once rather than on every recomposition — a fresh "now" each
    // pass would reset the state remembered below.
    val now = remember { LocalDateTime.now() }

    val todayCourses = remember(now) {
        sampleCourses
            .filter { it.dayOfWeek == now.dayOfWeek }
            .sortedBy { it.startTime }
    }

    // Owned here because two children need it: the filmstrip sets it, the notes
    // panel reads it to decide which class's note to show.
    var pointedTime by remember(now) { mutableStateOf(now.toLocalTime()) }

    val pointedCourse = remember(todayCourses, pointedTime) {
        todayCourses.firstOrNull { pointedTime >= it.startTime && pointedTime < it.endTime }
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }

    // ModalNavigationDrawer always opens from the *start* edge. Providing a
    // right-to-left layout direction is what moves it to the right; the spec puts the
    // settings panel there. Both children then re-provide LTR, because otherwise the
    // drawer's own text — and the entire main screen — would render mirrored.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    SettingsDrawer()
                }
            },
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Scaffold(
                    topBar = {
                        MainTopBar(
                            date = now.toLocalDate(),
                            onSettingsClick = { scope.launch { drawerState.open() } },
                        )
                    },
                    bottomBar = {
                        MainBottomBar(
                            courses = todayCourses,
                            now = now,
                            onPointedTimeChange = { pointedTime = it },
                            onAddClick = { showAddDialog = true },
                        )
                    },
                ) { innerPadding ->
                    NotesEditor(
                        course = pointedCourse,
                        note = pointedCourse?.let { sampleNotes[it.id] },
                        dayHasClasses = todayCourses.isNotEmpty(),
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddCourseDialog(onDismiss = { showAddDialog = false })
    }
}
