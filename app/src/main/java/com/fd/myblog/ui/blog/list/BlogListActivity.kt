package com.fd.myblog.ui.blog.list

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.fd.myblog.data.model.BlogPost
import com.fd.myblog.helper.Constants
import com.fd.myblog.ui.blog.detail.BlogDetailActivity
import com.fd.myblog.ui.blog.form.BlogFormActivity
import com.fd.myblog.ui.theme.MyBlogTheme
import com.fd.myblog.ui.user.detail.UserDetailActivity
import com.fd.myblog.ui.user.login.UserLoginActivity
import org.koin.android.ext.android.inject

class BlogListActivity : ComponentActivity() {

    private val viewModel: BlogListViewModel by inject()
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        registerActivityResultLauncher()

        setContent {
            MyBlogTheme {
                BlogListScreen(
                    viewModel,
                    onProfileClick = {
                        if (viewModel.isAuthenticated()) {
                            navigateToProfile()
                        } else {
                            navigateToLogin()
                        }
                    },
                    onCreateNewPostClick = {
                        if (viewModel.isAuthenticated()) {
                            navigateToCreateNewPost()
                        } else {
                            navigateToLogin()
                        }
                    },
                    onItemClick = { blog ->
                        navigateToDetail(blog)
                    }
                )
            }
        }
    }

    private fun registerActivityResultLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    viewModel.reloadBlogs()
                }
            }
    }

    private fun navigateToProfile() {
        val intent = Intent(this, UserDetailActivity::class.java)
        intent.putExtra(Constants.KEY_USER_ID, viewModel.getUser()?.id)
        startActivity(intent)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, UserLoginActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToCreateNewPost() {
        val intent = Intent(this, BlogFormActivity::class.java)
        activityResultLauncher.launch(intent)
    }

    private fun navigateToDetail(blog: BlogPost) {
        val intent = Intent(this, BlogDetailActivity::class.java)
        intent.putExtra(Constants.KEY_BLOG_ID, blog.id)
        activityResultLauncher.launch(intent)
    }
}