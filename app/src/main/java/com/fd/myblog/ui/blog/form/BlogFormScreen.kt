package com.fd.myblog.ui.blog.form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fd.myblog.R
import com.fd.myblog.data.model.BlogPost
import com.fd.myblog.data.remote.request.BlogFormRequest
import com.fd.myblog.helper.ImageHelper
import com.fd.myblog.helper.UiHelper
import com.fd.myblog.ui.blog.detail.BlogDetailState
import com.fd.myblog.ui.common.ErrorBottomSheetDialog
import com.fd.myblog.ui.common.ImageOptionBottomSheet
import com.fd.myblog.ui.common.ImageOptionListener
import com.fd.myblog.ui.common.LoadingButton
import com.fd.myblog.ui.common.LoadingScreen
import com.fd.myblog.ui.common.SuccessBottomSheetDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogFormScreen(
    viewModel: BlogFormViewModel,
    imageHelper: ImageHelper,
    onClose: () -> Unit,
    onSuccess: () -> Unit
) {

    Scaffold(topBar = {
        TopAppBar(
            colors = UiHelper.topAppBarColors(),
            title = {
                Text(
                    if (viewModel.isEdit) "Edit Post" else "Create Post",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            },
            navigationIcon = {
                IconButton(onClick = { onClose() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                    )
                }
            }
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.isEdit) {
                when (val state = viewModel.stateDetail) {
                    is BlogDetailState.Loading -> {
                        LoadingScreen()
                    }

                    is BlogDetailState.Success -> {
                        BlogFormContent(viewModel, imageHelper, state.blogPost, onSuccess)
                    }

                    is BlogDetailState.Error -> {
                        ErrorBottomSheetDialog(
                            subMessage = state.message,
                            onDismissRequest = { onClose() }
                        )
                    }

                    is BlogDetailState.Idle -> {}
                }
            } else {
                BlogFormContent(viewModel, imageHelper, onSuccess = onSuccess)
            }
        }
    }
}

@Composable
fun BlogFormContent(
    viewModel: BlogFormViewModel,
    imageHelper: ImageHelper,
    blogPost: BlogPost? = null,
    onSuccess: () -> Unit = {}
) {

    var title by remember { mutableStateOf(blogPost?.title) }
    var content by remember { mutableStateOf(blogPost?.content) }
    var showImageOptionBottomSheet by remember { mutableStateOf(false) }

    Spacer(Modifier.height(16.dp))

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        AsyncImage(
            model = if (viewModel.image != null) viewModel.image else blogPost?.imageUrl,
            contentDescription = "Blog Post Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    showImageOptionBottomSheet = true
                },
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.placeholder_image),
            error = painterResource(id = R.drawable.placeholder_image)
        )
    }

    OutlinedTextField(
        value = if (title.isNullOrEmpty()) "" else title.toString(),
        onValueChange = {
            title = it
        },
        label = { Text("Title") },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    )

    OutlinedTextField(
        value = if (content.isNullOrEmpty()) "" else content.toString(),
        onValueChange = {
            content = it
        },
        label = { Text("Content") },
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 300.dp)
            .padding(top = 8.dp)
    )

    Spacer(modifier = Modifier.height(16.dp))

    when (val state = viewModel.state) {
        is BlogFormState.Loading -> {
            LoadingButton()
        }

        is BlogFormState.Success -> {
            SuccessBottomSheetDialog(onDismissRequest = { onSuccess() })
        }

        is BlogFormState.Error -> {
            ErrorBottomSheetDialog(
                subMessage = state.message,
                onDismissRequest = { viewModel.state = BlogFormState.Idle },
                onButtonClick = { viewModel.state = BlogFormState.Idle }
            )
        }

        is BlogFormState.Idle -> {
            SubmitButton(viewModel, BlogFormRequest(viewModel.image, title, content))
        }
    }

    Spacer(Modifier.height(16.dp))

    if (showImageOptionBottomSheet) {
        ImageOptionBottomSheet(
            imageHelper = imageHelper,
            userFrontCamera = true,
            listener = object : ImageOptionListener {
                override fun onDismiss() {
                    showImageOptionBottomSheet = false
                }
            })
    }
}

@Composable
fun SubmitButton(viewModel: BlogFormViewModel, request: BlogFormRequest) {
    Button(
        onClick = {
            if (viewModel.isEdit) {
                viewModel.edit(request)
            } else {
                viewModel.create(request)
            }
        },
        elevation = UiHelper.buttonElevation(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("SUBMIT")
    }
}