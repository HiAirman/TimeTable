package io.github.hiairman.monet.data

/**
 * Sample notes keyed by `Course.id`.
 *
 * Real notes need a `(courseId, date)` key — a note belongs to a class *on a day*,
 * not to the class forever. Keying by id alone is a simplification that holds only
 * while notes are read-only; it has to change the moment they become editable.
 *
 * Some courses deliberately have no entry, so the "no notes yet" state is reachable.
 */
val sampleNotes: Map<Long, String> = mapOf(
    1L to """
        ## Maths

        Bring the **calculator**.

        - ex. 12 → 18
        - revise *derivatives*
        - `f'(x)` notation, not `dy/dx`
    """.trimIndent(),

    4L to """
        ## Physique-Chimie

        Lab session in **LAB2** — titration.

        - wear goggles
        - record the *burette* readings twice
    """.trimIndent(),

    5L to """
        ## Anglais

        Essay draft due.

        - 500 words
        - topic: *technology and attention*
    """.trimIndent(),

    9L to """
        ## Informatique

        Continue the Compose exercises.

        - run `gradlew assembleDebug`
        - remember: state hoisting

        Ask about `remember` if still unclear.
    """.trimIndent(),
)
