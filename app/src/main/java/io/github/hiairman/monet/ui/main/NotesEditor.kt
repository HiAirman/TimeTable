package io.github.hiairman.monet.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import io.github.hiairman.monet.model.Course
import java.time.format.DateTimeFormatter

private val TIME_RANGE: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/**
 * The note area (Body in the spec) with the markdown tool strip down its right edge.
 *
 * The text is plain local state — nothing is stored anywhere. It is keyed on the
 * course id so that moving the filmstrip's pointer to another class swaps in that
 * class's note; edits made to one class are discarded when you scroll to the next,
 * which is the honest behaviour given nothing is persisted yet.
 */
@Composable
fun NotesEditor(
    course: Course?,
    note: String?,
    dayHasClasses: Boolean,
    modifier: Modifier = Modifier,
) {
    var value by remember(course?.id) { mutableStateOf(TextFieldValue(note.orEmpty())) }

    Row(modifier = modifier.fillMaxWidth().fillMaxHeight()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            if (course == null) {
                NoCourseNow(dayHasClasses = dayHasClasses)
            } else {
                Meta(course)
                NoteField(
                    value = value,
                    onValueChange = { value = it },
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(top = 8.dp),
                )
            }
        }

        MarkdownToolbar(
            // Guarded so tapping a tool with no class selected cannot edit text that
            // isn't on screen.
            onInsert = { action -> if (course != null) value = value.apply(action) },
        )
    }
}

@Composable
private fun NoteField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface,
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Box {
                if (value.text.isEmpty()) {
                    Text(
                        text = "No notes yet for this class.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                innerTextField()
            }
        },
    )
}

@Composable
private fun Meta(course: Course) {
    Text(
        text = "${course.startTime.format(TIME_RANGE)}–${course.endTime.format(TIME_RANGE)} · " +
            "${course.room} · ${course.teacher}",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/**
 * The two empty states, carried over from the old notes panel. Worth keeping: the
 * note area is empty on weekends and between classes, which is most of the time.
 */
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
            "Enjoy it. The week overview is in settings."
        },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 6.dp),
    )
}

// ---------------------------------------------------------------------------
// Toolbar actions
//
// These edit the TextFieldValue directly, at the cursor. That is the whole job of
// the toolbar — 点击即可添加 — and it touches no stored data.
// ---------------------------------------------------------------------------

private fun TextFieldValue.apply(action: MarkdownAction): TextFieldValue = when (action) {
    MarkdownAction.H1 -> prefixLine("# ")
    MarkdownAction.H2 -> prefixLine("## ")
    MarkdownAction.H3 -> prefixLine("### ")
    MarkdownAction.BOLD -> wrapSelection("**")
    MarkdownAction.ITALIC -> wrapSelection("*")
    MarkdownAction.LINK -> insertLink()
    // Photo insertion is deferred: it needs a picker, file storage, and a new block
    // type in the markdown parser. The button renders so the layout is complete.
    MarkdownAction.PHOTO -> this
}

/** Wraps the selection in [marker], or drops the cursor between a fresh pair. */
private fun TextFieldValue.wrapSelection(marker: String): TextFieldValue {
    val start = selection.min
    val end = selection.max
    val selected = text.substring(start, end)
    val replacement = marker + selected + marker
    val newText = text.replaceRange(start, end, replacement)
    val cursor = if (selection.collapsed) start + marker.length else start + replacement.length
    return TextFieldValue(newText, TextRange(cursor))
}

/** Inserts [prefix] at the start of the line the cursor is on. */
private fun TextFieldValue.prefixLine(prefix: String): TextFieldValue {
    val cursor = selection.min
    val lineStart = text.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0))
        .let { if (it < 0) 0 else it + 1 }
    val newText = text.replaceRange(lineStart, lineStart, prefix)
    return TextFieldValue(newText, TextRange(cursor + prefix.length))
}

/** Inserts `[]()`, leaving the cursor inside the brackets when nothing is selected. */
private fun TextFieldValue.insertLink(): TextFieldValue {
    val start = selection.min
    val end = selection.max
    val selected = text.substring(start, end)
    val replacement = "[$selected]()"
    val newText = text.replaceRange(start, end, replacement)
    val cursor = if (selection.collapsed) start + 1 else start + replacement.length
    return TextFieldValue(newText, TextRange(cursor))
}
