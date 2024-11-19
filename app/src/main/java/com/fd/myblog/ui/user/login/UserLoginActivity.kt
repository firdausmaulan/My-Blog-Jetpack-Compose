package com.fd.myblog.ui.user.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fd.myblog.ui.blog.list.BlogListActivity
import com.fd.myblog.ui.theme.MyBlogTheme
import com.fd.myblog.ui.user.register.UserRegisterActivity
import org.koin.android.ext.android.inject

class UserLoginActivity : ComponentActivity() {

    private val viewModel: UserLoginViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyBlogTheme {
                UserLoginScreen(
                    viewModel,
                    onLoginSuccess = {
                        navigateToBlogList()
                    },
                    onRegisterClick = {
                        navigateToRegister()
                    }
                )
            }
        }
    }


    private fun navigateToRegister() {
        val intent = Intent(this, UserRegisterActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToBlogList() {
        val intent = Intent(this, BlogListActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}