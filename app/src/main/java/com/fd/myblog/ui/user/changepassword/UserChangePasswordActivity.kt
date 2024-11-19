package com.fd.myblog.ui.user.changepassword

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fd.myblog.helper.Constants
import com.fd.myblog.ui.theme.MyBlogTheme
import org.koin.android.ext.android.inject

class UserChangePasswordActivity : ComponentActivity() {

    private val viewModel: UserChangePasswordViewModel by inject()
    private val usedId: Int by lazy { intent.getIntExtra(Constants.KEY_USER_ID, 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.userId = usedId

        setContent {
            MyBlogTheme {
                UserChangePasswordScreen(
                    viewModel,
                    onBack = { finish() }
                )
            }
        }
    }

}