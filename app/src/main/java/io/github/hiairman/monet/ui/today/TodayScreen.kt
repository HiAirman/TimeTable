package io.github.hiairman.monet.ui.today

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.hiairman.monet.data.sampleCourses
import io.github.hiairman.monet.data.sampleNotes
import io.github.hiairman.monet.model.Course
import io.github.hiairman.monet.ui.notes.NotesPanel
import io.github.hiairman.monet.ui.theme.MonetTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

/**
 * The three bands: date, notes, filmstrip.
 *
 * @param now injected rather than read from the system clock inside. A composable
 *   that calls `LocalDateTime.now()` internally cannot be previewed or tested —
 *   it would render whatever the wall clock says whenever it runs.
 */
@Composable
fun TodayScreen(
    now: LocalDateTime,
    modifier: Modifier = Modifier,
    courses: List<Course> = sampleCourses,
    notes: Map<Long, String> = sampleNotes,
) {
    val todayCourses = remember(courses, now) {
        courses
            .filter { it.dayOfWeek == now.dayOfWeek }
            .sortedBy { it.startTime }
    }

    // The one piece of real state on this screen. Filmstrip owns the scroll; the
    // pointed time lives here because the notes panel and the filmstrip both need it.
    var pointedTime by remember(now) { mutableStateOf(now.toLocalTime()) }

    val pointedCourse = remember(todayCourses, pointedTime) {
        todayCourses.firstOrNull {
            pointedTime >= it.startTime && pointedTime < it.endTime
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        DateHeader(now.toLocalDate())
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        NotesPanel(
            course = pointedCourse,
            note = pointedCourse?.let { notes[it.id] },
            dayHasClasses = todayCourses.isNotEmpty(),
            modifier = Modifier.weight(1f),
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Filmstrip(
            courses = todayCourses,
            now = now,
            onPointedTimeChange = { pointedTime = it },
        )
    }
}

@Composable
private fun DateHeader(date: LocalDate) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        Text(
            text = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * A fixed Monday morning, so the preview shows the real UI no matter what day you
 * happen to be reading this. Without the injected clock this preview would render
 * an empty screen every weekend.
 */
@Preview(showBackground = true, widthDp = 400, heightDp = 850)
@Composable
private fun TodayPreview() {
    MonetTheme {
        TodayScreen(now = LocalDateTime.of(2026, 9, 28, 10, 30))
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 850)
@Composable
private fun TodayPreviewNoNotes() {
    MonetTheme {
        TodayScreen(
            now = LocalDateTime.of(2026, 9, 29, 9, 0),
            notes = emptyMap(),
        )
    }
}
