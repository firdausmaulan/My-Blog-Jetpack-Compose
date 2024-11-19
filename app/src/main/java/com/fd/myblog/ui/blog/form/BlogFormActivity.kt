package com.fd.myblog.ui.blog.form

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fd.myblog.helper.Constants
import com.fd.myblog.helper.ImageHelper
import com.fd.myblog.ui.theme.MyBlogTheme
import org.koin.android.ext.android.inject
import java.io.File

class BlogFormActivity : ComponentActivity() {

    private val viewModel: BlogFormViewModel by inject()
    private lateinit var imageHelper: ImageHelper
    private val blogId: Int by lazy { intent.getIntExtra(Constants.KEY_BLOG_ID, 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageHelper = ImageHelper(this)
        imageHelper.registerLaunchers(object : ImageHelper.Listener {
            override fun onImageCaptured(file: File) {
                viewModel.image = file
            }

            override fun onImageSelected(file: File) {
                viewModel.image = file
            }
        })

        viewModel.blogPostId = blogId
        if (blogId != 0) {
            viewModel.fetchDetail()
            viewModel.isEdit = true
        }

        setContent {
            MyBlogTheme {
                BlogFormScreen(
                    viewModel,
                    imageHelper,
                    onClose = { finish() },
                    onSuccess = {
                        setResult(RESULT_OK)
                        finish()
                    }
                )
            }
        }
    }

}