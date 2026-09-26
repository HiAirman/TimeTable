package io.github.hiairman.monet.markdown

/**
 * A block-level element of a parsed Markdown document.
 *
 * Block structure is separated from inline styling on purpose. A block is decided
 * one *line* at a time; inline spans are decided *within* a line. Mixing the two
 * in one pass is where hand-written parsers usually go wrong.
 */
sealed interface MarkdownBlock {
    data class Heading(val level: Int, val text: String) : MarkdownBlock

    /**
     * Consecutive `-` lines collapse into one list, so the renderer can space the
     * items tightly without needing to know about their neighbours.
     */
    data class BulletList(val items: List<String>) : MarkdownBlock

    /** Consecutive plain lines join with a space, as Markdown specifies. */
    data class Paragraph(val text: String) : MarkdownBlock
}

/** An inline element within a block's text. */
sealed interface MarkdownSpan {
    data class Text(val text: String) : MarkdownSpan
    data class Bold(val text: String) : MarkdownSpan
    data class Italic(val text: String) : MarkdownSpan
    data class Code(val text: String) : MarkdownSpan
}
