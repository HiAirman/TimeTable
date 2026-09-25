package io.github.hiairman.monet.model

import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime

/**
 * One class session in the weekly schedule.
 *
 * A `data class` gives us equals/hashCode/toString/copy for free. That matters in
 * Compose: when state changes, Compose compares values to decide whether to redraw.
 *
 * These are java.time types rather than Strings because they are comparable and
 * carry their own arithmetic. java.time needs API 26+, and our minSdk is 26,
 * so no desugaring library is required.
 */
data class Course(
    val id: Long,
    val name: String,
    val room: String,
    val teacher: String,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
) {
    /** How long the class runs. The grid turns this into a block height. */
    val duration: Duration get() = Duration.between(startTime, endTime)
}
