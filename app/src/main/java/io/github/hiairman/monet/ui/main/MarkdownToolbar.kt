package io.github.hiairman.monet.ui.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.hiairman.monet.R

/**
 * Everything the toolbar can insert.
 *
 * The icon is a drawable resource rather than a Material icon so the artwork can be
 * replaced without touching this file — see the handover table in the plan.
 */
enum class MarkdownAction(@DrawableRes val icon: Int, val label: String) {
    H1(R.drawable.ic_format_h1, "Heading 1"),
    H2(R.drawable.ic_format_h2, "Heading 2"),
    H3(R.drawable.ic_format_h3, "Heading 3"),
    BOLD(R.drawable.ic_format_bold, "Bold"),
    ITALIC(R.drawable.ic_format_italic, "Italic"),
    LINK(R.drawable.ic_link, "Link"),
    PHOTO(R.drawable.ic_photo, "Photo"),
}

/**
 * The vertical tool strip down the right of the note area (工具 in the mockup).
 *
 * It is scrollable because seven buttons at 48dp do not fit the height left over by
 * the header and the filmstrip on a small screen. Dividers group the tools the way
 * the spec describes them: headings, then emphasis, then insertions.
 */
@Composable
fun MarkdownToolbar(
    onInsert: (MarkdownAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(56.dp)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MarkdownAction.entries.forEachIndexed { index, action ->
            IconButton(onClick = { onInsert(action) }) {
                Icon(
                    painter = painterResource(action.icon),
                    contentDescription = action.label,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            // After the headings and after italic: three visual groups.
            if (index == 2 || index == 4) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
        }
    }
}
