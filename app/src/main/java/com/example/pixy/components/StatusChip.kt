package com.example.pixy.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pixy.model.IssueStatus
import com.example.pixy.ui.theme.*

@Composable
fun StatusChip(status: IssueStatus) {
    val (bg, fg, label) = when (status) {
        IssueStatus.OPEN        -> Triple(StatusOpen.copy(alpha = 0.15f),  StatusOpen,      "Open")
        IssueStatus.IN_PROGRESS -> Triple(StatusProgress.copy(alpha = 0.15f), StatusProgress, "In Progress")
        IssueStatus.RESOLVED    -> Triple(StatusResolved.copy(alpha = 0.15f), StatusResolved,  "✓ Resolved")
    }
    Text(
        text     = label,
        color    = fg,
        fontSize = 10.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}
