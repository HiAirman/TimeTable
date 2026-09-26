package io.github.hiairman.monet.markdown

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * These run on the JVM — no emulator, no device. That is the entire payoff of
 * keeping [MarkdownParser] free of Android imports.
 */
class MarkdownParserTest {

    @Test
    fun `blank source produces no blocks`() {
        assertEquals(emptyList<MarkdownBlock>(), MarkdownParser.parse(""))
        assertEquals(emptyList<MarkdownBlock>(), MarkdownParser.parse("\n\n   \n"))
    }

    @Test
    fun `heading captures level and text`() {
        assertEquals(
            listOf(MarkdownBlock.Heading(2, "Maths")),
            MarkdownParser.parse("## Maths"),
        )
    }

    @Test
    fun `heading level follows the number of hashes`() {
        val blocks = MarkdownParser.parse("# One\n## Two\n### Three")

        assertEquals(
            listOf(
                MarkdownBlock.Heading(1, "One"),
                MarkdownBlock.Heading(2, "Two"),
                MarkdownBlock.Heading(3, "Three"),
            ),
            blocks,
        )
    }

    @Test
    fun `consecutive bullets collapse into one list`() {
        val blocks = MarkdownParser.parse("- ex. 12\n- ex. 13\n- ex. 14")

        assertEquals(1, blocks.size)
        assertEquals(
            MarkdownBlock.BulletList(listOf("ex. 12", "ex. 13", "ex. 14")),
            blocks.first(),
        )
    }

    @Test
    fun `a blank line separates two bullet lists`() {
        val blocks = MarkdownParser.parse("- first\n\n- second")

        assertEquals(
            listOf(
                MarkdownBlock.BulletList(listOf("first")),
                MarkdownBlock.BulletList(listOf("second")),
            ),
            blocks,
        )
    }

    @Test
    fun `consecutive plain lines join into one paragraph`() {
        val blocks = MarkdownParser.parse("Bring the calculator\nand the workbook")

        assertEquals(
            listOf(MarkdownBlock.Paragraph("Bring the calculator and the workbook")),
            blocks,
        )
    }

    @Test
    fun `a whole note parses into the expected block sequence`() {
        val blocks = MarkdownParser.parse(
            """
            ## Maths
            Bring the **calculator**.

            - ex. 12 → 18
            - revise *derivatives*
            """.trimIndent(),
        )

        assertEquals(
            listOf(
                MarkdownBlock.Heading(2, "Maths"),
                MarkdownBlock.Paragraph("Bring the **calculator**."),
                MarkdownBlock.BulletList(listOf("ex. 12 → 18", "revise *derivatives*")),
            ),
            blocks,
        )
    }

    @Test
    fun `plain text yields a single span`() {
        assertEquals(
            listOf(MarkdownSpan.Text("just words")),
            MarkdownParser.parseInline("just words"),
        )
    }

    @Test
    fun `double asterisks are bold, not italic`() {
        assertEquals(
            listOf(
                MarkdownSpan.Text("a "),
                MarkdownSpan.Bold("b"),
                MarkdownSpan.Text(" c"),
            ),
            MarkdownParser.parseInline("a **b** c"),
        )
    }

    @Test
    fun `single asterisks are italic`() {
        assertEquals(
            listOf(MarkdownSpan.Italic("b")),
            MarkdownParser.parseInline("*b*"),
        )
    }

    @Test
    fun `backticks are code`() {
        assertEquals(
            listOf(
                MarkdownSpan.Text("run "),
                MarkdownSpan.Code("gradlew"),
                MarkdownSpan.Text(" now"),
            ),
            MarkdownParser.parseInline("run `gradlew` now"),
        )
    }
}
