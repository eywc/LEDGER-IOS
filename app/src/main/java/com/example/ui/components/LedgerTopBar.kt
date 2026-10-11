package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LedgerPillDark
import com.example.ui.theme.LedgerTextPrimary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LedgerTopBar(
    onOpenHistory: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenEditSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        onOpenEditSheet()
                    }
                )
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left icons group: Profile, Compass, Search
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile icon circle
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(LedgerPillDark)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = onOpenEditSheet
                    )
                    .testTag("top_bar_profile_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Profile",
                    tint = LedgerTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Compass icon circle
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(LedgerPillDark)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = onOpenEditSheet
                    )
                    .testTag("top_bar_compass_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Explore,
                    contentDescription = "Explore",
                    tint = LedgerTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Search icon circle
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(LedgerPillDark)
                    .combinedClickable(
                        onClick = onOpenSearch,
                        onLongClick = onOpenEditSheet
                    )
                    .testTag("top_bar_search_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = LedgerTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Right icon: Clock / History
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(LedgerPillDark)
                .combinedClickable(
                    onClick = onOpenHistory,
                    onLongClick = onOpenEditSheet
                )
                .testTag("top_bar_history_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = "Transaction History",
                tint = LedgerTextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
