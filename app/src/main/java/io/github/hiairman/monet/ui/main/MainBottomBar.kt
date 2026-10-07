package io.github.hiairman.monet.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.hiairman.monet.R
import io.github.hiairman.monet.model.Course
import io.github.hiairman.monet.ui.today.Filmstrip
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * The bottom bar: the timeline across the full width, the square add button floating
 * over its right end.
 *
 * The strip deliberately spans edge to edge. The pointer is drawn at the centre of the
 * strip, so an inset strip puts the pointer off the screen's centre — with a 12dp
 * margin, a 12dp gap and a 48dp button on the right, it landed 30dp to the left. Full
 * width is what makes "centre of the strip" and "centre of the screen" the same line.
 *
 * A side effect worth knowing: the button covers roughly the rightmost 40 minutes of
 * whatever the strip is showing. Nothing is permanently hidden — the strip carries half
 * a viewport of blank padding at each end, so both 08:00 and 18:00 can still be scrolled
 * out from under it.
 */
@Composable
fun MainBottomBar(
    courses: List<Course>,
    now: LocalDateTime,
    onPointedTimeChange: (LocalTime) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            Filmstrip(
                courses = courses,
                now = now,
                onPointedTimeChange = onPointedTimeChange,
                modifier = Modifier.fillMaxWidth(),
            )

            // Drawn after the strip, so it sits on top of it rather than beside it.
            FilledTonalIconButton(
                onClick = onAddClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .size(48.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Add course",
                )
            }
        }
    }
}
