package com.example.pixy.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pixy.model.Issue
import com.example.pixy.model.IssueStatus
import com.example.pixy.model.categoryIcon
import com.example.pixy.ui.theme.BgDark
import com.example.pixy.ui.theme.BorderDark
import com.example.pixy.ui.theme.MutedDark
import com.example.pixy.ui.theme.PixyGreen
import com.example.pixy.ui.theme.PixyOrange
import com.example.pixy.ui.theme.StatusOpen
import com.example.pixy.ui.theme.StatusProgress
import com.example.pixy.ui.theme.StatusResolved
import com.example.pixy.ui.theme.Surface2Dark
import com.example.pixy.ui.theme.Surface3Dark
import com.example.pixy.ui.theme.TextPrimary
import com.example.pixy.viewmodel.IssueViewModel

@Composable
fun AdminIssueCard(
    issue: Issue,
    issueViewModel: IssueViewModel,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Surface2Dark)
            .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(issue.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${categoryIcon(issue.category)} ${issue.category}",
                        fontSize = 11.sp,
                        color = MutedDark,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Surface3Dark)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Text("▲ ${issue.votes}", fontSize = 12.sp, color = PixyGreen)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "By: ${issue.reportedByName.ifBlank { issue.reportedBy }}",
                    fontSize = 11.sp,
                    color = MutedDark
                )
                if (issue.media.isNotEmpty()) {
                    Text(
                        text = "Media attached: ${issue.media.size}",
                        fontSize = 11.sp,
                        color = PixyGreen
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = PixyOrange)
            }
        }

        Spacer(Modifier.height(6.dp))
        StatusChip(status = issue.status)

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusButton(
                label = "Open",
                active = issue.status == IssueStatus.OPEN,
                color = StatusOpen,
                modifier = Modifier.weight(1f)
            ) { issueViewModel.updateStatus(issue.id, IssueStatus.OPEN) }

            StatusButton(
                label = "In Progress",
                active = issue.status == IssueStatus.IN_PROGRESS,
                color = StatusProgress,
                modifier = Modifier.weight(1f)
            ) { issueViewModel.updateStatus(issue.id, IssueStatus.IN_PROGRESS) }

            StatusButton(
                label = "Resolved",
                active = issue.status == IssueStatus.RESOLVED,
                color = StatusResolved,
                modifier = Modifier.weight(1f)
            ) { issueViewModel.updateStatus(issue.id, IssueStatus.RESOLVED) }
        }
    }
}

@Composable
private fun StatusButton(
    label: String,
    active: Boolean,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (active) color else Surface3Dark,
            contentColor = if (active) BgDark else MutedDark
        ),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Text(label, fontSize = 11.sp)
    }
}