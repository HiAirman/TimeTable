package io.github.hiairman.monet.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.hiairman.monet.R
import io.github.hiairman.monet.data.sampleCourses
import io.github.hiairman.monet.ui.timetable.TimetableScreen

/**
 * The settings panel (设置区 in the spec).
 *
 * **Nothing here does anything.** The four switches are local `remember` state that
 * flips on tap and is read by no other screen — see the plan: this pass is the UI
 * only, so 显示地点 and 显示老师 do not yet filter the grid, and the two notification
 * rows schedule nothing. They render so the layout is complete and honest, rather
 * than hiding controls that are coming.
 */
@Composable
fun SettingsDrawer(modifier: Modifier = Modifier) {
    var showRoom by remember { mutableStateOf(true) }
    var showTeacher by remember { mutableStateOf(true) }
    var notifyBeforeClass by remember { mutableStateOf(false) }
    var notifyDayBefore by remember { mutableStateOf(false) }

    ModalDrawerSheet(modifier = modifier) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp),
        ) {
            // ----- Header: avatar, then the divider from the spec -----
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_avatar),
                    contentDescription = null,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // ----- Week overview -----
            Surface(
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    // Bounded: TimetableScreen fills its parent, and the drawer is a
                    // scrolling Column, so the grid needs a height of its own to
                    // scroll inside rather than expanding without limit.
                    .height(260.dp),
            ) {
                TimetableScreen(
                    courses = sampleCourses,
                    modifier = Modifier.padding(8.dp),
                )
            }

            // ----- Menu -----
            GroupLabel("主要设置")
            NavRow("课表设置")
            HorizontalDivider(
                modifier = Modifier.padding(start = 20.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )
            NavRow("我的笔记")

            GroupLabel("布局")
            SwitchRow("显示地点", showRoom) { showRoom = it }
            SwitchRow("显示老师", showTeacher) { showTeacher = it }

            GroupLabel("通知")
            SwitchRow("课前通知", notifyBeforeClass) { notifyBeforeClass = it }
            SwitchRow("前一天提醒", notifyDayBefore) { notifyDayBefore = it }
        }
    }
}

@Composable
private fun GroupLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 4.dp),
    )
}

@Composable
private fun NavRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun SwitchRow(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 12.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
