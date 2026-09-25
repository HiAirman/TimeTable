package io.github.hiairman.monet.ui.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.hiairman.monet.data.sampleCourses
import io.github.hiairman.monet.model.Course
import io.github.hiairman.monet.ui.theme.MonetTheme
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// ---------------------------------------------------------------------------
// Geometry. Every position and size on screen derives from these four numbers.
// ---------------------------------------------------------------------------

/** The slice of the day the grid shows. Classes outside it would fall off. */
private val DAY_START: LocalTime = LocalTime.of(8, 0)
private val DAY_END: LocalTime = LocalTime.of(21, 0)

/** THE ratio: one hour of class = this many dp of screen. Change it and the whole grid rescales. */
private val HOUR_HEIGHT: Dp = 56.dp

private val GUTTER_WIDTH: Dp = 56.dp

private val DAY_COLUMN_WIDTH: Dp = 64.dp

private val WEEKDAYS: List<DayOfWeek> = listOf(
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY,
)

/** The hour marks drawn down the left gutter and ruled across each day column. */
private val GRID_HOURS: List<LocalTime> = buildList {
    var hour = DAY_START
    while (hour < DAY_END) {
        add(hour)
        hour = hour.plusHours(1)
    }
}

private val GRID_HEIGHT: Dp = HOUR_HEIGHT * GRID_HOURS.size

private val HOUR_LABEL: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/**
 * Where [this] time sits vertically inside the grid, measured from [DAY_START].
 *
 * This single function is why the timetable lines up: a block's top edge and its
 * height both come from the same conversion, so 10:00 always lands exactly on the
 * 10:00 rule no matter how tall the grid is.
 */
private fun LocalTime.offsetY(): Dp {
    val minutesFromStart = (toSecondOfDay() - DAY_START.toSecondOfDay()) / 60f
    return HOUR_HEIGHT * (minutesFromStart / 60f)
}

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

@Composable
fun TimetableScreen(
    courses: List<Course>,
    modifier: Modifier = Modifier,
) {
    // Header and body must scroll sideways together, so they share one state.
    val horizontalScroll = rememberScrollState()
    val verticalScroll = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {
        // Day names. Outside the vertical scroll, so they stay pinned while you
        // scroll the grid down, but inside the horizontal state so they track columns.
        Row {
            Spacer(Modifier.width(GUTTER_WIDTH))
            Row(Modifier.horizontalScroll(horizontalScroll)) {
                WEEKDAYS.forEach { day -> DayHeader(day) }
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        // The grid itself. The hour gutter sits outside the horizontal scroll, so it
        // stays pinned on the left while the days slide underneath it.
        Row(Modifier.verticalScroll(verticalScroll)) {
            HourGutter()
            Row(Modifier.horizontalScroll(horizontalScroll)) {
                WEEKDAYS.forEach { day ->
                    DayColumn(courses = courses.filter { it.dayOfWeek == day })
                }
            }
        }
    }
}

@Composable
private fun DayHeader(day: DayOfWeek) {
    Box(
        modifier = Modifier
            .width(DAY_COLUMN_WIDTH)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
        )
    }
}

@Composable
private fun HourGutter() {
    Column(modifier = Modifier.width(GUTTER_WIDTH).height(GRID_HEIGHT)) {
        GRID_HOURS.forEach { hour ->
            Box(
                modifier = Modifier.fillMaxWidth().height(HOUR_HEIGHT),
                contentAlignment = Alignment.TopEnd,
            ) {
                Text(
                    text = hour.format(HOUR_LABEL),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 8.dp, top = 2.dp),
                )
            }
        }
    }
}

@Composable
private fun DayColumn(courses: List<Course>) {
    Box(
        modifier = Modifier
            .width(DAY_COLUMN_WIDTH)
            .height(GRID_HEIGHT)
            .clipToBounds(),
    ) {
        // Hour rules behind the blocks.
        GRID_HOURS.forEachIndexed { index, _ ->
            HorizontalDivider(
                modifier = Modifier.offset(y = HOUR_HEIGHT * index),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
        }

        // Blocks on top. Each one positions itself by its own start time.
        courses.forEach { course -> CourseBlock(course) }
    }
}

@Composable
private fun CourseBlock(course: Course) {
    val top = course.startTime.offsetY()
    val bottom = course.endTime.offsetY()
    val (container, content) = courseColors(course.name)

    Box(
        modifier = Modifier
            .padding(horizontal = 3.dp)
            .offset(y = top)
            .fillMaxWidth()
            .height(bottom - top)
            .clip(RoundedCornerShape(8.dp))
            .background(container)
            .padding(horizontal = 6.dp, vertical = 4.dp),
    ) {
        Column {
            Text(
                text = course.name,
                style = MaterialTheme.typography.labelLarge,
                color = content,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = course.room,
                style = MaterialTheme.typography.labelSmall,
                color = content,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * A stable colour per subject: the same name always hashes to the same slot, so
 * Maths is the same colour on Monday and Wednesday. Material's container/on-container
 * pairs are guaranteed to have readable contrast in both light and dark themes.
 */
@Composable
private fun courseColors(name: String): Pair<Color, Color> {
    val scheme = MaterialTheme.colorScheme
    val palette = listOf(
        scheme.primaryContainer to scheme.onPrimaryContainer,
        scheme.secondaryContainer to scheme.onSecondaryContainer,
        scheme.tertiaryContainer to scheme.onTertiaryContainer,
        scheme.surfaceContainerHighest to scheme.onSurface,
    )
    return palette[Math.floorMod(name.hashCode(), palette.size)]
}

@Preview(showBackground = true,
         showSystemUi = true,
         widthDp = 400,
         heightDp = 800)
@Composable
private fun TimetablePreview() {
    MonetTheme {
        TimetableScreen(courses = sampleCourses)
    }
}
