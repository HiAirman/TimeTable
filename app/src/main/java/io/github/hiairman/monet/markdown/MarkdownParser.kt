package io.github.hiairman.monet.markdown

/**
 * A deliberately small Markdown parser.
 *
 * Supported: `#`/`##`/`###` headings, `-` bullets, blank-line-separated paragraphs,
 * and inline `**bold**`, `*italic*`, `` `code` ``.
 *
 * This file imports nothing from Android. That is the point: the whole parser is
 * plain Kotlin, so it runs in a JVM unit test in milliseconds instead of needing
 * an emulator. Keep it that way — the temptation to `import androidx.compose...`
 * here for convenience is what makes text handling untestable.
 */
object MarkdownParser {

    private val HEADING = Regex("""^(#{1,3})\s+(.*)$""")
    private val BULLET = Regex("""^[-*]\s+(.*)$""")

    /**
     * Matches inline markup, longest-marker first so `**bold**` is never mistaken
     * for `*italic*` followed by a stray asterisk. Alternation in a regex is
     * ordered, which is what makes this work.
     */
    private val INLINE = Regex("""\*\*(.+?)\*\*|\*(.+?)\*|`(.+?)`""")

    fun parse(source: String): List<MarkdownBlock> {
        val blocks = mutableListOf<MarkdownBlock>()
        val bullets = mutableListOf<String>()
        val paragraph = mutableListOf<String>()

        fun flushBullets() {
            if (bullets.isNotEmpty()) {
                blocks += MarkdownBlock.BulletList(bullets.toList())
                bullets.clear()
            }
        }

        fun flushParagraph() {
            if (paragraph.isNotEmpty()) {
                blocks += MarkdownBlock.Paragraph(paragraph.joinToString(" "))
                paragraph.clear()
            }
        }

        // Bullets and paragraph lines can never both be pending, because every line
        // is routed to exactly one of them.
        fun flushAll() {
            flushParagraph()
            flushBullets()
        }

        source.lines().forEach { rawLine ->
            val line = rawLine.trim()
            val heading = HEADING.matchEntire(line)
            val bullet = BULLET.matchEntire(line)

            when {
                line.isEmpty() -> flushAll()

                heading != null -> {
                    flushAll()
                    blocks += MarkdownBlock.Heading(
                        level = heading.groupValues[1].length,
                        text = heading.groupValues[2].trim(),
                    )
                }

                bullet != null -> {
                    flushParagraph()
                    bullets += bullet.groupValues[1].trim()
                }

                else -> {
                    flushBullets()
                    paragraph += line
                }
            }
        }
        flushAll()

        return blocks
    }

    /** Splits one line's text into plain and styled spans. */
    fun parseInline(text: String): List<MarkdownSpan> {
        val spans = mutableListOf<MarkdownSpan>()
        var cursor = 0

        INLINE.findAll(text).forEach { match ->
            if (match.range.first > cursor) {
                spans += MarkdownSpan.Text(text.substring(cursor, match.range.first))
            }
            val (bold, italic, code) = match.destructured
            spans += when {
                bold.isNotEmpty() -> MarkdownSpan.Bold(bold)
                italic.isNotEmpty() -> MarkdownSpan.Italic(italic)
                else -> MarkdownSpan.Code(code)
            }
            cursor = match.range.last + 1
        }

        if (cursor < text.length) {
            spans += MarkdownSpan.Text(text.substring(cursor))
        }
        return spans
    }
}
