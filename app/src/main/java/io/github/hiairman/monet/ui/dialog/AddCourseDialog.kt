package io.github.hiairman.monet.ui.dialog

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.hiairman.monet.R

private val WHEEL_ITEM_HEIGHT = 40.dp
private val WHEEL_HEIGHT = WHEEL_ITEM_HEIGHT * 3

/**
 * The add-item dialog (弹窗 in the spec).
 *
 * **The input goes nowhere.** ✓ and ✗ both just close it — the plan scopes this pass
 * to the UI, so the name and the five selectors are read by nothing. They are laid
 * out faithfully so the shape is right when the data layer lands.
 */
@Composable
fun AddCourseDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var year by remember { mutableIntStateOf(2026) }
    var month by remember { mutableIntStateOf(10) }
    var day by remember { mutableIntStateOf(8) }
    var hour by remember { mutableIntStateOf(14) }
    var minute by remember { mutableIntStateOf(30) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Confirm on the left, cancel on the right, as drawn.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            painter = painterResource(R.drawable.ic_confirm),
                            contentDescription = "Confirm",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            painter = painterResource(R.drawable.ic_cancel),
                            contentDescription = "Cancel",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    placeholder = { Text("输入添加项目名称") },
                    singleLine = true,
                )

                Spacer(Modifier.height(24.dp))

                // 年份 月份 日期 小时 分钟 — five wheels, alarm-clock style.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Wheel("年份", year, (2020..2035).toList()) { year = it }
                    Wheel("月份", month, (1..12).toList()) { month = it }
                    Wheel("日期", day, (1..31).toList()) { day = it }
                    Wheel("小时", hour, (0..23).toList()) { hour = it }
                    Wheel("分钟", minute, (0..59).toList()) { minute = it }
                }
            }
        }
    }
}

/**
 * One column of a number wheel.
 *
 * The list carries a spacer item at each end so the first and last values can still
 * reach the middle — without them you could never select 2020 or minute 59.
 *
 * With one spacer up top, the item sitting at the viewport's centre is always
 * `firstVisibleItemIndex` positions into [values]; snapping keeps the scroll aligned
 * to item boundaries, so that index is exact rather than approximate.
 */
@Composable
private fun Wheel(
    label: String,
    value: Int,
    values: List<Int>,
    onValueChange: (Int) -> Unit,
) {
    val state = rememberLazyListState(
        initialFirstVisibleItemIndex = values.indexOf(value).coerceAtLeast(0),
    )
    val centred by remember {
        derivedStateOf { state.firstVisibleItemIndex.coerceIn(0, values.lastIndex) }
    }

    // Report the centred number upward. Guarded against re-reporting the value we
    // were already given, which would otherwise fight the initial scroll position.
    LaunchedEffect(centred) {
        values.getOrNull(centred)?.let { if (it != value) onValueChange(it) }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(modifier = Modifier.height(WHEEL_HEIGHT).width(46.dp)) {
            LazyColumn(
                state = state,
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
            ) {
                item { Spacer(Modifier.height(WHEEL_ITEM_HEIGHT)) }
                items(values.size) { i ->
                    WheelItem(number = values[i], selected = i == centred)
                }
                item { Spacer(Modifier.height(WHEEL_ITEM_HEIGHT)) }
            }
        }
    }
}

@Composable
private fun WheelItem(number: Int, selected: Boolean) {
    Box(
        modifier = Modifier
            .height(WHEEL_ITEM_HEIGHT)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = number.toString().padStart(2, '0'),
            style = if (selected) {
                MaterialTheme.typography.titleMedium
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
    }
}
