package com.fd.myblog.ui.blog.detail

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.fd.myblog.helper.Constants
import com.fd.myblog.ui.blog.form.BlogFormActivity
import com.fd.myblog.ui.theme.MyBlogTheme
import org.koin.android.ext.android.inject

class BlogDetailActivity : ComponentActivity() {

    private val viewModel: BlogDetailViewModel by inject()
    private val blogId: Int by lazy { intent.getIntExtra(Constants.KEY_BLOG_ID, 0) }
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var reloadList = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchBlogDetail(blogId)

        registerActivityResultLauncher()

        setContent {
            MyBlogTheme {
                BlogDetailScreen(
                    viewModel,
                    onBackClick = { onBackClick() },
                    onEditClick = { navigateToEditBlog() }
                )
            }
        }
    }

    private fun registerActivityResultLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    viewModel.fetchBlogDetail(blogId)
                    reloadList = true
                }
            }
    }

    private fun navigateToEditBlog() {
        val intent = Intent(this, BlogFormActivity::class.java)
        intent.putExtra(Constants.KEY_BLOG_ID, blogId)
        activityResultLauncher.launch(intent)
    }

    private fun onBackClick() {
        if (reloadList) setResult(RESULT_OK)
        finish()
    }
}