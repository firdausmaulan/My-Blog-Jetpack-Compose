package com.fd.myblog.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fd.myblog.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorBottomSheetDialog(
    onDismissRequest: () -> Unit,
    imageRes: Int = R.drawable.ic_error,
    message: String = "Oops.. Something went wrong",
    subMessage: String = "Please try again later",
    buttonText: String = "OK",
    buttonColor: Color = Color.Red,
    onButtonClick: () -> Unit = onDismissRequest
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = Color.White
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
                contentDescription = "Error Image",
                modifier = Modifier
                    .height(150.dp)
                    .width(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            // Message
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Sub-message
            Text(
                text = subMessage,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Button
            Button(
                onClick = {
                    onButtonClick()
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(contentColor = buttonColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = buttonText, color = Color.White)
            }
        }
    }
}
