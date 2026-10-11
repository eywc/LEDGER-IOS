package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LedgerCard
import com.example.ui.theme.LedgerCardBorder
import com.example.ui.theme.LedgerGreen
import com.example.ui.theme.LedgerPillDark
import com.example.ui.theme.LedgerPurpleAccent
import com.example.ui.theme.LedgerSurface
import com.example.ui.theme.LedgerTextMuted
import com.example.ui.theme.LedgerTextPrimary
import com.example.ui.theme.LedgerTextSecondary
import com.example.ui.theme.MonoFontFamily
import com.example.viewmodel.NotificationBanner

@Composable
fun NotificationBannerView(
    banner: NotificationBanner?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = banner != null,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        modifier = modifier
    ) {
        if (banner != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1B1927))
                    .border(1.dp, LedgerPurpleAccent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .clickable(onClick = onDismiss)
                    .padding(16.dp)
                    .testTag("incoming_tx_banner")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(LedgerGreen.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = LedgerGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = banner.title,
                            color = LedgerTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = InterFontFamily
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = banner.subtitle,
                            color = LedgerTextSecondary,
                            fontSize = 13.sp,
                            fontFamily = InterFontFamily
                        )
                    }

                    Text(
                        text = banner.amountStr,
                        color = LedgerGreen,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferActionSheet(
    onDismiss: () -> Unit,
    onSelectSend: () -> Unit,
    onSelectReceive: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = LedgerSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .padding(bottom = 30.dp)
        ) {
            Text(
                text = "Transfer",
                color = LedgerTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFontFamily
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Send option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(LedgerCard)
                    .border(1.dp, LedgerCardBorder, RoundedCornerShape(14.dp))
                    .clickable {
                        onDismiss()
                        onSelectSend()
                    }
                    .padding(16.dp)
                    .testTag("transfer_sheet_send"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(LedgerPillDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Send",
                        tint = LedgerTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = "Send",
                        color = LedgerTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = InterFontFamily
                    )
                    Text(
                        text = "Transfer crypto to another wallet or simulator",
                        color = LedgerTextSecondary,
                        fontSize = 13.sp,
                        fontFamily = InterFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Receive option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(LedgerCard)
                    .border(1.dp, LedgerCardBorder, RoundedCornerShape(14.dp))
                    .clickable {
                        onDismiss()
                        onSelectReceive()
                    }
                    .padding(16.dp)
                    .testTag("transfer_sheet_receive"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(LedgerPillDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = "Receive",
                        tint = LedgerGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = "Receive",
                        color = LedgerTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = InterFontFamily
                    )
                    Text(
                        text = "Deposit coins via QR code or public address",
                        color = LedgerTextSecondary,
                        fontSize = 13.sp,
                        fontFamily = InterFontFamily
                    )
                }
            }
        }
    }
}

@Composable
fun VerifyLedgerHardwareDialog(
    address: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Ledger Hardware",
                    tint = LedgerPurpleAccent
                )
                Text(
                    text = "Ledger Stax / Nano",
                    color = LedgerTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily
                )
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Verify that the address shown on your Ledger device matches:",
                    color = LedgerTextSecondary,
                    fontSize = 14.sp,
                    fontFamily = InterFontFamily
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Simulated Ledger e-ink screen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE5E7EB))
                        .padding(14.dp)
                ) {
                    Text(
                        text = address,
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontFamily = MonoFontFamily,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Hardware Secure Element: Approved ✓",
                    color = LedgerGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = InterFontFamily
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Approved on Device", color = LedgerPurpleAccent, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = LedgerCard
    )
}
