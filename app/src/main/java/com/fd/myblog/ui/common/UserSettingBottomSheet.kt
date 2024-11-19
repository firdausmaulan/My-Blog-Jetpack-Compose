package com.fd.myblog.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSettingBottomSheet(
    onDismissRequest: () -> Unit,
    onEditClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        ListItem(
            headlineContent = { Text("Edit Profile") },
            modifier = Modifier.clickable { onEditClick() }
        )
        ListItem(
            headlineContent = { Text("Change Password") },
            modifier = Modifier.clickable { onChangePasswordClick() }
        )
        ListItem(
            headlineContent = { Text("Logout") },
            modifier = Modifier.clickable { onLogoutClick() }
        )
    }
}