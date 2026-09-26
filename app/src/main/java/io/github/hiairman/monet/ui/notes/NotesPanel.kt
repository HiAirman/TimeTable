package io.github.hiairman.monet.ui.notes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.hiairman.monet.markdown.MarkdownText
import io.github.hiairman.monet.model.Course
import java.time.format.DateTimeFormatter

private val TIME_RANGE: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/**
 * The notepad for whichever class the pointer is sitting on.
 *
 * Four states, and the empty ones are the ones that matter — this panel is empty
 * on weekends and between classes, which is most of the time.
 */
@Composable
fun NotesPanel(
    course: Course?,
    note: String?,
    dayHasClasses: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        when {
            course == null -> NoCourseNow(dayHasClasses = dayHasClasses)

            note.isNullOrBlank() -> {
                Meta(course)
                Text(
                    text = "No notes yet for this class.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            else -> {
                Meta(course)
                MarkdownText(
                    source = note,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun Meta(course: Course) {
    Text(
        text = "${course.startTime.format(TIME_RANGE)}–${course.endTime.format(TIME_RANGE)} · ${course.room} · ${course.teacher}",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
private fun NoCourseNow(dayHasClasses: Boolean) {
    Text(
        text = if (dayHasClasses) "Nothing scheduled now" else "No classes today",
        style = MaterialTheme.typography.titleMedium,
    )
    Text(
        text = if (dayHasClasses) {
            "Scroll the timeline to browse today's classes."
        } else {
            "Enjoy it. The Week tab still has the full schedule."
        },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 6.dp),
    )
}
