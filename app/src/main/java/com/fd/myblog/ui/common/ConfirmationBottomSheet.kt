package com.fd.myblog.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fd.myblog.R
import com.fd.myblog.ui.theme.ThemeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationBottomSheet(
    imageRes: Int = R.drawable.ic_question,
    title: String = "Are You Sure?",
    message: String = "",
    onConfirmClick: () -> Unit,
    onDismissClick: (() -> Unit)? = null,
    confirmText: String = "Yes",
    dismissText: String = "No",
    confirmTextColor: Color = Color.White,
    confirmButtonColor: Color = ThemeColor.success,
    dismissTextColor: Color = ThemeColor.error,
    dismissButtonColor: Color = ThemeColor.error
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissClick?.invoke() },
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "Confirmation Image",
                modifier = Modifier
                    .height(120.dp)
                    .width(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )

            // Message
            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Dismiss Button (shown only if onDismissClick is provided)
                if (onDismissClick != null) {
                    OutlinedButton(
                        onClick = { onDismissClick() },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = dismissTextColor
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = dismissButtonColor
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = dismissText)
                    }
                }

                // Confirm Button
                Button(
                    onClick = onConfirmClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = confirmButtonColor,
                        contentColor = confirmTextColor
                    ),
                    modifier = if (onDismissClick == null) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier.weight(1f)
                    }
                ) {
                    Text(text = confirmText)
                }
            }
        }
    }
}
