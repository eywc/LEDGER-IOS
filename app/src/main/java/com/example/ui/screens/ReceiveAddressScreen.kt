package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CryptoAsset
import com.example.ui.components.QrCodeView
import com.example.ui.theme.InterFontFamily
import com.example.ui.theme.LedgerBlack
import com.example.ui.theme.LedgerCard
import com.example.ui.theme.LedgerCardBorder
import com.example.ui.theme.LedgerGreen
import com.example.ui.theme.LedgerPillDark
import com.example.ui.theme.LedgerTextPrimary
import com.example.ui.theme.LedgerTextSecondary
import com.example.ui.theme.MonoFontFamily
import kotlinx.coroutines.delay

@Composable
fun ReceiveAddressScreen(
    asset: CryptoAsset,
    onClose: () -> Unit,
    onVerifyAddress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onClose() }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2500)
            isCopied = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LedgerBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Receive Title + Close (X)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Receive ${asset.symbol}",
                        color = LedgerTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "On ${asset.networkName}",
                        color = LedgerTextSecondary,
                        fontSize = 13.sp,
                        fontFamily = InterFontFamily
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .testTag("receive_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LedgerTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Card (Matches Screenshot 4)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(LedgerCard)
                    .border(1.dp, LedgerCardBorder, RoundedCornerShape(20.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = asset.accountName,
                        color = LedgerTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = InterFontFamily
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // QR Code
                    QrCodeView(
                        data = asset.address,
                        size = 190.dp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Full Address
                    Text(
                        text = asset.address,
                        color = LedgerTextPrimary,
                        fontSize = 13.sp,
                        fontFamily = MonoFontFamily,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .clickable {
                                clipboardManager.setText(AnnotatedString(asset.address))
                                isCopied = true
                                Toast.makeText(context, "Address copied", Toast.LENGTH_SHORT).show()
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons: Share + Copy/Copied
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Share button
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(LedgerCard)
                        .border(1.dp, LedgerCardBorder, RoundedCornerShape(14.dp))
                        .clickable {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, asset.address)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share ${asset.symbol} address"))
                        }
                        .testTag("receive_share_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Share address",
                        tint = LedgerTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Copy / Copied button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(LedgerCard)
                        .border(1.dp, LedgerCardBorder, RoundedCornerShape(14.dp))
                        .clickable {
                            clipboardManager.setText(AnnotatedString(asset.address))
                            isCopied = true
                            Toast.makeText(context, "Address copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                        .testTag("receive_copy_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (isCopied) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Copied",
                                tint = LedgerGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Copied",
                                color = LedgerGreen,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = InterFontFamily
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "Copy",
                                tint = LedgerTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Copy",
                                color = LedgerTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = InterFontFamily
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(40.dp))

            // Bottom CTA: Verify your address
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(Color.White)
                    .clickable { onVerifyAddress(asset.address) }
                    .testTag("verify_address_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Verify your address",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily
                )
            }
        }
    }
}
