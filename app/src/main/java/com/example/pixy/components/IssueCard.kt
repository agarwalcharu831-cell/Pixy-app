package com.example.pixy.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pixy.model.Issue
import com.example.pixy.model.categoryIcon
import com.example.pixy.ui.theme.*

@Composable
fun IssueCard(
    issue: Issue,
    onVote: () -> Unit,
    hasVoted: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Surface2Dark)
            .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        // Title row
        Row(
            modifier            = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment   = Alignment.Top
        ) {
            Text(
                text     = issue.title,
                style    = MaterialTheme.typography.titleMedium,
                color    = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            StatusChip(status = issue.status)
        }

        Spacer(Modifier.height(8.dp))

        // Category + vote row
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Category tag
            Text(
                text     = "${categoryIcon(issue.category)} ${issue.category}",
                fontSize = 11.sp,
                color    = MutedDark,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Surface3Dark)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
            // Vote button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (hasVoted) PixyGreen.copy(alpha = 0.1f) else Surface3Dark)
                    .border(1.dp, if (hasVoted) PixyGreen else BorderDark, RoundedCornerShape(4.dp))
                    .clickable { onVote() }
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("▲", fontSize = 11.sp, color = if (hasVoted) PixyGreen else MutedDark)
                Text("${issue.votes}", fontSize = 11.sp, color = if (hasVoted) PixyGreen else MutedDark)
            }
        }

        if (issue.address.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text     = "📍 ${issue.address}",
                fontSize = 10.sp,
                color    = MutedDark
            )
        }
    }
}
