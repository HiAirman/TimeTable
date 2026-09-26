package io.github.hiairman.monet.markdown

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

/**
 * Renders Markdown source as Compose text.
 *
 * Parsing happens once behind [remember]; only styling runs on recomposition.
 */
@Composable
fun MarkdownText(
    source: String,
    modifier: Modifier = Modifier,
) {
    val blocks = remember(source) { MarkdownParser.parse(source) }

    Column(modifier = modifier) {
        blocks.forEachIndexed { index, block ->
            val topPadding = when {
                index == 0 -> 0.dp
                block is MarkdownBlock.Heading -> 14.dp
                else -> 6.dp
            }
            Block(block, Modifier.padding(top = topPadding))
        }
    }
}

@Composable
private fun Block(block: MarkdownBlock, modifier: Modifier = Modifier) {
    when (block) {
        is MarkdownBlock.Heading -> Text(
            text = block.text.toAnnotated(),
            modifier = modifier,
            style = when (block.level) {
                1 -> MaterialTheme.typography.headlineSmall
                2 -> MaterialTheme.typography.titleLarge
                else -> MaterialTheme.typography.titleMedium
            },
            fontWeight = FontWeight.SemiBold,
        )

        is MarkdownBlock.Paragraph -> Text(
            text = block.text.toAnnotated(),
            modifier = modifier,
            style = MaterialTheme.typography.bodyMedium,
        )

        is MarkdownBlock.BulletList -> Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            block.items.forEach { item ->
                Row {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text(
                        text = item.toAnnotated(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

/**
 * Turns one line's inline markup into a styled string.
 *
 * `@Composable` only because the code span needs a colour from the theme.
 */
@Composable
private fun String.toAnnotated(): AnnotatedString {
    val codeBackground = MaterialTheme.colorScheme.surfaceContainerHighest

    return buildAnnotatedString {
        MarkdownParser.parseInline(this@toAnnotated).forEach { span ->
            when (span) {
                is MarkdownSpan.Text -> append(span.text)
                is MarkdownSpan.Bold -> withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(span.text)
                }
                is MarkdownSpan.Italic -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(span.text)
                }
                is MarkdownSpan.Code -> withStyle(
                    SpanStyle(
                        fontFamily = FontFamily.Monospace,
                        background = codeBackground,
                    ),
                ) {
                    append(span.text)
                }
            }
        }
    }
}
