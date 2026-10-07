package io.github.hiairman.monet.ui.today

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.hiairman.monet.model.Course
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/**
 * The window of the day the strip covers, and THE ratio: one minute of class is
 * this many dp of strip. Rotate the week grid's `HOUR_HEIGHT` ninety degrees and
 * you have this.
 */
private val TIMELINE_START: LocalTime = LocalTime.of(8, 0)
private val TIMELINE_END: LocalTime = LocalTime.of(18, 0)
private val DP_PER_MINUTE: Dp = 1.5.dp

private const val STRIP_HEIGHT_DP = 132

private val TIMELINE_MINUTES: Int =
    (TIMELINE_END.toSecondOfDay() - TIMELINE_START.toSecondOfDay()) / 60

private val TIMELINE_WIDTH: Dp = DP_PER_MINUTE * TIMELINE_MINUTES

private val TIME_LABEL: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

/**
 * Where [this] time sits horizontally along the strip.
 *
 * Frame position *and* frame width both come from this one function, so a 09:00
 * class always starts exactly on the 09:00 mark of the ruler — the same property
 * that made the week grid line up vertically.
 */
private fun LocalTime.xOffset(): Dp =
    DP_PER_MINUTE * ((toSecondOfDay() - TIMELINE_START.toSecondOfDay()) / 60f)

/**
 * Today's classes laid out as a scrollable strip with a fixed pointer at the center.
 *
 * The pointer does not scroll, and it is not a sticky header — it simply sits
 * *outside* the scroll container, the same "pinning by exclusion" trick that keeps
 * the hour gutter fixed in the week view.
 *
 * @param onPointedTimeChange fires with the time currently under the pointer.
 */
@Composable
fun Filmstrip(
    courses: List<Course>,
    now: LocalDateTime,
    onPointedTimeChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val dpPerMinutePx = with(density) { DP_PER_MINUTE.toPx() }

    var viewportWidthPx by remember { mutableIntStateOf(0) }

    // Half a viewport of blank canvas goes at each end of the strip, inside the
    // scroll container. Without it the pointer could never reach the ends of the day:
    // a scroll container cannot scroll past its own edge, so at scroll 0 the centre of
    // the viewport would already be half a viewport *into* the timeline — roughly
    // 10:13 at 1.5dp per minute — and 18:00 would be equally unreachable at the far
    // end. Same problem as the number wheels in the add dialog, same fix: pad the
    // content so the first and last values can both reach the middle.
    val edgePad = with(density) { (viewportWidthPx / 2).toDp() }

    // The pointer only becomes meaningful once the strip has been measured.
    // Until then, we hold back, so the notes panel doesn't flash the 08:00 class.
    var ready by remember { mutableStateOf(false) }

    // Because the leading pad is exactly half a viewport, scroll value 0 already puts
    // 08:00 under the pointer and the scroll offset *is* the distance from the start of
    // the timeline. No correction term — the padding did the arithmetic for us.
    // (Change the pad above and this line has to change with it.)
    val pointedTime by remember {
        derivedStateOf {
            val minutes = (scrollState.value / dpPerMinutePx).roundToInt()
            TIMELINE_START.plusMinutes(minutes.toLong())
        }
    }

    // Auto-center on "now".
    //
    // Keyed on maxValue *and* viewportWidth: `ScrollState.maxValue` is 0 until the
    // content has been measured, so an effect keyed on Unit alone would run too
    // early, clamp to 0 and silently do nothing.
    LaunchedEffect(scrollState.maxValue, viewportWidthPx) {
        if (viewportWidthPx == 0) return@LaunchedEffect
        if (scrollState.maxValue > 0) {
            // Scrolling to "now"'s offset from 08:00 centres it, for the same reason
            // the pointer maths above needs no correction: the pad ate the half-viewport
            // term. maxValue is now exactly TIMELINE_WIDTH, so this can reach both ends.
            val nowPx = with(density) { now.toLocalTime().xOffset().toPx() }
            scrollState.scrollTo(nowPx.roundToInt().coerceIn(0, scrollState.maxValue))
        }
        ready = true
    }

    // rememberUpdatedState so a new lambda instance from the caller doesn't restart
    // the effect on every recomposition.
    val report = rememberUpdatedState(onPointedTimeChange)
    LaunchedEffect(pointedTime, ready) {
        if (ready) report.value(pointedTime)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(STRIP_HEIGHT_DP.dp)
                .onSizeChanged { viewportWidthPx = it.width },
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .height(STRIP_HEIGHT_DP.dp),
            ) {
                // Blank run-in, so 08:00 can be scrolled all the way to the pointer.
                Spacer(Modifier.width(edgePad))

                // Fixed-width canvas; frames place themselves inside it.
                Box(modifier = Modifier.width(TIMELINE_WIDTH).fillMaxHeight()) {
                    courses.forEach { course ->
                        FilmFrame(
                            course = course,
                            isPointed = pointedTime >= course.startTime &&
                                pointedTime < course.endTime,
                        )
                    }
                }

                // Blank run-out, so 18:00 can be scrolled back to the pointer.
                Spacer(Modifier.width(edgePad))
            }

            // The pointer: outside the scroll container, so it never moves.
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight()
                    .width(2.dp)
                    .background(MaterialTheme.colorScheme.primary),
            )
        }

        Text(
            text = pointedTime.format(TIME_LABEL),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun FilmFrame(course: Course, isPointed: Boolean) {
    val start = course.startTime.xOffset()
    val end = course.endTime.xOffset()

    val container = if (isPointed) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }

    Box(
        modifier = Modifier
            .offset(x = start)
            .width(end - start)
            .fillMaxHeight()
            .padding(horizontal = 3.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(container)
            .padding(8.dp),
    ) {
        Column {
            Text(
                text = course.name,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = course.startTime.format(TIME_LABEL),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
            Text(
                text = course.room,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
