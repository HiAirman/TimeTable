package io.github.hiairman.monet.data

import io.github.hiairman.monet.model.Course
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * A hard-coded schedule so the UI has something real to draw before we build storage.
 *
 * In step 3 this becomes a one-time "seed" for an empty database, or disappears.
 * Keeping fake data in one clearly-named file stops it leaking into the rest of the app.
 */
val sampleCourses: List<Course> = listOf(
    // Monday
    Course(1, "Maths", "B204", "Mme Dupont", DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 30)),
    Course(2, "Français", "A102", "M. Bernard", DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 30)),
    Course(3, "Histoire-Géo", "C301", "Mme Leroy", DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(15, 30)),

    // Tuesday
    Course(4, "Physique-Chimie", "LAB2", "M. Petit", DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(10, 0)),
    Course(5, "Anglais", "A210", "Ms. Smith", DayOfWeek.TUESDAY, LocalTime.of(10, 15), LocalTime.of(11, 45)),
    Course(6, "EPS", "Gymnase", "M. Roche", DayOfWeek.TUESDAY, LocalTime.of(14, 0), LocalTime.of(16, 0)),

    // Wednesday
    Course(7, "Maths", "B204", "Mme Dupont", DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(9, 30)),
    Course(8, "SVT", "LAB1", "Mme Moreau", DayOfWeek.WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(12, 0)),

    // Thursday
    Course(9, "Informatique", "INFO1", "M. Chen", DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(11, 0)),
    Course(10, "Français", "A102", "M. Bernard", DayOfWeek.THURSDAY, LocalTime.of(11, 15), LocalTime.of(12, 45)),
    Course(11, "Philosophie", "C305", "Mme Adam", DayOfWeek.THURSDAY, LocalTime.of(14, 0), LocalTime.of(16, 0)),

    // Friday
    Course(12, "Anglais", "A210", "Ms. Smith", DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(9, 30)),
    Course(13, "Physique-Chimie", "LAB2", "M. Petit", DayOfWeek.FRIDAY, LocalTime.of(10, 0), LocalTime.of(12, 0)),
    Course(14, "EPS", "Gymnase", "M. Roche", DayOfWeek.FRIDAY, LocalTime.of(15, 0), LocalTime.of(17, 0)),
)
